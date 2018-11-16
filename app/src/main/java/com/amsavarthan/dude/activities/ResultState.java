package com.amsavarthan.dude.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.adapter.SolutionAdapter;
import com.amsavarthan.dude.adapter.SolutionStateResultAdapter;
import com.amsavarthan.dude.api.WolframAlphaAPI;
import com.amsavarthan.dude.models.Solution;
import com.amsavarthan.dude.utils.RecentsDatabase;
import com.amsavarthan.dude.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ResultState extends AppCompatActivity {

    private static final int QUERY = 1;
    String title,name,input,query;
    private SearchTask searchTask;
    private RecyclerView recyclerView;
    CoordinatorLayout layout;
    private SolutionStateResultAdapter solutionStateResultAdapter;

    public static void startActivity(Context context, String title, String name, String input, String query){
        context.startActivity(new Intent(context,ResultState.class)
                .putExtra("title",title)
                .putExtra("name",name)
                .putExtra("input",input)
                .putExtra("query",query));
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

        setContentView(R.layout.activity_result_state);

        recyclerView=findViewById(R.id.recyclerView);
        layout=findViewById(R.id.layout);
        TextView title_text = findViewById(R.id.title);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        title=getIntent().getStringExtra("title");
        name=getIntent().getStringExtra("name");
        input=getIntent().getStringExtra("input");
        query=getIntent().getStringExtra("query");

        title_text.setText(title);

        initiateSearch(query,QUERY);

    }

    public void goBack(View view) {
        finish();
    }

    private class SearchTask extends AsyncTask<String, Void, ArrayList<Solution>> {

        Context context;
        ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            context=ResultState.this;

            mDialog=new ProgressDialog(context);
            mDialog.setMessage("Please wait....");
            mDialog.setIndeterminate(true);
            mDialog.setCancelable(true);
            mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    finish();
                }
            });
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();

        }

        @Override
        protected ArrayList<Solution> doInBackground(String... params) {

            if (!Utils.isNetworkAvailable(context))
                return null;

            int searchType = Integer.parseInt(params[1]);

            if (searchType == QUERY && !StringUtils.isEmpty(params[0])) {

                return WolframAlphaAPI.getStateQueryResult(params[0],title,input,name);

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
            } else
                showInformation(getString(R.string.error_unable_to_search), getString(R.string.dismiss));

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

    private void initiateSearch(String query, int searchType) {

        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }

        String[] queryParameter = {query, searchType + ""};
        searchTask = new SearchTask();
        searchTask.execute(queryParameter);
    }

    private void populateResult(ArrayList<Solution> solutions) {

        solutionStateResultAdapter = new SolutionStateResultAdapter(solutions);
        recyclerView.setAdapter(solutionStateResultAdapter);

    }

}
