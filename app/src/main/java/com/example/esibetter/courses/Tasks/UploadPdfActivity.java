package com.example.esibetter.courses.Tasks;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.QwertyKeyListener;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.esibetter.R;
import com.example.esibetter.courses.Add_Tutorial;
import com.example.esibetter.notifications.Profile_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class UploadPdfActivity extends AppCompatActivity {

    EditText description;
    boolean haveChoosedTheType , haveChoosedTheModule, Done;
    Spinner yearSpin , ModuleSpin  ;
    int Year ;
    ArrayAdapter<CharSequence> adapterModule ;

    String Module;
    public static final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Button uploadbtn;
    TextView selectPdf;
    EditText pdfName;
    private TextView post_article;
    final Calendar cc = Calendar.getInstance();
    final String month = String.valueOf(cc.get(Calendar.MONTH) + 1);
    final String date = cc.get(Calendar.YEAR) + "/" + month + "/" + cc.get(Calendar.DAY_OF_MONTH);
    private static final int RC_TAKE_PDF = 101;
    private Uri mFileUri = null;
     Uri image_art ;
    private ImageView show_picked_image;

    private Uri mDownloadUrl = null;
    private static final String KEY_FILE_URI = "key_file_uri";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_upload_pdf);

        ModuleSpin = findViewById(R.id.ModuleSpin);
        yearSpin = findViewById(R.id.yearSpin);
        show_picked_image = findViewById(R.id.image_art);
        initViews();

            yearSpin.setEnabled(true);
            yearSpin.setSelection(0);

        findViewById(R.id.Add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick_Image(null);
            }
        });


//What year .....
            final ArrayAdapter<CharSequence> adapterYear = ArrayAdapter.createFromResource(this,
                    R.array.year, android.R.layout.simple_spinner_item);
            adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            yearSpin.setAdapter(adapterYear);
            yearSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Year = i;

                    haveChoosedTheModule = !(Year==0);

                    if ( haveChoosedTheModule )
                        ModuleSpin.setEnabled(true);
                    else {
                        ModuleSpin.setEnabled(false);
                        Done = false;
                    }
                    if (Year==1)
                    {
                        adapterModule =  ArrayAdapter.createFromResource(UploadPdfActivity.this,
                                R.array.first_modules_titles, android.R.layout.simple_spinner_item);
                        adapterModule.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        ModuleSpin.setAdapter(adapterModule);

                        ModuleSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                Module = ModuleSpin.getSelectedItem().toString();
                                Done = true;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    }
                    else   if(Year==2){
                        adapterModule =  ArrayAdapter.createFromResource(UploadPdfActivity.this,
                                R.array.second_modules_titles, android.R.layout.simple_spinner_item);

                        adapterModule.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        ModuleSpin.setAdapter(adapterModule);

                        ModuleSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                Module = ModuleSpin.getSelectedItem().toString();
                                Done = true;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    }





                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });




        post_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = getIntent().getExtras();
                // String postId = bundle.getString("postId");
                //    int position = bundle.getInt("position");

                // handle the event/Article .. edit/add ..

                String title = pdfName.getText().toString().trim();

                boolean fileIsSet =  mFileUri!=null;
                if (TextUtils.isEmpty(title))
                    Toast.makeText(UploadPdfActivity.this, "title should not be empty", Toast.LENGTH_SHORT).show();
                else if (!fileIsSet)
                    Toast.makeText(UploadPdfActivity.this, "Add a file please", Toast.LENGTH_SHORT).show();
                else if (!haveChoosedTheModule) {
                    Toast.makeText(UploadPdfActivity.this, "please choose a year and module please", Toast.LENGTH_SHORT).show();

                }
                else if (image_art==null) {
                    Toast.makeText(UploadPdfActivity.this, "please choose a thumbnail", Toast.LENGTH_SHORT).show();

                }else
                             uploadFromUri(mFileUri,title);






            }
        });
    }


    public void pick_Image(View v) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 12);

    }

    public void initViews(){
        selectPdf =  findViewById(R.id.myTextView);
        pdfName =  findViewById(R.id.Title);
        post_article = findViewById(R.id.post_Tutorial);
    }
        
    public void attach (View v){
    
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, RC_TAKE_PDF);
    }


    public String getFileName(String uri){
        int index;
        while(uri.contains(":")){
            index = uri.indexOf(":");
            uri = uri.substring(index+1);
        }
        while (uri.contains("/")){
            index = uri.indexOf("/");
            uri = uri.substring(index+1);
        }
        return uri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_TAKE_PDF) {
            if (resultCode == RESULT_OK) {
                mFileUri = data.getData();
                if (mFileUri != null) {
                    selectPdf.setText(getFileName(mFileUri.getLastPathSegment()) + " selected");
                    pdfName.setText(getFileName(mFileUri.getLastPathSegment()));
                } 
            } else {
                Toast.makeText(this, "Taking Pdf failed.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 12 && data != null && data.getData() != null) {
            image_art = data.getData(); //IMAGE
            Glide.with(UploadPdfActivity.this).load(image_art).into(show_picked_image);
            Toast.makeText(this, "succ1", Toast.LENGTH_SHORT).show();

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Toast.makeText(this, "succ0", Toast.LENGTH_SHORT).show();
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "FAIL", Toast.LENGTH_SHORT).show();

                Exception error = result.getError();
                error.printStackTrace();

            }
        }
    }

    private void uploadFromUri(Uri fileUri, String pdfname) {

        // Save the File URI
        mFileUri = fileUri;

        // Clear the last download, if any
        mDownloadUrl = null;
        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        startService(new Intent(this, MyUploadService.class)
                .putExtra(MyUploadService.EXTRA_FILE_URI, fileUri)
                .putExtra(MyUploadService.EXTRA_IMAGE_URI, image_art)
                .putExtra("year", String.valueOf(Year))
                .putExtra("title", pdfname)
                .putExtra("module", Module)
                .putExtra("pdf",true)
                .putExtra("date",date)
                .setAction(MyUploadService.ACTION_UPLOAD));

        Toast.makeText(this, "progress_uploading", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, Profile_Activity.class);
        startActivity(intent);
finish();
    }









}
