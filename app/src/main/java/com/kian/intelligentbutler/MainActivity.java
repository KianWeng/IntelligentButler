package com.kian.intelligentbutler;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baidu.speech.asr.SpeechConstant;
import com.kian.intelligentbutler.api.weather.Weather;
import com.kian.intelligentbutler.baidu_speech.BaiduRecognizer;
import com.kian.intelligentbutler.baidu_speech.BaiduUnit;
import com.kian.intelligentbutler.baidu_speech.BaiduWakeup;
import com.kian.intelligentbutler.baidu_speech.recognization.params.CommonRecogParams;
import com.kian.intelligentbutler.baidu_speech.IStatus;
import com.kian.intelligentbutler.baidu_speech.recognization.StatusRecogListener;
import com.kian.intelligentbutler.baidu_speech.recognization.params.AllRecogParams;
import com.kian.intelligentbutler.baidu_speech.recognization.params.OfflineRecogParams;
import com.kian.intelligentbutler.baidu_speech.tts.TTSAPIService;
import com.kian.intelligentbutler.baidu_speech.wakeup.IWakeupListener;
import com.kian.intelligentbutler.baidu_speech.wakeup.RecogWakeupListener;
import com.kian.intelligentbutler.baidu_speech.wakeup.WakeupParams;
import com.kian.intelligentbutler.mqtt.MQTTService;
import com.kian.intelligentbutler.ui.LineWaveVoiceView;
import com.kian.intelligentbutler.ui.MyAdapter;
import com.kian.intelligentbutler.ui.NoScrollViewPager;
import com.kian.intelligentbutler.ui.RecognizerView;
import com.kian.intelligentbutler.ui.weather.WeatherView;
import com.kian.intelligentbutler.util.PPLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements RecognizerView.IRecordAudioListener,IStatus{

    private static final String TAG = "MainActivity";
    private GifImageView gifView;
    private LineWaveVoiceView mHorVoiceView;
    private RecognizerView recordAudioView;
    private TextView tvRecordTips;
    private NoScrollViewPager noScrollViewPager;
    private List<View> viewLists = new ArrayList<View>();
    private MyAdapter myAdapter;
    protected BaiduRecognizer myRecognizer;
    protected BaiduWakeup myWakeup;
    protected BaiduUnit myUnit;
    protected TTSAPIService myTTSAPIService;
    private WeatherView weatherView;
    protected int status;
    protected boolean enableOffline = false;
    protected CommonRecogParams apiParams;
    public static Handler handler;
    private static boolean audioPermission = false;
    /**
     *  0: 方案1， 唤醒词说完后，直接接句子，中间没有停顿。
     * >0 : 方案2： 唤醒词说完后，中间有停顿，然后接句子。推荐4个字 1500ms
     *
     *  backTrackInMs 最大 15000，即15s
     */
    private int backTrackInMs = 1500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMsg(msg);
            }

        };

        initView();
        initPermission();
        initBaiduSpeech();

        //启动MQTT服务
        startService(new Intent(this, MQTTService.class));
    }

    /**
     * 初始化主页面UI
     */
    private void initView(){
        gifView = (GifImageView) findViewById(R.id.gif1);
        mHorVoiceView = (LineWaveVoiceView) findViewById(R.id.horvoiceview);
        recordAudioView = (RecognizerView) findViewById(R.id.iv_recording);
        tvRecordTips = (TextView) findViewById(R.id.record_tips);

        //初始化gif视图
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.mipmap.cotana02);
            gifView.setImageDrawable(gifDrawable);
        }catch (Exception e){
            e.printStackTrace();
        }

        //初始化主页面中的viewpager
        noScrollViewPager = (NoScrollViewPager) findViewById(R.id.noscrollviewpager);
        viewLists.add(getLayoutInflater().inflate(R.layout.welcome, null));
        viewLists.add(getLayoutInflater().inflate(R.layout.weather, null));
        viewLists.add(getLayoutInflater().inflate(R.layout.monitor, null));
        myAdapter = new MyAdapter(viewLists);
        noScrollViewPager.setAdapter(myAdapter);
        noScrollViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                PPLog.i(TAG,"current view position is " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        noScrollViewPager.setCurrentItem(0,true);

        recordAudioView.setRecordAudioListener(this);
    }

    /**
     *初始化百度语音平台的参数
     */
    public void initParams(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(SpeechConstant.IN_FILE); // infile参数用于控制识别一个PCM音频流（或文件），每次进入程序都将该值清除，以避免体验时没有使用录音的问题
        editor.putBoolean("_nlu_online",true);
        editor.putString("_model","search");
        editor.putString("_language","cmn-Hans-CN");
        editor.commit();
    }
    /**
     * 在onCreate中调用。初始化百度语音的相关功能
     */
    protected void initBaiduSpeech() {
        //初始化语音识别、唤醒的相关参数
        initParams();
        //初始化语音识别类
        StatusRecogListener listener = new StatusRecogListener(handler);
        listener.setVoiceView(mHorVoiceView);
        myRecognizer = new BaiduRecognizer(this, listener);
        apiParams = getApiParams();
        status = STATUS_NONE;
        if (enableOffline) {
            myRecognizer.loadOfflineEngine(OfflineRecogParams.fetchOfflineParams());
        }
        //初始化语音唤醒类
        IWakeupListener wakeupListener = new RecogWakeupListener(handler);
        myWakeup = new BaiduWakeup(this, wakeupListener);
        //初始化百度UNIT平台
        myUnit = new BaiduUnit(this);
        myUnit.initAccessToken();
    }

    protected CommonRecogParams getApiParams() {
        return new AllRecogParams(this);
    }

    /**
     * 开始语音唤醒服务
     */
    private void startWakeup(){
        WakeupParams wakeupParams = new WakeupParams(this);
        Map<String,Object> params = wakeupParams.fetch();
        myWakeup.start(params);
    }

    /**
     * 在主线程里处理各个模块发送的消息
     * @param msg
     */
    protected  void handleMsg(Message msg){

        switch (msg.what) {
            case TYPE_RECOG:
                handleRecogMessage(msg);
                break;
            case TYPE_WAKEUP:
                handleWakeupMessage(msg);
                break;
            case TYPE_TTS:
                handleTTSMessage(msg);
                break;
            case TYPE_UPDATE_UI:
                updateViewPager(msg.arg1,msg.obj);
                break;
            default:
                break;
        }

    }

    private void handleTTSMessage(Message msg){
        switch (msg.arg1){
            case STATUS_TTS_INIT_SUCCESS:
                myTTSAPIService.speak(getString(R.string.welcome).toString());
                break;
            default:
                break;
        }
    }
    private void handleRecogMessage(Message msg){
        switch (msg.arg1){
            case STATUS_NONE:
                mHorVoiceView.stopRecord();
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
            case STATUS_FINISHED:
                status = msg.arg1;
                PPLog.i(TAG, msg.obj.toString());
                break;
        }
    }

    private void handleWakeupMessage(Message msg){
        switch (msg.arg1){
            case STATUS_WAKEUP_SUCCESS:
                onRecognizeStart();
                break;
            default:
                break;
        }
    }

    private void updateViewPager(int viewPagerID,Object object){
        PPLog.i(TAG,"view pager ID is " + viewPagerID);
        switch (viewPagerID){
            case 0:
                noScrollViewPager.setCurrentItem(0,true);
                break;
            case 1:
                Weather weather = (Weather)object;
                if(weatherView == null) {
                    weatherView = new WeatherView(this, noScrollViewPager.findViewWithTag(WEATHER_VIEWPAGER_ID));
                    weatherView.init();
                }
                weatherView.updateData(weather);
                noScrollViewPager.setCurrentItem(1,true);
                break;
            case 2:
                noScrollViewPager.setCurrentItem(2,true);
                break;
            default:
                break;
        }
    }

    @Override
    public  void onRecognizeStart(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, Object> params = apiParams.fetch(sp);
        //开始识别
        if(status == STATUS_NONE){
            myRecognizer.start(params);
            status = STATUS_WAITING_READY;
        }
        //声音波纹开始
        mHorVoiceView.setVisibility(View.VISIBLE);
        mHorVoiceView.startRecord();
    }

    @Override
    public void onRecognizeStop(){
        switch(status){
            case STATUS_WAITING_READY: // 调用本类的start方法后，即输入START事件后，等待引擎准备完毕。
            case STATUS_READY: // 引擎准备完毕。
            case STATUS_SPEAKING:
            case STATUS_FINISHED:// 长语音情况
            case STATUS_RECOGNITION:
                myRecognizer.stop();
                status = STATUS_STOPPED; // 引擎识别中
                break;
            case STATUS_STOPPED:
                myRecognizer.cancel();
                status = STATUS_NONE;
                break;
            default:
                break;
        }
        //mHorVoiceView.stopRecord();
    }

    @Override
    protected void onResume(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(audioPermission) {
                    PPLog.i(TAG,"已经获取到音频权限，启动语音唤醒功能");
                    //打开语音唤醒服务
                    startWakeup();
                    //打开语音合成服务
                    myTTSAPIService = TTSAPIService.getInstance();
                }else {
                    PPLog.i(TAG,"未获取到音频权限！");
                }
            }
        },1500);
        PPLog.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onStop(){
        myWakeup.stop();
        myRecognizer.stop();
        myTTSAPIService.stop();
        PPLog.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        myRecognizer.release();
        myWakeup.release();
        myTTSAPIService.release();
        PPLog.i(TAG, "onDestory");
        super.onDestroy();
    }
    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm :permissions){
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.
                PPLog.i(TAG,perm + " is not granted");
                if(perm.equals("android.permission.RECORD_AUDIO")){
                    audioPermission = false;
                }
            }else{
                if(perm.equals("android.permission.RECORD_AUDIO")){
                    audioPermission = true;
                }
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()){
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
        int i = permissions.length;
        int j = 0;
        for(j = 0; j < i; j++) {
            PPLog.i(TAG, "requestCode:" + requestCode + " permissions:" + permissions[j] + " grantResults:" + grantResults[j]);
            if (permissions[j].equals("android.permission.RECORD_AUDIO") && grantResults[j] == 0) {
               if(audioPermission == false){
                   audioPermission = true;
                   PPLog.i(TAG,"已经获取到音频权限，启动语音唤醒功能");
                   //打开语音唤醒服务
                   startWakeup();
                   //打开语音合成服务
                   myTTSAPIService = TTSAPIService.getInstance();
               }

            }
        }
    }

}
