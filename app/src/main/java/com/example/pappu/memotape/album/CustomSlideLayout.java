package com.example.pappu.memotape.album;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.pappu.memotape.R;


public class CustomSlideLayout extends RelativeLayout {
	Context cx;
	int images = 0;

	ImageView title;
	int w = 0, h = 0;
	Boolean firsttime = true;

	public CustomSlideLayout(Context context) {

		super(context);
		cx = context;

	}

	public CustomSlideLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		setGravity(Gravity.CENTER);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.custom_layout, this, true);
		title = (ImageView) getChildAt(0);

	}

	public void changeImage(final Bitmap btm) {

		title.setImageBitmap(btm);

		title.post(new Runnable() {

			@Override
			public void run() {

				if (firsttime) {
					h = title.getHeight();
					w = title.getWidth();
					
//					Log.i("changeImage", "view height :"+h+" view width"+w+" bm height :"+btm.getHeight()+" bm width :"+btm.getWidth());
					
					firsttime = false;
				}

				if (btm.getHeight() > btm.getWidth()) {
					float ratio = (float) btm.getHeight() / btm.getWidth();
					int dif = 0;
					if (h > btm.getHeight()) {
						dif = h - btm.getHeight();
					}


					RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
							(int) ((h - dif-90) / ratio) + getPaddingBottom(),
							h - dif-90);
					layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
					setLayoutParams(layoutParams);
				} else if (btm.getHeight() < btm.getWidth()) {
					float ratio = (float) btm.getWidth() / btm.getHeight();

					RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
							w-50, (int) ((w-50) / ratio) + getPaddingLeft());
					layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
					setLayoutParams(layoutParams);
				} else {
					float ratio = (float) btm.getWidth() / btm.getHeight();

					RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
							w-50, w-50);
					layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
					setLayoutParams(layoutParams);
				}
				
				setVisibility(View.VISIBLE);
				
			}
		});

	}

}
