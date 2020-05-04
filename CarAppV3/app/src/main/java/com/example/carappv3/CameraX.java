package com.example.carappv3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;

import android.content.Context;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.Surface;

public class CameraX extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_x);

        ImageCapture imageCapture = new ImageCapture.Builder().build();

        OrientationEventListener orientationEventListener = new OrientationEventListener((Context)this) {
            @Override
            public void onOrientationChanged(int orientation) {
                int rotation;

                // Monitors orientation values to determine the target rotation value
                if (orientation >= 45 && orientation < 135) {
                    rotation = Surface.ROTATION_270;
                } else if (orientation >= 135 && orientation < 225) {
                    rotation = Surface.ROTATION_180;
                } else if (orientation >= 225 && orientation < 315) {
                    rotation = Surface.ROTATION_90;
                } else {
                    rotation = Surface.ROTATION_0;
                }

                imageCapture.setTargetRotation(rotation);
            }
        };

        orientationEventListener.enable();
    }
}
