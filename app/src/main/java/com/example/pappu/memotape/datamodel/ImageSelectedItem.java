package com.example.pappu.memotape.datamodel;

/**
 * Created by hello on 1/16/18.
 */

public class ImageSelectedItem {
    public int iconImageResourceId;
    public int position;
    public String xmlFileName;
    public MediaStoreImage mediaStoreImage;

    public ImageSelectedItem(MediaStoreImage mediaStoreImage, int position) {
        this.mediaStoreImage = mediaStoreImage;
        this.position = position;
    }

}
