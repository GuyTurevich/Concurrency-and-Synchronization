package bgu.spl.mics;

import org.junit.jupiter.api.Test;
import org.junit.Before;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class FutureTest {
    private static Future<String> future;
    @Before
    public void setUp(){
        future = new Future<>();
    }

    @Test
    public void testGet() {
        Thread t = new Thread(()-> future.get());
        t.start();
        String result = "result";
        future.resolve(result);
        assertTrue(future.isDone());
        assertEquals(future.get(),result);
    }

    @Test
    public void testResolve() {
        assertFalse(future.isDone());
        String result = "result";
        future.resolve(result);
        assertTrue(future.isDone());
        assertEquals(result, future.get());
    }

    @Test
    public void testIsDone() {
        assertFalse(future.isDone());
        future.resolve("result");
        assertTrue(future.isDone());
    }

    @Test
    public void testGetTime() throws InterruptedException {
        Thread t = new Thread(()-> { }); //NEED TO FILL IN
        t.start();
        assertFalse(future.isDone());
        String result = "result";
        TimeUnit.MILLISECONDS.sleep(501);
        future.resolve(result);
        assertFalse(future.isDone());
        assertEquals(future.get(),result);
    }
}