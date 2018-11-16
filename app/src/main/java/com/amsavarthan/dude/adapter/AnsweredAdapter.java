package com.amsavarthan.dude.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.dude.R;
import com.amsavarthan.dude.activities.AnswersActivity;
import com.amsavarthan.dude.models.AllQuestionsModel;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AnsweredAdapter extends RecyclerView.Adapter<AnsweredAdapter.ViewHolder> {

    private List<AllQuestionsModel> allQuestionsModels;
    private Context context;

    public AnsweredAdapter(List<AllQuestionsModel> unanswereds) {
        this.allQuestionsModels = unanswereds;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answered,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        AllQuestionsModel allQuestionsModel = allQuestionsModels.get(holder.getAdapterPosition());

        if(allQuestionsModel.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

            holder.item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new MaterialDialog.Builder(context)
                            .title("Delete")
                            .content("Are you sure do you want to delete this question?")
                            .positiveText("Yes")
                            .negativeText("No")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();

                                    final ProgressDialog mDialog=new ProgressDialog(context);
                                    mDialog.setMessage("Please wait....");
                                    mDialog.setIndeterminate(true);
                                    mDialog.setCancelable(false);
                                    mDialog.setCanceledOnTouchOutside(false);
                                    mDialog.show();

                                    FirebaseFirestore.getInstance().collection("Questions")
                                            .document(allQuestionsModels.get(holder.getAdapterPosition()).Answered_doc_id)
                                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mDialog.dismiss();
                                            Toast.makeText(context, "Question Deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    mDialog.dismiss();
                                                    Log.e("Error",e.getLocalizedMessage());
                                                }
                                            });

                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    return false;
                }
            });

        }

        holder.question.setText(allQuestionsModel.getQuestion());
        holder.timestamp.setText(TimeAgo.using(Long.parseLong(allQuestionsModel.getTimestamp())));

        if(TextUtils.isEmpty(allQuestionsModel.getAnswered_by())||allQuestionsModel.getAnswered_by().equals("")){
            holder.answered_by.setVisibility(View.GONE);
        }else{
            holder.answered_by.setText(allQuestionsModel.getAnswered_by());
        }

        holder.subject.setText(" "+ allQuestionsModel.getSubject()+" ");
        holder.author.setText(" "+ allQuestionsModel.getName()+" ");
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, AnswersActivity.class)
                        .putExtra("answered_by", allQuestionsModels.get(holder.getAdapterPosition()).getAnswered_by())
                        .putExtra("user_id", allQuestionsModels.get(holder.getAdapterPosition()).getId())
                        .putExtra("doc_id", allQuestionsModels.get(holder.getAdapterPosition()).Answered_doc_id)
                        .putExtra("author", allQuestionsModels.get(holder.getAdapterPosition()).getName())
                        .putExtra("question", allQuestionsModels.get(holder.getAdapterPosition()).getQuestion())
                        .putExtra("timestamp", allQuestionsModels.get(holder.getAdapterPosition()).getTimestamp()));

            }
        });

    }

    @Override
    public int getItemCount() {
        return allQuestionsModels.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView question,timestamp,author,subject,answered_by;
        LinearLayout item;

        ViewHolder(View itemView) {
            super(itemView);

            item=itemView.findViewById(R.id.layout);
            question=itemView.findViewById(R.id.question);
            timestamp=itemView.findViewById(R.id.timestamp);
            author=itemView.findViewById(R.id.author);
            subject=itemView.findViewById(R.id.subject);
            answered_by=itemView.findViewById(R.id.answered_by);

        }
    }
}