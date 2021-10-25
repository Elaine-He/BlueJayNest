package com.example.teamd_donationapp;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.teamd_donationapp.ui.account.AccountFragment;
import com.example.teamd_donationapp.ui.chats.ChatsFragment;
import com.example.teamd_donationapp.ui.home.HomeFragment;
import com.example.teamd_donationapp.ui.search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Item> myPostedItems; //
    private ArrayList<Item> myClaimedItems;

    DatabaseReference itemsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_chats, R.id.navigation_account)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // database
        itemsDatabase = FirebaseDatabase.getInstance().getReference("items");


    }
    @Override
    protected void onResume() {
        super.onResume();

    }


}