package com.example.user.cheahweiseng.Activity.Lodge;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cheahweiseng.Class.Lodge;
import com.example.user.cheahweiseng.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class UpdateLodgeActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView lodgeImage;
    private TextView lodgeTitle;
    private TextView lodgeDescription;
    private TextView lodgeLocation;
    private TextView lodgePrice;
    private RadioGroup radioGroup;
    private String radiobutton_condition;
    private RadioButton radRent, radSale;

    private FirebaseAuth firebaseAuth;

    private String lodgeID;

    private byte[] lodge_byte;
    private File lodgeFile;
    private Uri selectImageURI;
    private StorageReference storageDatebase;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private String url;
    private static final int GALLERY_INTENT = 11;

    private Map lodgeprofile = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_lodge);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        radioGroup = (RadioGroup) findViewById(R.id.update_lodge_status);
        radRent = (RadioButton) findViewById(R.id.update_lodge_status_rent);
        radSale = (RadioButton) findViewById(R.id.update_lodge_status_sale);
        lodgeImage = (ImageView) findViewById(R.id.update_lodge_pic);
        lodgeTitle = (TextView) findViewById(R.id.update_lodge_title);
        lodgeDescription = (TextView) findViewById(R.id.update_lodge_description);
        lodgeLocation = (TextView) findViewById(R.id.update_lodge_location);
        lodgePrice = (TextView) findViewById(R.id.update_lodge_price);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (radRent.isChecked()) {
                    System.out.println("Rent button");
                    radiobutton_condition = "Rent";
                } else if (radSale.isChecked()) {
                    System.out.println("Sale button");
                    radiobutton_condition = "Sale";
                } else {
                    System.out.println("Error radio button!");
                    radiobutton_condition = "Unknown";
                }
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        lodgeID = getIntent().getStringExtra("lodge_id");

        BackgroundWorker worker = new BackgroundWorker(UpdateLodgeActivity.this);
        worker.execute(lodgeID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.history_lodge_view, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update:
                checkPermission();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    private void uploadPicture() {

//        lodgeFile = new File(getRealPathFromURI(selectImageURI));
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion <= Build.VERSION_CODES.HONEYCOMB) {
            lodgeFile = new File(getRealPathFromURI_BelowAPI11(UpdateLodgeActivity.this, selectImageURI));
        } else if (sdkVersion <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            lodgeFile = new File(getRealPathFromURI_API11to18(UpdateLodgeActivity.this, selectImageURI));
        } else if (sdkVersion >= Build.VERSION_CODES.KITKAT) {
            lodgeFile = new File(getRealPathFromURI_API19(UpdateLodgeActivity.this, selectImageURI));
        }
        System.out.println("Thomas: " + lodgeFile.toString());
        try {
            progressDialog.setMessage("Uploading Image.....");
            progressDialog.show();

            final Bitmap lodgeBitmap = new Compressor(this)
                    .setMaxWidth(500)
                    .setMaxHeight(200)
                    .setQuality(100)
                    .compressToBitmap(lodgeFile.getAbsoluteFile());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            lodgeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            lodge_byte = baos.toByteArray();

            StorageReference filePath = storageDatebase.child("Photo").child(firebaseAuth.getCurrentUser().getUid()).child(lodgeID + ".jpg");//.child("Images/"+ ".jpg");

            filePath.putBytes(lodge_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(UpdateLodgeActivity.this, "Upload Sucessfully", Toast.LENGTH_SHORT).show();
                        url = task.getResult().getDownloadUrl().toString();

                        updateLodge();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLodge() {
        final NotificationManager mNM = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        final String description = lodgeDescription.getText().toString().trim();
        final String location = lodgeLocation.getText().toString().trim();
        final String price = lodgePrice.getText().toString().trim();
        final String status = radiobutton_condition;
        final String title = lodgeTitle.getText().toString().trim();
        final String image = url;

        if (TextUtils.isEmpty(description)) {

        } else if (TextUtils.isEmpty(location)) {

        } else if (TextUtils.isEmpty(price)) {

        } else if (TextUtils.isEmpty(status)) {

        } else if (TextUtils.isEmpty(title)) {

        } else if (TextUtils.isEmpty(image)) {

        } else {

            Lodge lodge = new Lodge(lodgeID, title, status, description, location, price, image, new Date(), firebaseAuth.getCurrentUser().getUid());
            lodgeprofile.put("Lodge/" + firebaseAuth.getCurrentUser().getUid() + "/" + lodgeID + "/",lodge);
            databaseReference.updateChildren(lodgeprofile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        UpdateLodge update = new UpdateLodge(UpdateLodgeActivity.this);
                        update.execute(title, status, description, location, price, image, lodgeID);
                    }
                }
            });
            Notification mNotify = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.test)
                    .setContentTitle("You Have Updated " + "" + "!" + lodgeTitle.getText())
                    .setContentText(" " + "At" + " " + lodgeTitle.getText() + lodgeLocation.getText())
                    .build();
            // mId allows you to update the notification later on.
            // mNotificationManager.notify(001, mBuilder.build());
            Toast.makeText(UpdateLodgeActivity.this, "Sucessfully Updated Lodge Post", Toast.LENGTH_SHORT).show();
            mNM.notify(0, mNotify);
            startActivity(new Intent(UpdateLodgeActivity.this, LodgeHistoryDetailActivity.class));
        }


    }

    private void callGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Choose Image:"), GALLERY_INTENT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            selectImageURI = data.getData();

            Picasso.with(UpdateLodgeActivity.this)
                    .load(lodgeFile)// web image url
                    .into(lodgeImage);
        }
    }

    public void onClick(View v) {
        if (v == lodgeImage) {
            callGallery();
        }
    }

    private void checkPermission() {
        int sdkVersion = Build.VERSION.SDK_INT;

        if (sdkVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(UpdateLodgeActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        123);
            } else if (ContextCompat.checkSelfPermission(UpdateLodgeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        123);
            } else if (ContextCompat.checkSelfPermission(UpdateLodgeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(UpdateLodgeActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        123);
            } else {
                uploadPicture();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                uploadPicture();
            } else {
                checkPermission();
            }
        }
    }

    class BackgroundWorker extends AsyncTask<String, Void, String> {

        private Context ctx;
        private String lodge_id;

        public BackgroundWorker(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... strings) {

            lodge_id = strings[0];

            String lodge_detail_url = "http://192.168.1.5/LodgeServiceSystem/database/lodge/display_lodge_detail_by_id.php";
            try {
                URL url = new URL(lodge_detail_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("lodge_id", "UTF-8") + "=" + URLEncoder.encode(lodge_id, "UTF-8");
                bufferedWriter.write(post_data);

                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String result = "";
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                connection.disconnect();

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(final String s) {
            System.out.println(s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                JSONObject object;
                for (int i = 0; i < jsonArray.length(); i++) {
                    object = jsonArray.getJSONObject(i);
                    String id = object.getString("lodgeID");
                    String title = object.getString("lodgeTitle");
                    String status = object.getString("lodgeStatus");
                    String description = object.getString("lodgeDescription");
                    String location = object.getString("lodgeLocation");
                    String price = object.getString("lodgePrice");
                    String image = object.getString("lodgeImage");
                    String addedDate = object.getString("lodgeAddedDate");
                    String provider_id = object.getString("lodgeProviderID");

                    Picasso.with(ctx)
                            .load(image)// web image url
                            .error(R.drawable.ic_error_black_24dp)
                            .into(lodgeImage);

                    lodgeTitle.setText(title);

                    if (status.equals("Sale")) {
                        radSale.setChecked(true);
                        radRent.setChecked(false);
                    } else if (status.equals("Rent")) {
                        radSale.setChecked(false);
                        radRent.setChecked(true);
                    }

                    lodgeDescription.setText(description);
                    lodgeLocation.setText(location);
                    lodgePrice.setText(price);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    class UpdateLodge extends AsyncTask<String, Void, String> {

        private Context ctx;
        private String title, status, description, location, price, image, lodge_id;

        public UpdateLodge(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... strings) {

            title = strings[0];
            status = strings[1];
            description = strings[2];
            location = strings[3];
            price = strings[4];
            image = strings[5];
            lodge_id = strings[6];
            String profile_url = "http://192.168.1.5/LodgeServiceSystem/database/lodge/update_lodge_details.php";

            try {
                URL url = new URL(profile_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8") + "&" +
                        URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8") + "&" +
                        URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(description, "UTF-8") + "&" +
                        URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8") + "&" +
                        URLEncoder.encode("price", "UTF-8") + "=" + URLEncoder.encode(price, "UTF-8") + "&" +
                        URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(image, "UTF-8") + "&" +
                        URLEncoder.encode("lodge_id", "UTF-8") + "=" + URLEncoder.encode(lodge_id, "UTF-8");
                bufferedWriter.write(post_data);

                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String result = "";
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                connection.disconnect();

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(final String s) {
            Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}

