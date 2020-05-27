package com.example.carappv3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.textclassifier.TextLinks;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Control extends AppCompatActivity {

    //user's input vertices in a 640x480 grid
    ArrayList<ArrayList<Point>> userVerticesList;
    //translated vertices in a 192cm x 144cm grid, some vertices too close to each other is combined
    ArrayList<ArrayList<Point>> carVerticesList;

    //car connection
    boolean connectionReady;

    //request queue
    RequestQueue rq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        //setup request queue
        rq = Volley.newRequestQueue(this);

        userVerticesList = new ArrayList<>();
        //extract paths info
        if(getIntent().hasExtra("vertices")) {
            String mVertices = getIntent().getStringExtra("vertices");
            ArrayList<String> stringifiedPaths = new Gson().fromJson(mVertices,ArrayList.class);
            int counter =0;
            Type listOfMyClassObject = new TypeToken<ArrayList<Point>>() {}.getType();
            for (String str : stringifiedPaths){
                List<Point> tmpPoints = new Gson().fromJson(str,listOfMyClassObject);
                userVerticesList.add(new ArrayList<Point>());
                for(Point ptr : tmpPoints){
                    userVerticesList.get(counter).add(ptr);
                }
                counter++;
            }
        } else {
            Log.d("TrackerErr", "No extra");
        }

        //translate vertices list
        carVerticesList = new ArrayList<>();
        translate();

        //confirm connection
        checkConnection();

        //start drawing
        draw();
    }

    private void translate() {
        for(ArrayList<Point> path : userVerticesList) {
            ArrayList<Point> newPath = new ArrayList<>();
            for(Point p : path) {
                newPath.add(new Point((int)(p.x * 0.3), (int)(p.y * 0.3)));
            }
            carVerticesList.add(newPath);
        }
    }

    private void draw() {
        //initially car is a the center
        Point c = new Point(96, 77);
        Point pp = new Point(96, 80);
        PositionData cPd = new PositionData(c, pp);
        while(!carVerticesList.isEmpty()) {
            //while there are more paths
            if(!connectionReady) {
                //wait for car to complete last action
                continue;
            }

            //get next path
            ArrayList<Point> path = carVerticesList.remove(0);
            while(!path.isEmpty()) {
                if(!connectionReady) {
                    //wait for car to complete last action
                    continue;
                }

                //connection ready, get next target point
                Point np = path.remove(0);
                ControlData controlData = ControlData.getControlData(cPd, np);
                sendControlData(controlData);
            }
        }
    }

    private void sendControlData(ControlData controlData) {
        String dirStr = "dir="+Integer.toString(controlData.getTurningDirection());
        String angleStr = "angle="+Integer.toString(controlData.getAngle());
        String distStr = "dist="+Integer.toString(controlData.getDistance());
        String url = "http://10.0.0.86/control?"+dirStr+"&"+angleStr+"&"+distStr;

        connectionReady = false;
        StringRequest controlReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == "ok") {
                    connectionReady = true;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ControlActivitySendControl", error.getMessage());
            }
        });

        rq.add(controlReq);
    }

    private void checkConnection() {
        String url = "http://10.0.0.86/confirm";
        connectionReady = false;
        StringRequest confirmReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == "confirmed") {
                    connectionReady = true;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ControlActivityCheckConnection", error.getMessage());
            }
        });

        rq.add(confirmReq);
    }
}
