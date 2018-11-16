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

public class Triangle extends Fragment {

    EditText a_height,a_base,p_aside,p_base,p_cside;
    EditText output_area,output_perimeter;
    Button find_a,find_p;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_triangle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        find_a=view.findViewById(R.id.find_a);
        find_p=view.findViewById(R.id.find_p);

        output_area=view.findViewById(R.id.output_area);
        output_perimeter=view.findViewById(R.id.output_perimeter);

        a_height=view.findViewById(R.id.input_area_height);
        a_base=view.findViewById(R.id.input_area_base);
        p_aside=view.findViewById(R.id.input_perimeter_aside);
        p_base=view.findViewById(R.id.input_perimeter_base);
        p_cside=view.findViewById(R.id.input_perimeter_cside);


        find_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findArea(a_base.getText().toString(),a_height.getText().toString(),a_base,a_height);
            }
        });

        find_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPerimeter(p_aside.getText().toString(),p_base.getText().toString(),p_cside.getText().toString(),p_aside,p_base,p_cside);
            }
        });

    }

    private void findPerimeter(String s, String s1, String s2, EditText p_aside, EditText p_base, EditText p_cside) {

        if(TextUtils.isEmpty(s) ){
            p_aside.setError("Invalid Input");
            return;
        }

        if(TextUtils.isEmpty(s1) ){
            p_base.setError("Invalid Input");
            return;
        }

        if(TextUtils.isEmpty(s2) ){
            p_cside.setError("Invalid Input");
            return;
        }

        try {
            output_perimeter.setText(String.valueOf(FormulaUtils.trianglePerimeter(Double.parseDouble(s1),Double.parseDouble(s),Double.parseDouble(s2))));
        } catch (FormulaUtils.InvalidInputException e) {
            e.printStackTrace();
        }

    }

    private void findArea(String s, String s1, EditText a_base, EditText a_height) {

        if(TextUtils.isEmpty(s) ){
            a_base.setError("Invalid Input");
            return;
        }

        if(TextUtils.isEmpty(s1) ){
            a_height.setError("Invalid Input");
            return;
        }

        try {
            output_area.setText(String.valueOf(FormulaUtils.triangleArea(Double.parseDouble(s),Double.parseDouble(s1))));
        } catch (FormulaUtils.InvalidInputException e) {
            e.printStackTrace();
        }

    }


}
