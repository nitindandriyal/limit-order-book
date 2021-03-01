package org.ecn.clob;

import org.ecn.clob.model.Order;
import org.ecn.clob.model.Trade;

import java.util.*;

public class TradeBook {
    private final List<Trade> listOfTrades = new ArrayList<>();
    private final Set<TradeListener> tradeListeners = new HashSet<>();

    public void fillTrade(Order buyOrder, Order sellOrder, int price, int volume) {
        Trade trade = new Trade(buyOrder.getId(), sellOrder.getId(), price, volume);
        listOfTrades.add(trade);
        for (TradeListener tradeListener : tradeListeners) {
            tradeListener.onTrade(trade);
        }
    }

    public List<Trade> doneTrades() {
        return Collections.unmodifiableList(listOfTrades);
    }

    public void subscribe(TradeListener tradeListener) {
        tradeListeners.add(tradeListener);
    }
}
