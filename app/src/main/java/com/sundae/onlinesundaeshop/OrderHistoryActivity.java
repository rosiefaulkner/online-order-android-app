package com.sundae.onlinesundaeshop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** @noinspection resource*/
public class OrderHistoryActivity extends AppCompatActivity {
    TextView orderHistoryTextView;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.flavor), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize order page UI elements / widgets
        initWidgets();

        // initialize variables
        ArrayList<OrderModel> orderModalArrayList;
        DBHandler dbHandler = new DBHandler(this);

        // Get orders list from db handler
        orderModalArrayList = dbHandler.readOrders();

        // If orders list is empty, set text to "No orders found"
        if (orderModalArrayList.isEmpty()) {
            orderHistoryTextView.setText(R.string.no_orders_found);
        }

        List<String> result = extractStrings(orderModalArrayList);
        listView = findViewById(R.id.order_history_list_body);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, result);
        listView.setAdapter(adapter);
    }


    /**
     * Initialize widgets
     */
    private void initWidgets() {
        // Set page title
        setTitle(getString(R.string.order_history));

        // Get the order history data from the intent
        orderHistoryTextView = findViewById(R.id.order_history_text);
        Intent intent = getIntent();
        String body = intent.getStringExtra(getString(R.string.order_history));
        orderHistoryTextView.setText(body);
    }

    /**
     * Extract the strings from the order model in a list of formatted string values
     *
     * @param orderModelArrayList The order model array list
     *
     * @return The list of strings
     */
    public List<String> extractStrings(ArrayList<OrderModel> orderModelArrayList) {
        List<String> extractedStrings = new ArrayList<>();

        for (OrderModel order : orderModelArrayList) {
            String orderTitle = getString(R.string.order_history_item_title) +
                    order.getId().toString() +
                    getString(R.string.order_history_item_title_ending);

            String orderDetails = "\n" +
                    orderTitle +
                    "\n" +
                    getString(R.string.size_label) +
                    order.getSize() +
                    "\n" +
                    getString(R.string.flavor_label) +
                    order.getFlavor() +
                    "\n" +
                    getString(R.string.fudge_label) +
                    order.getFudge() +
                    "\n" +
                    getString(R.string.date_label) +
                    convertToReadableDate(order.getCreatedAt()) +
                    "\n" +
                    getString(R.string.price_label) +
                    order.getPrice() +
                    "\n";

            extractedStrings.add(orderDetails);
        }

        return extractedStrings;
    }


    /**
     * Convert the epoch time to a readable date
     *
     * @param timestampStr The epoch time
     *
     * @return The readable date
     */
    public static String convertToReadableDate(String timestampStr) {
        try {
            long timestampMillis = Long.parseLong(timestampStr);
            Date date = new Date(timestampMillis);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy @ h:mm a");
            return formatter.format(date);
        } catch (NumberFormatException e) {
            // The timestamp string is not a valid long
            return "Invalid Date";
        }
    }
}