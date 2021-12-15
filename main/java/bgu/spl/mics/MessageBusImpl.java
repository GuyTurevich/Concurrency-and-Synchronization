package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
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
	private ConcurrentHashMap<Class<? extends Event>, LinkedList<MicroService>> eventsSubscribers;
	private ConcurrentHashMap<Class<? extends Broadcast> , LinkedList<MicroService>> broadcasts;
	private ConcurrentHashMap<Event, Future> eventFuture; //stores current events + their future obj

	public MessageBusImpl(){
		this.messageQueues = new ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>>();
		this.eventsSubscribers = new ConcurrentHashMap<Class<? extends Event>,LinkedList<MicroService>>();
		this.broadcasts = new ConcurrentHashMap<Class<? extends Broadcast> , LinkedList<MicroService>>();
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
		synchronized (eventsSubscribers) {
			eventsSubscribers.putIfAbsent(type, new LinkedList<MicroService>());
			eventsSubscribers.get(type).add(m);
		}

		//Maybe need to add a reverse hashmap if we need to get events list by key - Microservice
	}

	/**
	 *
	 * @param type 	The type to subscribe to.
	 * @param m    	The subscribing micro-service.
	 * @PRE  message_queues !=null , broadcast !=null
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (broadcasts){
			broadcasts.putIfAbsent(type,new LinkedList<MicroService>());
			broadcasts.get(type).add(m);

			//Maybe need to add a reverse hashmap if we need to get broadcasts list by key - Microservice
		}

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
		LinkedList<MicroService> subs = broadcasts.get(b);
		synchronized (broadcasts) {// in case of unregister we will have out of bounds Exception.
			for (int i = 0; i < subs.size(); i++) {
				ConcurrentLinkedQueue<Message> messages =messageQueues.get(subs.get(i)); //get Microservice message queue
				messages.add(b);
				if (messages.size()==1) //if the message we added is the only message in queue we will notify the queue
					synchronized (messages){
					messages.notify();
					}
			}
		}
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
		Future future =new Future<T>();
		eventFuture.put(e,future);
		LinkedList<MicroService>  subs= eventsSubscribers.get(e.getClass());
		if (subs==null || subs.size()==0){
			return null;
		}
		MicroService microService ;
		synchronized (subs){
			microService = subs.peek();
		}

		ConcurrentLinkedQueue<Message> messages = messageQueues.get(microService);
		messageQueues.get(microService).add(e); // adding the event to messagesQueues

		if (messages.size()==1){ //if the message we added is the only message in queue we will notify the queue
			synchronized (messages){
				messages.notify();
			}
		}
		return future;
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
