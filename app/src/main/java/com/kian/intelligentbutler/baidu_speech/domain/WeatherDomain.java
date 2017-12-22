package com.kian.intelligentbutler.baidu_speech.domain;

import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.kian.intelligentbutler.api.weather.Weather;
import com.kian.intelligentbutler.api.weather.WeatherParser;
import com.kian.intelligentbutler.baidu_speech.IStatus;
import com.kian.intelligentbutler.baidu_speech.tts.TTSAPIService;
import com.kian.intelligentbutler.baidu_speech.unit.OnResultListener;
import com.kian.intelligentbutler.baidu_speech.unit.UnitError;
import com.kian.intelligentbutler.util.ContextUtil;
import com.kian.intelligentbutler.util.HttpUtil;
import com.kian.intelligentbutler.util.PPLog;

import org.json.JSONObject;


/**
 * Created by Kian on 2017/12/15.
 */

public class WeatherDomain implements IStatus{
    private static final String TAG = "WeatherDomain";
    private static final String BASE_WEATHER_URL = "https://free-api.heweather.com/s6/weather?";
    private DomainObjects objects;
    private CommonDomain commonDomain;
    private TTSAPIService myTTSAPIService;
    private Handler handler;

    public WeatherDomain(CommonDomain commonDomain, Handler handler){
        this.commonDomain = commonDomain;
        this.handler = handler;
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
            weatherDomain.objects.region = "杭州";
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
        if(result.status.equals("ok")) {
            PPLog.i(TAG, result.now.cond_txt);
            myTTSAPIService = TTSAPIService.getInstance();
            myTTSAPIService.speak("为您查询到最近三天的天气结果。");
//            myTTSAPIService.speak(result.basic.location + "今天的天气是" + result.now.cond_txt + ",当前温度"
//            + result.now.tmp + "," + result.now.wind_dir + "风力" + result.now.wind_sc +
//            "风速" + result.now.wind_spd + "公里每小时");
            sendMessage(TYPE_UPDATE_UI, WEATHER_VIEWPAGER_ID, result);
        }
    }

    private DomainObjects jsonParser(JSONObject jsonObject){
        String content = jsonObject.toString();
        Gson gson = new Gson();
        DomainObjects objects = gson.fromJson(content,DomainObjects.class);
        return objects;
    }

    private void sendMessage(int what, int message, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = message;
        msg.obj = obj;
        handler.sendMessage(msg);
    }
}
