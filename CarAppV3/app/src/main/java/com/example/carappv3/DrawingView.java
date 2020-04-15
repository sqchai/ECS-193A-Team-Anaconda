package com.example.carappv3;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

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

    private float currX;
    private float currY;
    private Canvas canvas;
    private Paint paint;
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

        currX = 0;
        currY = 0;
        canvas = new Canvas();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(DRAWING_COLOR));
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setMaskFilter(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.drawColor(getResources().getColor(CANVAS_COLOR));
        for(DrawingPath drawingPath : paths) {
            canvas.drawPath(drawingPath.path, paint);
        }
        //TODO: update json file
    }

}
