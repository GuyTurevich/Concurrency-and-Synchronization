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
    private int numberOfBatches;
    private int tick;
    private int currentBatch;
    private Boolean isTrainModel;
    private Boolean isTraining;
    private Model model;
    private int processedBatchCounter;


    public GPUService(String name , GPU gpu) {
        super(name);
        this.gpu = gpu;
        this.numberOfBatches = gpu.getModel().getData().getSize()/1000; // how many batches is in this model
        currentBatch = 0;
    }
    private Callback<TrainModelEvent> trainCallback = (TrainModelEvent trainModelEvent)->{
        isTrainModel = true;
        isTraining = true;
        model = trainModelEvent.getModel();
        sendBatchesToCluster(model);
    };

    private Callback<TestModelEvent> testCallback = (TestModelEvent testModelEvent)->{
        model = testModelEvent.getModel();
        setResults(model);
        //talk with guy about where the future changes
    };

    private Callback<TickBroadcast> tickCallback = (TickBroadcast tickBroadcast)->{

        //if GPU started training and there are Batches to train
        if (isTraining && cluster.getGpuQueueSize(gpu)!=0){
            tick++;
            if (gpu.getTickTimeToTrain()==tick){
                tick=0;
                synchronized (cluster) {
                    cluster.removeFirstFromQueue(gpu);
                }
                processedBatchCounter++;
                if (processedBatchCounter==numberOfBatches)
                    isTraining=false;
            }
        }

        //Send More batches to CPU through Cluster
        if (isTrainModel) {
            sendBatchesToCluster(model);
        }
    };

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
        int queueSize = cluster.getGpuQueueSize(gpu);
        while(queueSize <= gpu.getDataBatchCapacity() && currentBatch < numberOfBatches) {
            DataBatch dataBatch = new DataBatch(model.getData().getType(),gpu);

            synchronized (cluster) {
                cluster.receiveBatchFromGpu(dataBatch);
            }

            currentBatch++;
        }

        if (currentBatch==numberOfBatches-1){
            isTrainModel = false;
            currentBatch=0;
        }
    }

}
