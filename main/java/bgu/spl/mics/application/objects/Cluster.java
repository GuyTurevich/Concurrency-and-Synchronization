package bgu.spl.mics.application.objects;


import bgu.spl.mics.MessageBusImpl;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private static Cluster singleton;

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		if (singleton==null) singleton= new Cluster();

		return singleton;
	}

}
