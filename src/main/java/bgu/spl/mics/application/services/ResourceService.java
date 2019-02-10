package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;

import bgu.spl.mics.application.passiveObjects.*;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;


/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService
{
	private LinkedList<Future<DeliveryVehicle>> FutureVehicles =new LinkedList<>();
	private static int count=1;//static counter to differentiate different instances of the service
	private ResourcesHolder resourcesHolder;
	private int currentTick;
	private CountDownLatch countDownLatch;
	/**
	 *constructor gets the instance of {@link ResourcesHolder}.
	 */
	public ResourceService(CountDownLatch countDownLatch) {
		super("resources "+count);
		this.countDownLatch = countDownLatch;
		resourcesHolder=ResourcesHolder.getInstance();
		count++;
		currentTick=0;//safety initialization
	}
	/**
	 *subscribes to wanted events and broadcasts
	 */
	protected void initialize() {
		subscribeAcquireVehicleEvent();
		subscribeTickBroadcast();
		subscribeFreeVehicleEvent();
		countDownLatch.countDown();

	}
	/**
	 *call acquire vehicle to get a future representing a potential vehicle
	 * if it is not resolved, save the future associated with it
	 *to support termination.
	 */
	private void subscribeAcquireVehicleEvent() {
		subscribeEvent(AcquireVehicleEvent.class, message->
		{
			Future<DeliveryVehicle> futureObject=resourcesHolder.acquireVehicle();
			if(!futureObject.isDone()) {
				FutureVehicles.add(futureObject);
			}
			this.complete(message, futureObject);
		});
	}
	/**
	 * release the vehicle stored in message
	 * making it available to delivery
	 */
	private void subscribeFreeVehicleEvent() {
		subscribeEvent(FreeVehicleEvent.class, message->
		{
			resourcesHolder.releaseVehicle(message.getVehicle());
			this.complete(message,true);

		});		
	}
	private void subscribeTickBroadcast() {
		subscribeBroadcast(TickBroadcast.class, message ->
		{
			currentTick = message.getTick();
			if (currentTick == message.getDuration()) {
				for(Future<DeliveryVehicle> element: FutureVehicles) {//upon termination, resolve all  unresolved futures to null
					if (!element.isDone()) {
						element.resolve(null);
					}
				}
				terminate();
			}
		});
	}

	}

