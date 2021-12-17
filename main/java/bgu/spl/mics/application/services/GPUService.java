package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.GPU;

/**
 * GPU service is responsible for handling the
 * {@link bgu.spl.mics.application.messages.TrainModelEvent} and {@link bgu.spl.mics.application.messages.TestModelEvent},
 * in addition to sending the {link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService{

    private GPU gpu;


    public GPUService(String name , GPU gpu) {
        super(name);
        this.gpu = gpu;
        // TODO Implement this
    }
    private Callback<TrainModelEvent> trainCallback = (TrainModelEvent trainModelEvent)->{};

    private Callback<TestModelEvent> testCallback = (TestModelEvent testModelEvent)->{};

    private Callback<TickBroadcast> tickCallback = (TickBroadcast tickBroadcast)->{};

    private Callback<TerminationBroadcast> terminateCallback = (TerminationBroadcast terminationBroadcast) ->{
        terminate();
    };

    @Override
    protected void initialize() {
        this.subscribeEvent(TrainModelEvent.class,trainCallback);
        this.subscribeEvent(TestModelEvent.class, testCallback);
        this.subscribeBroadcast(TickBroadcast.class, tickCallback);
        this.subscribeBroadcast(TerminationBroadcast.class, terminateCallback);
    }
}
