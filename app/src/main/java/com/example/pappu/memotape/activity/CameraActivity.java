package com.example.pappu.memotape.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.pappu.memotape.Frame.FrameDtoCollection;
import com.example.pappu.memotape.Frame.FrameItem;
import com.example.pappu.memotape.Frame.FrameSelectionAdapter;
import com.example.pappu.memotape.R;
import com.example.pappu.memotape.VideoCreator;
import com.example.pappu.memotape.VideoSaveListener;
import com.example.pappu.memotape.datamodel.CameraData;
import com.example.pappu.memotape.datamodel.Orientation;

import java.util.List;




/**
 * Created by pappu on 12/5/16.
 */


public class CameraActivity extends Activity implements SensorEventListener, Camera.PreviewCallback,View.OnClickListener,VideoSaveListener{

    private Camera mCamera = null;
    private   int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private FrameLayout previewLayout;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    int mFrameHeight,mFrameWidth;
    public Orientation orientation = Orientation.PotraitUp;
    SurfaceTexture surfaceTexture;
    private ImageButton imageCaptureButton, cameraFlipButton;
    private VideoCreator videoCreator;
    private boolean startVideo = true;
    private FrameSelectionAdapter frameSelectionAdapter;
    private RecyclerView frameSelectionRecyclerView;
    private FrameDtoCollection frameDtoCollection = new FrameDtoCollection();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera_activity);
        senSensorManager = (SensorManager) this.getSystemService(
                Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);

        videoCreator = new VideoCreator(this);
        previewLayout = (FrameLayout)findViewById(R.id.preview);
        imageCaptureButton = (ImageButton)findViewById(R.id.capture_imgbutton);
        cameraFlipButton = (ImageButton)findViewById(R.id.cameraflip_imagebutton);
        frameSelectionRecyclerView = (RecyclerView)findViewById(R.id.frame_selection_panel_recycler_view);
        frameDtoCollection.loadFrametem();


        imageCaptureButton.setOnClickListener(this);
        cameraFlipButton.setOnClickListener(this);
        setUpFrameSelectionRecyclerView();
        setCamera();
        videoCreator.initVideoCreatorLibrary(previewLayout,mFrameWidth,mFrameHeight);
        videoCreator.setVideoSaveListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.capture_imgbutton:
//                Toast.makeText(this,"Saved Image Successfully", Toast.LENGTH_SHORT).show();
//                videoCreator.captureImage();

                if(startVideo){
                    videoCreator.captureVideo();
                    startVideo = false;
                }
                else {
                    videoCreator.finishVideoCapture();
                    startVideo = true;
                }


                break;
            case R.id.cameraflip_imagebutton:
                flipcamera();
                break;
        }
    }

    private void flipcamera() {

        if (mCamera != null)
        {
            releaseCamera();

        }
        if(mCameraId==Camera.CameraInfo.CAMERA_FACING_BACK){
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            videoCreator.changeCameraOrientation(CameraData.frontCamera);
        }
        else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            videoCreator.changeCameraOrientation(CameraData.backCamera);
        }
        setCamera();

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
       videoCreator.drawCameradata(data);
    }




    void setCamera(){

        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewFormat(ImageFormat.NV21);
        Camera.Size previewSize = params.getPreviewSize() ;

        List<Camera.Size> prevsizeArray =  params.getSupportedPreviewSizes();

        for(int i=0;i<prevsizeArray.size();i++){

            int width = prevsizeArray.get(i).width,height = prevsizeArray.get(i).height;
            float ratio = (float)width/height;
            if(ratio==(float)16/9 && height<=720){
                previewSize.width = width;
                previewSize.height = height;
                break;
            }

        }
        mFrameHeight = previewSize.height;
        mFrameWidth = previewSize.width;
        params.setPreviewSize(mFrameWidth,mFrameHeight);

        mCamera.setParameters(params);
        try {
            surfaceTexture = new SurfaceTexture(0);
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();

        } catch (Exception error ) {
            error.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    void setUpFrameSelectionRecyclerView() {
        frameSelectionAdapter = new FrameSelectionAdapter();
        frameSelectionAdapter.setOnItemClickListener(onFrameItemClickListener);
        frameSelectionAdapter.setFrameItemArrayList(frameDtoCollection.getMaskItemArrayList());
        frameSelectionAdapter.notifyDataSetChanged();
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        frameSelectionRecyclerView.setLayoutManager(horizontalLayoutManager);
        frameSelectionRecyclerView.setAdapter(frameSelectionAdapter);
    }

    private FrameSelectionAdapter.OnItemClickListener onFrameItemClickListener = new FrameSelectionAdapter.OnItemClickListener() {
        @Override
        public void onMakItemClicked(FrameItem frameItem) {

        }
    };
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            if ((sensorEvent.values[0] < 3 && sensorEvent.values[0] > -3)
                    && (sensorEvent.values[1] < 3 && sensorEvent.values[1] > -3)) {
               orientation = Orientation.Flat;

            } else {
                if (Math.abs(sensorEvent.values[1]) > Math
                        .abs(sensorEvent.values[0])) {

                    if (sensorEvent.values[1] < 0) {
                       orientation = Orientation.PotraitDown;

                    } else {
                        orientation = Orientation.PotraitUp;
                    }

                } else {
                    if (sensorEvent.values[0] < 0) {
                        orientation = Orientation.LandscapeDown;

                    } else {
                        orientation = Orientation.LandscapeUp;

                    }

                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public  Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open(mCameraId);
        } catch (Exception error) {
            error.printStackTrace();
            Log.e("mainactivity", "[ERROR] Camera open failed." + error.getMessage());
        }

        return camera;
    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        senSensorManager.unregisterListener(this);
        releaseCamera();
        super.onDestroy();
    }

    public void releaseCamera() {
        if (mCamera != null) {
            try{
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                surfaceTexture.release();
                mCamera.release();
                mCamera = null;
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onFailure(Exception exception) {

    }

    @Override
    public void onSuccess(String videoFilePath) {


        Log.d("Video","Success>>>>>");
    }

}
