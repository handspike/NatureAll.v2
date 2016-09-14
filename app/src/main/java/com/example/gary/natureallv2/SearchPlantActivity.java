package com.example.gary.natureallv2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Gary on 12/09/2016.Go Me
 */
public class SearchPlantActivity extends AppCompatActivity{



        EditText etSearchName;
        String commonname;
        Button btnSearch;
        RequestQueue requestQueue;
        String showUrl = "http://192.168.1.10/myDocs/mainProject/search_plant_and.php";
        TextView tvName;
        TextView tvLatinName;
        TextView tvDescription;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.search_plant_activity);
            etSearchName = (EditText) findViewById(R.id.etSearchName);
            tvName = (TextView) findViewById(R.id.tvName);
            tvLatinName = (TextView) findViewById(R.id.tvLatinName);
            tvDescription = (TextView) findViewById(R.id.tvDescription);
            btnSearch = (Button) findViewById(R.id.btnSearch);


            requestQueue = Volley.newRequestQueue(getApplicationContext());

            btnSearch.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                   commonname = etSearchName.getText().toString();


                   // System.out.println("ww");
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                            showUrl, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response.toString());

                            try {
                                JSONArray plant = response.getJSONArray("plants");
                                for (int i = 0; i < plant.length(); i++) {
                                    JSONObject plants = plant.getJSONObject(i);
//Change here for different string names
                                    String name = plants.getString("name");
                                    String latinName = plants.getString("latinName");
                                    String description = plants.getString("description");

                                    tvName.setText(name);
                                    tvLatinName.setText(latinName);
                                    tvDescription.setText(description);

                                }
                                //result.append("===\n");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.append(error.getMessage());

                        }
                    });


                    requestQueue.add(jsonObjectRequest);

                }
            });




        }


}

