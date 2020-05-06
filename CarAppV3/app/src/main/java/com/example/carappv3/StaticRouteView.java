package com.example.carappv3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class StaticRouteView extends View {
    public StaticRouteView(Context context) {
        super(context);
        //init(null, 0, context);
    }

    public StaticRouteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init(attrs, 0, context);
    }

    public StaticRouteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //init(attrs, defStyle, context);
    }

    public void initialize(DisplayMetrics displayMetrics){
        BitmapDrawable ob = new BitmapDrawable(getResources(), Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888));
        setBackground(ob);
    }

    public void initialize(DisplayMetrics displayMetrics, Bitmap bitmap){
        BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
        setBackground(ob);
    }
}
