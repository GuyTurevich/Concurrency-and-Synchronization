package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl singleton;
	private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> messageQueues;
	private ConcurrentHashMap<Event<?>, LinkedList<MicroService>> eventsSubscribers;
	private ConcurrentHashMap<Broadcast , LinkedList<MicroService>> broadcasts;
	private ConcurrentHashMap<Event, Future> eventFuture; //stores current events + their future obj

	public MessageBusImpl(){
		this.messageQueues = new ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>>();
		this.eventsSubscribers = new ConcurrentHashMap<Event<?>,LinkedList<MicroService>>();
		this.broadcasts = new ConcurrentHashMap<Broadcast , LinkedList<MicroService>>();
		this.eventFuture = new ConcurrentHashMap<Event,Future>();
	}

	public static MessageBusImpl getInstance(){
		if (singleton==null) singleton= new MessageBusImpl();

		return singleton;

	}

	@Override
	/**
	 * @PRE message_queues !=null , events !=null
	 */
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	/**
	 *
	 * @param type 	The type to subscribe to.
	 * @param m    	The subscribing micro-service.
	 * @PRE  message_queues !=null , broadcast !=null
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	/**
	 *
	 * @param e      The completed event.
	 * @param result The resolved result of the completed event.
	 * @param <T>
	 *
	 * @PRE message_queues !=null
	 */
	@Override
	public <T> void complete(Event<T> e, T result) {

		eventFuture.get(e).resolve(result);
		synchronized (eventFuture.get(e)) {
			eventFuture.get(e).notifyAll();
		}
		eventFuture.remove(e);

	}


	/**
	 *
	 * @param b 	The message to added to the queues.
	 * @PRE    message_queues !=null , broadcast !=null
	 */


	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}

	/**
	 *
	 * @param e     	The event to add to the queue.
	 * @param <T>
	 * @return
	 *
	 * @PRE message_queues !=null , events !=null
	 */
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void register(MicroService m) {

		if(!messageQueues.containsKey(m))
			messageQueues.put(m,new ConcurrentLinkedQueue<Message>());

	}

	/**
	 *
	 * @param m the micro-service to unregister.
	 * @PRE message_queues !=null
	 */
	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	/**
	 *
	 * @param m The micro-service requesting to take a message from its message
	 *          queue.
	 * @return
	 * @throws InterruptedException
	 *
	 * @PRE message_queues !=null
	 */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {

		ConcurrentLinkedQueue<Message> queue = messageQueues.get(m);
		if (queue.isEmpty()) {
			synchronized (queue) {
				if (queue.isEmpty()) {
					try {
						queue.wait(); // wait for notify on the message queue (sendEvent,sendBroadcast)
					} catch (InterruptedException i) {
					}
				}
			}
		}
		Message message = messageQueues.get(m).remove();
		return message;
	}




	public Boolean isSubscribedToEvent(Event event){
		return eventsSubscribers.contains(event);
	}
	public Boolean isSubscribedToBroadCast(Broadcast broadcast){
		return broadcasts.contains(broadcast);
	}
	public boolean isRegistered(MicroService microService){
		return messageQueues.contains(microService);
	}

	

}
