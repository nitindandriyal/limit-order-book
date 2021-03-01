package org.ecn.clob.execution;

public interface Execution {
    int nextSlice(String orderId, int remainingQuantity);
}
