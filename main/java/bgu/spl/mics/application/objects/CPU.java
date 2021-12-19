package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private ConcurrentLinkedDeque<DataBatch> db;
    private final Cluster cluster;
    private int tick;
    private int ticksTillEnd;
    private int timeUsed;


    public CPU (int cores){
        this.cores = cores;
        this.db = new ConcurrentLinkedDeque<DataBatch>();
        this.cluster= Cluster.getInstance();
        tick = 0;
        ticksTillEnd = 0;
        timeUsed = 0;
    }

    public void addDataBatch(DataBatch dataBatch){
        synchronized (this) {
            db.add(dataBatch);
            ticksTillEnd += (32/cores)*dataBatch.numberOfTicks();
        }

    }

    public int getTicksTillEnd(){
        return ticksTillEnd;
    }

    public boolean dbEmpty(){
        return db.isEmpty();
    }

    public int firstBatchTicks(){
        return db.getFirst().numberOfTicks() * 32/cores;
    }

    public void finishProcess(){
        synchronized (this) {
            ticksTillEnd -= firstBatchTicks();
            db.removeFirst();
            this.tick = 0;
        }
    }

    public void incrementTimeUsed(){
        timeUsed +=firstBatchTicks();
    }









    //
    public void run(ConcurrentLinkedDeque<DataBatch> db){
        this.db=db;
    }

    /**
     * this functions add databatch of unprocessed data to our vector
     * @param databatch
     *
     * @POST check that the new data batch is in the vector
     */
//    public void receiveBatch(DataBatch databatch){
//        db.add(databatch);
//    }

    /**
     * this functions process the unprocessed data in a certain amout of time - depends on the number of cores
     * when finished it will also send the data back to the cluster
     *
     * @POST check that the DataBatch that has been processed has been past on
     */
//    public DataBatch process(DataBatch databatch){
//        DataBatch processed = new DataBatch();
//        if (!db.isEmpty()){
//            processed = db.remove(0);
//            //add sleep by the data type
//        }
//        return processed;
//    }

    public ConcurrentLinkedDeque<DataBatch> getDb(){
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
