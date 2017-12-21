package com.kian.intelligentbutler.ui.weather;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kian.intelligentbutler.R;
import com.kian.intelligentbutler.api.weather.Weather;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Kian on 2017/12/21.
 */

public class WeatherListViewAdapter extends BaseAdapter{
    private Context context;
    private Weather weather;
    private LayoutInflater mInflater;

    public WeatherListViewAdapter(Context context){
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount(){
        return weather.daily_forecast.size();
    }

    @Override
    public Object getItem(int i){
        return weather.daily_forecast.get(i);
    }

    @Override
    public long getItemId(int i){
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.weather_listview_item, null);
            holder.img = (ImageView)convertView.findViewById(R.id.weather_listview_img);
            holder.date = (TextView)convertView.findViewById(R.id.weather_listview_date);
            holder.type = (TextView)convertView.findViewById(R.id.weather_listview_type);
            holder.condition = (TextView)convertView.findViewById(R.id.weather_listview_condition);
            holder.temp = (TextView)convertView.findViewById(R.id.weather_listview_temp);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.img.setImageResource(getImg(position));
        if(position == 0) {
            holder.date.setText("今天");
        }else if(position == 1){
            holder.date.setText("明天");
        }else {
            holder.date.setText(getWeek(weather.daily_forecast.get(position).date));
        }
        holder.condition.setText(weather.daily_forecast.get(position).wind_dir);
        holder.type.setText(weather.daily_forecast.get(position).cond_txt_d);
        holder.temp.setText(weather.daily_forecast.get(position).tmp_max + "/" + weather.daily_forecast.get(position).tmp_min + "℃");

        return convertView;
    }

    public void setData(Weather weather){
        this.weather = weather;
    }

    private String getWeek(String date){
        String week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(date));
        }catch (ParseException e){
            e.printStackTrace();
        }

        if(c.get(Calendar.DAY_OF_WEEK) == 1){
            week += "周日";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            week += "周一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            week += "周二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            week += "周三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            week += "周四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            week += "周五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            week += "周六";
        }
        return week;
    }

    private int getImg(int position){
        ApplicationInfo appInfo = context.getApplicationInfo();
        String code = weather.daily_forecast.get(position).cond_code_d;
        String name = "hf_" + code;
        int id = 0;
        id = context.getResources().getIdentifier(name,"mipmap", appInfo.packageName);
        return id;
    }

    private class ViewHolder{
        public TextView date;
        public TextView type;
        public TextView condition;
        public TextView temp;
        public ImageView img;
    }
}
