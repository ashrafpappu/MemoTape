package com.example.pappu.memotape.render;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.pappu.memotape.ImageSaveListener;
import com.example.pappu.memotape.Utility.AppUtils;
import com.example.pappu.memotape.capture.FrameRenderObserver;
import com.example.pappu.memotape.capture.MaskVideoCapture;
import com.example.pappu.memotape.datamodel.PreviewInfo;
import com.example.pappu.memotape.logger.SDKLoggerConfig;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;




public class ImageRenderer implements GLSurfaceView.Renderer{

    private final float[] mtrxProjection = new float[16];
    private final float[] mtrxView = new float[16];
    private final float[] mtrxProjectionAndView = new float[16];
    private PreviewRenderer previewRenderer;
    private int width = 0;
    private  int height = 0;
   // int offsceenPreviewWidth, offScreenPreviewHeight;
    private static String TAG = "ImageRenderer";
    private YUVGLRender yuvglRender;
    private boolean capture = false;

    private int[] offScreenFrameBufferId = new int[1];
    private int[] offScreenTexureId = new int[1];
    private Context context;
    PreviewInfo previewInfo;
    private MaskVideoCapture maskVideoCapture;

    private OnscreenTextureDraw onscreenTextureDraw;
    private ImageSaveListener imageSaveListener;

    private List<FrameRenderObserver> frameRenderObserverList = new ArrayList<>();
    int offsceenPreviewWidth, offScreenPreviewHeight;
    private static final int RECORDING_OFF = 0;
    private static final int RECORDING_ON = 1;
    private static final int RECORDING_RESUMED = 2;
    private boolean mRecordingEnabled = false;
    private int mRecordingStatus = RECORDING_OFF;
    EGLDisplay eglDisplay;
    EGLSurface eglSurface;
    EGLContext eglContext;


    public ImageRenderer(Context context, int previewWidth, int previewHeight){
        this.context = context;
        this.offsceenPreviewWidth = (int) Math.ceil(previewWidth / 16.0) * 16;
        this.offScreenPreviewHeight = previewHeight;
        yuvglRender = new YUVGLRender();
        previewRenderer = new PreviewRenderer(context);
        previewRenderer.setYuvglRender(yuvglRender);
        onscreenTextureDraw = new OnscreenTextureDraw();
    }


    public void changeCameraOrientation(int cameraId) {
        previewRenderer.changeCameraOrientation(cameraId);
    }

    public void captureImage(){
        capture = true;
    }

    public void setImageSaveListener(ImageSaveListener imageSaveListener){
        this.imageSaveListener = imageSaveListener;
    }

    public void updateYUVBuffers(final byte[] imageBuf) {
        previewRenderer.updateYUVBuffers(imageBuf, offScreenPreviewHeight, offsceenPreviewWidth);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        previewRenderer.intitPreviewShader();
        onscreenTextureDraw.initOnScreenShaderProgramm(context);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        // clear display color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }


    void createOffScreenTexture(){
        GLES20.glGenFramebuffers(1, offScreenFrameBufferId, 0);
        GLES20.glGenTextures(1,offScreenTexureId,0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, offScreenTexureId[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, offScreenPreviewHeight, offsceenPreviewWidth, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }


    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        this.width = width;
        this.height = height;
        GLES20.glViewport(0, 0, width, height);
        previewInfo = AppUtils.getadjustedPreview(width,height, offsceenPreviewWidth, offScreenPreviewHeight,null);
        GLES20.glViewport(previewInfo.offsetX, previewInfo.offsetY, previewInfo.previewWidth, previewInfo.preiviewHeight);

        for (int i = 0; i < 16; i++) {
            mtrxProjection[i] = 0.0f;
            mtrxView[i] = 0.0f;
            mtrxProjectionAndView[i] = 0.0f;
        }

        Matrix.orthoM(mtrxProjection, 0, 0f, previewInfo.previewWidth, 0.0f, previewInfo.preiviewHeight, 0, 10);
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);
        createOffScreenTexture();
        eglDisplay = EGL14.eglGetCurrentDisplay();
        eglSurface = EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW);
        eglContext = EGL14.eglGetCurrentContext();
        if (EGL14.EGL_OPENGL_ES_API == EGL14.eglQueryAPI())
            SDKLoggerConfig.logD(TAG,"Current Api : EGL_OPENGL_ES_API");
    }

