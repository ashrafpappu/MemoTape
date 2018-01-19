package com.example.pappu.memotape.datamodel;

/**
 * Created by hello on 1/16/18.
 */

public class ImageSelectedItem {
    public int iconImageResourceId;
    public int position;
    public String xmlFileName;

    public ImageSelectedItem(int iconImageResourceId, int position) {
        this.iconImageResourceId = iconImageResourceId;
        this.position = position;
    }

}
