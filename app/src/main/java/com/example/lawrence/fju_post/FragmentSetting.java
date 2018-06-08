package com.example.lawrence.fju_post;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class FragmentSetting extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST =1;
    private Button mUpload;
    private Button mChooseImage;
    private ImageView image;
    private ProgressBar mProgressBar;

    private Uri mImageUri;

    private StorageReference mStorageRef;

    private StorageTask mUploadTask;

    private FirebaseAuth firebaseAuth;
    private Button logout;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_setting);


         firebaseAuth=FirebaseAuth.getInstance();
         logout =(Button)findViewById(R.id.buttonSignOut);

         mChooseImage=findViewById(R.id.button2);
         mUpload = findViewById(R.id.button7);
         image=findViewById(R.id.userPicture);
         mProgressBar=findViewById(R.id.progress_bar);

         mStorageRef= FirebaseStorage.getInstance().getReference("uploads");

         mChooseImage.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v){
                 openFileChooser();
             }
         });

         mUpload.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v){
                 if(mUploadTask != null && mUploadTask.isInProgress()){
                     Toast.makeText(FragmentSetting.this, "Upload in progress",Toast.LENGTH_SHORT).show();
                 }
                 uploadFile();
             }
         });


         logout.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View view){
                 firebaseAuth.signOut();
                 finish();
                 startActivity(new Intent(FragmentSetting.this,LoginActivity.class));
             }
         });


     }
    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(image);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadFile(){
         if(mImageUri != null){
             StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                     +"."+getFileExtension(mImageUri));
             mUploadTask = fileReference.putFile(mImageUri)
                     .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                         @Override
                         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                             Handler handler = new Handler();
                             handler.postDelayed(new Runnable() {
                                 @Override
                                 public void run() {
                                     mProgressBar.setProgress(0);
                                 }
                             },500);
                             Toast.makeText(FragmentSetting.this,"Upload successful",Toast.LENGTH_LONG).show();
                         }
                     })
                     .addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             Toast.makeText(FragmentSetting.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                         }
                     })
                     .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                             double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                             mProgressBar.setProgress((int)progress);
                         }
                     });
         }else{
             Toast.makeText(this,"No file selected", Toast.LENGTH_SHORT).show();
         }
    }
}
