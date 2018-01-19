package com.example.pappu.memotape.adapter;

import android.app.Activity;
import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.example.pappu.memotape.R;
import com.example.pappu.memotape.Utility.LazyImageLoader;
import com.example.pappu.memotape.datamodel.MediaStoreImage;

import java.util.ArrayList;

public class PhotoSelectAdapter extends ArrayAdapter<MediaStoreImage> {

	private Activity context;
	private ArrayList<MediaStoreImage> mediaStoreImagelist;
	private LayoutInflater inflater = null;
	private RelativeLayout selectConfirmLayout;
	private RelativeLayout mainLayout;
	private int width;

	public PhotoSelectAdapter(Activity context,
                              ArrayList<MediaStoreImage> mediaStoreImagelist, int resource,
                              int textViewResourceId, int width) {
		super(context, resource, textViewResourceId, mediaStoreImagelist);
		this.context = context;
		this.mediaStoreImagelist = mediaStoreImagelist;
		this.width = width;

	}

	@Override
	public View getView(final int position, View convertView,
                        final ViewGroup parent) {

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//		if (convertView == null)
			convertView = inflater.inflate(
					R.layout.photogallery_select_item_layout, null);

		ImageView image = (ImageView) convertView
				.findViewById(R.id.photoselect_gallary_imageview);
		selectConfirmLayout = (RelativeLayout) convertView
				.findViewById(R.id.photo_select_layout);

		mainLayout = (RelativeLayout) convertView
				.findViewById(R.id.photo_select_mainlayout);
		MediaStoreImage mediaStoreImage = mediaStoreImagelist.get(position);
	
		makeVisible(mediaStoreImage.isSelected);

//
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				width, width);

		image.setLayoutParams(layoutParams);
//
//		image.setImageBitmap(mediaStoreImage.imageData);

		
		LazyImageLoader.getInstance().displayMediaStoreImage(image,
				mediaStoreImage.imageId, MediaStore.Images.Thumbnails.MICRO_KIND,
				position, context);
		
		
		return convertView;
	}

	void makeVisible(Boolean isSelected) {
		if (isSelected) {
			selectConfirmLayout.setVisibility(View.VISIBLE);
		} else {
			selectConfirmLayout.setVisibility(View.GONE);

		}
	}

}