    void getFramebuffer(){

//        if (capture) {
//            byte b[] = new byte[offsceenPreviewWidth * offScreenPreviewHeight * 4];
//            ByteBuffer byteBuffer = ByteBuffer.wrap(b);
//            byteBuffer.order(ByteOrder.nativeOrder());
//            byteBuffer.position(0);
//            GLES20.glReadPixels(0, 0, offScreenPreviewHeight, offsceenPreviewWidth, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, byteBuffer);
//            imageSaveListener.saveImage(b,offsceenPreviewWidth,offScreenPreviewHeight);
//            byteBuffer.clear();
//            capture = false;
//        }

        if (!frameRenderObserverList.isEmpty()) {
            byte b[] = new byte[offsceenPreviewWidth * offScreenPreviewHeight * 4];
            ByteBuffer byteBuffer = ByteBuffer.wrap(b);
            byteBuffer.order(ByteOrder.nativeOrder());
            byteBuffer.position(0);
            GLES20.glReadPixels(0, 0, offScreenPreviewHeight, offsceenPreviewWidth, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, byteBuffer);
            for (FrameRenderObserver frameRenderObserver : frameRenderObserverList){
                frameRenderObserver.update(b);
                byteBuffer.clear();
            }

        }

    }

    void onScreenDraw(){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glViewport(previewInfo.offsetX, previewInfo.offsetY, previewInfo.previewWidth, previewInfo.preiviewHeight);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        onscreenTextureDraw.onScreenRender(offScreenTexureId[0]);
    }

    void offScreenRender(){

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, offScreenFrameBufferId[0]);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, offScreenTexureId[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, offScreenTexureId[0], 0);
        GLES20.glViewport(0, 0, offScreenPreviewHeight, offsceenPreviewWidth);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        renderOnBuffer();
    }

    void renderOnBuffer(){
        previewRenderer.renderPreview();
    }


    @Override
    public void onDrawFrame(GL10 gl10) {
        offScreenRender();
        getFramebuffer();
        drawOnSecondarySurface();
        onScreenDraw();
    }

    public void attach(FrameRenderObserver frameRenderObserver) {
        frameRenderObserverList.add(frameRenderObserver);
    }

    public void detach(FrameRenderObserver frameRenderObserver) {
        frameRenderObserverList.remove(frameRenderObserver);
    }
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOffsceenPreviewWidth() {
        return offScreenPreviewHeight;
    }

    public int getOffScreenPreviewHeight() {
        return offsceenPreviewWidth;
    }

    private void drawOnSecondarySurface() {
        updateRecordingState();
        if (mRecordingEnabled && maskVideoCapture.isReady()) {
            SDKLoggerConfig.logD(TAG, "Change to media codec surface");
            maskVideoCapture.makeCurrent();
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glViewport(0, 0, offScreenPreviewHeight, offsceenPreviewWidth);
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            onscreenTextureDraw.onScreenRender(offScreenTexureId[0]);
            maskVideoCapture.frameAvailable();
           // SDKLoggerConfig.logD(TAG, "Video Frame: " + ++mFrameCount);
            restoreDefaultSurface();
        }
    }

    public void updateRecordingState() {
        // If the recording state is changing, take care of it here.  Ideally we wouldn't
        // be doing all this in onDrawFrame(), but the EGLContext sharing with GLSurfaceView
        // makes it hard to do elsewhere.
        if (mRecordingEnabled) {
            switch (mRecordingStatus) {
                case RECORDING_OFF:
                    SDKLoggerConfig.logD(TAG, "START recording");
                    // start recording
                    mRecordingStatus = RECORDING_ON;
                    eglContext = EGL14.eglGetCurrentContext();
                    maskVideoCapture.startRecording(eglContext);
                    restoreDefaultSurface();
                    break;
                case RECORDING_RESUMED:
                    SDKLoggerConfig.logD(TAG, "RESUME recording");
                    mRecordingStatus = RECORDING_ON;
                    eglContext = EGL14.eglGetCurrentContext();
                    maskVideoCapture.updateSharedContext(eglContext);
                    break;
                case RECORDING_ON:
                    // yay
                    break;
            }
        } else {
            switch (mRecordingStatus) {
                case RECORDING_ON:
                case RECORDING_RESUMED:
                    // stop recording
                    SDKLoggerConfig.logD(TAG, "STOP recording");
                    mRecordingStatus = RECORDING_OFF;
                    maskVideoCapture.stopRecording();
                    restoreDefaultSurface();
                    break;
                case RECORDING_OFF:
                    // yay
                    break;
            }
        }
    }

    private void restoreDefaultSurface() {
        if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            SDKLoggerConfig.logD(TAG,"Changing to default surface failed");
        }
    }

    public void startRecording() {
        mRecordingEnabled = true;
    }

    public void stopRecording() {
        mRecordingEnabled = false;
    }

    public void cancelRecording() {
        mRecordingEnabled = false;
        mRecordingStatus = RECORDING_OFF;
    }

    public void setMaskVideoCapture(MaskVideoCapture maskVideoCapture) {
        this.maskVideoCapture = maskVideoCapture;
    }

}
