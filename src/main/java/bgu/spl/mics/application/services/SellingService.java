package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;

import static bgu.spl.mics.application.passiveObjects.OrderResult.SUCCESSFULLY_TAKEN;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService
{
	private static int count=1;//static counter to differentiate different instances of the service
	private MoneyRegister moneyRegister;
	private int currentTick;
	private CountDownLatch countDownLatch;
	
	/**
	 *The constructor gets an instance of {@link MoneyRegister}
	 */
	public SellingService(CountDownLatch countDownLatch)
	{
		super("selling "+count);
		this.countDownLatch = countDownLatch;
		moneyRegister=MoneyRegister.getInstance();
		currentTick=0;	//safety initialization
		count++;
	}
	/**
	 *subscribes to wanted events and broadcasts
	 */
	protected void initialize() 
	{
		subscribeBookOrderEvent();
		subscribeTickBroadcast();
		countDownLatch.countDown();
	}

	private void subscribeBookOrderEvent() {
		subscribeEvent(BookOrderEvent.class, message->
		{
			int customerId=message.getCustomer().getId();//decipher message
			String customerName=message.getName();
			int orderTick=message.getOrderTick();
            OrderReceipt potentialReceipt=new OrderReceipt(customerId,customerName,orderTick,currentTick,this.getName());//create semi filled receipt
			Future<Integer> futureObject=sendEvent(new GetPriceEvent(message.getName()));
            if (futureObject != null && futureObject.get() != null)
            {
            	int price= futureObject.get();
            	if(checkOrderAndTakeBook(price,message.getCustomer(),potentialReceipt,message.getName()))
            	{//check if book can be taken and take it if so, updating potentialReceipt
            		this.complete(message, potentialReceipt);//order is completed 
            		moneyRegister.file(potentialReceipt);
            		sendEvent(new DeliveryEvent(message.getCustomer()));//send book to customer
            	}
            	else//cannot complete Order
            		this.complete(message, null);
            }
            else//no micro service is registered, order cannot be completed
            	this.complete(message, null);		});
	}
	private void subscribeTickBroadcast() 
	{
		subscribeBroadcast(TickBroadcast.class, message->
		{
			currentTick=message.getTick();
			if (currentTick == message.getDuration()) {//termination tick
					terminate();
			}
		});
	}
	/**
	 * checks if all conditions for customer to buy a book of price "price" are met
	 * and if so, removes the book from the inventory, charges the customer and updates the receipt.
	 *  <p>
	 * @param book    Name of the book ordered.
     * @param customer      The customer ordering the book {@code book}.
     * @param price   The price of the ordered book {@code book}.
     * @return 	boolean  whether order was successful or not.
	 */
	private boolean checkOrderAndTakeBook(int price,Customer customer,OrderReceipt orderReceipt, String book) 
	{
		if(price!=-1)//book is not available
			synchronized(customer)//lock wallet
			{
				if(customer.isLegalOrder(price))
				{
					Future<OrderResult> futureObject=sendEvent(new ExportBookEvent(book));
					if(futureObject.get()!=SUCCESSFULLY_TAKEN)//book cannot be taken
						return false;
					else //book is removed, charge customer, and update receipt
					{
						moneyRegister.chargeCreditCard(customer,price);
						orderReceipt.setPrice(price);
						orderReceipt.setIssuedTick(currentTick);
						return true;
					}
				}
			}
		return false;
	}

}
