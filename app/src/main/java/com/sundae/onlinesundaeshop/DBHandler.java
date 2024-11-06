package com.sundae.onlinesundaeshop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "franksSundaesDB";

    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "orders";

    private static final String ID_COL = "id";

    private static final String SIZE_COL = "size";

    private static final String FLAVOR_COL = "flavor";

    private static final String FUDGE_COL = "fudge";

    private static final String CREATED_AT_COL = "created_at";

    private static final String PRICE_COL = "price";

    // Constructor
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Create orders table
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Set column names and schema
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SIZE_COL + " TEXT,"
                + FLAVOR_COL + " TEXT,"
                + FUDGE_COL + " TEXT,"
                + CREATED_AT_COL + " int,"
                + PRICE_COL + " TEXT)";
        db.execSQL(query);
    }

    /**
     * Add new order to DB
     */
    public void addNewOrder(String orderSize, String orderFlavor, String orderFudge, String orderPrice) {

        // Call DB handler writable method
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        // Pass values to content values
        values.put(SIZE_COL, orderSize);
        values.put(FLAVOR_COL, orderFlavor);
        values.put(FUDGE_COL, orderFudge);
        values.put(CREATED_AT_COL, System.currentTimeMillis());
        values.put(PRICE_COL, orderPrice);

        // Pass content values to DB table
        db.insert(TABLE_NAME, null, values);

        // Close DB connection
        db.close();
    }

    /**
     * Read orders from DB
     */
    public ArrayList<OrderModel> readOrders()
    {
        // Open DB for reading
        SQLiteDatabase db = this.getReadableDatabase();

        // Create cursor to read DB
        Cursor cursorOrders
                = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        // New array list
        ArrayList<OrderModel> orderModelArrayList
                = new ArrayList<>();

        // Move cursor to first order
        if (cursorOrders.moveToFirst()) {
            do {
                // Add cursor data to list
                orderModelArrayList.add(new OrderModel(
                        cursorOrders.getInt(0),
                        cursorOrders.getString(1),
                        cursorOrders.getString(2),
                        cursorOrders.getString(3),
                        cursorOrders.getString(4),
                        cursorOrders.getString(5)));
            } while (cursorOrders.moveToNext());
            // Move cursor to next order.
        }
        // Close cursor and return array list of orders
        cursorOrders.close();
        return orderModelArrayList;
    }

    /**
     * Upgrade DB
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Check if the table already exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}