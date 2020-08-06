package com.example.esibetter.courses.Tasks;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.esibetter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class MyDownloadService extends MyBaseTaskService{

    public static final String ACTION_DOWNLOAD = "action_download";
    public static final String DOWNLOAD_COMPLETED = "download_completed";
    public static final String DOWNLOAD_ERROR = "download_error";
    public static final String EXTRA_DOWNLOAD_PATH = "extra_download_path";
    public static final String EXTRA_BYTES_DOWNLOADED = "extra_bytes_downloaded";

    private StorageReference mStorageRef;
    private static String TAG;
    private static String pdfname;

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        TAG ="";
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand:" + intent + ":" + startId);

        pdfname = intent.getStringExtra("pdfname");

        if (ACTION_DOWNLOAD.equals(intent.getAction())) {
            mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://pdfsociety-936a1.appspot.com")
                    .child(intent.getStringExtra("uid"))
                    .child(intent.getStringExtra("pdfkey"))
                    .child(intent.getStringExtra("pdfname"));
            String downloadFileName = mStorageRef.getName();
            downloadFromPath(downloadFileName);
        }

        return START_REDELIVER_INTENT;
    }

    private void downloadFromPath(final String downloadPath) {
        Log.d(TAG, "downloadFromPath:" + downloadPath);

        // Mark task started
        taskStarted();
      //  showProgressNotification(pdfname,getString(R.string.progress_downloading), 0, 0);

        File rootPath = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        final File localFile = new File(rootPath,downloadPath);
        // Download and get total bytes
        mStorageRef.getFile(localFile)
                .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                      //  showDownloadProgressNotification(pdfname,"Downloading", taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        //Log.d(TAG, "download:SUCCESS");
                        Log.e("firebase ",";local tem file created  created " +localFile.toString());

                        showDownloadFinishedNotification(pdfname,downloadPath, (int) taskSnapshot.getTotalByteCount());

                        // Mark task completed
                        taskCompleted();
                        Toast.makeText(MyDownloadService.this, "Download Finished", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.w(TAG, "download:FAILURE", exception);
                        showDownloadFinishedNotification(pdfname,downloadPath, -1);
                        Toast.makeText(MyDownloadService.this, "Download Failed", Toast.LENGTH_SHORT).show();
                        // Mark task completed
                        taskCompleted();
                    }
                });
    }

    private void showDownloadFinishedNotification(String Title,String downloadPath, int bytesDownloaded) {
        // Hide the progress notification
        dismissProgressNotification();

       // Intent intent = new Intent(MyDownloadService.this, HomeActivity.class);
        File rootPath = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));

        File localFile = new File(rootPath,downloadPath);
        Log.d("Pdfsociety","localFile "+localFile);
        // Download and get total bytes
        mStorageRef.getFile(localFile);

        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext=localFile.getName().substring(localFile.getName().indexOf(".")+1);
        String type = mime.getMimeTypeFromExtension(ext);
        Intent openFile = new Intent(Intent.ACTION_VIEW, Uri.fromFile(localFile));
        openFile.setDataAndType(Uri.fromFile(localFile), type);

        openFile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openFile, 0);



        boolean success = bytesDownloaded != -1;
     //   String caption = success ? getString(R.string.download_success) : getString(R.string.download_failure);
       // showFinishedNotification(Title,caption, pendingIntent, success);
    }
}

