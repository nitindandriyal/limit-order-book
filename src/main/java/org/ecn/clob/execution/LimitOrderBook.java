package org.ecn.clob.execution;

import org.ecn.clob.TradeBook;
import org.ecn.clob.model.Order;
import org.ecn.clob.model.OrderNode;

public class LimitOrderBook {

    private final TradeBook tradeBook;

    public LimitOrderBook(TradeBook tradeBook) {
        this.tradeBook = tradeBook;
    }

    private final OrderNode[] bids = new OrderNode[1000000];
    private int bidTopOfBook = -1;

    private final OrderNode[] asks = new OrderNode[1000000];
    private int askTopOfBook = -1;

    public void execute(Order order, Execution execution) {
        int remainingVolume;
        if (order.getSide() == 'B') {
            if ((remainingVolume = sweepAsks(order)) > 0) {
                addRemainingToOrderBook(order, bids[order.getPrice()], bids);
                if (bidTopOfBook == -1 || order.getPrice() > bids[bidTopOfBook].getOrder().getPrice()) {
                    bidTopOfBook = order.getPrice();
                }
            }
        } else {
            if ((remainingVolume = sweepBids(order)) > 0) {
                addRemainingToOrderBook(order, asks[order.getPrice()], asks);
                if (askTopOfBook == -1 || order.getPrice() < asks[askTopOfBook].getOrder().getPrice()) {
                    askTopOfBook = order.getPrice();
                }
            }
        }
    }

    private void addRemainingToOrderBook(Order order, OrderNode node, OrderNode[] orderNodes) {
        if (node == null) {
            orderNodes[order.getPrice()] = new OrderNode(order);
        } else {
            while (node.getNext() != null) {
                node = node.getNext();
            }
            if (!node.getOrder().getId().equals(order.getId())) {
                node.setNext(new OrderNode(order));
            }
        }
    }

    private int sweepBids(Order incomingSellOrder) {
        int quantityRemaining = incomingSellOrder.getVolume();
        if (bidTopOfBook == -1) {
            return quantityRemaining;
        }
        int matchIndex = bidTopOfBook;
        while (null == bids[matchIndex] || bids[matchIndex].getOrder().getPrice() < incomingSellOrder.getPrice()) {
            matchIndex--;
            if (matchIndex == 0) {
                return quantityRemaining;
            }
        }

        while (matchIndex > 0 && quantityRemaining > 0) {
            OrderNode matchedOrderNode = bids[matchIndex];
            if (matchedOrderNode != null) {
                if (bids[matchIndex].getOrder().getPrice() < incomingSellOrder.getPrice()) {
                    break;
                }
                int fillVolume;
                if (quantityRemaining > matchedOrderNode.getOrder().getVolume()) {
                    quantityRemaining = quantityRemaining - matchedOrderNode.getOrder().getVolume();
                    fillVolume = matchedOrderNode.getOrder().getVolume();
                } else {
                    fillVolume = quantityRemaining;
                    quantityRemaining = 0;
                }
                matchedOrderNode.getOrder().setVolume(matchedOrderNode.getOrder().getVolume() - fillVolume);
                removeBidsNode(matchIndex, matchedOrderNode, fillVolume);
                tradeBook.fillTrade(matchedOrderNode.getOrder(), incomingSellOrder, matchedOrderNode.getOrder().getPrice(), fillVolume);

                while (quantityRemaining > 0 && matchedOrderNode.getNext() != null) {
                    matchedOrderNode = matchedOrderNode.getNext();
                    quantityRemaining = quantityRemaining - matchedOrderNode.getOrder().getVolume();
                    fillVolume = quantityRemaining > 0 ? matchedOrderNode.getOrder().getVolume() : incomingSellOrder.getVolume();
                    matchedOrderNode.getOrder().setVolume(matchedOrderNode.getOrder().getVolume() - fillVolume);
                    removeBidsNode(matchIndex, matchedOrderNode, fillVolume);
                    tradeBook.fillTrade(incomingSellOrder, matchedOrderNode.getOrder(), matchedOrderNode.getOrder().getPrice(), fillVolume);
                }
            }
            --matchIndex;
        }

        return quantityRemaining;
    }

