package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Test something.
 *
 * @author skywalker
 */
public class Test {

    /**
     * Test {@link Integer#numberOfLeadingZeros(int)}.
     */
    @org.junit.Test
    public void leadingZeroes() throws ParseException {
        System.out.println(Integer.numberOfLeadingZeros(16));
        System.out.println(146 / 95.6666666667 >= 1.5);
    }

    @org.junit.Test
    public void linkedQueue() {
        SomeQueue<String> queue = new SomeQueue<>();
        queue.offer("a");
        System.out.println(queue.poll());
    }

    @org.junit.Test
    public void threadPool() {
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.shutdown();
        service.execute(() -> System.out.println("hello"));
    }

}