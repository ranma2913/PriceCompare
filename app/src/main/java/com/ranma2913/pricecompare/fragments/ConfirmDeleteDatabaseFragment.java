package com.ranma2913.pricecompare.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.ranma2913.pricecompare.R;

/**
 * Created by jsticha on 8/10/2015.
 */
public class ConfirmDeleteDatabaseFragment extends DialogFragment {

    final String TAG = ConfirmDeleteDatabaseFragment.class.getSimpleName();
    ConfirmDeleteDatabaseDialogListener confirmDialogListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            confirmDialogListener = (ConfirmDeleteDatabaseDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ConfirmDeleteDatabaseDialogListener");
        }
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_database)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG + "@onCreateDialog", "Confirm button Clicked");
                        confirmDialogListener.onDeleteDatabasePositiveClick(ConfirmDeleteDatabaseFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG + "@onCreateDialog", "Cancel button Clicked");
                        confirmDialogListener.onDeleteDatabaseNegativeClick(ConfirmDeleteDatabaseFragment.this);
                    }
                });
        return builder.create();
    }

    public interface ConfirmDeleteDatabaseDialogListener {
        void onDeleteDatabasePositiveClick(DialogFragment dialog);

        void onDeleteDatabaseNegativeClick(DialogFragment dialog);
    }

}
