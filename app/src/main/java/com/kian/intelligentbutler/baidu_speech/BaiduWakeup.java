package com.kian.intelligentbutler.baidu_speech;

import android.content.Context;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.kian.intelligentbutler.baidu_speech.wakeup.IWakeupListener;
import com.kian.intelligentbutler.baidu_speech.wakeup.WakeupEventAdapter;
import com.kian.intelligentbutler.util.PPLog;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Kian on 2017/12/11.
 */

public class BaiduWakeup {

    private static boolean isInited = false;

    private EventManager wp;
    private EventListener eventListener;

    private static final String TAG = "MyWakeup";

    public BaiduWakeup(Context context, EventListener eventListener) {
        if (isInited) {
            PPLog.e(TAG,"还未调用release()，请勿新建一个新类");
            throw new RuntimeException("还未调用release()，请勿新建一个新类");
        }
        isInited = true;
        this.eventListener = eventListener;
        wp = EventManagerFactory.create(context, "wp");
        wp.registerListener(eventListener);
    }

    public BaiduWakeup(Context context, IWakeupListener eventListener) {
        this(context,new WakeupEventAdapter(eventListener));
    }
    public void start(Map<String, Object> params) {
        String json = new JSONObject(params).toString();
        PPLog.i(TAG + ".Debug", "wakeup params(反馈请带上此行日志):" + json);
        wp.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
    }

    public void stop() {
        PPLog.i(TAG,"唤醒结束");
        wp.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
    }

    public void release(){
        stop();
        wp.unregisterListener(eventListener);
        wp = null;
        isInited  =false;
    }
}
