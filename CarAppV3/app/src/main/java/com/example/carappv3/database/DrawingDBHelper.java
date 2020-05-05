package com.example.carappv3.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DrawingDBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DBNAME = "drawings.db";
    public DrawingDBHelper(Context mcontext){
        super(mcontext, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DrawingSchema.DrawingTable.NAME+ "(" +
                " _id integer primary key autoincrement, " +
                DrawingSchema.DrawingTable.Cols.PATHS + ", " +
                DrawingSchema.DrawingTable.Cols.BITMAP +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion){

    }
}
