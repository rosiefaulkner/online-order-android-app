package com.sundae.onlinesundaeshop;

import static java.lang.String.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    SeekBar fudgeAmount;
    Spinner flavorDropdown, sizeDropdown;
    TextView description, fudgeProgressText, priceText;
    Button orderButton, theWorksButton, resetButton;
    CheckBox peanuts, mAndMs, almonds, brownie, strawberries, oreos, gummyBears, marshmallows;
    Float total;
    private CheckBox[] toppings;
    private DBHandler dbHandler;
    private Map<String, Float> sizePrices, toppingPrices, hotFudgePrices;
    private String addAction = "add";
    private String subtractAction = "subtract";
    private String small = "small";
    private String medium = "medium";
    private String large = "large";
    private String peanutsTopping = "peanuts";
    private String mAndMsTopping = "m_and_ms";
    private String almondsTopping = "almonds";
    private String brownieTopping = "brownie";
    private String strawberriesTopping = "strawberries";
    private String oreosTopping = "oreos";
    private String gummyBearsTopping = "gummy_bears";
    private String marshmallowsTopping = "marshmallows";
    private String zeroOunce = "0";
    private String oneOunce = "1";
    private String twoOunces = "2";
    private String threeOunces = "3";
    private Float peanutPrice = 0.15f;
    private Float mAndMsPrice = 0.25f;
    private Float almondsPrice = 0.15f;
    private Float browniePrice = 0.20f;
    private Float strawberriesPrice = 0.20f;
    private Float oreosPrice = 0.20f;
    private Float gummyBearsPrice = 0.20f;
    private Float marshmallowsPrice = 0.15f;
    private Float hotFudgePriceZeroOunce = 0.00f;
    private Float hotFudgePriceOneOunce = 0.15f;
    private Float hotFudgePriceTwoOunces = 0.25f;
    private Float hotFudgePriceThreeOunces = 0.30f;
    private Float smallPrice = 2.99f;
    private Float mediumPrice = 3.99f;
    private Float largePrice = 4.99f;
    final int[] previousProgress = {1};
    final int[] previousSize = {0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Initialize views
        initWidgets();

        // Initialize pricing hashmaps
        setPricing();

        // Initialize database handler
        dbHandler = new DBHandler(MainActivity.this);
        // Size dropdown listener
        onSizeDropdownSelect();

        // Flavor dropdown listener
        onFlavorDropdownSelect();

        // Initialize price
        total = 0.15f;
        priceText.setText(String.format("%s $%.2f", getString(R.string.price_label), total));
    }

    /**
     * Initializes widgets
     */
    private void initWidgets()
    {
        // Fudge slider and listener
        fudgeAmount = findViewById(R.id.amount_fudge);
        fudgeAmount.setProgress(1);
        fudgeAmount.setOnSeekBarChangeListener(onFudgeAmountChanged());
        fudgeProgressText = findViewById(R.id.fudge_progress_text);
        fudgeProgressText.setText(getString(R.string.progress_1oz));
        // Flavor dropdown
        flavorDropdown = findViewById(R.id.flavor);
        // Size dropdown
        sizeDropdown = findViewById(R.id.size);
        // Instructions for user
        description = findViewById(R.id.description);
        // Order button listener
        orderButton = findViewById(R.id.order);
        orderButton.setOnClickListener(this::onOrderClick);
        // The Works button listener
        theWorksButton = findViewById(R.id.the_works);
        theWorksButton.setOnClickListener(this::onTheWorksClick);
        // Reset button listener
        resetButton = findViewById(R.id.reset);
        resetButton.setOnClickListener(this::onResetClick);
        // Toppings checkboxes
        peanuts = findViewById(R.id.peanuts);
        mAndMs = findViewById(R.id.m_and_ms);
        almonds = findViewById(R.id.almonds);
        brownie = findViewById(R.id.brownie);
        strawberries = findViewById(R.id.strawberries);
        oreos = findViewById(R.id.oreos);
        gummyBears = findViewById(R.id.gummy_bears);
        marshmallows = findViewById(R.id.marshmallows);
        // Toppings onclick listener
        toppings = new CheckBox[]{peanuts, mAndMs, almonds, brownie,
                strawberries, oreos, gummyBears, marshmallows
        };
        peanuts.setOnCheckedChangeListener(this::onCheckboxChecked);
        mAndMs.setOnCheckedChangeListener(this::onCheckboxChecked);
        almonds.setOnCheckedChangeListener(this::onCheckboxChecked);
        brownie.setOnCheckedChangeListener(this::onCheckboxChecked);
        strawberries.setOnCheckedChangeListener(this::onCheckboxChecked);
        oreos.setOnCheckedChangeListener(this::onCheckboxChecked);
        gummyBears.setOnCheckedChangeListener(this::onCheckboxChecked);
        marshmallows.setOnCheckedChangeListener(this::onCheckboxChecked);
        // Price text
        priceText = findViewById(R.id.price_label_text);
    }


    /**
     * Initializes the pricing hashmaps.
     */
    private void setPricing() {
        // sizePrices map
        sizePrices = new HashMap<>();
        sizePrices.put(small, smallPrice);
        sizePrices.put(medium, mediumPrice);
        sizePrices.put(large, largePrice);

        // toppingPrices map
        toppingPrices = new HashMap<>();
        toppingPrices.put(peanutsTopping, peanutPrice);
        toppingPrices.put(mAndMsTopping, mAndMsPrice);
        toppingPrices.put(almondsTopping, almondsPrice);
        toppingPrices.put(brownieTopping, browniePrice);
        toppingPrices.put(strawberriesTopping, strawberriesPrice);
        toppingPrices.put(oreosTopping, oreosPrice);
        toppingPrices.put(gummyBearsTopping, gummyBearsPrice);
        toppingPrices.put(marshmallowsTopping, marshmallowsPrice);

        // hotFudgePrices map
        hotFudgePrices = new HashMap<>();
        hotFudgePrices.put(zeroOunce, hotFudgePriceZeroOunce);
        hotFudgePrices.put(oneOunce, hotFudgePriceOneOunce);
        hotFudgePrices.put(twoOunces, hotFudgePriceTwoOunces);
        hotFudgePrices.put(threeOunces, hotFudgePriceThreeOunces);
    }

    /**
     * Resets the form to its default state.
     *
     * @param v The view that was clicked.
     */
    private void onResetClick(View v) {
        fudgeProgressText.setText(String.format("%s", getString(R.string.progress_1oz)));
        fudgeAmount.setProgress(1);
        sizeDropdown.setSelection(0);
        flavorDropdown.setSelection(0);
        for (CheckBox topping : toppings) {
            this.toggleCheckbox(false, topping);
        }
    };

    /**
     * Toggles the checked state of a checkbox.
     *
     * @param checked The new checked state of the checkbox.
     * @param c The checkbox to be toggled.
     */
    private void toggleCheckbox(boolean checked, CheckBox c) {
        c.setChecked(checked);
    }

    /**
     * Handles the checked state of a checkbox.
     *
     * @param topping The checkbox that was checked or unchecked.
     * @param checked The new checked state of the checkbox.
     */
    private void onCheckboxChecked(CompoundButton topping, boolean checked) {
        int toppingID = topping.getId();

        String toppingResourceName = getResources().getResourceEntryName(toppingID).toLowerCase();
        Float price = toppingPrices.get(toppingResourceName);
        if (topping.isChecked()) {
            calculatePrice(price, addAction);
        } else {
            calculatePrice(price, subtractAction);
        }
    }

    /**
     * Calculates the price of the order.
     *
     * @param priceChange The change in price.
     * @param action The action to be performed.
     */
    private void calculatePrice(Float priceChange, String action) {
        if (action.equals(addAction)) {
            total += priceChange;
        } else if (action.equals(subtractAction)) {
            total -= priceChange;
        }
        priceText.setText(String.format("%s $%.2f", getString(R.string.price_label), total));
    }

    /**
     * The user can click the button labeled,
     * 'The Works!' and they will get a large
     * sundae with everything on it
     *
     * @param v The view that was clicked.
     */
    private void onTheWorksClick(View v) {
        fudgeAmount.setProgress(3);
        sizeDropdown.setSelection(2);
        flavorDropdown.setSelection(0);
        for (CheckBox topping : toppings) {
            this.toggleCheckbox(true, topping);
        }
    };

    /**
     * Handles changes in the fudge amount.
     *
     * @return {@link SeekBar.OnSeekBarChangeListener}
     */
    private SeekBar.OnSeekBarChangeListener onFudgeAmountChanged() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fudgeProgressText.setText(String.format("%s %s", valueOf(progress), getString(R.string.oz)));
                Float fudgePrice = hotFudgePrices.get(valueOf(progress));
                calculatePrice(fudgePrice, addAction);
                // Subtract previous selected progress price
                if (previousProgress[0] != progress) {
                    Float previousProgressAmount = hotFudgePrices.get(valueOf(previousProgress[0]));
                    calculatePrice(previousProgressAmount, subtractAction);
                }

                // Set previousProgress to current
                previousProgress[0] = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };
    }

    /**
     * Size dropdown handler
     */
    private void onSizeDropdownSelect() {
        sizeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSize = parent.getItemAtPosition(position).toString().toLowerCase();
                Float sizePrice = sizePrices.get(selectedSize);
                calculatePrice(sizePrice, addAction);
                // Subtract previous size price
                int prev = previousSize[0];
                if (prev != position) {
                    String previousSizeText = parent.getItemAtPosition(previousSize[0]).toString().toLowerCase();
                    Float previousSizeAmount = sizePrices.get(previousSizeText);
                    calculatePrice(previousSizeAmount, subtractAction);
                }

                // Set previousSize to current
                previousSize[0] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not needed
            }
        });
    }

    /**
     * Flavor dropdown handler
     */
    private void onFlavorDropdownSelect() {
        flavorDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Not needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not needed
            }
        });
    }

    /**
     * Creates the options menu for the activity.
     *
     * @param menu The menu to be created.
     *
     * @return {@code true} if the menu was created, {@code false} otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Handles item selections in the navigation (options) menu. This method is called when the user
     * selects an item from the options menu. It identifies the selected item by its ID and performs
     * the corresponding action, such as starting an activity, i.e. navigating to a page in the
     * user's perspective.
     *
     * @param item The selected menu item.
     *
     * @return {@code true} if the item selection was handled, {@code false} otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            intent.putExtra(getString(R.string.about_text_title), getString(R.string.about_text_body));
            startActivity(intent);
            return true;
        } else if (id == R.id.order_history) {
            Intent intent = new Intent(MainActivity.this, OrderHistoryActivity.class);

            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles the click event of the "Order" button.
     *
     * @param v The view that was clicked.
     */
    private void onOrderClick(View v) {
        // Get the selected values from the order form
        String orderSize = sizeDropdown.getSelectedItem().toString();
        String orderFlavor = flavorDropdown.getSelectedItem().toString();
        String orderFudge = fudgeProgressText.getText().toString();
        String orderPrice = String.format("$%.2f", total);

        // Validate size and flavor inputs
        if (orderSize.isEmpty()) {
            Toast.makeText(MainActivity.this, getString(R.string.please_enter_a_size), Toast.LENGTH_SHORT).show();
            return;
        }

        if (orderFlavor.isEmpty()) {
            Toast.makeText(MainActivity.this, getString(R.string.please_enter_a_flavor), Toast.LENGTH_SHORT).show();
            return;
        }

        // Add order to database
        dbHandler.addNewOrder(orderSize, orderFlavor, orderFudge, orderPrice);

        // Reset the form
        Toast.makeText(MainActivity.this, getString(R.string.order_successfully_placed), Toast.LENGTH_SHORT).show();
        this.onResetClick(v);
    }
}