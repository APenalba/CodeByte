package edu.pis.codebyte.model.challenges;

import java.util.ArrayList;

public class MultichoiceChallenge extends Challenge{

    private ArrayList<String> choices;

    public MultichoiceChallenge(String question, String correct_answer, ArrayList<String> choices) {
        super(question, correct_answer);
        this.choices = choices;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }
}
