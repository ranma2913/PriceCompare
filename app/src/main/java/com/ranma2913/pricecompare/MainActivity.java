package com.ranma2913.pricecompare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_home_screen)
public class MainActivity extends AppCompatActivity {

    final String TAG = MainActivity.class.getName();

    @ViewById(R.id.homeScreenWelcomeMessage)
    TextView homeScreenWelcomeMessage;

    @ViewById(R.id.priceCompareHistoryListView)
    ListView priceCompareHistoryListView;
    @Bean
    PriceComparisonAdapter priceComparisonAdapter;

    @AfterViews
    void bindAdapter() {
        priceCompareHistoryListView.setAdapter(priceComparisonAdapter);
    }

    @ItemLongClick(R.id.priceCompareHistoryListView)
    void priceCompareHistoryListView(PriceComparisonItem priceComparisonItem) {
        Toast.makeText(getApplicationContext(), "Item Long Clicked:" + priceComparisonItem, Toast.LENGTH_SHORT).show();
    }

    @Click
    void addItemButton() {
        Intent intent = new Intent(getApplicationContext(), EnterItemActivity_.class);
        startActivity(intent);
    }
}
