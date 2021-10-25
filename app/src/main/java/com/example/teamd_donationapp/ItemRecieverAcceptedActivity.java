package com.example.teamd_donationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ItemRecieverAcceptedActivity extends AppCompatActivity {

    private TextView userName;
    private TextView itemName;
    private TextView itemDescription;
    private TextView itemLocation;
    private TextView itemCategory;
    private ImageView itemImage;
    private FirebaseAuth mAuth;
    private Item item;
    private Button chat;
    private Button finishTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_reciever_accepted);
        getSupportActionBar().setTitle("Receiver Item Details");
        mAuth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.PosterName);
        itemName = findViewById(R.id.TextViewItemName);
        itemDescription = findViewById(R.id.textViewItemDescription);
        itemLocation = findViewById(R.id.textViewLocation);
        itemCategory = findViewById(R.id.textViewCategory);
        itemImage = findViewById(R.id.imageViewItemImageDisplay);
        chat = findViewById(R.id.chat);
        finishTransaction = findViewById(R.id.finishTransactionButton);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            item = (Item) intent.getSerializableExtra("item");
            userName.setText(item.getPostedBy());
            itemName.setText(item.getName());
            itemDescription.setText(item.getDescription());
            itemLocation.setText(item.getLocation());
            itemCategory.setText(item.getCategory());
            Picasso.get().load(item.getImageURL()).into(itemImage);


        }
    }

    public void onClickChat(View view) {
        Intent intent = new Intent(this, ChatActivity.class);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ss: snapshot.getChildren()) {
                    User u = ss.getValue(User.class);
                    if (u.getEmail().equals(item.getPostedBy())) {

                        intent.putExtra("name", u.getUserName());
                        intent.putExtra("image", u.getProfilePicture());
                        intent.putExtra("uid", u.getUid());
                        startActivity(intent);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    public void onClickFinishTransaction(View view) {
        if (item.getPostedBy() != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("items");
            Query query = ref.orderByChild("id").equalTo(item.getId());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    snapshot.getRef().child(item.getId()).child("status").setValue("finished");
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

//        databaseReference.child("items").child(itemId).child("status").setValue("finished");
        finish();
    }
}