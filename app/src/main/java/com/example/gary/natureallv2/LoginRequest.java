package com.example.gary.natureallv2;


import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gary on 03/08/2016.Go Me
 */
public class LoginRequest extends StringRequest {
    //address of database ip may change *check*
    private static final String LOGIN_REQUEST_URL = "http://192.168.1.9/myDocs/mainProject/res/login.php";
    private Map<String,String> params;
    //putting info from edit texts into a map
    public LoginRequest( String username,  String password, Response.Listener<String> listener){
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
