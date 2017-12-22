package com.kian.intelligentbutler.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;

import com.kian.intelligentbutler.util.PPLog;

import java.util.List;
import java.util.Locale;

/**
 * Created by Kian on 2017/12/22.
 */

public class LocationService extends Service{

    private static final String TAG = "LocationService";
    private LocationManager locationManager;
    private Location mLocation;
    public static String city = "杭州市";

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PPLog.i(TAG,"启动位置服务！");
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) &&
                PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION")
                && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION")){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5*60*1000, 1000, locationListener);
        }
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        PPLog.i(TAG,"退出位置服务！");
        locationManager.removeUpdates(locationListener);
        locationManager = null;
        super.onDestroy();
    }

    protected final LocationListener locationListener=new LocationListener() {

        // Provider的在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
        //  Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }
        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }
        //当坐标改变时触发此函数
        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            mLocation = location;
            //解除监听
            //locationManager.removeUpdates(locationListener);
            getAddress(mLocation);
        }
    };

    private void getAddress(Location location){
        List<Address> addressList = null;
        try {
            if(location != null){
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                addressList = gc.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(addressList != null) {
            PPLog.i(TAG, "当前定位城市是：" + addressList.get(0).getSubAdminArea());
            city = addressList.get(0).getSubAdminArea();
        }

    }
}
