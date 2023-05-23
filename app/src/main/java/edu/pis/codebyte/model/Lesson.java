package edu.pis.codebyte.model;

import edu.pis.codebyte.model.challenges.Challenge;

public class Lesson {

    private String name;
    private String lesson;
    private Course course;
    private int challengeId;

    public Lesson(String name, String lesson, Course course, int challengeId) {
        this.name = name;
        this.lesson = lesson;
        this.course = course;
        this.challengeId = challengeId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getChallengeId() {
        return challengeId;
    }

    public void setChallenge(int challengeId) {
        this.challengeId = challengeId;
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
