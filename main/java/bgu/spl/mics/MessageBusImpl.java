package bgu.spl.mics;

import java.util.LinkedList;
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
    private ConcurrentHashMap<Class<? extends Event>, LinkedList<MicroService>> eventsSubs;
    private ConcurrentHashMap<Class<? extends Broadcast>, LinkedList<MicroService>> broadcastsSubs;
    private ConcurrentHashMap<MicroService, LinkedList<Class<? extends Event>>> unregisterEvent; // reverse eventSubscribers object
    private ConcurrentHashMap<MicroService, LinkedList<Class<? extends Broadcast>>> unregisterBroadcast; // reverse broadcasts object
    private ConcurrentHashMap<Event, Future> eventFuture; //stores current events + their future obj


    public MessageBusImpl() {
        this.messageQueues = new ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>>();
        this.eventsSubs = new ConcurrentHashMap<Class<? extends Event>, LinkedList<MicroService>>();
        this.broadcastsSubs = new ConcurrentHashMap<Class<? extends Broadcast>, LinkedList<MicroService>>();
        this.unregisterEvent = new ConcurrentHashMap<MicroService, LinkedList<Class<? extends Event>>>();
        this.unregisterBroadcast = new ConcurrentHashMap<MicroService, LinkedList<Class<? extends Broadcast>>>();
        this.eventFuture = new ConcurrentHashMap<Event, Future>();

    }

    public static MessageBusImpl getInstance() {
        if (singleton == null) singleton = new MessageBusImpl();

        return singleton;

    }

    @Override
    /**
     * @PRE message_queues !=null , events !=null
     */
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        synchronized (eventsSubs) {
            eventsSubs.putIfAbsent(type, new LinkedList<MicroService>());
            eventsSubs.get(type).add(m);
        }
        synchronized (unregisterEvent) {
            unregisterEvent.putIfAbsent(m, new LinkedList<Class<? extends Event>>());
            unregisterEvent.get(m).add(type);
        }

        //Maybe need to add a reverse hashmap if we need to get events list by key - Microservice
    }

    /**
     * @param type The type to subscribe to.
     * @param m    The subscribing micro-service.
     * @PRE message_queues !=null , broadcast !=null
     */
    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        synchronized (broadcastsSubs) {
            broadcastsSubs.putIfAbsent(type, new LinkedList<MicroService>());
            broadcastsSubs.get(type).add(m);
        }
        synchronized (unregisterBroadcast) {
            unregisterBroadcast.putIfAbsent(m, new LinkedList<Class<? extends Broadcast>>());
            unregisterBroadcast.get(m).add(type);
        }
    }

    /**
     * @param e      The completed event.
     * @param result The resolved result of the completed event.
     * @param <T>
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
     * @param b The message to added to the queues.
     * @PRE message_queues !=null , broadcast !=null
     */


    @Override
    public void sendBroadcast(Broadcast b) {
        LinkedList<MicroService> subs = broadcastsSubs.get(b);
        synchronized (broadcastsSubs) {// in case of unregister we will have out of bounds Exception.
            for (int i = 0; i < subs.size(); i++) {
                ConcurrentLinkedQueue<Message> messages = messageQueues.get(subs.get(i)); //get Microservice message queue
                messages.add(b);
                if (messages.size() == 1) //if the message we added is the only message in queue we will notify the queue
                    synchronized (messages) {
                        messages.notify();
                    }
            }
        }
    }

    /**
     * @param e   The event to add to the queue.
     * @param <T>
     * @return
     * @PRE message_queues !=null , events !=null
     */

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        Future future = new Future<T>();
        eventFuture.put(e, future);
        LinkedList<MicroService> subs = eventsSubs.get(e.getClass());
        if (subs == null || subs.size() == 0) {
            return null;
        }
        MicroService microService;
        synchronized (subs) {
            microService = subs.peek();
        }

        ConcurrentLinkedQueue<Message> messages = messageQueues.get(microService);
        messageQueues.get(microService).add(e); // adding the event to messagesQueues

        if (messages.size() == 1) { //if the message we added is the only message in queue we will notify the queue
            synchronized (messages) {
                messages.notify();
            }
        }
        return future;
    }


    @Override
    public void register(MicroService m) {

        if (!messageQueues.containsKey(m))
            messageQueues.put(m, new ConcurrentLinkedQueue<Message>());
    }

    /**
     * @param m the micro-service to unregister.
     * @PRE message_queues !=null
     */
    @Override
    public void unregister(MicroService m) {
        if (unregisterEvent.get(m) != null) {
            synchronized (eventsSubs) {
                for (Class<? extends Event> event : unregisterEvent.get(m)) {
                    eventsSubs.get(event).remove();
                }
            }
            unregisterEvent.remove(m);
        }

        if (unregisterBroadcast.get(m)!=null){
            synchronized (eventsSubs){
                for (Class<? extends Broadcast> broadcast : unregisterBroadcast.get(m)){
                    broadcastsSubs.get(broadcast).remove();
                }
            }
            unregisterBroadcast.remove(m);
        }

        messageQueues.remove(m); // remove message




    }

    /**
     * @param m The micro-service requesting to take a message from its message
     *          queue.
     * @return
     * @throws InterruptedException
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


    public Boolean isSubscribedToEvent(Event event) {
        return eventsSubs.contains(event);
    }

    public Boolean isSubscribedToBroadCast(Broadcast broadcast) {
        return broadcastsSubs.contains(broadcast);
    }

    public boolean isRegistered(MicroService microService) {
        return messageQueues.contains(microService);
    }


}
