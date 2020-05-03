package com.example.mlkittest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    FirebaseVisionObjectDetectorOptions options =
            new FirebaseVisionObjectDetectorOptions.Builder()
                    .setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE)
                    .build();
    FirebaseVisionObjectDetector objectDetector =
            FirebaseVision.getInstance().getOnDeviceObjectDetector(options);
}
