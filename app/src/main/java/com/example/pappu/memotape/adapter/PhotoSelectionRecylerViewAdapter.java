package com.example.pappu.memotape.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.pappu.memotape.R;
import com.example.pappu.memotape.datamodel.ImageSelectedItem;
import com.example.pappu.memotape.datamodel.MediaStoreImageHelper;

import java.util.Collections;
import java.util.List;

/**
 * Created by hello on 1/15/18.
 */

public class PhotoSelectionRecylerViewAdapter extends RecyclerView.Adapter<PhotoSelectionRecylerViewAdapter.PhotoSelectionItemViewHolder> {

    private List<ImageSelectedItem>  imageList = Collections.emptyList();
    private int selectedItemPosition = -1;
    private ImageView selectedImageView = null;
    private OnItemClickListener onItemClickListener;
    private Context context;


    public PhotoSelectionRecylerViewAdapter(Context context){
        this.context = context;
    }

    public void setImageList(List<ImageSelectedItem>  imageList){
        this.imageList = imageList;
    }

    public interface OnItemClickListener {
        void onImageItemClicked(ImageSelectedItem imageSelectedItem);
    }
    @Override
    public PhotoSelectionItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_selection_view_item, parent, false);
        return new PhotoSelectionItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PhotoSelectionRecylerViewAdapter.PhotoSelectionItemViewHolder holder, int position) {

        final ImageSelectedItem imageSelectedItem = imageList.get(position);
        if (imageSelectedItem.position == selectedItemPosition) {
            holder.imageItemView.setSelected(true);
            selectedImageView = holder.imageItemView;
        }
        else {
            holder.imageItemView.setSelected(false);
        }
        Bitmap bitmap = null;
        bitmap = MediaStoreImageHelper.getInstance(context).getThumb(
                imageSelectedItem.mediaStoreImage.imageId,
                MediaStore.Images.Thumbnails.MICRO_KIND);



        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                150, 150);

        holder.imageItemView.setLayoutParams(layoutParams);

        holder.imageItemView.setImageBitmap(bitmap);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (selectedItemPosition == imageSelectedItem.position)
                    return;

                selectedItemPosition = imageSelectedItem.position;
                if (selectedImageView != null) {
                    selectedImageView.setSelected(false);
                }
                selectedImageView = holder.imageItemView;
                selectedImageView.setSelected(true);
                if (PhotoSelectionRecylerViewAdapter.this.onItemClickListener != null) {
                    PhotoSelectionRecylerViewAdapter.this.onItemClickListener.onImageItemClicked(imageSelectedItem);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class PhotoSelectionItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageItemView;
        public PhotoSelectionItemViewHolder(View itemView) {
            super(itemView);
            imageItemView = (ImageView) itemView.findViewById(R.id.photo_imageview);
        }
    }

}
