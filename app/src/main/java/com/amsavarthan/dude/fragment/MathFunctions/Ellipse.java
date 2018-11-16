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

public class Ellipse extends Fragment {

    EditText axisa,axisb,output;
    Button find_a;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_ellipse, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        find_a=view.findViewById(R.id.find_a);
        axisa=view.findViewById(R.id.input_area_aaxis);
        axisb=view.findViewById(R.id.input_area_baxis);
        output=view.findViewById(R.id.output_area);

        find_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findEllipse(axisa.getText().toString(),axisb.getText().toString(),axisa,axisb);
            }
        });

    }

    private void findEllipse(String s, String s1, EditText axisa, EditText axisb) {

        if(TextUtils.isEmpty(s) ){
            axisa.setError("Invalid Input");
            return;
        }

        if(TextUtils.isEmpty(s1) ){
            axisb.setError("Invalid Input");
            return;
        }

        try {
            output.setText(String.valueOf(FormulaUtils.ellipseArea(Double.parseDouble(s),Double.parseDouble(s1))));
        } catch (FormulaUtils.InvalidInputException e) {
            e.printStackTrace();
        }

    }


}
