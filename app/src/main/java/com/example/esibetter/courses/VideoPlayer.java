package com.example.esibetter.courses;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.esibetter.R;
import com.github.rtoshiro.view.video.FullscreenVideoLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoPlayer extends AppCompatActivity {
    TextView titleofcourse , posterName , date_post;
    FullscreenVideoLayout videoLayout;
    CircleImageView imageofpost ;
    String posterUid;
    String linkString , ref;
    public final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courses_video_player);
        InitViews();
    }

    private void InitViews() {

        titleofcourse = findViewById(R.id.titleofcourse);
        videoLayout =  findViewById(R.id.videoview);
        ref =getIntent().getStringExtra("ref");
        videoLayout.setActivity(this);
        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("title");
        posterUid = bundle.getString("uid");
        String date = bundle.getString("date");
         linkString = getIntent().getExtras().getString("link");
                try {
                    videoLayout.setVideoURI(Uri.parse(linkString));

                } catch (IOException e) {
                    e.printStackTrace();
                }


        // .....
        titleofcourse.setText(title);



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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayer.this);
                builder.setTitle(R.string.deleting_video)
                        .setMessage(R.string.are_you_sure_delte_video)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletethis();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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
                Courses.reportFile(VideoPlayer.this,posterUid,linkString);
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
                        Toast.makeText(VideoPlayer.this, R.string.succes, Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
            }
        });


    }
}
