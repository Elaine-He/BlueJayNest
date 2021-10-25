package com.example.teamd_donationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewPostActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int GET_FROM_GALLERY = 2;
    private ImageView itemImage;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private EditText itemName;
    private EditText itemDescription;
    private EditText itemLocation;

    private Spinner categorySpinner;
    private String currentPhotoPath;

    private String itemCategory;
    private int uploadErrorFlag;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        getSupportActionBar().setTitle("Give an Item");

        itemImage = (ImageView) findViewById(R.id.imageViewItemImage);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("items");
        itemName = findViewById(R.id.editTextItemName);
        itemDescription = findViewById(R.id.editTextItemDescription);
        itemLocation = findViewById(R.id.editTextLocation);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uploadErrorFlag = 0;

        // spinner
        categorySpinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_category,
                android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                // first item in list
                itemCategory = "Furniture";
                break;
            case 1:
                // second item in list
                itemCategory = "Books";
                break;
            case 2:
                itemCategory = "Clothes";
                break;
            case 3:
                itemCategory = "Other";
                break;
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        itemCategory = "Furniture";

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GET_FROM_GALLERY);
    }

    public void onClickTakePhoto(View view) {
        dispatchTakePictureIntent();
    }

    public void onClickSelectPhoto(View view) {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                GET_FROM_GALLERY);

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadItem () {
        if (mImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri).continueWithTask(
                    new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException(); }
                            return fileReference.getDownloadUrl();
                        } })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) { Uri downloadUri = task.getResult();
                                mAuth = FirebaseAuth.getInstance();
                                FirebaseUser user = mAuth.getCurrentUser();
                                String category = categorySpinner.getSelectedItem().toString();
                                Item upload = new Item(itemName.getText().toString().trim(),
                                        itemDescription.getText().toString(),
                                        category,itemLocation.getText().toString(),downloadUri.toString(), user.getEmail());
                                upload.setId(mDatabaseRef.push().getKey()); // set id of item
                                mDatabaseRef.child(upload.getId()).setValue(upload); // add to db
                                Toast.makeText(NewPostActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                                uploadErrorFlag = 0;
                            }
                            else { Toast.makeText(NewPostActivity.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                uploadErrorFlag = 1;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewPostActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            uploadErrorFlag = 1;
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_LONG).show();
            uploadErrorFlag = 1;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // get image from gallery
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK
        && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            mImageUri = selectedImage;
            Picasso.get().load(mImageUri).into(itemImage);
        }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK
                && data!=null && data.getData()!= null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(itemImage);
        } else {
            Toast.makeText(NewPostActivity.this, "Upload unsuccessful", Toast.LENGTH_SHORT).show();

        }

        // get image from camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (mImageUri == null) {
                Toast.makeText(NewPostActivity.this, "uri is null", Toast.LENGTH_LONG).show();
            } else {
                itemImage.setImageURI(mImageUri);
            }
        }



    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Toast.makeText(this, photoFile.toString(), Toast.LENGTH_LONG).show(); // debug uri text
                mImageUri = photoURI;
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void onClickPost(View view) {
        // check if edittext fields are empty
        String name = itemName.getText().toString();
        String description = itemDescription.getText().toString();
        String location = itemLocation.getText().toString();

        boolean fieldsEmpty = false;
        if (TextUtils.isEmpty(name)) {
            itemName.setError("Item name cannot be blank");
            fieldsEmpty = true;
        }
        if (TextUtils.isEmpty(description)) {
            itemDescription.setError("Item description cannot be blank");
            fieldsEmpty = true;
        }
        if (TextUtils.isEmpty(location)) {
            itemLocation.setError("Item location cannot be blank");
            fieldsEmpty = true;
        }
        if (fieldsEmpty) {
            return; // don't do anythin
        }

        uploadItem();
        if (uploadErrorFlag == 1) {
            return;
        }
        // save post in db
//        Item item = new Item(
//                itemName.getText().toString(),
//                itemDescription.getText().toString(),
//                itemLocation.getText().toString(),
//
//                )
        // update donated count
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).child("itemsDonated");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer donated = snapshot.getValue(Integer.class) + 1;
                ref.setValue(donated);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        finish();
    }
}