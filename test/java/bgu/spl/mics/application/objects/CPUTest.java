package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

class CPUTest {
    private CPU cpu;

    @BeforeAll
    void beforeall(){
        Vector<DataBatch> db=new Vector<>();
        cpu = new CPU(Cluster.getInstance(),db, Data.Type.Images);
    }

    @Test
    void increasetick(){
        int before = cpu.getTick();
        cpu.increaseTick();
        assertEquals(before+1,cpu.getTick());
    }

    @Test
    void receiveBatch() {
        assertTrue(cpu.getDb().isEmpty());
        DataBatch chunk= new DataBatch();
        cpu.receiveBatch(chunk);
        assertFalse(cpu.getDb().isEmpty());
    }
}