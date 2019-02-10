package bgu.spl.mics.application.passiveObjects;

import org.junit.Test;

import static bgu.spl.mics.application.passiveObjects.OrderResult.SUCCESSFULLY_TAKEN;
import static org.junit.Assert.*;

public class InventoryTest {
    Inventory l = Inventory.getInstance();
    @Test
    public void getInstance() throws Exception {
        Inventory l = Inventory.getInstance();
        Inventory s = Inventory.getInstance();
        assertEquals(l,s);
    }

    @Test
    public void load() throws Exception {
        BookInventoryInfo [] test = new BookInventoryInfo[15];
        for (int i = 0 ; i < test.length; i++){
            test[i] = new BookInventoryInfo(i+1,Integer.toString(i),i+1);
        }
        l.load(test);
        for(int i = 0 ; i < test.length; i++){
            assertEquals(i+1, l.checkAvailabiltyAndGetPrice(Integer.toString(i)));
        }
    }

    @Test
    public void take() throws Exception {
        assertEquals(-1,l.checkAvailabiltyAndGetPrice("a"));
        BookInventoryInfo[] invetory = new BookInventoryInfo[1];
        invetory[0] = new BookInventoryInfo(1,"i",1);
        l.load(invetory);
        assertEquals(SUCCESSFULLY_TAKEN,l.take("i"));
        assertNotEquals(SUCCESSFULLY_TAKEN,l.take("i"));
    }

    @Test
    public void checkAvailabiltyAndGetPrice() throws Exception {
        assertEquals(-1,l.checkAvailabiltyAndGetPrice("a"));
        BookInventoryInfo[] invetory = new BookInventoryInfo[1];
        invetory[0] = new BookInventoryInfo(1,"i",1);
        l.load(invetory);
        assertEquals(1,l.checkAvailabiltyAndGetPrice("i"));
        assertEquals(SUCCESSFULLY_TAKEN,l.take("i"));
        assertEquals(-1,l.checkAvailabiltyAndGetPrice("i"));

    }

    @Test
    public void printInventoryToFile() throws Exception {

    }

}