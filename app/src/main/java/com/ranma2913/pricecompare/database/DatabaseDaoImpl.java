package com.ranma2913.pricecompare.database;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;
import com.ranma2913.global.Constants;
import com.ranma2913.global.Utils;
import com.ranma2913.pricecompare.util.PriceComparisonComparator;

import org.androidannotations.annotations.EBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jsticha on 8/4/2015.
 */
@EBean
public class DatabaseDaoImpl implements DatabaseDao {
    final String TAG = DatabaseDaoImpl.class.getSimpleName();

    Manager manager;
    Database database;

    public DatabaseDaoImpl(Context context) {
        if (!Manager.isValidDatabaseName(Constants.PRICE_COMPARE_DB_NAME)) {
            Log.e(TAG + "@initDatabase", "Bad database name");
            return;
        }
        try {
            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
            Log.i(TAG + "@initDatabase", "Manager created");
            database = manager.getDatabase(Constants.PRICE_COMPARE_DB_NAME);
            Log.i(TAG + "@initDatabase", "Database created");
        } catch (IOException e) {
            Log.e(TAG + "@initDatabase", "Cannot create manager object");
        } catch (CouchbaseLiteException e) {
            Log.e(TAG + "@initDatabase", "Cannot get database");
        }
    }

    private String getNewDocument(Database database, HashMap<String, String> newProperties) {
        // Create a new document and add data
        Document document = database.createDocument();
        String documentId = document.getId();

        // create an object that contains data for a document
        Map<String, Object> doc = new HashMap<>();
        doc.put("docType", "priceComparison");
        doc.put("priceComparisonDocument", new PriceComparison(documentId, newProperties));

        try {
            // Save the properties to the document
            document.putProperties(doc);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG + "@getNewDocument", "Error putting document", e);
        }
        return documentId;
    }

    @Override
    public PriceComparison saveNewPriceComparison(String storeName, String itemDescription, String itemPrice, String numberOfUnits, String typeOfUnits) {
        HashMap<String, String> newProperties = new HashMap<>();
        newProperties.put("storeName", storeName);
        newProperties.put("itemDescription", itemDescription);
        newProperties.put("itemPrice", Utils.formatCleanPriceString(itemPrice));
        newProperties.put("numberOfUnits", numberOfUnits);
        newProperties.put("typeOfUnits", typeOfUnits);
        newProperties.put("creationDate", Utils.getCurrentTimeStampString());

        //create the new comparison document and get the ID
        String documentId = getNewDocument(database, newProperties);
        Log.d(TAG + "@saveNewPriceComparison", "new document id=" + documentId);
        // retrieve the document from the database
        Document retrievedDocument = database.getDocument(documentId);
        Log.d(TAG + "@getNewDocument", "document=" + String.valueOf(retrievedDocument));
        @SuppressWarnings("unchecked") Map<String, String> comparisonMap = (Map<String, String>) retrievedDocument.getProperty("priceComparisonDocument");
        return new PriceComparison(documentId, comparisonMap);
    }

    @Override
    public boolean deleteDatabase() {
        try {
            database.delete();
            Log.d(TAG + "@deleteDatabase", "Database successfully deleted");
            database = manager.getDatabase(Constants.PRICE_COMPARE_DB_NAME);
            Log.d(TAG + "@deleteDatabase", "Database successfully re-created");
            return true;
        } catch (CouchbaseLiteException e) {
            Log.e(TAG + "@deleteDatabase", "Cannot delete database", e);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public PriceComparison updatePriceComparison(PriceComparison docWithUpdates) {
        //get the document from the database
        Document retrievedDocument = database.getDocument(docWithUpdates.getDocumentID());
        Log.d(TAG + "@updatePriceComparison", "retrievedDocument=" + String.valueOf(retrievedDocument));
        try {
            @SuppressWarnings("unchecked") Map<String, Object> comparisonMap = (Map<String, Object>) retrievedDocument.getProperty("priceComparisonDocument");
            retrievedDocument.putProperties(comparisonMap);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG + "@updatePriceComparison", "Error Saving update: " + String.valueOf(docWithUpdates.getDocProperties()), e);
        }

        // retrieve the document from the database
        Document updatedDocument = database.getDocument(docWithUpdates.getDocumentID());
        Log.d(TAG + "@updatePriceComparison", "updatedDocument=" + String.valueOf(updatedDocument));

        //return a new construct containing the updates retrieved from the database.
        @SuppressWarnings("unchecked") Map<String, String> comparisonMap = (Map<String, String>) updatedDocument.getProperty("priceComparisonDocument");
        return new PriceComparison(updatedDocument.getId(), comparisonMap);
    }

    @Override
    public boolean deletePriceComparison(PriceComparison docToDelete) {
        Document retrievedDocument = database.getDocument(docToDelete.getDocumentID());
        Log.d(TAG + "@deletePriceComparison", "Document to delete=" + String.valueOf(retrievedDocument));
        // delete the document
        try {
            retrievedDocument.delete();
            Log.d(TAG + "@deletePriceComparison", "Deleted document, deletion status = " + retrievedDocument.isDeleted());
        } catch (CouchbaseLiteException e) {
            Log.e(TAG + "@deletePriceComparison", "Cannot delete document", e);
        }
        return retrievedDocument.isDeleted();
    }

    @Override
    public ArrayList<PriceComparison> getAllPriceComparisons() {
        Log.d(TAG + "@getAllPriceComparisons", "enter method");
        ArrayList<PriceComparison> allPriceComparisons = new ArrayList<>();
        try {
            QueryEnumerator result = database.createAllDocumentsQuery().run();
            Log.d(TAG + "@getAllPriceComparisons", "results successful. Number of Rows: " + result.getCount());
            while (result.hasNext()) {
                QueryRow row = result.next();
                Document retrievedDocument = database.getDocument(row.getSourceDocumentId());
                @SuppressWarnings("unchecked") Map<String, String> comparisonMap = (Map<String, String>) retrievedDocument.getProperty("priceComparisonDocument");
                if (null != comparisonMap && "priceComparison".equals(comparisonMap.get("docType"))) {
                    PriceComparison priceComparison = new PriceComparison(retrievedDocument.getId(), comparisonMap);
                    allPriceComparisons.add(priceComparison);
                } else {
                    Log.d(TAG + "@getAllPriceComparisons", "retrievedDocument is not a valid PriceComparison ID=" + retrievedDocument.getId());
                    Log.d(TAG + "@getAllPriceComparisons", "Bad retrievedDocument has been deleted :: " + retrievedDocument.delete());
                }
            }
        } catch (CouchbaseLiteException e) {
            Log.e(TAG + "@getAllPriceComparisons", "Error running query", e);
            e.printStackTrace();
        }
        Log.d(TAG + "@getAllPriceComparisons", "allPriceComparisons loaded. Size=" + allPriceComparisons.size());
        Log.d(TAG + "@getAllPriceComparisons", "exit method");
        return sortByCreationDate(allPriceComparisons);
    }

    private ArrayList<PriceComparison> sortByCreationDate(ArrayList<PriceComparison> priceComparisons) {
        Log.d(TAG + "@sortByCreationDate", "priceComparisons ArrayList:" + priceComparisons.toString());
        Collections.sort(priceComparisons, new PriceComparisonComparator());
        return priceComparisons;
    }
}
