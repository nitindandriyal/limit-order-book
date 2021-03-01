package org.ecn.clob.execution;

import org.ecn.clob.model.Order;

public class AggressiveExecution implements Execution {

    private final LimitOrderBook limitOrderBook;

    public AggressiveExecution(LimitOrderBook limitOrderBook) {
        this.limitOrderBook = limitOrderBook;
    }

    public void submit(Order order) {
        limitOrderBook.execute(order, this);
    }

    @Override
    public int nextSlice(String orderId, int remainingQuantity) {
        return remainingQuantity;
    }
}
