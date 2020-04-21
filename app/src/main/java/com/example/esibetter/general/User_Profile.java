package com.example.esibetter.general;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.esibetter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;

import javax.annotation.Nonnull;

import de.hdodenhof.circleimageview.CircleImageView;

public class User_Profile extends AppCompatActivity {

    public static TextView date_view;
    public static String date;
    public static boolean wilaya_isChanged, date_isChanged, gender_isChanged;
    public static int Edit_pointer = 0;
    public static Uri Image_picked = null;
    static StorageReference reference;
    static FirebaseFirestore database;
    static DatePickerDialog.OnDateSetListener mDateSetListener;
    static FirebaseUser user;
    static Spinner wilaya_spinner, gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_profile);

        wilaya_spinner = findViewById(R.id.wilaya);
        gender = findViewById(R.id.gender);
        date_view = findViewById(R.id.date);
        wilaya_spinner.setAdapter(new CustomAdapter(getApplicationContext(), getResources().getStringArray(R.array.wilayas)));
        wilaya_spinner.setEnabled(false);

        gender.setAdapter(new CustomAdapter(getApplicationContext(), getResources().getStringArray(R.array.gender)));
        gender.setEnabled(false);
        // initializing vars ...
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseFirestore.getInstance();
        UpdateUI();


        // open date picker
        date_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        User_Profile.this,
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
                        User_Profile.date_isChanged = true;
                    }
                });
                dialog.show();


            }
        });


    }


    private void UpdateUI() {

        database.collection("users").document(user.getUid()).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@Nonnull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().getData() != null) {
                            final CircleImageView image_user = findViewById(R.id.imageUser);
                            TextView user_name = findViewById(R.id.name_user);
                            TextView email = findViewById(R.id.email);
                            Spinner gender = findViewById(R.id.gender);
                            Spinner wilaya = findViewById(R.id.wilaya);
                            TextView status = findViewById(R.id.status_user);
                            TextView date = findViewById(R.id.date);
                            //............ collecting then saving user data
                            if ((boolean) task.getResult().get("isMale"))
                                gender.setSelection(0);
                            else
                                gender.setSelection(1);
                            date.setText(task.getResult().get("birthday").toString());
                            wilaya.setSelection(Integer.valueOf((String) task.getResult().get("wilaya")) - 1);
                            email.setText(user.getEmail());
                            user_name.setText(task.getResult().get("name").toString());
                            status.setText(task.getResult().get("status").toString());
                            reference = FirebaseStorage.getInstance().getReference("Images/" + user.getUid());
                            StorageReference photo = reference.child("/prof_pic.png");
                            photo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(getApplicationContext()).asBitmap()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL).load(uri).into(image_user);
                                }
                            });




                        }
                    }

                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(User_Profile.this, SettingsActivity.class));
        finish();
    }


    public void enable_edit(View view) {
        final LinearLayout linear = findViewById(R.id.change_data_layout);
        linear.setVisibility(View.VISIBLE);
        Edit_pointer = 1;
        EditText name, status;
        TextView date;
        name = findViewById(R.id.name_user);
        status = findViewById(R.id.status_user);
        // see if some fields has changed .............................................................
        wilaya_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                wilaya_isChanged = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                gender_isChanged = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // .............................................................................................
        date = findViewById(R.id.date);
        View[] views = {name/*, email*/, status, date, wilaya_spinner, gender};
        for (View v : views) {
            v.setEnabled(true);

        }
        Snackbar.make(findViewById(R.id.gender), "You can now edit your profile", Snackbar.LENGTH_SHORT).show();
    }


    public void commit(View v) {
        final LinearLayout linear = findViewById(R.id.change_data_layout);
        linear.setVisibility(View.VISIBLE);
        saveUserChanges();
        EditText name, status;
        TextView datee;
        Spinner wilaya, gender;
        name = findViewById(R.id.name_user);
        status = findViewById(R.id.status_user);
        datee = findViewById(R.id.date);
        wilaya = findViewById(R.id.wilaya);
        gender = findViewById(R.id.gender);
        View[] views = {name/*, email*/, status, datee, wilaya, gender};
        for (View vv : views) {
            vv.setEnabled(false);

        }
        Image_picked = null;
        linear.setVisibility(View.GONE);
        Edit_pointer = 0;


        // cancel  changes entered by user
    }

    public void cancel(View v) {
        EditText name, status;
        TextView datee;
        Spinner wilaya, gender;
        name = findViewById(R.id.name_user);
        status = findViewById(R.id.status_user);
        datee = findViewById(R.id.date);
        wilaya = findViewById(R.id.wilaya);
        gender = findViewById(R.id.gender);
        View[] views = {name/*, email*/, status, datee, wilaya, gender};
        for (View vv : views) {
            vv.setEnabled(false);

        }
        Image_picked = null;
        UpdateUI();
        final LinearLayout linear = findViewById(R.id.change_data_layout);
        linear.setVisibility(View.GONE);
        Edit_pointer = 0;


    }


    private void saveUserChanges() {

        //.............1 CHANGE EMAIL
        //   updateEmail();
        //.............2 CHANGE PHOTO

        final HashMap<String, Object> map = new HashMap<>();

        if (Image_picked != null) {
            reference = FirebaseStorage.getInstance().getReference("Images/" + user.getUid());
            StorageReference photo = reference.child("/prof_pic.png");
            photo.putFile(Image_picked).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@Nonnull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Snackbar.make(date_view, "Image Uploaded ", Snackbar.LENGTH_LONG).show();
                    } else Toast.makeText(User_Profile.this,
                            "can't upload image, try again", Toast.LENGTH_SHORT).show();

                }
            });


        }

        HashMap<String, Object> inputs = new HashMap<>();

        //.............3 CHANGE WILAYA
        if (wilaya_isChanged) {
            inputs.put("wilaya", String.valueOf(wilaya_spinner.getSelectedItemPosition() + 1));
        }
        //.............4 CHANGE GENDER
        if (gender_isChanged) {

            boolean isMale = gender.getSelectedItemPosition() == 0;
            inputs.put("isMale", isMale);
        }
        //............5 CHANGE DATE
        if (date_isChanged && !date.equals("")) {
            inputs.put("birthday", date);
        }
        //.............6 CHANGE STATUS

        EditText status = findViewById(R.id.status_user);
        String statuss = status.getText().toString();

        if (TextUtils.isEmpty(statuss)) {
            status.requestFocus();
            status.setError("Status should not be empty");
        } else if (statuss.length() > 20) {
            status.requestFocus();
            status.setError("Status should be < 20 chars ");
        } else {

            statuss = status.getText().toString();
            // Status is valid save it ....
            inputs.put("status", statuss);

        }

        //.............7 CHANGE NAME

        EditText name = findViewById(R.id.name_user);
        String names = name.getText().toString().trim();
        if (TextUtils.isEmpty(names)) {
            name.requestFocus();
            name.setError("Name should not be empty");
        } else if (!isValidName(names)) {
            name.requestFocus();
            name.setError("Name is not valid");

        } else {
            // Name is valid save it ....
            map.put("name", names);
            map.put("token", FirebaseInstanceId.getInstance().getToken());
            names = name.getText().toString();
            inputs.put("name", names);


        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users/" + user.getUid());
        if (map.containsKey("image") || map.containsKey("name"))
            ref.updateChildren(map);
        database.collection("users").document(user.getUid()).update(inputs).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@Nonnull Task<Void> task) {
                if (task.isSuccessful())
                    Snackbar.make(date_view, "fields updated successfully ", Snackbar.LENGTH_LONG).show();
                else
                    Toast.makeText(User_Profile.this, "can't update your profile , try again", Toast.LENGTH_SHORT).show();

            }
        });

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
        if (Edit_pointer == 1) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 0);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && data != null && data.getData() != null) {
            Image_picked = data.getData();
            CircleImageView img = findViewById(R.id.imageUser);
            img.setImageURI(Image_picked);

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
            names.setTypeface(Typeface.createFromAsset(context.getAssets(), "font/MavenPro-Regular.ttf"));
            names.setText(countryNames[i]);
            return view;
        }
    }

}