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

public class ItemRecieverDisplayActivity extends AppCompatActivity {

    private TextView userName;
    private TextView itemName;
    private TextView itemDescription;
    private TextView itemLocation;
    private TextView itemCategory;
    private ImageView itemImage;
    private FirebaseAuth mAuth;
    private Item item;
    private Button claim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_reciever_display);
        getSupportActionBar().setTitle("Receiver Item Details");

        mAuth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.textViewClaimerName);
        itemName = findViewById(R.id.TextViewItemName);
        itemDescription = findViewById(R.id.textViewItemDescription);
        itemLocation = findViewById(R.id.textViewLocation);
        itemCategory = findViewById(R.id.textViewCategory);
        itemImage = findViewById(R.id.imageViewItemImageDisplay);
        claim = findViewById(R.id.buttonClaim);
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            item = (Item) intent.getSerializableExtra("item");
            userName.setText(item.getPostedBy());
            itemName.setText(item.getName());
            itemDescription.setText(item.getDescription());
            itemLocation.setText(item.getLocation());
            itemCategory.setText(item.getCategory());
            Picasso.get().load(item.getImageURL()).into(itemImage);
            if (item.getClaimedBy()!= null) {
                claim.setVisibility(View.GONE);

            }
        }

    }

    public void onClickClaim(View view) {
        // do stuff here
        if (item != null) {
            item.setClaimedBy(mAuth.getCurrentUser().getEmail());
            item.setStatus("claimed");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("items");
            Query query = ref.orderByChild("id").equalTo(item.getId());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    snapshot.getRef().child(item.getId()).child("status").setValue("claimed");
                    snapshot.getRef().child(item.getId()).child("claimedBy").setValue(mAuth.getCurrentUser().getEmail());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
//        ref.child(item.getId()).child("status").setValue("claimed");

        // update items received count for user
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).child("itemsReceived");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer received = snapshot.getValue(Integer.class) + 1;
                ref.setValue(received);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        finish();
    }
}