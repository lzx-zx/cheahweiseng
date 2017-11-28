package com.example.user.cheahweiseng.Activity.Chat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.user.cheahweiseng.*;
import com.example.user.cheahweiseng.Class.Message;
import com.example.user.cheahweiseng.Adapter.MessageAdapter;
import com.example.user.cheahweiseng.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class ChatActivity extends AppCompatActivity {

    private RelativeLayout timeLayout;

    private String mChatUser, mCurrentUser;

    private DatabaseReference mRootRef;

    private TextView mTitleView, mLastSeenView;
    private ImageView mProfileImage;

    private FirebaseAuth mAuth;

    private ImageButton mAddBtn, mSendBtn;
    private EditText mChatMessage;

    private RecyclerView mMessageList;

    private final List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;

    private static final int GALLERY_PICK = 0, CAMERA = 1;
    private StorageReference mImageStorage;
    private String mCurrentPhotoPath;

    private ProgressDialog mProgress;

    private File thumb_filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.user.cheahweiseng.R.layout.activity_chat);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mCurrentUser = mAuth.getCurrentUser().getUid();

        mChatUser = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("user_name");

        System.out.println("LeeZX : " + userName);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(actionBarView);

        mAddBtn = (ImageButton) findViewById(R.id.chat_add_btn);
        mSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        mChatMessage = (EditText) findViewById(R.id.chat_message_view);

        mTitleView = (TextView) findViewById(R.id.custom_bar_title);
        mLastSeenView = (TextView) findViewById(R.id.custom_bar_seen);
        mProfileImage = (ImageView) findViewById(R.id.custom_bar_image);

        mAdapter = new MessageAdapter(messageList);

        mMessageList = (RecyclerView) findViewById(R.id.messages_list);
        mLinearLayout = new LinearLayoutManager(this);
//        mLinearLayout.setReverseLayout(true);
        mLinearLayout.setStackFromEnd(true);

        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLinearLayout);
//        mMessageList.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayout) {
//            @Override
//            public void onLoadMore(int current_page) {
//                messageList.clear();
//                mCurrentPage = current_page;
//                loadMessage();
//            }
//        });

        mMessageList.setAdapter(mAdapter);

        loadMessage();

        mImageStorage = FirebaseStorage.getInstance().getReference();


        mTitleView.setText(userName);

        mRootRef.child("Lodge Provider").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
//                String image = dataSnapshot.child("image").getValue().toString();

                if (online.equals("true")) {
                    mLastSeenView.setText("Online");
                } else {
                    GetTimeAgo getTimeAgo = new GetTimeAgo();

                    long lastTime = Long.parseLong(online);

                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());
                    mLastSeenView.setText(lastSeenTime);
                }
//                Picasso.with(ChatActivity.this).load(image).placeholder(R.drawable.ic_person_black_24dp).into(mProfileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("Chat").child(mCurrentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(mChatUser)) {

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUser + "/" + mChatUser, chatAddMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + mCurrentUser, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {

                                Log.d("CHAT_LOG", databaseError.getMessage().toString());

                            }
                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                sendMessage();
            }
        });

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder imageDialog = new AlertDialog.Builder(ChatActivity.this);
                imageDialog.setTitle("Select Action");
                CharSequence[] dialogItems = {"Gallery", "Camera"};
                imageDialog.setItems(dialogItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                chooseImageFromGallery();
                            case 1:
                                checkPermission();
                        }
                    }
                });

                imageDialog.show();
            }
        });

        timeLayout = (RelativeLayout) findViewById(R.id.time_layout);
    }

//    public void subscribeToTopic() {
//        try {
//            client.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//
////                    addToHistory("Subscribed!");
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
////                    addToHistory("Failed to subscribe");
//                }
//            });
//
//            // THIS DOES NOT WORK!
//            client.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
//                @Override
//                public void messageArrived(String topic, MqttMessage message) throws Exception {
//                    // message Arrived!
//                    System.out.println("Message: " + topic + " : " + new String(message.getPayload()));
//                }
//            });
//
//        } catch (MqttException ex) {
//            System.err.println("Exception whilst subscribing");
//            ex.printStackTrace();
//        }
//    }

