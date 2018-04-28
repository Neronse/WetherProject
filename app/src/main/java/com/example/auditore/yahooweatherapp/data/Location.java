package com.example.auditore.yahooweatherapp.data;

import org.json.JSONObject;

public class Location implements JSONPopulator{
    private String city;
    private String country;
    private String region;

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    @Override
    public void getJSONdata(JSONObject data) {
        city = data.optString("city");
        country = data.optString("county");
        region = data.optString("region");

    }
}
