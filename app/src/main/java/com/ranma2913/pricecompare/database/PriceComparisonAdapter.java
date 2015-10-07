package com.ranma2913.pricecompare.database;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;


/**
 * Created by jsticha on 8/4/2015.
 */
@EBean
public class PriceComparisonAdapter extends BaseAdapter {
    final String TAG = PriceComparisonAdapter.class.getSimpleName();

    ArrayList<PriceComparison> priceComparisons;

    @Bean(DatabaseDaoImpl.class)
    DatabaseDAO databaseDAO;

    @RootContext
    Context context;

    @AfterInject
    void initAdapter() {
        priceComparisons = databaseDAO.getAllPriceComparisons();
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
        return priceComparisons.size();
    }

    @Override
    public PriceComparison getItem(int position) {
        return priceComparisons.get(position);
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
    public boolean clearDatabase() {
        return databaseDAO.deleteDatabase();
    }

    public void refreshData() {
        this.priceComparisons = databaseDAO.getAllPriceComparisons();
    }
}

