package com.example.user.cheahweiseng.Activity.Lodge;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cheahweiseng.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
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

public class LodgeHistoryDetailActivity extends AppCompatActivity {

    private ImageView lodgeHistoryImage;
    private TextView lodgeHistoryTitle;
    private TextView lodgeHistoryStatus;
    private TextView lodgeHistoryPrice;
    private TextView lodgeHistoryAddedDate;
    private TextView lodgeHistoryLocation;
    private TextView lodgeHistoryDescription;
    private TextView lodgeHistoryProvider;
    private FirebaseAuth firebaseAuth;
    //    private Button gg_map_direction;
    private DatabaseReference databaseReference;
    private ProgressBar mProgress;
    private String lodge_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lodge_detail_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lodge_id = getIntent().getStringExtra("id");
        String providerID = getIntent().getStringExtra("provider_id");

        BackgroundWorker worker = new BackgroundWorker(LodgeHistoryDetailActivity.this);
        worker.execute(lodge_id, providerID);

        lodgeHistoryImage = (ImageView) findViewById(R.id.lodge_history_detail_image);
        mProgress = (ProgressBar) findViewById(R.id.lodge_history_detail_image_progress);
        lodgeHistoryTitle = (TextView) findViewById(R.id.lodge_history_detail_title);
        lodgeHistoryDescription = (TextView) findViewById(R.id.lodge_history_detail_description);
        lodgeHistoryProvider = (TextView) findViewById(R.id.lodge_history_detail_lodge_provider);
        lodgeHistoryAddedDate = (TextView) findViewById(R.id.lodge_history_detail_added_date);
        lodgeHistoryStatus = (TextView) findViewById(R.id.lodge_history_detail_status);
        lodgeHistoryPrice = (TextView) findViewById(R.id.lodge_history_detail_price);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.history_lodge_view, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_lodge:
                Intent intent = new Intent(this, UpdateLodgeActivity.class);
                intent.putExtra("lodge_id", lodge_id);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

            String lodge_detail_url = "http://192.168.1.5/LodgeServiceSystem/database/lodge/display_lodge_detail.php";
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

                    Picasso.with(LodgeHistoryDetailActivity.this)
                            .load(image)// web image url
                            .error(R.drawable.ic_error_black_24dp)
                            .into(lodgeHistoryImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mProgress.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {

                                }
                            });

                    lodgeHistoryTitle.setText(title);
                    lodgeHistoryPrice.setText(price);
                    lodgeHistoryDescription.setText(description);
                    lodgeHistoryAddedDate.setText(addedDate);

//                    gg_map_direction.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            String address = location;
//                            Intent m_post_view = new Intent(LodgeHistoryDetailActivity.this, MapsActivity.class);
////                            mapsActivity.getDestAddress(address);
//                            startActivity(m_post_view);
//                        }
//                    });
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

                    lodgeHistoryProvider.setText(firstName + " " + lastName);
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



