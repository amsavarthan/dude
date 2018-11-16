package com.amsavarthan.dude.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.activities.ImagePreview;
import com.amsavarthan.dude.models.Img;
import com.amsavarthan.dude.models.MyReport;
import com.github.marlonlom.utilities.timeago.TimeAgo;

import java.util.List;

public class ImageSubAdapter extends RecyclerView.Adapter<ImageSubAdapter.ViewHolder>{

    private List<Img> urls;
    private Context context;

    public ImageSubAdapter(List<Img> url){
       this.urls=url;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myreport_list, parent, false);
        context=parent.getContext();
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Img url=urls.get(position);
        holder.textView.setTextColor(Color.WHITE);
        holder.textView.setText(url.getId());
        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.accent));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ImagePreview.class).putExtra("url",url.getUrl()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        private TextView textView;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            cardView=mView.findViewById(R.id.card);
            textView=mView.findViewById(R.id.text);


        }

    }

}