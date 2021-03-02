package org.ecn.clob.datastructure;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BlockingConflatingQueueTest {

    public static final String BTCUSD = "BTCUSD";
    public static final String ETHUSD = "ETHUSD";

    private ConflatingQueue<String, BigDecimal> queue;

    @Before
    public void before() {
        queue = new BlockingConflatingQueue<>();
    }

    @Test(expected = NullPointerException.class)
    public void testNullValuesNotAllowed() {
        queue.offer(null);
    }

    @Test
    public void testInSequenceSingleThread() throws InterruptedException {
        queue.offer(SimpleKeyValue.of(BTCUSD, BigDecimal.valueOf(46428.34)));
        assertEquals(SimpleKeyValue.of(BTCUSD, BigDecimal.valueOf(46428.34)), queue.take());

        queue.offer(SimpleKeyValue.of(BTCUSD, BigDecimal.valueOf(47928.76)));
        assertEquals(SimpleKeyValue.of(BTCUSD, BigDecimal.valueOf(47928.76)), queue.take());

        queue.offer(SimpleKeyValue.of(ETHUSD, BigDecimal.valueOf(1487.65)));
        assertEquals(SimpleKeyValue.of(ETHUSD, BigDecimal.valueOf(1487.65)), queue.take());

        queue.offer(SimpleKeyValue.of(BTCUSD, BigDecimal.valueOf(47528.76)));
        assertEquals(SimpleKeyValue.of(BTCUSD, BigDecimal.valueOf(47528.76)), queue.take());

        queue.offer(SimpleKeyValue.of(ETHUSD, BigDecimal.valueOf(1477.15)));
        assertEquals(SimpleKeyValue.of(ETHUSD, BigDecimal.valueOf(1477.15)), queue.take());
    }

    @Test
    public void testOverwriteKeysSingleThread() throws InterruptedException {
        queue.offer(SimpleKeyValue.of(BTCUSD, BigDecimal.valueOf(46428.34)));
        queue.offer(SimpleKeyValue.of(BTCUSD, BigDecimal.valueOf(46928.76)));
        queue.offer(SimpleKeyValue.of(ETHUSD, BigDecimal.valueOf(1487.65)));

        queue.offer(SimpleKeyValue.of(BTCUSD, BigDecimal.valueOf(47928.76)));
        queue.offer(SimpleKeyValue.of(ETHUSD, BigDecimal.valueOf(1587.85)));

        assertEquals(SimpleKeyValue.of(BTCUSD, BigDecimal.valueOf(47928.76)), queue.take());
        assertEquals(SimpleKeyValue.of(ETHUSD, BigDecimal.valueOf(1587.85)), queue.take());
    }

    @Test
    public void testOrderMultiThreadTakeThenPut() {
        ExecutorService service = Executors.newFixedThreadPool(10);

        service.execute(() -> {
            try {
                assertEquals(SimpleKeyValue.of(BTCUSD, BigDecimal.valueOf(1.2340)), queue.take());
            } catch (Exception ignored) {
            }
        });

        service.execute(() -> queue.offer(SimpleKeyValue.of(BTCUSD, BigDecimal.valueOf(1.2340))));
        service.execute(() -> queue.offer(SimpleKeyValue.of(BTCUSD, BigDecimal.valueOf(1.2341))));
        assertTrue(queue.isEmpty());
    }

    @Test
    public void testOrderMultiThreadPutThenTake() throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(10);
        service.execute(() -> queue.offer(SimpleKeyValue.of(BTCUSD, BigDecimal.valueOf(1.2341))));
        Future<?> r = service.submit(() -> {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        });
        assertEquals(SimpleKeyValue.of(BTCUSD, BigDecimal.valueOf(1.2341)), r.get());
        assertTrue(queue.isEmpty());
    }
}