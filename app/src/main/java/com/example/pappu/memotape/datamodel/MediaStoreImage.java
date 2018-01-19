package com.example.pappu.memotape.datamodel;

import android.graphics.Bitmap;

public class MediaStoreImage {

	public enum MediaType {

		Album, Thumb, Image

	}

	public Bitmap imageData;
	public long id;
	public long imageId;
	public String displayName;
	public int numberOfPhoto;

	public String date,time;
	public MediaType mediaType;
	public Boolean isSelected = false;

}
