package edu.pis.codebyte.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.pis.codebyte.R;

public class ProgrammingLanguagesAdapter extends RecyclerView.Adapter<ProgrammingLanguagesAdapter.ViewHolder> {

    private ArrayList<ProgrammingLanguage> programmingLanguagesList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView languageImage;

        public ViewHolder(View itemView) {
            super(itemView);
            languageImage = (ImageView) itemView.findViewById(R.id.languageImage);
        }
    }

    public ProgrammingLanguagesAdapter(ArrayList<ProgrammingLanguage> programmingLanguagesList) {
        this.programmingLanguagesList = programmingLanguagesList;
    }

    @Override
    public ProgrammingLanguagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recyclerview_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProgrammingLanguagesAdapter.ViewHolder holder, int position) {

        ProgrammingLanguage programmingLanguage = programmingLanguagesList.get(position % programmingLanguagesList.size());
        holder.languageImage.setImageResource(programmingLanguage.getImageResourceId());
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }
}
