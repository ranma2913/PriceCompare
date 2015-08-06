package com.ranma2913.pricecompare;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ranma2913.global.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;

import java.math.BigDecimal;
import java.util.ArrayList;

@EActivity(R.layout.activity_enter_item)
public class EnterItemActivity extends AppCompatActivity {
    final String TAG = EnterItemActivity.class.getName();
    @ViewById(R.id.itemDescriptionInput)
    EditText itemDescriptionInput;
    @ViewById(R.id.itemPriceInput)
    EditText itemPriceInput;
    @ViewById(R.id.numberOfUnitsInput)
    EditText numberOfUnitsInput;
    @ViewById(R.id.typeOfUnitsInput)
    Spinner typeOfUnitsInputSpinner;
    @ViewById(R.id.itemStoreInput)
    EditText itemStoreInput;

    @ViewsById({R.id.itemDescriptionInput, R.id.itemPriceInput, R.id.numberOfUnitsInput, R.id.itemStoreInput})
    ArrayList<EditText> editTextArrayList;

    @Bean(DatabaseDaoImp.class)
    DatabaseDAO databaseDAO;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enter_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @AfterViews
    protected void init() {
//        itemPriceInput.addTextChangedListener(new MoneyTextWatcher(itemPriceInput));
//        initTypeOfUnitsInputOnKeyListener();
//        initItemDescriptionOnFocusListener();
        initTypeOfUnitsSpinner();
    }

    private void refreshScreen() {
        Log.d(TAG, ".refreshScreen(): started");
        itemStoreInput.setText(null);
        itemDescriptionInput.setText(null);
        itemPriceInput.setText(null);
        numberOfUnitsInput.setText(null);
        Log.d(TAG, ".refreshScreen(): finished");
    }

    private void initTypeOfUnitsSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.typeOfUnitsChoicesArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        typeOfUnitsInputSpinner.setAdapter(adapter);
    }

//    private void initItemDescriptionOnFocusListener() {
//        itemDescriptionInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    try {
//                        inputManager.showSoftInput(itemDescriptionInput, InputMethodManager.SHOW_IMPLICIT);
//                    } catch (NumberFormatException e) {
//                        Log.e(TAG, ".initItemDescriptionOnFocusListener(): Item description focus listener ERROR");
//                    }
//                }
//            }
//        });
//        Log.d(TAG, ".initItemDescriptionOnFocusListener(): Item description focus listener set.");
//    }

    private void initTypeOfUnitsInputOnKeyListener() {
        typeOfUnitsInputSpinner.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    calculatePrice();//match this behavior to your 'Send' (or Confirm) button
                    return true;
                }
                return false;
            }
        });
        Log.d(TAG, ".initTypeOfUnitsInputOnKeyListener(): Type of units input key listener set.");
    }

    @Click(R.id.clearButton)
    void clearButton() {
        refreshScreen();
    }

    @Click
    void calculatePriceButton() {
        calculatePrice();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshScreen();
    }

    private boolean validateInputFields() {
        Log.d(TAG, ".validateInputFields(): started");
        boolean isValid = true;
        if (!Utils.inputFieldsNotEmptyOrNull(editTextArrayList)) {
            Toast.makeText(getApplicationContext(), "Please fill all entry fields.", Toast.LENGTH_SHORT).show();
            isValid = false;
            Log.d(TAG, ".validateInputFields(): Please fill all entry fields.");
        } else {
            if (new BigDecimal(itemPriceInput.getText().toString().replace("$", "")).compareTo(BigDecimal.ZERO) <= 0) {
                Toast.makeText(getApplicationContext(), "Item Price must be greater than $0.00.", Toast.LENGTH_SHORT).show();
                isValid = false;
                Log.d(TAG, ".validateInputFields(): Item Price must be greater than $0.00.");
            }
            if (Double.parseDouble(numberOfUnitsInput.getText().toString().trim()) <= 0.0) {
                Toast.makeText(getApplicationContext(), "Number of units must be greater than 0.", Toast.LENGTH_SHORT).show();
                isValid = false;
                Log.d(TAG, ".validateInputFields(): Number of units must be greater than 0.");
            }
        }
        Log.d(TAG, ".validateInputFields(): finished");
        return isValid;
    }

    @SuppressWarnings("ConstantConditions")
    private void calculatePrice() {
        if (validateInputFields()) {
            PriceComparison priceComparison = databaseDAO.saveNewPriceComparison(itemStoreInput.getText().toString(), itemDescriptionInput.getText().toString(), itemPriceInput.getText().toString(), numberOfUnitsInput.getText().toString(), typeOfUnitsInputSpinner.getSelectedItem().toString());
            Toast.makeText(getApplicationContext(), priceComparison.getPricePerUnitString(), Toast.LENGTH_LONG).show();
            refreshScreen();
        }
    }
}