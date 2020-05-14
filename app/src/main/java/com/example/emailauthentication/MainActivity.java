package com.example.emailauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emailauthentication.utils.ApplicationUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context context;

    private EditText loginEmailET, loginPassET;
    private Button btnLogin, buttonSignUp;
    private EditText edittextVerifyEmail, edittextVerifyPassword;
    private Button buttonAccountCreate, btnSendEmail;
    private LinearLayout linearLayoutCreateAccount, linearLayoutVerification, linearLayoutVerified;
    private LinearLayout relativeLayoutLogin;
    private RelativeLayout relativeLayoutCreateAccount;
    private TextView verEmailTV;
    private TextView textViewVerifyEmail, textViewAfterVerifyEmail;
    private Button btnEmailVerified;
    private ProgressDialog progressDialog;
    private boolean doubleBackToExitPressedOnce = false;

    String savedEmail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initUI();
        setListeners();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");

            }
        };

        progressDialog = new ProgressDialog(this);
        if (SharedData.getInstance().getUserEmail() != null) {
            savedEmail = SharedData.getInstance().getUserEmail();
            Timber.e("savedEmail -> %s", savedEmail);
        }

        loginEmailET.setEnabled(false);
        loginEmailET.setFocusable(false);
        loginEmailET.setFocusableInTouchMode(false);
        loginPassET.setEnabled(false);
        loginPassET.setFocusable(false);
        loginPassET.setFocusableInTouchMode(false);

    }

    private void initUI() {
        edittextVerifyEmail = (EditText) findViewById(R.id.verEmailET);
        edittextVerifyPassword = (EditText) findViewById(R.id.verPassET);
        verEmailTV = (TextView) findViewById(R.id.verEmailTV);
        linearLayoutCreateAccount = (LinearLayout) findViewById(R.id.linearLayoutCreateAccount);
        linearLayoutVerification = (LinearLayout) findViewById(R.id.linearLayoutVerification);
        linearLayoutVerified = (LinearLayout) findViewById(R.id.linearLayoutVerified);
        relativeLayoutLogin = (LinearLayout) findViewById(R.id.relativeLayoutLogin);
        relativeLayoutCreateAccount = (RelativeLayout) findViewById(R.id.relativeLayoutCreateAccount);
        textViewVerifyEmail = (TextView) findViewById(R.id.verEmailTextView);
        textViewAfterVerifyEmail = (TextView) findViewById(R.id.afterEmailTV);
        btnLogin = (Button) findViewById(R.id.loginBTN);
        buttonSignUp = (Button) findViewById(R.id.createAccBTN);
        btnEmailVerified = (Button) findViewById(R.id.emailVerifiedBTN);
        loginEmailET = (EditText) findViewById(R.id.loginEmailET);
        loginPassET = (EditText) findViewById(R.id.loginPassET);
        btnSendEmail = (Button) findViewById(R.id.sendEmailBTN);
        buttonAccountCreate = (Button) findViewById(R.id.accCreateBTN);
    }

    private void setListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                if(SharedData.getInstance().getUserEmail()!=null){
//                final String email = SharedData.getInstance().getUserEmail();
//                loginEmailET.setText(savedEmail);
                final String email = loginEmailET.getText().toString();
//                }
//                final String password = SharedData.getInstance().getUserPass();
                final String password = loginPassET.getText().toString();
//                loginPassET.setText(password);

                progressDialog.setMessage("Please Wait....");
                progressDialog.show();

                if (email.isEmpty()) {
                    toastMessage("Enter E-Mail");
                } else if (password.isEmpty()) {
                    toastMessage("Enter Password");
                } else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Intent i = new Intent(MainActivity.this, NewActivity.class);
                                        i.putExtra("email", SharedData.getInstance().getUserEmail());
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        toastMessage("Login Successfull");
                                    } else {
                                        toastMessage("Enter valid E-mail and Password");
                                    }
                                }
                            });

                }
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, CreateAccActivity.class));
//                linearLayoutCreateAccount.setVisibility(View.VISIBLE);
                relativeLayoutCreateAccount.setVisibility(View.VISIBLE);
                buttonSignUp.setEnabled(false);

            }
        });

        buttonAccountCreate.setOnClickListener(v -> {
            final String email = edittextVerifyEmail.getText().toString();
            final String pass = edittextVerifyPassword.getText().toString();
            SharedData.getInstance().setUserEmail(email);
            SharedData.getInstance().setUserPass(pass);
            loginEmailET.setText(email);
            loginPassET.setText(pass);

            if (email.isEmpty()) {
                toastMessage("Enter E-Mail");
            } else if (pass.isEmpty()) {
                toastMessage("Enter Password");
            } else {
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (task.isSuccessful()) {

//                                        Intent i = new Intent(MainActivity.this,VerificationActivity.class);
//                                        i.putExtra("email",email);
//                                        i.putExtra("pass",pass);
//                                        startActivity(i);

//                                            loginEmailET.setText(email);
//                                            loginPassET.setText(pass);
//                                        emailCreate = loginEmailET.getText().toString();
//                                        passCreate = loginPassET.getText().toString();
                                    verEmailTV.setText(email);
                                    relativeLayoutCreateAccount.setVisibility(View.INVISIBLE);
                                    buttonAccountCreate.setVisibility(View.INVISIBLE);
                                    linearLayoutVerification.setVisibility(View.VISIBLE);
                                    buttonAccountCreate.setText("Send Verification E-Mail");
                                } else {
                                    toastMessage("Error!!! Check Internet Connection or E-Mail already exists");
                                }
                            }
                        });
            }
        });

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.sendEmailVerification();
                    mAuth.signOut();
                    linearLayoutVerification.setVisibility(View.GONE);
                    linearLayoutVerified.setVisibility(View.VISIBLE);
