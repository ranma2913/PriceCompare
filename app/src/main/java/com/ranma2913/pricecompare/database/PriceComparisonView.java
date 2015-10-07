package com.ranma2913.pricecompare.database;

/**
 * Created by jsticha on 8/5/2015.
 */

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ranma2913.pricecompare.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.listview_item_row)
public class PriceComparisonView extends LinearLayout {
    final String TAG = PriceComparisonView.class.getSimpleName();
    @ViewById
    TextView itemDescription;
    @ViewById
    TextView storeName;
    @ViewById
    TextView itemPrice;
    @ViewById
    TextView itemQuantityUnits;
    @ViewById
    TextView pricePerUnit;

    public PriceComparisonView(Context context) {
        super(context);
    }

    public void bind(PriceComparison pc) {
        itemDescription.setText(pc.getItemDescription());
        storeName.setText(pc.getStoreName());
        itemPrice.setText("$" + pc.getItemPrice());
        itemQuantityUnits.setText(pc.getQuantityAndUnitsString());
        pricePerUnit.setText(pc.getPricePerUnitString());
    }
}