package edu.pis.codebyte.view.challenges;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import edu.pis.codebyte.R;

public class MultichoiceChallengeFragment extends Fragment {

    private TextView questionTextView;
    private RadioGroup radioGroup;
    private Button hintButton;
    private Button solveButton;

    private String question;
    private String correctAnswer;
    private String[] responses = {"Respuesta 1", "Respuesta 2", "Respuesta 3"};
    private OnQuestionAnsweredListener onQuestionAnsweredListener;


    public static MultichoiceChallengeFragment newInstance(String question, String correctAnswer, String responses) {
        MultichoiceChallengeFragment fragment = new MultichoiceChallengeFragment();
        System.out.println("NEW INSTANCE");
        Bundle args = new Bundle();
        args.putString("question", question);
        args.putString("correctAnswer", correctAnswer);
        args.putString("responses", responses);
        fragment.setArguments(args);
        System.out.println(fragment.getArguments());
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            question = getArguments().getString("question");
            System.out.println("Question: " + question);
            correctAnswer = getArguments().getString("correctAnswer");
            System.out.println("CorrectAnswe: " + correctAnswer);
            responses = getArguments().getString("responses").split(",");
            System.out.println("Respuestas: " +responses);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_multichoice_challenge, container, false);

        System.out.println("Argumnets " + getArguments());
        if (getArguments() != null) {
            question = getArguments().getString("question");
            System.out.println("Question: " + question);
            correctAnswer = getArguments().getString("correctAnswer");
            System.out.println("CorrectAnswe: " + correctAnswer);
            responses = getArguments().getString("responses").split(",");
            System.out.println("Respuestas: " +responses);
        }

        questionTextView = rootView.findViewById(R.id.question_multichoiceChallenge_textView);
        radioGroup = rootView.findViewById(R.id.radioGroup);
        hintButton = rootView.findViewById(R.id.hint_multichoice_button);
        solveButton = rootView.findViewById(R.id.solve_multichoiceChallenge_button);
        questionTextView.setText(question);

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            radioButton.setText(responses[i]);
        }

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHint();
            }
        });

        solveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solve();
            }
        });

        return rootView;
    }

    private void setHint() {
        int childCount = radioGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            String response = radioButton.getText().toString();
            if (!response.equals(correctAnswer)) {
                radioButton.setEnabled(false);
                break; // Desactivamos solo uno de los RadioButtons incorrectos
            }
        }
    }

    private void solve() {
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        if (selectedRadioButtonId == -1) {
            // No se ha seleccionado ninguna opción
            Snackbar.make(getView(), "Debe seleccionar una opción", Snackbar.LENGTH_SHORT).show();
        } else {
            RadioButton selectedRadioButton = getView().findViewById(selectedRadioButtonId);
            String selectedResponse = selectedRadioButton.getText().toString();

            if (selectedResponse.equals(correctAnswer)) {
                // Respuesta correcta
                showResultDialog(true, null);
            } else {
                // Respuesta incorrecta
                showResultDialog(false, correctAnswer);
            }
        }
    }

    private void showResultDialog(boolean isCorrect, String correctAnswer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(isCorrect ? "Respuesta Correcta" : "Respuesta Incorrecta");

        if (!isCorrect) {
            builder.setMessage("La respuesta correcta es: " + correctAnswer);
        }

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (onQuestionAnsweredListener != null) {
                    onQuestionAnsweredListener.onQuestionAnswered(isCorrect);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setOnQuestionAnsweredListener(OnQuestionAnsweredListener onQuestionAnsweredListener) {
        this.onQuestionAnsweredListener = onQuestionAnsweredListener;
    }
}
