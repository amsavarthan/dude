package com.amsavarthan.dude.activities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.fragment.AllQuestions;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AllQuestionsActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_all_questions);

        loadfragment(new AllQuestions());

    }

    public void loadfragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
    }

}
