package com.ranma2913.global;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;

/**
 * Created by jsticha on 5/4/2015.
 * <p/>
 * This TextWatcher class fills digits from the right to the left. It will format the TextView with a dollar symbol and two decimal places.
 */
public class MoneyTextWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;

    public MoneyTextWatcher(EditText editText) {
        editTextWeakReference = new WeakReference<>(editText);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        EditText editText = editTextWeakReference.get();
        if (editText == null) {
            return;
        }
        if (TextUtils.isEmpty(editText.getText())) {
            return;
        }
        editText.removeTextChangedListener(this);
        String formatted = Utils.formatDollarPriceString(editable.toString());
        editText.setText(formatted);
        editText.setSelection(formatted.length());
        editText.addTextChangedListener(this);
    }
}

