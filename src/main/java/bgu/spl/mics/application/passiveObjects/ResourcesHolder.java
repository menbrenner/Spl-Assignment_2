package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.ConcurrentLinkedQueue;
import bgu.spl.mics.Future;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	
    // ---------------------- fields ----------------------
    private static ResourcesHolder instance = null;
    private ConcurrentLinkedQueue<DeliveryVehicle> VehicleCollection;// collection of vehicles in disposle.
    private ConcurrentLinkedQueue<Future <DeliveryVehicle>> unresolvedVehicles;// these vehicles will be resolved when a vehicle arrives.
	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
       if(instance == null) {
          instance = new ResourcesHolder();
       }
       return instance;
    }
	/**
     * Private constructor initializes fields as necessary.
     */
    private ResourcesHolder() {
		this.unresolvedVehicles = new ConcurrentLinkedQueue<>();
		this.VehicleCollection = new ConcurrentLinkedQueue<>();
	}
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public synchronized Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> futureVehicle=new Future<>();
		DeliveryVehicle potentialVehicle = VehicleCollection.poll();
		if(potentialVehicle != null){//if there is a vehicle in collection, resolve the future. 
			futureVehicle.resolve(potentialVehicle);
		}else{//if not, save it in the unresolved list to be resolved further on0
			unresolvedVehicles.add(futureVehicle);
		}
		return futureVehicle;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public synchronized void releaseVehicle(DeliveryVehicle vehicle) {
		Future <DeliveryVehicle> nextInLine = unresolvedVehicles.poll();
			if (nextInLine == null) {// if no unresolved vehicles, push it back to the collection
				VehicleCollection.add(vehicle);
			} else {//if there are, resolve one
				nextInLine.resolve(vehicle);
			}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	
	public void load(DeliveryVehicle[] vehicles) {
		for(int i=0;i<vehicles.length;i++)
		{
			VehicleCollection.add(vehicles[i]);
		}
	}

}
