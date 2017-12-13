package com.kian.intelligentbutler.baidu_speech.wakeup;

/**
 * Created by Kian on 2017/12/11.
 */

public interface IWakeupListener {

    void onSuccess(String word, WakeupResult result);

    void onStop();

    void onError(int errorCode, String errorMessge, WakeupResult result);

    void onASrAudio(byte[] data, int offset, int length);
}
