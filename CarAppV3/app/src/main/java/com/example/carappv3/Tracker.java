package com.example.carappv3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.UseCase;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;


import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Tracker extends AppCompatActivity {
    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    //UI
    TextureView textureView;
    Button beginDrawingButton;

    //Drawing
    //user's input vertices in a 640x480 grid
    ArrayList<ArrayList<Point>> userVerticesList;
    //translated vertices in a 192cm x 144cm grid, some vertices too close to each other is combined
    ArrayList<ArrayList<Point>> carVerticesList;
    //request queue
    RequestQueue rq;

    //Vision
    //ML Kit Object Detector
    FirebaseVisionObjectDetector firebaseVisionObjectDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        textureView = findViewById(R.id.view_finder);

        beginDrawingButton = findViewById(R.id.begin_button);

        //load drawing related data
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
        checkConnection(new VolleyCallBack() {
            @Override
            public void onSuccess() {
            }
        });

        beginDrawingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start drawing
                draw();
            }
        });

        //init vision related
        //init ML Kit Vision Detector
        FirebaseVisionObjectDetectorOptions options =
                new FirebaseVisionObjectDetectorOptions.Builder()
                        .setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE)
                        .build();
        firebaseVisionObjectDetector = FirebaseVision.getInstance().getOnDeviceObjectDetector(options);
        //Check Permission and start camera
        if(allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    /**
     Vision related functions
     */
    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void startCamera() {
        CameraX.unbindAll();

        Rational aspectRatio = new Rational (textureView.getWidth(), textureView.getHeight());
        Size screen = new Size(textureView.getWidth(), textureView.getHeight()); //size of the screen
        PreviewConfig pConfig = new PreviewConfig.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetResolution(screen)
                .build();
        Preview preview = new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    //to update the surface texture we  have to destroy it first then re-add it
                    @Override
                    public void onUpdated(Preview.PreviewOutput output){
                        ViewGroup parent = (ViewGroup) textureView.getParent();
                        parent.removeView(textureView);
                        parent.addView(textureView, 0);

                        textureView.setSurfaceTexture(output.getSurfaceTexture());
                        updateTransform();
                    }
                });


        ImageAnalysisConfig aConfig = new ImageAnalysisConfig.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetResolution(new Size(480, 640))
                .build();
        ImageAnalysis analysis = new ImageAnalysis(aConfig);

        analysis.setAnalyzer(new ImageAnalysis.Analyzer() {
            private int degreesToFirebaseRotation(int degrees) {
                switch (degrees) {
                    case 0:
                        return FirebaseVisionImageMetadata.ROTATION_0;
                    case 90:
                        return FirebaseVisionImageMetadata.ROTATION_90;
                    case 180:
                        return FirebaseVisionImageMetadata.ROTATION_180;
                    case 270:
                        return FirebaseVisionImageMetadata.ROTATION_270;
                    default:
                        throw new IllegalArgumentException(
                                "Rotation must be 0, 90, 180, or 270.");
                }
            }

            @Override
            public void analyze(ImageProxy image, int rotationDegrees) {
                if (image == null || image.getImage() == null) {
                    return;
                }
                Image mediaImage = image.getImage();
                int rotation = degreesToFirebaseRotation(rotationDegrees);
                FirebaseVisionImage firebaseVisionImage =
                        FirebaseVisionImage.fromMediaImage(mediaImage, rotation);

                firebaseVisionObjectDetector.processImage(firebaseVisionImage)
                        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionObject>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionObject> firebaseVisionObjects) {
                                //Log.d("Object Detector", "Success");
                                for (FirebaseVisionObject obj : firebaseVisionObjects) {
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Object Detector", e.toString());
                            }
                        });
            }
        });

        CameraX.bindToLifecycle((LifecycleOwner)this, preview, analysis);
    }

    private void updateTransform(){
        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = (int)textureView.getRotation();

        switch(rotation){
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        mx.postRotate((float)rotationDgr, cX, cY);
        textureView.setTransform(mx);
    }


    /**
     Drawing related functions
     */
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

    private void checkConnection(final VolleyCallBack callBack) {
        String url = "http://10.0.0.86/confirm";
        StringRequest confirmReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("confirmed")) {
                    callBack.onSuccess();
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

    private void draw() {
        //initially car is a the center
        Point c = new Point(50, 100);
        Point pp = new Point(50, 101);
        PositionData cPd = new PositionData(c, pp);
        boolean ready = true;
        while(!carVerticesList.isEmpty()) {
            //get next path
            ArrayList<Point> path = carVerticesList.remove(0);
            while(!path.isEmpty()) {
                //connection ready, get next target point
                Point np = path.remove(0);
                System.out.println("np: " + np.x + ","+np.y);
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
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ControlActivitySendControl", error.getMessage());
            }
        });

        controlReq.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        rq.add(controlReq);

        System.out.println("np: control data " + controlData.getTurningDirection() + " | "
        + controlData.getAngle() + " | "
                + controlData.getDistance());
    }
}
