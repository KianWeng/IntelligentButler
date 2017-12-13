package com.kian.intelligentbutler.baidu_speech.recognization.params;

import android.app.Activity;
import android.content.SharedPreferences;

import com.baidu.speech.asr.SpeechConstant;
import com.kian.intelligentbutler.baidu_speech.recognization.PidBuilder;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by Kian on 2017/12/11.
 */

public class AllRecogParams extends CommonRecogParams {

    private static final String TAG = "NluRecogParams";

    public AllRecogParams(Activity context) {
        super(context);
        stringParams.addAll(Arrays.asList(
                SpeechConstant.NLU,
                "_language",
                "_model"));

        intParams.addAll(Arrays.asList(
                SpeechConstant.DECODER,
                SpeechConstant.PROP));

        boolParams.addAll(Arrays.asList(SpeechConstant.DISABLE_PUNCTUATION,  "_nlu_online"));

        // copyOfflineResource(context);
    }

    public Map<String, Object> fetch(SharedPreferences sp) {

        Map<String, Object> map = super.fetch(sp);

        PidBuilder builder = new PidBuilder();
        map = builder.addPidInfo(map);
        //boolean isOfflineEnabled = sp.getBoolean(SpeechConstant.DECODER, false);
        //map.put(SpeechConstant.DECODER, 0);


        /*if ("sp.getString(SpeechConstant.DECODER, "0")){
            // 离线需要额外设置离线资源文件

        }*/
        return map;

    }
}
