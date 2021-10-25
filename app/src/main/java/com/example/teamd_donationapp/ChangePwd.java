package com.example.teamd_donationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class ChangePwd extends AppCompatActivity {

    EditText oldPwd;
    EditText newPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);

        oldPwd = (EditText) findViewById(R.id.tw_current_password_input);
        newPwd = (EditText) findViewById(R.id.tw_new_password_input);


    }
}