package com.example.carappv3;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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
    public static final int CANVAS_COLOR = R.color.colorWhite;
    public static final int TOUCH_TOLERANCE = 4;
    public static final int STROKE_WIDTH = 1;

    private float x0;
    private float y0;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<DrawingPath> paths = new  ArrayList<>();
    private ArrayList<DrawingPath> redo = new  ArrayList<>();

    public DrawingView(Context context) {
        super(context);
        init(null, 0);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DrawingView, defStyle, 0);

        x0 = 0;
        y0 = 0;
        mCanvas = new Canvas();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(DRAWING_COLOR));
        mPaint.setStrokeWidth(STROKE_WIDTH);
        mPaint.setMaskFilter(null);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void initialize(DisplayMetrics displayMetrics) {
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        mCanvas.drawColor(getResources().getColor(CANVAS_COLOR));
        for(DrawingPath drawingPath : paths) {
            mPaint.setColor(getResources().getColor(DRAWING_COLOR));
            mCanvas.drawPath(drawingPath.path, mPaint);
        }

        //TODO: update json file

        canvas.restore();
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        DrawingPath drawingPath = new DrawingPath(mPath);
        paths.add(drawingPath);

        mPath.reset();
        mPath.moveTo(x,y);

        x0 = x;
        y0 = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - x0);
        float dy = Math.abs(y - y0);

        if((dx >= TOUCH_TOLERANCE) && (dy >= TOUCH_TOLERANCE)) {
            mPath.quadTo(x0, y0, (x0 + x) / 2, (y0 + y) / 2);
        }

        x0 = x;
        y0 = y;
    }

    private void touchFinish() {
        mPath.lineTo(x0, y0);
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

    private void undo() {
        if (paths.size() > 0) {
            redo.add(paths.remove(paths.size() - 1));
            invalidate();
        } else {
            Toast.makeText(getContext(), "nothing to undo", Toast.LENGTH_SHORT).show();
        }
    }

    private void redo() {
        if (redo.size() > 0) {
            paths.add(redo.remove(redo.size() - 1));
            invalidate();
        } else {
            Toast.makeText(getContext(), "nothing to redo", Toast.LENGTH_SHORT).show();
        }
    }


}
