package com.example.pappu.memotape.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.pappu.memotape.R;
import com.example.pappu.memotape.Utility.AppUtils;
import com.example.pappu.memotape.Utility.RingStudio;
import com.example.pappu.memotape.adapter.PhotoSelectionRecylerViewAdapter;
import com.example.pappu.memotape.album.MyAlbumFragment;
import com.example.pappu.memotape.album.OtheralbumFragment;
import com.example.pappu.memotape.album.OtheralbumItemFragment;
import com.example.pappu.memotape.datamodel.ImageSelectedItem;
import com.example.pappu.memotape.datamodel.MediaStoreImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumFragmentActvity extends FragmentActivity implements
        OnClickListener {

	private OtheralbumFragment otherAlbumFragment;
	private MyAlbumFragment myAlbumFragment;
	private TextView  albumTitle;
	private RelativeLayout albumMainlayout, albumUpperlayout,
			selectUpperlayout, selectBottomLayout;
	private RelativeLayout albumBottomLayout;
	private TextView upperCancel;

	public TextView deleteTextview, chooseAll;
	private String Tag = "CameraActivity";

	private TextView selectCounterTxtView;
	private OtheralbumItemFragment otheralbumItemFragment;
	public String bucketId;
	public Boolean otherAlbumItem = false;
	private PhotoSelectionRecylerViewAdapter photoSelectionRecylerViewAdapter;
	private RecyclerView photoSelectionRecylerView;
	private ArrayList<ImageSelectedItem> imageList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppUtils.hideSystemUI(this);
		setContentView(R.layout.album_fragmentactivity_layout);
		inItViews();
		addOtherAlbumFragment();

	}

	private void inItViews(){
		albumTitle = (TextView) findViewById(R.id.album_title);
		albumMainlayout = (RelativeLayout) findViewById(R.id.album_relativelayout);
		selectUpperlayout = (RelativeLayout) findViewById(R.id.select_upper_layout);
		albumUpperlayout = (RelativeLayout) findViewById(R.id.upper_layout);
		selectBottomLayout = (RelativeLayout) findViewById(R.id.select_bottom_layout);
		albumBottomLayout = (RelativeLayout) findViewById(R.id.album_bottom_layout);
		upperCancel = (TextView) findViewById(R.id.upper_cancel);
		selectCounterTxtView = (TextView) findViewById(R.id.select_count_txtview);
		deleteTextview = (TextView) findViewById(R.id.delete);
		chooseAll = (TextView) findViewById(R.id.choseall);
		photoSelectionRecylerView = (RecyclerView)findViewById(R.id.image_selection_recylce_view);
		addImageSelectionRecyclerView();

		chooseAll.setOnClickListener(this);
		deleteTextview.setOnClickListener(this);
	}


	public void addImageSelectionRecyclerView(){
		photoSelectionRecylerViewAdapter = new PhotoSelectionRecylerViewAdapter(this);
		LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
		photoSelectionRecylerView.setLayoutManager(horizontalLayoutManager);
		photoSelectionRecylerView.setAdapter(photoSelectionRecylerViewAdapter);
	}

	public void addImageOnPhotoSelectionList(ImageSelectedItem imageSelectedItem){
		imageList.add(imageSelectedItem);
		updateImageSelectionRecyclerViewData();

	}


	private void updateImageSelectionRecyclerViewData(){
		photoSelectionRecylerViewAdapter.setImageList(imageList);
		photoSelectionRecylerViewAdapter.notifyDataSetChanged();
	}


	@Override
	protected void onPause() {
		super.onPause();
	}

	public void removeFragment() {
		removeOtherAlbumFragment(otherAlbumFragment);
	}

	public void setSelectCounterText(String count) {

		selectCounterTxtView.setText(count);
	}

	public void setAlbumTitle(String title) {
		albumTitle.setText(title);
	}

	
	@Override
	public void onBackPressed() {
	 if (otherAlbumItem) {
			otherAlbumItem = false;
			setAlbumTitle("Album");
			addOtherAlbumFragment();
		} else {
			super.onBackPressed();
		}

	}

	void selectCancel() {
		selectUpperlayout.setVisibility(View.INVISIBLE);
		selectBottomLayout.setVisibility(View.INVISIBLE);
		albumUpperlayout.setVisibility(View.VISIBLE);
		albumBottomLayout.setVisibility(View.VISIBLE);
	}

	public void transactFragment(Fragment fragment, Boolean remove) {

		FragmentManager fragmentManager = getSupportFragmentManager();

		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		if (remove) {

			fragmentTransaction.remove(fragment);
		}
		else {
			fragmentTransaction.replace(R.id.center_content_frame, fragment);
		}

		fragmentTransaction.commit();

	}

	public void addmyAlbumFragment() {

		myAlbumFragment = (MyAlbumFragment) getMyAlbumFragment();
		transactFragment(myAlbumFragment, false);

	}

	public void addOtherAlbumFragment() {

		otherAlbumFragment = (OtheralbumFragment) getOtherAlbumFragment();
		transactFragment(otherAlbumFragment, false);

	}



	public void addOtherAlbumItemFragment() {

		otheralbumItemFragment = (OtheralbumItemFragment) getOtherAlbumItemFragment();
		transactFragment(otheralbumItemFragment, false);
		// flag = true;

	}

	public void removeOtherAlbumFragment(Fragment fragment) {

		transactFragment(fragment, true);

	}

	private Fragment getMyAlbumFragment() {
		Fragment fragment = null;

		fragment = new MyAlbumFragment();

		return fragment;

	}


	private Fragment getOtherAlbumFragment() {

		Fragment fragment = null;

		fragment = new OtheralbumFragment();

		return fragment;

	}

	private Fragment getOtherAlbumItemFragment() {

		Fragment fragment = null;

		fragment = new OtheralbumItemFragment();

		return fragment;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {



		default:
			break;
		}

	}


	


	public void deleteTextviewActive(int selectCounter) {
		if (selectCounter <= 0) {
			selectCounter = 0;

			deleteTextview.setClickable(false);
		} else {
			deleteTextview.setClickable(true);
		}
	}

}