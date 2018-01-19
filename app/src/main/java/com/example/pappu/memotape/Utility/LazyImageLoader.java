package com.example.pappu.memotape.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.widget.ImageView;


import com.example.pappu.memotape.datamodel.MediaStoreImageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LazyImageLoader {

	private ArrayList<LazyImage> imageList = new ArrayList<LazyImage>();
	private Map<ImageView, Long> imageMap = new HashMap<ImageView, Long>();
	private static LazyImageLoader instance;

	private Context context;
	private LruCache<Long, Bitmap> mMemoryCache = new LruCache<Long, Bitmap>(
			100);
	ImageLoader imageLoader = new ImageLoader();

	public static LazyImageLoader getInstance() {

		if (instance == null) {
			instance = new LazyImageLoader();

		}

		return instance;
	}

	public void displayMediaStoreImage(ImageView imageView, long imageId,
                                       int imageKind, int pos, Context context) {

		LazyImage lazyImage = new LazyImage();

		this.context = context;

		lazyImage.setImageView(imageView);
		lazyImage.setImageId(imageId);
		lazyImage.imageKind = imageKind;

		// imageList.add(lazyImage);

		// if (imageId == 281){
//		Log.i("laz", "" + pos);
		new ImageLoader().execute(lazyImage);
		// }

	}

	public void displayImageFromPath(ImageView imageView, String path) {

	}

	public void displayImageFromUrl(ImageView imageView, String url) {

	}

	void createCacheofImage() {

	}

	void getImageFromCache() {

		// if cache not present create cache

	}

	private class ImageLoader extends AsyncTask<LazyImage, Integer, Boolean> {

		private Bitmap bitmap = null;
		private LazyImage imageinfo;

		@Override
		protected Boolean doInBackground(LazyImage... params) {
			// TODO Auto-generated method stub

			imageinfo = params[0];

//			bitmap = mMemoryCache.get(imageinfo.imageId);

			if (bitmap == null) {

				bitmap = MediaStoreImageHelper.getInstance(context).getThumb(
						imageinfo.getImageId(),
						MediaStore.Images.Thumbnails.MICRO_KIND);

//				mMemoryCache.put(imageinfo.imageId, bitmap);
			}

			return true;
		}

		protected void onPostExecute(Boolean result) {

			imageinfo.getImageView().setImageBitmap(bitmap);

		};

	}

	private class LazyImage {

		private ImageView imageView;
		private long imageId;
		public int imageKind;
		public String path;
		public String url;

		public ImageView getImageView() {
			return imageView;
		}

		public void setImageView(ImageView imageView) {
			this.imageView = imageView;
		}

		public long getImageId() {
			return imageId;
		}

		public void setImageId(long imageId) {
			this.imageId = imageId;
		}

	}
}
