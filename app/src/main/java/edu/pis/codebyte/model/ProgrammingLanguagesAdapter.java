package edu.pis.codebyte.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Random;

import edu.pis.codebyte.R;

public class ProgrammingLanguagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ProgrammingLanguage> programmingLanguagesList;
    private ProgrammingLanguage selectedLanguage;

    public enum ViewType {
        HOME_VIEW_TYPE,
        ALL_LANGUAGES_VIEW_TYPE;
    }

    private final ViewType viewType;
    private static final int HOME_VIEW_TYPE = 0;
    private static final int ALL_LANGUAGES_VIEW_TYPE = 1;

    public static class HomeRecyclerViewViewHolder extends RecyclerView.ViewHolder {
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
            progressBar = itemView.findViewById(R.id.languageProgress_allLanguagesFragment_progressBar);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // TODO: Handle item click
        }
    }

    public ProgrammingLanguagesAdapter(ArrayList<ProgrammingLanguage> programmingLanguagesList, ViewType viewType) {
        this.programmingLanguagesList = programmingLanguagesList;
        selectedLanguage = programmingLanguagesList.get(0);
        this.viewType = viewType;
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
        switch (viewType) {
            case HOME_VIEW_TYPE:
                HomeRecyclerViewViewHolder homeHolder = (HomeRecyclerViewViewHolder) holder;
                ProgrammingLanguage programmingLanguageHome = programmingLanguagesList.get(position % programmingLanguagesList.size());
                homeHolder.languageImage.setImageResource(programmingLanguageHome.getImageResourceId());

                homeHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedLanguage = programmingLanguageHome;
                        notifyDataSetChanged();
                    }
                });

                if (programmingLanguageHome == selectedLanguage) {
                    homeHolder.itemView.setBackgroundColor(homeHolder.itemView.getContext().getResources().getColor(R.color.selectedLanguageBackground));
                } else {
                    homeHolder.itemView.setBackgroundColor(homeHolder.itemView.getContext().getResources().getColor(android.R.color.transparent));
                }
                break;
            case ALL_LANGUAGES_VIEW_TYPE:
                AllLanguagesRecyclerViewViewHolder allLanguagesHolder = (AllLanguagesRecyclerViewViewHolder) holder;
                ProgrammingLanguage programmingLanguageAllLanguages = programmingLanguagesList.get(position % programmingLanguagesList.size());
                allLanguagesHolder.image.setImageResource(programmingLanguageAllLanguages.getImageResourceId());
                allLanguagesHolder.name.setText(programmingLanguageAllLanguages.getName());
                int randNumber = new Random().nextInt(101);
                allLanguagesHolder.progressBar.setProgressCompat(randNumber, true);
                allLanguagesHolder.progress.setText(Integer.toString(randNumber) + "%");
                allLanguagesHolder.description.setText(programmingLanguageAllLanguages.getDescription());

                allLanguagesHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedLanguage = programmingLanguageAllLanguages;
                        notifyDataSetChanged();
                    }
                });

                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return programmingLanguagesList.size();
        //return Integer.MAX_VALUE;
    }

    public ProgrammingLanguage getSelectedLanguage() {
        return selectedLanguage;
    }
}
