package com.ranma2913.database;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;

/**
 * Created by Joel on 6/11/2015.
 */
public class DatabaseHelper {
    final String TAG = "::DatabaseHelper";
    Manager manager;
    Database database;

    public DatabaseHelper(AndroidContext context, String dbName) {
        if (!Manager.isValidDatabaseName(dbName)) {
            Log.e(TAG, ".initDatabase(): Bad database name");
            return;
        }
        try {
            manager = new Manager(context, Manager.DEFAULT_OPTIONS);
            Log.d(TAG, ".initDatabase(): Manager created");
        } catch (IOException e) {
            Log.e(TAG, ".initDatabase(): Cannot create manager object");
            return;
        }
        try {
            database = manager.getDatabase(dbName);
            Log.d(TAG, ".initDatabase(): Database created");

        } catch (CouchbaseLiteException e) {
            Log.e(TAG, ".initDatabase(): Cannot get database");
        }
    }

    public Query createAllDocumentsQuery() {
        return database.createAllDocumentsQuery();
    }

    public Document createDocument() {
        return database.createDocument();
    }

    public Document getDocument(String docID) {
        return database.getDocument(docID);
    }
}
