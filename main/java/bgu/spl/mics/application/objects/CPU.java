package bgu.spl.mics.application.objects;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.services.CPUService;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU  {
    private int cores;
    private Vector <DataBatch> db;
    private final Cluster cluster;
    private int tick;
    private CPUService CPUProcess;


    public CPU(Cluster cluster, Vector<DataBatch> db) {
        this.cluster=cluster;
        this.db=db;
        tick =0;
    }

    //
    public void run(Vector<DataBatch> db){
        this.db=db;
    }

    public void process(){

    }

}
