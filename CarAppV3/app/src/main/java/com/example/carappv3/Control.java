package com.example.carappv3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;

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

    //request queue
    RequestQueue rq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

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

        System.out.println("Printing Vs: ");
        for(ArrayList<Point> path : carVerticesList) {
            for(Point p : path) {
                System.out.print("(" + p.x + " | " + p.y+") ");
            }
            System.out.println();
        }

        //confirm connection
        checkConnection(new VolleyCallBack() {
            @Override
            public void onSuccess() {
            }
        });

        //start drawing
        draw();
    }

    private void translate() {
        Point refP = new Point(500, 1000);
        for(ArrayList<Point> path : userVerticesList) {
            ArrayList<Point> newPath = new ArrayList<>();
            for(Point p : path) {
                if(! (Math.abs(p.x-refP.x) < 70 && Math.abs(p.y-refP.y) < 70)) {
                    newPath.add(new Point((int) (p.x * 0.1), (int) (p.y * 0.1)));
                    refP = p;
                }
            }
            carVerticesList.add(newPath);
        }
    }

    private void draw() {
        //initially car is a the center
        Point c = new Point(50, 100);
        Point pp = new Point(50, 101);
        PositionData cPd = new PositionData(c, pp);
        while(!carVerticesList.isEmpty()) {
            System.out.println("New Path");

            //get next path
            ArrayList<Point> path = carVerticesList.remove(0);
            while(!path.isEmpty()) {
                System.out.println("New Point");
                //connection ready, get next target point
                Point np = path.remove(0);
                System.out.println("np: " + np.x + "," + np.y);
                ControlData controlData = ControlData.getControlData(cPd, np);

                sendControlData(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                    }
                }, controlData);

                cPd = new PositionData(np, cPd.getC());
            }
        }
    }

    private void sendControlData(final VolleyCallBack callBack, ControlData controlData) {
        String dirStr = "dir="+Integer.toString(controlData.getTurningDirection());
        String angleStr = "angle="+Integer.toString(controlData.getAngle());
        String distStr = "dist="+Integer.toString(controlData.getDistance());
        String url = "http://10.0.0.86/control?"+dirStr+"&"+angleStr+"&"+distStr;

        StringRequest controlReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("ok")) {
                    callBack.onSuccess();
                    System.out.println("Point arrived");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ControlActivitySendControl", error.getMessage());
            }
        });

        rq.add(controlReq);

        String mdir = "RIGHT";
        if(controlData.getTurningDirection() == 1) {
            mdir = "LEFT";
        }
        System.out.println("turn " + controlData.getAngle() +
                " to the " + mdir + " move " + controlData.getDistance());
    }

    private void checkConnection(final VolleyCallBack callBack) {
        String url = "http://10.0.0.86/confirm";
        StringRequest confirmReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("confirmed")) {
                    callBack.onSuccess();
                    System.out.println("Connection confirmed");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ControlActivityCheckConnection", error.getMessage());
                System.out.println("Connection error");
            }
        });

        rq.add(confirmReq);
    }
}
