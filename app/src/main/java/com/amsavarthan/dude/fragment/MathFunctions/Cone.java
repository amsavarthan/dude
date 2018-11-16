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

public class Cone extends Fragment {

    EditText sh_radius,sh_height,V_radius,V_height,sa_radius,sa_slantheight;
    EditText output_sheight,output_volume,output_sarea;
    Button find_sh,find_v,find_sa;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_cone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sh_radius=view.findViewById(R.id.input_sheight_radius);
        sh_height=view.findViewById(R.id.input_sheight_height);

        V_radius=view.findViewById(R.id.input_volume_radius);
        V_height=view.findViewById(R.id.input_volume_height);

        sa_radius=view.findViewById(R.id.input_sarea_radius);
        sa_slantheight=view.findViewById(R.id.input_sarea_sheight);

        output_sheight=view.findViewById(R.id.output_sheight);
        output_volume=view.findViewById(R.id.output_volume);
        output_sarea=view.findViewById(R.id.output_sarea);

        find_sh=view.findViewById(R.id.find_sh);
        find_v=view.findViewById(R.id.find_v);
        find_sa=view.findViewById(R.id.find_sa);


        find_sh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findSlantHeight(sh_radius.getText().toString(),sh_height.getText().toString(),sh_radius,sh_height);
            }
        });

        find_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findVolume(V_radius.getText().toString(),V_height.getText().toString(),V_radius,V_height);
            }
        });

        find_sa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findSurfaceArea(sa_radius.getText().toString(),sa_slantheight.getText().toString(),sa_radius,sa_slantheight);
            }
        });

    }

    private void findSurfaceArea(String s, String s1, EditText sa_radius, EditText sa_slantheight) {

        if(TextUtils.isEmpty(s) ){
            sa_radius.setError("Invalid Input");
            return;
        }

        if(TextUtils.isEmpty(s1) ){
            sa_slantheight.setError("Invalid Input");
            return;
        }

        try {
            output_sarea.setText(String.valueOf(FormulaUtils.coneSurface(Double.parseDouble(s),Double.parseDouble(s1))));
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
            output_volume.setText(String.valueOf(FormulaUtils.coneVolume(Double.parseDouble(s),Double.parseDouble(s1))));
        } catch (FormulaUtils.InvalidInputException e) {
            e.printStackTrace();
        }

    }

    private void findSlantHeight(String s, String s1, EditText sh_radius, EditText sh_height) {

        if(TextUtils.isEmpty(s) ){
            sh_radius.setError("Invalid Input");
            return;
        }

        if(TextUtils.isEmpty(s1) ){
            sh_height.setError("Invalid Input");
            return;
        }

        try {
            output_sheight.setText(String.valueOf(FormulaUtils.coneSlantHeight(Double.parseDouble(s),Double.parseDouble(s1))));
        } catch (FormulaUtils.InvalidInputException e) {
            e.printStackTrace();
        }

    }


}
