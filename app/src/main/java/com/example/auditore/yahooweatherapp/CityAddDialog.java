package com.example.auditore.yahooweatherapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class CityAddDialog extends DialogFragment {

    public static CityAddDialog newInstance() {

        Bundle args = new Bundle();

        CityAddDialog fragment = new CityAddDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public interface CityAddListener {
        void onCityAddListener(String cityToAdd);
    }

    private CityAddListener cityAddListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialog_add = inflater.inflate(R.layout.dialog_adding_city,null);
        EditText inputText = dialog_add.findViewById(R.id.cityEditText);

        adb.setView(dialog_add)
                .setPositiveButton("Ok", (dialog, which) -> {
                    cityAddListener = (CityAddListener) activity;
                    String result = inputText.getText().toString();
                    if(!result.isEmpty()){
                    cityAddListener.onCityAddListener(result);}
                    dialog.dismiss();
                    
                })
                .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));
        return adb.create();
    }
}
