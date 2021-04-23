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
import com.kemriwellcome.dm.prisms.models.Stratum;

import java.util.ArrayList;
import java.util.List;


public class StrataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Stratum> items = new ArrayList<>();

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

    public StrataAdapter(Context context, List<Stratum> items) {
        this.items = items;
        this.context = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView stratum_name;
        public Button editBtn;
        public Button deleteBtn;

        public OriginalViewHolder(View v) {
            super(v);

            stratum_name = (TextView) v.findViewById(R.id.stratum_name);
            editBtn = (Button) v.findViewById(R.id.editBtn);
            deleteBtn = (Button) v.findViewById(R.id.deleteBtn);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stratum, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Stratum obj = items.get(position);
        if (holder instanceof OriginalViewHolder) {

            OriginalViewHolder view = (OriginalViewHolder) holder;
            view.stratum_name.setText(obj.getStratum());


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

