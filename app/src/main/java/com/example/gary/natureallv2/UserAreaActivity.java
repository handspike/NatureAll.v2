package com.example.gary.natureallv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class UserAreaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        final EditText etUsername = (EditText)findViewById(R.id.etUsername);
        final TextView welcomeMsg = (TextView)findViewById(R.id.tvWelcomeMsg);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String username = intent.getStringExtra("username");

        String message = name + " welcome to your user area";
        welcomeMsg.setText(message);
        etUsername.setText(username);
    }
    public void postAPictureBtnClicked(View view) {
        Intent getCameraPic = new Intent(this, CameraPicActivity.class);
        final int result = 1;

        startActivity(getCameraPic);
    }
    public void searchAnimalButtonClicked(View view) {
        Intent searchAnimal = new Intent(this, SearchAnimalActivity.class);
        final int result = 1;

        startActivity(searchAnimal);
    }
}
