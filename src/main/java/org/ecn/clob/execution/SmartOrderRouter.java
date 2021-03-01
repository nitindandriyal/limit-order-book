package org.ecn.clob.execution;

import org.ecn.clob.OrderParser;
import org.ecn.clob.TradeBook;
import org.ecn.clob.model.Order;

import java.util.Optional;

public class SmartOrderRouter {
    private final LimitOrderBook limitOrderBook;
    private final OrderParser orderParser;
    private final IcebergExecution icebergExecution;
    private final AggressiveExecution aggressiveExecution;

    public SmartOrderRouter(LimitOrderBook limitOrderBook, TradeBook tradeBook, OrderParser orderParser, AggressiveExecution aggressiveExecution, IcebergExecution icebergExecution) {
        this.limitOrderBook = limitOrderBook;
        this.orderParser = orderParser;
        this.aggressiveExecution = aggressiveExecution;
        this.icebergExecution = icebergExecution;
        tradeBook.subscribe(icebergExecution);
    }

    public void handle(String line) {
        String[] tokens = line.split(",");
        Optional<Order> validatedOrder = orderParser.validateAndParseOrder(tokens);
        if (validatedOrder.isPresent()) {
            if (tokens.length == 5) {
                icebergExecution.submit(validatedOrder.get(), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]));
            } else {
                limitOrderBook.execute(validatedOrder.get(), aggressiveExecution);
            }
        } else {
            System.out.println("Invalid input order [" + line + "], will continue to next");
        }
    }
}
