package com.example.esibetter.courses.Tasks;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.esibetter.R;
import com.example.esibetter.courses.Add_Tutorial;
import com.example.esibetter.notifications.Profile_Activity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MyUploadService extends MyBaseTaskService {



    public static String description ;
    public static Uri thumbnail;
    public static final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static String module;
    private static double pdfsize;
    private static String uploaddate;
    public static String pdfkey;
    public static String pdfname;
    public static String year , date;

    public StorageReference mStorageRef;
    public DatabaseReference dbref;
    private static final String TAG = "MyUploadService";

    /** Intent Actions **/
    public static final String ACTION_UPLOAD = "action_upload";
    public static final String UPLOAD_COMPLETED = "upload_completed";
    public static final String UPLOAD_ERROR = "upload_error";
    boolean isPdf ;
    /** Intent Extras **/
    public static final String EXTRA_FILE_URI = "extra_file_uri";
    public static final String EXTRA_IMAGE_URI = "extra_image_uri";

    public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";

    // [START declare_ref]
    // [END declare_ref]

    @Override
    public void onCreate() {
        super.onCreate();

        // [START get_storage_ref]
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // [END get_storage_ref]
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand:intent: " + intent + ":startId: " + startId);
        if (ACTION_UPLOAD.equals(intent.getAction())) {
            Uri fileUri = intent.getParcelableExtra(MyUploadService.EXTRA_FILE_URI);
            module = intent.getStringExtra("module");
            pdfname = intent.getStringExtra("title");
            year = intent.getStringExtra("year");
            date = intent.getStringExtra("date");
            isPdf = intent.getBooleanExtra("pdf", false);

            thumbnail = intent.getParcelableExtra(EXTRA_IMAGE_URI);
            uploadFromUri(fileUri);
        }

        return START_REDELIVER_INTENT;
    }

    // [START upload_from_uri]
    private void uploadFromUri(final Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        // [START_EXCLUDE]
        taskStarted();
        showProgressNotification("progress_uploading", 0, 0);
        // [END_EXCLUDE]

        // [START get_child_ref]
        // Get a reference to store file at photos/<FILENAME>.jpg
        final Calendar cc = Calendar.getInstance();
        final String keyDocument = uid + cc.getTimeInMillis();
        final StorageReference photoRef =  isPdf ? FirebaseStorage.getInstance().getReference("Summaries/"+uid).child(keyDocument+".pdf") :
                FirebaseStorage.getInstance().getReference("Tutorials/"+uid).child(keyDocument+".pdf");
        Toast.makeText(this, "ispdf"+isPdf, Toast.LENGTH_SHORT).show();


        // [END get_child_ref]

        // Upload file to Firebase Storage
        Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());
        photoRef.putFile(fileUri).
                addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        showProgressNotification("progress_uploading",
                                taskSnapshot.getBytesTransferred(),
                                taskSnapshot.getTotalByteCount());
                    }
                })
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        // Forward any exceptions
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        Log.d(TAG, "uploadFromUri: upload success");

                        // Request the public download URL
                        return photoRef.getDownloadUrl();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final @NonNull Uri downloadUri) {
                        // Upload succeeded
                        Log.d(TAG, "uploadFromUri: getDownloadUri success");
                        StorageReference reference = FirebaseStorage.getInstance().
                                getReference("Images/" + uid + "/" + keyDocument);
                        final StorageReference photoArt = reference.child("thumbnail.png");

                        photoArt.putFile(thumbnail).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return photoArt.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (!task.isSuccessful()) {

                                    // [START_EXCLUDE]
                                    broadcastUploadFinished(null, fileUri);
                                    showUploadFinishedNotification(null, fileUri);
                                    taskCompleted();


                                    Toast.makeText(MyUploadService.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                } else {

                                    Toast.makeText(MyUploadService.this, "00000", Toast.LENGTH_SHORT).show();
                                    final  DatabaseReference ref =  isPdf ? FirebaseDatabase.getInstance().getReference()
                                            .child("Summaries").child(year).push() :
                                           FirebaseDatabase.getInstance().getReference()
                                            .child("Tutorials").child(year).push();




                                    HashMap<String, Object> map = new HashMap<>();

                                    map.put("title", pdfname);
                                    map.put("thumbnail", task.getResult().toString());
                                    map.put("uid", uid);

                                    map.put("module", module);
                                    map.put("date", date);
                                    // map.put("likes", Long.parseLong(likes));
                                    //  map.put("dislikes", Long.parseLong(dislikes));
                                    map.put("PostId", downloadUri.toString());
                                    ref.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), getString(R.string.post_uploaded), Toast.LENGTH_SHORT).show();
                                                broadcastUploadFinished(downloadUri, fileUri);
                                                showUploadFinishedNotification(downloadUri, fileUri);
                                                Toast.makeText(MyUploadService.this, "33333", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "uploadFromUri: success");

                                                taskCompleted();
                                            } else {
                                                Toast.makeText(MyUploadService.this, "22222", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "uploadFromUri: getDownloadUri fail" + task.getException().getMessage());
                                                Log.d(TAG, "uploadFromUri: getDownloadUri fail" + task.getException().getCause());

                                                Toast.makeText(getApplicationContext(), getString(R.string.post_notuploaded), Toast.LENGTH_SHORT).show();
                                                broadcastUploadFinished(null, fileUri);
                                                showUploadFinishedNotification(null, fileUri);
                                                taskCompleted();
                                            }
                                        }
                                    });




                                }
                            }

                        });



                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w(TAG, "uploadFromUri:onFailure", exception);
                        Toast.makeText(MyUploadService.this, "111111", Toast.LENGTH_SHORT).show();

                        // [START_EXCLUDE]
                        broadcastUploadFinished(null, fileUri);
                        showUploadFinishedNotification(null, fileUri);
                        taskCompleted();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END upload_from_uri]

    /**
     * Broadcast finished upload (success or failure).
     * @return true if a running receiver received the broadcast.
     */
    private boolean broadcastUploadFinished(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        boolean success = downloadUrl != null;

        String action = success ? UPLOAD_COMPLETED : UPLOAD_ERROR;

        Intent broadcast = new Intent(action)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_FILE_URI, fileUri);
        return LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(broadcast);
    }

    /**
     * Show a notification for a finished upload.
     */
    private void showUploadFinishedNotification(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        // Hide the progress notification
        dismissProgressNotification();

        // Make Intent to MainActivity
       Intent intent = new Intent(this, Profile_Activity.class)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_FILE_URI, fileUri)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean success = downloadUrl != null;
        String caption = success ? "upload_success" : "upload_failure";
        showFinishedNotification(caption, intent, success);
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPLOAD_COMPLETED);
        filter.addAction(UPLOAD_ERROR);

        return filter;
    }







}
