package edu.pis.codebyte.viewmodel.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Random;

import edu.pis.codebyte.R;

public class ProgrammingLanguagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final OnLanguageSelectedListener languageSelectedListener;
    private ArrayList<Hashtable<String, String>> allProgrammingLanguageList;
    private ArrayList<String> currentUserLanguages;
    private Hashtable<String, String> selectedLanguage;
    private final ViewType viewType;
    public enum ViewType { HOME_VIEW_TYPE, ALL_LANGUAGES_VIEW_TYPE; }

    public interface OnLanguageSelectedListener { void onLanguageSelected(Hashtable<String, String> language); }

    public static class HomeRecyclerViewViewHolder extends RecyclerView.ViewHolder{

        public ImageView languageImage;
        public HomeRecyclerViewViewHolder(View itemView) {
            super(itemView);
            languageImage = itemView.findViewById(R.id.languageImage);
        }
    }
    public static class AllLanguagesRecyclerViewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView image;
        public TextView progress;
        public TextView description;
        public TextView name;
        public LinearProgressIndicator progressBar;
        public AllLanguagesRecyclerViewViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.languageName_allLanguagesFragment_textView);
            image = itemView.findViewById(R.id.languageImage_allLanguagesFragment_imageView);
            progress = itemView.findViewById(R.id.languageProgress_allLanguagesFragment_textView);
            description = itemView.findViewById(R.id.languageDescription_allLanguagesFragment_textView);
            progressBar = itemView.findViewById(R.id.titleBar_allLanguagesFragment);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // TODO: Handle item click
        }

    }
    public ProgrammingLanguagesAdapter(OnLanguageSelectedListener languageSelectedListener, ArrayList<Hashtable<String, String>> languages) {
        allProgrammingLanguageList = languages;
        if (allProgrammingLanguageList == null) allProgrammingLanguageList = this.getDefaultLanguagesList();

        this.viewType = ViewType.ALL_LANGUAGES_VIEW_TYPE;
        this.languageSelectedListener = languageSelectedListener;
    }

    public ProgrammingLanguagesAdapter(OnLanguageSelectedListener languageSelectedListener, ArrayList<String> currentUserLanguages, ArrayList<Hashtable<String, String>> languages) {
        allProgrammingLanguageList = languages;
        if (allProgrammingLanguageList == null) allProgrammingLanguageList = this.getDefaultLanguagesList();
        this.currentUserLanguages = currentUserLanguages;


        for (Hashtable<String, String> hashtable : allProgrammingLanguageList) {
            if (hashtable.get("name").equals(currentUserLanguages.get(0))) {
                selectedLanguage = hashtable;
                break;
            }
        }
        if (selectedLanguage == null) selectedLanguage = allProgrammingLanguageList.get(0);
        this.viewType = ViewType.HOME_VIEW_TYPE;
        this.languageSelectedListener = languageSelectedListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int param_viewType) {
        View view;
        switch (viewType) {
            case HOME_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recyclerview_layout, parent, false);
                return new HomeRecyclerViewViewHolder(view);
            case ALL_LANGUAGES_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_languages_card_layout, parent, false);
                return new AllLanguagesRecyclerViewViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Hashtable<String, String> programmingLanguage = null;
        switch (viewType) {
            case HOME_VIEW_TYPE:
                String name = currentUserLanguages.get(position);
                for (Hashtable<String, String> hashtable : allProgrammingLanguageList) {
                    if (hashtable.get("name").equals(name)) {
                        programmingLanguage = hashtable;
                        break;
                    }
                }
                if (programmingLanguage == null) programmingLanguage = allProgrammingLanguageList.get(position % allProgrammingLanguageList.size());
                languageSelectedListener.onLanguageSelected(programmingLanguage);
                HomeRecyclerViewViewHolder homeRecyclerViewViewHolderHolder = (HomeRecyclerViewViewHolder) holder;
                homeRecyclerViewViewHolderHolder.languageImage.setImageResource(Integer.parseInt(Objects.requireNonNull(programmingLanguage.get("imageResourceId"))));
                homeRecyclerViewViewHolderHolder.languageImage.setTag(programmingLanguage.get("name"));

                Hashtable<String, String> finalProgrammingLanguage = programmingLanguage;
                homeRecyclerViewViewHolderHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedLanguage = finalProgrammingLanguage;
                        notifyDataSetChanged();
                        languageSelectedListener.onLanguageSelected(selectedLanguage);
                    }
                });
                if (Objects.equals(programmingLanguage.get("name"), selectedLanguage.get("name"))) {
                    homeRecyclerViewViewHolderHolder.itemView.setBackgroundColor(homeRecyclerViewViewHolderHolder.itemView.getContext().getResources().getColor(R.color.selectedLanguageBackground));
                } else {
                    homeRecyclerViewViewHolderHolder.itemView.setBackgroundColor(homeRecyclerViewViewHolderHolder.itemView.getContext().getResources().getColor(android.R.color.transparent));
                }
                break;

            case ALL_LANGUAGES_VIEW_TYPE:
                AllLanguagesRecyclerViewViewHolder allLanguagesHolder = (AllLanguagesRecyclerViewViewHolder) holder;
                programmingLanguage = allProgrammingLanguageList.get(position % allProgrammingLanguageList.size());
                allLanguagesHolder.image.setImageResource(Integer.parseInt(Objects.requireNonNull(programmingLanguage.get("imageResourceId"))));
                allLanguagesHolder.name.setText(programmingLanguage.get("name"));

                int randNumber = new Random().nextInt(101);
                allLanguagesHolder.progressBar.setProgressCompat(randNumber, true);
                allLanguagesHolder.progress.setText(String.format("%s%%", Integer.toString(randNumber)));
                allLanguagesHolder.description.setText(programmingLanguage.get("description"));


                Hashtable<String, String> finalProgrammingLanguage1 = programmingLanguage;
                allLanguagesHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedLanguage = finalProgrammingLanguage1;
                        languageSelectedListener.onLanguageSelected(selectedLanguage);
                    }
                });

                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        switch (viewType) {
            case HOME_VIEW_TYPE:
                return currentUserLanguages.size();
            case ALL_LANGUAGES_VIEW_TYPE:
                return allProgrammingLanguageList.size();
            default:
                return 0;
        }
        //return Integer.MAX_VALUE;
    }

    private ArrayList<Hashtable<String, String>> getDefaultLanguagesList() {
        ArrayList<Hashtable<String, String>> languageList = new ArrayList<>();
        Hashtable<String, String> java = new Hashtable<>();
        java.put("name", "Java");
        java.put("imageResourceId", String.valueOf(R.drawable.logo_java));
        java.put("description", "    ");
        languageList.add(java);

        Hashtable<String, String> python = new Hashtable<>();
        python.put("name", "Python");
        python.put("imageResourceId", String.valueOf(R.drawable.logo_python));
        python.put("description", "    ");
        languageList.add(python);

        Hashtable<String, String> cpp = new Hashtable<>();
        cpp.put("name", "C++");
        cpp.put("imageResourceId", String.valueOf(R.drawable.logo_cpp));
        cpp.put("description", "    ");
        languageList.add(cpp);

        Hashtable<String, String> c = new Hashtable<>();
        c.put("name", "C");
        c.put("imageResourceId", String.valueOf(R.drawable.logo_c));
        c.put("description", "    ");
        languageList.add(c);

        Hashtable<String, String> js = new Hashtable<>();
        js.put("name", "JavaScript");
        js.put("imageResourceId", String.valueOf(R.drawable.logo_js));
        js.put("description", "    ");
        languageList.add(js);

        Hashtable<String, String> csh = new Hashtable<>();
        csh.put("name", "C#");
        csh.put("imageResourceId", String.valueOf(R.drawable.logo_csh));
        csh.put("description", "    ");
        languageList.add(csh);

        return languageList;
    }
}
