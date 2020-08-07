package com.alexzamurca.animetrackersprint2.series.search;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.series.dialog.CheckConnection;
import com.alexzamurca.animetrackersprint2.series.dialog.NoConnectionDialog;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements NoConnectionDialog.TryAgainListener, SearchRecyclerViewAdapter.RowClickListener {

    private static final String TAG = "SearchActivity";
    private FragmentActivity mContext;
    private NavController navController;

    private ArrayList<SearchResult> list = new ArrayList<>();
    private SearchRecyclerViewAdapter adapter;
    private EditText editText;
    private View globalView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        globalView = inflater.inflate(R.layout.fragment_search, container, false);
        Log.d(TAG, "onCreate: started");

        Toolbar toolbar = globalView.findViewById(R.id.add_series_toolbar_object);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return globalView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        editText = view.findViewById(R.id.search_edit_text);
        initImageBitmaps();
        initRecyclerView();


        editText.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
            {
                searchProcess();
            }
            return false;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            Toast.makeText(getContext(), "BugTest: go back clicked!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchProcess()
    {
        CheckConnection checkConnection = new CheckConnection(getContext());
        boolean isConnected = checkConnection.isConnected();
        if (isConnected)
        {
            String seriesName = editText.getText().toString();
            Toast.makeText(getContext(), "Searching for \"" + seriesName + "\"", Toast.LENGTH_SHORT).show();
            adapter.searchName(seriesName);
        }
        else
        {
            newInstance();
            Toast.makeText(getContext(), "Cannot connect to the internet, check internet connection!", Toast.LENGTH_SHORT).show();
        }
    }

    public void newInstance()
    {
        NoConnectionDialog dialog = new NoConnectionDialog();
        Bundle args = new Bundle();
        args.putSerializable("data", this);
        dialog.setArguments(args);
        dialog.show(mContext.getSupportFragmentManager(), "NoCustomDialog");
    }

    private void initImageBitmaps()
    {
        Log.d(TAG, "initImageBitmaps: preparing bitmaps");
    }

    private void initRecyclerView()
    {
        Log.d(TAG, "initRecyclerView: initialising");
        RecyclerView recyclerView = globalView.findViewById(R.id.search_recycler_view);
        adapter = new SearchRecyclerViewAdapter(getContext(), list, this, globalView.findViewById(R.id.no_search_results_text), globalView.findViewById(R.id.search_layout), navController);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        mContext = (FragmentActivity)context;
        super.onAttach(context);
    }

    @Override
    public void OnSuccessfulClick()
    {
        Toast.makeText(getContext(), "Search has refreshed", Toast.LENGTH_SHORT).show();


        navController.navigate(R.id.searchFragment);
        /*
        FragmentTransaction tr = mContext.getSupportFragmentManager().beginTransaction();
        tr.replace(R.id.fragment_container, new SearchFragment());
        tr.commit();

         */

    }

    @Override
    public void onFailedClick()
    {
        newInstance();
    }
}
