package com.example.auditore.yahooweatherapp.data;

import org.json.JSONObject;

public class Condition implements JSONPopulator {
    private int code;
    private int tempreture;
    private String description;
    private String date;

    public int getCode() {
        return code;
    }

    public String getDate() {
        return date;
    }

    public int getTempreture() {
        return tempreture;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public void getJSONdata(JSONObject data) {
        code = data.optInt("code");
        tempreture = data.optInt("temp");
        description = data.optString("text");
        date = data.optString("date");

    }
}
