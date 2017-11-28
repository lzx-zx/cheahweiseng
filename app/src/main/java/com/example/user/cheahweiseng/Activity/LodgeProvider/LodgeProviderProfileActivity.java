package com.example.user.cheahweiseng.Activity.LodgeProvider;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cheahweiseng.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

//import static com.example.user.cheahweiseng.Activity.MainActivity.subscriptionTopic;

public class LodgeProviderProfileActivity extends AppCompatActivity {
    
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private TextView ID;
    private TextView IC;
    private TextView Email;
    private TextView PHN;
    private Button btnUpdate;
    private String password, ic;
    private DataSnapshot dataSnapshot;
    private TextView mTextView;
    private ImageView editName, editIC;
    private DatabaseReference mRootRef;
    private Button mSubscribe;

    private FirebaseUser mCurrentUser;

    private ProgressDialog mProgress;
//    private MqttAndroidClient mqttAndroidClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lodge_provider_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextView = (TextView) findViewById(R.id.lodge_provider_name);
        ID = (TextView) findViewById(R.id.lodge_provider_name);
        IC = (TextView) findViewById(R.id.lodge_provider_ic);
        Email = (TextView) findViewById(R.id.lodge_provider_email);
        PHN = (TextView) findViewById(R.id.lodge_provider_phone);
        mSubscribe = (Button) findViewById(R.id.lodge_provider_subscribe);


        final String user_id = getIntent().getStringExtra("user_id");

        mRootRef = FirebaseDatabase.getInstance().getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Lodge Provider").child(user_id);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading Data...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("firstName").getValue().toString() + " " + dataSnapshot.child("lastName").getValue().toString();
//                String status = dataSnapshot.child("status").getValue().toString();
//                final String profile_image = dataSnapshot.child("image").getValue().toString();

                ID.setText(display_name);
                IC.setText(dataSnapshot.child("ic").getValue().toString());
                Email.setText(dataSnapshot.child("email").getValue().toString());
                PHN.setText(dataSnapshot.child("phoneNumber").getValue().toString());

                mProgress.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
//                mProfileStatus.setText(status);

//                Picasso.with(ProfileActivity.this).load(profile_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar).into(mProfileImage, new Callback() {
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                    @Override
//                    public void onError() {
//                        Picasso.with(ProfileActivity.this).load(profile_image).placeholder(R.drawable.avatar).into(mProfileImage);
//                    }
//                });

//                if (mCurrentUser.getUid().equals(user_id)) {
//
//                    mDeclineBtn.setEnabled(false);
//                    mDeclineBtn.setVisibility(View.INVISIBLE);
//
//                    mProfileSendRequest.setEnabled(false);
//                    mProfileSendRequest.setVisibility(View.INVISIBLE);
//
//                }

//                mFriendRequestDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        if (dataSnapshot.hasChild(user_id)) {
//                            String request_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
//
//                            if (request_type.equals("received")) {
//                                current_state = "req_received";
//                                mProfileSendRequest.setText("Accept Friend Request");
//
//                                mDeclineBtn.setVisibility(View.VISIBLE);
//                                mDeclineBtn.setEnabled(true);
//
//                            } else if (request_type.equals("sent")) {
//                                current_state = "req_sent";
//                                mProfileSendRequest.setText("Cancel Friend Request");
//
//                                mDeclineBtn.setVisibility(View.INVISIBLE);
//                                mDeclineBtn.setEnabled(false);
//                            }
//                            mProgress.dismiss();
//                        } else {
//                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.hasChild(user_id)) {
//                                        current_state = "friend";
//                                        mProfileSendRequest.setText("Unfriend this Person");
//
//                                        mDeclineBtn.setVisibility(View.INVISIBLE);
//                                        mDeclineBtn.setEnabled(false);
//                                    }
//                                    mProgress.dismiss();
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//                                    mProgress.dismiss();
//                                }
//                            });
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });

