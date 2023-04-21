package edu.pis.codebyte.model;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Course {

    private String id;
    private String name;
    private String description;
    private ArrayList<Lesson> lessons;

    public Course(String name, String description, ArrayList<Lesson> lessons) {
        this.name = name;
        this.description = description;
        this.lessons = lessons;
    }

    public Course(String name, String description) {
        this.name = name;
        this.description = description;
        this.lessons = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public ArrayList<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(ArrayList<Lesson> lessons) {
        this.lessons = lessons;
    }

    public void addLesson(Lesson lesson) {
        this.lessons.add(lesson);
    }

    public void removeLesson(Lesson lesson) {
        this.lessons.remove(lesson);
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj == this) {
            return true;
        }

        Course course = obj instanceof  Course? ((Course) obj) : null;
        if (course == null) return false;
        return this.id == course.id;

    }
}
