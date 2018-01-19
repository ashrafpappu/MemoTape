package com.example.pappu.memotape.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Log;

import com.example.pappu.memotape.datamodel.AlbumImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import static Reversed.reversed;
public class FileManager {

	public static String MainAlbumFolder = ".RingStudio";
	public static String ThumbFolder = "thumb";
	public static Boolean isEmpty = false;

	public static int ThumbHeight = 300;

	public static Boolean createFolder(String folderName) {

		File mainFolder = new File(basePath());

		if (!mainFolder.exists()) {
			mainFolder.mkdir();
		}

		File folder = new File(basePath() + File.separator + folderName);

		if (!folder.exists()) {
			folder.mkdir();
		}

		return true;

	}

	public static void pickImage(Activity activity) {

		Intent galleryIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		galleryIntent.setType("image/*");

		// Create chooser
		Intent chooser = Intent.createChooser(galleryIntent, "Pick Image");

		activity.startActivityForResult(chooser, 1);

	}

	public static Boolean saveImage(String imageName, Bitmap bitmapImage) {

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

		Log.i("Saveorginal", "" + basePath() + File.separator + imageName);

		File f = new File(basePath() + File.separator + imageName);

		try {
			f.createNewFile();

			FileOutputStream fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());
			fo.flush();
			fo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// readAllImage();

		return true;
	}

	public static Boolean saveOriginalImage(String imageName, Bitmap bitmapImage) {

		return saveImage(imageName, bitmapImage);

	}

	public static Boolean saveThumb(String imageName, Bitmap bitmapImage) {

		Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(bitmapImage,
				ThumbHeight, ThumbHeight);
		return saveImage(imageName, thumbBitmap);

	}

	public static ArrayList<AlbumImage> readAllImage() {

		ArrayList<AlbumImage> imageArrayList = new ArrayList<AlbumImage>();

		File[] folders = new File(basePath()).listFiles();
		if (folders == null) {
			isEmpty = true;
			return imageArrayList;
		}
		for (File folder : folders) {
			if (folder.isDirectory()) {

				File[] files = new File(folder.toString() + File.separator
						+ ThumbFolder).listFiles();

				if (files != null) {

					for (File file : files) {

						AlbumImage albumImage = new AlbumImage();

						albumImage.originalImagePath = folder.toString()
								+ File.separator + file.getName().toString();
						albumImage.thumbImagePath = file.getPath().toString();
						albumImage.section = folder.getName().toString();

						imageArrayList.add(albumImage);

					}
				}

			}
		}

		return imageArrayList;
	}

	public static String basePath() {

		return Environment.getExternalStorageDirectory() + File.separator
				+ MainAlbumFolder;
	}

	public static Bitmap calculateInSampleSize(Bitmap bm, Point reqReso, int decrease) {

		// *************************

		int width = bm.getWidth();

		int height = bm.getHeight();
		float aspect, scaleWidth = 0, scaleHeight = 0;
		// if(width>height)
		// aspect = (float) height / width;
		// else
		aspect = (float) height / width;

		if (width > height) {
			scaleWidth = (float) (reqReso.x);
			scaleHeight = scaleWidth * aspect; // yeah!
		}
		else
		{
			scaleHeight = (float) (reqReso.y-decrease);
			scaleWidth = scaleHeight / aspect; // yeah!
		}

		Log.i("resizing bitmap"," aspect "+aspect+"height :" + height + "width :" + width
				+ "scaleheight " + scaleHeight + "  scalewidth :" + scaleWidth);
		// create a matrix for the manipulation

		Matrix matrix = new Matrix();

		// resize the bit map

		matrix.postScale(scaleWidth / width, scaleHeight / height);

		// recreate the new Bitmap

		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, true);
		// Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, width, height,
		// true);

		// bm.recycle();

		// *********************

		// Log.i("resizing bitmap",
		// "height :"+height+"width :"+width+"scaleheight "+scaleHeight+"  scalewidth :"+scaleWidth);
		// Log.i("resizing bitmap",
		// "height :"+bm.getHeight()+"width :"+bm.getWidth()+"res "+resizedBitmap.getHeight()+"  res"+resizedBitmap.getWidth());

		if (scaleHeight > height) {
			resizedBitmap = bm.copy(bm.getConfig(), true);
			return resizedBitmap;
		}

		return resizedBitmap;
	}

	public static Bitmap calculateInSampleSize2(Bitmap bm, int reqwidth) {

		// *************************

		int width = bm.getWidth();

		int height = bm.getHeight();
		float aspect, scaleWidth = 0, scaleHeight = 0;
		// if(width>height)
		// aspect = (float) height / width;
		// else
		aspect = (float) width / height;

//		if (width > height) {
			scaleWidth = (float) (reqwidth);
			scaleHeight = scaleWidth / aspect; // yeah!
//		}
//		else
//		{
//			scaleHeight = (float) (reqReso.y);
//			scaleWidth = scaleHeight / aspect; // yeah!
//		}

		Log.i("resizing bitmap", "height :" + height + "width :" + width
				+ "scaleheight " + scaleHeight + "  scalewidth :" + scaleWidth);
		// create a matrix for the manipulation

		Matrix matrix = new Matrix();

		// resize the bit map

		matrix.postScale(scaleWidth / width, scaleHeight / height);

		// recreate the new Bitmap

		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, true);
		// Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, width, height,
		// true);

		// bm.recycle();

		// *********************

		// Log.i("resizing bitmap",
		// "height :"+height+"width :"+width+"scaleheight "+scaleHeight+"  scalewidth :"+scaleWidth);
		// Log.i("resizing bitmap",
		// "height :"+bm.getHeight()+"width :"+bm.getWidth()+"res "+resizedBitmap.getHeight()+"  res"+resizedBitmap.getWidth());

		if (scaleHeight > height) {
			resizedBitmap = bm.copy(bm.getConfig(), true);
			return resizedBitmap;
		}

		return resizedBitmap;
	}

	
	
	
	public static Map<String, ArrayList<AlbumImage>> readAllImageWithSection(
			Context context) {

		((RingStudio) context.getApplicationContext()).countsectionArray++;

		Map<String, ArrayList<AlbumImage>> albumImageMap = new HashMap<String, ArrayList<AlbumImage>>();

		File[] folders = new File(basePath()).listFiles();
		if (folders == null) {
			isEmpty = true;
			return albumImageMap;
		}
		for (File folder : folders) {
			if (folder.isDirectory()) {

				// Log.i("Folader", "name : "+folder.getName());

				if (((RingStudio) context.getApplicationContext()).countsectionArray == 1)
					((RingStudio) context.getApplicationContext()).selectAllHeaderArray
							.add(folder.getName().toString());

				File[] files = new File(folder.toString() + File.separator
						+ ThumbFolder).listFiles();

				if (files != null) {

					ArrayList<AlbumImage> imageArrayList = new ArrayList<AlbumImage>();
					for (File file : files) {

						AlbumImage albumImage = new AlbumImage();

						albumImage.originalImagePath = folder.toString()
								+ File.separator + file.getName().toString();
						albumImage.thumbImagePath = file.getPath().toString();
						albumImage.section = folder.getName().toString();

						imageArrayList.add(albumImage);

					}

					albumImageMap.put(folder.getName().toString(),
							imageArrayList);
				}

			}
		}

		return albumImageMap;
	}

	public static void delete(AlbumImage albumImage) {
		File originalFile = new File(albumImage.originalImagePath);
		originalFile.delete();
		File thumbFile = new File(albumImage.thumbImagePath);
		thumbFile.delete();

	}

}
