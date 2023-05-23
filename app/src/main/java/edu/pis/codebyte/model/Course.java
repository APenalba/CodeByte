package edu.pis.codebyte.model;

import java.util.ArrayList;

import edu.pis.codebyte.model.challenges.Challenge;

public class Course {

    private String name;
    private String programmingLanguage;
    private String description;
    private ArrayList<Lesson> lessons;
    private ArrayList<Challenge> challenges;

    public Course(String name, String description, String programmingLanguage, ArrayList<Lesson> lessons, ArrayList<Challenge> challenges) {
        this.name = name;
        this.description = description;
        this.lessons = lessons;
        this.programmingLanguage = programmingLanguage;
        this.challenges = challenges;
    }

    public Course(String name, String description, String programmingLanguage) {
        this.name = name;
        this.description = description;
        this.lessons = new ArrayList<>();
        this.challenges = new ArrayList<>();
        this.programmingLanguage = programmingLanguage;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
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

    public void addChallenge(Challenge challenge, int position) {
        this.challenges.add(position, challenge);
    }
    public ArrayList<Challenge> getChallenges() {
        return this.challenges;
    }
}
