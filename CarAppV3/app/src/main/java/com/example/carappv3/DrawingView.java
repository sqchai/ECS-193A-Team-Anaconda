package com.example.carappv3;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.carappv3.database.DrawingDBHelper;
import com.example.carappv3.database.DrawingSchema;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Here we creates a canvas for the user
 * to input the desired drawing path.
 * The path is shown on the screen like
 * in an drawing app.
 * The path is recorded in a json file.
 */
public class DrawingView extends View {
    public static final int DRAWING_COLOR = R.color.colorRoseRed;
    public static final int LINE_COLOR = R.color.colorPineAppleYellow;
    public static final int VERTEX_COLOR = R.color.colorPineArcticBlue;
    public static final int CANVAS_COLOR = R.color.colorWhite;

    public static final int TOUCH_TOLERANCE = 4;
    public static final int LINE_PATH_TOLERANCE = 20;

    public static final int DRAWING_STROKE_WIDTH = 15;
    public static final int LINE_STROKE_WIDTH = 5;
    public static final int VERTEX_STROKE_WIDTH = 20;

    private float x0;
    private float y0;

    //bit map for custom canvas to write into
    private Bitmap mBitmap;
    private Canvas mCanvas;

    private Path mPath;
    private Path mLinePath;
    private ArrayList<Point> mVertices;

    private Paint mPaint;

    //list to record all drawing paths
    private ArrayList<DrawingPath> paths = new  ArrayList<>();
    private ArrayList<DrawingPath> redo = new  ArrayList<>();

    //DB objects
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public DrawingView(Context context) {
        super(context);
        init(null, 0, context);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, context);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle, context);
    }

    private void init(AttributeSet attrs, int defStyle, Context context) {
        Log.d("in init","!!!!!!");
        x0 = 0;
        y0 = 0;

        mCanvas = new Canvas();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(DRAWING_COLOR));
        mPaint.setStrokeWidth(DRAWING_STROKE_WIDTH);
        mPaint.setMaskFilter(null);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //getting database for saving
        mContext = context.getApplicationContext();
        mDatabase = new DrawingDBHelper(mContext).getWritableDatabase();
    }

    public void initialize(DisplayMetrics displayMetrics) {
        Log.d("in initialize","1 argument one     !!!!!!");
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        //a bit map for the custom canvas to draw into
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    public void initialize(DisplayMetrics displayMetrics,Bitmap bitmap) {
        Log.d("in initialize","the two argument one  22222   !!!!!!");
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        //a bit map for the custom canvas to draw into
        mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("in onDraw","for all paths and vertices");
        canvas.save();
        mCanvas.drawColor(getResources().getColor(CANVAS_COLOR));
        for(DrawingPath drawingPath : paths) {
            mPaint.setColor(getResources().getColor(DRAWING_COLOR));
            mPaint.setStrokeWidth(DRAWING_STROKE_WIDTH);
            mCanvas.drawPath(drawingPath.path, mPaint);

            mPaint.setColor(getResources().getColor(LINE_COLOR));
            mPaint.setStrokeWidth(LINE_STROKE_WIDTH);
            mCanvas.drawPath(drawingPath.linePath, mPaint);

            mPaint.setColor(getResources().getColor(VERTEX_COLOR));
            mPaint.setStrokeWidth(VERTEX_STROKE_WIDTH);
            for(Point point : drawingPath.vertices) {
                mCanvas.drawPoint(point.x, point.y, mPaint);
            }
        }

        //TODO: update json file
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        canvas.restore();
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        mLinePath = new Path();
        mVertices = new ArrayList<>();
        DrawingPath drawingPath = new DrawingPath(mPath, mLinePath, mVertices);
        paths.add(drawingPath);

        mPath.reset();
        mPath.moveTo(x,y);

        mLinePath.reset();
        mLinePath.moveTo(x,y);

        mVertices.add(new Point((int)x, (int)y));

        x0 = x;
        y0 = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - x0);
        float dy = Math.abs(y - y0);

        if((dx >= TOUCH_TOLERANCE) && (dy >= TOUCH_TOLERANCE)) {
            mPath.quadTo(x0, y0, (x0 + x) / 2, (y0 + y) / 2);
        }

        if((dx >= LINE_PATH_TOLERANCE) || (dy >= LINE_PATH_TOLERANCE)) {
            mLinePath.lineTo((x0 + x) / 2, (y0 + y) / 2);
            mVertices.add(new Point((int)((x0 + x) / 2), (int)((y0 + y) / 2)));
        }

        x0 = x;
        y0 = y;
    }

    private void touchFinish() {
        mPath.lineTo(x0, y0);
        mLinePath.lineTo(x0, y0);
        mVertices.add(new Point((int)x0, (int)y0));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchFinish();
                invalidate();
                break;
        }

        return true;
    }

    public void undo() {
        if (paths.size() > 0) {
            redo.add(paths.remove(paths.size() - 1));
            invalidate();
        } else {
            Toast.makeText(getContext(), "nothing to undo", Toast.LENGTH_SHORT).show();
        }
    }


    public void redo() {
        if (redo.size() > 0) {
            paths.add(redo.remove(redo.size() - 1));
            invalidate();
        } else {
            Toast.makeText(getContext(), "nothing to redo", Toast.LENGTH_SHORT).show();
        }
    }

    public void save(){
        Gson gson = new Gson();
        ArrayList<String> stringifiedPaths = new  ArrayList<>();
        for(DrawingPath drawingPath : paths) {
            String jsonPath = gson.toJson(drawingPath.vertices);
            stringifiedPaths.add(jsonPath);
        }
        String mPaths = gson.toJson(stringifiedPaths);
        //System.out.println(mPaths);
        String jsonMap = getStringFromBitmap(mBitmap);
        //System.out.println(jsonMap);
        ContentValues values = getContentValues(mPaths,jsonMap);
        mDatabase.insert(DrawingSchema.DrawingTable.NAME, null, values);
        mDatabase.close();
    }

    private static String getStringFromBitmap(Bitmap bitmapPicture) {
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    private static ContentValues getContentValues(String paths, String mapstr){
        ContentValues values = new ContentValues();
        values.put(DrawingSchema.DrawingTable.Cols.PATHS, paths);
        values.put(DrawingSchema.DrawingTable.Cols.BITMAP, mapstr);
        return values;
    }

}