//    public void publishMessage() {
//
//        try {
//            MqttMessage message = new MqttMessage();
//            message.setPayload(publishMessage.getBytes());
//            client.publish(publishTopicChatUser, message);
////            addToHistory("Message Published");
//            if (!client.isConnected()) {
////                addToHistory(mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
//            }
//        } catch (MqttException e) {
//            System.err.println("Error Publishing: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA);

        } else if (ContextCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA);
        } else if (ContextCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA);
        } else {
            captureImageUsingCamera();
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private void captureImageUsingCamera() {
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.N) {
            Intent cameraIntent = new Intent();
            cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException e) {
                e.printStackTrace();
            }

            String authorities = getApplicationContext().getPackageName() + ".provider";
            Uri imageUri = FileProvider.getUriForFile(this, authorities, photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, CAMERA);
        } else {
            Intent cameraIntent = new Intent();
            cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException e) {
                e.printStackTrace();
            }

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(cameraIntent, CAMERA);
        }
    }

    private void chooseImageFromGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureImageUsingCamera();
            } else {
                checkPermission();
            }
        }
    }

    private void loadMessage() {
//        DatabaseReference mMessageRef = mRootRef.child("Message").child(mCurrentUser).child(mChatUser);
        messageList.clear();
        mRootRef.child("Message").child(mCurrentUser).child(mChatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Message message = dataSnapshot.getValue(Message.class);

                messageList.add(message);
                for (int i = 0; i < messageList.size(); i++) {
                    if (messageList.get(i).getMessage().isEmpty()) {
                        messageList.remove(i);
                    }
                }

                mMessageList.scrollToPosition(messageList.size() - 1);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage() {
        final String message = mChatMessage.getText().toString();

        if (!TextUtils.isEmpty(message)) {

            String current_user_ref = "Message/" + mCurrentUser + "/" + mChatUser;
            String chat_user_ref = "Message/" + mChatUser + "/" + mCurrentUser;

            DatabaseReference user_message_push = mRootRef.child("Message").child(mCurrentUser).child(mChatUser).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUser);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mChatMessage.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    } else {
//                        publishTopicChatUser = publishTopicChatUser + "/" + mChatUser + "/" + mCurrentUser;
//                        publishMessage = mCurrentUser + ":" + message;
//                        publishMessage();
//                        if (mCurrentUser.equals(mAuth.getCurrentUser().getUid())) {
//                            topic = topic + mCurrentUser;
//                        } else {
//                            topic = topic + mChatUser;
//                        }
                        loadMessage();
                    }
                }
            });
        }
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
                Uri resultUri = data.getData();

                int sdkVersion = Build.VERSION.SDK_INT;
                if (sdkVersion <= Build.VERSION_CODES.HONEYCOMB) {
                    thumb_filePath = new File(getRealPathFromURI_BelowAPI11(ChatActivity.this, resultUri));
                } else if (sdkVersion <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    thumb_filePath = new File(getRealPathFromURI_API11to18(ChatActivity.this, resultUri));
                } else if (sdkVersion >= Build.VERSION_CODES.KITKAT) {
                    thumb_filePath = new File(getRealPathFromURI_API19(ChatActivity.this, resultUri));
                }


                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(800)
                        .setMaxHeight(600)
                        .setQuality(100)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                final String current_user_ref = "Message/" + mCurrentUser + "/" + mChatUser;
                final String chat_user_ref = "Message/" + mChatUser + "/" + mCurrentUser;

                DatabaseReference user_message_push = mRootRef.child("Message").child(mCurrentUser).child(mChatUser).push();

                final String push_id = user_message_push.getKey();

                StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");

                UploadTask uploadTask = filepath.putBytes(thumb_byte);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            String download_url = task.getResult().getDownloadUrl().toString();

                            Map messageMap = new HashMap();
                            messageMap.put("message", download_url);
                            messageMap.put("seen", false);
                            messageMap.put("type", "image");
                            messageMap.put("time", ServerValue.TIMESTAMP);
                            messageMap.put("from", mCurrentUser);

                            Map messageUserMap = new HashMap();
                            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                                    } else {
                                        loadMessage();
                                    }
                                }
                            });
                        } else {
                        }
                    }

                });

            } else {
                final Uri imageUri = Uri.fromFile(new File(mCurrentPhotoPath));

                // ScanFile so it will be appeared on Gallery
                MediaScannerConnection.scanFile(ChatActivity.this,
                        new String[]{imageUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {

                                try {
                                    File imageFile = new File(imageUri.getPath());

                                    Bitmap thumb_bitmap = new Compressor(ChatActivity.this)
                                            .setMaxWidth(800)
                                            .setMaxHeight(600)
                                            .setQuality(100)
                                            .compressToBitmap(imageFile);

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    final byte[] thumb_byte = baos.toByteArray();

                                    final String current_user_ref = "Message/" + mCurrentUser + "/" + mChatUser;
                                    final String chat_user_ref = "Message/" + mChatUser + "/" + mCurrentUser;

                                    DatabaseReference user_message_push = mRootRef.child("Message").child(mCurrentUser).child(mChatUser).push();

                                    final String push_id = user_message_push.getKey();

                                    StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");

                                    UploadTask uploadTask = filepath.putBytes(thumb_byte);

                                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                final String download_url = task.getResult().getDownloadUrl().toString();

                                                final Map messageMap = new HashMap();
                                                messageMap.put("message", download_url);
                                                messageMap.put("seen", false);
                                                messageMap.put("type", "image");
                                                messageMap.put("time", ServerValue.TIMESTAMP);
                                                messageMap.put("from", mCurrentUser);

                                                Map messageUserMap = new HashMap();
                                                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                                                messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

//                        mChatMessage.setText("");

                                                mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        if (databaseError != null) {
                                                            Log.d("CHAT_LOG", databaseError.getMessage().toString());
                                                        } else {
                                                            loadMessage();
                                                        }
                                                    }
                                                });
                                            } else {
                                            }
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        client.close();

    }
}
