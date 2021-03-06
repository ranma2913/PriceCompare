package com.ranma2913.global;

import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by jsticha on 5/5/2015.
 * <p/>
 * This class contains various useful utility functions.
 */
public class Utils {
    final static String TAG = "::Utils";

    public static boolean inputFieldsNotEmptyOrNull(ArrayList<EditText> editTextArrayList) {
        for (EditText entryField : editTextArrayList) {
            if (TextUtils.isEmpty(entryField.getText().toString().trim())) {
                Log.d(TAG, ".inputFieldsNotEmptyOrNull(): EntryField Empty --> ID:" + entryField.getId() + " Hint:" + entryField.getHint());
                return false;
            }
        }
        Log.d(TAG, ".inputFieldsNotEmptyOrNull(): No Empty Fields Found");
        return true;
    }

    public static String getCurrentTimeStampString() {
        // get the current date and time
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        return dateFormatter.format(GregorianCalendar.getInstance().getTime());
    }

    public static String formatDollarPriceString(String priceString) {
        String cleanString = priceString.replaceAll("[$,.]", "");
        BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        return NumberFormat.getCurrencyInstance().format(parsed);

    }

    public static String formatCleanPriceString(String priceString) {
        String cleanString = priceString.replaceAll("[$,.]", "");
        BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        return parsed.toString();
    }
}
