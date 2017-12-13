package com.kian.intelligentbutler.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.kian.intelligentbutler.AudioRecordManager;
import com.kian.intelligentbutler.baidu_speech.BaiduRecognizer;
import com.kian.intelligentbutler.util.PPLog;

/**
 * Created by YYTD on 2017/12/8.
 */

public class RecognizerView extends Button{

    private static final String TAG = "RecordAudioView";

    private Context context;
    private IRecordAudioListener recordAudioListener;
    private AudioRecordManager audioRecordManager;
    private boolean isCanceled;
    private float downPointY;
    private static final float DEFAULT_SLIDE_HEIGHT_CANCEL = 150;
    private boolean isRecording;


    public RecognizerView(Context context) {
        super(context);
        initView(context);
    }

    public RecognizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RecognizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        this.context = context;
        audioRecordManager = AudioRecordManager.getInstance();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PPLog.i(TAG, "onTouchEvent");
        super.onTouchEvent(event);
        if(recordAudioListener != null){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    PPLog.i(TAG, "Action down");
                    setSelected(true);
                    recordAudioListener.onRecognizeStart();
                    break;
                case MotionEvent.ACTION_UP:
                    PPLog.i(TAG, "Action up");
                    setSelected(false);
                    recordAudioListener.onRecognizeStop();
                    break;
                default:

                    break;
            }
        }
        return true;
    }

    /**
     * 需要设置IRecordAudioStatus,来监听开始录音结束录音等操作,并对权限进行处理
     * @param recordAudioListener
     */
    public void setRecordAudioListener(IRecordAudioListener recordAudioListener) {
        this.recordAudioListener = recordAudioListener;
    }

    public interface IRecordAudioListener {
        void onRecognizeStart();
        void onRecognizeStop();
    }
}
