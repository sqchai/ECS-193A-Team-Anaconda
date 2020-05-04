package com.example.carappv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class DesignDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getIntentExtra();

        //test WiFi Connection
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url ="http://10.0.0.86/toggle";
        final String urlM ="http://10.0.0.86/motor?m1speed=0&m2speed=0";
        final String urlS="http://10.0.0.86/servo1";

        final Button wifiButton = findViewById(R.id.wifi_button);
        wifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, urlS, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("R: ", response);
                    }
                }, new Response.ErrorListener () {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("No Response: ", error.getMessage());
                    }
                });

                requestQueue.add(stringRequest);
            }
        });

        final SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int m1_speed = 0;
                int m2_speed = 0;
                m1_speed = seekBar.getProgress();
                m2_speed = seekBar.getProgress();

                MappingSpeed mappingSpeed = new MappingSpeed();
                m1_speed = mappingSpeed.convert(m1_speed, 0, 1023);
                m2_speed = mappingSpeed.convert(m2_speed, 0, 1023);

                String speedChangeUrl = "http://10.0.0.86/motor?m1speed=" + m1_speed + "&m2speed=" + m2_speed;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, speedChangeUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("R: ", response);
                    }
                }, new Response.ErrorListener () {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("No Response: ", error.getMessage());
                    }
                });

                requestQueue.add(stringRequest);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getIntentExtra() {
        if(getIntent().hasExtra("position") && getIntent().hasExtra("name")) {
            int position = getIntent().getIntExtra("position", 0);
            String name = getIntent().getStringExtra("name");

            setDetailsView(position, name);
        }
    }

    private void setDetailsView(int position, String name) {
        String details = "Id #" + position + ": " + name;
        TextView textView = (TextView)findViewById(R.id.design_detail_text);
        textView.setText(details);
    }
}
