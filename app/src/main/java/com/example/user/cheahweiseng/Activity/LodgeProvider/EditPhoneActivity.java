package com.example.user.cheahweiseng.Activity.LodgeProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cheahweiseng.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

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
import java.util.concurrent.TimeUnit;

public class EditPhoneActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String mCurrentUserID;

    private TextView mPhone;
    private TextView mPhoneVerificationCode;
    private Button btnSend;
    private Button btnResend;
    private Button btnVerify;
    private Button btnUpdate;
    private ProgressDialog progressDialog;

    private String PHN;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthCredential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PHN = getIntent().getStringExtra("phn");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();

        mPhone = (TextView) findViewById(R.id.edit_phone_number);
        mPhoneVerificationCode = (TextView) findViewById(R.id.edit_phone_verification_code);
        btnUpdate = (Button) findViewById(R.id.edit_phone_btn_update);
        btnVerify = (Button) findViewById(R.id.edit_phone_btn_verify);
        btnSend = (Button) findViewById(R.id.edit_phone_btn_send);
        btnResend = (Button) findViewById(R.id.edit_phone_btn_resend);
        progressDialog = new ProgressDialog(EditPhoneActivity.this);

        btnResend.setVisibility(View.INVISIBLE);
        btnUpdate.setVisibility(View.INVISIBLE);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPhoneNumberWithCode(mVerificationId, mPhoneVerificationCode.getText().toString());
                btnVerify.setVisibility(View.INVISIBLE);
                btnUpdate.setVisibility(View.VISIBLE);
            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PHN.equals("none")) {
                    mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                BackgroundWorker worker = new BackgroundWorker(EditPhoneActivity.this);
                                worker.execute(mPhone.getText().toString(), mCurrentUserID);
                            }
                        }
                    });
                }else{
                    mAuth.getCurrentUser().updatePhoneNumber(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                BackgroundWorker worker = new BackgroundWorker(EditPhoneActivity.this);
                                worker.execute(mPhone.getText().toString(), mCurrentUserID);;
                            }
                        }
                    });
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mPhone.getText().toString();
                if (!TextUtils.isEmpty(phoneNumber)) {
                    startPhoneNumberVerification(phoneNumber);
                    btnSend.setVisibility(View.INVISIBLE);
                    btnResend.setVisibility(View.VISIBLE);
                }
            }
        });

        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mPhone.getText().toString();
                if (!TextUtils.isEmpty(phoneNumber)) {
                    resendVerificationCode(phoneNumber, mResendToken);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(EditPhoneActivity.this, "Invalid phone number.", Toast.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerificationId = s;
                mResendToken = forceResendingToken;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }
        };
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        credential = PhoneAuthProvider.getCredential(verificationId, code);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    class BackgroundWorker extends AsyncTask<String, Void, String> {

        private Context ctx;
        private String phone, provider_id;

        public BackgroundWorker(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... strings) {
            phone = strings[0];
            provider_id = strings[1];

            String update_email_url = "https://2f766948.ngrok.io/LodgeServiceSystem/database/lodge_provider/update_lodge_provider_phone.php";

            try {
                URL url = new URL(update_email_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&" +
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
            progressDialog.setMessage("Updating Phone Number...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(final String s) {
            Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
            Intent profileIntent = new Intent(EditPhoneActivity.this, EditProfileActivity.class);
            startActivity(profileIntent);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
