package com.example.carappv5;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Guider {
    Context context;
    final String TAG = "GUIDER";
    private int x;
    private int y;
    private float angle;

    RequestQueue requestQueue;
    StringRequest gyroRequest;


    public void updateCarPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void updateCarDir() {
        this.requestQueue.add(stringRequest);
    }

    public void guide() {

    }

    public void setStop() {

    }


    public Guider(Context context) {
        this.context = context;

        this.x = 0;
        this.y = 0;
        this.angle = 0f;

        this.requestQueue = Volley.newRequestQueue(context);

        init();
    }

    private void init() {
        final String gyroRequest="http://10.0.0.86/gyro";
        this.gyroRequest = new StringRequest(Request.Method.GET, gyroRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        angle = Float.parseFloat(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "gyro request error");
                    }
        });

        final String velocityDeltaRequest="http://10.0.0.86/v";
        this.gyroRequest = new StringRequest(Request.Method.GET, gyroRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        angle = Float.parseFloat(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "gyro request error");
                    }
        });
    }
}
