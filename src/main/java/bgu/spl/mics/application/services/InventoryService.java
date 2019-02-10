package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.ExportBookEvent;
import bgu.spl.mics.application.messages.GetPriceEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private int currentTick;
	private CountDownLatch countDownLatch;
	private Inventory inventory;
	private static int count=1;//static counter to differentiate different instances of the service

	/**
	 *The constructor gets an instance of {@link Inventory}
	 */
	public InventoryService(CountDownLatch countDownLatch) {
		super("inventory "+count);
		this.countDownLatch = countDownLatch;
		inventory=Inventory.getInstance();
		currentTick=0;//safety initialization
		count++;
	}
	/**
	 *subscribes to wanted events and broadcasts
	 */
	protected void initialize() {
		subscribeTickBroadcast();
		subscribeGetPriceEvent();
		subscribeExportBookEvent();
		countDownLatch.countDown();

	}
	private void subscribeExportBookEvent() {
		subscribeEvent(ExportBookEvent.class, message->
		{	
			OrderResult result=inventory.take(message.getBook());
			this.complete(message, result);
		});
				
	}

	private void subscribeTickBroadcast() 
	{
		subscribeBroadcast(TickBroadcast.class, message->
		{
			currentTick=message.getTick();
			if(currentTick==message.getDuration()) {
				terminate();
			}
		});
		

	}
	private void subscribeGetPriceEvent() {
		subscribeEvent(GetPriceEvent.class, message->
		{
			int price=inventory.checkAvailabiltyAndGetPrice(message.getBook());
			this.complete(message, price);
		});

	}

}
