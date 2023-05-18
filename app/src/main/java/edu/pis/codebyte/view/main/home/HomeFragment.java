package edu.pis.codebyte.view.main.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import edu.pis.codebyte.R;
import edu.pis.codebyte.view.challenges.DailyChallengeActivity;
import edu.pis.codebyte.viewmodel.main.MainViewModel;
import edu.pis.codebyte.viewmodel.main.ProgrammingLanguagesAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements ProgrammingLanguagesAdapter.OnLanguageSelectedListener, MainViewModel.OnUpdateProgressListener, MainViewModel.LanguagesUpdateListener {
    private RecyclerView recyclerView;
    private ProgrammingLanguagesAdapter adapter;
    private MainViewModel mainViewModel;
    private Hashtable<String, String> selectedLanguage;
    private ProgressBar loadingBar;
    private LinearProgressIndicator progressBar;
    private LinearProgressIndicator titleBar;
    private HashSet<String> currentUserLanguages;
    private View rootView;
    private boolean loaded = false;



    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflar la vista del fragmento
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mainViewModel = MainViewModel.getInstance();
        mainViewModel.setCurrentLanguagesUpdateListener(this);
        mainViewModel.setLanguageListListener(this);

        loadingBar = rootView.findViewById(R.id.home_progressBar);
        loadingBar.bringToFront();
        loadingBar.setVisibility((loaded)? View.GONE : View.VISIBLE);

        progressBar = rootView.findViewById(R.id.languageProgress_homeFragment);

        titleBar = rootView.findViewById(R.id.titleBar_homeFragment);
        titleBar.setIndicatorColor(getResources().getColor(R.color.black));
        titleBar.setTrackColor(getResources().getColor(R.color.grey_progressbar));;
        titleBar.setProgressCompat(40, true);

        recyclerViewSetUp();

        Button dailyChallenge = rootView.findViewById(R.id.start_home_button);
        dailyChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), DailyChallengeActivity.class));
            }
        });

        return rootView;
    }


    @Override
    public void onLanguageSelected(Hashtable<String, String> language) {
        ImageView selectedLanguageImage = getView().findViewById(R.id.selectedLanguage_home_imageView);
        this.selectedLanguage = language;
        selectedLanguageImage.setImageResource(Integer.parseInt(language.get("imageResourceId")));
        progressBar.setProgressCompat((int) mainViewModel.getUserProgressOfLanguage(language.get("name")), true);
    }

    private HashSet<String> getDefaultCurrentLanguagesList() {
        HashSet<String> hash= new HashSet<>();
        hash.add("Java");
        hash.add("Python");
        hash.add("C");
        hash.add("Cpp");
        hash.add("JavaScript");
        hash.add("C#");
        return hash;
    }

    private void recyclerViewSetUp() {
        recyclerView = rootView.findViewById(R.id.recyclerView);
        currentUserLanguages = mainViewModel.getuCurrentLanguages();
        if (currentUserLanguages == null || currentUserLanguages.size() == 0) {
            currentUserLanguages = getDefaultCurrentLanguagesList();
        }
        else loadingBar.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new ProgrammingLanguagesAdapter(this, new ArrayList<>(currentUserLanguages), mainViewModel.getLanguages());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onUpdateProgressListener() {
        recyclerViewSetUp();
    }

    @Override
    public void updateLanguageList(ArrayList<Hashtable<String, String>> new_languageList) {
        recyclerViewSetUp();
        loaded = true;
    }
}