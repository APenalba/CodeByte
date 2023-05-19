package edu.pis.codebyte.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

public class UserProgress {

    Hashtable<String,Hashtable<String, HashSet<String>>> progress;
    private String uId;
    public UserProgress(String uId) {
        this.uId = uId;
        progress = new Hashtable<>();
    }
    public void addLanguageToProgress(String language) {
        if (!progress.containsKey(language)) {
            progress.put(language, new Hashtable<>());
        }
    }
    public void addCourseToProgress(String course, String language) {
        addLanguageToProgress(language);
        if (!progress.get(language).containsKey(course)) {
            progress.get(language).put(course, new HashSet<>());
        }
    }
    public void addLessonToProgress(String lessonName, String courseName, String language) {
        addCourseToProgress(courseName, language);
        //No hace falta comprobar si esta antes de añadirlo ya que si ya esta en el set no cambiara
        // nada. Al menos asi funcionaban los sets de python, aqui espero que tambien :D
        progress.get(language).get(courseName).add(lessonName);
    }

    public HashSet<String> getStartedProgrammingLanguages() {
        return new HashSet<>(progress.keySet());
    }

    public HashSet<String> getStartedCoursesFromLanguage(String language) {
        if (progress.get(language) == null) return new HashSet<>();
        return  new HashSet<>(progress.get(language).keySet());
    }


    public float calcProgress(ProgrammingLanguage pg) {
        float prog = 0;
        if (!progress.containsKey(pg.getName())) return 0;

        ArrayList<Course> courses = pg.getCourses();
        int coursesSize = courses.size();
        if (coursesSize == 0) return 100;
        Hashtable<String, HashSet<String>> coursesProgress = progress.get(pg.getName());
        for (Course course: pg.getCourses()) {
            if(coursesProgress.containsKey(course.getName())) {
                float courseProgress = calcProgress(course);
                System.out.println("Progreso del curso " + course.getName() +  " = " + courseProgress);
                prog += courseProgress / coursesSize;
            }
        }
        return prog;
    }

    public float calcProgress(Course course) {
        float prog = 0;
        Hashtable<String, HashSet<String>> coursesProgress = progress.get(course.getProgrammingLanguage());
        if (!coursesProgress.containsKey(course.getName())) return 0;

        int courseSize = course.getLessons().size();
        if (courseSize == 0) return 100;

        HashSet<String> lessonsProgress = coursesProgress.get(course.getName());
        if (lessonsProgress.size() == 0) return 0;

        for (Lesson lesson : course.getLessons()) {
            if (lessonsProgress.contains(lesson.getName())) {
                prog += 100 / courseSize;
            }
        }
        return prog;
    }

    public Course getLastCourse(ProgrammingLanguage language) {
        ArrayList<Course> courses = language.getCourses();
        int coursesSize = courses.size();
        if (coursesSize == 0) return null;
        if (!progress.containsKey(language.getName())) return language.getCourses().get(0);
        Hashtable<String, HashSet<String>> coursesProgress = progress.get(language.getName());
        for (Course course: courses) {
            if(this.calcProgress(course) != 100) {
                return course;
            }
        }
        return null;
    }
}
