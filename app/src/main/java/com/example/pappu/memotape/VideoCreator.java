package com.example.pappu.memotape;

import android.content.Context;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;

import com.example.pappu.memotape.Utility.AppUtils;
import com.example.pappu.memotape.capture.AudioVedioMerger;
import com.example.pappu.memotape.capture.MaskImageCapture;
import com.example.pappu.memotape.capture.MaskVideoCapture;
import com.example.pappu.memotape.capture.VideoCaptureException;
import com.example.pappu.memotape.glview.GlSurfaceView;
import com.example.pappu.memotape.logger.SDKLoggerConfig;
import com.example.pappu.memotape.render.ImageRenderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;



/**
 * Created by hello on 1/8/18.
 */

public class VideoCreator {
    private MaskVideoCapture maskVideoCapture;
    private Uri audioUri;
    private MaskImageCapture maskImageCapture;
    private int previewHeight,previewWidth;
    private Context context;
    private AudioVedioMerger audioVedioMerger;

    private VideoSaveListener videoSaveListener;
    private ImageSaveListener imageSaveListener;
    private int count = 0;
    private String videoSavePath = "";
    public boolean isCancelVideo = false;
    private boolean isCaptureSessionOnGoing = false;
    private GlSurfaceView glSurfaceView;
    private ImageRenderer imageRenderer;
    private  String TAG = "VideoCreator";

    public VideoCreator(Context context){
        this.context = context;
        audioVedioMerger = new AudioVedioMerger(context);
    }

    public void initVideoCreatorLibrary(ViewGroup parentView, int previewWidth, int previewHeight){
        this.previewHeight = previewHeight;
        this.previewWidth = previewWidth;
        glSurfaceView = new GlSurfaceView(context);
        imageRenderer = new ImageRenderer(context,previewWidth,previewHeight);
        glSurfaceView.setRenderer(imageRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        parentView.addView(glSurfaceView);
        maskVideoCapture = new MaskVideoCapture(imageRenderer,context);
        maskVideoCapture.setVideoCaptureListener(new VideoCaptureListenerImp());
        maskImageCapture = new MaskImageCapture(imageRenderer,context);
        maskImageCapture.setImageCaptureListener(new ImageCaptureListenerImp());
    }

    public void drawCameradata(byte[] data){
        imageRenderer.updateYUVBuffers(data);
        glSurfaceView.requestRender();
    }



    public void setAudioPath(@NonNull Uri audioUri) {
        this.audioUri = audioUri;
        if(maskVideoCapture!=null)
            maskVideoCapture.setAudioPath(audioUri);
    }


    public void setVideoSaveListener(VideoSaveListener videoSaveListener) {
        this.videoSaveListener = videoSaveListener;
    }

    public void changeCameraOrientation(int cameraId) {
        imageRenderer.changeCameraOrientation(cameraId);
    }

    private class ImageCaptureListenerImp implements MaskImageCapture.ImageCaptureListener {

        @Override
        public void onSuccess(String imageSavePath) {
            imageSaveListener.onSuccess(imageSavePath);
        }

        @Override
        public void onFailure(String exception) {
            imageSaveListener.onFailure("capture problem");
        }
    }

    public void setImageSaveListener(ImageSaveListener imageSaveListener) {
        this.imageSaveListener = imageSaveListener;
    }


    public void disposeFaceMaskingJni(){
        isCancelVideo = true;
        maskVideoCapture.destroy();
    }

    public void cancelVideoCapture(){
        isCancelVideo = true;
        maskVideoCapture.stop();
    }


    public void captureImage() {
        maskImageCapture.captureImage();
    }
    public void captureVideo(){
        if (isCaptureSessionOnGoing) {
            SDKLoggerConfig.logD(TAG,"A video session is already on going");
            return;
        }
        try {
            isCancelVideo = false;
            maskVideoCapture.initializeEncoder();
            maskVideoCapture.start();
            isCaptureSessionOnGoing = true;
        } catch (Exception e) {
            videoSaveListener.onFailure(new Exception(e.getMessage(),e.getCause()));
        }

    }
    public void finishVideoCapture(){
        if (!isCaptureSessionOnGoing) {
            SDKLoggerConfig.logD(TAG,"No video session is on going");
            return;
        }
        if (!maskVideoCapture.isReady()) {
            SDKLoggerConfig.logD(TAG,"Video encoder haven't finished initialization");
            return;
        }
        maskVideoCapture.stop();
    }

    private class VideoCaptureListenerImp implements MaskVideoCapture.VideoCaptureListener {

        @Override
        public void onSuccess(String videoFilePath) {
            if (videoSavePath.isEmpty()) {
                String folder_main = AppUtils.getApplicationName(context);
                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), folder_main);
                if (!f.exists()) {
                    f.mkdirs();
                }
                videoSavePath = f.getPath();
            }
            Calendar rightNow = Calendar.getInstance();
            String fileName = "/VID_"+rightNow.get(Calendar.YEAR)+rightNow.get(Calendar.MONTH)+rightNow.get(Calendar.DAY_OF_MONTH)+"_"+System.currentTimeMillis()+".mp4";
            String destinationFilePath = videoSavePath + fileName;
            try {

                if (!isCancelVideo)
                {
                    if(audioUri!=null){
                        audioVedioMerger.mergeAudioWithCapturedVideo(audioUri,destinationFilePath);
                    }
                    else {
                        moveFile(videoFilePath,destinationFilePath);
                    }

                    videoSaveListener.onSuccess(destinationFilePath);
                }
                isCaptureSessionOnGoing = false;
            } catch (IOException e) {
                File file = new File(destinationFilePath);
                file.delete();
                videoSaveListener.onFailure(new Exception("Unable to save video"));
                e.printStackTrace();
            } finally {
                File file = new File(videoFilePath);
                file.delete();
            }
        }

        @Override
        public void onFailure(VideoCaptureException exception) {
            isCaptureSessionOnGoing = false;
            videoSaveListener.onFailure(new Exception(exception.getMessage(),exception.getCause()));
        }
    }


    private void moveFile(String inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            in = new FileInputStream(inputPath );
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

}
