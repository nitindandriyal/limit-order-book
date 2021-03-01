package org.ecn.clob.execution;

import org.ecn.clob.TradeBook;
import org.ecn.clob.TradeListener;
import org.ecn.clob.model.Order;
import org.ecn.clob.model.Trade;

import java.util.HashMap;
import java.util.Map;

public class IcebergExecution implements TradeListener, Execution {

    private final Map<String, Integer> totalVolumeMap = new HashMap<>();
    private final Map<String, Order> orderMap = new HashMap<>();
    private final LimitOrderBook limitOrderBook;

    public IcebergExecution(LimitOrderBook limitOrderBook, TradeBook tradeBook) {
        this.limitOrderBook = limitOrderBook;
        tradeBook.subscribe(this);
    }

    public void submit(Order order, int totalVolume, int visibleVolume) {
        totalVolumeMap.put(order.getId(), totalVolume - visibleVolume);
        orderMap.put(order.getId(), order);
        order.setVolume(visibleVolume);
        limitOrderBook.execute(order, this);
    }

    @Override
    public void onTrade(Trade trade) {
        if (orderMap.containsKey(trade.getBuyer())) {
            sliceOrFinish(trade.getBuyer());
        }

        if (orderMap.containsKey(trade.getSeller())) {
            sliceOrFinish(trade.getSeller());
        }
    }

    private void sliceOrFinish(String orderId) {
        int totalVolume = totalVolumeMap.get(orderId);
        if (totalVolume > 0) {
            Order order = orderMap.get(orderId);
            int visibleVolume = Math.min(totalVolume, order.getVolume());
            totalVolumeMap.put(order.getId(), totalVolume - visibleVolume);
            orderMap.put(order.getId(), order);
            order.setVolume(visibleVolume);
            limitOrderBook.execute(order, this);
        } else {
            totalVolumeMap.remove(orderId);
            orderMap.remove(orderId);
        }
    }

    @Override
    public int getSlice(String orderId, int remainingQuantity) {
        int totalVolume = totalVolumeMap.get(orderId);
        if (totalVolume > 0) {
            Order order = orderMap.get(orderId);
            int visibleVolume = Math.min(totalVolume, order.getVolume());
            totalVolumeMap.put(order.getId(), totalVolume - visibleVolume);
            return visibleVolume;
        } else {
            totalVolumeMap.remove(orderId);
            orderMap.remove(orderId);
            return 0;
        }
    }
}
