package com.kian.intelligentbutler.baidu_speech.unit;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by YYTD on 2017/12/15.
 */

public class CommunicateParser implements Parser<CommunicateResponse>{
    @Override
    public CommunicateResponse parse(String json) throws UnitError {
        Log.e("xx", "CommunicateParser:" + json);
        try {
            JSONObject jsonObject = new JSONObject(json);

            if (jsonObject.has("error_code")) {
                UnitError error = new UnitError(jsonObject.optInt("error_code"), jsonObject.optString("error_msg"));
                throw error;
            }

            CommunicateResponse result = new CommunicateResponse();
            result.setLogId(jsonObject.optLong("log_id"));
            result.setJsonRes(json);

            JSONObject resultObject = jsonObject.getJSONObject("result");
            List<CommunicateResponse.Action> actionList = result.actionList;
            JSONArray actionListArray = resultObject.optJSONArray("action_list");
            if (actionListArray != null) {
                for (int i = 0; i < actionListArray.length(); i++) {
                    JSONObject actionListObject = actionListArray.optJSONObject(i);
                    if (actionListObject == null) {
                        continue;
                    }
                    CommunicateResponse.Action action = new CommunicateResponse.Action();
                    action.actionId = actionListObject.optString("action_id");
                    JSONObject actionTypeObject = actionListObject.optJSONObject("action_type");

                    action.actionType = new CommunicateResponse.ActionType();
                    action.actionType.target = actionTypeObject.optString("act_target");
                    action.actionType.targetDetail = actionTypeObject.optString("act_target_detail");
                    action.actionType.type = actionTypeObject.optString("act_type");
                    action.actionType.typeDetail = actionTypeObject.optString("act_type_detail");

                    action.confidence = actionListObject.optInt("confidence");
                    action.say = actionListObject.optString("say");
                    action.mainExe = actionListObject.optString("main_exe");

                    JSONArray hintListArray = actionListObject.optJSONArray("hint_list");
                    if (hintListArray != null) {
                        for (int j = 0; j < hintListArray.length(); j++) {
                            JSONObject hintQuery =  hintListArray.optJSONObject(j);
                            if (hintQuery != null) {
                                action.hintList.add(hintQuery.optString("hint_query"));
                            }
                        }
                    }

                    actionList.add(action);
                }
            }

            result.sessionId = resultObject.optString("session_id");

            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            UnitError error = new UnitError(UnitError.ErrorCode.JSON_PARSE_ERROR, "Json parse error:" + json, e);
            throw error;
        }
    }
}
