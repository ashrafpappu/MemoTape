package com.example.pappu.memotape.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.pappu.memotape.R;
import com.example.pappu.memotape.datamodel.AlbumImage;
import com.example.pappu.memotape.datamodel.Orientation;
import com.example.pappu.memotape.datamodel.PreviewInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


/**
 * Created by pappu on 3/14/2017.
 */

public class AppUtils {
    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }
    public static PreviewInfo getadjustedPreview(int parentViewWidth, int parentViewHeight, int previewWidth, int previewHeight, Orientation orientation) {
        PreviewInfo previewInfo = new PreviewInfo();
        int minScreenDimension,maxScreenDimension, minPreviewDimension,maxPreviewDimension;
        minScreenDimension = parentViewWidth < parentViewHeight ? parentViewWidth : parentViewHeight;
        minPreviewDimension = previewWidth < previewHeight ? previewWidth : previewHeight;
        maxPreviewDimension = previewWidth > previewHeight ? previewWidth : previewHeight;
        double ratio = (double) minScreenDimension / minPreviewDimension;
        maxScreenDimension = (int) (maxPreviewDimension * ratio);
        previewInfo.preiviewHeight = maxScreenDimension;
        previewInfo.previewWidth = minScreenDimension;
        previewInfo.offsetX =(parentViewWidth-previewInfo.previewWidth)/2;
        previewInfo.offsetY = (parentViewHeight-previewInfo.preiviewHeight)/2;
        return previewInfo;
    }

        public static String getCurrentDate() {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            return dateFormat.format(date).toString();

        }

        public static Bitmap openFile(Context context, String fileName) {
            Bitmap bm = null;
            try {

                InputStream is = context.getAssets().open(fileName);

                bm = BitmapFactory.decodeStream(is);

                // Reader reader = new InputStreamReader(is);
                // BufferedReader br = new BufferedReader(reader);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return bm;
        }


        public static Bitmap cropImage(Bitmap bmp,double previewRatio) {
            int height, width;
            height = bmp.getWidth();
            width = bmp.getHeight();

            double bitmapRatio = (double) height / width;
//		Log.i("orginal Image", " h " + height + " w " + width);
            int newWidth, newHeight;

            newWidth = (int) (height / previewRatio);
            newHeight = height;

//		Log.i("neworginal Image", " h " + newHeight + " w " + newWidth);

            if (bitmapRatio != previewRatio) {

                if (newWidth > width) {
                    newWidth = width;
                    newHeight = (int) (width * previewRatio);
//				Log.i("newimgorginal Image", " h " + newHeight + " w "
//						+ newWidth);

                }

                int dw = Math.abs(newWidth-width)/2;
                int dh = Math.abs(newHeight-height)/2;
//			Log.i("dpreview", " h " + dh + " w " + dw);
                bmp = Bitmap.createBitmap(bmp, dh, dw, newHeight, newWidth);

            }
//		Log.i("preview", " h " + bmp.getHeight() + " w " + bmp.getWidth()
//				+ " previewRatio :" + previewRatio);
            return bmp;
        }







        public static String getCurrentTime() {

            DateFormat dateFormat = new SimpleDateFormat("HH-mm-ss");
            Date date = new Date();
            return dateFormat.format(date).toString();
        }

        public static void hideSystemUI(Context context) {

            ((Activity) context).getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            ((Activity) context).getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }

        public static ArrayList<AlbumImage> convertMapToArray(
                Map<String, ArrayList<AlbumImage>> albumImageMap) {

            ArrayList<AlbumImage> albumArrayList = new ArrayList<AlbumImage>();

            for (int i = 0; i < albumImageMap.size(); i++) {
                ArrayList<AlbumImage> imageArray = albumImageMap.get(albumImageMap
                        .keySet().toArray()[i]);

                albumArrayList.addAll(imageArray);

            }
            return albumArrayList;
        }

        public static void showView(View view, int len) {

            view.animate().y(len).setDuration(200);

        }

        public static void rotateImageView(Context context, ImageView imageView,
                                           int state) {

            if (state == 1) {
                Animation rotation = AnimationUtils.loadAnimation(context,
                        R.anim.up_rotation);
                imageView.startAnimation(rotation);
            } else {
                Animation rotation = AnimationUtils.loadAnimation(context,
                        R.anim.down_rotation);
                imageView.startAnimation(rotation);
            }

        }

        public static void saveBitmap(Bitmap bitmap) {

            String date = getCurrentDate();
            // Random r = new Random();

            UUID uuid = UUID.randomUUID();
            String time = uuid.toString();
            FileManager.createFolder(date);

            Log.i("image fie", "bitmap: " + date + File.separator + time + ".jpg");
            FileManager.saveOriginalImage(date + File.separator + time + ".jpg",
                    bitmap);

            FileManager.createFolder(date + File.separator
                    + FileManager.ThumbFolder);
            // Log.i("image fie", "bitmap: " + date + File.separator
            // + FileManager.ThumbFolder + File.separator + time + ".jpg");
            FileManager.saveThumb(date + File.separator + FileManager.ThumbFolder
                    + File.separator + time + ".jpg", bitmap);

        }

        public static void savepreviewBitmap(Bitmap bitmap, String name) {

            String date = getCurrentDate();
            // Random r = new Random();

            UUID uuid = UUID.randomUUID();
            String time = uuid.toString();

            FileManager.createFolder(date + File.separator + "preview " + name);
            // Log.i("image fie", "bitmap: " + date + File.separator
            // + FileManager.ThumbFolder + File.separator + time + ".jpg");
            FileManager.saveOriginalImage(date + File.separator + "preview " + name
                    + File.separator + time + ".jpg", bitmap);

        }

        public static Bitmap rotateBitmap(Bitmap source, float angle) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                    source.getHeight(), matrix, true);
        }

        public static void heapsizeCalculate() {
            double max = Runtime.getRuntime().maxMemory() / (1024 * 1024);
            double heapSize = Runtime.getRuntime().totalMemory() / (1024 * 1024);
            double heapRemaining = Runtime.getRuntime().freeMemory()
                    / (1024 * 1024);

            double imagesize = 1952 * 2592 * 3 / (1024 * 1024);

            Log.i("CaptureImage--->", "Data len : " + " max :" + max
                    + " heapSize :" + heapSize + " heapRemaining :" + heapRemaining
                    + ":imageSize :" + imagesize);
        }

        public static Bitmap mirrorBitmap(Bitmap tempBitmap) {
            Matrix matrixMirror = new Matrix();
            matrixMirror.preScale(-1.0f, 1.0f);
            Bitmap bitmapMaster = Bitmap.createBitmap(tempBitmap, 0, 0,
                    tempBitmap.getWidth(), tempBitmap.getHeight(), matrixMirror,
                    false);
            return bitmapMaster;
        }

        public static Point displayWidth(Context context) {
            Display display = ((Activity) context).getWindowManager()
                    .getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            return size;
        }



}
