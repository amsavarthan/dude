package com.amsavarthan.dude.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.adapter.MathAdapter;
import com.amsavarthan.dude.models.Functions;

import java.util.ArrayList;

public class Math extends Fragment {

    private MathAdapter mathAdapter;
    private ArrayList<Functions> functions=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_math, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mathAdapter=new MathAdapter(functions);

        RecyclerView recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(mathAdapter);

        addItems();
        mathAdapter.notifyDataSetChanged();

    }

    private void addItems() {
        Functions function;

        function=new Functions("Circle","Area, Circumference, Diameter, Radius",1);
        functions.add(function);

        function=new Functions("Cone","Slant Height, Volume, Surface Area",2);
        functions.add(function);

        function=new Functions("Cube","Area, Volume",3);
        functions.add(function);

        function=new Functions("Cylinder","Radius, Height, Volume, Surface Area",4);
        functions.add(function);

        function=new Functions("Ellipse","Area",5);
        functions.add(function);

        function=new Functions("Rectangle","Area, Perimeter, Diagonal",6);
        functions.add(function);

        function=new Functions("Square","Area, Perimeter, Diagonal",7);
        functions.add(function);

        function=new Functions("Triangle","Area, Perimeter",8);
        functions.add(function);

        function=new Functions("Parallelogram","Area, Perimeter",9);
        functions.add(function);

    }

}
