package com.kian.intelligentbutler.baidu_speech.unit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YYTD on 2017/12/15.
 */

public class CommunicateResponse extends ResponseResult{
    public List<Action> actionList = new ArrayList<>();

    public Schema schema;

    public String sessionId;


    public static class Action {
        public String actionId;
        public ActionType actionType;
        public List argList = new ArrayList<>();
        // public CodeAction codeAction;
        public int confidence;
        public List exeStatusList = new ArrayList<>();
        public List<String> hintList = new ArrayList<String>();
        public String mainExe;
        public String say;
    }

    public static class ActionType {
        public String target;
        public String targetDetail;
        public String type;
        public String typeDetail;
    }

    // public static class CodeAction {}

    public static class Schema {
        public List botMergedSlots = new ArrayList();
        public String currentQueryInent;
        public int intentConfidence;
    }
}
