package com.amsavarthan.dude.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.dude.R;
import com.amsavarthan.dude.activities.AddQuestion;
import com.amsavarthan.dude.adapter.AnsweredAdapter;
import com.amsavarthan.dude.models.AllQuestionsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tylersuehr.esr.EmptyStateRecyclerView;
import com.tylersuehr.esr.TextStateDisplay;

import java.util.ArrayList;
import java.util.List;

public class MyQuestions extends Fragment {


    private EmptyStateRecyclerView recyclerView;
    private Context context;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mCurrentUser;
    private AnsweredAdapter adapter;
    private static String TAG = MyQuestions.class.getSimpleName();
    private List<AllQuestionsModel> allQuestionsModelList = new ArrayList<>();
    private View view;
    private ImageView filter, add;

    public MyQuestions() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_my_answered, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = view.getContext();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        adapter = new AnsweredAdapter(allQuestionsModelList);

        if (mCurrentUser != null) {

            mFirestore = FirebaseFirestore.getInstance();

            filter = view.findViewById(R.id.filter);
            add = view.findViewById(R.id.add);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, AddQuestion.class));
                }
            });

            filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(context)
                            .title("Filter by subjects")
                            .items(R.array.subjects)
                            .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    allQuestionsModelList.clear();
                                    filterResult(text.toString());
                                    return true;
                                }
                            })
                            .positiveText("Filter")
                            .show();
                }
            });

            recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            TextStateDisplay error_style = new TextStateDisplay(context, "Sorry for the inconvenience", "Something went wrong :(");
            TextStateDisplay empty_style = new TextStateDisplay(context, "It's Empty", "No unanswered questions found");

            recyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR, error_style);
            recyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY, empty_style);

            allQuestionsModelList.clear();
            recyclerView.setAdapter(adapter);

            getQuestions();

        }
    }

    private void getQuestions() {

        Query firstQuery = mFirestore.collection("Questions").orderBy("timestamp", Query.Direction.DESCENDING);
        firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            if (doc.getDocument().getString("id").equals(mCurrentUser.getUid())) {
                                AllQuestionsModel question = doc.getDocument().toObject(AllQuestionsModel.class).withId(doc.getDocument().getId());
                                allQuestionsModelList.add(question);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        if (doc.getType() == DocumentChange.Type.REMOVED) {


                                adapter.notifyDataSetChanged();

                        }

                        if (doc.getType() == DocumentChange.Type.MODIFIED) {


                                adapter.notifyDataSetChanged();

                        }

                    }


                } else {
                    recyclerView.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                }

            }

        });


    }

    public void filterResult(String subject){

        if(subject.equals("All")){
            getQuestions();
        }else{

            Query firstQuery = mFirestore.collection("Questions")
                    .whereEqualTo("subject",subject)
                    .orderBy("timestamp", Query.Direction.DESCENDING);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    try {
                        if (!documentSnapshots.isEmpty()) {

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    if (doc.getDocument().getString("id").equals(mCurrentUser.getUid())) {
                                        AllQuestionsModel question = doc.getDocument().toObject(AllQuestionsModel.class).withId(doc.getDocument().getId());
                                        allQuestionsModelList.add(question);
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                if (doc.getType() == DocumentChange.Type.REMOVED) {
                                    adapter.notifyDataSetChanged();

                                }

                                if (doc.getType() == DocumentChange.Type.MODIFIED) {
                                    adapter.notifyDataSetChanged();
                                }
                            }


                        } else {
                            recyclerView.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                        }
                    }catch (NullPointerException eee){
                        adapter.notifyDataSetChanged();
                    } catch (Exception ee){
                        ee.printStackTrace();
                    }

                }

            });


        }

    }

}

