package com.kian.intelligentbutler.baidu_speech;

import android.content.Context;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.kian.intelligentbutler.baidu_speech.recognization.IRecogListener;
import com.kian.intelligentbutler.baidu_speech.recognization.RecogEventAdapter;
import com.kian.intelligentbutler.util.PPLog;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Kian on 2017/12/11.
 */

public class BaiduRecognizer {
    /**
     * SDK 内部核心 EventManager 类
     */
    private EventManager asr;

    /**
     * SDK 内部核心 事件回调类， 用于开发者写自己的识别回调逻辑
     */
    private EventListener eventListener;

    private static boolean isOfflineEngineLoaded = false;

    private static boolean isInited = false;

    private static final String TAG = "BaiduRecognizer";

    /**
     *  初始化
     * @param context
     * @param IRecogListener 将EventListener结果做解析的DEMO回调。使用RecogEventAdapter 适配EventListener
     */
    public BaiduRecognizer(Context context, IRecogListener IRecogListener) {
        this(context, new RecogEventAdapter(IRecogListener));
    }

    /**
     * 初始化 提供 EventManagerFactory需要的Context和EventListener
     * @param context
     * @param eventListener
     */
    public BaiduRecognizer(Context context, EventListener eventListener) {
        if (isInited) {
            PPLog.e(TAG, "还未调用release()，请勿新建一个新类");
            throw new RuntimeException("还未调用release()，请勿新建一个新类");
        }
        isInited = true;
        this.eventListener = eventListener;
        asr = EventManagerFactory.create(context, "asr");
        asr.registerListener(eventListener);
    }


    /**
     *
     * @param params
     */
    public void loadOfflineEngine(Map<String, Object> params) {
        String json = new JSONObject(params).toString();
        PPLog.i(TAG + ".Debug", "loadOfflineEngine params:" + json);
        asr.send(SpeechConstant.ASR_KWS_LOAD_ENGINE, json, null, 0, 0);
        isOfflineEngineLoaded = true;
        // 没有ASR_KWS_LOAD_ENGINE这个回调表试失败，如缺少第一次联网时下载的正式授权文件。
    }

    public void start(Map<String, Object> params) {
        String json = new JSONObject(params).toString();
        PPLog.i(TAG + ".Debug", "asr params(反馈请带上此行日志):" + json);
        asr.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }


    /**
     * 提前结束录音等待识别结果。
     */
    public void stop() {
        PPLog.i(TAG, "停止录音");
        asr.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0);
    }

    /**
     * 取消本次识别，取消后将立即停止不会返回识别结果。
     * cancel 与stop的区别是 cancel在stop的基础上，完全停止整个识别流程，
     */
    public void cancel() {
        PPLog.i(TAG, "取消识别");
        if (asr != null){
            asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        }
    }


    public void release() {
        if (asr == null){
            return;
        }
        cancel();
        if (isOfflineEngineLoaded) {
            asr.send(SpeechConstant.ASR_KWS_UNLOAD_ENGINE, null, null, 0, 0);
            isOfflineEngineLoaded = false;
        }
        asr.unregisterListener(eventListener);
        asr = null;
        isInited = false;
    }
}
