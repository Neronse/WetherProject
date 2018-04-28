package com.example.auditore.yahooweatherapp.data;

import org.json.JSONObject;

public class Atmosphere implements JSONPopulator {
    //TODO: fix units of pressure, 33k mb pressure it'not normal
    private  double pressure;
    private int humidity;

    public double getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    @Override
    public void getJSONdata(JSONObject data) {
        pressure = data.optDouble("pressure");
        humidity = data.optInt("humidity");

    }
}