//        mProfileSendRequest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                mProfileSendRequest.setEnabled(false);
//
//                if (current_state.equals("not_friend")) {
//                    DatabaseReference newNotificationRef = mRootRef.child("Notification").child(user_id).push();
//                    String newNotificationId = newNotificationRef.getKey();
//
//                    HashMap<String, String> notificationMap = new HashMap<>();
//                    notificationMap.put("from", mCurrentUser.getUid());
//                    notificationMap.put("type", "request");
//
//                    Map requestMap = new HashMap();
//                    requestMap.put("Friend_Request/" + mCurrentUser.getUid() + "/" + user_id + "/request_type", "sent");
//                    requestMap.put("Friend_Request/" + user_id + "/" + mCurrentUser.getUid() + "/request_type", "received");
//                    requestMap.put("Notification/" + user_id + "/" + newNotificationId, notificationMap);
//
//                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                            if (databaseError != null) {
//                                Toast.makeText(ProfileActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();
//                            } else {
//                                current_state = "req_sent";
//                                mProfileSendRequest.setText("Cancel Friend Request");
//                            }
//                            mProfileSendRequest.setEnabled(true);
//                        }
//                    });
//                }
//
//                if (current_state.equals("req_sent")) {
//                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    mProfileSendRequest.setEnabled(true);
//                                    current_state = "not_friend";
//                                    mProfileSendRequest.setText("Send Friend Request");
//
//                                    mDeclineBtn.setVisibility(View.INVISIBLE);
//                                    mDeclineBtn.setEnabled(false);
//                                }
//                            });
//                        }
//                    });
//                }
//
//                if (current_state.equals("req_received")) {
//                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
//
//                    Map subscriberMap = new HashMap();
//                    subscriberMap.put("Friend/" + mCurrentUser.getUid() + "/" + user_id + "/date", currentDate);
//                    subscriberMap.put("Friend/" + user_id + "/" + mCurrentUser.getUid() + "/date", currentDate);
//
//                    subscriberMap.put("Friend_Request/" + mCurrentUser.getUid() + "/" + user_id, null);
//                    subscriberMap.put("Friend_Request/" + user_id + "/" + mCurrentUser.getUid(), null);
//
//                    mRootRef.updateChildren(subscriberMap, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                            if (databaseError == null) {
//
//                                mProfileSendRequest.setEnabled(true);
//                                current_state = "friend";
//                                mProfileSendRequest.setText("Unfriend this Person");
//
//                                mDeclineBtn.setVisibility(View.INVISIBLE);
//                                mDeclineBtn.setEnabled(false);
//
//                            } else {
//
//                                String error = databaseError.getMessage();
//
//                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
//
//
//                            }
//                        }
//                    });
//                }
//
//                if (current_state.equals("friend")) {
//                    Map unsubscriberMap = new HashMap();
//                    unsubscriberMap.put("Friend/" + mCurrentUser.getUid() + "/" + user_id, null);
//                    unsubscriberMap.put("Friend/" + user_id + "/" + mCurrentUser.getUid(), null);
//
//                    mRootRef.updateChildren(unsubscriberMap, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                            if (databaseError == null) {
//
//                                current_state = "not_friend";
//                                mProfileSendRequest.setText("Send Friend Request");
//
//                                mDeclineBtn.setVisibility(View.INVISIBLE);
//                                mDeclineBtn.setEnabled(false);
//
//                            } else {
//
//                                String error = databaseError.getMessage();
//
//                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
//
//
//                            }
//
//                            mProfileSendRequest.setEnabled(true);
//                        }
//                    });
//                }
//            }
//        });
//    }

        });

        mSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //subscribe
                Map subscriberMap = new HashMap();
                subscriberMap.put("Subscriber/" + mCurrentUser.getUid() + "/" + user_id + "/date", ServerValue.TIMESTAMP);
                subscriberMap.put("Subscriber/" + user_id + "/" + mCurrentUser.getUid() + "/date", ServerValue.TIMESTAMP);

                mRootRef.updateChildren(subscriberMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
//                            subscribeToTopic();
                        } else {
                            String error = databaseError.getMessage();
                            Toast.makeText(LodgeProviderProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

//    public void subscribeToTopic() {
//        try {
//            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    addToHistory("Subscribed!");
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    addToHistory("Failed to subscribe");
//                }
//            });
//
//            // THIS DOES NOT WORK!
//            mqttAndroidClient.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
//                @Override
//                public void messageArrived(String topic, MqttMessage message) throws Exception {
//                    // message Arrived!
//                    System.out.println("Message: " + topic + " : " + new String(message.getPayload()));
//                    publishMessage();
//                }
//            });
//
//        } catch (MqttException ex) {
//            System.err.println("Exception whilst subscribing");
//            ex.printStackTrace();
//        }
//    }
//
//    public void publishMessage() {
//
//        try {
//            MqttMessage message = new MqttMessage();
//            message.setPayload(publishMessage.getBytes());
//            mqttAndroidClient.publish(publishTopic, message);
//            addToHistory("Message Published");
//            if (!mqttAndroidClient.isConnected()) {
//                addToHistory(mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
//            }
//        } catch (MqttException e) {
//            System.err.println("Error Publishing: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
}
