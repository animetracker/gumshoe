package com.alexzamurca.animetrackersprint2.settings;

import android.content.Intent;
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
    Integer[] initials = {};
    Integer[] roles = {};

    private LayoutInflater layoutInflater;

    RecyclerAdapter(String[] _data, String[] _data2, Integer[] _data3, Integer[] _data4) {
        names = _data;
        contact = _data2;
        initials = _data3;
        roles = _data4;
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
        int initial = initials[position];
        holder.imgIcon.setImageResource(initial);
        int role = roles[position];
        holder.imgRole.setImageResource(role);
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    public class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView names;
        TextView contact;
        ImageView imgRole;
        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            names = itemView.findViewById(R.id.namestxt);
            contact = itemView.findViewById(R.id.contacttxt);
            imgRole = itemView.findViewById(R.id.roleTag);
        }
    }
}
