package com.ranma2913.pricecompare;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;
import com.ranma2913.global.MoneyTextWatcher;
import com.ranma2913.global.PriceComparisonComparator;
import com.ranma2913.global.Utils;
import com.ranma2913.valueObjects.PriceComparisonVO;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.activity_home_screen)
public class MainActivity extends Activity {

    final String TAG = "::MainActivity";
    final String dbName = "price_compare_db";

    @ViewById(R.id.homeScreenWelcomeMessage)
    TextView homeScreenWelcomeMessage;
    @ViewById(R.id.itemDescriptionInput)
    EditText itemDescriptionInput;
    @ViewById(R.id.itemPriceInput)
    EditText itemPriceInput;
    @ViewById(R.id.numberOfUnitsInput)
    EditText numberOfUnitsInput;
    @ViewById(R.id.typeOfUnitsInput)
    Spinner typeOfUnitsInputSpinner;
    @ViewsById({R.id.itemDescriptionInput, R.id.itemPriceInput, R.id.numberOfUnitsInput})
    ArrayList<EditText> editTextArrayList;
    @ViewById(R.id.priceCompareHistoryListView)
    ListView priceCompareHistoryListView;
    @ViewById(R.id.hiddenLayout)
    LinearLayout linearLayout;
    @SystemService
    InputMethodManager inputManager;
    ArrayList<PriceComparisonVO> priceComparisonVOArrayList;
    ArrayAdapter<PriceComparisonVO> priceComparisonVOArrayAdapter;
    Manager manager;
    Database database;

    @AfterViews
    protected void init() {
        itemPriceInput.addTextChangedListener(new MoneyTextWatcher(itemPriceInput));
        priceComparisonVOArrayList = new ArrayList<>();
//        initTypeOfUnitsInputOnKeyListener();
//        initItemDescriptionOnFocusListener();
        initDatabase();
        initTypeOfUnitsSpinner();
        initPriceCompareHistory();
    }

    private void initTypeOfUnitsSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.typeOfUnitsChoicesArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        typeOfUnitsInputSpinner.setAdapter(adapter);
    }

    private void initItemDescriptionOnFocusListener() {
        itemDescriptionInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    try {
                        inputManager.showSoftInput(itemDescriptionInput, InputMethodManager.SHOW_IMPLICIT);
                    }
                    catch (NumberFormatException e) {
                        Log.e(TAG, ".initItemDescriptionOnFocusListener(): Item description focus listener ERROR");
                    }
                }
            }
        });
        Log.d(TAG, ".initItemDescriptionOnFocusListener(): Item description focus listener set.");
    }

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

    private void initDatabase() {
        if (!Manager.isValidDatabaseName(dbName)) {
            Log.e(TAG, ".initDatabase(): Bad database name");
            return;
        }
        try {
            manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
            Log.d(TAG, ".initDatabase(): Manager created");
            database = manager.getDatabase(dbName);
            Log.d(TAG, ".initDatabase(): Database created");
        }
        catch (IOException e) {
            Log.e(TAG, ".initDatabase(): Cannot create manager object");
        }
        catch (CouchbaseLiteException e) {
            Log.e(TAG, ".initDatabase(): Cannot get database");
        }
    }

    private void initPriceCompareHistory() {
        loadPriceCompareHistoryArray();
        priceComparisonVOArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, priceComparisonVOArrayList);
