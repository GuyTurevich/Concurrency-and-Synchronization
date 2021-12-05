package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;

import java.sql.Time;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

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
    private Model model ;
    private Cluster cluster;
    private Queue<Event> bus_queue;
    private Integer trained;

    public GPU(Type type){
        super();
        this.type=type;
        model = null;
        cluster = Cluster.getInstance();  //singelton
        bus_queue = null;
    }

    /**
     * this functions sets a new model for the gpu
     * @param model
     * @PRE gpu model ==null
     * @POST gpu model == updated model
     */
    public void setModel(Model model){
        this.model=model;
    }




    public Model getModel(){
        return model;
    }

    public boolean isFree(){
        return model==null;
    }

    /**
     *
     * @param queue
     * this function saves the queue we receive from the message bus
     * @PRE check that the queue was empty
     */
    public void setBus_queue(Queue<Event> queue){
        this.bus_queue = queue;
    }

    public Queue<Event> getBus_queue(){
        return bus_queue;
    }

    /**
     *
     * @param startindex
     * @return
     * @PRE check that one dataBatch is being sent
     */
    public DataBatch sendDataToCluster(int startindex){
        DataBatch chunk= new DataBatch();
        if (model.getData().getSize()-startindex>1000) return chunk;
        else if (model.getData().getSize()-startindex==0) return null; //break whenever we went through all the data.
        else return new DataBatch(model.getData().getSize()-startindex);
    }

    /**
     * sends all the DataBatches to the cluster untill data is empty
     * @POST check that all the data was sent
     */
    public void SendDataToCluster(){
        if (getModel()==null) return;
        DataBatch chunk= new DataBatch();
        int startindex = 0;
        while(chunk!=null){
            chunk = sendDataToCluster(startindex);
            startindex+=chunk.size();
            //send chunk to cluster
        }
    }

    public Integer getTrainedData(){
        return trained;
    }

    public void addTrained(Integer handled){
        trained+=handled;
    }

    /**
     * this function talk to the Cluster and receives DataBatches that are being sent.
     * @param dataBatch
     * @POST check that the processed data has been updated.
     */
    public void receiveDataFromCluster(DataBatch dataBatch){
        model.getData().addProcessed(dataBatch.size());
        trainModel(dataBatch); // Maybe it's better calling this function from the Cluster
    }

    /**
     * this functions is training the model of the GPU by the time of the GPU type.
     * @param dataBatch
     * @POST check that all the data after the training time has been added to handled field.
     */
    public void trainModel(DataBatch dataBatch){ //needs to be done with threads
        //need to implement TimeService for this
    }











}
