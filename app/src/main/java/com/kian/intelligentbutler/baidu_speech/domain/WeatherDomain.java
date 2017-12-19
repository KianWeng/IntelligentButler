package com.kian.intelligentbutler.baidu_speech.domain;

import com.google.gson.Gson;
import com.kian.intelligentbutler.api.weather.Weather;
import com.kian.intelligentbutler.api.weather.WeatherParser;
import com.kian.intelligentbutler.baidu_speech.tts.TTSAPIService;
import com.kian.intelligentbutler.baidu_speech.unit.OnResultListener;
import com.kian.intelligentbutler.baidu_speech.unit.UnitError;
import com.kian.intelligentbutler.util.HttpUtil;
import com.kian.intelligentbutler.util.PPLog;

import org.json.JSONObject;


/**
 * Created by Kian on 2017/12/15.
 */

public class WeatherDomain {
    private static final String TAG = "WeatherDomain";
    private static final String BASE_WEATHER_URL = "https://free-api.heweather.com/s6/weather?";
    private DomainObjects objects;
    private CommonDomain commonDomain;
    private TTSAPIService myTTSAPIService;

    public WeatherDomain(CommonDomain commonDomain){
        this.commonDomain = commonDomain;
    }

    public class DomainObjects{
        public String date;
        public String region;
        public String weather;
        public String wind;
        public String temp;
        public String focus;
    }

    public DomainObjects getObjects(){return this.objects;}
    public void setObjects(DomainObjects objects){this.objects = objects;}

    public void action(WeatherDomain weatherDomain){
        StringBuilder url = new StringBuilder(BASE_WEATHER_URL);
        WeatherParser weatherParser = new WeatherParser();

        this.objects = jsonParser(commonDomain.getObject());
        if (weatherDomain.objects.region == null) {
            weatherDomain.objects.region = "北京";
        }
        url.append("location=" + weatherDomain.objects.region).append("&key=58cf5e5e46df41cbbb2a59d83860e76c");

        HttpUtil.getInstance().get(url.toString(),weatherParser, new OnResultListener<Weather>(){
            @Override
            public void onResult(Weather result) {
                handleResponse(result);
            }

            @Override
            public void onError(UnitError error) {
                PPLog.i(TAG,"httputil error");
            }

        });

    }

    private void handleResponse(Weather result){
        if(result.daily_forecast != null) {
            PPLog.i(TAG, result.daily_forecast.get(0).cond_txt_d);
            myTTSAPIService = TTSAPIService.getInstance();
            myTTSAPIService.speak("今天的天气是" + result.daily_forecast.get(0).cond_txt_d);
        }
    }

    private DomainObjects jsonParser(JSONObject jsonObject){
        String content = jsonObject.toString();
        Gson gson = new Gson();
        DomainObjects objects = gson.fromJson(content,DomainObjects.class);
        return objects;
    }
}
