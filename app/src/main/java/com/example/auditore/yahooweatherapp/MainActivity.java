package com.example.auditore.yahooweatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.auditore.yahooweatherapp.data.Channel;
import com.example.auditore.yahooweatherapp.data.Item;
import com.example.auditore.yahooweatherapp.service.WeatherServiceCallback;
import com.example.auditore.yahooweatherapp.service.YahooWeatherService;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements WeatherServiceCallback, CityChooseDialog.CityChooseListener, CityAddDialog.CityAddListener {

    private ImageView weatherIcon;
    private TextView temperatureTv;
    private TextView pressureTv;
    private TextView humidityTv;
    private TextView locationTv;
    private TextView lastUpdateTv;
    private TextView tvDescription;
    private final String[] typeTmp = new String[]{"c", "f"};
    private MaterialSearchView searchView;
    private SharedPreferences sPref;
    private int resourceImageId;
    private final int COUNT_STANDART_CITY = 3;

    private YahooWeatherService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        weatherIcon = findViewById(R.id.weatherType);
        temperatureTv = findViewById(R.id.tvTempreture);
        pressureTv = findViewById(R.id.tvPressure);
        humidityTv = findViewById(R.id.tvHumidity);
        locationTv = findViewById(R.id.tvCity);
        lastUpdateTv = findViewById(R.id.tvLastUpdate);
        tvDescription = findViewById(R.id.tvDescription);
        searchView = findViewById(R.id.search_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        service = new YahooWeatherService(this, getFragmentManager(), this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Temperature now is in " + temperature(service.getTmpType()), Snackbar.LENGTH_SHORT)
                .setAction("Switch", v -> {
                    if (service.getTmpType().equals(typeTmp[0])) service.setTmpType(typeTmp[1]);
                    else service.setTmpType(typeTmp[0]);
                    if (isNetworkReady()) {
                        service.refreshWeather();
                    } else
                        Toast.makeText(this, "Need internet connection", Toast.LENGTH_SHORT).show();
                }).show());

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(view -> {
            CityAddDialog cityAddDialog = CityAddDialog.newInstance();
            cityAddDialog.show(getFragmentManager(), "addDialog");
        });

        loadData();

        if (isNetworkReady()) {
            service.refreshWeather();
        }


        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                service.setLocation(query);
                if (isNetworkReady()) {
                    service.refreshWeather();
                } else
                    Toast.makeText(getApplicationContext(), "Need internet connection", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });
    }

    private String temperature(String s) {
        if (s.equals(typeTmp[0])) return "Celsius";
        else return "Fahrenheit";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle args = new Bundle();
        switch (item.getItemId()) {
            case R.id.change_city: {
                if(service.getListLocations().isEmpty()){
                    Toast.makeText(this, "No cities in memory", Toast.LENGTH_SHORT).show();
                    break;
                }
                args.putString("actionType", "choose");
                args.putStringArrayList("city", service.getListLocations());
                CityChooseDialog dialog = CityChooseDialog.newInstance();
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "CityChoose");
                break;
            }
            case R.id.delete_city:
                if(service.getListLocations().isEmpty()){
                    Toast.makeText(this, "No cities in memory", Toast.LENGTH_SHORT).show();
                    break;
                }
                args.putString("actionType", "delete");
                args.putStringArrayList("city", service.getListLocations());
                CityChooseDialog dialog = CityChooseDialog.newInstance();
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "CityDelete");
                break;
        }
        return true;
    }

    @Override
    public void serviceSuccsess(Channel channel) {
        Item item = channel.getItem();
        resourceImageId = getResources().getIdentifier("drawable/icon_" + item.getCondition().getCode(), null, getPackageName());
        Drawable weatherIconDraw = getResources().getDrawable(resourceImageId, null);
        weatherIcon.setImageDrawable(weatherIconDraw);

        String temperature = item.getCondition().getTempreture() + " " + "°" + channel.getUnits().getTemprature();
        temperatureTv.setText(temperature);

        String pressure = "Pressure: " + String.valueOf(channel.getAtmosphere().getPressure()) + " " + channel.getUnits().getPressure();
        pressureTv.setText(pressure);

        String humidity = "Humidity: " + String.valueOf(channel.getAtmosphere().getHumidity()) + "%";
        humidityTv.setText(humidity);

        String lastUpdate = "Last Update: " + item.getCondition().getDate();
        lastUpdateTv.setText(lastUpdate);

        tvDescription.setText(item.getCondition().getDescription());

        String locationText = channel.getLocation().getCity() + ", " + channel.getLocation().getRegion();
        locationTv.setText(locationText);
    }

    @Override
    public void serviceFailure(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListChoose(int which) {
        String loc = service.getListLocations().get(which);
        service.setLocation(loc);
        if (isNetworkReady()) {
            service.refreshWeather();
        } else
            Toast.makeText(getApplicationContext(), "Need internet connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListDelete(int which) {
        service.getListLocations().remove(which);
    }

    private void saveData() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor edit = sPref.edit();
        edit.putInt("weatherResource", resourceImageId);
        edit.putString("temperature", temperatureTv.getText().toString());
        edit.putString("pressure", pressureTv.getText().toString());
        edit.putString("humidity", humidityTv.getText().toString());
        edit.putString("lastUp", lastUpdateTv.getText().toString());
        edit.putString("description", tvDescription.getText().toString());
        edit.putString("location", locationTv.getText().toString());
        edit.putString("tmpType", service.getTmpType());

        ArrayList<String> cityList = service.getListLocations();
        int size = cityList.size();
        if (size < COUNT_STANDART_CITY) return;
        edit.putInt("listSize", size);
        for (int i = COUNT_STANDART_CITY; i < cityList.size(); i++) {
            edit.putString(String.valueOf(i), cityList.get(i));
        }
        edit.apply();
    }

    private void loadData() {
        sPref = getPreferences(MODE_PRIVATE);
        String location = sPref.getString("location", "Moscow");
        service.setLocation(location.split(",")[0]);
        String tmpType = sPref.getString("tmpType", "c");
        service.setTmpType(tmpType);

        ArrayList<String> cityList = new ArrayList<>();
        int size = sPref.getInt("listSize", 0);
        if (size > 0) {
            for (int i = COUNT_STANDART_CITY; i < size; i++) {
                cityList.add(sPref.getString(String.valueOf(i), null));
            }
            service.getListLocations().addAll(cityList);
        }
        if (!isNetworkReady()) {
            String temperature = sPref.getString("temperature", "TEMP");
            temperatureTv.setText(temperature);

            String pressure = sPref.getString("pressure", "Pressure: N/A");
            pressureTv.setText(pressure);

            String humidity = sPref.getString("humidity", "Humidity: N/A");
            humidityTv.setText(humidity);

            String lastUpdate = sPref.getString("lastUp", "Last Update: N/A");
            lastUpdateTv.setText(lastUpdate);

            String description = sPref.getString("description", "Description N/A");
            tvDescription.setText(description);

            location = sPref.getString("location", "No Location");
            locationTv.setText(location);

            resourceImageId = sPref.getInt("weatherResource", getResources().getIdentifier("drawable/icon_3200", null, getPackageName()));
            weatherIcon.setImageDrawable(getResources().getDrawable(resourceImageId, null));
        }
    }

    private boolean isNetworkReady() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }

    @Override
    public void onCityAddListener(String cityToAdd) {
        if (isNetworkReady()) {
            service.setNeedToAdd(true);
            service.setLocation(cityToAdd);
            service.refreshWeather();
        } else
            Toast.makeText(getApplicationContext(), "Need internet connection", Toast.LENGTH_SHORT).show();
    }




}
