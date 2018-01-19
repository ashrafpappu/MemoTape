package com.example.pappu.memotape.adapter;

import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.pappu.memotape.R;
import com.example.pappu.memotape.Utility.LazyImageLoader;
import com.example.pappu.memotape.Utility.RingStudio;
import com.example.pappu.memotape.datamodel.MediaStoreImage;

import java.util.ArrayList;

public class PhotGalleryAdapter extends ArrayAdapter<MediaStoreImage> {

	private Context context;
	private ArrayList<MediaStoreImage> mediaStoreImagelist;
	private LayoutInflater inflater = null;
	private MediaStoreImage mediaStoreImage = null;

	public PhotGalleryAdapter(Context context,
                              ArrayList<MediaStoreImage> mediaStoreImagelist, int resource,
                              int textViewResourceId) {
		super(context, resource, textViewResourceId, mediaStoreImagelist);
		this.context = context;
		this.mediaStoreImagelist = mediaStoreImagelist;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// if (convertView == null)
		convertView = inflater.inflate(R.layout.otheralbum_item_layout, null);

		ImageView effectIcon = (ImageView) convertView
				.findViewById(R.id.photogallary_imageview);
		ImageView grayIcon = (ImageView) convertView
				.findViewById(R.id.gray_image);

		TextView photocountTextView = (TextView) convertView
				.findViewById(R.id.photocount_textview);
		TextView titleTextView = (TextView) convertView
				.findViewById(R.id.phottitle_textview);

		mediaStoreImage = (MediaStoreImage) mediaStoreImagelist.get(position);
		photocountTextView.setText("" + mediaStoreImage.numberOfPhoto);
		titleTextView.setText(mediaStoreImage.displayName);

		if (((RingStudio) inflater.getContext().getApplicationContext()).selectOn) {
			grayIcon.setVisibility(View.VISIBLE);
		} else {
			grayIcon.setVisibility(View.INVISIBLE);
		}

		if (mediaStoreImage.isSelected) {
			grayIcon.setImageResource(R.drawable.common_selection_h);
		}

		LazyImageLoader.getInstance().displayMediaStoreImage(effectIcon,
				mediaStoreImage.imageId,
				MediaStore.Images.Thumbnails.MICRO_KIND, position, context);

		return convertView;
	}

}
