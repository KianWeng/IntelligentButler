package com.kian.intelligentbutler.ui.weather;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kian.intelligentbutler.R;
import com.kian.intelligentbutler.api.weather.Weather;

/**
 * Created by Kian on 2017/12/21.
 */

public class WeatherView {

    private static final String TAG = "WeatherView";
    private Context context;
    private ListView mListView;
    private Weather weather;
    private View weatherView;
    private WeatherListViewAdapter adapter;
    private TextView type;
    private TextView temp;
    private TextView time;
    private TextView wind;
    private TextView location;
    private ImageView img;

    public WeatherView(Context context, View view){
        this.context = context;
        this.weatherView = view;
    }

//    private SimpleAdapter adapter = new SimpleAdapter(context,getData(), R.layout.weather_listview_item,
//            new String[]{"date", "img", "type", "condition", "temp"},
//            new int[]{R.id.weather_listview_date,R.id.weather_listview_img,R.id.weather_listview_type,
//                    R.id.weather_listview_condition,R.id.weather_listview_temp});

    public void init(){
        mListView = (ListView)weatherView.findViewById(R.id.weather_listview);
        adapter = new WeatherListViewAdapter(context);
        temp = (TextView)weatherView.findViewById(R.id.weather_temp);
        type = (TextView)weatherView.findViewById(R.id.weather_type);
        location = (TextView)weatherView.findViewById(R.id.weather_location);
        wind = (TextView)weatherView.findViewById(R.id.weather_wind);
        time = (TextView)weatherView.findViewById(R.id.weather_time);
        img = (ImageView) weatherView.findViewById(R.id.weather_img);
    }

    public void updateData(Weather weather){
        this.weather = weather;
        adapter.setData(weather);
        temp.setText(weather.now.tmp + "℃");
        type.setText(weather.now.cond_txt);
        location.setText(weather.basic.location);
        wind.setText(weather.now.wind_dir);
        time.setText(weather.update.loc);
        img.setImageResource(getImg());
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private int getImg(){
        ApplicationInfo appInfo = context.getApplicationInfo();
        String code = weather.now.cond_code;
        String name = "hf_" + code;
        int id = 0;
        id = context.getResources().getIdentifier(name,"mipmap", appInfo.packageName);
        return id;
    }
}
