package com.ranma2913.pricecompare;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
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

    @AfterViews
    protected void init() {
        itemPriceInput.addTextChangedListener(new MoneyTextWatcher(itemPriceInput));
        typeOfUnitsInput.setOnKeyListener(typeOfUnitsInputKeyListener);
    }

    @Click
    void clearButton() {
        //R.id.homeScreenWelcomeMessage,R.id.itemDescriptionInput,R.id.itemPriceInput,R.id.numberOfUnitsInput,R.id.typeOfUnitsInput
        resetInputFields();
    }

    private void resetInputFields() {
        itemDescriptionInput.setText(null);
        itemPriceInput.setText(null);
        numberOfUnitsInput.setText(null);
        typeOfUnitsInput.setText(null);
    }

    private void calculatePrice() {
        BigDecimal pricePerUnit = new BigDecimal(itemPriceInput.getText().toString().replace("$", "")).divide(new BigDecimal(numberOfUnitsInput.getText().toString()), MathContext.DECIMAL128);
        String outputString = itemDescriptionInput.getText().toString()
                + " $" + new BigDecimal(pricePerUnit.toString()).setScale(4, RoundingMode.HALF_UP).doubleValue()
                + " Price Per Unit";
        //homeScreenWelcomeMessage.setText(outputString);
        Toast.makeText(getApplicationContext(), outputString, Toast.LENGTH_LONG).show();
        resetInputFields();
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
