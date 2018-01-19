package com.example.pappu.memotape;

/**
 * Created by hello on 10/20/17.
 */

public interface ImageSaveListener {
   // void saveImage(byte[] data, int width, int height);

    void onSuccess(String imageFilePath);
    void onFailure(String errorMsg);

}
