package edu.pis.codebyte.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;

import edu.pis.codebyte.R;

public class ProgrammingLanguage {

    private String name;
    private String description;
    private ArrayList<Course> courses;
    private HashSet<String> tags;
    private int imageResourceId;

    public ProgrammingLanguage(String name, String description, ArrayList<Course> courses, HashSet<String> tags, int imageResourceId) {
        this.name = name;
        this.description = description;
        this.courses = courses;
        this.tags = tags;
        if (imageResourceId == 0) this.imageResourceId = R.drawable.logo__256;
        else this.imageResourceId = imageResourceId;
    }

    public ProgrammingLanguage(String name, String description, ArrayList<Course> courses, int imageResourceId) {
        this.name = name;
        this.description = description;
        this.courses = courses;
        this.tags = new HashSet<>();
        this.imageResourceId = imageResourceId;
    }

    public ProgrammingLanguage(String name, String description, HashSet<String> tags, int imageResourceId) {
        this.name = name;
        this.description = description;
        this.courses = new ArrayList<>();
        this.tags = tags;
        this.imageResourceId = imageResourceId;
    }

    public ProgrammingLanguage(String name, String description, int imageResourceId) {
        this.name = name;
        this.description = description;
        this.courses = new ArrayList<>();
        this.tags = new HashSet<>();
        this.imageResourceId = imageResourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }

    public HashSet<String> getTags() {
        return tags;
    }

    public void setTags(HashSet<String> tags) {
        this.tags = tags;
    }

    public void addCourse(Course course) {
        this.courses.add(course);
    }

    public void removeCourse(Course course) {
        this.courses.remove(course);
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public void removeTag(String tag){
        this.tags.remove(tag);
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
    public int getImageResourceId() {
        if (this.imageResourceId == 0) return R.drawable.logo__256;
        return this.imageResourceId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj == this) {
            return true;
        }

        ProgrammingLanguage lp = obj instanceof ProgrammingLanguage ? ((ProgrammingLanguage) obj) : null;
        if (lp == null) return false;
        return this.name.equals(lp.name);
    }

    @NonNull
    @Override
    public String toString() {
        return this.name + ":\n" + this.description;
    }
}
