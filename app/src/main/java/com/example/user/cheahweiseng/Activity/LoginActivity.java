package com.example.user.cheahweiseng.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cheahweiseng.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btnSignIn;
    private EditText editTextemail;
    private EditText editTextpassword;
    private TextView textViewregister;
    private TextView textviewResetpass;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(LoginActivity.this);
        editTextemail = (EditText) findViewById(R.id.reg_email);
        editTextpassword = (EditText) findViewById((R.id.reg_password));
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        textViewregister = (TextView) findViewById(R.id.register);
        textviewResetpass = (TextView) findViewById(R.id.resetpass);

        btnSignIn.setOnClickListener(this);
        textviewResetpass.setOnClickListener(this);
        textViewregister.setOnClickListener(this);
    }

    private void userlogin() {
        final String email = editTextemail.getText().toString().trim();
        final String password = editTextpassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password) || password.length() < 10) {
            Toast.makeText(this, "Fill In Password more than 10 character", Toast.LENGTH_SHORT).show();
        } else {
            BackgroundWorker worker = new BackgroundWorker(LoginActivity.this);
            worker.execute(email, password);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnSignIn) {
            userlogin();
        }
        if (view == textViewregister) {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        }
        if (view == textviewResetpass) {
            Intent resetPasswordIntent = new Intent(LoginActivity.this, Reset_Password.class);
            startActivity(resetPasswordIntent);
        }
    }

    class BackgroundWorker extends AsyncTask<String, Void, String>{

        private Context ctx;
        private String email, password;

        public BackgroundWorker(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... strings) {

            email = strings[0];
            password = strings[1];
            String login_url = "https://2f766948.ngrok.io/LodgeServiceSystem/database/lodge_provider/login.php";
            try {

                URL url = new URL(login_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
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
            progressDialog.setMessage("Logging in....");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(final String s) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                    }
                }
            });
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
