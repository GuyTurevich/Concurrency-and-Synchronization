package bgu.spl.mics.application.objects;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.services.CPUService;

import java.util.Collection;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU extends MicroService {
    private int cores;
    private Collection <DataBatch> db;
    private final Cluster cluster;
    private int tick;
    private CPUService process;
    // process()

    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public CPU(String name,Cluster cluster, Collection<DataBatch> db) {
        super(name);
        this.cluster=cluster;
        this.db=db;
        tick =0;
    }

    public void process(DataBatch data){

    }

    @Override
    protected void initialize() {

    }
}
