package edu.pis.codebyte.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;

public class LenguajeDeProgramacion {

    private String name;
    private String id;
    private String description;
    private ArrayList<Course> courses;
    private HashSet<String> tags;

    public LenguajeDeProgramacion(String name, String description, ArrayList<Course> courses, HashSet<String> tags) {
        this.name = name;
        this.description = description;
        this.courses = courses;
        this.tags = tags;
    }

    public LenguajeDeProgramacion(String name, String description, ArrayList<Course> courses) {
        this.name = name;
        this.description = description;
        this.courses = courses;
        this.tags = new HashSet<>();
    }

    public LenguajeDeProgramacion(String name, String description, HashSet<String> tags) {
        this.name = name;
        this.description = description;
        this.courses = new ArrayList<>();
        this.tags = tags;
    }

    public LenguajeDeProgramacion(String name, String description) {
        this.name = name;
        this.description = description;
        this.courses = new ArrayList<>();
        this.tags = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj == this) {
            return true;
        }

        LenguajeDeProgramacion lp = obj instanceof  LenguajeDeProgramacion? ((LenguajeDeProgramacion) obj) : null;
        if (lp == null) return false;
        return this.id.equals(lp.id);

    }

    @NonNull
    @Override
    public String toString() {
        return this.name + ":\n" + this.description;
    }
}
