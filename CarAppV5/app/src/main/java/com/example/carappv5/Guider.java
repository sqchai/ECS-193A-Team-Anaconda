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
    final String gyroRequest="http://10.0.0.86/gyro";

    public void updateCarPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void updateCarDir() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, gyroRequest,
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
        stringRequest
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
    }
}
