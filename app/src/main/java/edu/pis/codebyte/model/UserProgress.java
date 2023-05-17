package edu.pis.codebyte.model;

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


}
