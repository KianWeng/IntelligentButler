package com.kian.intelligentbutler.baidu_speech.unit;

/**
 * Created by YYTD on 2017/12/15.
 */

public class Scene {
    private int id;

    private String name;

    public Scene(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
