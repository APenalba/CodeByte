package edu.pis.codebyte.view.lesson;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

import edu.pis.codebyte.R;
import edu.pis.codebyte.model.DataBaseManager;
import edu.pis.codebyte.view.challenges.FillTheGapChallengeFragment;
import edu.pis.codebyte.view.challenges.MultichoiceChallengeFragment;
import edu.pis.codebyte.view.challenges.OnQuestionAnsweredListener;
import edu.pis.codebyte.view.main.MainActivity;

public class LessonActivity extends AppCompatActivity implements OnQuestionAnsweredListener {
    private int cont;
    private ArrayList<HashMap<String, String>> lessons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        lessons = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("lessons");
        cont = 0;

        LinearProgressIndicator progressBar = findViewById(R.id.languageProgress_lessonActivity_progressBar);
        progressBar.setProgressCompat(32, true);
        progressBar.setIndicatorColor(getResources().getColor(R.color.purple_codebyte));
        progressBar.setTrackColor(getResources().getColor(R.color.lightpurple_codebyte));
        TextView textView = findViewById(R.id.course_lessonActivity_textview);
        updateLesson();

    }

    private Spanned obtenerTextoFormateado(String texto) {
        String textoFormateado = texto.replaceAll("\\*(.*?)\\*", "<b>$1</b>");
        textoFormateado = textoFormateado.replaceAll("\"(.*?)\"", "<i>$1</i>");
        return Html.fromHtml(textoFormateado, Html.FROM_HTML_OPTION_USE_CSS_COLORS);
    }

    @Override
    public void onQuestionAnswered(boolean isCorrect) {
        if (isCorrect){
            DataBaseManager dataBaseManager = DataBaseManager.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            dataBaseManager.registrarLeccionEnProgresso(user.getUid(), lessons.get(cont).get("name"), lessons.get(cont).get("course"), lessons.get(cont).get("language"));
        }
        if( cont == lessons.size()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle("Curso completado")
                    .setMessage("¡Felicidades! Has finalizado el curso de manera exitosa.")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Acciones adicionales al hacer clic en "Aceptar"
                            dialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LessonActivity.this).toBundle());
                            BottomNavigationView navigation = findViewById(R.id.navigation);;
                            navigation.setSelectedItemId(R.id.navigation_bar_lenguajes);
                            onStop();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
        cont += 1;
        updateLesson();
    }

    private void updateLesson() {
        TextView textView = findViewById(R.id.course_lessonActivity_textview);
        textView.setText(lessons.get(cont).get("course"));
        textView = findViewById(R.id.language_lessonActivity_textView);
        textView.setText(lessons.get(cont).get("language"));
        textView = findViewById(R.id.lesson_lessonActivity_textView);
        textView.setText(lessons.get(cont).get("lesson"));
        Fragment fragment = null;
        if (lessons.get(cont).get("challenge_type").equals("multichoice")) {
            fragment = MultichoiceChallengeFragment.newInstance(lessons.get(cont).get("challenge_question"), lessons.get(cont).get("correct_answer"), lessons.get(cont).get("choices"));
            ((MultichoiceChallengeFragment) fragment).setOnQuestionAnsweredListener(this);
        }else if (lessons.get(cont).get("challenge_type").equals("fill_the_gap")) {

            fragment = FillTheGapChallengeFragment.newInstance(lessons.get(cont).get("challenge_question"), lessons.get(cont).get("correct_answer"));
            ((FillTheGapChallengeFragment) fragment).setOnQuestionAnsweredListener(this);
        }

        if (!lessons.get(cont).get("challenge_type").equals("no_challenge")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.challenge_lessonActivity_fragmentContainer, fragment)
                    .commit();
        }

    }
}