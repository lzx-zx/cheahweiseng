package com.example.user.cheahweiseng.Activity.Chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.cheahweiseng.Class.Message;
import com.example.user.cheahweiseng.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView mChatList;

    private FirebaseAuth mAuth;
    private static String currentUserId;

    private DatabaseReference mChatDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUserDatabase;

    private LinearLayoutManager mLayoutManager;

    private String chat_id;

    private static String lastMessageType;
    private static String lastMessageFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChatList = (RecyclerView) findViewById(R.id.chat_list);
        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();

        mChatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId);
        mChatDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("LodgeUser");
        mUserDatabase.keepSynced(true);
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Message").child(currentUserId);
        mMessageDatabase.keepSynced(true);

        mLayoutManager = new LinearLayoutManager(ChatListActivity.this);


        mChatList.setHasFixedSize(true);
        mChatList.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Message, ChatListActivity.MessageViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, ChatListActivity.MessageViewHolder>(
                Message.class, R.layout.chat_single_layout, ChatListActivity.MessageViewHolder.class, mMessageDatabase
        ) {
            @Override
            protected void populateViewHolder(final ChatListActivity.MessageViewHolder viewHolder, Message model, int position) {
                final String list_user_id = getRef(position).getKey();


                mUserDatabase.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();

                        System.out.println("LeeZX : " + list_user_id + ", " + userName);

                        String thumbnail = dataSnapshot.child("thumbnail").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(userOnline);
                        }
                        viewHolder.setName(userName);
                        viewHolder.setUserImage(thumbnail, ChatListActivity.this);

                        mMessageDatabase.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                dataSnapshot.getRef().limitToLast(1).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot ds) {
                                        chat_id = ds.getChildren().iterator().next().getKey();
                                        lastMessageType = ds.child(chat_id).child("type").getValue().toString();
                                        lastMessageFrom = ds.child(chat_id).child("from").getValue().toString();
                                        String lastMessage = ds.child(chat_id).child("message").getValue().toString();
                                        String timestamp = ds.child(chat_id).child("time").getValue().toString();

                                        viewHolder.setLastMessage(lastMessage, userName);
                                        viewHolder.setLastMessageTime(timestamp);

                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent chatIntent = new Intent(ChatListActivity.this, ChatActivity.class);
                                                chatIntent.putExtra("user_id", list_user_id);
                                                chatIntent.putExtra("user_name", userName);
                                                startActivity(chatIntent);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        };


        mChatList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public MessageViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }


        public void setName(String name) {
            TextView userNameView = mView.findViewById(R.id.chat_single_name);
            userNameView.setText(name);
        }

        public void setUserImage(String thumbnail, Context context) {
            ImageView userImageView = (ImageView) mView.findViewById(R.id.chat_single_image);
            Picasso.with(context).load(thumbnail).placeholder(R.drawable.ic_person_black_24dp).into(userImageView);
        }

        public void setUserOnline(String online_status) {
            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.chat_single_online_icon);

            if (online_status.equals("true")) {

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }
        }

        public void setLastMessage(String lastMessage, String userName) {
            TextView lastMessageView = (TextView) mView.findViewById(R.id.chat_single_status);
            if (lastMessageType.equals("text")) {
                lastMessageView.setText(lastMessage);
            } else if (lastMessageType.equals("image")) {
                if (currentUserId.equals(lastMessageFrom)) {
                    lastMessageView.setText("You have sent a image.");
                } else {
                    lastMessageView.setText(userName + " have sent a image.");
                }
            }
        }

        public void setLastMessageTime(String lastMessageTime) {
            TextView lastMessageTimeView = (TextView) mView.findViewById(R.id.chat_single_time);

            Date currentTime = new Date();

            Long time = Long.parseLong(lastMessageTime);

            if (currentTime.getTime() - time > 24 * 60 * 60 * 1000) {
                lastMessageTimeView.setText(new SimpleDateFormat("dd/MM/yy hh:mm aa").format(new Date(time)));
            } else if (currentTime.getTime() - time == 24 * 60 * 60 * 1000) {
                lastMessageTimeView.setText(new SimpleDateFormat("hh:mm aa").format(new Date(time)));
            } else if (currentTime.getTime() - time < 24 * 60 * 60 * 1000) {
                lastMessageTimeView.setText(new SimpleDateFormat("hh:mm aa").format(new Date(time)));
            }
        }
    }
}
