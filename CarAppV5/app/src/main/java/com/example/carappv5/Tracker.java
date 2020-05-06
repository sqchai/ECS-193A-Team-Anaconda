package com.example.carappv5;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;

import java.util.List;

public class Tracker extends AppCompatActivity {
    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    TextureView textureView;

    TextView centerXView;
    TextView centerYView;


    //ML Kit Object Detector
    FirebaseVisionObjectDetector firebaseVisionObjectDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        textureView = findViewById(R.id.view_finder);

        centerXView = findViewById(R.id.centerx_view);
        centerXView.setText("0");
        centerYView = findViewById(R.id.centery_view);
        centerYView.setText("0");

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
                                    Integer id = obj.getTrackingId();
                                    Rect bounds = obj.getBoundingBox();

                                    //Log.d("OBJ_DETECTED", (id+": "+bounds.centerX() + " | "+bounds.centerY()));
                                    centerXView.setText(Integer.toString(bounds.centerX()));
                                    centerYView.setText(Integer.toString(bounds.centerY()));

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


}
