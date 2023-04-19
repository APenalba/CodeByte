package edu.pis.codebyte.view.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

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
                mostrarDialogoCambiarNombreUsuario();
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
    private void mostrarDialogoCambiarNombreUsuario() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.nuevo_nombre_usuario_dialog, null);
        builder.setView(view);
        builder.setTitle(R.string.cambiar_nombre_usuario_titulo);
        builder.setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText etNuevoNombreUsuario = view.findViewById(R.id.et_nuevo_nombre_usuario);
                String nuevoNombreUsuario = etNuevoNombreUsuario.getText().toString().trim();
                cambiarNombreUsuario(nuevoNombreUsuario);

            }
        });
        builder.setNegativeButton(R.string.cancelar, null);
        builder.show();
    }

    private void cambiarNombreUsuario(String nuevoNombreUsuario) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nuevoNombreUsuario)
                .build();

        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // El nombre de usuario se ha actualizado correctamente
                            Toast.makeText(ProfileActivity.this, "Nombre de usuario actualizado", Toast.LENGTH_SHORT).show();
                        } else {
                            // Se produjo un error al actualizar el nombre de usuario
                            Toast.makeText(ProfileActivity.this, "Error al actualizar el nombre de usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}