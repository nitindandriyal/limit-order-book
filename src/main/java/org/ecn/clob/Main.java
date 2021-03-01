package org.ecn.clob;

import org.ecn.clob.execution.AggressiveExecution;
import org.ecn.clob.execution.IcebergExecution;
import org.ecn.clob.execution.LimitOrderBook;
import org.ecn.clob.execution.SmartOrderRouter;

import java.util.Scanner;

public class Main {
    public static void main(String[] a) {
        TradeBook tradeBook = new TradeBook();
        LimitOrderBook limitOrderBook = new LimitOrderBook(tradeBook);
        OrderParser orderParser = new OrderParser();
        AggressiveExecution aggressiveExecution = new AggressiveExecution();
        IcebergExecution icebergExecution = new IcebergExecution(limitOrderBook);
        SmartOrderRouter smartOrderRouter = new SmartOrderRouter(limitOrderBook, tradeBook, orderParser, aggressiveExecution, icebergExecution);
        Scanner reader = new Scanner(System.in);
        while (reader.hasNext()) {
            smartOrderRouter.handle(reader.nextLine());
        }
        tradeBook.doneTrades().forEach(System.out::println);
        System.out.print(limitOrderBook.toString());
    }
}
