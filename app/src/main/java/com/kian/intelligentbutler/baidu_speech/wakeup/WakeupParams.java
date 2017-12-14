package com.kian.intelligentbutler.baidu_speech.wakeup;

import android.content.Context;

import com.baidu.speech.asr.SpeechConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by YYTD on 2017/12/14.
 */

public class WakeupParams {
    private final String TAG = "WakeupParams";

    public WakeupParams(final Context context){
    }

    public Map<String,Object> fetch(){
        Map<String,Object> params = new HashMap<String,Object>();
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        //   params.put(SpeechConstant.ACCEPT_AUDIO_DATA,true);
        //params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME,true);
        // params.put(SpeechConstant.IN_FILE,"res:///com/baidu/android/voicedemo/wakeup.pcm");
        return params;
    }
}
