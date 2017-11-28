package com.example.user.cheahweiseng.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.user.cheahweiseng.Class.Message;
import com.example.user.cheahweiseng.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by USER on 22/10/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    public final static int CURRENT_USER = 0, CHAT_USER = 1;
    public final static String TEXT = "text", IMAGE = "image";
    private List<Message> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private Context ctx;

    public MessageAdapter(List<Message> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutRes = 0;
        ctx = parent.getContext();
        layoutRes = R.layout.message_single_layout;
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int position) {
        viewHolder.timeLayout.setVisibility(View.GONE);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.timeLayout.isShown()){
                    viewHolder.timeLayout.setVisibility(View.GONE);
                }else{
                    viewHolder.timeLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        final Message c = mMessageList.get(position);

        String from_user = c.getFrom();
        final String message_type = c.getType();

        if (from_user.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            viewHolder.messageText.setBackgroundResource(R.drawable.rounded_corner_msg_out);
            viewHolder.messageText.setTextColor(ContextCompat.getColor(ctx, R.color.White));

            viewHolder.imageLayout.setBackgroundResource(R.drawable.rounded_corner_msg_out);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.imageLayout.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.setMargins(0,10,10,10);
            viewHolder.imageLayout.setLayoutParams(layoutParams);

            viewHolder.timeLayout.setGravity(Gravity.RIGHT);
            viewHolder.messageLayout.setGravity(Gravity.RIGHT);

        } else if (!from_user.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            viewHolder.messageText.setBackgroundResource(R.drawable.rounded_corner_msg_in);
            viewHolder.messageText.setTextColor(ContextCompat.getColor(ctx, R.color.Black));

            viewHolder.imageLayout.setBackgroundResource(R.drawable.rounded_corner_msg_in);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.imageLayout.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.setMargins(10,10,0,10);
            viewHolder.imageLayout.setLayoutParams(layoutParams);

            viewHolder.timeLayout.setGravity(Gravity.LEFT);
            viewHolder.messageLayout.setGravity(Gravity.LEFT);
        }

//        viewHolder.timeLayout.setVisibility(View.INVISIBLE);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("LodgeUser").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (message_type.equals("text")) {
                    viewHolder.messageText.setText(c.getMessage());
                    viewHolder.imageLayout.setVisibility(View.INVISIBLE);
                    viewHolder.progressBar.setVisibility(View.GONE);
//                    viewHolder.timeText

                } else {
                    viewHolder.messageText.setVisibility(View.INVISIBLE);

                    viewHolder.progressBar.setVisibility(View.VISIBLE);

                    Picasso.with(viewHolder.messageImage.getContext()).setIndicatorsEnabled(false);
                    Picasso.with(viewHolder.messageImage.getContext())
                            .load(c.getMessage())
                            .error(R.drawable.ic_error_black_24dp)
                            .into(viewHolder.messageImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    if (viewHolder.progressBar != null) {
                                        viewHolder.progressBar.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onError() {
                                    if (viewHolder.progressBar != null) {
                                        viewHolder.progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });

                }

                Date currentTime = new Date();
                if (currentTime.getTime() - c.getTime() > 24 * 60 * 60 * 1000) {
                    viewHolder.timeText.setText(new SimpleDateFormat("dd/MM/yy hh:mm aa").format(new Date(c.getTime())));
                } else if (currentTime.getTime() - c.getTime() == 24 * 60 * 60 * 1000) {
                    viewHolder.timeText.setText(new SimpleDateFormat("hh:mm aa").format(new Date(c.getTime())));
                } else if (currentTime.getTime() - c.getTime() < 24 * 60 * 60 * 1000) {
                    viewHolder.timeText.setText(new SimpleDateFormat("hh:mm aa").format(new Date(c.getTime())));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout timeLayout, messageLayout, imageLayout;
        public TextView messageText;
        public ImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;
        public TextView timeText;
        public ProgressBar progressBar;

        public MessageViewHolder(View view) {
            super(view);

            timeLayout = (RelativeLayout) view.findViewById(R.id.time_layout);
            messageLayout = (RelativeLayout) view.findViewById(R.id.message_layout);
            imageLayout = (RelativeLayout) view.findViewById(R.id.image_layout);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
//            profileImage = (ImageView) view.findViewById(R.id.message_profile_layout);
//            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            timeText = (TextView) view.findViewById(R.id.time_text_layout);


            progressBar = (ProgressBar) view.findViewById(R.id.message_progress_layout);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    @Override
    public int getItemViewType(final int position) {
        Message m = mMessageList.get(position);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        if (m.getFrom().equals(mCurrentUserId)) {
            return CURRENT_USER;
        } else if (!m.getFrom().equals(mCurrentUserId)) {
            return CHAT_USER;
        } else {
            return 2;
        }
    }
}