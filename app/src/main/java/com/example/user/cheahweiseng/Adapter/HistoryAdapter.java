package com.example.user.cheahweiseng.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.cheahweiseng.Class.Lodge;
import com.example.user.cheahweiseng.Activity.Lodge.LodgeHistoryDetailActivity;
import com.example.user.cheahweiseng.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by USER on 2017/10/23.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.LodgeViewHolder> {
    private ArrayList<Lodge> values;
    private Context context;
    private RecyclerView rv;

    public HistoryAdapter(Context context, ArrayList<Lodge> values, RecyclerView rv) {
        this.context = context;
        this.values = values;
        this.rv = rv;
    }

    @Override
    public HistoryAdapter.LodgeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new LodgeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lodge_single_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final HistoryAdapter.LodgeViewHolder holder, final int position) {
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
                Intent intent = new Intent(context, LodgeHistoryDetailActivity.class);
                intent.putExtra("id", values.get(position).getLodgeID());
                intent.putExtra("provider_id", values.get(position).getLodgeProviderID());
                context.startActivity(intent);
            }
        });

        holder.itemView.setLongClickable(true);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Information")
                        .setMessage("Are you sure to delete this Lodge?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class LodgeViewHolder extends RecyclerView.ViewHolder {
        private FirebaseAuth firebaseAuth;

        private ImageView picture;
        private TextView title;
        private TextView price;
        private TextView description;
        private ProgressBar mProgress;

        public LodgeViewHolder(View itemView) {

            super(itemView);
            firebaseAuth = FirebaseAuth.getInstance();
//            databaseReference = FirebaseDatabase.getInstance().getReference().child("Lodge").child(firebaseAuth.getCurrentUser().getUid());
            picture=(ImageView) itemView.findViewById(R.id.lodge_image);
            title =(TextView) itemView.findViewById(R.id.lodge_title);
            price =(TextView) itemView.findViewById(R.id.lodge_price);
            description =(TextView) itemView.findViewById(R.id.lodge_description);
            mProgress = (ProgressBar)itemView.findViewById(R.id.lodge_image_progress);
        }
    }



}
