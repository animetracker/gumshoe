package com.alexzamurca.animetrackersprint2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StoreRecyclerViewAdapter extends RecyclerView.Adapter<StoreRecyclerViewAdapter.MyViewHolder>{

    private Context mContext;
    private List<Item> mData;

    public StoreRecyclerViewAdapter(Context mContext, List<Item> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.store_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_icon_name.setText(mData.get(position).getIconName());
        holder.img_icon_image.setImageResource(mData.get(position).getIconImage());
        holder.tv_icon_price.setText(mData.get(position).getIconPrice());
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

            tv_icon_name = (TextView) itemView.findViewById(R.id.icon_name_id);
            img_icon_image = (ImageView) itemView.findViewById(R.id.icon_image_id);
            tv_icon_price = (TextView) itemView.findViewById(R.id.icon_price);

        }
    }
}
