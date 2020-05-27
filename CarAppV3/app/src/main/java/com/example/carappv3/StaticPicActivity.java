package com.example.carappv3;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
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

import com.example.carappv3.database.DrawingDBHelper;
import com.example.carappv3.database.DrawingSchema;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StaticPicActivity extends AppCompatActivity {
    StaticRouteView mRoute;
    String mVertices;
    String mFilename;
    SQLiteDatabase mDatabase;
    Context mContext;
    List<List<Point>> verticesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_route);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = getApplicationContext();
        mDatabase = new DrawingDBHelper(mContext).getWritableDatabase();
        mRoute = findViewById(R.id.staticRouteView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if(getIntent().hasExtra("vertices") && getIntent().hasExtra("bitmap")&&getIntent().hasExtra("filename")) {
            verticesList = new ArrayList<List<Point>>();
            mVertices = getIntent().getStringExtra("vertices");
            //Log.d("vertices are", mVertices)
            //Log.d("TAG", Integer.toString(verticesList.get(0).get(0).x));
            mFilename = getIntent().getStringExtra("filename");
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
                finish();
                return true;
            case R.id.start_drawing_button:
                startDrawingActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void discardCurrentEntry(){
        mDatabase.delete(DrawingSchema.DrawingTable.NAME, "_id = ?", new String[]{mFilename});
        mDatabase.close();
    }

    private void startDrawingActivity() {
        Intent intent = new Intent(this, Tracker.class);
        intent.putExtra("vertices", this.mVertices);
        this.startActivity(intent);
    }
}
