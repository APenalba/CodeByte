package edu.pis.codebyte.view.main.allLanguages;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.Hashtable;

import edu.pis.codebyte.R;
import edu.pis.codebyte.view.lesson.LessonActivity;
import edu.pis.codebyte.viewmodel.main.CoursesAdapter;
import edu.pis.codebyte.viewmodel.main.MainViewModel;
import edu.pis.codebyte.viewmodel.main.ProgrammingLanguagesAdapter;


public class AllLanguagesFragment extends Fragment implements ProgrammingLanguagesAdapter.OnLanguageSelectedListener, CoursesAdapter.OnCourseSelectedListener {

    private RecyclerView recyclerView;
    private ProgrammingLanguagesAdapter adapter;
    private MainViewModel mainViewModel;
    private View rootView;
    private int lvl = 0;


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
        this.rootView = inflater.inflate(R.layout.fragment_all_languages, container, false);
        mainViewModel = MainViewModel.getInstance();

        LinearProgressIndicator titleBar = rootView.findViewById(R.id.titleBar_allLanguagesFragment);
        titleBar.setProgressCompat(86, true);
        titleBar.setIndicatorColor(getResources().getColor(R.color.black));
        titleBar.setTrackColor(getResources().getColor(R.color.grey_progressbar));;
        recyclerView_setup();
        return rootView;
    }

    private void updateRecyclerView() {
        adapter = new ProgrammingLanguagesAdapter(this, mainViewModel.getLanguages());
        recyclerView.setAdapter(adapter);
    }

    private void recyclerView_setup() {
        TextView title = rootView.findViewById(R.id.language_lessonActivity_textView);
        title.setText("Todos los lenguajes");
        LinearProgressIndicator titleBar = rootView.findViewById(R.id.titleBar_allLanguagesFragment);
        titleBar.setProgressCompat(86, true);
        titleBar.setIndicatorColor(getResources().getColor(R.color.black));
        titleBar.setTrackColor(getResources().getColor(R.color.grey_progressbar));;
        recyclerView = rootView.findViewById(R.id.allLanguage_fragment_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new ProgrammingLanguagesAdapter(this, mainViewModel.getLanguages());
        recyclerView.setAdapter(adapter);
        updateRecyclerView();
        lvl = 0;
    }

    public int getLvl() {
        return lvl;
    }

    public void onBack() {
        recyclerView_setup();
    }
    @Override
    public void onLanguageSelected(Hashtable<String, String> language) {
        TextView title = rootView.findViewById(R.id.language_lessonActivity_textView);
        title.setText(language.get("name") + " courses");
        LinearProgressIndicator titleBar = rootView.findViewById(R.id.titleBar_allLanguagesFragment);
        titleBar.setProgressCompat(100, true);
        titleBar.setIndicatorColor(getResources().getColor(R.color.black));
        titleBar.setTrackColor(getResources().getColor(R.color.grey_progressbar));;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        CoursesAdapter adapter1 = new CoursesAdapter(this, mainViewModel.getCourses(language.get("name")));
        recyclerView.setAdapter(adapter1);
        lvl = 1;
    }

    @Override
    public void onCourseSelected(Hashtable<String, String> course) {
        Intent intent = new Intent(getContext(), LessonActivity.class);
        intent.putExtra("lessons", mainViewModel.getLessons(course.get("language"), course.get("name")));
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        onStop();
    }
}