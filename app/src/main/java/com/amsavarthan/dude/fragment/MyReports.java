package com.amsavarthan.dude.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.adapter.MyReportsAdapter;
import com.amsavarthan.dude.models.MyReport;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyReports extends Fragment  {

    List<MyReport> reportList=new ArrayList<>();
    RecyclerView mRecyclerView;
    MyReportsAdapter adapter;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.frag_my_report, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter=new MyReportsAdapter(reportList);

        mRecyclerView=view.findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);

        getReports();

    }

    private void getReports() {

        FirebaseFirestore.getInstance().collection("Reports")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentChange doc:queryDocumentSnapshots.getDocumentChanges()){
                            if(doc.getType()== DocumentChange.Type.ADDED) {
                                if (doc.getDocument().getString("user_id").equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                {
                                    MyReport report = doc.getDocument().toObject(MyReport.class).withId(doc.getDocument().getId());
                                    reportList.add(report);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getContext(), "Some technical error occurred", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });

    }

}
