package bgu.spl.mics.application.passiveObjects;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import static bgu.spl.mics.application.passiveObjects.OrderResult.NOT_IN_STOCK;
import static bgu.spl.mics.application.passiveObjects.OrderResult.SUCCESSFULLY_TAKEN;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
	private static Inventory instance = null;
	private ConcurrentHashMap <String,Integer> PriceMap =new ConcurrentHashMap <>();
	private ConcurrentHashMap <String,Integer> AmountMap =new ConcurrentHashMap <>();
	private ConcurrentHashMap <String , Object> locks = new ConcurrentHashMap<>();

	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {
		if(instance == null) {
			instance = new Inventory();
		}
		return instance;
	}

	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) {
		for(int i = 0 ; i < inventory.length ; i++){
			BookInventoryInfo current = inventory[i];
			PriceMap.put(current.getBookTitle() , current.getPrice());
			AmountMap.put(current.getBookTitle() , current.getAmountInInventory());
			locks.put(current.getBookTitle() , new Object());
		}
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */

	public OrderResult take (String book) {
        Object lock = locks.get(book);
        if (lock != null) {
            synchronized (lock) {
            	int currentAmount = this.AmountMap.get(book);
                if (currentAmount > 0) {
					AmountMap.replace(book,currentAmount - 1);
					return SUCCESSFULLY_TAKEN;
				}
            }
        }
        return NOT_IN_STOCK;
    }	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
        Integer currentAmount = AmountMap.get(book);
        if (currentAmount != null) {
            synchronized (book) {
                if (currentAmount> 0)
                    return PriceMap.get(book);
            }
        }
        return -1;
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename)
	{

		HashMap <String, Integer> BookMap=new HashMap<String, Integer>();
		BookMap.putAll(AmountMap);//copy to HashMap as required in specifications
		try
		{
			FileOutputStream file = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(BookMap);
			out.close();
			file.close();
		}
		catch(IOException ex)
		{
			System.out.println("IOException is caught");
		}
	 }
}
