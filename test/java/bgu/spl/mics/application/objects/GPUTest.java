package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class GPUTest {
    private Student student;
    private Data data;
    private GPU gpu;

    @BeforeAll
    void beforeall(){
        student = new Student("Nitay","cs",Student.Degree.PhD);
        data = new Data(Data.Type.Text,1000);
        GPU gpu = new GPU(GPU.Type.RTX2080);
    }
    @Test
    void initialize() {
        assertTrue(gpu.getModel()==null);
        Model model = new Model("TestEvent",data,student);
        gpu.setModel(model);
    }

    @Test
    void setModel() {
        assertTrue(gpu.getModel()==null);
        Model model = new Model("TestEvent",data,student);
        gpu.setModel(model);
        assertEquals(gpu.getModel(),model);
    }


    @Test
    void getModel() {
    }

    @Test
    void setBus_queue() {
        assertTrue(gpu.getBus_queue()==null);
        Queue<Event> queue = new LinkedList<Event>();
        gpu.setBus_queue(queue);
        assertEquals(gpu.getBus_queue(),queue);
    }

    @Test
    void sendDataToCluster() {
        Model model = new Model("TestEvent",data,student);
        gpu.setModel(model);
        DataBatch chunk=gpu.sendDataToCluster(0);
        assertTrue(chunk.size()==1000);

    }


    @Test
    void receiveDataFromCluster() {
        Model model = new Model("TestEvent",data,student);
        gpu.setModel(model);
        assertTrue(model.getData().getProcessed()==0);
        DataBatch chunk= new DataBatch(); //constructor is making it in size of 1000
        gpu.receiveDataFromCluster(chunk);
        assertTrue(model.getData().getProcessed()==chunk.size());
    }

    @Test
    void trainModel(){
        Model model = new Model("TestEvent",data,student);
        gpu.setModel(model);
        DataBatch chunk= new DataBatch();
        gpu.trainModel(chunk);
        assertTrue(gpu.getTrainedData()==0);
        try {
            TimeUnit.MILLISECONDS.sleep(20); //assume each tick is 10 milliseconds
            assertTrue(gpu.getTrainedData()==1000);
        } catch (InterruptedException e) {
            System.out.println("failed the trainModel test");
        }

    }
}