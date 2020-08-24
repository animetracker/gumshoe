package com.alexzamurca.animetrackersprint2.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexzamurca.animetrackersprint2.R;


public class AboutFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String[] names = {"Safin Ahmed", "Jivraj Dhamija", "Henry Eason", "Vedant Nemane", "Alex Zamurca", "Carlston Rebelo"};
    private String[] contacts = {"ec19235@qmul.ac.uk", "jd2158@bath.ac.uk", "he328@bath.ac.uk", "vn295@bath.ac.uk", "az506@bath.ac.uk", "notslrac3@gmail.com"};

    private View listItemsView;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        listItemsView = inflater.inflate(R.layout.fragment_about, container, false);
        recyclerView = listItemsView.findViewById(R.id.aboutRecyclerView);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerAdapter(names, contacts);
        recyclerView.setAdapter(adapter);

        return listItemsView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}