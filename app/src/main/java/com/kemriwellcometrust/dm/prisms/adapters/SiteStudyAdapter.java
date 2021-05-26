package com.kemriwellcometrust.dm.prisms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kemriwellcometrust.dm.prisms.R;
import com.kemriwellcometrust.dm.prisms.models.SiteStudy;

import java.util.ArrayList;
import java.util.List;


public class SiteStudyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SiteStudy> items = new ArrayList<>();

    private Context context;
    private OnClickListener onClickListener;
    

    public interface OnClickListener {
        void onItemClick(int position);
    }
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public SiteStudyAdapter(Context context, List<SiteStudy> items) {
        this.items = items;
        this.context = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView study_name;
        public TextView study_detail;
        public Button viewBtn;

        public OriginalViewHolder(View v) {
            super(v);

            study_name = (TextView) v.findViewById(R.id.study_name);
            study_detail = (TextView) v.findViewById(R.id.study_detail);
            viewBtn = (Button) v.findViewById(R.id.viewBtn);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_site_study, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        SiteStudy obj = items.get(position);
        if (holder instanceof OriginalViewHolder) {

            OriginalViewHolder view = (OriginalViewHolder) holder;
            view.study_name.setText(obj.getStudy_name()+" - "+obj.getSite_name());
            view.study_detail.setText(obj.getStudy_detail());



            view.viewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onItemClick(position);
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

