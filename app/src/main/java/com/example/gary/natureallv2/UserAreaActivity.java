package com.example.gary.natureallv2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class UserAreaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        final EditText etUsername = (EditText)findViewById(R.id.etUsername);
        final TextView welcomeMsg = (TextView)findViewById(R.id.tvWelcomeMsg);
    }
}
