package com.example.pappu.memotape.Utility;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;


import com.example.pappu.memotape.datamodel.AlbumImage;

import java.util.ArrayList;

public class RingStudio extends Application {
	public Boolean isFrontCamera = false, isAllSelected = false;

	public enum FlashMode {
		On, Off, Auto
	}

	public int aspectRatioIndex = 0;

	public enum Transition {
		None, TransitionIn, TransitionOut

	}

	public FlashMode flashMode = FlashMode.Auto;
	// public AspectRatio aspectRatio = AspectRatio.sixteen;

	public Bitmap imageBitmap;
	public byte[] data;

	public String tag = "0";
	public int filterType;

	public Boolean collageOtherAlbum = false;
	public int effectIndex = 1;
	public String activity_Title = "CameraActivity";
	public int zoomValue = 0;
	public ArrayList<Bitmap> collageImge = new ArrayList<Bitmap>();
	public Boolean landscape = false, effect = true, dfault = true,
			selectOn = false;;
	public Boolean isRsBusy = Boolean.valueOf(false);
	public RectF rectG = new RectF();

	public ArrayList<String> selectHeaderArray = new ArrayList<String>();
	public ArrayList<String> selectAllHeaderArray = new ArrayList<String>();
	public Boolean ismediaImage = false;
	public int countsectionArray = 0;
	public String prevTagName = "";
	public String networkName;
	public Rect fixedRect = new Rect();
	public Rect variableRect = new Rect();
	public boolean isSend = false,isTransferselectImage = false;
	public ArrayList<AlbumImage> selectedSendImage = new ArrayList<AlbumImage>();

}
