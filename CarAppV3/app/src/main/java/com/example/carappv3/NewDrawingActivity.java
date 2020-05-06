package com.example.carappv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

public class NewDrawingActivity extends AppCompatActivity {
    DrawingView drawingView;
    Bundle mBundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
        setContentView(R.layout.activity_new_drawing);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set drawing view
        drawingView = findViewById(R.id.drawingView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if(getIntent().hasExtra("vertices") && getIntent().hasExtra("bitmap")) {
            String vertices = getIntent().getStringExtra("vertices");
            Bitmap bitmap = getBitmapFromString(getIntent().getStringExtra("bitmap"));
            drawingView.initialize(displayMetrics,bitmap);
            return;
            //setDetailsView(position, name);
        }
        drawingView.initialize(displayMetrics);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_drawing_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawingView.save();
                finish();
                return true;
            case R.id.redo:
                drawingView.redo();
                return true;
            case R.id.undo:
                drawingView.undo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}
