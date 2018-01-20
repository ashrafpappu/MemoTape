package com.example.pappu.memotape.album;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;


import com.example.pappu.memotape.R;
import com.example.pappu.memotape.Utility.AppUtils;
import com.example.pappu.memotape.Utility.RingStudio;
import com.example.pappu.memotape.activity.AlbumFragmentActvity;
import com.example.pappu.memotape.adapter.PhotoSelectAdapter;
import com.example.pappu.memotape.datamodel.ImageSelectedItem;
import com.example.pappu.memotape.datamodel.MediaStoreImage;
import com.example.pappu.memotape.datamodel.MediaStoreImageHelper;

import java.util.ArrayList;

public class OtheralbumItemFragment extends android.support.v4.app.Fragment
		implements OnItemClickListener {

	private GridView albumSelectGridview;
	private PhotoSelectAdapter adapter;
	private ArrayList<MediaStoreImage> mediaStoreImagelist;
	private String bucketId;
	MediaStoreImageHelper mediaStoreImageHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.photoselection_activity,
				container, false);

		AppUtils.hideSystemUI(getActivity());

		albumSelectGridview = (GridView) rootView
				.findViewById(R.id.album_gridview);

		bucketId = ((AlbumFragmentActvity) getActivity()).bucketId;

		mediaStoreImageHelper = MediaStoreImageHelper
				.getInstance(getActivity());
		Point point = AppUtils.displayWidth(getActivity());

		final int col = (point.x) / 4;

		new Thread(new Runnable() {

			@Override
			public void run() {

				mediaStoreImagelist = mediaStoreImageHelper
						.getMediaStoreAlbumImageThumbs(bucketId,
								MediaStore.Images.Thumbnails.MICRO_KIND);

				adapter = new PhotoSelectAdapter(getActivity(),
						mediaStoreImagelist,
						R.layout.photogallery_select_item_layout,
						R.id.photocount_textview, col);

				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {

						albumSelectGridview.setAdapter(adapter);
					}
				});

			}
		}).start();

		albumSelectGridview.setOnItemClickListener(this);

		return rootView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {


		MediaStoreImage mediaStoreImage = mediaStoreImagelist.get(position);
		ImageSelectedItem imageSelectedItem = new ImageSelectedItem(mediaStoreImage,position);

		((AlbumFragmentActvity) (getActivity())).addImageOnPhotoSelectionList(imageSelectedItem);

//		Bitmap bitmap = MediaStoreImageHelper.getInstance(this.getContext()).getMediaStoreAlbumImage(
//				mediaStoreImage.imageId);
//
//		Log.d("AlbumFragment","ok>>>>>>>>>"+mediaStoreImage.imageId+"    "+bitmap.getWidth()+"   "+bitmap.getHeight());


	}

	
}
