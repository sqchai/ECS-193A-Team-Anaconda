package com.example.carappv3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


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
        String[] items = {"Item 1",
                            "Item 2",
                            "Item 3"};

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drawing_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_1);
        ArrayAdapter<String> recyclerViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                items);


        recyclerView.setAdapter(recyclerViewAdapter);
        return view;
    }
}
