package com.example.esibetter.courses;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.esibetter.R;
import com.example.esibetter.courses.Tasks.MyUploadService;
import com.example.esibetter.notifications.Profile_Activity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


public class Add_Tutorial extends AppCompatActivity {
    public final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public final CollectionReference reference = FirebaseFirestore.getInstance()
            .collection("posts");
    public Uri image_art, file_art;
    final Calendar cc = Calendar.getInstance();
    final String month = String.valueOf(cc.get(Calendar.MONTH) + 1);
    final String date = cc.get(Calendar.YEAR) + "/" + month + "/" + cc.get(Calendar.DAY_OF_MONTH);
    String titleArt;
    TextView post_article;
    String dislikes = "0";
    Spinner yearSpin, ModuleSpin;
    String Module;
    int Type, Year;
    StorageReference images_url;
    ImageView show_picked_image;
    String BodyArt;
    boolean haveChoosedTheType, haveChoosedTheModule;
    String likes = "0";
    EditText TitleBody;
    String typeof = "add";
    boolean Done = false;
    CircleImageView photo;
    ArrayAdapter<CharSequence> adapterModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courses_add_tutorial);
        // init vars ......
         typeof = getIntent().getExtras().getString("type");

        post_article = findViewById(R.id.post_Tutorial);
        photo = findViewById(R.id.picture);
        show_picked_image = findViewById(R.id.imageView6);
        TitleBody = findViewById(R.id.Title);
        images_url = FirebaseStorage.getInstance().getReference("Images/" + uid);
        // init vars ......
        ModuleSpin = findViewById(R.id.ModuleSpin);
        yearSpin = findViewById(R.id.yearSpin);
        // pick thumbnail ...
        findViewById(R.id.Add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick_Image(null);
            }
        });

        findViewById(R.id.imageView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick_Video(null);

            }
        });
        yearSpin.setEnabled(true);
        yearSpin.setSelection(0);



//What year .....
        final ArrayAdapter<CharSequence> adapterYear = ArrayAdapter.createFromResource(this,
                R.array.year, android.R.layout.simple_spinner_item);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpin.setAdapter(adapterYear);
        yearSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Year = i;

                 haveChoosedTheType = !(Type==0);
                 haveChoosedTheModule = !(Year==0);

                if ( haveChoosedTheModule )
                ModuleSpin.setEnabled(true);
                else {
                    ModuleSpin.setEnabled(false);
                    Done = false;
                }
                if (Year==1)
                {
                    adapterModule =  ArrayAdapter.createFromResource(Add_Tutorial.this,
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
                    adapterModule =  ArrayAdapter.createFromResource(Add_Tutorial.this,
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



        images_url.child("/prof_pic.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).load(uri).into(photo);
            }
        });


        // on click post the Article .....

        post_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = getIntent().getExtras();
                // String postId = bundle.getString("postId");
                //    int position = bundle.getInt("position");

                // handle the event/Article .. edit/add ..

                String title = TitleBody.getText().toString().trim();

                boolean imageIsSET = image_art != null;
                boolean fileIsSet =  file_art!=null;
                if (TextUtils.isEmpty(title))
                    Toast.makeText(Add_Tutorial.this, R.string.title_shou_em, Toast.LENGTH_SHORT).show();
                else if (!imageIsSET)
                    Toast.makeText(Add_Tutorial.this, R.string.add_im, Toast.LENGTH_SHORT).show();
                else if (!fileIsSet)
                    Toast.makeText(Add_Tutorial.this, R.string.add_file_pl, Toast.LENGTH_SHORT).show();
                else {

                    titleArt = TitleBody.getText().toString().trim();
                    final String keyDocument = uid + cc.getTimeInMillis();
                    StorageReference reference = FirebaseStorage.getInstance().
                            getReference("Images/" + uid + "/" + keyDocument);
                    final StorageReference photoArt = reference.child("thumbnail.png");
                    View vv = LayoutInflater.from(Add_Tutorial.this).inflate(R.layout.general_layout_image,
                            null, false);
                    final AlertDialog dialog = new AlertDialog.Builder(Add_Tutorial.this).setTitle(R.string.uploding_file)
                            .setView(vv).create();
                    dialog.show();

                    if (typeof.equals("add")) {

                        uploadFromUri(file_art,title);

                    }


                }



            }
        });


    }



    private void uploadFromUri(Uri fileUri, String pdfname) {

        // Save the File URI

        // Clear the last download, if any
        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        startService(new Intent(this, MyUploadService.class)
                .putExtra(MyUploadService.EXTRA_FILE_URI, fileUri)
                .putExtra(MyUploadService.EXTRA_IMAGE_URI, image_art)
                .putExtra("year", String.valueOf(Year))
                .putExtra("title", pdfname)
                .putExtra("module", Module)
                .putExtra("pdf",false)
                .putExtra("date",date)
                .setAction(MyUploadService.ACTION_UPLOAD));

        Toast.makeText(this, R.string.progress_uploading, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, Profile_Activity.class);
        startActivity(intent);
        finish();
    }


    public void pick_Video(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 21);
    }
    public void pick_Image(View v) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 12);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 21 && resultCode == RESULT_OK && data != null && data.getData()!=null){
                //VIDEO
            file_art = data.getData();
            ((TextView) findViewById(R.id.myTextView)).setText(R.string.file_choosen);
        }
        if (requestCode == 12 && data != null && data.getData() != null) {
            image_art = data.getData(); //IMAGE
            show_picked_image.setImageURI(image_art);

        }

    }



    public void cancel(View view) {
        startActivity(new Intent(getApplicationContext(), Tutorials.class));
        finish();
    }





    }










