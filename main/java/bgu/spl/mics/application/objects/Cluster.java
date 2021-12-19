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
	private ConcurrentHashMap<GPU, ConcurrentLinkedDeque<DataBatch>> sendToCPU;
	private ConcurrentHashMap<GPU,ConcurrentLinkedDeque<DataBatch>> processedBatches;
	private static Cluster singleton;
	private int roundRobin;

	public Cluster (GPU [] gpus, CPU [] cpus){
		this.gpus = gpus;
		this.cpus = cpus;
		this.roundRobin = 0;
		this.processedBatches = new ConcurrentHashMap<GPU,ConcurrentLinkedDeque<DataBatch>>();
		this.sendToCPU = new ConcurrentHashMap<GPU,ConcurrentLinkedDeque<DataBatch>>();
		for(GPU gpu : gpus){
			sendToCPU.put(gpu,new ConcurrentLinkedDeque<DataBatch>());
			processedBatches.put(gpu,new ConcurrentLinkedDeque<DataBatch>());

		}
		singleton = this;
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
		receiveBatchFromMap();


	}

	public CPU getFastestCpu(){
		CPU fastest=cpus[0];
		for(int i=1; i<cpus.length;i++){
			if (cpus[i].getTicksTillEnd()<fastest.getTicksTillEnd())
				fastest=cpus[i];
		}
		return fastest;
	}

	public void receiveBatchFromGpu(DataBatch dataBatch) {
		synchronized (sendToCPU) {
			sendToCPU.get(dataBatch.getGpu()).add(dataBatch);
			CPU cpu = getFastestCpu();
			if (cpu.getDbSize() < 100) {
				DataBatch toSend = getBatchToSend();
				if (toSend!=null) {
					synchronized (cpu) {
						cpu.addDataBatch(toSend);
					}
				}
			}
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

	public void roundRobin(){
		if (roundRobin+1 !=gpus.length)
			roundRobin++;
		else roundRobin =0;

	}

	public DataBatch getBatchToSend() {

			DataBatch toSend = sendToCPU.get(gpus[roundRobin]).pollFirst();
			int count = 0;
			while (toSend == null && count != gpus.length) {
				roundRobin();
				toSend = sendToCPU.get(gpus[roundRobin]).pollFirst();
				count++;
			}
			return toSend;

	}

	public void receiveBatchFromMap() {
		synchronized (sendToCPU) {
			CPU cpu = getFastestCpu();
			if (cpu.getDbSize() < 100) {
					DataBatch toSend = getBatchToSend();
					if (toSend!=null) {
						synchronized (cpu) {
							cpu.addDataBatch(toSend);
						}
					}
				}
		}
	}





}
