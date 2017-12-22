package com.kian.intelligentbutler.baidu_speech;

/**
 * Created by Kian on 2017/12/11.
 */

public interface IStatus {
    //Event type
    int TYPE_RECOG = 0;
    int TYPE_WAKEUP = 1;
    int TYPE_TTS = 2;
    int TYPE_UPDATE_UI = 3;

    //Recognize status
    int STATUS_WAITING_READY = 1001;
    int STATUS_NONE = 1002;
    int STATUS_READY = 1003;
    int STATUS_SPEAKING = 1004;
    int STATUS_RECOGNITION = 1005;
    int STATUS_FINISHED = 1006;
    int STATUS_STOPPED = 1007;
    int WHAT_MESSAGE_STATUS = 1008;

    //Wakeup status
    int STATUS_WAKEUP_SUCCESS = 2001;
    int STATUS_WAKEUP_EXIT = 2002;

    //TTS status
    int STATUS_TTS_INIT_SUCCESS = 3001;
    int STATUS_TTS_INIT_FAIL = 3002;

    //View Pager ID
    int WELCOME_VIEWPAGER_ID = 0;
    int WEATHER_VIEWPAGER_ID = 1;
    int MONITOR_VIEWPAGER_ID = 2;
}
