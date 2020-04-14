package com.example.carappv3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DrawingListFragment extends Fragment {

    public DrawingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ArrayList<String> items = new ArrayList<>();
        items.add("item1");
        items.add("item2");
        items.add("item3");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drawing_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_1);
        RecyclerAdapter


        recyclerView.setAdapter(recyclerViewAdapter);
        return view;
    }
}
