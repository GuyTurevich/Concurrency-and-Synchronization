package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
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
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private GPU gpu;
    private Cluster cluster = Cluster.getInstance();
    private MessageBusImpl messageBus = MessageBusImpl.getInstance();
    private int numberOfBatches;
    private int tick;
    private int currentBatch;
    private Boolean isTrainModel;
    private Boolean isTraining;
    private Model model;
    private int processedBatchCounter;
    private TrainModelEvent currTrainModelEvent;


    public GPUService(String name, GPU gpu) {
        super(name);
        this.gpu = gpu;
        this.numberOfBatches = 0; // how many batches is in this model
        currentBatch = 0;
        tick = 0;
        currentBatch = 0;
        isTrainModel = false;
        isTraining = false;
        model = null;
        processedBatchCounter = 0;
        currTrainModelEvent = null;
    }

    private Callback<TrainModelEvent> trainCallback = (TrainModelEvent trainModelEvent) -> {
        currTrainModelEvent = trainModelEvent;
        isTrainModel = true;
        isTraining = true;
        numberOfBatches = trainModelEvent.getModel().getData().getSize() /1000 ;
        model = trainModelEvent.getModel();
        model.setStatusToTraining();
        System.out.println(model.getName() + " Training");
        sendBatchesToCluster(model);
    };

    private Callback<TestModelEvent> testCallback = (TestModelEvent testModelEvent) -> {
        model = testModelEvent.getModel();
        setResults(model);
        model.setStatusToTested();
        System.out.println(model.getName() + " Tested");
        this.complete(testModelEvent,model);
    };

    private Callback<TickBroadcast> tickCallback = (TickBroadcast tickBroadcast) -> {

        //if GPU started training and there are Batches to train
        if (isTraining && cluster.getGpuQueueSize(gpu) != 0) {
            tick++;
            if (gpu.getTickTimeToTrain() == tick) {
                synchronized (gpu) {
                    gpu.incrementTimeUsed();
                    tick = 0;
                    cluster.removeFirstFromQueue(gpu);
                    processedBatchCounter++;
                    gpu.incrementTotalProcessed();
                }
                if (processedBatchCounter == numberOfBatches) {
                    synchronized (this) {
                        isTraining = false;
                        processedBatchCounter = 0;
                        model.setStatusToTrained();
                        System.out.println(model.getName() + " Trained");
                        this.complete(currTrainModelEvent, model);
                    }
                }
            }
        }

        //Send More batches to CPU through Cluster
        if (isTrainModel) {
            sendBatchesToCluster(model);
//            System.out.println(model.getName() + " SendingBatchesTocluster");
        }
    };

    private Callback<TerminationBroadcast> terminateCallback = (TerminationBroadcast terminationBroadcast) -> {
        terminate();
    };

    @Override
    protected void initialize() {
        this.subscribeEvent(TrainModelEvent.class, trainCallback);
        this.subscribeEvent(TestModelEvent.class, testCallback);
        this.subscribeBroadcast(TickBroadcast.class, tickCallback);
        this.subscribeBroadcast(TerminationBroadcast.class, terminateCallback);
    }


    public void setResults(Model model) {
        Random r = new Random();
        int rand = r.nextInt(100);

        if (model.getStudent().isMsc()) {
            if (rand < 60)
                model.setResultsGood();
            else model.setResultsBad();
        } else {
            if (rand < 80)
                model.setResultsGood();
            else model.setResultsBad();
        }
    }

    public void sendBatchesToCluster(Model model) {
        //int queueSize = cluster.getGpuQueueSize(gpu);
        synchronized (gpu) {
            while (cluster.getGpuQueueSize(gpu) <= gpu.getDataBatchCapacity() && currentBatch < numberOfBatches) {
                DataBatch dataBatch = new DataBatch(model.getData().getType(), gpu);
                cluster.receiveBatchFromGpu(dataBatch);
                currentBatch++;
            }
        }

        if (currentBatch == numberOfBatches) {
            isTrainModel = false;
            currentBatch = 0;
        }
    }

}
