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

public class Cylinder extends Fragment {

    EditText r_height,r_volume,h_radius,h_volume,v_radius,v_height,sa_radius,sa_sheight;
    EditText output_radius,output_height,output_volume,output_sarea;
    Button find_r,find_h,find_v,find_sa;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_cylinder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        r_height=view.findViewById(R.id.input_radius_height);
        r_volume=view.findViewById(R.id.input_radius_volume);
        h_radius=view.findViewById(R.id.input_height_radius);
        h_volume=view.findViewById(R.id.input_height_volume);
        v_radius=view.findViewById(R.id.input_volume_radius);
        v_height=view.findViewById(R.id.input_volume_height);
        sa_radius=view.findViewById(R.id.input_sarea_radius);
        sa_sheight=view.findViewById(R.id.input_sarea_sheight);

        output_radius=view.findViewById(R.id.output_radius);
        output_height=view.findViewById(R.id.output_height);
        output_volume=view.findViewById(R.id.output_volume);
        output_sarea=view.findViewById(R.id.output_sarea);

        find_r=view.findViewById(R.id.find_r);
        find_h=view.findViewById(R.id.find_h);
        find_v=view.findViewById(R.id.find_v);
        find_sa=view.findViewById(R.id.find_sa);

        find_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findRadius(r_height.getText().toString(),r_volume.getText().toString(),r_height,r_volume);
            }
        });

        find_h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findHeight(h_radius.getText().toString(),h_volume.getText().toString(),h_radius,h_volume);
            }
        });

        find_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findVolume(v_radius.getText().toString(),v_height.getText().toString(),v_radius,v_height);
            }
        });

        find_sa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findSurfaceArea(sa_radius.getText().toString(),sa_sheight.getText().toString(),sa_radius,sa_sheight);
            }
        });

    }

    private void findSurfaceArea(String s, String s1, EditText sa_radius, EditText sa_sheight) {

        if(TextUtils.isEmpty(s) ){
            sa_radius.setError("Invalid Input");
            return;
        }

        if(TextUtils.isEmpty(s1) ){
            sa_sheight.setError("Invalid Input");
            return;
        }

        try {
            output_sarea.setText(String.valueOf(FormulaUtils.cylinderSurface(Double.parseDouble(s),Double.parseDouble(s1))));
        } catch (FormulaUtils.InvalidInputException e) {
            e.printStackTrace();
        }

    }

    private void findVolume(String s, String s1, EditText v_radius, EditText v_height) {

        if(TextUtils.isEmpty(s) ){
            v_radius.setError("Invalid Input");
            return;
        }

        if(TextUtils.isEmpty(s1) ){
            v_height.setError("Invalid Input");
            return;
        }

        try {
            output_volume.setText(String.valueOf(FormulaUtils.cylinderVolume(Double.parseDouble(s),Double.parseDouble(s1))));
        } catch (FormulaUtils.InvalidInputException e) {
            e.printStackTrace();
        }
    }

    private void findHeight(String s, String s1, EditText h_radius, EditText h_volume) {

        if(TextUtils.isEmpty(s) ){
            h_radius.setError("Invalid Input");
            return;
        }

        if(TextUtils.isEmpty(s1) ){
            h_volume.setError("Invalid Input");
            return;
        }

        try {
            output_height.setText(String.valueOf(FormulaUtils.cylinderHeight(Double.parseDouble(s),Double.parseDouble(s1))));
        } catch (FormulaUtils.InvalidInputException e) {
            e.printStackTrace();
        }

    }

    private void findRadius(String s, String s1, EditText r_height, EditText r_volume) {

        if(TextUtils.isEmpty(s) ){
            r_height.setError("Invalid Input");
            return;
        }

        if(TextUtils.isEmpty(s1) ){
            r_volume.setError("Invalid Input");
            return;
        }

        try {
            output_radius.setText(String.valueOf(FormulaUtils.cylinderRadius(Double.parseDouble(s),Double.parseDouble(s1))));
        } catch (FormulaUtils.InvalidInputException e) {
            e.printStackTrace();
        }

    }


}
