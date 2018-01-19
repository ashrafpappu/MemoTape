package com.example.pappu.memotape.Frame;

/**
 * Created by pappu on 1/15/18.
 */

public class FrameItem {
    public int iconImageResourceId;
    public int position;
    public String xmlFileName;

    public FrameItem(int iconImageResourceId,String xmlFileName, int position) {
        this.iconImageResourceId = iconImageResourceId;
        this.position = position;
        this.xmlFileName = xmlFileName;
    }
}
