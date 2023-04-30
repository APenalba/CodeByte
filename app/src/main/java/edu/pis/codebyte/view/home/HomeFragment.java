package edu.pis.codebyte.view.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.pis.codebyte.R;
import edu.pis.codebyte.model.ProgrammingLanguage;
import edu.pis.codebyte.model.ProgrammingLanguagesAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgrammingLanguagesAdapter adapter;
    private Button btn_start;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflar la vista del fragmento
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


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


        adapter = new ProgrammingLanguagesAdapter(programmingLanguagesList, ProgrammingLanguagesAdapter.ViewType.HOME_VIEW_TYPE);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(programmingLanguagesList.size() / 2);

        return rootView;
    }


}