package bgu.spl.mics.application.services;


import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	private static int count=1;//static counter to differentiate different instances of the service
	private int currentTick;
	private CountDownLatch countDownLatch;
	/**
	 *constructor
	 */
	public LogisticsService(CountDownLatch countDownLatch) {
		super("logistics "+count);
		this.countDownLatch = countDownLatch;
		count++;
		currentTick=0;//safety initialization

	}
	/**
	 *subscribes to wanted events and broadcasts
	 */
	protected void initialize() {
		subscribeDeliveryEvent();
		subscribeTickBroadcast();
		countDownLatch.countDown();
	}
	/**
	 * send {@link AcquireVehicleEvent} deliver the shipment using the returned vehicle if not null
	 * and send {@link FreeVehicleEvent}.
	 */
	private void subscribeDeliveryEvent()
	{
		subscribeEvent(DeliveryEvent.class, message->
		{
			Future<DeliveryVehicle> obtainedFuture;
			Future<Future<DeliveryVehicle>> futureObject=sendEvent(new AcquireVehicleEvent());//get Future containing a future Vehicle
            if (futureObject != null)
            {
				obtainedFuture = futureObject.get();
				if (obtainedFuture != null) 
				{
					DeliveryVehicle obtainedVehicle=obtainedFuture.get();
					if(obtainedVehicle!=null)
					{
						obtainedVehicle.deliver(message.getAddress(), message.getDistance());//deliver vehicle
						sendEvent(new FreeVehicleEvent(obtainedVehicle));//release vehicle
						this.complete(message, true);//mission successful
					}
					else
						this.complete(message, false);//no vehicle available, future<> resolved by resources service upon termination
				}else
					this.complete(message, false);//no vehicle available, Future<Future<>> resolved to null by selling unregistering
			}
            else
            	this.complete(message, false);//no microservice available for the request
		});	
	}
	private void subscribeTickBroadcast() {
		subscribeBroadcast(TickBroadcast.class, message ->
		{
			currentTick = message.getTick();
			if (currentTick == message.getDuration()) {
				terminate();
			}
		});
	}
}
