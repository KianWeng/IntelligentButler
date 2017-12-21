package com.kian.intelligentbutler.baidu_speech.domain;

import android.os.Handler;

import com.google.gson.Gson;
import com.kian.intelligentbutler.MainActivity;
import com.kian.intelligentbutler.util.PPLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * Created by YYTD on 2017/12/18.
 */

public class DomainService implements IDomain {
    private static final String TAG = "DomainService";
    private String domainName;
    private static DomainService instance;
    private Handler handler;

    private DomainService() {
        this.handler = MainActivity.handler;
    }

    public static DomainService getInstance() {
        synchronized (DomainService.class) {
            if (instance == null) {
                instance = new DomainService();
            }
        }
        return instance;
    }

    public void handleNluResult(String nluResult){
        try {
            JSONObject jsonObject = new JSONObject(nluResult);
            jsonObject = new JSONObject(jsonObject.getString("merged_res"));
            jsonObject = new JSONObject(jsonObject.getString("semantic_form"));
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            JSONObject object = new JSONObject(jsonArray.getJSONObject(0).getString("object"));
            String resultsContent = jsonArray.getJSONObject(0).toString();
            Gson gson = new Gson();
            CommonDomain commonDomain = gson.fromJson(resultsContent,CommonDomain.class);
            commonDomain.setObject(object);
            //PPLog.i(TAG,commonDomain.getName() + commonDomain.getIntent() + commonDomain.getScore()+commonDomain.getObject().getString("date"));

            if (commonDomain.getName().equals("weather")){
                PPLog.i(TAG,"start weather action");
                WeatherDomain weatherDomain = new WeatherDomain(commonDomain,handler);
                weatherDomain.action(weatherDomain);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
