package com.example.pappu.memotape.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.pappu.memotape.R;
import com.example.pappu.memotape.datamodel.ImageSelectedItem;

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
        holder.imageItemView.setImageResource(imageSelectedItem.iconImageResourceId);
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
            imageItemView = (ImageView) itemView.findViewById(R.id.frame_selection_panel_recycler_item_iv);
        }
    }

}
