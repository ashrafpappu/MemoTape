package com.example.pappu.memotape.capture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.example.pappu.memotape.Utility.FileUtils;
import com.example.pappu.memotape.render.ImageRenderer;

import java.nio.ByteBuffer;




/**
 * Created by pappu on 2/25/17.
 */

public class MaskImageCapture extends FrameRenderObserver {

    private String imageFilePath;
    private ImageCaptureListener imageCaptureListener;
    private Context mContext;

    public MaskImageCapture(ImageRenderer imageRenderer, Context mContext) {
        this.imageRenderer = imageRenderer;
        this.mContext = mContext;
    }

    public interface ImageCaptureListener {
        void onSuccess(String videoFilePath);
        void onFailure(String exception);
    }

    public void setImageCaptureListener(ImageCaptureListener imageCaptureListener) {
        this.imageCaptureListener = imageCaptureListener;
    }

    @Override
    public void update(byte[] frameData) {

        int w = imageRenderer.getOffsceenPreviewWidth();
        int h = imageRenderer.getOffScreenPreviewHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bmp.copyPixelsFromBuffer(ByteBuffer.wrap(frameData));

        Matrix matrix = new Matrix();


        matrix.setScale(-1,1);
        matrix.postTranslate(bmp.getWidth(),0);
        matrix.postRotate(180);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bmp , 0, 0, bmp .getWidth(), bmp .getHeight(), matrix, true);
        imageFilePath = FileUtils.saveBitmapImage(rotatedBitmap,mContext);
        if(!imageFilePath.isEmpty()) {
            imageCaptureListener.onSuccess(imageFilePath);
        } else {
            imageCaptureListener.onFailure("image not captured");
        }
        imageRenderer.detach(this);
    }

    public void captureImage() {
        imageRenderer.attach(this);
    }
}
