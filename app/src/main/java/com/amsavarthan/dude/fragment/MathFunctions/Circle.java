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

public class Circle extends Fragment {

    Button find_a,find_c,find_d,find_r;
    EditText area_radius,circum_radius,dia_radius,radius_dia;
    EditText output_area,output_circum,output_diameter,output_radius;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_circle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        find_a=view.findViewById(R.id.find_a);
        find_c=view.findViewById(R.id.find_c);
        find_d=view.findViewById(R.id.find_d);
        find_r=view.findViewById(R.id.find_r);

        area_radius=view.findViewById(R.id.input_area_radius);
        circum_radius=view.findViewById(R.id.input_circum_radius);
        dia_radius=view.findViewById(R.id.input_dia_radius);
        radius_dia=view.findViewById(R.id.input_radius_dia);

        output_area=view.findViewById(R.id.output_area);
        output_circum=view.findViewById(R.id.output_perimeter);
        output_diameter=view.findViewById(R.id.output_diameter);
        output_radius=view.findViewById(R.id.output_radius);

        find_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findArea(area_radius.getText().toString(),area_radius);
            }
        });

        find_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findCircumference(circum_radius.getText().toString(),circum_radius);
            }
        });

        find_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findDiameter(dia_radius.getText().toString(),dia_radius);
            }
        });

        find_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findRadius(radius_dia.getText().toString(),radius_dia);
            }
        });

    }

    private void findArea(String s,EditText e) {
        if(!TextUtils.isEmpty(s)){
            try {
                output_area.setText(String.valueOf(FormulaUtils.circleArea(Double.parseDouble(s))));
            } catch (FormulaUtils.InvalidInputException e1) {
                e1.printStackTrace();
            }
        }else{
            e.setError("Invalid input");
        }
    }

    private void findCircumference(String s,EditText e) {
        if(!TextUtils.isEmpty(s)){
            try {
                output_circum.setText(String.valueOf(FormulaUtils.circlePerimeter(Double.parseDouble(s))));
            } catch (FormulaUtils.InvalidInputException e1) {
                e1.printStackTrace();
            }
        }else{
            e.setError("Invalid input");
        }
    }

    private void findDiameter(String s,EditText e) {
        if(!TextUtils.isEmpty(s)){
            try {
                output_diameter.setText(String.valueOf(FormulaUtils.circleDiameter(Double.parseDouble(s))));
            } catch (FormulaUtils.InvalidInputException e1) {
                e1.printStackTrace();
            }
        }else{
            e.setError("Invalid input");
        }
    }

    private void findRadius(String s,EditText e) {
        if(!TextUtils.isEmpty(s)){
            try {
                output_radius.setText(String.valueOf(FormulaUtils.circleRadius(Double.parseDouble(s))));
            } catch (FormulaUtils.InvalidInputException e1) {
                e1.printStackTrace();
            }
        }else{
            e.setError("Invalid input");
        }
    }


}
