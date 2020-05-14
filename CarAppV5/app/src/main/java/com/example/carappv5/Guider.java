package com.example.carappv5;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class Guider {
    final String TAG = "GUIDER";
    private int x;
    private int y;
    private float angle;
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
    }

    public Guider() {
        this.x = 0;
        this.y = 0;
        this.angle = 0f;
    }
}
