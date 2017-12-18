package com.kian.intelligentbutler.api.weather;

import com.google.gson.Gson;
import com.kian.intelligentbutler.baidu_speech.unit.Parser;
import com.kian.intelligentbutler.baidu_speech.unit.UnitError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kian on 2017/12/15.
 */

public class WeatherParser implements Parser<Weather>{
    @Override
    public Weather parse(String json) throws UnitError{
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            Gson gson = new Gson();
            Weather weather = gson.fromJson(weatherContent,Weather.class);

            return weather;
        }catch (JSONException e){
            e.printStackTrace();
            UnitError error = new UnitError(UnitError.ErrorCode.JSON_PARSE_ERROR, "Json parse error:" + json, e);
            throw error;
        }
    }

}
