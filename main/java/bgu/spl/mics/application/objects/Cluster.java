package bgu.spl.mics.application.objects;


import bgu.spl.mics.MessageBusImpl;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
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
//	private ConcurrentHashMap<GPU, ConcurrentLinkedDeque<DataBatch>> gpuQueues;
//	private ConcurrentHashMap<CPU,ConcurrentLinkedQueue<DataBatch>> cpuQueues; //might not need
	private ConcurrentHashMap<GPU,ConcurrentLinkedDeque<DataBatch>> processedBatches;
	private static Cluster singleton;

	public Cluster (GPU [] gpus, CPU [] cpus){
		this.gpus = gpus;
		this.cpus = cpus;
		this.processedBatches = new ConcurrentHashMap<GPU,ConcurrentLinkedDeque<DataBatch>>();
		for(GPU gpu : gpus){
//			gpuQueues.put(gpu,new ConcurrentLinkedDeque<DataBatch>());
			processedBatches.put(gpu,new ConcurrentLinkedDeque<DataBatch>());
		}
//		for(CPU cpu : cpus){
//			cpuQueues.put(cpu,new ConcurrentLinkedQueue<DataBatch>());
//		}

	}

	public Cluster(){}
	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		if (singleton==null) singleton= new Cluster();

		return singleton;
	}

//	public ConcurrentLinkedDeque<DataBatch> getGpuQueue(GPU gpu){
//		return gpuQueues.get(gpu);
//	}

	public void getBatchFromCpu(DataBatch dataBatch){
		processedBatches.get(dataBatch.getGpu()).add(dataBatch);
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
		synchronized (cpu) {
			cpu.addDataBatch(dataBatch);
		}
	}

	public int getGpuQueueSize(GPU gpu){
		return processedBatches.get(gpu).size();
	}

	public void removeFirstFromQueue(GPU gpu){
		ConcurrentLinkedDeque<DataBatch> gpuDeque = processedBatches.get(gpu);
		synchronized (gpuDeque) {
			gpuDeque.removeFirst();
		}
	}






}
