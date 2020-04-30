package com.example.carappv3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class ImageTracker extends AppCompatActivity {

    static {
        if(!OpenCVLoader.initDebug()) {
            Log.d(String.valueOf(R.string.image_tracker_tag), "OpenCV failed to load");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_tracker);


    }

    private boolean match(String pathA, String pathB) {
        File a = new File(pathA);
        File b = new File(pathB);
        if(!a.exists() || !b.exists()) {
            return false;
        }
        Mat srcA = Imgcodecs.imread(pathA);
        Mat srcB = Imgcodecs.imread(pathB);

        //find 
    }
}
