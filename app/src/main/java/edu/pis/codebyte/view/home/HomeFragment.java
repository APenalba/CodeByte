package edu.pis.codebyte.view.home;

import android.annotation.SuppressLint;
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
import edu.pis.codebyte.model.RecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private ProgrammingLanguagesAdapter adapter;
    private ArrayList<RecyclerViewAdapter.MyData> mData;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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


        adapter = new ProgrammingLanguagesAdapter(programmingLanguagesList);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(programmingLanguagesList.size() / 2);

        return rootView;
    }
}