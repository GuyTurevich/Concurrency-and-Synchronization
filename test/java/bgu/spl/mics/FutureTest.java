package bgu.spl.mics;

import org.junit.jupiter.api.Test;
import org.junit.Before;

import static org.junit.jupiter.api.Assertions.*;

class FutureTest {
    private static Future<String> future;
    @Before
    public void setUp(){
        future = new Future<>();
    }

    @Test
    public void testGet() {
        assertFalse(future.isDone());
        future.resolve("result");
        future.get();
        assertTrue(future.isDone());
    }

    @Test
    public void testResolve() {
        String result = "result";
        future.resolve(result);
        assertTrue(future.isDone());
        assertTrue(result.equals(future.get()));
    }

    @Test
    public void isDone() {
    }

    @Test
    public void testGetTime() {
    }
}