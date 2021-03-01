package org.ecn.clob.execution;

public interface Execution {
    int getSlice(String orderId, int remainingQuantity);
}
