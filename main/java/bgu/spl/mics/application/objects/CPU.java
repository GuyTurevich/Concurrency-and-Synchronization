package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;

import java.util.Vector;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private Vector <DataBatch> db;
    private final Cluster cluster;
    private int tick;
    private int ticksTillEnd;
    private CPUService CPUProcess;
    private Data.Type type;


    public CPU(Cluster cluster, Vector<DataBatch> db,Data.Type type) {
        this.cluster=Cluster.getInstance();
        this.db=db;
        tick =0;
        this.type=type;
    }
    public CPU (int cores){
        this.cores = cores;
        this.cluster= Cluster.getInstance();
    }

    public void addDataBatch(DataBatch dataBatch){
        db.add(dataBatch);
        ticksTillEnd += (32/cores)*dataBatch.numberOfTicks();
    }

    public int getTicksTillEnd(){
        return ticksTillEnd;
    }

    public boolean dbEmpty(){
        return db.isEmpty();
    }

    public int firstBatchTicks(){
        return db.firstElement().numberOfTicks() * 32/cores;
    }

    public void finishProcess(){
        db.remove(0);
        this.tick=0;
    }









    //
    public void run(Vector<DataBatch> db){
        this.db=db;
    }

    /**
     * this functions add databatch of unprocessed data to our vector
     * @param databatch
     *
     * @POST check that the new data batch is in the vector
     */
    public void receiveBatch(DataBatch databatch){
        db.add(databatch);
    }

    /**
     * this functions process the unprocessed data in a certain amout of time - depends on the number of cores
     * when finished it will also send the data back to the cluster
     *
     * @POST check that the DataBatch that has been processed has been past on
     */
    public DataBatch process(DataBatch databatch){
        DataBatch processed = new DataBatch();
        if (!db.isEmpty()){
            processed = db.remove(0);
            //add sleep by the data type
        }
        return processed;
    }

    public Vector<DataBatch> getDb(){
        return db;
    }

    /**
     * this function adds count after every tick of the timeservice
     *
     * @POST check that the tick has been added
     */
    public void increaseTick(){
        tick++;
    }

    public int getTick(){
        return tick;
    }

}
