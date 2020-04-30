package com.example.carappv3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.FlannBasedMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageTracker extends AppCompatActivity {

    private CameraBridgeViewBase mOpenCvCameraView;
    public CameraBridgeViewBase.CvCameraViewListener2 camListener;

    Button match_button;

    static {
        if (!OpenCVLoader.initDebug()){
            Log.d(String.valueOf(R.string.image_tracker_tag), "OpenCV not load");
        }
    }

//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS:
//                    Log.d(String.valueOf(R.string.image_tracker_tag), "OpenCV loaded successfully");
//                    mOpenCvCameraView.enableView();
//                    System.loadLibrary("libopencv_java3.so"); // if you are working with JNI
//                    run();
//                    break;
//                default:
//                    super.onManagerConnected(status);
//                    break;
//            }
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_tracker);

        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.HelloOpenCvView);
//        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback)){
//            Log.e("OPENCV", "Cannot connect to OpenCV Manager");
//        }else Log.i("OPENCV", "opencv successfull");
        run();

//        match_button = (Button)findViewById(R.id.match_image);
//        match_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                match();
//            }
//        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        mOpenCvCameraView.enableView();
    }


    private void run(){
        camListener = new CameraBridgeViewBase.CvCameraViewListener2() {

            @Override
            public void onCameraViewStopped() {
                // TODO Auto-generated method stub
            }

            @Override
            public void onCameraViewStarted(int width, int height) {
                // TODO Auto-generated method stub
            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                Mat rgb = inputFrame.rgba();
                // here you could just return inputframe.gray(), but for illustration we do the following
                Mat gray = new Mat();
                Imgproc.cvtColor(rgb, gray, Imgproc.COLOR_RGB2GRAY);
                return gray;
            }
        };

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(camListener);

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
