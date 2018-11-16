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

public class Square extends Fragment {

    EditText a_side,p_side,d_side;
    EditText output_area,output_perimeter,output_diagonal;
    Button find_a,find_p,find_d;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_square, container, false);
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

        a_side=view.findViewById(R.id.input_area_aside);
        p_side=view.findViewById(R.id.input_perimeter_aside);
        d_side=view.findViewById(R.id.input_diagonal_aside);

        find_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findArea(a_side.getText().toString(),a_side);
            }
        });

        find_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPerimeter(p_side.getText().toString(),p_side);
            }
        });

        find_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findDiagonal(d_side.getText().toString(),d_side);
            }
        });

    }

    private void findDiagonal(String s, EditText d_side) {

        if(!TextUtils.isEmpty(s)){
            try {
                output_diagonal.setText(String.valueOf(FormulaUtils.squareDiagonal(Double.parseDouble(s))));
            } catch (FormulaUtils.InvalidInputException e1) {
                e1.printStackTrace();
            }
        }else{
            d_side.setError("Invalid input");
        }

    }

    private void findPerimeter(String s, EditText p_side) {

        if(!TextUtils.isEmpty(s)){
            try {
                output_perimeter.setText(String.valueOf(FormulaUtils.squarePerimeter(Double.parseDouble(s))));
            } catch (FormulaUtils.InvalidInputException e1) {
                e1.printStackTrace();
            }
        }else{
            p_side.setError("Invalid input");
        }


    }

    private void findArea(String s, EditText a_side) {

        if(!TextUtils.isEmpty(s)){
            try {
                output_area.setText(String.valueOf(FormulaUtils.squareArea(Double.parseDouble(s))));
            } catch (FormulaUtils.InvalidInputException e1) {
                e1.printStackTrace();
            }
        }else{
            a_side.setError("Invalid input");
        }


    }


}
