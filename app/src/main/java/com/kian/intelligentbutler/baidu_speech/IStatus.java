package com.kian.intelligentbutler.baidu_speech;

/**
 * Created by Kian on 2017/12/11.
 */

public interface IStatus {
    int STATUS_NONE = 2;

    int STATUS_READY = 3;
    int STATUS_SPEAKING = 4;
    int STATUS_RECOGNITION = 5;

    int STATUS_FINISHED = 6;
    int STATUS_STOPPED = 10;
    int STATUS_UPDATE_ViewPager = 11;

    int STATUS_TTS_INIT_SUCCESS = 6001;
    int STATUS_TTS_INIT_FAIL = 6002;

    int STATUS_WAITING_READY = 8001;
    int WHAT_MESSAGE_STATUS = 9001;

    int STATUS_WAKEUP_SUCCESS = 7001;
    int STATUS_WAKEUP_EXIT = 7003;

    //Event type
    int TYPE_RECOG = 0;
    int TYPE_WAKEUP = 1;
    int TYPE_UPDATE_UI = 2;

    //View Pager ID
    int WELCOME_VIEWPAGER_ID = 0;
    int WEATHER_VIEWPAGER_ID = 1;
    int MONITOR_VIEWPAGER_ID = 2;
}
