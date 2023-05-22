package edu.pis.codebyte.view.main.allLanguages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.Hashtable;

import edu.pis.codebyte.R;
import edu.pis.codebyte.viewmodel.main.MainViewModel;
import edu.pis.codebyte.viewmodel.main.ProgrammingLanguagesAdapter;


public class AllLanguagesFragment extends Fragment implements ProgrammingLanguagesAdapter.OnLanguageSelectedListener {

    private RecyclerView recyclerView;
    private ProgrammingLanguagesAdapter adapter;
    private MainViewModel mainViewModel;


    public AllLanguagesFragment() {
        // Required empty public constructor
    }

    public static AllLanguagesFragment newInstance() {
        return new AllLanguagesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_languages, container, false);
        mainViewModel = MainViewModel.getInstance();

        LinearProgressIndicator titleBar = rootView.findViewById(R.id.LoadingScreen_AlllanguagesFragment_titleBar);
        titleBar.setProgressCompat(86, true);
        titleBar.setIndicatorColor(getResources().getColor(R.color.black));
        titleBar.setTrackColor(getResources().getColor(R.color.grey_progressbar));;
        recyclerView_setup(rootView);
        return rootView;
    }

    private void updateRecyclerView() {
        adapter = new ProgrammingLanguagesAdapter(this, mainViewModel.getLanguages());
        recyclerView.setAdapter(adapter);
    }

    private void recyclerView_setup(View rootView) {
        recyclerView = rootView.findViewById(R.id.LanguagesList_AlllanguagesFragment_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        updateRecyclerView();
    }

    @Override
    public void onLanguageSelected(Hashtable<String, String> language) {

    }
}