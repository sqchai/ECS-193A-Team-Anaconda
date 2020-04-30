package com.example.carappv3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.FlannBasedMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageTracker extends AppCompatActivity {

    static {
        if(!OpenCVLoader.initDebug()) {
            Log.d(String.valueOf(R.string.image_tracker_tag), "OpenCV failed to load");
        }
    }

    Button match_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_tracker);

        match_button = (Button)findViewById(R.id.match_image);
        match_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                match();
            }
        });
    }

    private boolean match(String pathA, String pathB) {
        File a = new File(pathA);
        File b = new File(pathB);
        if(!a.exists() || !b.exists()) {
            return false;
        }
        Mat srcA = Imgcodecs.imread(pathA);
        Mat srcB = Imgcodecs.imread(pathB);

        //find key points and descriptor using orb
        ORB orb = ORB.create();
        MatOfKeyPoint keyPointA = new MatOfKeyPoint();
        MatOfKeyPoint keyPointB = new MatOfKeyPoint();
        Mat descriptionA = new Mat();
        Mat descriptionB = new Mat();
        orb.compute(srcA, keyPointA, descriptionA);
        orb.compute(srcB, keyPointB, descriptionB);

        //match
        List<MatOfDMatch> matches = new ArrayList<>();

        FlannBasedMatcher flannBasedMatcher = FlannBasedMatcher.create();
        flannBasedMatcher.knnMatch(descriptionA, descriptionB, matches, 2);
        float ratio = (float) 0.75;

        //find good matches
        List<MatOfDMatch> goodMatches = new ArrayList<>();
        for (MatOfDMatch match : matches) {
            List<DMatch> dMatches = match.toList();
            if (dMatches.size() == 2 && (dMatches.get(0).distance < dMatches.get(0).distance * ratio)) {
                goodMatches.add(match);
            }
        }

        //decide if this is a real match
        if (goodMatches.size() > 5) {
            Log.d(String.valueOf(R.string.image_tracker_tag), "Matched");
            return true;
        }

        return false;

    }
}
