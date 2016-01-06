package com.ranma2913.pricecompare.activities;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ranma2913.pricecompare.R;
import com.ranma2913.pricecompare.database.PriceComparison;
import com.ranma2913.pricecompare.database.PriceComparisonAdapter;
import com.ranma2913.pricecompare.fragments.ConfirmDeleteDatabaseFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_home_screen)
@OptionsMenu(R.menu.menu_home_screen)
public class MainActivity extends AppCompatActivity implements ConfirmDeleteDatabaseFragment.ConfirmDeleteDatabaseDialogListener {

    final String TAG = MainActivity.class.getSimpleName();

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

    @OptionsItem(R.id.refreshData)
    public boolean refreshDataSelected() {
        Toast.makeText(getApplicationContext(), "Refresh Data menu item pressed", Toast.LENGTH_SHORT).show();
        priceComparisonAdapter.refreshData();
        return true;
    }

    @OptionsItem(R.id.about)
    public boolean aboutSelected() {
        Toast.makeText(getApplicationContext(), "About menu item pressed", Toast.LENGTH_SHORT).show();
        return true;
    }

    @OptionsItem(R.id.clearDatabase)
    public boolean clearDatabaseSelected() {
        // Create an instance of the dialog fragment and show it
        new ConfirmDeleteDatabaseFragment().show(getSupportFragmentManager(), "ConfirmDeleteDatabaseFragment");
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        priceComparisonAdapter.refreshData();
    }

    /**
     * Method starts the create item activity.
     */
    @Click
    void addItemButton() {
        Intent intent = new Intent(getApplicationContext(), EnterItemActivity_.class);
        startActivity(intent);
    }

    /**
     * Method captures long click events on a history Item.
     *
     * @param priceComparison The item from the adapter that was clicked.
     */
    @ItemLongClick
    void priceCompareHistoryListView(PriceComparison priceComparison) {
        Toast.makeText(getApplicationContext(), "Item Long Clicked:" + priceComparison, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteDatabasePositiveClick(DialogFragment dialog) {
        Log.d(TAG, "@onDeleteDatabasePositiveClick: Positive button Clicked for delete database");
        Toast.makeText(getApplicationContext(), "Delete all comparisons result: " + (priceComparisonAdapter.clearTable() ? "SUCCESS" : "FAILED"), Toast.LENGTH_LONG).show();
        priceComparisonAdapter.refreshData();
    }

    @Override
    public void onDeleteDatabaseNegativeClick(DialogFragment dialog) {
        Log.d(TAG, "@onDeleteDatabaseNegativeClick: Negative button Clicked for delete database");
    }
}
