package com.ranma2913.pricecompare;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
    EditText typeOfUnitsInput;
    @ViewsById({R.id.itemDescriptionInput, R.id.itemPriceInput, R.id.numberOfUnitsInput, R.id.typeOfUnitsInput})
    ArrayList<EditText> editTextArrayList;
    @ViewById(R.id.priceCompareHistoryListView)
    ListView priceCompareHistoryListView;
    @SystemService
    InputMethodManager inputManager;
    ArrayList<PriceComparisonVO> priceComparisonVOArrayList;
    ArrayAdapter<PriceComparisonVO> priceComparisonVOArrayAdapter;
    Manager manager;
    Database database;
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

    @AfterViews
    protected void init() {
        itemPriceInput.addTextChangedListener(new MoneyTextWatcher(itemPriceInput));
        typeOfUnitsInput.setOnKeyListener(typeOfUnitsInputKeyListener);
        initDatabase();
        initHistoryList();
    }

    private void initDatabase() {
        if (!Manager.isValidDatabaseName(dbName)) {
            Log.e(TAG, ".initDatabase(): Bad database name");
            return;
        }
        try {
            manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
            Log.d(TAG, ".initDatabase(): Manager created");
        }
        catch (IOException e) {
            Log.e(TAG, ".initDatabase(): Cannot create manager object");
            return;
        }
        try {
            database = manager.getDatabase(dbName);
            Log.d(TAG, ".initDatabase(): Database created");

        }
        catch (CouchbaseLiteException e) {
            Log.e(TAG, ".initDatabase(): Cannot get database");
        }
    }

    private void initHistoryList() {
        loadPriceCompareHistoryList();
        priceComparisonVOArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, priceComparisonVOArrayList);
        priceCompareHistoryListView.setAdapter(priceComparisonVOArrayAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPriceCompareHistoryList();

    }

    @Click
    void calculatePriceButton() {
        calculatePrice();
    }

    @Click
    void clearButton() {
        refreshScreen();
    }


    private void loadPriceCompareHistoryList() {
        priceComparisonVOArrayList = new ArrayList<>();
        Query query = database.createAllDocumentsQuery();
        query.setDescending(true);
        try {
            QueryEnumerator result = query.run();
            while (result.hasNext()) {
                QueryRow row = result.next();
                Document document = row.getDocument();
                if (document.getProperty("docType").equals("priceComparison")) {
                    PriceComparisonVO comparisonVO = new PriceComparisonVO((Map<String, String>) document.getProperty("priceComparisonVO"));
                    priceComparisonVOArrayList.add(comparisonVO);
                    Log.d(TAG, ".loadPriceCompareHistoryList(): Price Comparison Loaded =" + comparisonVO.getValueMap());
                }
            }
            Collections.sort(priceComparisonVOArrayList, new PriceComparisonComparator());
            Log.d(TAG, ".loadPriceCompareHistoryList(): Sorted List:");
            for (PriceComparisonVO comparisonVO : priceComparisonVOArrayList) {
                Log.d(TAG, ".loadPriceCompareHistoryList(): Price Comparison Loaded =" + comparisonVO.getValueMap());
            }
        }
        catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, ".loadPriceCompareHistoryList(): priceComparisonVOArrayList Size(" + priceComparisonVOArrayList.size() + ")");
    }

    private void refreshScreen() {
        Log.d(TAG, ".refreshScreen(): started");
        itemDescriptionInput.setText(null);
        itemPriceInput.setText(null);
        numberOfUnitsInput.setText(null);
        typeOfUnitsInput.setText(null);
        loadPriceCompareHistoryList();
        priceComparisonVOArrayAdapter.notifyDataSetChanged();
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
            PriceComparisonVO priceComparisonVO = new PriceComparisonVO(itemDescriptionInput.getText().toString(), itemPriceInput.getText().toString(), numberOfUnitsInput.getText().toString(), typeOfUnitsInput.getText().toString());
            Toast.makeText(getApplicationContext(), priceComparisonVO.getPricePerUnitString(), Toast.LENGTH_LONG).show();
            savePriceComparison(priceComparisonVO);
            refreshScreen();
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
