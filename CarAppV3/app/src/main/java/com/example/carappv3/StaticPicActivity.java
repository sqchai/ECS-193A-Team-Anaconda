package com.example.carappv3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StaticPicActivity extends AppCompatActivity {
    StaticRouteView mRoute;
    String mVertices;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_route);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRoute = findViewById(R.id.staticRouteView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if(getIntent().hasExtra("vertices") && getIntent().hasExtra("bitmap")) {
            mVertices = getIntent().getStringExtra("vertices");
            Bitmap bitmap = getBitmapFromString(getIntent().getStringExtra("bitmap"));
            mRoute.initialize(displayMetrics,bitmap);
            return;
        }else{
            mRoute.initialize(displayMetrics);
            Log.d("onCreate","no extra intent");
            return;
        }
    }

    private Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.saved_file_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.discard:
                discardCurrentEntry();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void discardCurrentEntry(){

    }
}
