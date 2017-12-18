package com.kian.intelligentbutler.baidu_speech.unit;

/**
 * Created by YYTD on 2017/12/15.
 */

public interface OnResultListener<T> {
    void onResult(T result);

    void onError(UnitError error);
}
