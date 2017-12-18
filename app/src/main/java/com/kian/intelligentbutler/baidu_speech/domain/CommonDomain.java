package com.kian.intelligentbutler.baidu_speech.domain;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Kian on 2017/12/15.
 */

public class CommonDomain {
    private String domain;
    private String intent;
    private float score;
    private JSONObject object;

    public String getName(){return domain;}
    public void setName(String name){this.domain = name;}
    public String getIntent(){return intent;}
    public void setIntent(String intent){this.intent = intent;}
    public float getScore(){return score;}
    public void setScore(float score){this.score = score;}
    public JSONObject getObject(){return object;}
    public void setObject(JSONObject object){this.object = object;}
}
