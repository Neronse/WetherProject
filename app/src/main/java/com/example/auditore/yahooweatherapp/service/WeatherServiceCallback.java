package com.example.auditore.yahooweatherapp.service;

import com.example.auditore.yahooweatherapp.data.Channel;

public interface WeatherServiceCallback {
    void serviceSuccsess(Channel channel);
    void serviceFailure (Exception e);
}
