package org.ecn.clob;

import org.ecn.clob.execution.AggressiveExecution;
import org.ecn.clob.execution.IcebergExecution;
import org.ecn.clob.execution.LimitOrderBook;
import org.ecn.clob.model.Order;
import org.ecn.clob.model.Trade;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class IcebergExecutionTest {

    private LimitOrderBook limitOrderBook;
    private IcebergExecution icebergExecution;
    private AggressiveExecution aggressiveExecution;
    private TradeBook tradeBook;

    @Before
    public void before() {
        tradeBook = new TradeBook();
        limitOrderBook = new LimitOrderBook(tradeBook);
        aggressiveExecution = new AggressiveExecution();
        icebergExecution = new IcebergExecution(limitOrderBook);
        tradeBook.subscribe(icebergExecution);
    }

    @Test
    public void testTest3() {
        limitOrderBook.execute(new Order("10000", 'B', 98, 25500), aggressiveExecution);
        limitOrderBook.execute(new Order("10005", 'S', 101, 20000), aggressiveExecution);
        limitOrderBook.execute(new Order("10002", 'S', 100, 10000), aggressiveExecution);
        limitOrderBook.execute(new Order("10001", 'S', 100, 7500), aggressiveExecution);
        limitOrderBook.execute(new Order("10003", 'B', 99, 50000), aggressiveExecution);
        icebergExecution.submit(new Order("ice1", 'B', 100, 100000), 100000, 10000);

        assertEquals(2, tradeBook.doneTrades().size());
        List<Trade> trades = tradeBook.doneTrades();
        assertEquals("ice1", trades.get(0).getBuyer());
        assertEquals("ice1", trades.get(1).getBuyer());

        assertEquals("10002", trades.get(0).getSeller());
        assertEquals("10001", trades.get(1).getSeller());

        assertEquals(100, trades.get(0).getPrice());
        assertEquals(100, trades.get(1).getPrice());

        assertEquals(10000, trades.get(0).getVolume());
        assertEquals(7500, trades.get(1).getVolume());

        trades.forEach(System.out::println);
        System.out.println(limitOrderBook.toString());
    }

}