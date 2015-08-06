package com.ranma2913.pricecompare;

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

    ArrayList<PriceComparison> priceComparisons;

    @Bean(DatabaseDaoImp.class)
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
}

