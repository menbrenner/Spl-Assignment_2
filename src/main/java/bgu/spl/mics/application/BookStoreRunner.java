package bgu.spl.mics.application;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.MessageBusImpl;
import com.google.gson.Gson;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
	public static void main(String[] args) {
		Gson gson = new Gson();
		String inputFilename = args[0];
		ArrayList<Thread> ThreadList;
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFilename));
			//convert the json string back to object. start all the passive objects (in order to make them thread safe singleton, and print them in the end)
			BookStore bookStore = gson.fromJson(br, BookStore.class);
			CountDownLatch count = new CountDownLatch(bookStore.getSum());
			ResourcesHolder RH = ResourcesHolder.getInstance();
			RH.load(bookStore.getVehicles());
			MoneyRegister moneyRegister = MoneyRegister.getInstance();
			Inventory inventory = Inventory.getInstance();
			inventory.load(bookStore.getInitialInventory());
			MessageBusImpl messageBus = MessageBusImpl.getInstance();
			ThreadList = runThreads(bookStore , count);
			//wait until all threads finish in order to print the output.
			joinThreads(ThreadList);
			printToFile(bookStore , moneyRegister , inventory , args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ArrayList<Thread> runThreads(BookStore bookStore , CountDownLatch count) {
		ArrayList<Thread> ThreadList = new ArrayList<Thread>();
		Thread time = new Thread(bookStore.getTime());
		Customer[] customers = bookStore.getCustomers();
		Thread[] sellers = new Thread[bookStore.getSelling()];
		Thread[] logics = new Thread[bookStore.getLogistics()];
		Thread[] resources = new Thread[bookStore.getResourcesService()];
		Thread[] warehouses = new Thread[bookStore.getInventoryService()];
		Thread[] APIs = new Thread[customers.length];

		for (Thread element : sellers) {
			element = new Thread(new SellingService(count));
			ThreadList.add(element);
			element.start();
		}
		for (Thread element : logics) {
			element = new Thread(new LogisticsService(count));
			ThreadList.add(element);
			element.start();
		}
		for (Thread element : resources) {
			element = new Thread(new ResourceService(count));
			ThreadList.add(element);
			element.start();
		}
		for (Thread element : warehouses) {
			element = new Thread(new InventoryService(count));
			ThreadList.add(element);
			element.start();
		}
		for (int i = 0; i < APIs.length; i++) {
			APIs[i] = new Thread(new APIService(customers[i],count));
			ThreadList.add(APIs[i]);
			APIs[i].start();
		}
		try {
			count.await();//to make sure all the microservices finish the initializations.
			time.start();
			ThreadList.add(0, time);
			return ThreadList;
		}catch (Exception e){
			e.printStackTrace();
		}
		return ThreadList;
	}

	private static void joinThreads(ArrayList<Thread> ThreadList) {
		for (Thread element : ThreadList) {
			try {
				element.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
//print the serialized output
	private static void printToFile(BookStore bookStore , MoneyRegister moneyRegister , Inventory inventory , String[] args){
		moneyRegister.printOrderReceipts(args[3]);
		PrintCustomerHash(bookStore, args[1]);
		inventory.printInventoryToFile(args[2]);
		PrintRegister(moneyRegister, args[4]);
	}

	private static void PrintCustomerHash(BookStore bookStore, String filename) {
		HashMap<Integer, Customer> CustomerMap = new HashMap<Integer, Customer>();
		Customer[] customers = bookStore.getCustomers();
		for (Customer element : customers)
			CustomerMap.put(element.getId(), element);
		try
		{
			//Saving of object in a file
			FileOutputStream file = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(file);

			// Method for serialization of object
			out.writeObject(CustomerMap);
			out.close();
			file.close();
		}
		catch(IOException ex)
		{
			System.out.println("IOException is caught");
		}
	}

	private static void PrintRegister(MoneyRegister moneyRegister, String filename) {
		try
		{
			//Saving of object in a file
			FileOutputStream file = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(file);

			// Method for serialization of object
			out.writeObject(moneyRegister);
			out.close();
			file.close();
		}
		catch(IOException ex)
		{
			System.out.println("IOException is caught");
		}

	}
}

    
