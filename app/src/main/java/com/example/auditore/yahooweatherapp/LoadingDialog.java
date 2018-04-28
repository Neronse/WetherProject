package com.example.auditore.yahooweatherapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.ProgressBar;

public class LoadingDialog extends DialogFragment {

    public static LoadingDialog newInstance() {
        return new LoadingDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setView(new ProgressBar(getActivity()));
        adb.setMessage("Loading...");

        return adb.create();
    }
}
