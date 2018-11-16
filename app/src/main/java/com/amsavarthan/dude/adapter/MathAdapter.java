package com.amsavarthan.dude.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.activities.MathContainer;
import com.amsavarthan.dude.models.Functions;

import java.util.List;

public class MathAdapter extends RecyclerView.Adapter<MathAdapter.ViewHolder> {

    private List<Functions> functions;
    private Context context;

    public MathAdapter(List<Functions> functions) {
        this.functions = functions;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_functions_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.title.setText(functions.get(position).getTitle());
        holder.subtitle.setText(functions.get(position).getSubtitle());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MathContainer.class).putExtra("id",functions.get(holder.getAdapterPosition()).getId() ));
            }
        });
    }

    @Override
    public int getItemCount() {
        return functions.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title,subtitle;
        LinearLayout item;


        ViewHolder(View itemView) {
            super(itemView);

            item=itemView.findViewById(R.id.item);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
        }
    }
}