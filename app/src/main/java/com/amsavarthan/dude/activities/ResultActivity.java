package com.amsavarthan.dude.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.dude.R;
import com.amsavarthan.dude.utils.RecentsDatabase;
import com.amsavarthan.dude.adapter.SolutionAdapter;
import com.amsavarthan.dude.utils.Utils;
import com.amsavarthan.dude.api.WolframAlphaAPI;
import com.amsavarthan.dude.models.Solution;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class ResultActivity extends AppCompatActivity {

    private String output,type;
    private String TAG=ResultActivity.class.getSimpleName();

    private SearchTask searchTask;
    private SolutionAdapter solutionAdapter;
    private RecyclerView recyclerView;
    private CoordinatorLayout layout;
    private TextToSpeech textToSpeech;

    private final int QUERY = 1;
    private RecentsDatabase recentsDatabase;
    private String search_input;


    public static void startActivity(Context context,String output,String type){
        context.startActivity(new Intent(context,ResultActivity.class).putExtra("output",output).putExtra("type",type));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/bold.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        setContentView(R.layout.activity_result);

        recentsDatabase=new RecentsDatabase(this);

        recyclerView=findViewById(R.id.recyclerView);
        layout=findViewById(R.id.layout);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        output = getIntent().getStringExtra("output");
        type= getIntent().getStringExtra("type");

        if(type.equals("image")) {

            if (StringUtils.isNotEmpty(output)) {

                new MaterialStyledDialog.Builder(this)
                        .setTitle("I got it!")
                        .setDescription("This is the text i read: '" + output + "'")
                        .withDialogAnimation(true)
                        .withDarkerOverlay(true)
                        .setCancelable(false)
                        .autoDismiss(false)
                        .setHeaderColorInt(Color.parseColor("#4CAF50"))
                        .setStyle(Style.HEADER_WITH_ICON)
                        .setIcon(R.drawable.success)
                        .setPositiveText("Show me the answer")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                dialog.dismiss();
                                recentsDatabase.insertQuery(output);
                                initiateSearch(output, QUERY);

                            }
                        })
                        .setNegativeText("Edit")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();

                                new MaterialDialog.Builder(ResultActivity.this)
                                        .title("Edit Output")
                                        .content("I think i have done some mistake, You can correct it by yourself")
                                        .inputType(InputType.TYPE_CLASS_TEXT)
                                        .input("Output", output, new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                                if (TextUtils.isEmpty(input)) {
                                                    Toast.makeText(ResultActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                                                } else {

                                                    output = input.toString();
                                                    dialog.dismiss();
                                                    recentsDatabase.insertQuery(output);
                                                    initiateSearch(output, QUERY);
                                                }
                                            }
                                        })
                                        .canceledOnTouchOutside(false)
                                        .cancelable(false)
                                        .positiveText("Search")
                                        .show();

                            }
                        })
                        .setNeutralText("Read again")
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                finish();
                                MainActivity.startActivity(ResultActivity.this, "camera");
                            }
                        }).show();

            } else {

                new MaterialStyledDialog.Builder(this)
                        .setTitle("No text found!")
                        .setDescription("I couldn't read that. Try retaking the photo")
                        .withDialogAnimation(true)
                        .withDarkerOverlay(true)
                        .setCancelable(false)
                        .autoDismiss(false)
                        .setHeaderColorInt(Color.parseColor("#f44336"))
                        .setStyle(Style.HEADER_WITH_ICON)
                        .setIcon(R.drawable.no_text)
                        .setPositiveText("Ok")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                finish();
                                MainActivity.startActivity(ResultActivity.this, "camera");
                            }
                        }).show();

            }
        }else{

            if(StringUtils.isNotEmpty(output)){

                recentsDatabase.insertQuery(output);
                initiateSearch(output, QUERY);

            }

        }

    }

    private class SearchTask extends AsyncTask<String, Void, ArrayList<Solution>> {

        Context context;
        ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            context=ResultActivity.this;

            mDialog=new ProgressDialog(context);
            mDialog.setTitle("Searching...");
            mDialog.setMessage("Searching in my brain for the answers, Please wait....");
            mDialog.setIndeterminate(true);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();

        }

        @Override
        protected ArrayList<Solution> doInBackground(String... params) {

            if (!Utils.isNetworkAvailable(context))
                return null;

            int searchType = Integer.parseInt(params[1]);

            if (searchType == QUERY && !StringUtils.isEmpty(params[0])) {

                return WolframAlphaAPI.getQueryResult(params[0]);

            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Solution> solutions) {
            super.onPostExecute(solutions);

            mDialog.dismiss();

            if (solutions != null && solutions.size() > 0) {
                populateResult(solutions);
            } else if (!Utils.isNetworkAvailable(context)) {
                showInformation(getString(R.string.error_network_not_available), getString(R.string.okay));
            } else {
                Utils.showMultiLineSnackBar(layout, "Sorry i don't know about that, But you can ask it to a other users in forum", "Ask", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do action if needed
                        startActivity(new Intent(ResultActivity.this,AddQuestion.class).putExtra("question",search_input));
                    }
                });
            }
        }
    }

    private void initiateSearch(String query, int searchType) {

        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }

        search_input=query;

        String[] queryParameter = {query, searchType + ""};
        searchTask = new SearchTask();
        searchTask.execute(queryParameter);
    }

    private void populateResult(ArrayList<Solution> solutions) {

        solutionAdapter = new SolutionAdapter(solutions);
        recyclerView.setAdapter(solutionAdapter);

        String mainResult = null;

        try {
            mainResult = solutions.get(1).getDescription();
        } catch (Exception e) {
            mainResult = solutions.get(0).getDescription();
        }

        if (StringUtils.isNotEmpty(mainResult)) {

            stopTextToSpeech();
            textToSpeech.speak(mainResult, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY).replace(" ","");
            initiateSearch(query, QUERY);
        }
    }

    private void showInformation(String messageContent, String actionMessage) {

        Utils.showMultiLineSnackBar(layout, messageContent, actionMessage, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do action if needed
            }
        });

    }

    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
    }

    private void stopTextToSpeech() {
        if (textToSpeech != null && textToSpeech.isSpeaking())
            textToSpeech.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        initTextToSpeech();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }
    }

}
