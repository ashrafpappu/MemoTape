package com.example.pappu.memotape;

/**
 * Created by hello on 1/8/18.
 */

public interface VideoSaveListener {
    void onSuccess(String videoFilePath);
    void onFailure(Exception exception);
}