    private int sweepAsks(Order incomingBuyOrder) {
        int quantityRemaining = incomingBuyOrder.getVolume();
        if (askTopOfBook == -1) {
            return quantityRemaining;
        }
        int matchIndex = askTopOfBook;
        while (null == asks[matchIndex] || asks[matchIndex].getOrder().getPrice() > incomingBuyOrder.getPrice()) {
            matchIndex++;
            if (matchIndex == asks.length) {
                return quantityRemaining;
            }
        }

        while (matchIndex < asks.length && quantityRemaining > 0) {
            OrderNode matchedOrderNode = asks[matchIndex];
            if (matchedOrderNode != null) {
                if (asks[matchIndex].getOrder().getPrice() > incomingBuyOrder.getPrice()) {
                    break;
                }
                int fillVolume;
                if (quantityRemaining > matchedOrderNode.getOrder().getVolume()) {
                    quantityRemaining = quantityRemaining - matchedOrderNode.getOrder().getVolume();
                    fillVolume = matchedOrderNode.getOrder().getVolume();
                } else {
                    fillVolume = quantityRemaining;
                    quantityRemaining = 0;
                }

                matchedOrderNode.getOrder().setVolume(matchedOrderNode.getOrder().getVolume() - fillVolume);
                removeAsksNode(matchIndex, matchedOrderNode, fillVolume);
                tradeBook.fillTrade(incomingBuyOrder, matchedOrderNode.getOrder(), matchedOrderNode.getOrder().getPrice(), fillVolume);

                while (quantityRemaining > 0 && matchedOrderNode.getNext() != null) {
                    matchedOrderNode = matchedOrderNode.getNext();
                    quantityRemaining = quantityRemaining - matchedOrderNode.getOrder().getVolume();
                    fillVolume = quantityRemaining > 0 ? matchedOrderNode.getOrder().getVolume() : incomingBuyOrder.getVolume();
                    matchedOrderNode.getOrder().setVolume(matchedOrderNode.getOrder().getVolume() - fillVolume);
                    removeAsksNode(matchIndex, matchedOrderNode, fillVolume);
                    tradeBook.fillTrade(incomingBuyOrder, matchedOrderNode.getOrder(), matchedOrderNode.getOrder().getPrice(), fillVolume);
                }
            }
            ++matchIndex;
        }

        return quantityRemaining;
    }

    private void removeBidsNode(int matchIndex, OrderNode matchedOrderNode, int fillVolume) {
        if (fillVolume >= matchedOrderNode.getOrder().getVolume()) {
            bids[matchIndex] = matchedOrderNode.getNext();
            while (bidTopOfBook >= 0 && bids[bidTopOfBook] == null) {
                --bidTopOfBook;
            }
        }
    }

    private void removeAsksNode(int matchIndex, OrderNode matchedOrderNode, int fillVolume) {
        if (fillVolume >= matchedOrderNode.getOrder().getVolume()) {
            asks[matchIndex] = matchedOrderNode.getNext();
            while (askTopOfBook < asks.length && asks[askTopOfBook] == null) {
                ++askTopOfBook;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        OrderNode lastBidNode = null;
        OrderNode lastAskNode = null;
        for (int i = bidTopOfBook, j = askTopOfBook; i >= 0 || j < asks.length; ) {
            Order bid = null;
            while (bid == null && i >= 0) {
                if (bids[i] != null) {
                    if (null != lastBidNode && lastBidNode.getNext() != null) {
                        lastBidNode = lastBidNode.getNext();
                    } else {
                        lastBidNode = bids[i];
                        i--;
                    }
                    bid = lastBidNode.getOrder();
                } else {
                    i--;
                }
            }
            if (bid != null) {
                builder.append(String.format("%,8d", bid.getVolume())).append("  ").append(String.format("%,6d", bid.getPrice()))
                        .append("  | ");
            }

            Order ask = null;
            while (ask == null && j < asks.length) {
                if (asks[j] != null) {
                    if (null != lastAskNode && lastAskNode.getNext() != null) {
                        lastAskNode = lastAskNode.getNext();
                    } else {
                        lastAskNode = asks[j];
                        j++;
                    }
                    ask = lastAskNode.getOrder();
                } else {
                    j++;
                }
            }
            if (ask != null) {
                if (bid == null) {
                    builder.append("                ")
                            .append("  | ");
                }
                builder.append(String.format("%,6d", ask.getPrice())).append("  ").append(String.format("%,8d", ask.getVolume()))
                        .append(System.lineSeparator());
            }
            if (bid != null && ask == null) {
                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }
}
