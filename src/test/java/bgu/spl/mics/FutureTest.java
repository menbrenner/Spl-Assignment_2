package bgu.spl.mics;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    Future test = new Future();

    @Test
    public void get() throws Exception {
        Future secondTest = new Future();
        Object result = new Object();
        test.resolve(result);
        secondTest.resolve(result);
        assertEquals(test.get(),secondTest.get());
        assertNotEquals(null,test.get());
    }

    @Test
    public void resolve() throws Exception {
        Object c = new Object();
        test.resolve(c);
        assertEquals(c , test.get());
    }

    @Test
    public void isDone() throws Exception {
        assertEquals(false , test.isDone());
        test.resolve(new Object());
        assertEquals(true, test.isDone());
    }

    @Test
    public void get1() throws Exception {
        TimeUnit t = TimeUnit.NANOSECONDS;
        double s = (Long.MAX_VALUE * Math.random());
        long z = (long)s;
        Object l = test.get(z,t);
        test.resolve(new Object());
        assertEquals(l , test.get());
        assertEquals(test.isDone(), false);
    }
}