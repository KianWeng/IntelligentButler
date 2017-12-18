package com.kian.intelligentbutler.api.weather;

import java.util.List;

/**
 * Created by Kian on 2017/12/15.
 */

public class Weather {
    public Basic basic;
    public Update update;
    public List<DailyForecast> daily_forecast;
    public Now now;
    public String status;
    public List<LifeStyle> lifeStyles;
}
