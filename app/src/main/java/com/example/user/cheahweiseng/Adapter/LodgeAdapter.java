package com.example.user.cheahweiseng.Adapter;

/**
 * Created by USER on 2017/11/13.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.cheahweiseng.Class.Lodge;
import com.example.user.cheahweiseng.Activity.Lodge.LodgeDetailActivity;
import com.example.user.cheahweiseng.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class LodgeAdapter extends RecyclerView.Adapter<LodgeAdapter.LodgeViewHolder> {
    private ArrayList<Lodge> values;
    private Context context;
    private static ArrayList<String> key;
    private RecyclerView rv;

    public LodgeAdapter(Context context, ArrayList<Lodge> values, RecyclerView rv) {

        this.context = context;
        this.values = values;
        this.rv = rv;
    }

    @Override
    public LodgeAdapter.LodgeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LodgeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lodge_single_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final LodgeAdapter.LodgeViewHolder holder, final int position) {
        holder.title.setText(values.get(position).getLodgeTitle());
        Picasso.with(context)
                .load(values.get(position).getLodgeImage())// web image url
                .error(R.drawable.ic_error_black_24dp)
                .into(holder.picture, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.mProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });
        holder.price.setText("RM " + values.get(position).getLodgePrice());
        holder.description.setText(values.get(position).getLodgeDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LodgeDetailActivity.class);
                intent.putExtra("id", values.get(position).getLodgeID());
                intent.putExtra("provider_id", values.get(position).getLodgeProviderID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class LodgeViewHolder extends RecyclerView.ViewHolder {
        private ImageView picture;
        private TextView title;
        private TextView price;
        private TextView description;
        private ProgressBar mProgress;

        public LodgeViewHolder(View itemView) {
            super(itemView);
            picture = (ImageView) itemView.findViewById(R.id.lodge_image);
            title = (TextView) itemView.findViewById(R.id.lodge_title);
            price = (TextView) itemView.findViewById(R.id.lodge_price);
            description = (TextView) itemView.findViewById(R.id.lodge_description);
            mProgress = (ProgressBar) itemView.findViewById(R.id.lodge_image_progress);
        }
    }
}
