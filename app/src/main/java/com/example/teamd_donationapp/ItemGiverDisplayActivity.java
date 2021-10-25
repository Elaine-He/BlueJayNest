package com.example.teamd_donationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ItemGiverDisplayActivity extends AppCompatActivity {

    private TextView itemName;
    private TextView itemDescription;
    private TextView itemLocation;
    private TextView itemCategory;
    private ImageView itemImage;
    private DatabaseReference databaseReference;
    private String itemId;
    private Item item;
    private Button accept;
    private Button deny;
    private Button chat;
    private Button finishTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_giver_display);
        getSupportActionBar().setTitle("Giver Item Details");
        databaseReference = FirebaseDatabase.getInstance().getReference("items");

        itemName = findViewById(R.id.TextViewItemName);
        itemDescription = findViewById(R.id.textViewItemDescription);
        itemLocation = findViewById(R.id.textViewLocation);
        itemCategory = findViewById(R.id.textViewCategory);
        itemImage = findViewById(R.id.imageViewItemImageDisplay);
        accept = findViewById(R.id.buttonAccept);
        deny = findViewById(R.id.buttonDeny);
        chat = findViewById(R.id.buttonChat);
        finishTransaction = findViewById(R.id.buttonFinishTransaction);
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            item = (Item) intent.getSerializableExtra("item");
            itemId = item.getId();
            itemName.setText(item.getName());
            itemDescription.setText(item.getDescription());
            itemLocation.setText(item.getLocation());
            itemCategory.setText(item.getCategory());
            Picasso.get().load(item.getImageURL()).into(itemImage);
            if (item.getClaimedBy()!=null) {
                TextView claimedText = (TextView) findViewById(R.id.textViewClaimer);
                claimedText.setVisibility(View.VISIBLE);
                TextView claimerName = (TextView) findViewById(R.id.textViewClaimerName);
                claimerName.setText(item.getClaimedBy());
                claimerName.setVisibility(View.VISIBLE);
                accept.setVisibility(View.VISIBLE);
                deny.setVisibility(View.VISIBLE);
                finishTransaction.setVisibility(View.VISIBLE);
                if (item.isAccepted() != null && item.isAccepted().equals("true")) {
                    accept.setVisibility(View.GONE);
                    deny.setVisibility(View.GONE);
                    chat.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void onClickFinishTransaction(View view) {
        // do stuff
        if (item.getClaimedBy() != null) {
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

    public void onClickAccept(View view) {
        accept.setVisibility(View.GONE);
        deny.setVisibility(View.GONE);
        chat.setVisibility(View.VISIBLE);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("items");
        Query query = ref.orderByChild("id").equalTo(item.getId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().child(item.getId()).child("accepted").setValue("true");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        item.setAccepted();


    }
    public void onClickDeny(View view) {
        item.setStatus("posted");
        item.setClaimedBy(null);
        finish();
    }
    public void onClickChat(View view) {
        Intent intent = new Intent(this, ChatActivity.class);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ss: snapshot.getChildren()) {
                    User u = ss.getValue(User.class);
                    if (u.getEmail().equals(item.getClaimedBy())) {

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


}