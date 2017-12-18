package com.kian.intelligentbutler.baidu_speech.unit;

/**
 * Created by YYTD on 2017/12/15.
 */
/**
 * JSON解析
 * @param <T>
 */
public interface Parser<T> {
    T parse(String json) throws UnitError;
}
