package org.ecn.clob;

import org.ecn.clob.execution.AggressiveExecution;
import org.ecn.clob.execution.LimitOrderBook;
import org.ecn.clob.model.Order;
import org.ecn.clob.model.Trade;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class LimitOrderBookTest {

    private LimitOrderBook limitOrderBook;
    private TradeBook tradeBook;
    private AggressiveExecution aggressiveExecution;

    @Before
    public void before() {
        tradeBook = new TradeBook();
        aggressiveExecution = new AggressiveExecution(limitOrderBook);
        limitOrderBook = new LimitOrderBook(tradeBook);
    }

    @Test
    public void testTest1() {
        limitOrderBook.execute(new Order("10000", 'B', 98, 25500), aggressiveExecution);
        limitOrderBook.execute(new Order("10005", 'S', 105, 20000), aggressiveExecution);
        limitOrderBook.execute(new Order("10001", 'S', 100, 500), aggressiveExecution);
        limitOrderBook.execute(new Order("10002", 'S', 102, 10000), aggressiveExecution);
        limitOrderBook.execute(new Order("10003", 'B', 99, 50000), aggressiveExecution);
        limitOrderBook.execute(new Order("10004", 'S', 103, 100), aggressiveExecution);

        assertEquals(0, tradeBook.doneTrades().size());
        tradeBook.doneTrades().forEach(System.out::println);
        System.out.println(limitOrderBook.toString());
    }

    @Test
    public void testTest2() {
        limitOrderBook.execute(new Order("10000", 'B', 98, 25500), aggressiveExecution);
        limitOrderBook.execute(new Order("10005", 'S', 105, 20000), aggressiveExecution);
        limitOrderBook.execute(new Order("10001", 'S', 100, 500), aggressiveExecution);
        limitOrderBook.execute(new Order("10002", 'S', 100, 10000), aggressiveExecution);
        limitOrderBook.execute(new Order("10003", 'B', 99, 50000), aggressiveExecution);
        limitOrderBook.execute(new Order("10004", 'S', 103, 100), aggressiveExecution);
        limitOrderBook.execute(new Order("10006", 'B', 105, 16000), aggressiveExecution);

        assertEquals(4, tradeBook.doneTrades().size());
        List<Trade> trades = tradeBook.doneTrades();
        assertEquals("10006", trades.get(0).getBuyer());
        assertEquals("10006", trades.get(1).getBuyer());
        assertEquals("10006", trades.get(2).getBuyer());
        assertEquals("10006", trades.get(3).getBuyer());

        assertEquals("10001", trades.get(0).getSeller());
        assertEquals("10002", trades.get(1).getSeller());
        assertEquals("10004", trades.get(2).getSeller());
        assertEquals("10005", trades.get(3).getSeller());

        assertEquals(100, trades.get(0).getPrice());
        assertEquals(100, trades.get(1).getPrice());
        assertEquals(103, trades.get(2).getPrice());
        assertEquals(105, trades.get(3).getPrice());

        assertEquals(500, trades.get(0).getVolume());
        assertEquals(10000, trades.get(1).getVolume());
        assertEquals(100, trades.get(2).getVolume());
        assertEquals(5400, trades.get(3).getVolume());

        trades.forEach(System.out::println);
        System.out.println(limitOrderBook.toString());
    }

    @Test
    public void testTest3() {
        limitOrderBook.execute(new Order("10001", 'B', 99, 1000), aggressiveExecution);
        limitOrderBook.execute(new Order("10002", 'B', 99, 500), aggressiveExecution);
        limitOrderBook.execute(new Order("10003", 'B', 98, 1200), aggressiveExecution);
        limitOrderBook.execute(new Order("10004", 'S', 101, 2000), aggressiveExecution);
        limitOrderBook.execute(new Order("10005", 'S', 95, 2000), aggressiveExecution);

        assertEquals(3, tradeBook.doneTrades().size());
        List<Trade> trades = tradeBook.doneTrades();
        assertEquals("10001", trades.get(0).getBuyer());
        assertEquals("10005", trades.get(1).getBuyer());
        assertEquals("10003", trades.get(2).getBuyer());

        assertEquals("10005", trades.get(0).getSeller());
        assertEquals("10002", trades.get(1).getSeller());
        assertEquals("10005", trades.get(2).getSeller());

        assertEquals(99, trades.get(0).getPrice());
        assertEquals(99, trades.get(1).getPrice());
        assertEquals(98, trades.get(2).getPrice());

        assertEquals(1000, trades.get(0).getVolume());
        assertEquals(500, trades.get(1).getVolume());
        assertEquals(500, trades.get(2).getVolume());

        trades.forEach(System.out::println);
        System.out.println(limitOrderBook.toString());
    }

}