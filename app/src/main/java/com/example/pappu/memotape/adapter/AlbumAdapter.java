package com.example.pappu.memotape.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.example.pappu.memotape.R;
import com.example.pappu.memotape.Utility.RingStudio;
import com.example.pappu.memotape.datamodel.AlbumImage;

import java.util.ArrayList;
import java.util.Map;

public class AlbumAdapter extends SectionableAdapter {

	private Activity activity;

	private LayoutInflater inflater;
	private int width;

	public interface OnAlbumItemListener {

		void onAlbumItemClicked(int realposition, View v, String s);

	}

	private OnAlbumItemListener onAlbumItemListener;

	public OnAlbumItemListener getOnAlbumItemListener() {
		return onAlbumItemListener;
	}

	public void setOnAlbumItemListener(OnAlbumItemListener onAlbumItemListener) {
		this.onAlbumItemListener = onAlbumItemListener;
	}

	public AlbumAdapter(Activity activity, LayoutInflater inflater,
                        int rowLayoutID, int headerID, int selectID, int itemHolderID,
                        int resizeMode, Map<String, ArrayList<AlbumImage>> albumImageMap,
                        int width) {
		super(inflater, rowLayoutID, headerID, selectID, itemHolderID,
				resizeMode, albumImageMap);
		this.activity = activity;
		this.width = width;
		this.inflater = inflater;

	}

	@Override
	public Object getItem(int position) {

		for (int i = 0; i < albumImageMap.size(); ++i) {
			if (position < albumImageMap.get(
					albumImageMap.keySet().toArray()[i]).size()) {

				return albumImageMap.get(albumImageMap.keySet().toArray()[i])
						.get(position);
			}
			position -= albumImageMap.get(albumImageMap.keySet().toArray()[i])
					.size();
		}
		// This will never happen.
		return null;
	}

	@Override
	protected int getDataCount() {
		int total = 0;
		for (int i = 0; i < albumImageMap.size(); ++i) {
			total += albumImageMap.get(albumImageMap.keySet().toArray()[i])
					.size();
		}
		return total;
	}

	@Override
	protected int getSectionsCount() {

		return albumImageMap.size();
	}

	@Override
	protected int getCountInSection(int index) {
		return albumImageMap.get(albumImageMap.keySet().toArray()[index])
				.size();
	}

	@Override
	protected int getTypeFor(int position) {
		int runningTotal = 0;
		int i = 0;
		for (i = 0; i < albumImageMap.size(); ++i) {
			int sectionCount = albumImageMap.get(
					albumImageMap.keySet().toArray()[i]).size();
			if (position < runningTotal + sectionCount)
				return i;
			runningTotal += sectionCount;
		}
		// This will never happen.
		return -1;
	}

	@Override
	protected String getHeaderForSection(int section) {

		String albumHeader = (String) albumImageMap.keySet().toArray()[section]
				.toString();
		return albumHeader;
	}

	@Override
	protected void bindView(View convertView, final int position) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;

		final AlbumImage albumImage = (AlbumImage) getItem(position);

		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.album_imageview);
		ImageView grayImageView = (ImageView) convertView
				.findViewById(R.id.gray_image);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				width, width);

		if (albumImage.isSelected) {
			grayImageView.setBackgroundResource(R.drawable.common_selection_h);
		} else {
			grayImageView.setBackgroundResource(R.drawable.common_selection);
		}
		imageView.setLayoutParams(layoutParams);

		if (!((RingStudio) inflater.getContext().getApplicationContext()).selectOn) {
			grayImageView.setVisibility(View.INVISIBLE);
		} else {
			grayImageView.setVisibility(View.VISIBLE);
		}

		Bitmap bitmap = BitmapFactory.decodeFile(albumImage.thumbImagePath,
				options);

		imageView.setImageBitmap(bitmap);

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getOnAlbumItemListener().onAlbumItemClicked(position, v,
						albumImage.originalImagePath);

			}
		});

	}

}
