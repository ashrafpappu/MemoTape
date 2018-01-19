package com.example.pappu.memotape.videoencoder;


import android.opengl.EGLContext;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;


public class VideoEncoderHandler implements Runnable {

    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_FRAME_AVAILABLE = 2;
    private static final int MSG_UPDATE_SHARED_CONTEXT = 3;
    private static final int MSG_QUIT = 4;
    private static final int BIT_RATE = 6000000;

    private VideoEncoderCore videoEncoderCore;

    private volatile EncoderHandler mHandler;

    // guards ready/running
    private Object mReadyFence = new Object();
    private boolean mReady;
    private boolean mRunning;
    private static String TAG = "VideoEncoderHandler";

    String outputPath = new File(Environment.getExternalStorageDirectory(), "Video.mp4").toString();
    private VideoEncodeListener videoEncodeListener;
    private boolean isErrorOccurred = false;

    public void setVideoEncodeListener(VideoEncodeListener videoEncodeListener) {
        this.videoEncodeListener = videoEncodeListener;
    }

    public Surface getInputSurface() {
        return videoEncoderCore.getInputSurface();
    }

    public static class EncoderConfig {
        final int mWidth;
        final int mHeight;
        final EGLContext mEglContext;

        public EncoderConfig(int width, int height,
                             EGLContext sharedEglContext) {
            mWidth = width;
            mHeight = height;
            mEglContext = sharedEglContext;
        }
    }

    public interface VideoEncodeListener {
        void onFinish();
        void onError(Exception exception);
    }
    /**
     * Tells the video recorder to start recording.  (Call from non-encoder thread.)
     * <p>
     * Creates a new thread, which will create an encoder using the provided configuration.
     * <p>
     * Returns after the recorder thread has started and is ready to accept Messages.  The
     * encoder may not yet be fully configured.
     */
    public void startRecording(EncoderConfig config) throws Exception {
       // SDKLoggerConfig.logD(TAG, "Encoder: startRecording()");
        prepareEncoder(config.mEglContext, config.mWidth, config.mHeight, BIT_RATE,
                new File(outputPath));
        new Thread(this, "VideoEncoderHandler").start();
    }

    /**
     * Tells the video recorder to stop recording.  (Call from non-encoder thread.)
     * <p>
     * Returns immediately; the encoder/muxer may not yet be finished creating the movie.
     * <p>
     * TODO: have the encoder thread invoke a callback on the UI thread just before it shuts down
     * so we can provide reasonable status UI (and let the caller know that movie encoding
     * has completed).
     */
    public void stopRecording() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_STOP_RECORDING));
        mHandler.sendMessage(mHandler.obtainMessage(MSG_QUIT));
        // We don't know when these will actually finish (or even start).  We don't want to
        // delay the UI thread though, so we return immediately.
    }

    /**
     * Tells the video recorder to refresh its EGL surface.  (Call from non-encoder thread.)
     */
    public void updateSharedContext(EGLContext sharedContext) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SHARED_CONTEXT, sharedContext));
    }

    /**
     * Tells the video recorder that a new frame is available.  (Call from non-encoder thread.)
     * <p>
     * This function sends a message and returns immediately.  This isn't sufficient -- we
     * don't want the caller to latch a new frame until we're done with this one -- but we
     * can get away with it so long as the input frame rate is reasonable and the encoder
     * thread doesn't stall.
     * <p>
     * TODO: either block here until the texture has been rendered onto the encoder surface,
     * or have a separate "block if still busy" method that the caller can execute immediately
     * before it calls updateTexImage().  The latter is preferred because we don't want to
     * stall the caller while this thread does work.
     */
    public void frameAvailable() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_FRAME_AVAILABLE));
    }

    /**
     * Encoder thread entry point.  Establishes Looper/Handler and waits for messages.
     * <p>
     * @see Thread#run()
     */
    @Override
    public void run() {
        // Establish a Looper for this thread, and define a Handler for it.
        Looper.prepare();
        synchronized (mReadyFence) {
            mHandler = new EncoderHandler(this);
            mReady = true;
            mReadyFence.notify();
        }
        Looper.loop();

        //SDKLoggerConfig.logD(TAG, "Encoder thread exiting");
        synchronized (mReadyFence) {
            mReady = mRunning = false;
            mHandler = null;
            isErrorOccurred = false;
        }
    }

    private class EncoderHandler extends Handler {
        private WeakReference<VideoEncoderHandler> mVideoEncoderHandlerWeakReference;

        public EncoderHandler(VideoEncoderHandler videoEncoderHandler) {
            mVideoEncoderHandlerWeakReference = new WeakReference<VideoEncoderHandler>(videoEncoderHandler);
        }
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            VideoEncoderHandler encoder = mVideoEncoderHandlerWeakReference.get();
            if (encoder == null) {
               // SDKLoggerConfig.logD(TAG, "EncoderHandler.handleMessage: encoder is null");
                return;
            }
            if (isErrorOccurred && what != MSG_QUIT) {
               // SDKLoggerConfig.logD(TAG, "EncoderHandler.handleMessage: error occurred in video encoder, only quit message is handling. msg: "+what);
                return;
            }

            switch (what) {
                case MSG_STOP_RECORDING:
                    encoder.handleStopRecording();
                    break;
                case MSG_FRAME_AVAILABLE:
                    encoder.handleFrameAvailable();
                    break;
                case MSG_UPDATE_SHARED_CONTEXT:
                    encoder.handleUpdateSharedContext((EGLContext) msg.obj);
                    break;
                case MSG_QUIT:
                    Looper.myLooper().quit();
                    break;
                default:
                    throw new RuntimeException("Unhandled msg what=" + what);
            }
        }
    }

    /**
     * Handles notification of an available frame.
     * <p>
     * The texture is rendered onto the encoder's input surface, along with a moving
     * box (just because we can).
     * <p>
     */
    private void handleFrameAvailable() {
        try {
            videoEncoderCore.drainEncoder(false);
        } catch (Exception e) {
            handleEncoderError(e);
        }
    }

    /**
     * Handles a request to stop encoding.
     */
    private void handleStopRecording() {
       // SDKLoggerConfig.logD(TAG, "handleStopRecording");
        try {
            videoEncoderCore.drainEncoder(true);
            releaseEncoder();
            videoEncodeListener.onFinish();
        } catch (Exception e) {
            synchronized (mReadyFence) {
                handleEncoderError(e);
            }
        }
    }

    /**
     * Tears down the EGL surface and context we've been using to feed the MediaCodec input
     * surface, and replaces it with a new one that shares with the new context.
     * <p>
     * This is useful if the old context we were sharing with went away (maybe a GLSurfaceView
     * that got torn down) and we need to hook up with the new one.
     */
    private void handleUpdateSharedContext(EGLContext newSharedContext) {
        //SDKLoggerConfig.logD(TAG, "handleUpdatedSharedContext " + newSharedContext);
    }

    private void prepareEncoder(EGLContext sharedContext, int width, int height, int bitRate,
                                File outputFile) {
        try {
            videoEncoderCore = new VideoEncoderCore(width, height, bitRate, outputFile);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public void cancelRecording() {
        releaseEncoder();
        if (mHandler!= null) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_QUIT));
        }
    }

    private void releaseEncoder() {
        if (videoEncoderCore != null) {
            videoEncoderCore.release();
        }
    }

    public boolean isReady()
    {
        synchronized (mReadyFence) {
            return mReady;
        }
    }

    private void handleEncoderError(Exception e) {
        synchronized (mReadyFence) {
            mReady = false;
            isErrorOccurred = true;
            videoEncodeListener.onError(e);
        }
    }
}
