package com.kian.intelligentbutler.baidu_speech.recognization;
import android.os.Handler;
import android.os.Message;

import com.kian.intelligentbutler.baidu_speech.IStatus;
import com.kian.intelligentbutler.baidu_speech.domain.DomainService;
import com.kian.intelligentbutler.ui.LineWaveVoiceView;
import com.kian.intelligentbutler.util.PPLog;

/**
 * Created by Kian on 2017/12/11.
 */

public class StatusRecogListener implements IRecogListener,IStatus {
    private static final String TAG = "StatusRecogListener";

    /**
     * 识别的引擎当前的状态
     */
    protected int status = STATUS_NONE;
    private LineWaveVoiceView mHorVoiceView;
    private Handler handler;

    public StatusRecogListener(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onAsrReady() {
        status = STATUS_READY;
        sendMessage("引擎就绪，可以开始说话。", status);
    }

    @Override
    public void onAsrBegin() {
        status = STATUS_SPEAKING;
        sendMessage("检测到用户说话", status);
    }

    @Override
    public void onAsrEnd() {
        status = STATUS_RECOGNITION;
        sendMessage("检测到用户说话结束", status);
    }

    @Override
    public void onAsrPartialResult(String[] results, RecogResult recogResult) {}

    @Override
    public void onAsrFinalResult(String[] results, RecogResult recogResult) {
        String message = "识别结束，结果是”" + results[0] + "”";
        status = STATUS_FINISHED;
        sendMessage(message, status);
    }

    @Override
    public void onAsrFinish(RecogResult recogResult) {
        status = STATUS_FINISHED;
        sendMessage("识别一段话结束。如果是长语音的情况会继续识别下段话。", status);
    }


    @Override
    public void onAsrFinishError(int errorCode,int subErrorCode, String errorMessage, String descMessage,RecogResult recogResult) {
        String message = "识别错误, 错误码：" + errorCode + "," + subErrorCode;
        status = STATUS_FINISHED;
        sendMessage(message, status);
    }

    /**
     * 长语音识别结束
     */
    @Override
    public void onAsrLongFinish() {
        status = STATUS_FINISHED;
        sendMessage("长语音识别结束。", status);
    }

    @Override
    public void onAsrVolume(int volumePercent, int volume) {
        PPLog.i(TAG,"音量百分比"+volumePercent +" ; 音量"+ volume);
        this.mHorVoiceView.maxAmp = (float) volumePercent / 100;
    }

    @Override
    public void onAsrAudio(byte[] data, int offset, int length) {
        if (offset != 0 || data.length != length) {
            byte[] actualData = new byte[length];
            System.arraycopy(data, 0, actualData, 0, length);
            data = actualData;
        }

        PPLog.i(TAG, "音频数据回调, length:" + data.length);
    }

    @Override
    public void onAsrExit() {
        status = STATUS_NONE;
        sendMessage("识别引擎结束并空闲中", status);
    }

    @Override
    public void onAsrOnlineNluResult(String nluResult) {
        status = STATUS_FINISHED;
        if (!nluResult.isEmpty()) {
            sendMessage("原始语义识别结果json：" + nluResult, status);
            DomainService.getInstance().handleNluResult(nluResult);
        }
    }

    @Override
    public void onOfflineLoaded() {}

    @Override
    public void onOfflineUnLoaded() {}

    public void setVoiceView(LineWaveVoiceView mHorVoiceView){
        this.mHorVoiceView = mHorVoiceView;
    }

    private void sendMessage(String message, int what) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = status;
        msg.obj = message + "\n";
        handler.sendMessage(msg);
    }
}
