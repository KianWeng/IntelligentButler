package com.kian.intelligentbutler.mqtt;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.kian.intelligentbutler.util.ContextUtil;
import com.kian.intelligentbutler.util.PPLog;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by Kian on 2017/12/20.
 */

public class MQTTService {
    private static final String TAG = "MQTTService";

    private static MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    private static MQTTService instance;
    private String host = "tcp://45.32.7.217:1883";
    private String userName = "admin";
    private String passWord = "password";
    private static String myTopic = "android/common";
    private String clientId = "Android_";

    private MQTTService(){
        init();
    }

    public static MQTTService getInstance(){
        synchronized (MQTTService.class){
            if(instance == null){
                instance = new MQTTService();
            }
        }
        return instance;
    }

    public static void publish(String topic,String msg){
        publish(topic, msg, 0, false);
    }

    public static void publish(String topic,String msg, Integer qos, Boolean retained){
        PPLog.i(TAG,"publish message: " + msg + " to topic: " + topic);
        try {
            client.publish(topic, msg.getBytes(), qos.intValue(), retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToTopic(String topic){
        PPLog.i(TAG,"subscribe to topic: " + topic);
        try {
            client.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    PPLog.i(TAG,"Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    PPLog.i(TAG,"Failed to subscribe");
                }
            });
        } catch (MqttException ex){
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    private void init() {
        // 服务器地址（协议+地址+端口号）
        String uri = host;
        clientId = clientId + getIMEI(ContextUtil.getInstance());
        client = new MqttAndroidClient(ContextUtil.getInstance(), uri, clientId);
        // 设置MQTT监听并且接受消息
        client.setCallback(mqttCallback);

        conOpt = new MqttConnectOptions();
        // 清除缓存
        conOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(20);
        //设置自动重连
        conOpt.setAutomaticReconnect(true);
        // 用户名
        conOpt.setUserName(userName);
        // 密码
        conOpt.setPassword(passWord.toCharArray());

        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + clientId + "\"}";
        String topic = myTopic;
        Integer qos = 0;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            // 最后的遗嘱
            try {
                conOpt.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                Log.i(TAG, "Exception Occured", e);
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }

        if (doConnect) {
            doClientConnection();
        }

    }

    /** 连接MQTT服务器 */
    private void doClientConnection() {
        if (!client.isConnected() && isConnectIsNomarl()) {
            try {
                client.connect(conOpt, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }

    // MQTT是否连接成功
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "连接成功 ");
            // 订阅myTopic话题
            subscribeToTopic(myTopic);
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            // 连接失败，重连
            PPLog.i(TAG,"Failed to connect to: " + host);
        }
    };

    // MQTT监听并且接受消息
    private MqttCallbackExtended mqttCallback = new MqttCallbackExtended() {

        @Override
        public void connectComplete(boolean reconnect, String serverURI){
            if (reconnect) {
                // Because Clean Session is true, we need to re-subscribe
                subscribeToTopic(myTopic);
            } else {
                PPLog.i(TAG,"Connected to: " + serverURI);
            }
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String str1 = new String(message.getPayload());
            String str2 = topic + ";qos:" + message.getQos() + ";retained:" + message.isRetained();
            PPLog.i(TAG, "messageArrived:" + str1);
            PPLog.i(TAG, str2);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

        }

        @Override
        public void connectionLost(Throwable arg0) {
            // 失去连接，重连
            PPLog.i(TAG,"The Connection was lost.");
        }
    };

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ContextUtil.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "MQTT当前网络名称：" + name);
            return true;
        } else {
            Log.i(TAG, "MQTT 没有可用网络");
            return false;
        }
    }

    /**
     * 获取手机IMEI号
     *
     * 需要动态权限: android.permission.READ_PHONE_STATE
     */
    public static String getIMEI(Context context) {
        String imei = "0123456789";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context,"android.permission.READ_PHONE_STATE")) {
            imei = telephonyManager.getDeviceId();
        }
        return imei;
    }

    public void release() {
        try {
            client.disconnect();
            instance = null;
            client = null;
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
