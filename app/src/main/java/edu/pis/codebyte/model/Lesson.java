package edu.pis.codebyte.model;

import edu.pis.codebyte.model.challenges.Challenge;

public class Lesson {

    private String name;
    private String lesson;
    private Course course;
    private Challenge challenge;

    public Lesson(String name, String lesson, Course course, Challenge challenge) {
        this.name = name;
        this.lesson = lesson;
        this.course = course;
        this.challenge = challenge;
    }

    public Lesson(String name, String lesson, Course course) {
        this.name = name;
        this.lesson = lesson;
        this.challenge = null;
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
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
