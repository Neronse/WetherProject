package com.example.auditore.yahooweatherapp.data;

import org.json.JSONObject;

import java.io.Serializable;

public class Channel implements JSONPopulator, Serializable{
    private Item item;
    private Units units;
    private Atmosphere atmosphere;
    private Location location;

    public Item getItem() {
        return item;
    }

    public Units getUnits() {
        return units;
    }

    public Atmosphere getAtmosphere() {
        return atmosphere;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public void getJSONdata(JSONObject data) {
        units = new Units();
        units.getJSONdata(data.optJSONObject("units"));

        item = new Item();
        item.getJSONdata(data.optJSONObject("item"));

        atmosphere = new Atmosphere();
        atmosphere.getJSONdata(data.optJSONObject("atmosphere"));

        location = new Location();
        location.getJSONdata(data.optJSONObject("location"));

    }
}
