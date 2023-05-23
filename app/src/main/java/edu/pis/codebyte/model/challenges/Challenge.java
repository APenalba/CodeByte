package edu.pis.codebyte.model.challenges;

public abstract class Challenge {
    private String question;
    private String correct_answer;
    Challenge(String question, String correct_answer)  {
        this.question = question;
        this.correct_answer = correct_answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrect_answer() {
        return correct_answer;
    }
}
