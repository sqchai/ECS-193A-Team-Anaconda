package com.example.carappv3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
        items.add("design1");
        items.add("design2");
        items.add("design3");
        items.add("design4");
        items.add("design5");
        items.add("design6");
        items.add("design7");
        items.add("design8");
        items.add("design9");
        items.add("design10");
        items.add("design11");
        items.add("design12");
        items.add("design13");
        items.add("design14");
        items.add("design15");
        items.add("design16");
        items.add("design17");
        items.add("design18");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drawing_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(getContext(), items);
        recyclerView.setAdapter(recyclerAdapter);

        return view;
    }
}
