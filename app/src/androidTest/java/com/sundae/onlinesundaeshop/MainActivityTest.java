package com.sundae.onlinesundaeshop;

import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {

    private MainActivity mainActivity;
    private Spinner sizeDropdown;
    private Spinner flavorDropdown;
    private SeekBar fudgeAmount;
    private TextView priceText;
    private CheckBox peanuts;
    private AppCompatButton orderButton;

    /** @noinspection DataFlowIssue*/
    @Before
    public void setup() {
        // Mock MainActivity
        mainActivity = new MainActivity();

        // Mock UI elements
        sizeDropdown = new Spinner(null);
        flavorDropdown = new Spinner(null);
        fudgeAmount = new SeekBar(null);
        priceText = new TextView(null);
        peanuts = new CheckBox(null);
        orderButton = new AppCompatButton(null);

        // Mock using default val for UI elements
        fudgeAmount.setProgress(1);
        priceText.setText("Price: $0.50");
    }

    /**
     * Test initial price calculation
     */
    @Test
    public void testInitialPriceCalculation() {
        // Initialize the price to the default amount (should be 0.50f for 1 oz fudge)
        mainActivity.initPrice();

        // Assert that the price is initialized correctly
        assertEquals("Initial price should be $0.50", 0.50f, mainActivity.total, 0.01);
    }

    /**
     * Test onFudgeAmountChanged()
     */
    @Test
    public void testOnFudgeAmountChanged() {
        // Simulate the user changing the fudge amount to 2 ounces
        fudgeAmount.setProgress(2);

        // Call the method to recalculate the price based on fudge amount
        mainActivity.onFudgeAmountChanged();

        // Assert that the price is updated to $1.00 after adding 2 ounces of fudge
        assertEquals("Price should be $1.00 after adding 2 ounces of fudge", 1.00f, mainActivity.total, 0.01);
    }

    /**
     * Test topping checkbox behavior
     */
    @Test
    public void testOnToppingChecked() {
        // Simulate the user checking the peanuts topping
        peanuts.setChecked(true);

        // Call the method to recalculate the price when a topping is checked
        mainActivity.onCheckboxChecked(peanuts, true);

        // Verify that the price is updated after adding the peanuts topping
        assertEquals("Price should increase by 0.15 when peanuts are checked", 0.15f, mainActivity.total, 0.01);
    }

    /**
     * Test onCheckboxUnchecked method
     */
    @Test
    public void testOnToppingUnchecked() {
        // Simulate the user unchecking the peanuts topping
        peanuts.setChecked(false);

        // Call the method to recalculate the price when a topping is unchecked
        mainActivity.onCheckboxChecked(peanuts, false);

        // Verify that the price is reduced after removing peanuts topping
        assertEquals("Price should decrease by 0.15 when peanuts are unchecked", 0.00f, mainActivity.total, 0.01);
    }

    /**
     * Test onOrderClick()
     */
    @Test
    public void testOnOrderClick() {
        // Simulate the user selecting a size, flavor, and price
        sizeDropdown.setSelection(0); // Assume 0 is SMALL
        flavorDropdown.setSelection(1); // Assume 1 is Vanilla
        priceText.setText("Price: $2.99");

        // Simulate a button click that places an order
        mainActivity.onOrderClick(orderButton);
    }

    /**
     * Test onResetClick()
     */
    @Test
    public void testOnResetClick() {
        // Simulate clicking the reset button
        mainActivity.onResetClick(orderButton);

        // Verify that the fudge progress is reset to 1 oz
        assertEquals("Fudge amount should reset to 1oz", MainActivity.HotFudge.ONE_OUNCE.getOunces(), fudgeAmount.getProgress());
        // Verify that the size dropdown resets to Small (assuming SMALL is index 0)
        assertEquals("Size dropdown should reset to Small", 0, sizeDropdown.getSelectedItemPosition());
    }

}
