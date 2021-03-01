package org.ecn.clob.execution;

public class AggressiveExecution implements Execution {

    @Override
    public int nextSlice(String orderId, int remainingQuantity) {
        return remainingQuantity;
    }
}
