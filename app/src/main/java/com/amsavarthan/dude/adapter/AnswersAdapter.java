package com.amsavarthan.dude.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.dude.R;
import com.amsavarthan.dude.models.Answers;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder> {

    private List<Answers> answereds;
    private Context context;
    private String doc_id,type,owner_id,answered_by;

    public AnswersAdapter(List<Answers> unanswereds, String owner_id, String doc_id, String type, String answered_by) {
        this.answereds = unanswereds;
        this.doc_id = doc_id;
        this.type = type;
        this.owner_id = owner_id;
        this.answered_by = answered_by;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Answers answer=answereds.get(holder.getAdapterPosition());
        holder.answer.setText(answer.getAnswer());
        holder.timestamp.setText(TimeAgo.using(Long.parseLong(answer.getTimestamp())));
        holder.name.setText(answer.getName());

        FirebaseFirestore.getInstance().collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(answer.getName().equals(documentSnapshot.getString("name"))){
                            holder.name.append(" (You)");
                            if(answer.getIs_answer().equals("Yes")) {
                                holder.delete.setVisibility(View.GONE);
                            }else{
                                holder.delete.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

        if(owner_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.unmrk_ans.setVisibility(View.GONE);
            holder.mrk_ans.setVisibility(View.VISIBLE);
        }else{
            holder.mrk_ans.setVisibility(View.GONE);
            holder.unmrk_ans.setVisibility(View.GONE);
        }

        if(answer.getIs_answer().equals("Yes")){
            holder.up.setBackgroundColor(context.getResources().getColor(R.color.green_top));
            holder.bottom.setBackgroundColor(context.getResources().getColor(R.color.green_bottom));
            if(owner_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                holder.mrk_ans.setVisibility(View.GONE);
                holder.delete.setVisibility(View.GONE);
                holder.unmrk_ans.setVisibility(View.VISIBLE);
            }else{
                holder.mrk_ans.setVisibility(View.VISIBLE);
                holder.mrk_ans.setEnabled(false);
                holder.unmrk_ans.setVisibility(View.GONE);
                holder.mrk_ans.setText("Marked as Answer");
            }
        }else{
            if(TextUtils.isEmpty(answered_by)) {
                if(owner_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.unmrk_ans.setVisibility(View.GONE);
                    holder.mrk_ans.setVisibility(View.VISIBLE);
                }else{
                    holder.unmrk_ans.setVisibility(View.GONE);
                    holder.mrk_ans.setVisibility(View.GONE);
                }
            }else{
                holder.unmrk_ans.setVisibility(View.GONE);
                holder.mrk_ans.setVisibility(View.GONE);
            }
        }

        holder.mrk_ans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(context)
                        .title("Mark as Answer")
                        .content("Are you sure do you want to mark it as answer?. By marking it as answer, This question will be closed and no one can further answer.")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                dialog.dismiss();
                                final ProgressDialog mDialog=new ProgressDialog(context);
                                mDialog.setMessage("Marking as answer....");
                                mDialog.setIndeterminate(true);
                                mDialog.setCancelable(false);
                                mDialog.setCanceledOnTouchOutside(false);
                                mDialog.show();

                                FirebaseFirestore.getInstance().collection("Questions")
                                        .document(doc_id)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                try {
                                                    if (TextUtils.isEmpty(documentSnapshot.getString("answered_by"))) {

                                                        Map<String, Object> map1 = new HashMap<>();
                                                        map1.put("is_answer", "Yes");

                                                        FirebaseFirestore.getInstance()
                                                                .collection("Questions")
                                                                .document(doc_id)
                                                                .collection("Answers")
                                                                .document(answer.Answers_doc_id)
                                                                .update(map1)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                        Map<String, Object> map2 = new HashMap<>();
                                                                        map2.put("answered_by", answer.getName());

                                                                        FirebaseFirestore.getInstance()
                                                                                .collection("Questions")
                                                                                .document(doc_id)
                                                                                .update(map2)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        mDialog.dismiss();
                                                                                        Toast.makeText(context, "Marked as answer", Toast.LENGTH_SHORT).show();
                                                                                        notifyDataSetChanged();
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        mDialog.dismiss();
                                                                                        Log.e("Error", e.getLocalizedMessage());
                                                                                        Toast.makeText(context, "Error marking as answer: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });

                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        mDialog.dismiss();
                                                                        Log.e("Error", e.getLocalizedMessage());
                                                                        Toast.makeText(context, "Error marking as answer: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });


                                                    } else {

                                                        Toast.makeText(context, "Cannot mark more than one answer as correct.", Toast.LENGTH_SHORT).show();

                                                    }
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                mDialog.dismiss();
                                                Log.e("Error",e.getLocalizedMessage());
                                                Toast.makeText(context, "Error marking as answer: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        holder.unmrk_ans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .title("Unmark as Answer")
                        .content("Are you sure do you want to unmark it as answer?. By unmarking it as answer, This question will be opened for others users to answer")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                dialog.dismiss();
                                final ProgressDialog mDialog=new ProgressDialog(context);
                                mDialog.setMessage("Unmarking as answer....");
                                mDialog.setIndeterminate(true);
                                mDialog.setCancelable(false);
                                mDialog.setCanceledOnTouchOutside(false);
                                mDialog.show();

                                Map<String,Object> map1=new HashMap<>();
                                map1.put("is_answer","No");

                                FirebaseFirestore.getInstance()
                                        .collection("Questions")
                                        .document(doc_id)
                                        .collection("Answers")
                                        .document(answer.Answers_doc_id)
                                        .update(map1)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Map<String,Object> map2=new HashMap<>();
                                                map2.put("answered_by","");

                                                FirebaseFirestore.getInstance()
                                                        .collection("Questions")
                                                        .document(doc_id)
                                                        .update(map2)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                mDialog.dismiss();
                                                                Toast.makeText(context, "Unmarked as answer", Toast.LENGTH_SHORT).show();
                                                                notifyDataSetChanged();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                mDialog.dismiss();
                                                                Log.e("Error",e.getLocalizedMessage());
                                                                Toast.makeText(context, "Error unmarking as answer: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                mDialog.dismiss();
                                                Log.e("Error",e.getLocalizedMessage());
                                                Toast.makeText(context, "Error unmarking as answer: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });



                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(context)
                        .title("Delete")
                        .content("Are you sure do you want to delete it?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                dialog.dismiss();
                                final ProgressDialog mDialog = new ProgressDialog(context);
                                mDialog.setMessage("Please wait....");
                                mDialog.setIndeterminate(true);
                                mDialog.setCancelable(false);
                                mDialog.setCanceledOnTouchOutside(false);
                                mDialog.show();

                                FirebaseFirestore.getInstance()
                                        .collection("Questions")
                                        .document(doc_id)
                                        .collection("Answers")
                                        .document(answer.Answers_doc_id)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mDialog.dismiss();
                                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                                notifyItemRemoved(holder.getAdapterPosition());
                                                notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                mDialog.dismiss();
                                                Log.e("Error",e.getLocalizedMessage());
                                                Toast.makeText(context, "Error deleting: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();


            }
        });


    }

    @Override
    public int getItemCount() {
        return answereds.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout item,up;
        FrameLayout bottom;
        TextView answer,name,timestamp;
        Button mrk_ans,unmrk_ans;
        ImageButton delete;

        ViewHolder(View itemView) {
            super(itemView);

            item=itemView.findViewById(R.id.layout);
            up=itemView.findViewById(R.id.up);
            bottom=itemView.findViewById(R.id.bottom);
            delete=itemView.findViewById(R.id.delete);
            mrk_ans=itemView.findViewById(R.id.mrk_ans);
            unmrk_ans=itemView.findViewById(R.id.unmrk_ans);
            answer=itemView.findViewById(R.id.answer);
            name=itemView.findViewById(R.id.name);
            timestamp=itemView.findViewById(R.id.timestamp);


        }
    }
}