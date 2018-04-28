package com.example.auditore.yahooweatherapp.data;

import org.json.JSONObject;

public class Units implements JSONPopulator {
    private String temprature;
    private String pressure;


    public String getTemprature() {
        return temprature;
    }

    @Override
    public void getJSONdata(JSONObject data) {

        temprature = data.optString("temperature");
        pressure = data.optString("pressure");
    }

    public String getPressure() {
        return pressure;
    }
}
