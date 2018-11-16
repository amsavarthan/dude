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

public class Cube extends Fragment {

    Button find_a,find_v;
    EditText output_area,output_volume;
    EditText area_edge,volume_edge;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_cube, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        find_a=view.findViewById(R.id.find_a);
        find_v=view.findViewById(R.id.find_v);

        output_area=view.findViewById(R.id.output_area);
        output_volume=view.findViewById(R.id.output_volume);

        area_edge=view.findViewById(R.id.input_area_edge);
        volume_edge=view.findViewById(R.id.input_volume_edge);

        find_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findVolume(volume_edge.getText().toString(),volume_edge);
            }
        });

        find_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findArea(area_edge.getText().toString(),area_edge);
            }
        });

    }

    private void findVolume(String s, EditText volume_edge) {

        if(!TextUtils.isEmpty(s)){
            try {
                output_volume.setText(String.valueOf(FormulaUtils.cubeVolume(Double.parseDouble(s))));
            } catch (FormulaUtils.InvalidInputException e) {
                e.printStackTrace();
            }
        }else{
            volume_edge.setError("Invalid input");
        }

    }

    private void findArea(String s, EditText area_edge) {

        if(!TextUtils.isEmpty(s)){
            try {
                output_area.setText(String.valueOf(FormulaUtils.cubeSurface(Double.parseDouble(s))));
            } catch (FormulaUtils.InvalidInputException e) {
                e.printStackTrace();
            }
        }else{
            area_edge.setError("Invalid input");
        }

    }


}
