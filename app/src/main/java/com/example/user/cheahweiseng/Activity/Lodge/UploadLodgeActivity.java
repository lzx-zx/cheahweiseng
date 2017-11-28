package com.example.user.cheahweiseng.Activity.Lodge;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.app.Notification;

import com.example.user.cheahweiseng.Activity.MainActivity;
import com.example.user.cheahweiseng.Class.Lodge;
import com.example.user.cheahweiseng.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

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
import java.util.Calendar;
import java.util.Date;

import id.zelory.compressor.Compressor;

public class UploadLodgeActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView7;
    private ImageView imageView8;
    private Button upload_pic;
    private EditText editTexttitle;
    private EditText editTextdesc;
    private EditText editTextloc;
    private EditText editTextprice;
    private EditText editTextid;

    private Button save;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private String userid;
    private String radiobutton_condition;
    private String title, description, location, price, l_id;
    private RadioGroup radioGroup;
    private RadioButton radRent, radSale;
    private ImageView ImageView7, ImageView8;
    private StorageReference storageDatebase;
    private Uri selectImageURI;
    private static final int GALLERY_INTENT = 11;
    private File lodgeFile;
    private byte[] lodge_byte;
    private String url;
    private Calendar calendar = Calendar.getInstance();
    private DatabaseReference store_databaseReference;

    private String lodgeID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_lodge);

        getSupportActionBar().setTitle("Upload Lodge");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        userid = firebaseAuth.getCurrentUser().getUid();
        store_databaseReference = FirebaseDatabase.getInstance().getReference();

        imageView7 = (ImageView) findViewById(R.id.upload_lodge_pic);
//        upload_pic = (Button) findViewById(R.id.upload_pic);
//        save = (Button) findViewById(R.id.btn_save);
//        upload_pic.setOnClickListener(this);
        imageView7.setOnClickListener(this);
        radioGroup = (RadioGroup) findViewById(R.id.upload_lodge_status);

        radRent = (RadioButton) findViewById(R.id.upload_lodge_status_rent);
        radSale = (RadioButton) findViewById(R.id.upload_lodge_status_sale);
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
        editTexttitle = (EditText) findViewById(R.id.upload_lodge_title);
        editTextdesc = (EditText) findViewById(R.id.upload_lodge_description);
        editTextloc = (EditText) findViewById(R.id.upload_lodge_location);
        editTextprice = (EditText) findViewById(R.id.upload_lodge_price);
        editTextid = (EditText) findViewById(R.id.upload_lodge_id);
        radioGroup = (RadioGroup) findViewById(R.id.upload_lodge_status);
//        save.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageDatebase = FirebaseStorage.getInstance().getReference();
    }

    private void upload_post() {
        final NotificationManager mNM = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        final String description = editTextdesc.getText().toString().trim();
        final String location = editTextloc.getText().toString().trim();
        final String price = editTextprice.getText().toString().trim();
        final String status = radiobutton_condition;
        final String title = editTexttitle.getText().toString().trim();
        final String date = new Date().toString();
        final String image = url;

        if (TextUtils.isEmpty(description)) {

        } else if (TextUtils.isEmpty(location)) {

        } else if (TextUtils.isEmpty(price)) {

        } else if (TextUtils.isEmpty(status)) {

        } else if (TextUtils.isEmpty(title)) {

        } else if (TextUtils.isEmpty(image)) {

        } else {
            Lodge lodge = new Lodge(lodgeID, title, status, description, location, price, image, new Date(date), userid);
            store_databaseReference.child("Lodge").child(firebaseAuth.getUid()).child(lodgeID).setValue(lodge).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        BackgroundWorker worker = new BackgroundWorker(UploadLodgeActivity.this);
                        worker.execute(lodgeID, title, status, description, location, price, image, date, userid);
                    }
                }
            });
            Notification mNotify = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.test)
                    .setContentTitle("You Have Upload" + "" + "!" + editTexttitle.getText())
                    .setContentText(" " + "At" + " " + editTexttitle.getText() + editTextloc.getText())
                    .build();
            // mId allows you to update the notification later on.
            // mNotificationManager.notify(001, mBuilder.build());
            Toast.makeText(UploadLodgeActivity.this, "Sucessfully Upload Lodge Post", Toast.LENGTH_SHORT).show();
            mNM.notify(0, mNotify);
            startActivity(new Intent(UploadLodgeActivity.this, MainActivity.class));
        }
    }

    private void callGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Choose Image:"), GALLERY_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            selectImageURI = data.getData();
