package com.kemriwellcome.dm.prisms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.kemriwellcome.dm.prisms.R;
import com.kemriwellcome.dm.prisms.models.Site;

import java.util.ArrayList;
import java.util.List;


public class SitesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Site> items = new ArrayList<>();

    private Context context;
    private OnEditListener onEditListener;
    private OnDeleteListener onDeleteListener;

    public interface OnEditListener {
        void onItemClick(int position);
    }
    public void setOnEditListener(OnEditListener onEditListener) {
        this.onEditListener = onEditListener;
    }

    public interface OnDeleteListener {
        void onItemClick(int position);
    }
    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public SitesAdapter(Context context, List<Site> items) {
        this.items = items;
        this.context = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView site_name;
        public Button editBtn;
        public Button deleteBtn;

        public OriginalViewHolder(View v) {
            super(v);

            site_name = (TextView) v.findViewById(R.id.site_name);
            editBtn = (Button) v.findViewById(R.id.editBtn);
            deleteBtn = (Button) v.findViewById(R.id.deleteBtn);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_site, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Site obj = items.get(position);
        if (holder instanceof OriginalViewHolder) {

            OriginalViewHolder view = (OriginalViewHolder) holder;
            view.site_name.setText(obj.getSite_name());


            view.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onEditListener != null) {
                        onEditListener.onItemClick(position);
                    }
                }
            });

            view.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeleteListener != null) {
                        onDeleteListener.onItemClick(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}