//                    relativeLayoutLogin.setVisibility(View.VISIBLE);
//                    Intent i = new Intent(VerificationActivity.this,EmailVerification.class);
//                    i.putExtra("email",email);
//                    i.putExtra("pass",pass);
//                    startActivity(i);
//                    toastMessage("Verification E-Mail has been sent to " + SharedData.getInstance().getUserEmail());

//                    TastyToastUtils.showTastySuccessToast(context, "Verification E-Mail has been sent to \n" + SharedData.getInstance().getUserEmail()
//                    +"\n Once Verified Press E-MAIL VERIFIED button");

                    ApplicationUtils.showMessageDialog("Verification E-Mail has been sent to \n" + SharedData.getInstance().getUserEmail()
                            + "\nOnce Verified Press E-MAIL VERIFIED button", context);

                }
            }
        });

        btnEmailVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signInWithEmailAndPassword(SharedData.getInstance().getUserEmail(), SharedData.getInstance().getUserPass())
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (task.isSuccessful()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {
                                        if (user.isEmailVerified()) {
                                            toastMessage("Your E-Mail " + SharedData.getInstance().getUserEmail() + " has been successfully Verified");
                                            textViewAfterVerifyEmail.setVisibility(View.VISIBLE);
                                            textViewAfterVerifyEmail.setText("Your E-Mail \n" + SharedData.getInstance().getUserEmail() + " \n has been successfully Verified. \n Go to Welcome page by pressing the LOGIN Button");
                                            btnEmailVerified.setVisibility(View.GONE);
                                            relativeLayoutLogin.setVisibility(View.VISIBLE);
                                            btnEmailVerified.setEnabled(false);
//                                            relativeLayoutLogin.setVisibility(View.VISIBLE);
                                            btnLogin.setVisibility(View.VISIBLE);
                                        } else {
                                            toastMessage("Error!!! Check your E-Mail");
                                        }
                                    } else {
                                        toastMessage("Error!!! Check your Internet Connection");
                                    }
                                }
                            }
                        });
            }
        });
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        mAuth.signOut();
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        ApplicationUtils.showExitDialog(this);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
