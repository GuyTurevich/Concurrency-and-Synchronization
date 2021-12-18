package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.DataBatch;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.Random;

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
    private Cluster cluster = Cluster.getInstance();


    public GPUService(String name , GPU gpu) {
        super(name);
        this.gpu = gpu;
    }
    private Callback<TrainModelEvent> trainCallback = (TrainModelEvent trainModelEvent)->{
        Model model = trainModelEvent.getModel();
        sendBatchesToCluster(model);
        // for tomorrow - 1. think on how to train data when cpu finishes processing it.
    };

    private Callback<TestModelEvent> testCallback = (TestModelEvent testModelEvent)->{
        Model model = testModelEvent.getModel();
        setResults(model);
        //talk with guy about where the future changes
    };

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

    public void setResults(Model model){
        Random r = new Random();
        int rand = r.nextInt(100);

        if (model.getStudent().isMsc()){
            if (rand<60)
                model.setResultsGood();
            else model.setResultsBad();
        }
        else {
            if (rand<80)
                model.setResultsGood();
            else model.setResultsBad();
        }
    }

    public void sendBatchesToCluster(Model model){
        int batchNumber = model.getData().getSize();
        for (int i=0;i<batchNumber;i++){
            DataBatch dataBatch = new DataBatch(model.getData().getType(),cluster.getGpuQueue(gpu));
            cluster.receiveBatchFromGpu(dataBatch);
        }
    }
}
