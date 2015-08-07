package com.ranma2913.pricecompare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enter_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
