package com.example.pappu.memotape.capture;


import com.example.pappu.memotape.render.ImageRenderer;

/**
 * Created by pappu on 2/25/17.
 */

public abstract class FrameRenderObserver {
    protected ImageRenderer imageRenderer;
    public abstract void update(byte[] frameData);
}
