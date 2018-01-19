package com.example.pappu.memotape.datamodel;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MediaStoreImageHelper {

	private Context context;

	private static MediaStoreImageHelper instance;

	private MediaStoreImageHelper(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public static MediaStoreImageHelper getInstance(Context context) {

		if (instance == null)
			instance = new MediaStoreImageHelper(context);

		return instance;

	}

	public ArrayList<MediaStoreImage> getMediaStoreAlbums() {

		Uri imageMediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

		ArrayList<MediaStoreImage> storeAlbumArrayList = new ArrayList<MediaStoreImage>();

		String[] imageProjection = {

		"DISTINCT " + MediaStore.Images.ImageColumns.BUCKET_ID,
				MediaStore.Images.ImageColumns._ID,
				MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME };
		
		
//		Log.i("getMediaStoreAlbums", "Cursoruri start");

		Cursor cursor = context.getContentResolver().query(imageMediaUri,
				imageProjection, null, null,
				MediaStore.Images.ImageColumns.BUCKET_ID + " ASC");
//		Log.i("getMediaStoreAlbums", "Cursoruri end");

		if (cursor != null) {

			int bucketNameIndex = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);

			int bucketIdIndex = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID);

			int imageIdIndex = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns._ID);

			int count = 0;
//			Log.i("getMediaStoreAlbums", "Cursor start");
			while (cursor.moveToNext()) {

				long imageId = cursor.getLong(imageIdIndex);

				MediaStoreImage albumImage = new MediaStoreImage();

				albumImage.mediaType = MediaStoreImage.MediaType.Album;

				albumImage.displayName = cursor.getString(bucketNameIndex);

				albumImage.id = cursor.getLong(bucketIdIndex);
				albumImage.imageId = imageId;

				if (storeAlbumArrayList.size() > 0) {
					if (storeAlbumArrayList.get(storeAlbumArrayList.size() - 1).id != albumImage.id) {

//						Bitmap thumbBitmap = getThumb(imageId,
//								MediaStore.Images.Thumbnails.MICRO_KIND);
//
//						albumImage.imageData = thumbBitmap;

						storeAlbumArrayList.get(storeAlbumArrayList.size() - 1).numberOfPhoto = count;
						count = 0;
						// Log.i("AlbumId: ", "Imagedataid :  " +
						// albumImage.id);

						storeAlbumArrayList.add(albumImage);
					}
				} else {

//					Bitmap thumbBitmap = getThumb(imageId,
//							MediaStore.Images.Thumbnails.MICRO_KIND);
//
//					albumImage.imageData = thumbBitmap;

					storeAlbumArrayList.add(albumImage);
				}

				count++;
			}
//			Log.i("getMediaStoreAlbums", "Cursor end");
			if (storeAlbumArrayList.size() != 0)
				storeAlbumArrayList.get(storeAlbumArrayList.size() - 1).numberOfPhoto = count;

			cursor.close();
		}

		return storeAlbumArrayList;

	}

	public ArrayList<MediaStoreImage> getMediaStoreAlbumImageThumbs(
            String bucketId, int thumbType) {

		Uri imageMediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

		ArrayList<MediaStoreImage> storeThumbArrayList = new ArrayList<MediaStoreImage>();

		String[] imageProjection = {

		"DISTINCT " + MediaStore.Images.ImageColumns.BUCKET_ID,
				MediaStore.Images.ImageColumns._ID,
				MediaStore.Images.ImageColumns.DATE_TAKEN };

		String selection = MediaStore.Images.ImageColumns.BUCKET_ID + "=?";
		String selectionArgs[] = new String[] { bucketId };

		Cursor cursor = context.getContentResolver().query(imageMediaUri,
				imageProjection, selection, selectionArgs, null);
		if (cursor != null) {

			int imageIdIndex = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns._ID);
			int imageIdIndex1 = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);

			while (cursor.moveToNext()) {

				long imageId = cursor.getLong(imageIdIndex);

				long date = cursor.getLong(imageIdIndex1);
				// Log.i("AlbumId: ", "date :  " + date );

				Calendar myCal = Calendar.getInstance();
				myCal.setTimeInMillis(date);
				Date dateText = new Date(myCal.get(Calendar.YEAR) - 1900,
						myCal.get(Calendar.MONTH), myCal.get(Calendar.DATE),
						myCal.get(Calendar.HOUR), myCal.get(Calendar.MINUTE),
						myCal.get(Calendar.SECOND));

				String dateTime = android.text.format.DateFormat.format(
						"yyyy/MM/dd hh:mm:ss", dateText).toString();
				// Log.i("AlbumId: ", "Imagedate :  " + dateTime );
				MediaStoreImage storeThumbImage = new MediaStoreImage();

				storeThumbImage.mediaType = MediaStoreImage.MediaType.Thumb;

//				Bitmap thumbBitmap = getThumb(imageId, thumbType);

				storeThumbImage.date = dateTime.substring(0, 10);
				storeThumbImage.time = dateTime.substring(12, 19);
//				storeThumbImage.imageData = thumbBitmap;

				storeThumbImage.id = imageId;
				storeThumbImage.imageId = imageId;
				storeThumbArrayList.add(storeThumbImage);
			}

			cursor.close();
		}

		return storeThumbArrayList;

	}

	public Bitmap getThumb(long imageId, int thumbType) {

		Bitmap thumbBitmap = MediaStore.Images.Thumbnails.getThumbnail(
				context.getContentResolver(), imageId, thumbType, null);

		return thumbBitmap;

	}

	public Bitmap getMediaStoreAlbumImage(long imageId) {

		Uri imageUri = Uri.withAppendedPath(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				Long.toString(imageId));
		Bitmap bitmap = null;
		try {
//			bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
			
			bitmap = decodeUri(imageUri);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}

	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
               || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
//        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o2);

    }
	
	
	
	
	
	
	
	
	public String getImagePath(long imageId) {
		Uri imageUri = Uri.withAppendedPath(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				Long.toString(imageId));

		return imageUri.toString();
	}

	public static void deleteDirectory(String str) {
		File path = new File(str);
		if(path.exists())
		{
			Log.i("path", "exist-----");
		}
	}

	public static String basePath(String MainAlbumFolder) {

		return Environment.getExternalStorageDirectory() + File.separator
				+ MainAlbumFolder;
	}

}
