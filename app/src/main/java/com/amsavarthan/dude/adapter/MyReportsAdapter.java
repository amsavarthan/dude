package com.amsavarthan.dude.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.dude.R;
import com.amsavarthan.dude.activities.CreateReport;
import com.amsavarthan.dude.activities.ImagePreview;
import com.amsavarthan.dude.models.Img;
import com.amsavarthan.dude.models.MyReport;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyReportsAdapter extends RecyclerView.Adapter<MyReportsAdapter.ViewHolder>{

    private List<MyReport> reportList;
    private Context context;
    private int i;

    public MyReportsAdapter(List<MyReport> reportList){

        this.reportList=reportList;

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_report, parent, false);
        context=parent.getContext();
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final MyReport report=reportList.get(position);

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new MaterialDialog.Builder(context)
                        .title("Delete")
                        .content("Are you sure do you want to delete this report?")
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

                                FirebaseFirestore.getInstance().collection("Reports")
                                        .document(report.reportID)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                mDialog.show();
                                                if(!TextUtils.isEmpty(documentSnapshot.getString("image_url_0"))){

                                                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(documentSnapshot.getString("image_url_0"));
                                                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // File deleted successfully
                                                            mDialog.dismiss();
                                                            i++;
                                                            Log.d("Report", "onSuccess: deleted file");
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            // Uh-oh, an error occurred!
                                                            mDialog.dismiss();
                                                            Log.d("Report", "onFailure: did not delete file");
                                                        }
                                                    });

                                                }

                                                mDialog.show();
                                                if(!TextUtils.isEmpty(documentSnapshot.getString("image_url_1"))){

                                                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(documentSnapshot.getString("image_url_1"));
                                                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // File deleted successfully
                                                            mDialog.dismiss();
                                                            i++;
                                                            Log.d("Report", "onSuccess: deleted file");
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            // Uh-oh, an error occurred!
                                                            mDialog.dismiss();
                                                            Log.d("Report", "onFailure: did not delete file");
                                                        }
                                                    });

                                                }

                                                mDialog.show();
                                                if(!TextUtils.isEmpty(documentSnapshot.getString("image_url_2"))){

                                                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(documentSnapshot.getString("image_url_2"));
                                                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // File deleted successfully
                                                            mDialog.dismiss();
                                                            i++;
                                                            Log.d("Report", "onSuccess: deleted file");
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            // Uh-oh, an error occurred!
                                                            mDialog.dismiss();
                                                            Log.d("Report", "onFailure: did not delete file");
                                                        }
                                                    });

                                                }

                                                mDialog.show();
                                                if(!TextUtils.isEmpty(documentSnapshot.getString("image_url_3"))){

                                                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(documentSnapshot.getString("image_url_3"));
                                                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // File deleted successfully
                                                            mDialog.dismiss();
                                                            i++;
                                                            Log.d("Report", "onSuccess: deleted file");
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            // Uh-oh, an error occurred!
                                                            mDialog.dismiss();
                                                            Log.d("Report", "onFailure: did not delete file");
                                                        }
                                                    });

                                                }

                                                mDialog.show();
                                                if(!TextUtils.isEmpty(documentSnapshot.getString("image_url_4"))){

                                                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(documentSnapshot.getString("image_url_4"));
                                                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // File deleted successfully
                                                            mDialog.dismiss();
                                                            i++;
                                                            Log.d("Report", "onSuccess: deleted file");
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            // Uh-oh, an error occurred!
                                                            mDialog.dismiss();
                                                            Log.d("Report", "onFailure: did not delete file");
                                                        }
                                                    });

                                                }

                                                mDialog.show();
                                                if(!TextUtils.isEmpty(documentSnapshot.getString("image_url_5"))){

                                                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(documentSnapshot.getString("image_url_5"));
                                                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // File deleted successfully
                                                            i++;
                                                            mDialog.dismiss();
                                                            Log.d("Report", "onSuccess: deleted file");
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            // Uh-oh, an error occurred!
                                                            mDialog.dismiss();
                                                            Log.d("Report", "onFailure: did not delete file");
                                                        }
                                                    });

                                                }

                                                mDialog.show();
                                                if(!TextUtils.isEmpty(documentSnapshot.getString("image_url_6"))){

                                                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(documentSnapshot.getString("image_url_6"));
                                                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // File deleted successfully
                                                            i++;
                                                            mDialog.dismiss();
                                                            Log.d("Report", "onSuccess: deleted file");
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            // Uh-oh, an error occurred!
                                                            mDialog.dismiss();
                                                            Log.d("Report", "onFailure: did not delete file");
                                                        }
                                                    });

                                                }

                                                mDialog.show();
                                                FirebaseFirestore.getInstance().collection("Reports")
                                                        .document(report.reportID)
                                                        .delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                mDialog.dismiss();
                                                                Toast.makeText(context, "Report Deleted", Toast.LENGTH_SHORT).show();
                                                                reportList.remove(position);
                                                                notifyItemRemoved(position);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                mDialog.dismiss();
                                                                Log.e("Error", e.getLocalizedMessage());
                                                            }
                                                        });

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

        holder.info.setText(report.getInfo());
        holder.timestamp.setText(TimeAgo.using(Long.parseLong(report.getTimestamp())));
        holder.status.setText(String.format(" %s ", report.getStatus()));
        holder.date.setText(String.format(" %s ", report.getDate()));
        holder.location.setText(String.format(" %s ", report.getLocation()));

        if(report.getImage_count()==0){
            holder.attachments.setVisibility(View.GONE);
        }else{

            List<Img> urls = new ArrayList<>();

            ImageSubAdapter adapter=new ImageSubAdapter(urls);
            holder.attachments.setVisibility(View.VISIBLE);
            holder.attachments.setItemAnimator(new DefaultItemAnimator());
            LinearLayoutManager layoutManager=new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.attachments.setLayoutManager(layoutManager);
            holder.attachments.setHasFixedSize(true);
            holder.attachments.setAdapter(adapter);

            if(!TextUtils.isEmpty(report.getImage_url_0())){
                Img img=new Img("1",report.getImage_url_0());
                urls.add(img);
                adapter.notifyDataSetChanged();
            }

            if(!TextUtils.isEmpty(report.getImage_url_1())){
                Img img=new Img("2",report.getImage_url_1());
                urls.add(img);
                adapter.notifyDataSetChanged();
            }

            if(!TextUtils.isEmpty(report.getImage_url_2())){
                Img img=new Img("3",report.getImage_url_2());
                urls.add(img);
                adapter.notifyDataSetChanged();
            }

            if(!TextUtils.isEmpty(report.getImage_url_3())){
                Img img=new Img("4",report.getImage_url_3());
                urls.add(img);
                adapter.notifyDataSetChanged();
            }

            if(!TextUtils.isEmpty(report.getImage_url_4())){
                Img img=new Img("5",report.getImage_url_4());
                urls.add(img);
                adapter.notifyDataSetChanged();
            }

            if(!TextUtils.isEmpty(report.getImage_url_5())){
                Img img=new Img("6",report.getImage_url_5());
                urls.add(img);
                adapter.notifyDataSetChanged();
            }

            if(!TextUtils.isEmpty(report.getImage_url_6())){
                Img img=new Img("7",report.getImage_url_6());
                urls.add(img);
                adapter.notifyDataSetChanged();
            }

        }



    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private LinearLayout layout;
        private TextView info,timestamp,location,date,status;
        private RecyclerView attachments;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            layout=mView.findViewById(R.id.layout);
            location=mView.findViewById(R.id.location);
            date=mView.findViewById(R.id.date);
            info=mView.findViewById(R.id.info);
            timestamp=mView.findViewById(R.id.timestamp);
            status=mView.findViewById(R.id.status);
            attachments=mView.findViewById(R.id.attachments);

        }

    }

}