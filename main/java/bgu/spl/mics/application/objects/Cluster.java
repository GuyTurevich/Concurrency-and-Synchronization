package bgu.spl.mics.application.objects;


import bgu.spl.mics.MessageBusImpl;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private GPU [] gpus;
	private CPU [] cpus;
	private ConcurrentHashMap<GPU, ConcurrentLinkedQueue> gpuQueues;
	private ConcurrentHashMap<CPU,ConcurrentLinkedQueue> cpuQueues; //might not need
	private Vector<DataBatch> processedBatches;
	private static Cluster singleton;

	public Cluster (GPU [] gpus, CPU [] cpus){
		this.gpus = gpus;
		this.cpus = cpus;
		for(GPU gpu : gpus){
			gpuQueues.put(gpu,new ConcurrentLinkedQueue());
		}
		for(CPU cpu : cpus){
			cpuQueues.put(cpu,new ConcurrentLinkedQueue());
		}
	}

	public Cluster(){}
	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		if (singleton==null) singleton= new Cluster();

		return singleton;
	}

	public ConcurrentLinkedQueue getGpuQueue(GPU gpu){
		return gpuQueues.get(gpu);
	}

	public void getBatchFromCpu(DataBatch dataBatch){
		processedBatches.add(dataBatch);
	}

	public CPU getFastestCpu(){
		CPU fastest=cpus[0];
		for(int i=1; i<cpus.length;i++){
			if (cpus[i].getTicksTillEnd()<fastest.getTicksTillEnd())
				fastest=cpus[i];
		}
		return fastest;
	}

	public void receiveBatchFromGpu(DataBatch dataBatch){
		CPU cpu = getFastestCpu();
		cpu.addDataBatch(dataBatch);
	}

	public DataBatch getNextProcessed(){
		return processedBatches.remove(0);
	}






}
