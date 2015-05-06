package com.ranma2913.global;

import android.text.TextUtils;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by jsticha on 5/5/2015.
 * <p/>
 * This class contains various useful utility functions.
 */
public class Utils {

    public static boolean inputFieldsNotEmptyOrNull(ArrayList<EditText> editTextArrayList) {
        for (EditText entryField : editTextArrayList) {
            if (TextUtils.isEmpty(entryField.getText().toString().trim())) {
                return false;
            }
        }
        return true;
    }
}
