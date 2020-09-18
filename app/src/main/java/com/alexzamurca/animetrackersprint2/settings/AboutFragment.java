package com.alexzamurca.animetrackersprint2.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexzamurca.animetrackersprint2.R;

import java.util.Arrays;
import java.util.Collections;


public class AboutFragment extends Fragment {

    private String[] names = {"Safin Ahmed", "Jivraj Dhamija", "Henry Eason", "Vedant Nemane","Carlston Rebelo", "Alex Zamurca"};
    private String[] contacts = {"ec19235@qmul.ac.uk", "jd2158@bath.ac.uk", "he328@bath.ac.uk", "vn295@bath.ac.uk", "notslrac3@gmail.com", "az506@bath.ac.uk"};
    private Integer[] initials = {R.drawable.sa, R.drawable.jd, R.drawable.he, R.drawable.vn, R.drawable.cr, R.drawable.az};
    private Integer[] roles = {R.drawable.frontenddev, R.drawable.frontenddev, R.drawable.backenddev, R.drawable.admin, R.drawable.artist, R.drawable.backenddev};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View listItemsView = inflater.inflate(R.layout.fragment_about, container, false);

        Toolbar toolbar = listItemsView.findViewById(R.id.about_toolbar_object);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if(activity.getSupportActionBar()!=null)
        {
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = listItemsView.findViewById(R.id.aboutRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new RecyclerAdapter(names, contacts, initials, roles);
        recyclerView.setAdapter(adapter);

        return listItemsView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}