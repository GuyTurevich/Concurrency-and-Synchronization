package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {

    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model model;
    private Cluster cluster;
    private Queue<Event> bus_queue;
    private int tickTimeToTrain;
    private int trained;
    private int dataBatchCapacity;
    private int timeUsed;
    private int processedBatchesTotal;


    public GPU(String type) {
        super();
        if (type.equals("RTX3090")) {
            this.type = Type.RTX3090;
            dataBatchCapacity=32;
            tickTimeToTrain = 1;
        } else if (type.equals("RTX2080")) {
            this.type = Type.RTX2080;
            dataBatchCapacity=16;
            tickTimeToTrain =2;
        } else {
            this.type = Type.GTX1080;
            dataBatchCapacity=8;
            tickTimeToTrain =4;
        }
        model = null;
        cluster = Cluster.getInstance();  //singelton
        bus_queue = null;
        timeUsed =0;
    }

    public int getDataBatchCapacity(){
        return dataBatchCapacity;
    }

    public int getTickTimeToTrain(){
        return tickTimeToTrain;
    }

    public void incrementTimeUsed(){
        timeUsed +=tickTimeToTrain;
    }
    public void incrementTotalProcessed(){
        processedBatchesTotal ++;
    }











    /**
     * this functions sets a new model for the gpu
     *
     * @param model
     * @PRE gpu model ==null
     * @POST gpu model == updated model
     */
    public void setModel(Model model) {
        this.model = model;
    }


    public Model getModel() {
        return model;
    }

    public boolean isFree() {
        return model == null;
    }



    /**
     * @param queue this function saves the queue we receive from the message bus
     * @PRE check that the queue was empty
     */
    public void setBus_queue(Queue<Event> queue) {
        this.bus_queue = queue;
    }

    public Queue<Event> getBus_queue() {
        return bus_queue;
    }

    /**
     * @param startindex
     * @return
     * @PRE check that one dataBatch is being sent
     */
    public DataBatch sendDataToCluster(int startindex) {
        DataBatch chunk = new DataBatch();
        if (model.getData().getSize() - startindex > 1000) return chunk;
        else if (model.getData().getSize() - startindex == 0)
            return null; //break whenever we went through all the data.
        else return new DataBatch(model.getData().getSize() - startindex);
    }

    public Integer getTrainedData() {
        return trained;
    }


    /**
     * sends all the DataBatches to the cluster untill data is empty
     *
     * @POST check that all the data was sent
     */
    public void SendDataToCluster() {
        if (getModel() == null) return;
        DataBatch chunk = new DataBatch();
        int startindex = 0;
        while (chunk != null) {
            chunk = sendDataToCluster(startindex);
            startindex += chunk.size();
            //send chunk to cluster
        }
    }

    public void addTrained(Integer handled) {
        trained += handled;
    }






    /**
     * this function talk to the Cluster and receives DataBatches that are being sent.
     *
     * @param dataBatch
     * @POST check that the processed data has been updated.
     */
    public void receiveDataFromCluster(DataBatch dataBatch) {
        model.getData().addProcessed(dataBatch.size());
        trainModel(dataBatch); // Maybe it's better calling this function from the Cluster
    }

    /**
     * this functions is training the model of the GPU by the time of the GPU type.
     *
     * @param dataBatch
     * @POST check that all the data after the training time has been added to handled field.
     */
    public void trainModel(DataBatch dataBatch) { //needs to be done with threads
        //need to implement TimeService for this
    }


}
