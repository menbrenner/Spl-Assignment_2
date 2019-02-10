package bgu.spl.mics.application.messages;
//from: selling service
//to: logistics service
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
public class DeliveryEvent implements Event<Boolean> {
	private Customer customer;

	public DeliveryEvent(Customer customer)
	{
		this.customer=customer;
	}
	public int getDistance()
	{
		return customer.getDistance();
	}
	public String getAddress()
	{
		return customer.getAddress();
	}
}
