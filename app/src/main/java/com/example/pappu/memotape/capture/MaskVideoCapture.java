package com.example.pappu.memotape.capture;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.EGLContext;
import android.opengl.EGLSurface;

import com.example.pappu.memotape.Utility.FileUtils;
import com.example.pappu.memotape.gles.EglCore;
import com.example.pappu.memotape.render.ImageRenderer;
import com.example.pappu.memotape.videoencoder.VideoEncoderHandler;

import java.io.File;
import java.io.IOException;




/**
 * Created by pappu on 2/27/17.
 */

public class MaskVideoCapture {
    private static final boolean VERBOSE = false;           // lots of logging
    private static final String TAG = "MaskVideoCapture";
    private Context context;
    private final ImageRenderer imageRenderer;
    // size of a frame, in pixels
    private int mWidth = 0;
    private int mHeight = 0;
    private boolean stopVideoCapture = false;
    private MediaPlayer mediaPlayer;
    private Uri audioUri;
    private String tempVideoPath  = FileUtils.getTemprorayVideoFilePath();
    private VideoEncoderHandler videoEncoderHandler = new VideoEncoderHandler();
    private VideoCaptureListener videoCaptureListener;
    private boolean isMediaPlayerErrorOccurred = false;
    private boolean isMediaPlayerPrepared = false;

    private EglCore eglCore;
    private EGLSurface eglSurface;
    private long previousFramePSTime;
    private int generateIndex = 0;

    public MaskVideoCapture(ImageRenderer imageRenderer, Context context) {
        this.context = context;
        this.imageRenderer = imageRenderer;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnErrorListener( new MediaPlayerErrorListener());
        videoEncoderHandler = new VideoEncoderHandler();
        videoEncoderHandler.setVideoEncodeListener(new VideoEncodeListenerImp());
        imageRenderer.setMaskVideoCapture(this);
    }

    public void setVideoCaptureListener(VideoCaptureListener videoCaptureListener) {
        this.videoCaptureListener = videoCaptureListener;
    }

    public void setAudioPath(Uri audioUri) {
        this.audioUri = audioUri;
    }

    public boolean isReady() {
        return videoEncoderHandler.isReady();
    }

    public void makeCurrent() {
        try {
            eglCore.makeCurrent(eglSurface);
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void frameAvailable() {
        long ptsUsec = computePresentationTime(generateIndex);
        eglCore.setPresentationTime(eglSurface,ptsUsec);
        eglCore.swapBuffers(eglSurface);
        videoEncoderHandler.frameAvailable();
        generateIndex++;
    }

    public void stopRecording() {
        videoEncoderHandler.stopRecording();
    }

    public void startRecording(EGLContext eglContext) {
        try {
            videoEncoderHandler.startRecording(new VideoEncoderHandler.EncoderConfig(mWidth, mHeight, eglContext));
            eglCore = new EglCore(eglContext, EglCore.FLAG_RECORDABLE);
            eglSurface = eglCore.createWindowSurface(videoEncoderHandler.getInputSurface());
            eglCore.makeCurrent(eglSurface);
        } catch (Exception e) {
           handleError(e);
        }
    }

    public void updateSharedContext(EGLContext eglContext) {

        // Release the EGLSurface and EGLContext.
        eglCore.releaseSurface(eglSurface);
        eglCore.release();

        // Create a new EGLContext and recreate the window surface.
        try {
            eglCore = new EglCore(eglContext, EglCore.FLAG_RECORDABLE);
            eglSurface = eglCore.createWindowSurface(videoEncoderHandler.getInputSurface());
            videoEncoderHandler.updateSharedContext(eglContext);
        } catch (Exception e) {
            handleError(e);
        }
    }

    public interface VideoCaptureListener {
        void onSuccess(String videoFilePath);
        void onFailure(VideoCaptureException exception);
    }

    public void initializeEncoder() throws IOException {
        mHeight = imageRenderer.getOffScreenPreviewHeight();
        mWidth = imageRenderer.getOffsceenPreviewWidth();
        stopVideoCapture = false;
        if (audioUri != null) {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context,audioUri);
            mediaPlayer.prepare();
            isMediaPlayerPrepared = true;
        }
        isMediaPlayerErrorOccurred = false;
    }

    public void start() {
        if (canCallMediaPlayerStart() && audioUri!=null) {
            mediaPlayer.start();
        }
        imageRenderer.startRecording();
    }

    public void stop() {
        stopVideoCapture = true;
        resetMediaPlayer();
        imageRenderer.stopRecording();
    }

    public void destroy() {
        stopVideoCapture = true;
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
        }
    }

    private class VideoEncodeListenerImp implements VideoEncoderHandler.VideoEncodeListener {

        @Override
        public void onFinish() {
            releaseEGL();
            videoCaptureListener.onSuccess(tempVideoPath);
        }

        @Override
        public void onError(Exception exception) {
            handleError(exception);
        }
    }


    /**
     * Generates the presentation time for frame N, in nanoseconds.
     */
    private  long computePresentationTime(int frameIndex) {
        if (frameIndex == 0) {
            previousFramePSTime = System.nanoTime();
            return 132;
        }
        else {
            return 132 + (System.nanoTime() - previousFramePSTime);
        }
    }

    private void handleError(Exception exp){
        resetMediaPlayer();
        videoEncoderHandler.cancelRecording();
        imageRenderer.cancelRecording();
        releaseEGL();
        File file = new File(tempVideoPath);
        file.delete();
        videoCaptureListener.onFailure(new VideoCaptureException(exp.getMessage(),exp.getCause()));
    }

    private void resetMediaPlayer() {
        if (mediaPlayer != null) {
            if (canCallMediaPlayerStop()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            isMediaPlayerPrepared = false;
        }
    }

    private void releaseEGL() {
        if (eglCore != null) {
            if (eglSurface != null) {
                eglCore.releaseSurface(eglSurface);
            }
            eglCore.release();
            eglCore = null;
        }
    }

    private class MediaPlayerErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            isMediaPlayerErrorOccurred = true;
            return true;
        }
    }

    private boolean canCallMediaPlayerStart() {
        return isMediaPlayerPrepared;
    }

    private boolean canCallMediaPlayerStop() {
        return isMediaPlayerPrepared && !isMediaPlayerErrorOccurred;
    }
}
