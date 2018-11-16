package com.amsavarthan.dude.fragment.MathFunctions;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.utils.FormulaUtils;

public class Parallelogram extends Fragment {

    Button find_a,find_p;
    EditText a_height,a_base,p_aside,p_base;
    EditText output_area,output_perimeter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_parallelogram, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        find_a=view.findViewById(R.id.find_a);
        find_p=view.findViewById(R.id.find_p);

        a_height=view.findViewById(R.id.input_area_height);
        a_base=view.findViewById(R.id.input_area_base);
        p_aside=view.findViewById(R.id.input_perimeter_aside);
        p_base=view.findViewById(R.id.input_perimeter_base);

        output_area=view.findViewById(R.id.output_area);
        output_perimeter=view.findViewById(R.id.output_perimeter);

        find_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findArea(a_height.getText().toString(),a_base.getText().toString(),a_height,a_base);
            }
        });

        find_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPerimeter(p_aside.getText().toString(),p_base.getText().toString(),p_aside,p_base);
            }
        });

    }

    private void findPerimeter(String s, String s1, EditText p_aside, EditText p_base) {

        if(TextUtils.isEmpty(s) ){
            p_aside.setError("Invalid Input");
            return;
        }

        if(TextUtils.isEmpty(s1) ){
            p_base.setError("Invalid Input");
            return;
        }

        try {
            output_perimeter.setText(String.valueOf(FormulaUtils.parallelogramPerimeter(Double.parseDouble(s),Double.parseDouble(s1))));
        } catch (FormulaUtils.InvalidInputException e) {
            e.printStackTrace();
        }

    }

    private void findArea(String s, String s1, EditText a_height, EditText a_base) {

        if(TextUtils.isEmpty(s) ){
            a_height.setError("Invalid Input");
            return;
        }

        if(TextUtils.isEmpty(s1) ){
            a_base.setError("Invalid Input");
            return;
        }

        try {
            output_area.setText(String.valueOf(FormulaUtils.parallelogramArea(Double.parseDouble(s),Double.parseDouble(s1))));
        } catch (FormulaUtils.InvalidInputException e) {
            e.printStackTrace();
        }

    }


}
