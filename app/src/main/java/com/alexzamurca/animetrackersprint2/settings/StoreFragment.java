package com.alexzamurca.animetrackersprint2.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.alexzamurca.animetrackersprint2.R;

import java.util.ArrayList;
import java.util.List;

public class StoreFragment extends Fragment
{

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_store, container, false);

        Toolbar toolbar = view.findViewById(R.id.store_toolbar_object);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if(activity.getSupportActionBar()!=null)
        {
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = view.findViewById(R.id.store_recyclerview_id);
        initRecyclerView();


        return view;
    }

    void initRecyclerView()
    {
        List<Item> listItem = new ArrayList<>();
        listItem.add(new Item("First Icon", R.drawable.ash, 5000));
        listItem.add(new Item("Second Icon", R.drawable.goku, 4696));
        listItem.add(new Item("Third Icon", R.drawable.luffy, 3696));
        listItem.add(new Item("Fourth Icon", R.drawable.naruto, 6969));
        listItem.add(new Item("Ran", R.drawable.ash, 5000));
        listItem.add(new Item("Out", R.drawable.ash, 5000));
        listItem.add(new Item("Of", R.drawable.ash, 5000));
        listItem.add(new Item("Images", R.drawable.ash, 5000));
        listItem.add(new Item("Yeah", R.drawable.ash, 5000));

        StoreRecyclerViewAdapter myAdapter = new StoreRecyclerViewAdapter(listItem);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }
}
