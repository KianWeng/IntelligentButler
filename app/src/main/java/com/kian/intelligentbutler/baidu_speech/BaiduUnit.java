package com.kian.intelligentbutler.baidu_speech;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.kian.intelligentbutler.baidu_speech.unit.UnitAPIService;
import com.kian.intelligentbutler.baidu_speech.unit.AccessToken;
import com.kian.intelligentbutler.baidu_speech.unit.CommunicateResponse;
import com.kian.intelligentbutler.baidu_speech.unit.OnResultListener;
import com.kian.intelligentbutler.baidu_speech.unit.Scene;
import com.kian.intelligentbutler.baidu_speech.unit.UnitError;
import com.kian.intelligentbutler.util.PPLog;

import java.util.List;

/**
 * Created by kian on 2017/12/15.
 */

public class BaiduUnit {
    private static final String TAG = "BaiduUnit";
    private Context context;
    private Scene curScene;
    private String accessToken;
    private String sessionId = "";

    public BaiduUnit(Context context){
        this.context = context;
    }

    /**
     * 为了防止破解app获取ak，sk，建议您把ak，sk放在服务器端。
     */
    public void initAccessToken() {
        UnitAPIService.getInstance().init(context);
        UnitAPIService.getInstance().initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                accessToken = result.getAccessToken();
                Log.i("MainActivity", "AccessToken->" + result.getAccessToken());
            }

            @Override
            public void onError(UnitError error) {
                Log.i("wtf", "AccessToken->" + error.getErrorMessage());
            }
        }, "jMWIPDmK6zaxiK9KMCbb0hQl", "D4d5DD51gElVGkoHuoOZZfSHFDDT5K5k");
    }

    public void sendMessage(String message) {

        if (TextUtils.isEmpty(accessToken)) {
            PPLog.i(TAG,"accessToken is not ready");
            return;
        }

        UnitAPIService.getInstance().communicate(new OnResultListener<CommunicateResponse>() {
            @Override
            public void onResult(CommunicateResponse result) {

                handleResponse(result);
            }

            @Override
            public void onError(UnitError error) {

            }
        }, curScene.getId(), message, sessionId);

    }

    private void handleResponse(CommunicateResponse result) {
        if (result != null) {
            sessionId = result.sessionId;

            //  如果有对于的动作action，请执行相应的逻辑
            List<CommunicateResponse.Action> actionList = result.actionList;
            if (actionList.size() > 1) {
                PPLog.i(TAG,"actionList size is bigger than 1");
            } else if (actionList.size() == 1){
                CommunicateResponse.Action action = actionList.get(0);
                if (!TextUtils.isEmpty(action.say)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(action.say);
                    PPLog.i(TAG,sb.toString());
                }

                // 执行自己的业务逻辑
                if ("start_work_satisfy".equals(action.actionId)) {
                    Log.i("wtf", "开始扫地");
                } else if ("stop_work_satisfy".equals(action.actionId)) {
                    Log.i("wtf", "停止工作");
                } else if ("move_action_satisfy".equals(action.actionId)) {
                    Log.i("wtf", "移动");
                } else if ("timed_charge_satisfy".equals(action.actionId)) {
                    Log.i("wtf", "定时充电");
                } else if ("timed_task_satisfy".equals(action.actionId)) {
                    Log.i("wtf", "定时扫地");
                } else if ("sing_song_satisfy".equals(action.actionId)) {
                    Log.i("wtf", "唱歌");
                }

                if (!TextUtils.isEmpty(action.mainExe)) {
                    Toast.makeText(context, "请执行函数：" + action.mainExe, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 切换场景
     *
     * @param scene
     */
    private void setScene(Scene scene) {
        curScene = scene;
    }
}
