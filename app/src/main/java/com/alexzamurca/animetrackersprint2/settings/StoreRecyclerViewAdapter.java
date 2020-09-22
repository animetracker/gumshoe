package com.alexzamurca.animetrackersprint2.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.series.series_list.SeriesRecyclerViewAdapter;

import java.util.List;

public class StoreRecyclerViewAdapter extends RecyclerView.Adapter<StoreRecyclerViewAdapter.MyViewHolder>{

    private List<Item> mData;

    public StoreRecyclerViewAdapter(List<Item> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item, parent, false);
        return new StoreRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.tv_icon_name.setText(mData.get(position).getIconName());
        holder.img_icon_image.setImageResource(mData.get(position).getIconImage());
        holder.tv_icon_price.setText(Integer.toString(mData.get(position).getIconPrice()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_icon_name;
        ImageView img_icon_image;
        TextView tv_icon_price;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_icon_name = itemView.findViewById(R.id.icon_name_id);
            img_icon_image =itemView.findViewById(R.id.icon_image_id);
            tv_icon_price =  itemView.findViewById(R.id.icon_price);

        }
    }
}
