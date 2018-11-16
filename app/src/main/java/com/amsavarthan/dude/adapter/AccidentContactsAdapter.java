package com.amsavarthan.dude.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.models.Contacts;
import com.amsavarthan.dude.utils.ContactsDatabase;

import java.util.List;

public class AccidentContactsAdapter extends RecyclerView.Adapter<AccidentContactsAdapter.ViewHolder> {

    private List<Contacts> contactsList;
    private Context context;

    public AccidentContactsAdapter(List<Contacts> contactsList){
        this.contactsList=contactsList;
    }

    @NonNull
    @Override
    public AccidentContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_accident_contact,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final AccidentContactsAdapter.ViewHolder holder, int position) {

        holder.name.setText(contactsList.get(position).getName());
        holder.phone.setText(contactsList.get(position).getPhone());
        holder.number.setText(String.valueOf(position+1));

    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView number,name,phone;
        View item;
        ImageView delete;

        public ViewHolder(View itemView) {
            super(itemView);

            item=itemView;
            delete=itemView.findViewById(R.id.delete);
            number=itemView.findViewById(R.id.number);
            name=itemView.findViewById(R.id.name);
            phone=itemView.findViewById(R.id.phone);

        }
    }
}
