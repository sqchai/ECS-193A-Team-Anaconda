package com.example.carappv3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DesignDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design_details);

        getIntentExtra();
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
