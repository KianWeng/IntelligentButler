package com.kian.intelligentbutler;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ant.liao.GifView;
import com.baidu.speech.asr.SpeechConstant;
import com.kian.intelligentbutler.baidu_speech.BaiduRecognizer;
import com.kian.intelligentbutler.baidu_speech.BaiduWakeup;
import com.kian.intelligentbutler.baidu_speech.recognization.PidBuilder;
import com.kian.intelligentbutler.baidu_speech.recognization.params.CommonRecogParams;
import com.kian.intelligentbutler.baidu_speech.recognization.IStatus;
import com.kian.intelligentbutler.baidu_speech.recognization.StatusRecogListener;
import com.kian.intelligentbutler.baidu_speech.recognization.params.AllRecogParams;
import com.kian.intelligentbutler.baidu_speech.recognization.params.NluRecogParams;
import com.kian.intelligentbutler.baidu_speech.recognization.params.OfflineRecogParams;
import com.kian.intelligentbutler.baidu_speech.wakeup.IWakeupListener;
import com.kian.intelligentbutler.baidu_speech.wakeup.RecogWakeupListener;
import com.kian.intelligentbutler.baidu_speech.wakeup.WakeupParams;
import com.kian.intelligentbutler.ui.LineWaveVoiceView;
import com.kian.intelligentbutler.ui.RecognizerView;
import com.kian.intelligentbutler.util.PPLog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements RecognizerView.IRecordAudioListener,IStatus{

    private static final String TAG = "MainActivity";
    private GifView gifView;
    private LineWaveVoiceView mHorVoiceView;
    private RecognizerView recordAudioView;
    private TextView tvRecordTips;
    protected BaiduRecognizer myRecognizer;
    protected BaiduWakeup myWakeup;
    protected int status;
    protected boolean enableOffline = false;
    protected CommonRecogParams apiParams;
    private Handler handler;
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

        initParams();
        initView();
        initPermission();
        initRecog();
        startWakeup();
    }

    private void startWakeup(){
        WakeupParams wakeupParams = new WakeupParams(this);
        Map<String,Object> params = wakeupParams.fetch();
        myWakeup.start(params);
    }

    private void initView(){
        gifView = (GifView) findViewById(R.id.gif1);
        mHorVoiceView = (LineWaveVoiceView) findViewById(R.id.horvoiceview);
        recordAudioView = (RecognizerView) findViewById(R.id.iv_recording);
        tvRecordTips = (TextView) findViewById(R.id.record_tips);

        //init gif view
        gifView.setGifImage(R.mipmap.cotana02);
        gifView.setShowDimension(300,300);
        gifView.setGifImageType(GifView.GifImageType.COVER);

        recordAudioView.setRecordAudioListener(this);
    }

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
     * 在onCreate中调用。初始化识别控制类MyRecognizer
     */
    protected void initRecog() {
        //init recognization
        StatusRecogListener listener = new StatusRecogListener(handler);
        listener.setVoiceView(mHorVoiceView);
        myRecognizer = new BaiduRecognizer(this, listener);
        apiParams = getApiParams();
        status = STATUS_NONE;
        if (enableOffline) {
            myRecognizer.loadOfflineEngine(OfflineRecogParams.fetchOfflineParams());
        }

        //init wakeup
        IWakeupListener wakeupListener = new RecogWakeupListener(handler);
        myWakeup = new BaiduWakeup(this, wakeupListener);
    }

    protected CommonRecogParams getApiParams() {
        return new AllRecogParams(this);
    }

    protected  void handleMsg(Message msg){

        switch (msg.what) { // 处理StatusRecogListener中的状态回调
            case STATUS_FINISHED:
                PPLog.i(TAG, msg.obj.toString());
                //故意不写break
            case STATUS_NONE:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                status = msg.what;
                break;
            case STATUS_WAKEUP_SUCCESS:
                Map<String, Object> params = new LinkedHashMap<String, Object>();
                params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
                params.put(SpeechConstant.VAD,SpeechConstant.VAD_DNN);
                int pid = PidBuilder.create().model(PidBuilder.INPUT).toPId(); //如识别短句，不需要需要逗号，将PidBuilder.INPUT改为搜索模型PidBuilder.SEARCH
                params.put(SpeechConstant.PID,pid);
                if (backTrackInMs > 0) { // 方案1， 唤醒词说完后，直接接句子，中间没有停顿。
                    params.put(SpeechConstant.AUDIO_MILLS, System.currentTimeMillis() - backTrackInMs);

                }
                myRecognizer.cancel();
                myRecognizer.start(params);
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
        mHorVoiceView.stopRecord();
    }

    @Override
    protected void onDestroy() {
        myRecognizer.release();
        myWakeup.release();
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
    }

}
