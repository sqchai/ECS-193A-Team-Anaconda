package com.example.carappv3;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FileViewAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context context;
    //private LayoutInflater layoutInflater;
    private ArrayList<String> files = new ArrayList<>();
    private ArrayList<String> vertices = new ArrayList<>();
    private ArrayList<String> bitmap = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler, parent, false);
        RecyclerAdapter.ViewHolder holder = new RecyclerAdapter.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, final int position) {
        final String item_name = files.get(position);
        holder.textView.setText(item_name);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StaticPicActivity.class);
                intent.putExtra("vertices", vertices.get(position));
                intent.putExtra("bitmap", bitmap.get(position));
                intent.putExtra("filename", files.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public FileViewAdapter(Context context, ArrayList<String> files, ArrayList<String> vertices,ArrayList<String> bitmap) {
        this.context = context;
        //this.layoutInflater = LayoutInflater.from(context);
        this.files = files;
        this.vertices = vertices;
        this.bitmap = bitmap;
    }
}
