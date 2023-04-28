package edu.pis.codebyte.view.allLanguages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.pis.codebyte.R;
import edu.pis.codebyte.model.ProgrammingLanguage;
import edu.pis.codebyte.model.ProgrammingLanguagesAdapter;


public class AllLanguagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgrammingLanguagesAdapter adapter;

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
        recyclerView = rootView.findViewById(R.id.allLanguage_fragment_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        ArrayList<ProgrammingLanguage> programmingLanguagesList = new ArrayList<>();
        programmingLanguagesList.add(new ProgrammingLanguage("Java","", R.drawable.logo_java));
        programmingLanguagesList.add(new ProgrammingLanguage("Python","", R.drawable.logo_python));
        programmingLanguagesList.add(new ProgrammingLanguage("C++","", R.drawable.logo_cpp));
        programmingLanguagesList.add(new ProgrammingLanguage("HTML","", R.drawable.logo_html));
        programmingLanguagesList.add(new ProgrammingLanguage("C","", R.drawable.logo_c));
        programmingLanguagesList.add(new ProgrammingLanguage("JavaScript","", R.drawable.logo_js));
        programmingLanguagesList.add(new ProgrammingLanguage("C#","", R.drawable.logo_csh));

        adapter = new ProgrammingLanguagesAdapter(programmingLanguagesList, ProgrammingLanguagesAdapter.ViewType.ALL_LANGUAGES_VIEW_TYPE);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(programmingLanguagesList.size() / 2);

        return rootView;
    }
}