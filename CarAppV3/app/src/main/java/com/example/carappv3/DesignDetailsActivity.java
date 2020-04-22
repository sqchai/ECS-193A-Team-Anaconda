package com.example.carappv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DesignDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getIntentExtra();

        //test WiFi Connection
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://www.google.com";


        Button wifiButton = findViewById(R.id.wifi_button);
        wifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
