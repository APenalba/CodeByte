package edu.pis.codebyte.view.lesson;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import edu.pis.codebyte.R;

public class LessonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        LinearProgressIndicator progressBar = findViewById(R.id.progressBar);
        progressBar.setProgressCompat(35, true);

        TextView textView = findViewById(R.id.textView2);
        textView.setText(obtenerTextoFormateado(getResources().getString(R.string.texto_ejemplo_leccion)));
    }

    private Spanned obtenerTextoFormateado(String texto) {
        String textoFormateado = texto.replaceAll("\\*(.*?)\\*", "<strong><b>$1</b></strong>");
        textoFormateado = textoFormateado.replaceAll("\"(.*?)\"", "<i>$1</i>");
        return Html.fromHtml(textoFormateado, Html.FROM_HTML_OPTION_USE_CSS_COLORS);
    }
}