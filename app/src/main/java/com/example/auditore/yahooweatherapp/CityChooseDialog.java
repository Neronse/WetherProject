package com.example.auditore.yahooweatherapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.ArrayAdapter;


import java.util.ArrayList;

public class CityChooseDialog extends DialogFragment {

    public interface CityChooseListener{
        void onListChoose(int which);
        void onListDelete(int which);
    }

     public static CityChooseDialog newInstance() {
         return new CityChooseDialog();
    }

    CityChooseListener cityListener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());

        Bundle args = this.getArguments();
        String actionType = args.getString("actionType");

            ArrayList<String> arrayList = args.getStringArrayList("city");

            adb.setTitle("Выберите город");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, arrayList);
            adb.setAdapter(adapter, (dialog, which) -> {
                cityListener = (CityChooseListener) getActivity();
                if(actionType != null && actionType.equals("choose")){
                cityListener.onListChoose(which);
                }else if(actionType != null && actionType.equals("delete")){
                    cityListener.onListDelete(which);
                }
                dialog.cancel();
            });
            return adb.create();

    }
}
