package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable{
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String address;
	private int distance;
	private List<OrderReceipt> customerReceiptList;
	private creditCard creditCard;
	private FutureOrder[] orderSchedule;
	private class creditCard implements Serializable{
		private static final long serialVersionUID = 1L;
		private int number;
		private int amount;
		/**
		 * set credit card number.
		 */
		@SuppressWarnings("unused")
		public void setNumber(int number) {
			this.number = number;
		}
		/**
		 * set amount of money in credit card.
		 */
		public void setAmount(int amount) {
			this.amount = amount;
		}
		/**
		 * <p>
		 *  * @return the number of the credit card.
		 */
		public int getNumber() {
			return number;
		}
		/**
		 * <p>
		 *  * @return the amount of money in the credit card.
		 */
		public int getAmount() {
			return amount;
		}
	}


	public Customer(){
		this.customerReceiptList = new ArrayList<OrderReceipt>();
	}
	/**
	 * Constructor by given values.
	 */
	public Customer(int id , String name , String address , int distance , creditCard credit , FutureOrder[] orderSchedule){
		this.id = id;
		this.name = name;
		this.address = address;
		this.distance = distance;
		this.customerReceiptList = new ArrayList<OrderReceipt>();
		this.creditCard = credit;
		this.orderSchedule = orderSchedule;

	}
	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}
	/**
     * Retrieves the order schedule of the customer.  
     */
	public FutureOrder[] getOrderSchedule() {
		return orderSchedule;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return customerReceiptList;
	}
	/**
     *adds a receipt to the customers receipt list
     */
	public void addreceipt(OrderReceipt receipt) {
		 this.customerReceiptList.add(receipt);
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return creditCard.getAmount();
	}
	/**
     *charges an amount of money from the customer.
     */
	public void chargeByAmount(int moneySpend) {
		creditCard.setAmount(creditCard.getAmount() - moneySpend);
	}
	/**
     *checks if the order can be done.
     */
	public boolean isLegalOrder(int amount){
		return amount <= creditCard.getAmount();
	}
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return creditCard.getNumber();
	}
}
