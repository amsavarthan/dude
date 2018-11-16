package com.amsavarthan.dude.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.activities.CreateReport;
import com.amsavarthan.dude.activities.ImagePreview;

import java.util.ArrayList;
import java.util.List;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder>{

    private List<String> fileNameList;
    private List<String> fileUriList;
    private List<String> fileDoneList;
    public static List<String> uploadedImagesUrl;
    private Activity activity;
    private Context context;

    public UploadListAdapter(List<String> fileUriList,List<String> fileNameList, List<String> fileDoneList){

        this.fileDoneList = fileDoneList;
        this.fileNameList = fileNameList;
        this.fileUriList = fileUriList;
        uploadedImagesUrl=new ArrayList<>();

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_list, parent, false);
        context=parent.getContext();
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ImagePreview.class)
                        .putExtra("uri",fileUriList.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });

        String fileName = fileNameList.get(position);
        String fileDone = fileDoneList.get(position);

        if(fileDone.equals("uploading")){

            holder.imageView.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));

        } else {

            holder.progressBar.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setImageResource(R.drawable.ic_done_white_24dp);
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.green));

            if(uploadedImagesUrl.size()==getItemCount()){
                CreateReport.canUpload=true;
            }
        }



    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        private ProgressBar progressBar;
        private ImageView imageView;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            cardView=mView.findViewById(R.id.card);
            imageView=mView.findViewById(R.id.imageview);
            progressBar =mView.findViewById(R.id.upload_progress);


        }

    }

}