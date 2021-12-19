package bgu.spl.mics;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.GPUService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {
    private MessageBusImpl messagebus;
    private Cluster cluster;
    private GPU gpu;
    private Student student;
    private Data data;
    private Model model;
    private GPUService gpuService;
    private Event event;
    private Broadcast broadcastevent;

    @BeforeAll
    void beforeall(){
        messagebus = new MessageBusImpl();
        cluster = new Cluster();
        student = new Student("YohananHaTzdik","cs");
        data = new Data(null,1000);
        gpu = new GPU(null);
        model = new Model("TestEvent",data,student);
        gpu.setModel(model);
        gpuService = new GPUService("gpu",gpu,5000);
        this.event = new TestModelEvent();
        this.broadcastevent =  new TickBroadcast();



    }


    @Test
    void subscribeEvent() { //this code checks register,subscribeEvent,sendEvent & awaitMessage.
        messagebus.register(gpuService);
        messagebus.subscribeEvent(TestModelEvent.class,gpuService);
        Message m =null;
        messagebus.sendEvent(event);
        try{
            m=messagebus.awaitMessage(gpuService);
        }
        catch (Exception E){
            System.out.println("failed on subscribeEvent test");
        }
        assertEquals(gpuService,m);
    }

    @Test
    void subscribeBroadcast() { //this code also checks sendBroadcast
        messagebus.register(gpuService);
        messagebus.subscribeBroadcast(TickBroadcast.class,gpuService);
        Message m = null;
        messagebus.sendBroadcast(broadcastevent);
        try{
            m=messagebus.awaitMessage(gpuService);
        }
        catch (Exception E){
            System.out.println("failed on subscribeBroadcast test");
        }
        assertEquals(gpuService,m);
    }

    @Test
    void complete() {
        messagebus.register(gpuService);
        messagebus.subscribeEvent(TestModelEvent.class,gpuService);
        Future<String> future = messagebus.sendEvent(event);
        messagebus.complete(event,"Resolved");
        assertTrue(future.isDone());
    }

    @Test
    void unregister() {
        messagebus.register(gpuService);
        messagebus.unregister(gpuService);
        assertFalse(messagebus.isRegistered(gpuService));

    }

}