package com.example.esibetter;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URISyntaxException;
import java.util.HashMap;

class User_Account {
    private Uri ImageUri;
    private String Name;
    private String Status;
    private String Wilaya;
    private boolean IsMale;
    private String Birthday;
    final HashMap<String, Object> map = new HashMap<>();
    DatabaseReference refe = FirebaseDatabase.getInstance().getReference().child("users");
    DatabaseReference ref = refe.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    public User_Account(Uri imageUrl, String name, String status, String wilaya, boolean isMale, String birthday) throws URISyntaxException {
        ImageUri = imageUrl;
        Name = name;
        Status = status;
        Wilaya = wilaya;
        IsMale = isMale;
        Birthday = birthday;
    }

    // should not forget to put for every user A as a picture
    public Uri getImageUri() {
        return ImageUri;
    }

    public void setImageUri(Uri imageUri) {
        ImageUri = imageUri;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getWilaya() {
        return Wilaya;
    }

    public void setWilaya(String wilaya) {
        Wilaya = wilaya;
    }

    public boolean isMale() {
        return IsMale;
    }

    public void setMale(boolean male) {
        IsMale = male;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public boolean SaveData(FirebaseUser user, final User_Account user_account) {


        FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
        StorageReference reference;
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", user_account.getName());
        data.put("status", user_account.getStatus());
        data.put("isMale", user_account.isMale());
        data.put("wilaya", user_account.getWilaya());
        data.put("birthday", user_account.getBirthday());
        boolean data_saved = dataBase.collection("users").document(user.getUid()).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).isSuccessful();
        reference = FirebaseStorage.getInstance().getReference("Images/" + user.getUid());
        StorageReference photo = reference.child("prof_pic.png");
        final Uri imageUri;


        if (user_account.getImageUri() == null) {
            if (user_account.isMale())
                imageUri = Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + R.mipmap.male);
            else
                imageUri = Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + R.mipmap.female);
        } else
            imageUri = user_account.getImageUri();

        boolean photo_saved = photo.putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    }
                }).isSuccessful();


        map.put("name", user_account.getName());
        map.put("token", FirebaseInstanceId.getInstance().getToken());
            ref.setValue(map);


        return photo_saved || data_saved;
    }
}