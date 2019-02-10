package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {
	private int price;
	private String bookTitle;
	private int amount;

	public BookInventoryInfo(int price , String bookTitle , int amount){
		this.price = price;
		this.bookTitle = bookTitle;
		this.amount = amount;
	}
	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String getBookTitle() {
		return bookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return amount;
	}
	/**
     * reduce by one the current amount in inventory.     
     */
	public void reduceAmountInInventory(){
		amount--;
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return price;
	}
	
	

	
}
