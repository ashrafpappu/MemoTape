package com.example.pappu.memotape.Frame;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.example.pappu.memotape.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class FrameSelectionAdapter extends RecyclerView.Adapter<FrameSelectionAdapter.FrameItemViewHolder> {

    private List<FrameItem> frameItemArrayList = Collections.emptyList();
    private OnItemClickListener onItemClickListener;
    private int selectedItemPosition = -1;
    private ImageView selectedImageView = null;

    public interface OnItemClickListener {
        void onMakItemClicked(FrameItem frameItem);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setFrameItemArrayList(ArrayList<FrameItem> frameItemArrayList) {
        this.frameItemArrayList = frameItemArrayList;
    }

    @Override
    public FrameItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.frame_selection_recycler_view_item, parent, false);
        return new FrameItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FrameItemViewHolder holder, int position) {
        final FrameItem frameItem = frameItemArrayList.get(position);
        if (frameItem.position == selectedItemPosition) {
            holder.frameItemView.setSelected(true);
            selectedImageView = holder.frameItemView;
        }
        else {
            holder.frameItemView.setSelected(false);
        }
        holder.frameItemView.setImageResource(frameItem.iconImageResourceId);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (selectedItemPosition == frameItem.position)
                    return;

                selectedItemPosition = frameItem.position;
                if (selectedImageView != null) {
                    selectedImageView.setSelected(false);
                }
                selectedImageView = holder.frameItemView;
                selectedImageView.setSelected(true);
                if (FrameSelectionAdapter.this.onItemClickListener != null) {
                    FrameSelectionAdapter.this.onItemClickListener.onMakItemClicked(frameItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return frameItemArrayList.size();
    }

    public static class FrameItemViewHolder extends RecyclerView.ViewHolder {
        ImageView frameItemView;
        public FrameItemViewHolder(View itemView) {
            super(itemView);
            frameItemView = (ImageView) itemView.findViewById(R.id.frame_selection_panel_recycler_item_iv);
        }
    }

    public void resetSelection() {
        selectedItemPosition = -1;
        if (selectedImageView != null) {
            selectedImageView.setSelected(false);
            selectedImageView = null;
        }
    }
}
