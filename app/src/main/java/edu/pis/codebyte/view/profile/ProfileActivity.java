package edu.pis.codebyte.view.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.pis.codebyte.R;
import edu.pis.codebyte.viewmodel.profile.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextView nombreUsuario;
    private TextView email;
    private Button cambiarNombre_button;
    private Button cambiarCorreo_button;
    private Button cambiarContraseña_button;
    private Button enviaProblema_button;
    private EditText problema;
    private Spinner idioma;
    private ImageView pfp;
    private ProfileViewModel profileVM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        activity_setup();

    }

    private void activity_setup() {

        nombreUsuario = (TextView) findViewById(R.id.username_textView);
        //TODO puede ser nulo el username
        nombreUsuario.setText(currentUser.getDisplayName());

        email = (TextView) findViewById(R.id.correo_textView);
        email.setText(currentUser.getEmail());

        cambiarNombre_button_setup();
        cambiarContraseña_button_setup();
        cambiarCorreo_button_setup();

        enviaProblema_button = (Button) findViewById(R.id.btn_enviaProblema);

        problema = (EditText) findViewById(R.id.editText_problem);

        idioma = (Spinner) findViewById(R.id.spinner_idiomas);

        pfp = (ImageView) findViewById(R.id.profile_picture);

    }

    private void cambiarNombre_button_setup() {
        cambiarNombre_button = (Button) findViewById(R.id.btn_nombre);
        cambiarNombre_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO cambiar nombre
            }
        });
    }

    private void cambiarContraseña_button_setup() {
        cambiarContraseña_button = (Button) findViewById(R.id.btn_contrasena);
        cambiarContraseña_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO cambiar contraseña
            }
        });
    }

    private void cambiarCorreo_button_setup() {
        cambiarCorreo_button = (Button) findViewById(R.id.btn_correo);
        cambiarCorreo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO cambiar correo
            }
        });
    }
}