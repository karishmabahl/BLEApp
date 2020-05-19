package com.example.karis.projproj;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SelectedDeviceActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_device);

        String dept = getIntent().getStringExtra("device"); // for sending to API
        String savedExtra = "Files for " + getIntent().getStringExtra("device");
        TextView textDeviceName = (TextView) findViewById(R.id.textView_Name_selectedDevice);
        textDeviceName.setText(savedExtra);


        //Check internet available
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkStatus = connectivityManager.getActiveNetworkInfo();
        if(!(networkStatus != null && networkStatus.isConnected()) )
        {
            Toast.makeText(this, "Please enable Wi-FI or Mobile Data " +
                    "to proceed with downloading the file.", Toast.LENGTH_SHORT).show();
        }



        String URL = "http://iotapi1-env.eba-2mbg2e2v.ap-south-1.elasticbeanstalk.com/api/get-file?dept="+dept;

        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Bubba",response.toString());
                        try {
                            String file = response.getString("fileUrl");
                            Log.i("Bubba",file);
                             clickJsonString(file);
                        } catch(JSONException e) { e.toString(); }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Bubba",error.toString());
                    }
                }
        );

        Button downloadButton = (Button) findViewById(R.id.button_selectedActivity);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestQueue.add(jsonObjectRequest);


            }
        });
    }

    public void clickJsonString(String clickjsonString)
    {
        Uri uri = Uri.parse(clickjsonString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
