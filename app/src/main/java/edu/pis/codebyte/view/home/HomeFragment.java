package edu.pis.codebyte.view.home;

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

import java.util.ArrayList;

import edu.pis.codebyte.R;
import edu.pis.codebyte.model.ProgrammingLanguage;
import edu.pis.codebyte.model.ProgrammingLanguagesAdapter;
import edu.pis.codebyte.view.challenges.DailyChallengeActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements ProgrammingLanguagesAdapter.OnLanguageSelectedListener {
    private RecyclerView recyclerView;
    private ProgrammingLanguagesAdapter adapter;
    private ProgrammingLanguage selectedLanguage;
    private ProgressBar progressBar;

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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        //TODO:
        //  -Cargar lenguajes
        //  -Cargar lenguajes en curso
        //  -Si hay lenguajes en curso mostrarlos
        //  -Si no hay lenguajes en curso cambiar el textView a lenguajes recomendados y mostrar el siguiente recycler view
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        ArrayList<ProgrammingLanguage> programmingLanguagesList = new ArrayList<>();
        programmingLanguagesList.add(new ProgrammingLanguage("Java","", R.drawable.logo_java));
        programmingLanguagesList.add(new ProgrammingLanguage("Python","", R.drawable.logo_python));
        programmingLanguagesList.add(new ProgrammingLanguage("C++","", R.drawable.logo_cpp));
        programmingLanguagesList.add(new ProgrammingLanguage("HTML","", R.drawable.logo_html));
        programmingLanguagesList.add(new ProgrammingLanguage("C","", R.drawable.logo_c));
        programmingLanguagesList.add(new ProgrammingLanguage("JavaScript","", R.drawable.logo_js));
        programmingLanguagesList.add(new ProgrammingLanguage("C#","", R.drawable.logo_csh));
        adapter = new ProgrammingLanguagesAdapter(programmingLanguagesList, ProgrammingLanguagesAdapter.ViewType.HOME_VIEW_TYPE, this);
        recyclerView.setAdapter(adapter);

        Button dailyChallenge = rootView.findViewById(R.id.start_home_button);
        dailyChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DailyChallengeActivity.class);
                startActivity(intent);
            }
        });

        progressBar = rootView.findViewById(R.id.home_progressBar);
        progressBar.bringToFront();

        Button todosLosLenguajes = rootView.findViewById(R.id.allLanguages_home_button);
        todosLosLenguajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
            }
        });
        return rootView;
    }


    @Override
    public void onLanguageSelected(ProgrammingLanguage language) {
        ImageView selectedLanguageImage = getView().findViewById(R.id.selectedLanguage_home_imageView);
        this.selectedLanguage = language;
        selectedLanguageImage.setImageResource(language.getImageResourceId());
    }
}