package com.alexzamurca.animetrackersprint2.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzamurca.animetrackersprint2.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.AppViewHolder>{

    String[] names = {};
    String[] contact = {};

    private LayoutInflater layoutInflater;

    RecyclerAdapter(String[] _data, String[] _data2) {
        names = _data;
        contact = _data2;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.settings_about_list_layout, parent, false);


        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        String name = names[position];
        holder.names.setText(name);
        String contacts = contact[position];
        holder.contact.setText(contacts);
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    public class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView names;
        TextView contact;
        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            names = itemView.findViewById(R.id.namestxt);
            contact = itemView.findViewById(R.id.contacttxt);
        }
    }
}
