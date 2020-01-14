package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    private Future <Integer> future;
    @Before
    public void setUp() throws Exception {
        this.future=new Future<Integer>();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() {
        assertNull(future.get());
    }

    @Test
    public void resolve() {
        future.resolve(1);
        assert(1== future.get());
    }

    @Test
    public void isDone() {
        assertEquals(false,future.isDone());
        future.resolve(1);
        assertEquals(true,future.isDone());

    }

    @Test
    public void get1() {
        long duration=5000;
        TimeUnit unit=TimeUnit.MILLISECONDS;
        long startTime=System.currentTimeMillis();
        Integer result=future.get(duration,unit);
        long elapsedTime=System.currentTimeMillis()-startTime;
        if(elapsedTime<duration)
            assertNotEquals(null,result);
        else
            assertNull(result);
    }
}