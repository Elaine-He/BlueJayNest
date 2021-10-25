package com.example.teamd_donationapp.ui.account;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.teamd_donationapp.Item;
import com.example.teamd_donationapp.MainActivity;
import com.example.teamd_donationapp.NewPostActivity;
import com.example.teamd_donationapp.R;
import com.example.teamd_donationapp.SettingsActivity;
import com.example.teamd_donationapp.SplashScreenActivity;
import com.example.teamd_donationapp.User;
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

import java.util.Objects;

public class AccountFragment extends Fragment implements View.OnClickListener {

    DatabaseReference mbase;
    private FirebaseAuth mAuth;
    private String userEmail;
    private FirebaseUser user;
    ImageView profilePicture;
    TextView username;
    TextView email;
    TextView profileText;
    TextView settings;
    TextView editProfile;
    TextView logout;
    Button itemsDonated;
    Button itemsReceived;
    Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private int uploadErrorFlag;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_account, container, false);

        mbase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        email = (TextView) root.findViewById(R.id.account_email);
        email.setText(user.getEmail());
        username = (TextView) root.findViewById(R.id.account_name);
        profileText = (TextView) root.findViewById(R.id.profile_body);
        profilePicture = (ImageView) root.findViewById(R.id.profile_image);
        settings = (TextView) root.findViewById(R.id.account_settings);
        editProfile = (TextView) root.findViewById(R.id.account_edit_profile);
        logout = (TextView) root.findViewById(R.id.account_logout);
        itemsDonated = (Button) root.findViewById(R.id.account_donated_button);
        itemsReceived = (Button) root.findViewById(R.id.account_received_button);
        settings.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        logout.setOnClickListener(this);
        itemsDonated.setOnClickListener(this);
        itemsReceived.setOnClickListener(this);
        profilePicture.setOnClickListener(this);
        profileText.setOnClickListener(this);
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_pics");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ss: snapshot.getChildren()) {
                    User u = ss.getValue(User.class);
                    if (u.getUid().equals(mAuth.getUid())) {
                        if (u.getUserName() != null) {
                            username.setText(u.getUserName());
                        }
                        if (u.getProfileText() != null) {
                            profileText.setText(u.getProfileText());
                        }
                        if (u.getProfilePicture() != null) {
                            Picasso.get().load(u.getProfilePicture()).fit().centerCrop().into(profilePicture);
                        } else {
                            profilePicture.setImageResource(R.drawable.ic_account_circle_black_24dp);
                        }
                        Integer donated = u.getItemsDonated();
                        Integer received = u.getItemsReceived();
                        itemsDonated.setText("Donated - " + donated.toString());
                        itemsReceived.setText("Received - " + received.toString());

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_settings:
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.account_edit_profile:
                Activity activity = getActivity();
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("New Username");
                final EditText input = new EditText(activity);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        TextView tv1 = (TextView)getView().findViewById(R.id.account_name);
                        String text = input.getText().toString();
                        tv1.setText(text);
                        mDatabaseRef.child(mAuth.getUid()).child("userName").setValue(text);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert.show();
                break;
            case R.id.account_logout:
                new AlertDialog.Builder(getContext())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getActivity(), SplashScreenActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .show();
                break;
            case R.id.account_donated_button:
                break;
            case R.id.account_received_button:
                break;
            case R.id.profile_image:
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                        1);
                break;
            case R.id.profile_body:
                Activity activity2 = getActivity();
                AlertDialog.Builder alert2 = new AlertDialog.Builder(activity2);
                alert2.setTitle("Edit Profile");
                final EditText input2 = new EditText(activity2);
                alert2.setView(input2);

                alert2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        TextView tv1 = (TextView)getView().findViewById(R.id.profile_body);
                        String text = input2.getText().toString();
                        tv1.setText(text);
                        mDatabaseRef.child(mAuth.getUid()).child("profileText").setValue(text);
                    }
                });

                alert2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert2.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // get image from gallery
        if(requestCode== 1 && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            mImageUri = selectedImage;
            Picasso.get().load(mImageUri).into(profilePicture);
        }else {
            Toast.makeText(getContext(), "Upload unsuccessful", Toast.LENGTH_SHORT).show();
        }
        uploadItem();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
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
                                mDatabaseRef.child(mAuth.getUid()).child("profilePicture").setValue(downloadUri.toString());
                                Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                                uploadErrorFlag = 0;
                            }
                            else { Toast.makeText(getContext(), "upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                uploadErrorFlag = 1;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            uploadErrorFlag = 1;
                        }
                    });
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_LONG).show();
            uploadErrorFlag = 1;
        }
    }
}