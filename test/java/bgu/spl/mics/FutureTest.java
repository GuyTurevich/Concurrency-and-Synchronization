package bgu.spl.mics;

import org.junit.jupiter.api.Test;
import org.junit.Before;

import static org.junit.jupiter.api.Assertions.*;

class FutureTest {
    private Future<String> future;
    @Before
    public void setUp(){
        future = new Future<>();
    }

    @Test
    public void testGet() {
        Thread t = new Thread(()->future.get());
        t.start();
        String result = "result";
        future.resolve(result);
        assertTrue(future.isDone());
        assertEquals(future.get(),result);
    }

    @Test
    public void testResolve() {
        String result = "result";
        future.resolve(result);
        assertTrue(future.isDone());
        assertEquals(future.get(),result);
    }

    @Test
    public void testisDone() {
        String result = "result";
        future.resolve(result);
        assertTrue(future.isDone());
    }

    @Test
    public void testGetTime() {

    }
}