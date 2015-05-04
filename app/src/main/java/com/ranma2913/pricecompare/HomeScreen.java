package com.ranma2913.pricecompare;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@EActivity(R.layout.activity_home_screen)
public class HomeScreen extends Activity {

    @ViewById(R.id.homeScreenWelcomeMessage)
    TextView homeScreenWelcomeMessage;

    @ViewById(R.id.itemDescriptionInput)
    EditText itemDescriptionInput;

    @ViewById(R.id.itemPriceInput)
    EditText itemPriceInput;

    @ViewById(R.id.numberOfUnitsInput)
    EditText numberOfUnitsInput;

    @ViewById(R.id.typeOfUnitsInput)
    EditText typeOfUnitsInput;

    @SystemService
    InputMethodManager inputManager;
    View.OnKeyListener typeOfUnitsInputKeyListener = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                calculatePrice();//match this behavior to your 'Send' (or Confirm) button
                return true;
            }
            return false;
        }
    };

    @Click
    void calculatePriceButton() {
        calculatePrice();
    }

    private void calculatePrice() {
        BigDecimal pricePerUnit = new BigDecimal(itemPriceInput.getText().toString().replace("$", "")).divide(new BigDecimal(numberOfUnitsInput.getText().toString()), MathContext.DECIMAL128);
        String outputString = itemDescriptionInput.getText().toString()
                + " $" + new BigDecimal(pricePerUnit.toString()).setScale(4, RoundingMode.HALF_UP).doubleValue()
                + " Price Per Unit";
        homeScreenWelcomeMessage.setText(outputString);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        itemPriceInput.addTextChangedListener(new MoneyTextWatcher(itemPriceInput));
        typeOfUnitsInput.setOnKeyListener(typeOfUnitsInputKeyListener);
    }
}
