package com.amsavarthan.dude.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.adapter.AnswersAdapter;
import com.amsavarthan.dude.models.Answers;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AnswersActivity extends AppCompatActivity {

    private static final String TAG = AnswersActivity.class.getSimpleName();
    String author_id,author,question,timestamp,doc_id;
    TextView author_textview,question_textview;
    EditText answer;
    RecyclerView mRecyclerView;
    FirebaseFirestore mFirestore;
    FirebaseUser mCurrentUser;
    AnswersAdapter adapter;
    List<Answers> answers=new ArrayList<>();
    private String answered_by;

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
        setContentView(R.layout.activity_answers);

        mFirestore=FirebaseFirestore.getInstance();
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        mRecyclerView=findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        author_id=getIntent().getStringExtra("user_id");
        author=getIntent().getStringExtra("author");
        doc_id=getIntent().getStringExtra("doc_id");
        timestamp=getIntent().getStringExtra("timestamp");
        answered_by=getIntent().getStringExtra("answered_by");
        question=getIntent().getStringExtra("question");

        adapter=new AnswersAdapter(answers,author_id, doc_id, "Questions", answered_by);
        mRecyclerView.setAdapter(adapter);

        author_textview=findViewById(R.id.auth_sub);
        question_textview=findViewById(R.id.question);
        answer=findViewById(R.id.answer);

        question_textview.setText(question);
        author_textview.setText("Asked by "+author+" ( "+ TimeAgo.using(Long.parseLong(timestamp))+" )");

        mFirestore.collection("Questions")
                .document(doc_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(!TextUtils.isEmpty(documentSnapshot.getString("answered_by"))){
                            answer.setEnabled(false);
                            answer.setHint("Question closed by "+author);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error",e.getLocalizedMessage());
                    }
                });

        getAnswers();

    }

    private void getAnswers() {

        mFirestore.collection("Questions")
                .document(doc_id)
                .collection("Answers")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if(e!=null){
                            Log.e(TAG,e.getLocalizedMessage());
                            return;
                        }

                        for(DocumentChange doc:queryDocumentSnapshots.getDocumentChanges()){

                            if(doc.getType()== DocumentChange.Type.ADDED){

                                Answers answer=doc.getDocument().toObject(Answers.class).withId(doc.getDocument().getId());
                                if(!TextUtils.isEmpty(doc.getDocument().getString("is_answer"))){
                                    answers.add(0,answer);
                                }else{
                                    answers.add(answer);
                                }
                                adapter.notifyDataSetChanged();

                            }

                            if(doc.getType()== DocumentChange.Type.MODIFIED){

                                adapter.notifyDataSetChanged();

                            }

                            if(doc.getType()== DocumentChange.Type.REMOVED){


                                adapter.notifyDataSetChanged();

                            }

                        }

                    }
                });

    }

    public void sendAnswer(View view) {
        if(!TextUtils.isEmpty(answer.getText().toString())) {

            final ProgressDialog mDialog=new ProgressDialog(this);
            mDialog.setMessage("Please wait....");
            mDialog.setIndeterminate(true);
            mDialog.setCancelable(false);
            mDialog.show();

           mFirestore.collection("Users")
                   .document(mCurrentUser.getUid())
                   .get()
                   .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                       @Override
                       public void onSuccess(DocumentSnapshot documentSnapshot) {
                           Map<String,Object> answerMap=new HashMap<>();
                           answerMap.put("user_id",documentSnapshot.getString("name"));
                           answerMap.put("name",documentSnapshot.getString("name"));
                           answerMap.put("timestamp",String.valueOf(System.currentTimeMillis()));
                           answerMap.put("answer",answer.getText().toString());
                           answerMap.put("is_answer","no");

                           mFirestore.collection("Questions")
                                   .document(doc_id)
                                   .collection("Answers")
                                   .add(answerMap)
                                   .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                       @Override
                                       public void onSuccess(DocumentReference documentReference) {
                                           mDialog.dismiss();
                                           adapter.notifyDataSetChanged();
                                       }
                                   })
                                   .addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           mDialog.dismiss();
                                           Log.e(TAG,e.getLocalizedMessage());
                                       }
                                   });
                       }
                   })
                   .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           mDialog.dismiss();
                           Log.e(TAG,e.getLocalizedMessage());
                       }
                   });
        }
    }
}
