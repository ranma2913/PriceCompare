package com.ranma2913.pricecompare.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.ranma2913.global.MoneyTextWatcher;
import com.ranma2913.global.Utils;
import com.ranma2913.pricecompare.R;
import com.ranma2913.pricecompare.database.DatabaseHelper;
import com.ranma2913.pricecompare.database.PriceComparison;
import com.ranma2913.pricecompare.placesApi.CustomAutoCompleteTextView;
import com.ranma2913.pricecompare.placesApi.PlaceJsonParser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@EActivity(R.layout.activity_enter_item)
@OptionsMenu(R.menu.menu_enter_item)
public class EnterItemActivity extends AppCompatActivity implements LocationListener {
    private static final String ANDROID_API_KEY = "AIzaSyDkYLGRs55lMh-c0oHQOsSWPdpy5thT6Sg";
    private static final String SERVER_API_KEY = "AIzaSyCh-rttwdSPLU__9S6t-JDimUS9E8gd61s";
    private static final String API_KEY = SERVER_API_KEY;
    private final String TAG = EnterItemActivity.class.getSimpleName();
    @ViewById(R.id.itemDescriptionInput)
    EditText itemDescriptionInput;
    @ViewById(R.id.itemPriceInput)
    EditText itemPriceInput;
    @ViewById(R.id.numberOfUnitsInput)
    EditText numberOfUnitsInput;
    @ViewById(R.id.typeOfUnitsInput)
    Spinner typeOfUnitsInputSpinner;
    @ViewById(R.id.itemStoreInput)
    CustomAutoCompleteTextView itemStoreInput;
    @ViewById(R.id.locationTextView)
    TextView locationTextView;

    @ViewsById({R.id.itemDescriptionInput, R.id.itemPriceInput, R.id.numberOfUnitsInput, R.id.itemStoreInput})
    ArrayList<EditText> editTextArrayList;

    DatabaseHelper databaseHelper;

    PlacesTask placesTask;
    ParserTask parserTask;

    LocationManager locationManager;
    String locationProvider;
    String latLongString;
    Location location;