//        priceComparisonVOArrayAdapter.setNotifyOnChange(true);
        priceCompareHistoryListView.setAdapter(priceComparisonVOArrayAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshScreen();
    }

    @Click
    void calculatePriceButton() {
        calculatePrice();
    }

    @Click(R.id.clearButton)
    void clearButton() {
        refreshScreen();
        linearLayout.requestFocus();
    }

    private void refreshPriceCompareHistoryArray() {
        loadPriceCompareHistoryArray();
        priceComparisonVOArrayAdapter.notifyDataSetChanged();
    }

    private void loadPriceCompareHistoryArray() {
        priceComparisonVOArrayList.removeAll(priceComparisonVOArrayList);
        Query query = database.createAllDocumentsQuery();
        query.setDescending(true);
        try {
            QueryEnumerator result = query.run();
            while (result.hasNext()) {
                QueryRow row = result.next();
                Document document = row.getDocument();
                if (document.getProperty("docType").equals("priceComparison")) {
                    PriceComparisonVO newPriceComparisonVO = new PriceComparisonVO((Map<String, String>) document.getProperty("priceComparisonVO"));
                    priceComparisonVOArrayList.add(newPriceComparisonVO);
                    Log.d(TAG, ".loadPriceCompareHistoryArray(): Price Comparison Loaded =" + newPriceComparisonVO.getValueMap());
                }
            }
            Collections.sort(priceComparisonVOArrayList, new PriceComparisonComparator());
            Log.d(TAG, ".loadPriceCompareHistoryArray(): Sorted List:");
            for (PriceComparisonVO comparisonVO : priceComparisonVOArrayList) {
                Log.d(TAG, ".loadPriceCompareHistoryArray(): Price Comparison Loaded =" + comparisonVO.getValueMap());
            }
        }
        catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, ".loadPriceCompareHistoryArray(): priceComparisonVOArrayList Size(" + priceComparisonVOArrayList.size() + ")");
    }

    private void refreshScreen() {
        Log.d(TAG, ".refreshScreen(): started");
        itemDescriptionInput.setText(null);
        itemPriceInput.setText(null);
        numberOfUnitsInput.setText(null);
//        typeOfUnitsInputSpinner.setText(null);
        refreshPriceCompareHistoryArray();
//        if (getCurrentFocus() != null) {
//            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
        Log.d(TAG, ".refreshScreen(): finished");
    }

    private boolean validateInputFields() {
        Log.d(TAG, ".validateInputFields(): started");
        boolean isValid = true;
        if (!Utils.inputFieldsNotEmptyOrNull(editTextArrayList)) {
            Toast.makeText(getApplicationContext(), "Please fill all entry fields.", Toast.LENGTH_SHORT).show();
            isValid = false;
            Log.d(TAG, ".validateInputFields(): Please fill all entry fields.");
        }
        else {
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
            PriceComparisonVO priceComparisonVO = new PriceComparisonVO(itemDescriptionInput.getText().toString(), itemPriceInput.getText().toString(), numberOfUnitsInput.getText().toString(), typeOfUnitsInputSpinner.getSelectedItem().toString());
            Toast.makeText(getApplicationContext(), priceComparisonVO.getPricePerUnitString(), Toast.LENGTH_LONG).show();
            savePriceComparison(priceComparisonVO);
            refreshScreen();
        }
    }

    private void savePriceComparison(PriceComparisonVO priceComparisonVO) {
        // create an object that contains data for a document
        Map<String, Object> docProperties = new HashMap<>();
        docProperties.put("docType", "priceComparison");
        docProperties.put("priceComparisonVO", priceComparisonVO);

        // display the data for the new document
        Log.d(TAG, ".savePriceComparison(PriceComparisonVO priceComparisonVO): docProperties=" + String.valueOf(docProperties));
        // create an empty document
        Document newDocument = database.createDocument();
        // add content to document and write the document to the database
        try {
            newDocument.putProperties(docProperties);
            Log.d(TAG, ".savePriceComparison(PriceComparisonVO priceComparisonVO): Document written to database named " + dbName + " with ID = " + newDocument.getId());
        }
        catch (CouchbaseLiteException e) {
            Log.e(TAG, ".savePriceComparison(PriceComparisonVO priceComparisonVO): Cannot write document to database", e);
        }
        // save the ID of the new document
        String docID = newDocument.getId();
        // retrieve the document from the database
        Document retrievedDocument = database.getDocument(docID);

//        PriceComparisonVO retrievedPriceComparisonVO = new PriceComparisonVO((Map<String, String>) retrievedDocument.getProperty("priceComparisonVO"));
        Log.d(TAG, ".savePriceComparison(PriceComparisonVO priceComparisonVO): retrievedDocument.getProperties=" + String.valueOf(retrievedDocument.getProperties()));
    }
}
