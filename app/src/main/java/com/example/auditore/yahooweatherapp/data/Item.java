package com.example.auditore.yahooweatherapp.data;

import org.json.JSONObject;

public class Item implements JSONPopulator {
    private Condition condition;

    public Condition getCondition() {
        return condition;
    }

    @Override

    public void getJSONdata(JSONObject data) {
        condition = new Condition();
        condition.getJSONdata(data.optJSONObject("condition"));
    }
}
