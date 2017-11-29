package com.example.user.cheahweiseng.Activity.LodgeProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.user.cheahweiseng.Class.LodgeProvider;
import com.example.user.cheahweiseng.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class EditProfileActivity extends AppCompatActivity {
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
    private ImageView editName, editIC, editEmail, editPhone;
    private ProgressDialog mProgress;
    private LodgeProvider lodgeProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Lodge Provider").child(firebaseAuth.getCurrentUser().getUid());
        mTextView = (TextView) findViewById(R.id.profile_view_name);
//        btnUpdate = (Button) findViewById(R.id.btnUpdate) ;
        ID = (TextView) findViewById(R.id.profile_view_name);
        IC = (TextView) findViewById(R.id.profile_view_ic);
        Email = (TextView) findViewById(R.id.profile_view_email);
        PHN = (TextView) findViewById(R.id.profile_view_phone);

        editName = (ImageView) findViewById(R.id.profile_view_edit_name);
        editIC = (ImageView) findViewById(R.id.profile_view_edit_ic);
        editEmail = (ImageView) findViewById(R.id.profile_view_edit_email);
        editPhone = (ImageView) findViewById(R.id.profile_view_edit_phone);

        BackgroundWorker worker = new BackgroundWorker(EditProfileActivity.this);
        worker.execute(firebaseAuth.getCurrentUser().getUid());

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editNameIntent = new Intent(EditProfileActivity.this, EditNameActivity.class);
                startActivity(editNameIntent);
            }
        });

        editIC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editICIntent = new Intent(EditProfileActivity.this, EditICActivity.class);
                startActivity(editICIntent);
            }
        });

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editEmailIntent = new Intent(EditProfileActivity.this, EditEmailActivity.class);
                startActivity(editEmailIntent);
            }
        });

        editPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editPhoneIntent = new Intent(EditProfileActivity.this, EditPhoneActivity.class);
                editPhoneIntent.putExtra("phn",PHN.getText().toString());
                startActivity(editPhoneIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.profile_view, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class BackgroundWorker extends AsyncTask<String, Void, String> {

        private Context ctx;
        private String provider_id;

        public BackgroundWorker(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... strings) {

            provider_id = strings[0];
            String profile_url = "http://192.168.1.5/LodgeServiceSystem/database/lodge_provider/retrieve_lodge_provider_details.php";

            try {
                URL url = new URL(profile_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("provider_id", "UTF-8") + "=" + URLEncoder.encode(provider_id, "UTF-8");
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
            mProgress = new ProgressDialog(EditProfileActivity.this);
            mProgress.setMessage("Loading...");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(final String s) {
//            Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
            try {
                JSONArray jsonArray = new JSONArray(s);
                JSONObject object;
                for (int i = 0; i < jsonArray.length(); i++) {
                    object = jsonArray.getJSONObject(i);
                    String firstName = object.getString("lodgeProviderFirstName");
                    String lastName = object.getString("lodgeProviderLastName");
                    String contact = object.getString("lodgeProviderContactNumber");
                    String ic = object.getString("lodgeProviderIC");
                    String email = object.getString("lodgeProviderEmail");
                    String password = object.getString("lodgeProviderPassword");
                    String image = object.getString("lodgeProviderProfileImage");

//                    lodgeProvider = new LodgeProvider(firstName, lastName, password, email, ic, contact);

                    ID.setText(firstName + " " + lastName);

                    if (ic.equals("null")) {
                        IC.setText("none");
                    } else {
                        IC.setText(ic);
                    }
                    Email.setText(email);

                    if (contact.equals("null")) {
                        PHN.setText("none");
                    } else {
                        PHN.setText(contact);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mProgress.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}


