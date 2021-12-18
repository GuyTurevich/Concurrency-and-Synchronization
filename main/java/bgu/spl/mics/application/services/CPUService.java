package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.DataBatch;

/**
 * CPU service is responsible for handling the {link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {

    private CPU cpu;
    private Cluster cluster=Cluster.getInstance();

    public CPUService(String name, CPU cpu) {
        super(name);
        this.cpu = cpu;
    }

    private Callback<TickBroadcast> TickBroadcastCallBack = (TickBroadcast tickBroadcast) ->{
        cpu.increaseTick();
        if (!cpu.dbEmpty()) {
            if (cpu.firstBatchTicks() == cpu.getTick()) {
                DataBatch processed = cpu.getDb().firstElement(); //retrive processed databatch
                cluster.getBatchFromCpu(processed); //send batch to Cluster
                cpu.finishProcess();
            }
        }
    };


    private Callback<TerminationBroadcast> terminateCallback = (TerminationBroadcast terminationBroadcast) ->{
        terminate();
    };

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class,TickBroadcastCallBack);
        this.subscribeBroadcast(TerminationBroadcast.class, terminateCallback);
    }
}
