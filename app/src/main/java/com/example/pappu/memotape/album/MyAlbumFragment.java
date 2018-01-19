package com.example.pappu.memotape.album;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.pappu.memotape.R;
import com.example.pappu.memotape.Utility.AppUtils;
import com.example.pappu.memotape.Utility.FileManager;

import com.example.pappu.memotape.Utility.RingStudio;
import com.example.pappu.memotape.activity.AlbumFragmentActvity;
import com.example.pappu.memotape.adapter.AlbumAdapter;
import com.example.pappu.memotape.adapter.SectionableAdapter;
import com.example.pappu.memotape.datamodel.AlbumImage;

import java.util.ArrayList;
import java.util.Map;

@SuppressLint("NewApi")
public class MyAlbumFragment extends android.support.v4.app.Fragment implements
		AlbumAdapter.OnAlbumItemListener, SectionableAdapter.OnSelectSectionHeaderListener {

	ArrayList<AlbumImage> objs = new ArrayList<AlbumImage>();

	AlbumAdapter adapter;
	private ListView albumListView;
	private int colWidth;
	private Map<String, ArrayList<AlbumImage>> albumImageMap;
	View rootView;
	private LayoutInflater inflater;
	public int selectCounter = 0;
	private ArrayList<AlbumImage> albumimageArraylist;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		this.inflater = inflater;
		rootView = inflater.inflate(R.layout.myalbum_fragment_center_layout,
				container, false);

		AppUtils.hideSystemUI(getActivity());

		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);

		if ((AppUtils.displayWidth(getActivity()).x / metrics.density) < 360) {
			colWidth = AppUtils.displayWidth(getActivity()).x / 3 - 15;
		} else {
			colWidth = AppUtils.displayWidth(getActivity()).x / 4 - 15;
		}



		albumListView = (ListView) rootView
				.findViewById(R.id.sectionedGrid_list);
		setAlbumAdapter();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		setAlbumAdapter();
	};

	private void setAlbumAdapter() {

		albumImageMap = FileManager.readAllImageWithSection(getActivity());

		adapter = new AlbumAdapter(getActivity(), inflater,
				R.layout.album_row_layout, R.id.album_section_header,
				R.id.album_section_header_select, R.id.album_item_holder,
				SectionableAdapter.MODE_VARY_WIDTHS, albumImageMap, colWidth);

		albumListView.setAdapter(adapter);
		albumListView.setDividerHeight(0);
		updateDataSet();
		adapter.setOnAlbumItemListener(this);
		adapter.setOnSelectSectionHeaderListener(this);
	}

	@Override
	public void onAlbumItemClicked(final int realposition, View view,
			String section) {


			AlbumImage albumImage = (AlbumImage) getItem(realposition);

			albumImage.isSelected = !albumImage.isSelected;
			ViewGroup viewgroup = (ViewGroup) view;
			ImageView gray_image = (ImageView) viewgroup.getChildAt(1);

			if (albumImage.isSelected) {
				selectCounter++;
				gray_image.setBackgroundResource(R.drawable.common_selection_h);
				if (sectionImageArraySize(albumImage.section)) {

					((RingStudio) getActivity().getApplicationContext()).selectHeaderArray
							.add(albumImage.section);
					updateDataSet();
				}

			} else {
				selectCounter--;
				gray_image.setBackgroundResource(R.drawable.common_selection);

				if (((RingStudio) getActivity().getApplicationContext()).selectHeaderArray
						.contains(albumImage.section))
					((RingStudio) getActivity().getApplicationContext()).selectHeaderArray
							.remove(albumImage.section);

				if (!sectionImageArraySize(albumImage.section)) {
					// selectHeader.setText("Select");
					updateDataSet();
				}
			}

			setChoosealltextview();

			if (!((RingStudio) getActivity().getApplicationContext()).isTransferselectImage) {
				((AlbumFragmentActvity) (getActivity()))
						.deleteTextviewActive(selectCounter);

				((AlbumFragmentActvity) (getActivity()))
						.setSelectCounterText(String.valueOf(selectCounter));
			}



	}

	public void updateDataSet() {
		adapter.notifyDataSetChanged();
		albumListView.invalidate();
	}

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

	Boolean sectionImageArraySize(String key) {
		int count = 0;
		if (albumImageMap.containsKey(key)) {
			albumimageArraylist = albumImageMap.get(key);
			for (int i = 0; i < albumimageArraylist.size(); ++i) {
				if (albumimageArraylist.get(i).isSelected) {
					count++;
				}
			}
			if (count == albumimageArraylist.size()) {
				return true;
			}
		}

		return false;
	}

	void selectSectionArray(String key, Boolean isSelected) {

		if (albumImageMap.containsKey(key)) {

			ArrayList<AlbumImage> albumimageArraylist = albumImageMap.get(key);

			for (int i = 0; i < albumimageArraylist.size(); ++i) {

				if (albumimageArraylist.get(i).isSelected != isSelected) {
					if (isSelected) {
						selectCounter++;
					} else {
						selectCounter--;
					}
					albumimageArraylist.get(i).isSelected = isSelected;
				}

			}

		}

		if (!((RingStudio) getActivity().getApplicationContext()).isTransferselectImage) {
			((AlbumFragmentActvity) (getActivity()))
					.deleteTextviewActive(selectCounter);
			((AlbumFragmentActvity) (getActivity()))
					.setSelectCounterText(String.valueOf(selectCounter));
		}

	}

	void deleteItem() {

		objs = AppUtils.convertMapToArray(albumImageMap);

		// Log.i("objs--", "size" + objs.size());
		for (int i = 0; i < objs.size(); ++i) {

			if (objs.get(i).isSelected) {

				FileManager.delete(objs.get(i));

			}

		}
	}

	public void sendImageArray() {

		objs = AppUtils.convertMapToArray(albumImageMap);

		for (int i = 0; i < objs.size(); ++i) {

			if (objs.get(i).isSelected) {

				((RingStudio) getActivity().getApplicationContext()).selectedSendImage
						.add(objs.get(i));

			}

		}
	}

	void chooseAll(Boolean isAllSelected) {
		objs = AppUtils.convertMapToArray(albumImageMap);

		int size = objs.size();
		selectCounter = size;
		for (int i = 0; i < size; ++i) {

			objs.get(i).isSelected = isAllSelected;
		}

		if (isAllSelected) {
			((AlbumFragmentActvity) (getActivity()))
					.setSelectCounterText(String.valueOf(selectCounter));

		} else {
			selectCounter = 0;
			((AlbumFragmentActvity) (getActivity()))
					.setSelectCounterText(String.valueOf(selectCounter));

		}
		((AlbumFragmentActvity) (getActivity()))
				.deleteTextviewActive(selectCounter);

	}

	@Override
	public void onSelectSectionHeaderClicked(int position, View v) {

		RelativeLayout RelLay = (RelativeLayout) v.getParent();

		TextView section = (TextView) RelLay.getChildAt(0);

		String sectionHeaderString = section.getText().toString();

		if (!((RingStudio) getActivity().getApplicationContext()).selectHeaderArray
				.contains(sectionHeaderString)) {
			((RingStudio) getActivity().getApplicationContext()).selectHeaderArray
					.add(section.getText().toString());

			selectSectionArray(sectionHeaderString, true);
		} else {
			((RingStudio) getActivity().getApplicationContext()).selectHeaderArray
					.remove(sectionHeaderString);

			selectSectionArray(sectionHeaderString, false);

		}

		setChoosealltextview();

		updateDataSet();

	}

	void setChoosealltextview() {
		if (!((RingStudio) getActivity().getApplicationContext()).isTransferselectImage) {
			if (((RingStudio) getActivity().getApplicationContext()).selectHeaderArray
					.size() == ((RingStudio) getActivity()
					.getApplicationContext()).selectAllHeaderArray.size()) {
				((AlbumFragmentActvity) (getActivity())).chooseAll
						.setText("Cancel All");
				((RingStudio) getActivity().getApplicationContext()).isAllSelected = true;
			} else {
				((AlbumFragmentActvity) (getActivity())).chooseAll
						.setText("Choose All");
				((RingStudio) getActivity().getApplicationContext()).isAllSelected = false;
			}
		}

	}

	@Override
	public void onDestroyView() {
		if (objs.size() > 0) {
			objs.clear();
		}
		
		if (albumimageArraylist !=null) {
			albumimageArraylist.clear();
		}

		super.onDestroyView();
	}

}
