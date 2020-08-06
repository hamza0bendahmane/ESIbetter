package com.example.esibetter.courses;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.esibetter.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class pdf extends AppCompatActivity {

    String product="";
    Uri link ;
    PDFView pdfView;
    String linkString,ref;
    String posterUid , postId , image ;
    public static final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        posterUid =getIntent().getStringExtra("uid");
        product =getIntent().getStringExtra("title");
        ref =getIntent().getStringExtra("ref");
        linkString = getIntent().getExtras().getString("link");
         pdfView=findViewById(R.id.pdfv);
         new pdf.RetrievePDFStream().execute(linkString);


        image =getIntent().getStringExtra("image");


        if (isConnected()) {
            Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(pdf.this);
            builder.setTitle("NoInternet Connection Alert")
                    .setMessage("GO to Setting ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(pdf.this,"Go Back TO HomePage!",Toast.LENGTH_SHORT).show();
                        }
                    });
            //Creating dialog box
            AlertDialog dialog  = builder.create();
            dialog.show();
        }






    }


    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }


    class RetrievePDFStream extends AsyncTask<String, Void, InputStream> {

        ProgressDialog progressDialog;
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(pdf.this);
            progressDialog.setTitle("getting the book content...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

        }
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;

            try {

                URL urlx = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) urlx.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;

        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
            progressDialog.dismiss();
        }
    }
  public void  handleMoreActions (View view){
      ImageView button = findViewById(R.id.more_actions);
      PopupMenu popup = new PopupMenu(getApplicationContext(), button);

      if (posterUid.equals(uid)) {  // the current user is the one who posted this Article...
          popup.getMenuInflater().inflate(R.menu.user_course_menu, popup.getMenu());
      } else { // the current user doesn't write this Article ...
          popup.getMenuInflater().inflate(R.menu.anonym_course_menu, popup.getMenu());
      }
      popup.show();//showing popup menu
      popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
          public boolean onMenuItemClick(MenuItem item) {
              switch (item.getItemId()) {
                  case R.id.delete_file:
                      HandleMenu("delete");
                      return true;
                  case R.id.open_file_with:
                      HandleMenu("open");
                      return true;
                  case R.id.report_file:
                      HandleMenu("report");
                      return true;
                  default:
                      break;
              }
              return false;
          }
      });
    }

    private void HandleMenu(String share) {

        switch (share) {


            case "open":
                Intent intent = new Intent();
                intent.setType(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(linkString));
                startActivity(intent);
                break;
            case "delete":
                AlertDialog.Builder builder = new AlertDialog.Builder(pdf.this);
                builder.setTitle("Deleting Summary!!")
                        .setMessage("Are you sure you want to delete this Summary ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletethis();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                            }
                        });
                //Creating dialog box
                AlertDialog dialog  = builder.create();
                dialog.show();
                break;
            case "report":
                    Courses.reportFile(pdf.this,posterUid,link.toString());
                break;
            default:
                break;


        }

    }

    public void go_back_to(View view){
        onBackPressed();
    }
    private void deletethis(){
        StorageReference fileref = FirebaseStorage.getInstance().getReferenceFromUrl(linkString);
        fileref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference refere = FirebaseDatabase.getInstance().getReferenceFromUrl(ref);
                refere.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(pdf.this, R.string.succes, Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
            }
        });


    }
}

