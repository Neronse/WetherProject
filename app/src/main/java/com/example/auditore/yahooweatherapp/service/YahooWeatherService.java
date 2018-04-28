package com.example.auditore.yahooweatherapp.service;

import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


import com.example.auditore.yahooweatherapp.LoadingDialog;
import com.example.auditore.yahooweatherapp.R;
import com.example.auditore.yahooweatherapp.data.Channel;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;


public class YahooWeatherService {
    private WeatherServiceCallback callback;
    private ArrayList<String> listLocations = new ArrayList<>();
    private String location;
    private Exception error;
    private String tmpType;
    private FragmentManager fm;
    private LoadingDialog loadingDialog;
    private boolean needToAdd = false;
    private final String LOG_TAG = "myLogs";

    public void setNeedToAdd(boolean needToAdd) {
        this.needToAdd = needToAdd;
    }

    public ArrayList<String> getListLocations() {
        return listLocations;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTmpType() {
        return tmpType;
    }

    public void setTmpType(String tmpType) {
        this.tmpType = tmpType;
    }

    public YahooWeatherService(WeatherServiceCallback callback, FragmentManager fm, Context context) {
        this.callback = callback;
        Collections.addAll(listLocations, context.getResources().getStringArray(R.array.base_city));
        location = listLocations.get(0);
        this.fm = fm;
        this.loadingDialog = LoadingDialog.newInstance();
    }

    public void refreshWeather() {
        new refreshClass().execute(location);
    }


    class refreshClass extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            loadingDialog.show(fm, "LoadingDialog");
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && error != null) {
                callback.serviceFailure(error);
                loadingDialog.dismissAllowingStateLoss();
                return;
            }
            try {
                JSONObject data = new JSONObject(s);
                JSONObject queryResult = data.optJSONObject("query");
                int count = queryResult.optInt("count");
                if (count == 0) {
                    callback.serviceFailure(new LocationWeatherException("No weather info found for " + location));
                    return;
                }
                Channel channel = new Channel();
                channel.getJSONdata(queryResult.optJSONObject("results").optJSONObject("channel"));
                if (needToAdd) {
                    String city = channel.getLocation().getCity();
                    if (!listLocations.contains(city.toUpperCase())) {
                        listLocations.add(city.toUpperCase());
                    }
                    needToAdd = false;
                }
                callback.serviceSuccsess(channel);
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                loadingDialog.dismissAllowingStateLoss();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\") and u='%s'", strings[0], tmpType);

            String endpoint = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json", Uri.encode(YQL));

            try {
                URL url = new URL(endpoint);

                URLConnection connection = url.openConnection();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                inputStream.close();

                return sb.toString();
            } catch (IOException e) {
                error = e;
                return null;
            }
        }
    }

    public class LocationWeatherException extends Exception {
        LocationWeatherException(String message) {
            super(message);
        }
    }
}

