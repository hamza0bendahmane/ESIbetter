package com.example.esibetter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.esibetter.notifications.Profile_Activity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.net.URISyntaxException;

public class login extends AppCompatActivity {
    public static final int RC_SIGN_IN = 1;
    public static SignInButton signin_google;
    static FirebaseAuth firebaseAuth;
    static Button signin, signup, forget_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSharedPreferences("seen_intro", MODE_PRIVATE).edit().
                putBoolean("have_seen_intro", true).apply();
        setContentView(R.layout.general_activity_login);

        // change the value of seen_intro to skip it next time ...

        // linking variables ...
        firebaseAuth = FirebaseAuth.getInstance();
        verify_your_email(FirebaseAuth.getInstance().getCurrentUser());

        signin = findViewById(R.id.signin_button);
        signup = findViewById(R.id.signup_button);
        signin_google = findViewById(R.id.signin_google);
        forget_password = findViewById(R.id.forget_password);

        // set actions on click the buttons ..
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSignup();
            }
        });
        signin_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin_google();
            }
        });
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forget_password();
            }
        });


    }

    private void goToProfile() {
        startActivity(new Intent(login.this, Profile_Activity.class));
        finish();
    }

    private void forget_password() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.reset_password));
        dialog.setMessage(getString(R.string.please_enter_your_email));

        LayoutInflater inflater = LayoutInflater.from(this);
        final View login_layout = inflater.inflate(R.layout.general_layout_forget_password, null);

        final TextInputEditText edtEmail = login_layout.findViewById(R.id.email_fields);
        final Button send_email = login_layout.findViewById(R.id.send_email);

        dialog.setView(login_layout);
        //set button

        send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    Toast.makeText(login.this, getString(R.string.please_enter_your_email), Toast.LENGTH_SHORT)
                            .show();
                } else if (!isValidEmail(edtEmail.getText().toString())) {
                    Toast.makeText(login.this, getString(R.string.please_enter_avalid_email), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(edtEmail.getText().toString()).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(login.this, getString(R.string.email_sent), Toast.LENGTH_SHORT)
                                                .show();
                                    } else {
                                        Toast.makeText(login.this, getString(R.string.error_sending_email), Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                }
                            });
                    startActivity(new Intent(getApplicationContext(), login.class));

                }
            }
        });

        dialog.show();
    }


    private void signin() {
        TextInputEditText SignUpMail = findViewById(R.id.email_field1);
        TextInputEditText SignUpPass = findViewById(R.id.password_field1);
        String email = SignUpMail.getText().toString();
        final String pass = SignUpPass.getText().toString();

        if (TextUtils.isEmpty(email)) {
            SignUpMail.requestFocus();
            SignUpMail.setError(getString(R.string.empty_email));
        } else if (!isValidEmail(email)) {
            SignUpPass.requestFocus();
            SignUpPass.setError(getString(R.string.r_u_esist2));
        }
        if (TextUtils.isEmpty(pass)) {
            SignUpPass.requestFocus();
            SignUpPass.setError(getString(R.string.empty_password));
        } else if (!isValidPassword(pass)) {
            SignUpPass.requestFocus();
            SignUpPass.setError(getString(R.string.short_password));
        } else if (isValidEmail(email) && isValidPassword(pass)) {
            firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(login.this, "ERROR :" + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(signin, getString(R.string.login_successful), Snackbar.LENGTH_LONG).show();
                                getSharedPreferences("user_data", 0).edit().putString("wordPass", pass).apply();
                                final FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (!user.isEmailVerified()) {
                                    verify_your_email(user);
                                } else {
                                    goToProfile();
                                }

                            }
                        }
                    });
        }
    }

    private void gotoSignup() {
        Intent intent = new Intent(login.this, signup.class);
        startActivity(intent);
    }

    private void signin_google() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }

    private boolean isValidEmail(String email) {
        return (email.contains("@esi-sba.dz"));
    }

    private boolean isValidPassword(String pass) {
        return (true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account.getEmail().contains("@esi-sba.dz"))
                    firebaseAuthWithGoogle(account);
                else
                    Snackbar.make(forget_password, getString(R.string.u_should_be_esist), Snackbar.LENGTH_LONG);
            } catch (ApiException e) {
                Toast.makeText(this, "failed " + e.getCause(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (!task.getResult().getAdditionalUserInfo().isNewUser())
                                goToProfile();
                            else {
                                boolean succ = false;
                                User_Account newUser = null;

                                try {
                                    newUser = new User_Account(acct.getPhotoUrl(), acct.getDisplayName(),
                                            "is Student", "16", true, "01/01/1999");
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }


                                succ = newUser.SaveData(user, newUser);

                                if (succ) {
                                    Snackbar.make(signin, getString(R.string.done), Snackbar.LENGTH_LONG).show();
                                    goToProfile();
                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(signin, getString(R.string.authenticatin_failed),
                                    Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void verify_your_email(final FirebaseUser user) {

        if (user != null && user.getEmail().contains("@esi-sba.dz")) {
            user.getUid();
            if (!user.isEmailVerified()) {
                final AlertDialog dialog = new AlertDialog.Builder(login.this).create();
                dialog.setTitle(getString(R.string.verify_your_email));
                dialog.setMessage(getString(R.string.email_sent));

                LayoutInflater inflater = LayoutInflater.from(login.this);
                final View login_layout = inflater.inflate(R.layout.general_layout_verify_email, null);

                final Button verify = login_layout.findViewById(R.id.verify);
                dialog.setView(login_layout);
                dialog.show();
                verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseAuth.getCurrentUser().reload();
                        if (!user.isEmailVerified()) {
                            Snackbar.make(verify, getString(R.string.yremail_not_vrf),
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            dialog.dismiss();
                            goToProfile();
                        }
                    }
                });

            } else {
                goToProfile();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        verify_your_email(FirebaseAuth.getInstance().getCurrentUser());
    }

}
