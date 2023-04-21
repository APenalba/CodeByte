package edu.pis.codebyte.model;

import edu.pis.codebyte.model.challenges.Challenge;

public class Lesson {

    private String id;
    private String name;
    private String lesson;

    private Challenge challenge;

    public Lesson(String name, String lesson, Challenge challenge) {
        this.name = name;
        this.lesson = lesson;
        this.challenge = challenge;
    }

    public Lesson(String name, String lesson) {
        this.name = name;
        this.lesson = lesson;
        this.challenge = null;
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

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }
}
