package com.example.esibetter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

public class signup extends AppCompatActivity {
    public static String URL = "https://emailverification.whoisxmlapi.com/api/v1?apiKey=at_ue6vuTmfKhO2EiG4DgUm0CaNhTU3X&emailAddress=";
    public static Button date_view;
    public static String date = "01/01/1999";
    public String STR;
    public boolean Exist;
    public Uri image_prof = null;
    public String user_wilaya;
    FirebaseAuth firebaseAuth;
    Button signin, signup;
    Spinner wilaya_spinner;
    StorageReference reference;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    ImageView goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_activity_signup);

        // url of profile pic for the user ...
        final RadioButton male_radio = findViewById(R.id.male);
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

                dialog.show();

                mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        date = day + "/" + month + "/" + year;
                        date_view.setText(date);

                    }
                };


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
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Signup();
            }
        });
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSignin();
            }
        });

        male_radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ImageView prof_pic = findViewById(R.id.image_picker);
                if (image_prof == null) {

                    Toast.makeText(signup.this, "Please pick a photo for your profile", Toast.LENGTH_LONG).show();
                    if (male_radio.isChecked())
                        prof_pic.setImageResource(R.mipmap.male);

                    else
                        prof_pic.setImageResource(R.mipmap.female);
                }

            }
        });


    }

    private void Signup() {
        final TextInputEditText name = findViewById(R.id.name_field1);
        TextInputEditText rep_pass = findViewById(R.id.password2_field1);
        TextInputEditText SignUpMail = findViewById(R.id.email_field1);
        TextInputEditText SignUpPass = findViewById(R.id.password_field1);
        final RadioButton male = findViewById(R.id.male);
        final boolean isMale = male.isChecked();
        final String email = SignUpMail.getText().toString();
        final String pass = SignUpPass.getText().toString();
        final String names = name.getText().toString();
        String rep_passs = rep_pass.getText().toString();
        if (TextUtils.isEmpty(names)) {
            name.requestFocus();
            name.setError("Name should not be empty");
        } else if (!isValidName(names)) {
            name.requestFocus();
            name.setError("Name should contain only letters");
        }
        if (TextUtils.isEmpty(email)) {
            SignUpMail.requestFocus();
            SignUpMail.setError("email should not be empty");
        } else if (!isValidEmail(email)) {
            SignUpMail.requestFocus();
            SignUpMail.setError("Are you sure , you are a \n student a ESI SBA !");
        }
        if (TextUtils.isEmpty(pass)) {
            SignUpPass.requestFocus();
            SignUpPass.setError("password should not be empty");
        } else if (!isValidPassword(pass)) {
            SignUpPass.requestFocus();
            SignUpPass.setError("Password must be than 8 digit or more");
        } else if (!rep_passs.equals(pass)) {
            SignUpPass.requestFocus();
            SignUpPass.setError("Password must matches ");
        } else if (date_view.getText() == "Birthday") {
            date_view.requestFocus();
            Snackbar.make(date_view, "please pick your Birthday", Snackbar.LENGTH_SHORT);
        } else if (isValidEmail(email) && isValidPassword(pass) && isValidName(names)) {
            STR = URL + email;
            DoesEmailExist user_mail = new DoesEmailExist();
            user_mail.execute();
            if (!Exist) {
                SignUpMail.requestFocus();
            } else {
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(signup.this,
                        new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(signup.this, "ERROR :" + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(signup.this, "register successful", Toast.LENGTH_LONG).show();
                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                            //send email verification ..
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull final Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(signup.this, "error can't send email verification,\n" +
                                                "try again ..", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            try {
                                User_Account account = new User_Account(image_prof, names, "Your Status", user_wilaya, isMale, date);
                                account.SaveData(user, account);

                            } catch (MalformedURLException | URISyntaxException e) {
                                e.printStackTrace();
                            }
                            skip();
                        }

                    }
                });


            }
        }
    }

    private void skip() {
        startActivity(new Intent(signup.this, login.class));
        finish();
    }

    private void gotoSignin() {
        startActivity(new Intent(signup.this, login.class));
        finish();
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
        boolean val = false;
        for (String ss : words) {
            for (int i = 0; i < ss.length(); i++) {
                char ch = ss.charAt(i);
                if (Character.isLetter(ch)) {
                    continue;
                }
                val = false;
                break;
            }
            val = true;
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
            URLConnection urlConn = null;
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
