package com.example.carappv3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class DesignDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design_details);
    }

    private void getIntentExtra() {
        if(getIntent().hasExtra("position") && getIntent().hasExtra("name")) {
            int position = getIntent().getIntExtra("position", 0);
            String name = getIntent().getStringExtra("name");
        }
    }
}
