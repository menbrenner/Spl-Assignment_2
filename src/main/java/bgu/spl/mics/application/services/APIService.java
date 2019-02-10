package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {
	private int currentTick;
	private static int count=1;//static counter to differentiate different instances of the service
	private Customer myCustomer;
	private ConcurrentHashMap<Integer, ArrayList<BookOrderEvent>> orderList;
	private CountDownLatch c;
	
	/**
	 *The constructor gets the {@link Customer} represented by this Api service
	 *and initializes  fields as needed, amongst them building a data structure to implement Book orders upon 
	 *the arrival of a tick
	 */
	public APIService(Customer c , CountDownLatch countDownLatch)
	{
		super("API "+count);
		count++;
		this.c = countDownLatch;
		currentTick = 0;//safety initialization
		this.myCustomer = c;
		orderList = new ConcurrentHashMap<Integer, ArrayList<BookOrderEvent>>();
		for (FutureOrder order : c.getOrderSchedule()) 
		{
			int orderTick= order.getOrderTick();
			String bookName=order.getBookName();
			BookOrderEvent orderEvent = new BookOrderEvent(bookName, myCustomer,orderTick);
			if (orderList.containsKey(orderEvent.getOrderTick()))//check if a list for tick exists
				orderList.get(order.getOrderTick()).add(orderEvent);
			else // if not, create one
			{
				ArrayList<BookOrderEvent> unrecognizedTickList = new ArrayList<BookOrderEvent>();
				unrecognizedTickList.add(orderEvent);
				orderList.put(order.getOrderTick(), unrecognizedTickList);
			}
		}
	}

	/**
	 *subscribes to wanted events and broadcasts
	 */
	protected void initialize() {
		subscribeTickBroadcast();
		c.countDown();
	}


	/**
	 * Upon  arrival of a {@link TickBroadcast} ship all orders meant to be ordered in this tick
	 * and update {@link Customer} with receipts
	 */
	private void subscribeTickBroadcast() {
		subscribeBroadcast(TickBroadcast.class, message ->
		{
			ArrayList<Future <OrderReceipt>> waitingList = new ArrayList<>();
			currentTick = message.getTick();
			if(currentTick<message.getDuration()) //check for termination tick
			{
				if (orderList.get(currentTick)!= null && !orderList.get(currentTick).isEmpty()) //check if there are orders in this tick
				{
					for (BookOrderEvent orderEvent : orderList.get(currentTick)) //send events and store futures
					{
						Future<OrderReceipt> futureReceipt = sendEvent(orderEvent);
						waitingList.add(futureReceipt);
					}
				}
				for (Future<OrderReceipt> futureReceipt : waitingList) //get futures and store receipts in customer
				{
					if(futureReceipt!=null)// was someone there to take there of event?
					{
						OrderReceipt receipt = futureReceipt.get();
						if(receipt!=null)// was the order successful?
							myCustomer.addreceipt(receipt);
					}
				}
			}else//termination tick
				terminate();
		});
	}
}