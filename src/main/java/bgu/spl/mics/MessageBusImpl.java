package bgu.spl.mics;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;



/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> MicroServiceQueuesTable = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Class<?>, ConcurrentLinkedQueue<MicroService>> Subscriptions = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Event<?>, Future<?>> eventToFuture = new ConcurrentHashMap<>();
	private static ConcurrentLinkedQueue<Class<?>> ClassesList = new ConcurrentLinkedQueue<>();
	private static MessageBusImpl instance = null;

	public static MessageBusImpl getInstance() {
		if (instance == null)
			instance = new MessageBusImpl();
		return instance;

	}
	/**
	 *put the type of message in a list, in order to do the unregister later.
	 * also, add the microservice to this type of messages line.
	 */
	private void subscribe(Class<? extends Message> type, MicroService m) {
		//in order to maintenance unregister function thread safe.
		synchronized (ClassesList) {
			if (!ClassesList.contains(type)) {//in order to unregister later.
				ClassesList.add(type);
				Subscriptions.put(type, new ConcurrentLinkedQueue<MicroService>());
			}
			Subscriptions.get(type).add(m);
		}
	}/**
	 send it to a private function in order to complete the calculation.
	 */
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		subscribe(type, m);
	}
	/**
	 send it to a private function in order to complete the calculation.
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		subscribe(type, m);
	}
	/**
	 * gets the corresponding future and resole it.
	 */
	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> f = (Future<T>) eventToFuture.get(e);
		{
			f.resolve(result);
		}
	}
	/**
	 send broadcast to everyone that listen.
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		ConcurrentLinkedQueue<MicroService> subsQueue = Subscriptions.get(b.getClass());//gets the subscriptions list of the event's class
		synchronized (subsQueue){
			//its synchronized in order to avoid someone change the queue and than the iterator would throw nullPointerException.
			for (MicroService element : subsQueue) {
				synchronized (element) {
					//we need to put synchronized to run the notifyAll function, to awake the microservice if necessary
					ConcurrentLinkedQueue<Message> messageHandler = MicroServiceQueuesTable.get(element);
					messageHandler.add(b);
					element.notifyAll();
				}
			}
		}
	}


	/**
	 send event, use round robin to decide which microservice should handle it.
	 */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> f;
		ConcurrentLinkedQueue<MicroService> eventSubscriptionQueue = Subscriptions.get(e.getClass());//gets the subscriptions list of the event's class
			if (eventSubscriptionQueue != null) {//else there is no microservices registered to this event.
				synchronized (eventSubscriptionQueue) {//in case this microservice try to unregister.
					if (eventSubscriptionQueue.isEmpty()) {//no microservice available to complete this event, so we resolve the future to null in order to prevent deadlock.
						f = new Future<>();
						f.resolve(null);
					} else {
						MicroService MSeventHandler = eventSubscriptionQueue.remove();
						synchronized (MSeventHandler) {//in order to wake it up in the end of this block (in case the microservice wait to messages
							ConcurrentLinkedQueue<Message> EventHandlerWaitingList = MicroServiceQueuesTable.get(MSeventHandler);
							EventHandlerWaitingList.add(e);
							f = new Future<>();
							eventToFuture.put(e, f);
							eventSubscriptionQueue.add(MSeventHandler);//round robin maintenance
							MSeventHandler.notifyAll();
						}
					}
				}
			}else{
				f = new Future<>();
				f.resolve(null);
				}
		return f;
	}

	/**
	register new microservice to the messagebus.
	 */

	@Override
	public void register(MicroService m) {
		MicroServiceQueuesTable.put(m,new ConcurrentLinkedQueue<>());
		
	}
	/**
	 unregister a microservice that finish is work (the last tick was over).
	 */
	@Override
	public void unregister(MicroService m)
	{
		for(Class<?> element: ClassesList) {
			ConcurrentLinkedQueue<MicroService> messageSubscriptionQueue = Subscriptions.get(element);
			if (messageSubscriptionQueue.contains(m)) {
				synchronized (messageSubscriptionQueue) {//in order to prevent a case that the message bus try to add a message to this microservice. (could cause a nullPointerException).
					messageSubscriptionQueue.remove(m);
				}
			}
		}
		//in order to make sure there is no other event that wait to this microservice answer, we resolve all of his event messages to null.
		ConcurrentLinkedQueue<Message> MSeventQueue = MicroServiceQueuesTable.get(m);
			for(Message element : MSeventQueue){
				if(element instanceof Event)
					complete((Event<?>)element,null);
			}
	}
	/**
	 wait to message if the queue is empty. return an event if it's not.
	 */

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		ConcurrentLinkedQueue <Message> waitingList=MicroServiceQueuesTable.get(m);
		if(waitingList==null)
			throw new IllegalStateException("the service has never register to messageBus");
		synchronized (m) {
			while (waitingList.isEmpty()) {//case there is no events to handle in the queue,
				m.wait();
			}
			return waitingList.remove();
		}
	}

	

}
