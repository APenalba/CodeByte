package edu.pis.codebyte.view.challenges;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import edu.pis.codebyte.R;

public class FillTheGapChallengeFragment extends Fragment {

    private TextView textView;
    private EditText editText;
    private Button solveButton;

    private OnQuestionAnsweredListener onQuestionAnsweredListener;

    private String question;
    private String correctAnswer;

    public FillTheGapChallengeFragment() {
        // Constructor vacío requerido
    }

    public static FillTheGapChallengeFragment newInstance(String question, String correctAnswer) {
        FillTheGapChallengeFragment fragment = new FillTheGapChallengeFragment();
        Bundle args = new Bundle();
        args.putString("question", question);
        args.putString("correctAnswer", correctAnswer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = getArguments().getString("question");
            correctAnswer = getArguments().getString("correctAnswer");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fill_the_gap_challenge, container, false);

        textView = view.findViewById(R.id.textView);
        editText = view.findViewById(R.id.editText);
        solveButton = view.findViewById(R.id.solve_fillTheGapsChallengeFragment_button);

        textView.setText(question);

        solveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solve();
            }
        });

        return view;
    }

    private void solve() {
        String userAnswer = editText.getText().toString().trim();
        boolean isCorrect = userAnswer.equalsIgnoreCase(correctAnswer);

        String message;
        if (isCorrect) {
            message = "Respuesta correcta";
        } else {
            message = "Respuesta incorrecta. La respuesta correcta es: " + correctAnswer;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (onQuestionAnsweredListener != null) {
                            onQuestionAnsweredListener.onQuestionAnswered(isCorrect);
                        }
                    }
                });
        builder.create().show();
    }
    public void setOnQuestionAnsweredListener(OnQuestionAnsweredListener onQuestionAnsweredListener) {
        this.onQuestionAnsweredListener = onQuestionAnsweredListener;
    }
}
