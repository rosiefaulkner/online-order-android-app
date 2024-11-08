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

    static final String TABLE_NAME = "orders";

    private static final String ID_COL = "id";

    private static final String SIZE_COL = "size";

    private static final String FLAVOR_COL = "flavor";

    private static final String FUDGE_COL = "fudge";

    private static final String CREATED_AT_COL = "created_at";

    private static final String PRICE_COL = "price";

    /**
     * Constructor
     *
     */
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Create orders table
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Set column names and schema in new table
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
        values.put(SIZE_COL, orderSize);
        values.put(FLAVOR_COL, orderFlavor);
        values.put(FUDGE_COL, orderFudge);
        values.put(CREATED_AT_COL, System.currentTimeMillis());
        values.put(PRICE_COL, orderPrice);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Read orders from DB
     */
    public ArrayList<OrderModel> readOrders()
    {
        // Get readable instance of DB
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorOrders
                = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<OrderModel> orderModelArrayList
                = new ArrayList<>();

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
            // Add next order to list
        }
        cursorOrders.close();
        return orderModelArrayList;
    }

    /**
     * Upgrade DB
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}