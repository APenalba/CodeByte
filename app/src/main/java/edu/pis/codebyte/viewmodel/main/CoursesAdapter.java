package edu.pis.codebyte.viewmodel.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Hashtable;

import edu.pis.codebyte.R;

public class CoursesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Hashtable<String, String>> courses;
    OnCourseSelectedListener listener;

    public interface OnCourseSelectedListener {
        void onCourseSelected(Hashtable<String, String> course);
    }

    public CoursesAdapter(OnCourseSelectedListener listener, ArrayList<Hashtable<String, String>> courses) {
        this.listener = listener;
        this.courses = courses;
    }

    public static class CoursesRecyclerViewViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView courseDescription;
        public Button button;
        public CoursesRecyclerViewViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView);
            courseDescription = itemView.findViewById(R.id.textView3);
            button = itemView.findViewById(R.id.startCourse_HomeFragment_button);

        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_courses_card_layout, parent, false);
        return new CoursesRecyclerViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Hashtable<String, String> course = courses.get(position);
        CoursesRecyclerViewViewHolder viewHolder = (CoursesRecyclerViewViewHolder) holder;
        viewHolder.courseDescription.setText(course.get("description"));
        viewHolder.title.setText(course.get("name"));
        viewHolder.button.setEnabled(Integer.parseInt(course.get("lessons")) > 0);
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCourseSelected(course);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
}
