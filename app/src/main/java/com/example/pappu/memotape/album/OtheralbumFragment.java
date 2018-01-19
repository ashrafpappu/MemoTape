package com.example.pappu.memotape.album;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.example.pappu.memotape.R;
import com.example.pappu.memotape.Utility.AppUtils;
import com.example.pappu.memotape.Utility.RingStudio;
import com.example.pappu.memotape.activity.AlbumFragmentActvity;
import com.example.pappu.memotape.adapter.PhotGalleryAdapter;
import com.example.pappu.memotape.datamodel.MediaStoreImage;
import com.example.pappu.memotape.datamodel.MediaStoreImageHelper;

import java.util.ArrayList;

public class OtheralbumFragment extends Fragment implements OnItemClickListener {

	private GridView albumSelectGridview;
	private PhotGalleryAdapter adapter;
	private ProgressDialog progress;
	private LayoutInflater inflater;
	private ArrayList<MediaStoreImage> mediaStoreImagelist;
	private ArrayList<String> folderName = new ArrayList<String>();
	public int count = 0;
	MediaStoreImageHelper mediaStoreImageHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.otheralbum_layout, container,
				false);

		AppUtils.hideSystemUI(getActivity());

		albumSelectGridview = (GridView) rootView
				.findViewById(R.id.selectalbumgridview);
		mediaStoreImageHelper = MediaStoreImageHelper
				.getInstance(getActivity());

		progress = new ProgressDialog(getActivity());
		progress.setMessage("Please Wait.. ");
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setIndeterminate(true);
		progress.setCancelable(false);
		progress.show();

		final Thread t = new Thread() {

			@Override
			public void run() {

				mediaStoreImagelist = mediaStoreImageHelper
						.getMediaStoreAlbums();
				adapter = new PhotGalleryAdapter(getActivity(),
						mediaStoreImagelist, R.layout.otheralbum_item_layout,
						R.id.photocount_textview);
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						albumSelectGridview.setAdapter(adapter);
						progress.dismiss();
					}
				});
			}
		};
		t.start();

		albumSelectGridview.setOnItemClickListener(this);

		return rootView;

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

		//if (!((RingStudio) getActivity().getApplicationContext()).selectOn) {

			MediaStoreImage mediaStoreImage = mediaStoreImagelist.get(position);


				((AlbumFragmentActvity) getActivity())
						.setAlbumTitle(mediaStoreImage.displayName);
				((AlbumFragmentActvity) getActivity()).otherAlbumItem = true;
				((AlbumFragmentActvity) getActivity()).bucketId = String
						.valueOf(mediaStoreImage.id);
				((AlbumFragmentActvity) getActivity())
						.addOtherAlbumItemFragment();


//		} else {
//			MediaStoreImage mediaStoreImage = mediaStoreImagelist.get(position);
//			mediaStoreImage.isSelected = !mediaStoreImage.isSelected;
//
//			// Log.i("OtherAlbum", "" + position);
//
//			ViewGroup viewgroup = (ViewGroup) view;
//			RelativeLayout layout = (RelativeLayout) viewgroup.getChildAt(0);
//
//			ImageView gray_image = (ImageView) layout.getChildAt(3);
//			// Log.i("OtherAlbum", ""+position);
//			if (mediaStoreImage.isSelected) {
//				count++;
//				gray_image.setImageResource(R.drawable.common_selection_h);
//				folderName.add(mediaStoreImage.displayName);
//			} else {
//				count--;
//				gray_image.setImageResource(R.drawable.common_selection);
//				folderName.remove(mediaStoreImage.displayName);
//			}
//			if (count < 0) {
//				count = 0;
//			}
//			((AlbumFragmentActvity) getActivity()).setSelectCounterText(String
//					.valueOf(count));
//			((AlbumFragmentActvity) (getActivity()))
//					.deleteTextviewActive(count);
//		}

	}

	void deleteFolderItem() {

		for (int i = 0; i < folderName.size(); ++i) {

			mediaStoreImageHelper.deleteDirectory(mediaStoreImageHelper
					.basePath(folderName.get(i)));

		}
	}

}
