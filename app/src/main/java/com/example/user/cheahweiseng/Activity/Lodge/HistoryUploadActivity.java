package com.example.user.cheahweiseng.Activity.Lodge;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.user.cheahweiseng.Adapter.HistoryAdapter;
import com.example.user.cheahweiseng.Class.Lodge;
import com.example.user.cheahweiseng.R;
import com.google.firebase.auth.FirebaseAuth;

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
import java.util.ArrayList;


public class HistoryUploadActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;

    private String mCurrentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_upload);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycle_view2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth = FirebaseAuth.getInstance();

        mCurrentUserID = firebaseAuth.getCurrentUser().getUid();

        BackgroundWorker worker = new BackgroundWorker(HistoryUploadActivity.this, recyclerView);
        worker.execute(mCurrentUserID);

        recyclerView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HistoryUploadActivity.this, LodgeHistoryDetailActivity.class);
                startActivity(intent);

            }

        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.uploadLodge);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent uploadLodgeIntent = new Intent(HistoryUploadActivity.this, UploadLodgeActivity.class);
                startActivity(uploadLodgeIntent);
            }
        });
    }

    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class BackgroundWorker extends AsyncTask<String, Void, String> {

        private Context ctx;
        RecyclerView lodgeView;
        HistoryAdapter lodgeDetail;
        ArrayList<Lodge> lodgeList = new ArrayList<>();
        private String provider_id;

        public BackgroundWorker(Context ctx, RecyclerView rv) {
            this.ctx = ctx;
            this.lodgeView = rv;
        }

        @Override
        protected String doInBackground(String... strings) {

            provider_id = strings[0];
            String history_url = "https://2f766948.ngrok.io/LodgeServiceSystem/database/lodge/display_history_lodge.php";

            try {
                URL url = new URL(history_url);
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
            System.out.print(s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                JSONObject object;
                for (int i = 0; i < jsonArray.length(); i++) {
                    object = jsonArray.getJSONObject(i);
                    String id = object.getString("lodgeID");
                    String title = object.getString("lodgeTitle");
                    String description = object.getString("lodgeDescription");
                    String price = object.getString("lodgePrice");
                    String image = object.getString("lodgeImage");
                    String provider_id = object.getString("lodgeProviderID");
                    lodgeList.add(new Lodge(id, title, description, price, image, provider_id));
                }
                lodgeDetail = new HistoryAdapter(ctx, lodgeList, lodgeView);
                lodgeView.setAdapter(lodgeDetail);
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

