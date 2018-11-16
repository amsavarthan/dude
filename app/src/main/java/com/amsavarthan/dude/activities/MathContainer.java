package com.amsavarthan.dude.activities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.fragment.MathFunctions.Circle;
import com.amsavarthan.dude.fragment.MathFunctions.Cone;
import com.amsavarthan.dude.fragment.MathFunctions.Cube;
import com.amsavarthan.dude.fragment.MathFunctions.Cylinder;
import com.amsavarthan.dude.fragment.MathFunctions.Ellipse;
import com.amsavarthan.dude.fragment.MathFunctions.Parallelogram;
import com.amsavarthan.dude.fragment.MathFunctions.Rectangle;
import com.amsavarthan.dude.fragment.MathFunctions.Square;
import com.amsavarthan.dude.fragment.MathFunctions.Triangle;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MathContainer extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/bold.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_holder);

        int id=getIntent().getIntExtra("id",0);

        switch (id){
            case 1:
                loadfragment(new Circle());
                return;
            case 2:
                loadfragment(new Cone());
                return;
            case 3:
                loadfragment(new Cube());
                return;
            case 4:
                loadfragment(new Cylinder());
                return;
            case 5:
                loadfragment(new Ellipse());
                return;
            case 6:
                loadfragment(new Rectangle());
                return;
            case 7:
                loadfragment(new Square());
                return;
            case 8:
                loadfragment(new Triangle());
                return;
            case 9:
                loadfragment(new Parallelogram());
                return;
                default:
                    finish();
        }

    }
    public void loadfragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
    }

}
