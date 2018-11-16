package com.amsavarthan.dude.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.dude.R;
import com.amsavarthan.dude.utils.RecentsDatabase;
import com.amsavarthan.dude.activities.ResultActivity;
import com.amsavarthan.dude.adapter.RecentsAdapter;
import com.amsavarthan.dude.models.Recents;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Input extends Fragment {

    private ImageButton search,help;
    private EditText editText;
    private RecyclerView recyclerView;
    private CoordinatorLayout layout;
    private RecentsAdapter recentsAdapter;
    private List<Recents> recentsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_input,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecentsDatabase recentsDatabase = new RecentsDatabase(getContext());

        search=view.findViewById(R.id.search);
        help=view.findViewById(R.id.help);

        editText=view.findViewById(R.id.edittext);
        recyclerView=view.findViewById(R.id.recyclerView);
        layout=view.findViewById(R.id.layout);

        recentsList=new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        for(String query: recentsDatabase.getAllQueries()){
            Recents recent=new Recents(query);
            recentsList.add(recent);
        }

        recentsAdapter=new RecentsAdapter(recentsList);
        recyclerView.setAdapter(recentsAdapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String query=editText.getText().toString();

                if(StringUtils.isNotEmpty(query)){

                    Recents recents=new Recents(query);
                    recentsList.add(recents);

                    getActivity().finish();
                    ResultActivity.startActivity(getContext(),query,"text");

                    recentsAdapter.notifyDataSetChanged();

                }else{

                    Snackbar.make(layout,"Search query is empty.",Snackbar.LENGTH_SHORT).show();

                }

            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(getContext())
                        .title("Help")
                        .content("For using complex math functions use (replace ? with number):" +
                                "\nTo power of: (?)^?" +
                                "\nLogarithme Naturel: ln(?)" +
                                "\nModulus: |?|" +
                                "\nMixed Fraction: ?&?/?" +
                                "\nLogx: log(?,?)" +
                                "\nLog: log(?)" +
                                "\nSquare Root: sqrt(?)" +
                                "\nRoot: root(?,?)" +
                                "\nAddition: ?+?" +
                                "\nSubtraction: ?-?" +
                                "\nMultiplication: ?*?" +
                                "\nDivision: ?/?\n\nFor any other subject just enter your question :).")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .positiveText("Got it")
                        .show();

            }
        });

    }



}
