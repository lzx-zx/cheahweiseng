package com.example.user.cheahweiseng.Activity.LodgeProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.cheahweiseng.Activity.Chat.ChatActivity;
import com.example.user.cheahweiseng.Class.LodgeProvider;
import com.example.user.cheahweiseng.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class LodgeProviderActivity extends AppCompatActivity {
    private RecyclerView mLodgeProviderList;

    private DatabaseReference mLodgeProviderDatabase;

    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lodge_provider);

        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLodgeProviderDatabase = FirebaseDatabase.getInstance().getReference().child("Lodge Provider");

        mLayoutManager = new LinearLayoutManager(this);

        mLodgeProviderList = (RecyclerView) findViewById(R.id.lodge_provider_list);
        mLodgeProviderList.setHasFixedSize(true);
        mLodgeProviderList.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<LodgeProvider, LodgeProviderActivity.FriendViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<LodgeProvider, LodgeProviderActivity.FriendViewHolder>(
                LodgeProvider.class, R.layout.lodge_provider_single_layout, LodgeProviderActivity.FriendViewHolder.class, mLodgeProviderDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendViewHolder viewHolder, LodgeProvider model, final int position) {

//                viewHolder.setDate(model.getDate());

                final String list_user_id = getRef(position).getKey();

                mLodgeProviderDatabase.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChildren()){
                        final String userName = dataSnapshot.child("firstName").getValue().toString() + " " + dataSnapshot.child("lastName").getValue().toString();
//                        String thumbnail = dataSnapshot.child("thumbnail").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(userOnline);
                        }
                        viewHolder.setName(userName);
//                        viewHolder.setUserImage(thumbnail, LodgeProviderActivity.this);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent profileIntent = new Intent(LodgeProviderActivity.this, LodgeProviderProfileActivity.class);
                                profileIntent.putExtra("user_id", list_user_id);
                                startActivity(profileIntent);
                            }
                        });

                        viewHolder.mView.setLongClickable(true);
                        viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                CharSequence options[] = new CharSequence[]{"Send message"};

                                final AlertDialog.Builder builder = new AlertDialog.Builder(LodgeProviderActivity.this);

                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if (i == 0) {

                                            Intent chatIntent = new Intent(LodgeProviderActivity.this, ChatActivity.class);
                                            chatIntent.putExtra("user_id", list_user_id);
                                            chatIntent.putExtra("user_name", userName);
                                            startActivity(chatIntent);
                                            finish();
                                        }

                                    }
                                });

                                builder.show();

                                return true;
                            }
                        });
                    }else{
                    }

                        }

                        @Override
                        public void onCancelled (DatabaseError databaseError){

                        }
                });

            }
        };

        mLodgeProviderList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDate(String date) {
            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(date);
        }

        public void setName(String name) {
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setUserImage(String thumbnail, Context context) {
            ImageView userImageView = (ImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(context).load(thumbnail).placeholder(R.drawable.ic_person_black_24dp).into(userImageView);
        }

        public void setUserOnline(String online_status) {
            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

            if (online_status.equals("true")) {

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }
        }
    }
}
