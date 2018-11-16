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

public class Rectangle extends Fragment {

    EditText a_length,a_width,p_length,p_width,d_length,d_width;
    EditText output_area,output_perimeter,output_diagonal;
    Button find_a,find_p,find_d;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_rectangle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        find_a=view.findViewById(R.id.find_a);
        find_p=view.findViewById(R.id.find_p);
        find_d=view.findViewById(R.id.find_d);

        output_area=view.findViewById(R.id.output_area);
        output_perimeter=view.findViewById(R.id.output_perimeter);
        output_diagonal=view.findViewById(R.id.output_diagonal);

        a_length=view.findViewById(R.id.input_area_length);
        a_width=view.findViewById(R.id.input_area_width);
        p_length=view.findViewById(R.id.input_perimeter_length);
        p_width=view.findViewById(R.id.input_perimeter_width);
        d_length=view.findViewById(R.id.input_diagonal_length);
        d_width=view.findViewById(R.id.input_diagonal_width);

        find_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findArea(a_length.getText().toString(),a_width.getText().toString(),a_length,a_width);
            }
        });

        find_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPerimeter(p_length.getText().toString(),p_width.getText().toString(),p_length,p_width);
            }
        });

        find_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findDiagonal(d_length.getText().toString(),d_width.getText().toString(),d_length,d_width);
            }
        });

    }

    private void findDiagonal(String s, String s1, EditText d_length, EditText d_width) {

        if(TextUtils.isEmpty(s) ){
            d_length.setError("Invalid Input");
            return;
        }

        if(TextUtils.isEmpty(s1) ){
            d_width.setError("Invalid Input");
            return;
        }

        try {
            output_diagonal.setText(String.valueOf(FormulaUtils.rectangleDiagonal(Double.parseDouble(s1),Double.parseDouble(s))));
        } catch (FormulaUtils.InvalidInputException e) {
            e.printStackTrace();
        }

    }

    private void findPerimeter(String s, String s1, EditText d_length, EditText d_width) {

        if(TextUtils.isEmpty(s) ){
            d_length.setError("Invalid Input");
            return;
        }

        if(TextUtils.isEmpty(s1) ){
            d_width.setError("Invalid Input");
            return;
        }

        try {
            output_perimeter.setText(String.valueOf(FormulaUtils.rectanglePerimeter(Double.parseDouble(s1),Double.parseDouble(s))));
        } catch (FormulaUtils.InvalidInputException e) {
            e.printStackTrace();
        }

    }

    private void findArea(String s, String s1, EditText a_length, EditText a_width) {
        if(TextUtils.isEmpty(s) ){
            a_length.setError("Invalid Input");
            return;
        }

        if(TextUtils.isEmpty(s1) ){
            a_width.setError("Invalid Input");
            return;
        }

        try {
            output_area.setText(String.valueOf(FormulaUtils.rectangleArea(Double.parseDouble(s1),Double.parseDouble(s))));
        } catch (FormulaUtils.InvalidInputException e) {
            e.printStackTrace();
        }
    }


}
