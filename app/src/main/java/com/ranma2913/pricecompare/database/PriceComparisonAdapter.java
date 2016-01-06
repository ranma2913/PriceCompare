package com.ranma2913.pricecompare.database;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by jsticha on 8/4/2015.
 */
@EBean
public class PriceComparisonAdapter extends BaseAdapter {
    final String TAG = PriceComparisonAdapter.class.getSimpleName();

    List<PriceComparison> priceComparisonsList;

    DatabaseHelper databaseHelper;
    @RootContext
    Context context;
    private Dao<PriceComparison, Integer> priceComparisonsDao;

    @AfterInject
    void initAdapter() {
        try {
            priceComparisonsDao = getHelper().getPriceComparisonDao();
            priceComparisonsList = new ArrayList<>();
            priceComparisonsList = priceComparisonsDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // This is how, DatabaseHelper can be initialized for future use
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PriceComparisonView priceComparisonView;
        if (convertView == null) {
            priceComparisonView = PriceComparisonView_.build(context);
        } else {
            priceComparisonView = (PriceComparisonView) convertView;
        }
        priceComparisonView.bind(getItem(position));
        return priceComparisonView;
    }

    @Override
    public int getCount() {
        return priceComparisonsList.size();
    }

    @Override
    public PriceComparison getItem(int position) {
        return priceComparisonsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Clear all data by deleting the database.
     *
     * @return boolean the result of the action.
     */
    public boolean clearTable() {
        return databaseHelper.clearTable(PriceComparison.class);
    }

    public void refreshData() {
        try {
            priceComparisonsList = new ArrayList<>();
            priceComparisonsList = priceComparisonsDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

