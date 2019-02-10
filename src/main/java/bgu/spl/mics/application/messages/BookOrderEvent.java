package bgu.spl.mics.application.messages;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
//from: API service
//to: selling service
public class  BookOrderEvent implements Event<OrderReceipt> {
	private String book;
	private Customer customer;
	private int OrderTick;

	public BookOrderEvent(String book, Customer c,int OrderTick)
	{
		this.OrderTick=OrderTick;
		this.book=book;
		this.customer=c;
		
	}

	public String getName( )
	{
		return book;
	}
	public Customer getCustomer( )
	{
		return customer;
	}
	public int getOrderTick() 
	{
		return OrderTick;
	}
}
