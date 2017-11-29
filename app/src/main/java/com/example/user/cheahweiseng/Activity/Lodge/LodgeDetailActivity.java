package com.example.user.cheahweiseng.Activity.Lodge;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cheahweiseng.Activity.MainActivity;
import com.example.user.cheahweiseng.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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

public class LodgeDetailActivity extends AppCompatActivity {
    MapsActivity mapsActivity = new MapsActivity();
    private ImageView lodgeImage;
    private TextView lodgeTitle;
    private TextView lodgeStatus;
    private TextView lodgePrice;
    private TextView lodgeAddedDate;
    private TextView lodgeLocation;
    private TextView lodgeDescription;
    private TextView lodgeProvider;
    private FirebaseAuth firebaseAuth;
    private Button gg_map_direction;
    private DatabaseReference databaseReference;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lodge_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String lodgeID = getIntent().getStringExtra("id");
        String providerID = getIntent().getStringExtra("provider_id");

        BackgroundWorker worker = new BackgroundWorker(LodgeDetailActivity.this);
        worker.execute(lodgeID, providerID);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Lodge");
        final Intent context = getIntent();
        final String s_longitude = context.getStringExtra("Longitude");
        final String s_laitude = context.getStringExtra("Laitude");

        lodgeImage = (ImageView) findViewById(R.id.lodge_detail_image);
        mProgress = (ProgressBar) findViewById(R.id.lodge_detail_image_progress);
        lodgeTitle = (TextView) findViewById(R.id.lodge_detail_title);
        lodgeDescription = (TextView) findViewById(R.id.lodge_detail_description);
        lodgeProvider = (TextView) findViewById(R.id.lodge_detail_lodge_provider);
        lodgeAddedDate = (TextView) findViewById(R.id.lodge_detail_added_date);
        lodgeStatus = (TextView) findViewById(R.id.lodge_detail_status);
        lodgePrice = (TextView) findViewById(R.id.lodge_detail_price);
        gg_map_direction = (Button) findViewById(R.id.gg_map_direction);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    class BackgroundWorker extends AsyncTask<String, Void, String> {

        private Context ctx;
        private String lodge_id, provider_id;

        public BackgroundWorker(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... strings) {

            lodge_id = strings[0];
            provider_id = strings[1];

            String lodge_detail_url = "http://192.168.43.151/LodgeServiceSystem/database/lodge/display_lodge_detail.php";
            try {
                URL url = new URL(lodge_detail_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("lodge_id", "UTF-8") + "=" + URLEncoder.encode(lodge_id, "UTF-8") + "&" +
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
                    final String location = object.getString("lodgeLocation");
                    String price = object.getString("lodgePrice");
                    String image = object.getString("lodgeImage");
                    String addedDate = object.getString("lodgeAddedDate");
                    String provider_id = object.getString("lodgeProviderID");

                    LodgeProviderLoader loader = new LodgeProviderLoader(ctx);
                    loader.execute(provider_id);

                    Picasso.with(LodgeDetailActivity.this)
                            .load(image)// web image url
                            .error(R.drawable.ic_error_black_24dp)
                            .into(lodgeImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mProgress.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {

                                }
                            });

                    lodgeTitle.setText(title);
                    lodgePrice.setText(price);
                    lodgeDescription.setText(description);
                    lodgeAddedDate.setText(addedDate);

                    gg_map_direction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String address = location;
                            Intent m_post_view = new Intent(LodgeDetailActivity.this, MapsActivity.class);
                            mapsActivity.getDestAddress(address);
                            startActivity(m_post_view);
                        }
                    });
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

    class LodgeProviderLoader extends AsyncTask<String, Void, String> {

        private Context ctx;
        private String provider_id;

        public LodgeProviderLoader(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... strings) {

            provider_id = strings[0];
            String profile_url = "http://192.168.43.151/LodgeServiceSystem/database/lodge_provider/retrieve_lodge_provider_details.php";

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
        }

        @Override
        protected void onPostExecute(final String s) {
            Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
            try {
                JSONArray jsonArray = new JSONArray(s);
                JSONObject object;
                for (int i = 0; i < jsonArray.length(); i++) {
                    object = jsonArray.getJSONObject(i);
                    String firstName = object.getString("lodgeProviderFirstName");
                    String lastName = object.getString("lodgeProviderLastName");

                    lodgeProvider.setText(firstName + " " + lastName);
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
}

