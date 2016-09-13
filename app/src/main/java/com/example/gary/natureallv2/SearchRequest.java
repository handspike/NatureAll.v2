package com.example.gary.natureallv2;
/*
Followed the login request example, extended String Request but had to import volley.Request in order to
get rid of the error on Request.Method.POST (Line 25), was getting no other options offered. At this stage I don't know if it will work
 */


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gary on 13/09/2016.Go Me
 */
public class SearchRequest extends StringRequest {
    //address of database ip may change *check*
    private static final String SEARCH_REQUEST_URL = "http://192.168.1.9/myDocs/mainProject/search_animal_and.php";
    private Map<String, String> params;

    //putting info from edit texts into a map
    public SearchRequest(String commonname, Response.Listener<String> listener) {
        super(Request.Method.POST, SEARCH_REQUEST_URL, listener, null);

        params = new HashMap<>();
        params.put("commonname", commonname);

    }
}
