package com.example.carappv3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.FlannBasedMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageTracker extends AppCompatActivity {

    private CameraBridgeViewBase mOpenCvCameraView;
    public CameraBridgeViewBase.CvCameraViewListener2 camListener;

    Button match_button;

    Mat srcA;

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

        initMatch();
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
                //Mat gray = new Mat();
                //Imgproc.cvtColor(rgb, gray, Imgproc.COLOR_RGB2GRAY);
                Mat mat = match(rgb);
                return mat;

//                Mat mRgba = inputFrame.rgba();
//                Mat mRgbaT = mRgba.t();
//                Core.flip(mRgba.t(), mRgbaT, 1);
//                Imgproc.resize(mRgbaT, mRgbaT, mRgba.size());
//                return mRgbaT;
            }
        };

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(camListener);

    }


    private void initMatch() {
        //load A
        try {
            srcA = Utils.loadResource(this, R.drawable.cococola1, CvType.CV_8SC4);
            Log.d("Load A", "success to load A");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Load A", "failed to load A");
        }
    }

    private Mat match(Mat srcB) {
//        File a = new File(pathA);
//        File b = new File(pathB);
//        if(!a.exists() || !b.exists()) {
//            return frame;
//        }
//        Mat srcA = Imgcodecs.imread(pathA);
//        Mat srcB = Imgcodecs.imread(pathB);

        if (srcA==null) {
            return srcB;
        }

        //find key points and descriptor using orb
        ORB orb = ORB.create();
        MatOfKeyPoint keyPointA = new MatOfKeyPoint();
        MatOfKeyPoint keyPointB = new MatOfKeyPoint();
        Mat descriptionA = new Mat();
        Mat descriptionB = new Mat();
        orb.compute(srcA, keyPointA, descriptionA);
        orb.compute(srcB, keyPointB, descriptionB);

        //match
//        List<MatOfDMatch> matches = new ArrayList<>();
//
//        FlannBasedMatcher flannBasedMatcher = FlannBasedMatcher.create();
//        flannBasedMatcher.knnMatch(descriptionA, descriptionB, matches, 2);
//        float ratio = (float) 0.75;
//
//        //find good matches
//        List<MatOfDMatch> goodMatches = new ArrayList<>();
//        for (MatOfDMatch match : matches) {
//            List<DMatch> dMatches = match.toList();
//            if (dMatches.size() == 2 && (dMatches.get(0).distance < dMatches.get(1).distance * ratio)) {
//                goodMatches.add(match);
//            }
//        }

//        //decide if this is a real match
//        if (goodMatches.size() > 2) {
//            Log.d(String.valueOf(R.string.image_tracker_tag), "Matched");
//            //return true;
//        } else {
//            Log.d(String.valueOf(R.string.image_tracker_tag), "Bad Match");
//        }



        List<MatOfDMatch> matches = new ArrayList<>();
        DescriptorMatcher matcher=DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
        matcher.radiusMatch(descriptionA, descriptionB, matches, 200f);


//        Mat dst=new Mat();
//        Features2d.drawMatches2(srcA, keyPointA, srcB, keyPointB, matches, dst);
//        Bitmap bitmap=Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_4444);
//        Utils.matToBitmap(dst, bitmap);
//        ivTest.setImageBitmap(bitmap);


        int total=Math.min(keyPointA.rows(), keyPointB.rows());
        int matchedNum=0;
        for(MatOfDMatch match : matches){
            if(match.rows()!=0) matchedNum++;
        }
        float ratio=matchedNum*1.0f/total;
        if(ratio>0.3f) {
            Log.d(String.valueOf(R.string.image_tracker_tag), "Matched");
        } else {
            Log.d("Ratio", Float.toString(matchedNum)+" | "+Float.toString(total) +" | "+Float.toString(ratio));
            Log.d(String.valueOf(R.string.image_tracker_tag), "Bad Match");
        }

        return srcB;
    }
}
