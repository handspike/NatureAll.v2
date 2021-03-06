package com.example.gary.natureallv2;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by Gary on 03/08/2016.Go Me
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etUsername = (EditText)findViewById(R.id.etUsername);
        final EditText etPassword = (EditText)findViewById(R.id.etPassword);
        final Button btnLogin = (Button)findViewById(R.id.btnLogin);
        final TextView registerLink = (TextView)findViewById(R.id.tvRegisterHere);

        registerLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                //declare strings
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();


                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {

                        try {
//create JSON object
                            JSONObject jsonResponse = new JSONObject(response);

                            boolean success = jsonResponse.getBoolean("success");
//if success get value of the json.response "name" and assign it to string name

                            if (success){
                            String name = jsonResponse.getString("name");
//declare a new intent
                                Intent intent = new Intent(LoginActivity.this, UserAreaActivity.class);
                                //put extras name and username in
                                intent.putExtra("name", name);
                                intent.putExtra("username", username);
//start LoginActivity
                                LoginActivity.this.startActivity(intent);
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Login Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                };
//calling the LoginRequest
                LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        }); 

    }
}