package com.amsavarthan.dude.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.activities.ResultActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class Speech extends Fragment {

    private ImageView btnSpeak;
    private EditText text;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ImageButton search;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_speech, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSpeak =  view.findViewById(R.id.btnSpeak);
        text =  view.findViewById(R.id.edittext);
        search=view.findViewById(R.id.search);

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String query=text.getText().toString();

                if(StringUtils.isNotEmpty(query)){

                    getActivity().finish();
                    ResultActivity.startActivity(getContext().getApplicationContext(),query,"text");

                }else{

                    Toast.makeText(getContext().getApplicationContext(), "Search query empty", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
               "Tell a question");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext().getApplicationContext(),
                    "Speech function not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    text.setText(result.get(0));
                }
                break;
            }

        }
    }
}
