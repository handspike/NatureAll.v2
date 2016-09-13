package com.example.gary.natureallv2;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gary on 03/08/2016.Go Me
 */
public class RegisterRequest extends StringRequest {

    //address of database ip may change *check*
    private static final String REGISTER_REQUEST_URL = "http://192.168.1.9/myDocs/mainProject/res/register.php";
    private Map<String,String> params;
//putting info from edit texts into a map
    public RegisterRequest(String name, String surname, String username, String email, String password, Response.Listener<String> listener){
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("surname", surname);
        params.put("username", username);
        params.put("email", email);
        params.put("password", password);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
