package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.services.TimeService;

import java.util.Collection;
/**
 we followed the pattern we saw at the json file, that why we build all this methods and fields.
 */

public class BookStore {
    private BookInventoryInfo [] initialInventory;
    private Resource [] initialResources;
    private Services services;

    public BookStore() {
    }

    public BookInventoryInfo[] getInitialInventory() {
        return initialInventory;
    }

    public DeliveryVehicle[] getVehicles() {
        return initialResources[0].vehicles;
    }

    public Services getServices() {
        return services;
    }

    public TimeService getTime() {
        return getServices().time;
    }

        public int getSelling() {
        return getServices().selling;
    }

    public int getInventoryService() {
        return getServices().inventoryService;
    }

    public int getLogistics() {
        return getServices().logistics;
    }

    public int getResourcesService() {
        return getServices().resourcesService;
    }

    public Customer[] getCustomers() {
        return getServices().customers;
    }

    public int getSum(){//sum of all microservices, in order to notify to countDownLatch later that everyone beside time finish their initialization, and time can start.
        return services.selling + services.inventoryService + services.logistics + services.resourcesService + services.customers.length;
    }
    private class Services{
    	private TimeService time;
    	private int selling;
    	private int inventoryService;
    	private int logistics;
    	private int resourcesService;
    	private Customer [] customers;;

    }
    private class Resource{
        private DeliveryVehicle[] vehicles;

    }
}
