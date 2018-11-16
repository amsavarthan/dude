package com.amsavarthan.dude.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.activities.AllQuestionsActivity;
import com.amsavarthan.dude.activities.CreateReport;
import com.amsavarthan.dude.activities.FragmentContainer;
import com.amsavarthan.dude.nearby.ui.SplashScreenActivity;
import com.amsavarthan.dude.reminder.MainActivity;
import com.amsavarthan.dude.activities.MyQuestions;
import com.amsavarthan.dude.models.CategoryItems;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private List<CategoryItems> categoryItemsList;
    private Context context;

    public CategoriesAdapter(List<CategoryItems> categoryItemsList){
        this.categoryItemsList=categoryItemsList;
    }

    @NonNull
    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoriesAdapter.ViewHolder holder, int position) {

        holder.category.setVisibility(View.GONE);
        /*if(position==0){
            holder.category.setVisibility(View.VISIBLE);
            holder.category.setText(categoryItemsList.get(position).getCategory_name());
        }else{
            if(categoryItemsList.get(position-1).getCategory_name().equals(categoryItemsList.get(position).getCategory_name())){
                holder.category.setVisibility(View.GONE);
            }else{
                holder.category.setVisibility(View.VISIBLE);
                holder.category.setText(categoryItemsList.get(position).getCategory_name());
            }
        }*/
        holder.textView.setText(categoryItemsList.get(position).getText());
        holder.textView.setTextColor(Integer.parseInt(categoryItemsList.get(position).getColor()));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name=categoryItemsList.get(holder.getAdapterPosition()).getName();

                if(name.equals("forum")){
                    context.startActivity(new Intent(context, AllQuestionsActivity.class));
                    return;
                }

                if(name.equals("my_forum")){
                    context.startActivity(new Intent(context, MyQuestions.class));
                    return;
                }

                if(name.equals("reminder")){
                    context.startActivity(new Intent(context, MainActivity.class));
                    return;
                }

                if(name.equals("reports")){
                    context.startActivity(new Intent(context, CreateReport.class));
                    return;
                }

                if(name.equals("nearby")){
                    context.startActivity(new Intent(context, SplashScreenActivity.class));
                    return;
                }

                if(name.equals("drive")){
                    context.startActivity(new Intent(context, com.amsavarthan.dude.drivingmode.MainActivity.class));
                    return;
                }

                context.startActivity(new Intent(context, FragmentContainer.class).putExtra("name",categoryItemsList.get(holder.getAdapterPosition()).getName()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryItemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView textView,category;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.card);
            textView=itemView.findViewById(R.id.textview);
            category=itemView.findViewById(R.id.category);
        }
    }
}
