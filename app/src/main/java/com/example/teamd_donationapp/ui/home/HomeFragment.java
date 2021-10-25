package com.example.teamd_donationapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamd_donationapp.Item;
import com.example.teamd_donationapp.ItemAdapter;
import com.example.teamd_donationapp.MainActivity;
import com.example.teamd_donationapp.NewPostActivity;
import com.example.teamd_donationapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private MainActivity mainActivity;
    private RecyclerView itemList;
    private RecyclerView claimedView;
    private ArrayList<Item> data;
    private ArrayList<Item> claimedItems;
    private HomeFragmentRecyclerViewAdapter adapter;
    private ClaimedHomeFragmentRecyclerViewAdapter claimedAdapter;
    DatabaseReference mbase;
    private FirebaseAuth mAuth;
    private String userEmail;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mbase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userEmail = user.getEmail();
// Read from the database
        data = new ArrayList<>();
        claimedItems = new ArrayList<>();
        itemList =(RecyclerView) root.findViewById(R.id.myItemsRecyclerView);
        itemList.setLayoutManager(new LinearLayoutManager(root.getContext()));

        claimedView = (RecyclerView) root.findViewById(R.id.myClaimedItemsRecyclerView);
        claimedView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        mainActivity = (MainActivity) getActivity();
        adapter = new HomeFragmentRecyclerViewAdapter(getContext(), data);
        claimedAdapter = new ClaimedHomeFragmentRecyclerViewAdapter(getContext(),claimedItems);
        itemList.setAdapter(adapter);
        claimedView.setAdapter(claimedAdapter);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // moved to onViewCreated
        mbase.child("items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                data = new ArrayList<>();
                claimedItems = new ArrayList<>();
                for (DataSnapshot s: dataSnapshot.getChildren()){
                    Item item = s.getValue(Item.class);
                    if(item.getPostedBy()!=null && item.getPostedBy().equals(userEmail)
                            &&!item.getStatus().equals("finished")){
                        data.add(item);
                    }
                    if(item.getClaimedBy()!=null && item.getClaimedBy().equals(userEmail)
                    &&!item.getStatus().equals("finished")){
                        claimedItems.add(item);
                    }
                }
                if (getActivity() != null) {
                    adapter = new HomeFragmentRecyclerViewAdapter(getContext(),data);
                    itemList.setAdapter(adapter);
                    claimedAdapter = new ClaimedHomeFragmentRecyclerViewAdapter(getContext(),claimedItems);
                    claimedView.setAdapter(claimedAdapter);
                }
//                 adapter = new HomeFragmentRecyclerViewAdapter(getContext(),data);
//                 itemList.setAdapter(adapter);
//                 claimedAdapter = new HomeFragmentRecyclerViewAdapter(getContext(),claimedItems);
//                 claimedView.setAdapter(claimedAdapter);
//                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("print", "Failed to read value.", error.toException());
            }
        });

        BottomNavigationView bnv = (BottomNavigationView) getView().findViewById(R.id.nav_view);
//        int bnv_height = (int) (bnv.getHeight() / getResources().getDisplayMetrics().density);
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewPostActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}