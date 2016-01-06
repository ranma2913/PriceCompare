package com.ranma2913.pricecompare.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by jsticha on 1/5/2016.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    public static final String DATABASE_NAME = "price_compare.db";
    public static final int DATABASE_VERSION = 1;
    final String TAG = DatabaseHelper.class.getSimpleName();
    private Dao<PriceComparison, Integer> priceComparisonDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            /*Create tables. This onCreate() method will be invoked only once of the application
            life time i.e. the first time when the application starts.*/
            TableUtils.createTable(connectionSource, PriceComparison.class);
        } catch (SQLException e) {
            Log.e(TAG + "@onCreate", "Unable to create databases", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.w(TAG + "@onUpgrade", "Upgrading database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data");
            TableUtils.dropTable(connectionSource, PriceComparison.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG + "@onUpgrade", "Unable to upgrade database from version " + oldVersion
                    + " to new " + newVersion, e);
        }
    }

    public Dao<PriceComparison, Integer> getPriceComparisonDao() throws SQLException {
        if (priceComparisonDao == null) {
            priceComparisonDao = getDao(PriceComparison.class);
        }
        return priceComparisonDao;
    }

    /**
     * Drop and recreate the table in the database.
     *
     * @return true if the operation was successful.
     */
    public boolean clearTable(Class tableName) {
        try {
            TableUtils.dropTable(this.connectionSource, tableName, true);
            TableUtils.createTable(this.connectionSource, tableName);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

