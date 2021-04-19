package com.example.howtostoreimageandretreveimageformfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText image_name_edit_text;
    private Button chose_image,save_image,show_image;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Uri imageUri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private UploadTask uploadTask;

    private static final int IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference("Upload");
        storageReference = FirebaseStorage.getInstance().getReference("Upload");

        image_name_edit_text = (EditText)findViewById(R.id.image_name_id);
        chose_image = (Button)findViewById(R.id.chose_image);
        save_image  = (Button)findViewById(R.id.save_button);
        show_image = (Button)findViewById(R.id.show_all_image);
        imageView = (ImageView)findViewById(R.id.image_id);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_id);


        chose_image.setOnClickListener(this);
        save_image.setOnClickListener(this);
        show_image.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chose_image:
                    oPenFileChooser();
                break;
            case R.id.save_button:
                progressBar.setVisibility(View.VISIBLE);
                if (uploadTask != null && uploadTask.isInProgress()){
                    Toast.makeText(getApplicationContext(),"Image task going",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"Yes Click",Toast.LENGTH_SHORT).show();
                    saveImage();
                }
                break;
        }
    }
    // getting the extension of image
    public String getFileExtension(Uri imageUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }
    private void saveImage() {

        String image_name_value = image_name_edit_text.getText().toString().trim();

        if (image_name_value.isEmpty()){
            image_name_edit_text.setError("set A name");
            image_name_edit_text.requestFocus();
            return;
        }

        StorageReference ref = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));

        ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Image Stored Successfuly",Toast.LENGTH_SHORT).show();
                Upload upload = new Upload(image_name_value,taskSnapshot.getStorage().getDownloadUrl().toString());
                String uploadId = databaseReference.push().getKey();
                databaseReference.child(uploadId).setValue(upload);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Image not Upload "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void oPenFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMAGE_REQUEST  && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);
        }
    }
}