package com.sundae.onlinesundaeshop;

import android.annotation.SuppressLint;
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

public class MainActivity extends AppCompatActivity {
    SeekBar fudgeAmount;
    Spinner flavorDropdown, sizeDropdown;
    TextView description, fudgeProgressText, priceText;
    Button orderButton, theWorksButton, resetButton;
    CheckBox peanuts, mAndMs, almonds, brownie, strawberries, oreos, gummyBears, marshmallows;
    Float total;
    private CheckBox[] toppings;
    private DBHandler dbHandler;
    private final String addAction = "add";
    private final String subtractAction = "subtract";

    final int[] previousProgress = {1};
    final int[] previousSize = {0};

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Initialize views
        initWidgets();

        // Initialize database handler
        dbHandler = new DBHandler(MainActivity.this);
        // Size dropdown listener
        onSizeDropdownSelect();

        // Flavor dropdown listener
        onFlavorDropdownSelect();
    }

    /**
     * Enum for storing size prices
     */
    public enum Size {
        SMALL(2.99f),
        MEDIUM(3.99f),
        LARGE(4.99f);

        private final float price;

        Size(float price) {
            this.price = price;
        }

        public float getPrice() {
            return price;
        }
    }

    /**
     * Enum for storing hot fudge amount prices
     * Throws an exception if no matching enum constant is found.
     */
    public enum HotFudge {
        ZERO_OUNCES(0, 0.00f),
        ONE_OUNCE(1, 0.50f),
        TWO_OUNCES(2, 1.00f),
        THREE_OUNCES(3, 1.50f);

        private final int ounces;
        private final float price;

        HotFudge(int ounces, float price) {
            this.ounces = ounces;
            this.price = price;
        }

        public int getOunces() {
            return ounces;
        }

        public float getPrice() {
            return price;
        }

        // Get HotFudge enum based on selected ounce(s)
        public static HotFudge fromOunces(int ounces) {
            for (HotFudge fudgeAmount : HotFudge.values()) {
                if (fudgeAmount.getOunces() == ounces) {
                    return fudgeAmount;
                }
            }
            // If no match is found, throw an exception
            throw new IllegalArgumentException("No HotFudge enum constant with " + ounces + " ounces");
        }
    }

    /**
     * Enum for storing topping prices.
     */
    public enum Topping {
        PEANUTS(0.15f),
        M_AND_MS(0.25f),
        ALMONDS(0.15f),
        BROWNIE(0.20f),
        STRAWBERRIES(0.20f),
        OREOS(0.20f),
        GUMMY_BEARS(0.20f),
        MARSHMALLOWS(0.15f);

        private final Float price;

        Topping(Float price) {
            this.price = price;
        }

        public Float getPrice() {
            return price;
        }
    }

    /**
     * Initializes widgets
     */
    private void initWidgets() {
        // Initialize Fudge slider and set listener
        initFudgeSlider();

        // Initialize flavor/size dropdowns
        initFlavorDropdown();
        initSizeDropdown();

        // Initialize description text
        description = findViewById(R.id.description);

        // Initialize buttons
        initButtons();

        // Initialize toppings checkboxes and set listeners
        initToppings();

        // Initialize price
        initPrice();
    }

    /**
     * Initializes hot fudge slider and sets listener
     */
    private void initFudgeSlider() {
        fudgeAmount = findViewById(R.id.amount_fudge);
        fudgeAmount.setProgress(HotFudge.ONE_OUNCE.ounces);
        fudgeAmount.setOnSeekBarChangeListener(onFudgeAmountChanged());

        fudgeProgressText = findViewById(R.id.fudge_progress_text);
        fudgeProgressText.setText(getString(R.string.progress_1oz));
    }

    /**
     * Initializes flavor dropdown
     */
    private void initFlavorDropdown() {
        flavorDropdown = findViewById(R.id.flavor);
    }

    /**
     * Initializes size dropdown
     */
    private void initSizeDropdown() {
        sizeDropdown = findViewById(R.id.size);
    }

    /**
     * Initializes buttons and sets respective listeners
     */
    private void initButtons() {
        // Order button
        orderButton = findViewById(R.id.order);
        orderButton.setOnClickListener(this::onOrderClick);
        // The Works! button
        theWorksButton = findViewById(R.id.the_works);
        theWorksButton.setOnClickListener(this::onTheWorksClick);
        // Reset button
        resetButton = findViewById(R.id.reset);
        resetButton.setOnClickListener(this::onResetClick);
    }

    /**
     * Initializes toppings and set listener
     */
    private void initToppings() {
        peanuts = findViewById(R.id.peanuts);
        mAndMs = findViewById(R.id.m_and_ms);
        almonds = findViewById(R.id.almonds);
        brownie = findViewById(R.id.brownie);
        strawberries = findViewById(R.id.strawberries);
        oreos = findViewById(R.id.oreos);
        gummyBears = findViewById(R.id.gummy_bears);
        marshmallows = findViewById(R.id.marshmallows);

        // Toppings listener is prettier in a loop
        toppings = new CheckBox[]{peanuts, mAndMs, almonds, brownie, strawberries, oreos, gummyBears, marshmallows};
        for (CheckBox topping : toppings) {
            topping.setOnCheckedChangeListener(this::onCheckboxChecked);
        }
    }

    /**
     * Initializes total price with default 1 oz hot fudge set
     */
    @SuppressLint("DefaultLocale")
    protected void initPrice() {
        total = HotFudge.ONE_OUNCE.getPrice();
        priceText = findViewById(R.id.price_label_text);
        priceText.setText(String.format("%s $%.2f", getString(R.string.price_label), total));
    }

    /**
     * Resets the form to its default state.
     *
     * @param v The view that was clicked.
     */
    protected void onResetClick(View v) {
        // Reset fudge amount to 1 oz & update corresponding text
        fudgeProgressText.setText(String.format("%s", getString(R.string.progress_1oz)));
        fudgeAmount.setProgress(HotFudge.ONE_OUNCE.ounces);
        // Reset size to small
        sizeDropdown.setSelection(Size.SMALL.ordinal());
        // Reset flavor to vanilla
        flavorDropdown.setSelection(0);
        for (CheckBox topping : toppings) {
            this.toggleCheckbox(false, topping);
        }
    }

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
     *
     * Throws exception if no matching enum constant is found.
     */
    protected void onCheckboxChecked(CompoundButton topping, boolean checked) {
        int toppingID = topping.getId();
        String toppingResourceName = getResources().getResourceEntryName(toppingID).toUpperCase();

        try {
            Topping selectedTopping = Topping.valueOf(toppingResourceName);
            Float price = selectedTopping.getPrice();
            // If the checkbox is checked, add the topping price to the total, else subtract it
            if (checked) {
                calculatePrice(price, addAction);
            } else {
                calculatePrice(price, subtractAction);
            }
        } catch (IllegalArgumentException e) {
            // Throw exception if no matching topping is found
            throw new IllegalArgumentException("No topping enum constant for " + toppingResourceName);
        }
    }

    /**
     * Calculates the price of the order.
     *
     * @param priceChange The change in price.
     * @param action The action to be performed.
     */
    @SuppressLint("DefaultLocale")
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
        fudgeAmount.setProgress(HotFudge.THREE_OUNCES.ounces);
        sizeDropdown.setSelection(Size.LARGE.ordinal());
        flavorDropdown.setSelection(0);
        for (CheckBox topping : toppings) {
            this.toggleCheckbox(true, topping);
        }
    }

    /**
     * Handles changes in the fudge amount.
     *
     * @return {@link SeekBar.OnSeekBarChangeListener}
     */
    protected SeekBar.OnSeekBarChangeListener onFudgeAmountChanged() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fudgeProgressText.setText(String.format("%s %s", progress, getString(R.string.oz)));
                HotFudge selectedFudge = HotFudge.fromOunces(progress);
                float fudgePrice = selectedFudge.getPrice();
                calculatePrice(fudgePrice, addAction);

                // Subtract previous selected progress price
                if (previousProgress[0] != progress) {
                    HotFudge previousFudge = HotFudge.fromOunces(previousProgress[0]);
                    float previousFudgePrice = previousFudge.getPrice();
                    calculatePrice(previousFudgePrice, subtractAction);
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
     * Size dropdown handler. Increments/decrements total price based on selected size.
     */
    private void onSizeDropdownSelect() {
        sizeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSizeText = parent.getItemAtPosition(position).toString().toUpperCase();
                Size selectedSize = Size.valueOf(selectedSizeText);
                float sizePrice = selectedSize.getPrice();
                calculatePrice(sizePrice, addAction);

                // Subtract previous size price
                int prevSizePosition = previousSize[0];
                if (prevSizePosition != position) {
                    String previousSizeText = parent.getItemAtPosition(prevSizePosition).toString().toUpperCase();
                    Size previousSizeEnum = Size.valueOf(previousSizeText);
                    float previousSizePrice = previousSizeEnum.getPrice();
                    calculatePrice(previousSizePrice, subtractAction);
                }

                // Set prevSize to current position
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
    protected void onOrderClick(View v) {
        // Get the selected values from the order form
        String orderSize = sizeDropdown.getSelectedItem().toString();
        String orderFlavor = flavorDropdown.getSelectedItem().toString();
        String orderFudge = fudgeProgressText.getText().toString();
        @SuppressLint("DefaultLocale") String orderPrice = String.format("$%.2f", total);

        // Validate size and flavor inputs
        if (!validateSpinners(orderSize, orderFlavor)) {
            return;
        }

        // Add order to database
        dbHandler.addNewOrder(orderSize, orderFlavor, orderFudge, orderPrice);

        // Reset the form
        Toast.makeText(MainActivity.this, getString(R.string.order_successfully_placed), Toast.LENGTH_SHORT).show();
        this.onResetClick(v);
    }

    /**
     * Validates the size and flavor inputs as well as the price. Provides a warning to the user
     * and prevents the order from being placed if the inputs are invalid. Provides light safeguard
     * against SQL injection.
     *
     * @param orderSize The selected size.
     * @param orderFlavor The selected flavor.
     *
     * @return {@code true} if the inputs are valid, {@code false} otherwise.
     */
    private boolean validateSpinners(String orderSize, String orderFlavor) {
        boolean isValidSize = false;
        if (orderSize.isEmpty()) {
            Toast.makeText(MainActivity.this, getString(R.string.please_enter_a_size), Toast.LENGTH_SHORT).show();
        }
        String[] sizes = getResources().getStringArray(R.array.size_names);
        for (String size : sizes) {
            if (size.equalsIgnoreCase(orderSize)) {
                isValidSize = true;
                break;
            }
        }
        String[] flavors = getResources().getStringArray(R.array.flavor_names);
        boolean isValidFlavor = false;
        if (orderFlavor.isEmpty()) {
            Toast.makeText(MainActivity.this, getString(R.string.please_enter_a_flavor), Toast.LENGTH_SHORT).show();
        }
        for (String flavor : flavors) {
            if (flavor.equalsIgnoreCase(orderFlavor)) {
                isValidFlavor = true;
                break;
            }
        }
        boolean isValidPrice = false;
        if (total >= Size.SMALL.getPrice()) {
            isValidPrice = true;
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.please_make_selection), Toast.LENGTH_SHORT).show();
        }
        return isValidSize && isValidFlavor && isValidPrice;
    }
}