package com.example.carappv3;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carappv3.database.DrawingDBHelper;
import com.example.carappv3.database.DrawingSchema;

import java.util.ArrayList;

public class SavedFilesFragment extends Fragment {
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private View mview;
    public boolean allowRefresh;

    public SavedFilesFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        allowRefresh=true;
        mContext = getContext();
        mDatabase = new DrawingDBHelper(mContext).getWritableDatabase();
        ArrayList<String> files = new ArrayList<>();
        ArrayList<String> vertices = new ArrayList<>();
        ArrayList<String> bitmap = new ArrayList<>();
        try {
            Cursor cursor = mDatabase.rawQuery("SELECT  * FROM " + DrawingSchema.DrawingTable.NAME, null);
            try {
                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        Log.d("getting file id ", cursor.getString(0));
                        files.add(cursor.getString(0));
                        vertices.add(cursor.getString(1));
                        bitmap.add(cursor.getString(2));
                    } while (cursor.moveToNext());
                }
            } finally {
                try { cursor.close(); } catch (Exception ignore) {}
            }
        } finally {
            try { mDatabase.close(); } catch (Exception ignore) {}
        }

        mview = inflater.inflate(R.layout.fragment_drawing_list, container, false);
        RecyclerView recyclerView = (RecyclerView) mview.findViewById(R.id.recycle_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        FileViewAdapter fileViewAdapterAdapter = new FileViewAdapter(getContext(), files, vertices, bitmap);
        recyclerView.setAdapter(fileViewAdapterAdapter);
        allowRefresh=false;
        return mview;
    }

    @Override
    public void onResume() {
        Log.d("out of allow refresh", "in on resume");
        super.onResume();
        if(allowRefresh){
            Log.d("in allow refresh", "in on resume");
            allowRefresh=false;
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Override
    public void onPause() {
        Log.d("!", "in on Pause");
        super.onPause();
        allowRefresh=true;
    }
}
