package com.example.teamd_donationapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle("Settings");
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        AlertDialog warning;
        Preference delete_account;
        Preference change_pwd;
        DatabaseReference mbase;
        private FirebaseAuth mAuth;
        private FirebaseUser user;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            mbase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(
                    "Are you sure you want to delete your account?")
                    .setCancelable(false)
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setTitle("Are you sure?")
                    .setPositiveButton("Yes", deleteListener)
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            warning = builder.create();
            delete_account = findPreference("delete_account");
            delete_account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    warning.show();
                    return false;
                }
            });

            change_pwd = findPreference("change_pwd");
            change_pwd.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getContext(), ChangePwd.class);
                    startActivity(intent);
                    return false;
                }
            });



        }

        private DialogInterface.OnClickListener deleteListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               // delete account here
                if (user != null) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    Query query = ref.child("users").orderByChild("email").equalTo(user.getEmail());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ss: snapshot.getChildren()) {
                                ss.getRef().removeValue();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    user.delete(); // delete from firebase auth

                }
                Toast.makeText(getContext(), "Account deleted", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), SplashScreenActivity.class);
                startActivity(intent);
            }
        };
    }




}