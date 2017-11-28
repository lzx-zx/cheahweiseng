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

import com.example.user.cheahweiseng.Class.LodgeProvider;
import com.example.user.cheahweiseng.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSubmit;
    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtPassword;
    private EditText txtEmail;
    //    private EditText editTextIC;
//    private EditText editTextPHN;
    private TextView txtSignIn;
    //    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference mRef;

    private String firstName, lastName, password, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        btnSubmit = (Button) findViewById(R.id.reg_submit);
        txtFirstName = (EditText) findViewById(R.id.reg_first_name);
        txtLastName = (EditText) findViewById(R.id.reg_last_name);
        txtPassword = (EditText) findViewById(R.id.reg_password);
        txtEmail = (EditText) findViewById(R.id.reg_email);
//        editTextIC = (EditText) findViewById(R.id.IC);
//        editTextPHN = (EditText) findViewById(R.id.PHN);
        txtSignIn = (TextView) findViewById(
                R.id.reg_signin);
        btnSubmit.setOnClickListener(this);
        txtSignIn.setOnClickListener(this);

        mRef = FirebaseDatabase.getInstance().getReference().child("Lodge Provider");
    }

    private void registerUser() {

        firstName = txtFirstName.getText().toString().trim();
        lastName = txtLastName.getText().toString().trim();
        password = txtPassword.getText().toString().trim();
        email = txtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(this, "Your First Name must not ber empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(lastName) || lastName.length() < 6) {
            Toast.makeText(this, "You Last Name Must Than 6 Character", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password) || password.length() < 10) {
            Toast.makeText(this, "You Password Less Than 10 Character", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "You Email is empty", Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.isEmpty(email)) {
            boolean valid = isValidEmail(email);
            if (!valid) {
                Toast.makeText(this, "You Email was Not In the Right Format", Toast.LENGTH_SHORT).show();
            }
        }
            progressDialog.setMessage("Registering Lodge Provider...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String lodgeProviderID = firebaseAuth.getCurrentUser().getUid();
                        BackgroundWorker worker = new BackgroundWorker(RegisterActivity.this);
                        worker.execute(lodgeProviderID, firstName, lastName, email, password);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Could Not Register,Please Try Again!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });


//            System.out.println("LeeZX6 : " + firstName + lastName + password + email);
    }

    public final static boolean isValidEmail(CharSequence Email) {
        if (Email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches();
        }
    }

    public void onClick(View view) {
        if (view == btnSubmit) {
            registerUser();
        }
        if (view == txtSignIn) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    class BackgroundWorker extends AsyncTask<String, Void, String> {

        private Context ctx;
        private String provider_id, firstName, lastName, email, password;

        public BackgroundWorker(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... strings) {

            provider_id = strings[0];
            firstName = strings[1];
            lastName = strings[2];
            email = strings[3];
            password = strings[4];
            String register_url = "https://2f766948.ngrok.io/LodgeServiceSystem/database/lodge_provider/register.php";

            try {
                URL url = new URL(register_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("provider_id", "UTF-8") + "=" + URLEncoder.encode(provider_id, "UTF-8") + "&" +
                        URLEncoder.encode("first_name", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8") + "&" +
                        URLEncoder.encode("last_name", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
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

        }

        @Override
        protected void onPostExecute(final String s) {
            LodgeProvider lodgeProvider = new LodgeProvider(firstName, lastName, password, email, "none", "none");

            if (firebaseAuth.getCurrentUser() != null) {

                mRef.child(provider_id).setValue(lodgeProvider).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_SHORT).show();
                            System.out.println("LeeZX : " + s);
                            progressDialog.dismiss();
                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                        }
                    }
                });

            }


        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}

