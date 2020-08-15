package com.example.esibetter.courses.Tasks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.esibetter.R;
import com.example.esibetter.notifications.Profile_Activity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class UploadPdfActivity extends AppCompatActivity {

    EditText description;
    boolean haveChoosedTheType , haveChoosedTheModule, Done;
    Spinner yearSpin , ModuleSpin  ;
    int Year ;
    ArrayAdapter<CharSequence> adapterModule ;

    String Module;
    public final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Button uploadbtn;
    TextView selectPdf;
    EditText pdfName;
    private TextView post_article;
    final Calendar cc = Calendar.getInstance();
    final String month = String.valueOf(cc.get(Calendar.MONTH) + 1);
    final String date = cc.get(Calendar.YEAR) + "/" + month + "/" + cc.get(Calendar.DAY_OF_MONTH);
    private final int RC_TAKE_PDF = 101;
    private Uri mFileUri = null;
    private final String KEY_FILE_URI = "key_file_uri";
    private ImageView show_picked_image;

    private Uri mDownloadUrl = null;
    private final String KEY_DOWNLOAD_URL = "key_download_url";
    Uri image_art;

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
                    Toast.makeText(UploadPdfActivity.this, R.string.title_shou_em, Toast.LENGTH_SHORT).show();
                else if (!fileIsSet)
                    Toast.makeText(UploadPdfActivity.this, R.string.add_file_pl, Toast.LENGTH_SHORT).show();
                else if (!haveChoosedTheModule) {
                    Toast.makeText(UploadPdfActivity.this, R.string.choose_year, Toast.LENGTH_SHORT).show();

                }
                else if (image_art==null) {
                    Toast.makeText(UploadPdfActivity.this, R.string.choose_thu, Toast.LENGTH_SHORT).show();

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
                    selectPdf.setText(getFileName(mFileUri.getLastPathSegment()) + R.string.selected);
                    pdfName.setText(getFileName(mFileUri.getLastPathSegment()));
                } 
            } else {
                Toast.makeText(this, R.string.faile_pdf, Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 12 && data != null && data.getData() != null) {
            image_art = data.getData(); //IMAGE
            Glide.with(UploadPdfActivity.this).load(image_art).into(show_picked_image);

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

        Toast.makeText(this, R.string.progress_uploading, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, Profile_Activity.class);
        startActivity(intent);
finish();
    }









}