    @AfterViews
    void initLocationManager() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }


        // Define the criteria how to select the location locationProvider -> use
        // default
        Criteria criteriaForLocationService = new Criteria();
        List<String> acceptableLocationProviders = locationManager.getProviders(criteriaForLocationService, true);
        if (!acceptableLocationProviders.isEmpty()) {
            locationProvider = acceptableLocationProviders.get(0);
        } else {
            locationProvider = "";
        }
        Log.d(TAG + "@initLocationManager", "Provider " + locationProvider + " has been selected.");
        if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        // Initialize the location fields
        if (location != null) {
            Log.d(TAG + "@initLocationManager", "Location=" + location);
            onLocationChanged(location);
        }
    }

    @AfterViews
    void initTypeOfUnitsSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.typeOfUnitsChoicesArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        typeOfUnitsInputSpinner.setAdapter(adapter);
    }

    @AfterViews
    void initMoneyTextWatcher() {
        itemPriceInput.addTextChangedListener(new MoneyTextWatcher(itemPriceInput));
    }

    @AfterViews
    void initTypeOfUnitsInputOnKeyListener() {
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
        Log.d(TAG, "@initTypeOfUnitsInputOnKeyListener: Type of units input key listener set.");
    }

    @AfterViews
    void initStoreNameAutoComplete() {
        Log.d(TAG, "@initStoreNameAutoComplete: enter");
        itemStoreInput.setThreshold(1);
        itemStoreInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                placesTask = new PlacesTask();
                placesTask.execute(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
        Log.d(TAG, "@initStoreNameAutoComplete: exit");
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(locationProvider, 400, 1, this);
        }
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

		/*
         * You'll need this in your class to release the helper when done.
		 */
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    /* This is how, DatabaseHelper can be initialized for future use */
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @OptionsItem(R.id.about)
    public boolean aboutSelected() {
        Toast.makeText(getApplicationContext(), "About menu item pressed", Toast.LENGTH_SHORT).show();
        return true;
    }

    void refreshScreen() {
        Log.d(TAG + "@refreshScreen", "enter");
        itemStoreInput.setText(null);
        itemDescriptionInput.setText(null);
        itemPriceInput.setText(null);
        numberOfUnitsInput.setText(null);
        Log.d(TAG + "@refreshScreen", "exit");
    }

    @Click(R.id.clearButton)
    void clearButton() {
        refreshScreen();
    }

    @Click
    void calculatePriceButton() {
        calculatePrice();
    }

    private boolean validateInputFields() {
        Log.d(TAG + "@validateInputFields", "started");
        boolean isValid = true;
        if (!Utils.inputFieldsNotEmptyOrNull(editTextArrayList)) {
            Toast.makeText(getApplicationContext(), "Please fill all entry fields.", Toast.LENGTH_SHORT).show();
            isValid = false;
            Log.d(TAG + "@validateInputFields", "Please fill all entry fields.");
        } else {
            if (new BigDecimal(Utils.formatCleanPriceString(itemPriceInput.getText().toString())).compareTo(BigDecimal.ZERO) <= 0) {
                Toast.makeText(getApplicationContext(), "Item Price must be greater than $0.00.", Toast.LENGTH_SHORT).show();
                isValid = false;
                Log.d(TAG + "@validateInputFields", "Item Price must be greater than $0.00.");
            }
            if (Double.parseDouble(numberOfUnitsInput.getText().toString().trim()) <= 0.0) {
                Toast.makeText(getApplicationContext(), "Number of units must be greater than 0.", Toast.LENGTH_SHORT).show();
                isValid = false;
                Log.d(TAG + "@validateInputFields", "Number of units must be greater than 0.");
            }
        }
        Log.d(TAG + "@validateInputFields", "finished");
        return isValid;
    }

    @SuppressWarnings("ConstantConditions")
    private void calculatePrice() {
        if (validateInputFields()) {
            PriceComparison priceComparison = new PriceComparison(
                    itemStoreInput.getText().toString(),
                    itemDescriptionInput.getText().toString(),
                    Utils.formatCleanPriceString(itemPriceInput.getText().toString()),
                    numberOfUnitsInput.getText().toString(),
                    typeOfUnitsInputSpinner.getSelectedItem().toString(),
                    Utils.getCurrentTimeStampString());
            try {
                // This is how, a reference of DAO object can be done
                final Dao<PriceComparison, Integer> sqLitePriceComparisonDao = getHelper().getPriceComparisonDao();

                //This is the way to insert data into a database table
                sqLitePriceComparisonDao.create(priceComparison);

            } catch (SQLException e) {
                e.printStackTrace();
            }
//            PriceComparison priceComparison = databaseHelper.saveNewPriceComparison(
//                    itemStoreInput.getText().toString(),
//                    itemDescriptionInput.getText().toString(),
//                    itemPriceInput.getText().toString(),
//                    numberOfUnitsInput.getText().toString(),
//                    typeOfUnitsInputSpinner.getSelectedItem().toString());
            Toast.makeText(getApplicationContext(), priceComparison.getPricePerUnitString(), Toast.LENGTH_LONG).show();
            refreshScreen();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        latLongString = lat + "," + lng;
        locationTextView.setText(latLongString);
        Log.d(TAG + "@onLocationChanged", "latLongString=" + latLongString);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Fetches all places from GooglePlaces AutoComplete Web Service
     */
    private class PlacesTask extends AsyncTask<String, Void, String> {
        private final String LocalTAG = PlacesTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... place) {
            Log.d(LocalTAG + "@doInBackground", "enter");
            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
                Log.d(LocalTAG + "@doInBackground", "Get Input: " + input);
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            // For storing data from web service
            String data = "";

            // Parameter builder for the parameters to the web service
            StringBuilder parameterBuilder = new StringBuilder();

            // Input from the TextView
            parameterBuilder.append(input);

            // Obtain browser key from https://code.google.com/apis/console
            parameterBuilder.append("&key=").append(API_KEY);

            // place type to be searched
            parameterBuilder.append("&types=establishment");

            // Device Location
            if (null != latLongString) {
                parameterBuilder.append("&location=").append(latLongString);
            }
            Log.d(LocalTAG + "@doInBackground", "Lat Long String: " + latLongString);

            // Rank results by distance
            parameterBuilder.append("&rankby=distance");


            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?" + parameterBuilder.toString();
            Log.d(LocalTAG + "@doInBackground", "URL Ready: " + url);
            try {
                // Fetching the data from we service
                data = downloadUrl(url);
                Log.d(LocalTAG + "@doInBackground", "Data fetched: " + data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(LocalTAG + "@onPostExecute", "enter");

            // Creating ParserTask
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
            Log.d(LocalTAG + "@onPostExecute", "exit");
        }

        /**
         * A method to download json data from url
         */
        protected String downloadUrl(String strUrl) throws IOException {
            Log.d(LocalTAG + "@downloadUrl", "enter");
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {
                Log.d(LocalTAG + "@downloadUrl", "Exception while downloading url :: " + e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            Log.d(LocalTAG + "@downloadUrl", "exit");
            return data;
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        private final String LocalTAG = ParserTask.class.getSimpleName();
        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {
            Log.d(LocalTAG + "@doInBackground", "enter");
            List<HashMap<String, String>> places = null;

            PlaceJsonParser placeJsonParser = new PlaceJsonParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            Log.d(LocalTAG + "@doInBackground", "exit");
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            Log.d(LocalTAG + "@onPostExecute", "enter");
            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

            // Setting the adapter
            itemStoreInput.setAdapter(adapter);
            Log.d(LocalTAG + "@onPostExecute", "exit");
        }
    }
}