//              lodgeFile = new File(selectImageURI.getPath());

            Picasso.with(UploadLodgeActivity.this)
                    .load(lodgeFile)// web image url
                    .into(imageView7);

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
            lodgeFile = new File(getRealPathFromURI_BelowAPI11(UploadLodgeActivity.this, selectImageURI));
        } else if (sdkVersion <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            lodgeFile = new File(getRealPathFromURI_API11to18(UploadLodgeActivity.this, selectImageURI));
        } else if (sdkVersion >= Build.VERSION_CODES.KITKAT) {
            lodgeFile = new File(getRealPathFromURI_API19(UploadLodgeActivity.this, selectImageURI));
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

            lodgeID = store_databaseReference.child("Lodge").child(firebaseAuth.getUid()).push().getKey();

            //final StorageReference filePath = storageDatebase.child("Photo").child(firebaseAuth.getCurrentUser().getUid()).child(firebaseAuth.getCurrentUser().getUid() + ".jpg");
            StorageReference filePath = storageDatebase.child("Photo").child(firebaseAuth.getCurrentUser().getUid()).child(lodgeID + ".jpg");//.child("Images/"+ ".jpg");


            filePath.putBytes(lodge_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(UploadLodgeActivity.this, "Upload Sucessfully", Toast.LENGTH_SHORT).show();
                        url = task.getResult().getDownloadUrl().toString();

                        upload_post();
                    }
                }
            });
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    progressDialog.dismiss();
//                    Toast.makeText(UploadLodgeActivity.this, "Upload Sucessfully", Toast.LENGTH_SHORT).show();
//                    url = taskSnapshot.getDownloadUrl().toString();
//
//                    upload_post();
//                }
//            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
//        if (v == upload_pic) {
//            uploadPicture();
//        } else
        if (v == imageView7) {
            callGallery();
        }
//        if (v == save) {
//            upload_post();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upload_lodge, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                checkPermission();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkPermission() {
        int sdkVersion = Build.VERSION.SDK_INT;

        if (sdkVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(UploadLodgeActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        123);
            } else if (ContextCompat.checkSelfPermission(UploadLodgeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        123);
            } else if (ContextCompat.checkSelfPermission(UploadLodgeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(UploadLodgeActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
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
        private String lodge_id, title, status, description, location, price, image, date, provider_id;

        public BackgroundWorker(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... strings) {

            lodge_id = strings[0];
            title = strings[1];
            status = strings[2];
            description = strings[3];
            location = strings[4];
            price = strings[5];
            image = strings[6];
            date = strings[7];
            provider_id = strings[8];

            String create_lodge_url = "http://192.168.1.5/LodgeServiceSystem/database/lodge/create_new_lodge.php";

            try {
                URL url = new URL(create_lodge_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("lodge_id", "UTF-8") + "=" + URLEncoder.encode(lodge_id, "UTF-8") + "&" +
                        URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8") + "&" +
                        URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8") + "&" +
                        URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(description, "UTF-8") + "&" +
                        URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8") + "&" +
                        URLEncoder.encode("price", "UTF-8") + "=" + URLEncoder.encode(price, "UTF-8") + "&" +
                        URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(image, "UTF-8") + "&" +
                        URLEncoder.encode("added_date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8") + "&" +
                        URLEncoder.encode("provider_id", "UTF-8") + "=" + URLEncoder.encode(provider_id, "UTF-8");
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
            progressDialog.setMessage("Uploading Lodge...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(final String s) {
            Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
            System.out.println(s + "\n");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
