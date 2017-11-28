package com.example.user.cheahweiseng.Activity.LodgeProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cheahweiseng.R;
import com.google.firebase.auth.FirebaseAuth;

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

public class EditICActivity extends AppCompatActivity {

    private TextView mIC;
    private Button btnUpdate;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private String mCurrentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ic);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();

        mIC = (TextView) findViewById(R.id.edit_ic_number_ic);
        btnUpdate = (Button) findViewById(R.id.edit_ic_btn_update);
        progressDialog = new ProgressDialog(EditICActivity.this);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ic = mIC.getText().toString();

                BackgroundWorker worker = new BackgroundWorker(EditICActivity.this);
                worker.execute(ic, mCurrentUserID);
            }
        });
    }

    class BackgroundWorker extends AsyncTask<String, Void, String> {

        private Context ctx;
        private String ic, provider_id;

        public BackgroundWorker(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... strings) {
            ic = strings[0];
            provider_id = strings[1];

            String update_ic_url = "https://2f766948.ngrok.io/LodgeServiceSystem/database/lodge_provider/update_lodge_provider_ic.php";

            try {
                URL url = new URL(update_ic_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("ic", "UTF-8") + "=" + URLEncoder.encode(ic, "UTF-8") + "&" +
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
            progressDialog.setMessage("Updating IC...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(final String s) {
            Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
            Intent profileIntent = new Intent(EditICActivity.this, EditProfileActivity.class);
            startActivity(profileIntent);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
