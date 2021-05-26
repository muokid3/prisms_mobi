package com.kemriwellcometrust.dm.prisms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kemriwellcometrust.dm.prisms.R;
import com.kemriwellcometrust.dm.prisms.models.Sms;

import java.util.ArrayList;
import java.util.List;


public class SmsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Sms> items = new ArrayList<>();

    private Context context;
    private OnViewListener onViewListener;

    public interface OnViewListener {
        void onItemClick(int position);
    }
    public void setOnViewListener(OnViewListener onViewListener) {
        this.onViewListener = onViewListener;
    }


    public SmsAdapter(Context context, List<Sms> items) {
        this.items = items;
        this.context = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView time_in;
        public TextView message;
        public TextView sender;
        public Button viewBtn;

        public OriginalViewHolder(View v) {
            super(v);

            time_in = (TextView) v.findViewById(R.id.time_in);
            message = (TextView) v.findViewById(R.id.message);
            sender = (TextView) v.findViewById(R.id.sender);
            viewBtn = (Button) v.findViewById(R.id.viewBtn);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sms, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Sms obj = items.get(position);
        if (holder instanceof OriginalViewHolder) {

            OriginalViewHolder view = (OriginalViewHolder) holder;
            view.time_in.setText(obj.getTimestamp());
            view.message.setText(obj.getText());
            view.sender.setText(obj.getSource());


            view.viewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onViewListener != null) {
                        onViewListener.onItemClick(position);
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

