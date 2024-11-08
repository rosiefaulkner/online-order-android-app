package com.sundae.onlinesundaeshop;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

public class DBHandlerTest {

    private DBHandler dbHandler;

    @Before
    public void setup() {
        // Get the application context (works in Android instrumented tests)
        Context context = ApplicationProvider.getApplicationContext();

        // Create an instance of DBHandler
        dbHandler = new DBHandler(context);

        // Clear out any pre-existing data (optional step to ensure test consistency)
        clearDatabase();
    }

    private void clearDatabase() {
        // Clear the orders table before each test (helps ensure a clean slate for tests)
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.execSQL("DELETE FROM " + DBHandler.TABLE_NAME);
        db.close();
    }

    @Test
    public void testAddNewOrder() {
        // Insert a new order
        String orderSize = "Medium";
        String orderFlavor = "Vanilla";
        String orderFudge = "Chocolate Fudge";
        String orderPrice = "$5.00";

        dbHandler.addNewOrder(orderSize, orderFlavor, orderFudge, orderPrice);

        // Verify if the order has been added
        ArrayList<OrderModel> orders = dbHandler.readOrders();

        assertEquals("There should be one order in the database", 1, orders.size());

        OrderModel insertedOrder = orders.get(0);
        assertNotNull("Inserted order should not be null", insertedOrder);
        assertEquals("Order size should match", orderSize, insertedOrder.getSize());
        assertEquals("Order flavor should match", orderFlavor, insertedOrder.getFlavor());
        assertEquals("Order fudge should match", orderFudge, insertedOrder.getFudge());
        assertEquals("Order price should match", orderPrice, insertedOrder.getPrice());
    }

    @Test
    public void testReadOrders() {
        // Insert some orders
        dbHandler.addNewOrder("Small", "Chocolate", "No Fudge", "$3.00");
        dbHandler.addNewOrder("Large", "Strawberry", "Caramel Fudge", "$6.00");

        // Read orders from the database
        ArrayList<OrderModel> orders = dbHandler.readOrders();

        assertEquals("There should be two orders in the database", 2, orders.size());

        OrderModel firstOrder = orders.get(0);
        assertNotNull("First order should not be null", firstOrder);
        assertEquals("First order size should be 'Small'", "Small", firstOrder.getSize());

        OrderModel secondOrder = orders.get(1);
        assertNotNull("Second order should not be null", secondOrder);
        assertEquals("Second order size should be 'Large'", "Large", secondOrder.getSize());
    }
}
