package bgu.spl.mics.application.passiveObjects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class InventoryTest {
    private Inventory inventory;

    @Before
    public void setUp() throws Exception {
        this.inventory = new Inventory();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance() {
        Inventory test=Inventory.getInstance();
        assertEquals(test,inventory);
    }

    @Test
    public void load() {
        BookInventoryInfo[] test = new BookInventoryInfo[3];
        BookInventoryInfo book1=new BookInventoryInfo("a",3,50);
        test[0]=book1;
        inventory.load(test);
        assertEquals(inventory.checkAvailabiltyAndGetPrice("a"),50);



    }

    @Test
    public void take() {
        BookInventoryInfo[] test = new BookInventoryInfo[3];
        BookInventoryInfo book1=new BookInventoryInfo("a",3,50);
        test[0]=book1;
        inventory.load(test);
        OrderResult result=inventory.take("a");
        assertEquals(result,"SUCCESSFULLY_TAKEN");
        result=inventory.take("a");
        assertEquals(result,"SUCCESSFULLY_TAKEN");
        result=inventory.take("a");
        assertEquals(result,"SUCCESSFULLY_TAKEN");
        result=inventory.take("a");
        assertEquals(result,"NOT_IN_STOCK");
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        BookInventoryInfo[] test = new BookInventoryInfo[3];
        BookInventoryInfo book1=new BookInventoryInfo("a",3,50);
        test[0]=book1;
        inventory.load(test);
        int price=inventory.checkAvailabiltyAndGetPrice("a");
        assertEquals(price,50);
        int price2=inventory.checkAvailabiltyAndGetPrice("b");
        assertEquals(price2,-1);
    }
}