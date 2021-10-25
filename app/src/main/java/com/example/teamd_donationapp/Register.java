package com.example.teamd_donationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextView switchToLoginActivity;
    Button SignUp;
    EditText username;
    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        username = (EditText)findViewById(R.id.username_register);
        email = (EditText)findViewById(R.id.email_register);
        password = (EditText)findViewById(R.id.password_register);
        switchToLoginActivity = findViewById(R.id.nav_to_login);
        switchToLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });

        SignUp = findViewById(R.id.signupbtn);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();
            }
        });

    }
    private void createAccount(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
                            String uid = FirebaseAuth.getInstance().getUid();
                            User user = new User(uid, email, email); // create new user
                            mDatabaseRef.child(uid).setValue(user); // add user to database by uid

                            Toast.makeText(Register.this, "Register succeed.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Register.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("id", email);
                            startActivity(intent); // login after signup
                        } else {
                            Log.w("create", "createUserWithEmail:failure", task.getException());

                            Toast.makeText(Register.this, "Register failed." + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(switchActivityIntent);
    }

    private void SignUp() {
        boolean fieldsEmpty = false;
        String username = this.username.getText().toString();
        String email = this.email.getText().toString() + "@jhu.edu";
        String password = this.password.getText().toString();
        if (TextUtils.isEmpty(username)) {
            this.username.setError("Email cannot be empty");
            fieldsEmpty = true;
        }
        if (TextUtils.isEmpty(email)) {
            this.email.setError("Email cannot be empty");
            fieldsEmpty = true;
        }
        if (TextUtils.isEmpty(password)) {
            this.password.setError("Password cannot be empty");
            fieldsEmpty = true;
        }
        if (fieldsEmpty) {
            return;
        }

        createAccount(username,email,password);
    }


}