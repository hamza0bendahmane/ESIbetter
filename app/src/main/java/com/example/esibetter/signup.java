package com.example.esibetter;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.HashMap;

public class signup extends AppCompatActivity {
    public String URL = "https://emailverification.whoisxmlapi.com/api/v1?apiKey=at_ue6vuTmfKhO2EiG4DgUm0CaNhTU3X&emailAddress=";
    public Button date_view;
    public String date = "01/01/1999";
    public String STR;
    public boolean Exist;
    public Uri image_prof = null;
    public String user_wilaya;
    FirebaseAuth firebaseAuth;
    Button signin, signup;
    RadioButton male_radio;
    Spinner wilaya_spinner;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    ImageView goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_activity_signup);

        // url of profile pic for the user ...
        male_radio = findViewById(R.id.male);
        wilaya_spinner = findViewById(R.id.wilaya_spinner);
        wilaya_spinner.setAdapter(new CustomAdapter(getApplicationContext(), getResources().getStringArray(R.array.wilayas)));


        //wilaya_spinner.setPrompt("Your Province");
        wilaya_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                user_wilaya = String.valueOf(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                user_wilaya = "16";
            }
        });
        // linking variables ...
        date_view = findViewById(R.id.dateview);
        date_view.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                // date picker

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        signup.this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        date = dayOfMonth + "/" + month + "/" + year;
                        date_view.setText(date);
                    }
                });
                dialog.show();


            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        signin = findViewById(R.id.signin_button);
        signup = findViewById(R.id.signup_button);
        goback = findViewById(R.id.go_back);
        // set actions on click the buttons ..
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSignin();
            }
        });

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        male_radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ImageView prof_pic = findViewById(R.id.image_picker);
                if (image_prof == null) {

                    Toast.makeText(signup.this, getString(R.string.pick_photo), Toast.LENGTH_LONG).show();
                    if (male_radio.isChecked())
                        prof_pic.setImageResource(R.mipmap.male);

                    else
                        prof_pic.setImageResource(R.mipmap.female);
                }

            }
        });


    }

    public void signup_bu(View cc) {
        final TextInputEditText name = findViewById(R.id.name_field1);
        TextInputEditText rep_pass = findViewById(R.id.password2_field1);
        TextInputEditText SignUpMail = findViewById(R.id.email_field1);
        TextInputEditText SignUpPass = findViewById(R.id.password_field1);
        final RadioButton male = findViewById(R.id.male);
        final boolean isMale = male.isChecked();
        final String email = SignUpMail.getText().toString();
        final String pass = SignUpPass.getText().toString();
        final String names = name.getText().toString().trim();
        String rep_passs = rep_pass.getText().toString();
        if (TextUtils.isEmpty(names)) {
            name.requestFocus();
            name.setError(getString(R.string.empty_name));
        } else if (!isValidName(names)) {
            name.requestFocus();
            name.setError(getString(R.string.name_shd_cntain_lttrs));
        }
        if (TextUtils.isEmpty(email)) {
            SignUpMail.requestFocus();
            SignUpMail.setError(getString(R.string.empty_email));
        } else if (!isValidEmail(email)) {
            SignUpMail.requestFocus();
            SignUpMail.setError(getString(R.string.r_u_esist2));
        }
        if (TextUtils.isEmpty(pass)) {
            SignUpPass.requestFocus();
            SignUpPass.setError(getString(R.string.empty_password));
        } else if (!isValidPassword(pass)) {
            SignUpPass.requestFocus();
            SignUpPass.setError(getString(R.string.short_password));
        } else if (!rep_passs.equals(pass)) {
            SignUpPass.requestFocus();
            SignUpPass.setError(getString(R.string.pswrd_mst_matches));
        } else if (date_view.getText() == "Birthday") {
            date_view.requestFocus();
            Snackbar.make(date_view, getString(R.string.pls_pick_birth), Snackbar.LENGTH_SHORT);
        } else if (isValidEmail(email) && isValidPassword(pass) && isValidName(names)) {
            STR = URL + email;
            DoesEmailExist user_mail = new DoesEmailExist();
            //user_mail.execute();
            // if (!Exist) {
            //     SignUpMail.requestFocus();
            //  } else
            //  {
            final ProgressDialog progressDialog = new ProgressDialog(signup.this);
            progressDialog.setTitle(R.string.crating_profile);
            progressDialog.setCancelable(false);
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(signup.this,
                    new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(signup.this, R.string.error + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(signup.this, getString(R.string.register_successful), Toast.LENGTH_LONG).show();
                                final ProgressDialog progressDialog0 = new ProgressDialog(signup.this);
                                progressDialog0.setTitle(R.string.saving_data);
                                progressDialog0.setCancelable(false);
                                progressDialog0.show();
                                final FirebaseUser user = firebaseAuth.getCurrentUser();
                                //send email verification ..
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull final Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(signup.this, getString(R.string.cant_snd_email_vrf), Toast.LENGTH_SHORT).show();
                                        } else {


                                            if (image_prof == null) {
                                                if (isMale)
                                                    image_prof = Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + R.mipmap.male);
                                                else
                                                    image_prof = Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + R.mipmap.female);
                                            }


                                            byte[] data = null;

                                            // image compression
                                            try {
                                                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), image_prof); // getting image from gallery
                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                bmp.compress(Bitmap.CompressFormat.PNG, 10, baos);
                                                data = baos.toByteArray();
                                            } catch (Exception e) {

                                            }

                                            final Long ts_long = System.currentTimeMillis() / 1000;
                                            final String ts = ts_long.toString();
                                            final StorageReference childRef = FirebaseStorage.getInstance().getReference().child("Images/" + user.getUid() + "prof_pic.png");
                                            final UploadTask uploadTask = childRef.putBytes(data);

                                            // Progress dialog box implemented
                                            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                                    int current_progress = (int) progress;
                                                    progressDialog0.setProgress(current_progress);
                                                }
                                            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {

                                                }
                                            });


                                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                            @Override
                                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                                if (!task.isSuccessful()) {
                                                                    throw task.getException();
                                                                }

                                                                // Continue with the task to get the download URL
                                                                return childRef.getDownloadUrl();
                                                            }
                                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Uri> task) {
                                                                if (task.isSuccessful()) {
                                                                    Uri downloadUri = task.getResult();
                                                                    String mUri = downloadUri.toString();
                                                                    HashMap<String, Object> data = new HashMap<>();
                                                                    data.put("name", names);
                                                                    data.put("status", "is Student");
                                                                    data.put("isMale", isMale);
                                                                    data.put("wilaya", user_wilaya);
                                                                    data.put("birthday", date);
                                                                    FirebaseFirestore.getInstance().collection("users").document(user.getUid()).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                gotoSignin();
                                                                                Toast.makeText(getApplicationContext(), R.string.succes, Toast.LENGTH_LONG).show();

                                                                            } else {
                                                                                String errMsg = task.getException().getMessage();
                                                                                Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                                                                            }
                                                                            progressDialog0.dismiss();
                                                                        }
                                                                    });
                                                                } else {
                                                                    //Log.w("LOGIN", "signInWithCredential:failure", task.getException());
                                                                    progressDialog0.dismiss();
                                                                    String errMsg = task.getException().getMessage();
                                                                    Toast.makeText(getApplicationContext(), "خطأ رفع الملف " + errMsg, Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });

                                                    } else {
                                                        String errMsg = task.getException().getMessage();
                                                        Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "فشل الرفع " + e, Toast.LENGTH_LONG).show();
                                                }
                                            });

                                        }

                                    }
                                });


                            }

                        }
                    });


        }
        //  }
    }

    private void gotoSignin() {
        onBackPressed();
    }


    private boolean isValidEmail(String email) {
        return (email.contains("@esi-sba.dz"));
    }

    private boolean isValidPassword(String pass) {
        return (pass.length() >= 8);
    }

    public boolean isValidName(String s) {
        String[] words = s.split(" ");
        boolean twoOrMore = words.length >= 2;
        boolean val = true;
        for (String ss : words) {
            for (int i = 0; i < ss.length(); i++) {
                char ch = ss.charAt(i);
                if (Character.isLetter(ch)) {
                    continue;
                }
                val = false;
                break;
            }
            if (!val)
                break;
        }
        return val && twoOrMore;
    }


    public void pick_image(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && data != null && data.getData() != null) {
            ImageView prof_pic = findViewById(R.id.image_picker);
            prof_pic.setImageURI(data.getData());
            image_prof = data.getData();
        }
    }

    public class DoesEmailExist extends AsyncTask<Void, Void, JSONObject> {


        @Override
        protected JSONObject doInBackground(Void... params) {
            URLConnection urlConn;
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(STR);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                    Log.d("hamzahb", line);
                }
                Exist = new JSONObject(stringBuffer.toString()).getBoolean("smtpCheck");

                return new JSONObject(stringBuffer.toString());
            } catch (Exception ex) {
                Log.e("App", "DoesEmailExist", ex);
                return null;
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if (response != null) {
                try {
                    Exist = response.getBoolean("smtpCheck");
                } catch (JSONException ex) {
                    Exist = true;
                }
            }
        }

    }

    public class CustomAdapter extends BaseAdapter {
        Context context;
        String[] countryNames;
        LayoutInflater inflter;

        public CustomAdapter(Context applicationContext, String[] countryNames) {
            this.context = applicationContext;
            this.countryNames = countryNames;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return countryNames.length;
        }

        @Override
        public String getItem(int i) {
            return countryNames[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(android.R.layout.simple_spinner_dropdown_item, null);
            TextView names = view.findViewById(android.R.id.text1);
            names.setTypeface(Typeface.createFromAsset(context.getAssets(), "font/CeraPro-Regular.ttf"));
            names.setText(countryNames[i]);
            return view;
        }
    }
}
