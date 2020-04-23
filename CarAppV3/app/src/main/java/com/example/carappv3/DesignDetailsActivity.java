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
    int speed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getIntentExtra();

        //test WiFi Connection
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url ="http://10.0.0.86/toggle";
        String url1 ="http://10.0.0.86/m1";
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response: ", "succeed");
            }
        }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("No Response: ", error.getMessage());
            }
        });



        final Button wifiButton = findViewById(R.id.wifi_button);
        wifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestQueue.add(stringRequest);
            }
        });

        final SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                speed = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
