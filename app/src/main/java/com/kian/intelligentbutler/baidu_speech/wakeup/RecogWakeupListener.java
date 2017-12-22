package com.kian.intelligentbutler.baidu_speech.wakeup;

import android.os.Handler;
import android.os.Message;

import com.kian.intelligentbutler.baidu_speech.BaiduTTS;
import com.kian.intelligentbutler.baidu_speech.IStatus;
import com.kian.intelligentbutler.baidu_speech.tts.TTSAPIService;
import com.kian.intelligentbutler.util.PPLog;

/**
 * Created by YYTD on 2017/12/14.
 */

public class RecogWakeupListener implements IWakeupListener,IStatus{
    private static final String TAG = "RecogWakeupListener";

    private Handler handler;

    public RecogWakeupListener(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onSuccess(String word, WakeupResult result) {
        PPLog.i(TAG, "唤醒成功，唤醒词：" + word);
        if(word.equals("公羽启皓")) {
            //handler.sendMessage(handler.obtainMessage(STATUS_WAKEUP_SUCCESS));
            sendMessage(TYPE_WAKEUP,STATUS_WAKEUP_SUCCESS,word);
        }
    }

    @Override
    public void onStop() {
        PPLog.i(TAG, "唤醒词识别结束：");
    }

    @Override
    public void onError(int errorCode, String errorMessge, WakeupResult result) {
        PPLog.i(TAG,"唤醒错误："+ errorCode +";错误消息："+errorMessge +"; 原始返回"+ result.getOrigalJson());
    }

    @Override
    public void onASrAudio(byte[] data, int offset, int length) {
        PPLog.e(TAG,"audio data： "+data.length);
    }

    private void sendMessage(int what, int arg, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg;
        msg.obj = obj;
        handler.sendMessage(msg);
    }
}
