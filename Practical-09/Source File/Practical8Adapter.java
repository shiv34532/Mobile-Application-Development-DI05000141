package com.example.arpitapplication;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Practical8Adapter extends RecyclerView.Adapter<Practical8Adapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Practical8Model item);
    }

    private final List<Practical8Model> itemList;
    private final OnItemClickListener listener;

    public Practical8Adapter(List<Practical8Model> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.practical8_item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Practical8Model item = itemList.get(position);

        holder.tvItemTitle.setText(item.getTitle());
        holder.tvItemSubtitle.setText(item.getSubtitle());
        holder.tvBadgeChar.setText(item.getBadgeLetter());

        // Create a rounded circular badge background with item's theme color
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(item.getBadgeColor());
        holder.layoutBadge.setBackground(shape);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout layoutBadge;
        TextView tvBadgeChar, tvItemTitle, tvItemSubtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutBadge = itemView.findViewById(R.id.layoutBadge);
            tvBadgeChar = itemView.findViewById(R.id.tvBadgeChar);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemSubtitle = itemView.findViewById(R.id.tvItemSubtitle);
        }
    }
}
