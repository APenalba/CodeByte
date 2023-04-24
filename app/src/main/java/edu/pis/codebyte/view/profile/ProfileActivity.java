package edu.pis.codebyte.view.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import edu.pis.codebyte.R;
import edu.pis.codebyte.viewmodel.profile.ProfileViewModel;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class ProfileActivity extends AppCompatActivity {

    private TextView username_textView;
    private TextView email_textView;
    private Button enviaProblema_button;
    private EditText problema;
    private Spinner idioma;
    private ImageView pfp;
    private ProfileViewModel profileVM;
    private String userProvider;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileVM = new ViewModelProvider(this).get(ProfileViewModel.class);
        activity_setup();
    }

    private void activity_setup() {

        getUserProvider();
        username_textView_setup();
        email_textView_setup();
        cambiarProfileImage_button_setup();
        cambiarUsername_button_setup();

        enviaProblema_button = findViewById(R.id.btn_enviaProblema);
        problema = findViewById(R.id.editText_problem);
        idioma = findViewById(R.id.spinner_idiomas);
    }

    private void getUserProvider() {
        profileVM.getCurrentUserProvider(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Crear objeto User con los datos del usuario de Firestore
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String provider = document.getString("provider");
                        userProvider = provider;
                        cambiarPassword_button_setup();
                        cambiarCorreo_button_setup();
                    }
                } else {
                    // Error al obtener datos del usuario de Firestore
                }
            }
        });
    }

    private void email_textView_setup() {
        email_textView = findViewById(R.id.correo_textView);
        final Observer<String> observerEmail = new Observer<String>() {
            @Override
            public void onChanged(String email) {
                email_textView.setText(email);
            }
        };
        profileVM.getEmail().observe(this, observerEmail);
    }

    private void username_textView_setup() {
        username_textView = findViewById(R.id.username_textView);
        final Observer<String> observerUsername = new Observer<String>() {
            @Override
            public void onChanged(String username) {
                username_textView.setText(username);
            }
        };
        profileVM.getUsername().observe(this, observerUsername);

    }

    private void cambiarUsername_button_setup() {
        Button cambiarUsername_button = findViewById(R.id.btn_username);
        cambiarUsername_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarUsername();
            }
        });
    }

    private void cambiarProfileImage_button_setup() {
        pfp = findViewById(R.id.profile_picture);
        pfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        final Observer<String> observerImage = new Observer<String>() {
            @Override
            public void onChanged(String imageURL) {
                if(!imageURL.isEmpty()) Picasso.get().load(imageURL)
                            .transform(new CropCircleTransformation())
                            .into(pfp);
            }
        };
        profileVM.getImageURL().observe(this, observerImage);
    }

    private void cambiarPassword_button_setup() {

        Button cambiarContraseña_button = findViewById(R.id.btn_contrasena);
        if (userProvider.equals("google.com") || userProvider.equals("github.com")) {
            cambiarContraseña_button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D6BEF6")));
        }
        cambiarContraseña_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userProvider.equals("google.com") || userProvider.equals("github.com")) {
                    Toast.makeText(getApplicationContext(), "Has iniciado sesion con Google o " +
                            "Github, no puedes cambiar la contraseña de la cuenta desde aqui", Toast.LENGTH_SHORT).show();
                } else {
                    cambiarContraseña();
                }
            }
        });
    }

    private void cambiarCorreo_button_setup() {
        Button cambiarEmail_button = findViewById(R.id.btn_correo);
        if (userProvider.equals("google.com") || userProvider.equals("github.com")) {
            cambiarEmail_button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D6BEF6")));
        }
        cambiarEmail_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userProvider.equals("google.com") || userProvider.equals("github.com")) {
                    Toast.makeText(getApplicationContext(), "Has iniciado sesion con Google o " +
                            "Github, no puedes cambiar el email de la cuenta desde aqui", Toast.LENGTH_SHORT).show();
                } else {
                    cambiarCorreo();
                }
            }
        });

        }

    private void cambiarContraseña() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.change_password_dialog, null);
        builder.setView(view);
        builder.setTitle("Cambiar contraseña");
        builder.setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText current_password_editText = view.findViewById(R.id.current_password_editText);
                EditText new_password_editText = view.findViewById(R.id.new_password_editText);

                String current_password = current_password_editText.getText().toString().trim();
                String new_password = new_password_editText.getText().toString().trim();

                if(!current_password.isEmpty() && !new_password.isEmpty()) {
                    profileVM.cambiarContrasena(current_password, new_password, ProfileActivity.this);
                }
            }
        });
        builder.setNegativeButton(R.string.cancelar, null);
        builder.show();
    }

    private void cambiarCorreo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.input_email_password_dialog, null);
        builder.setView(view);
        builder.setTitle("Cambiar correo");

        EditText newEmailEditText = view.findViewById(R.id.new_email_editText);
        EditText currentPasswordEditText = view.findViewById(R.id.password_editText);

        builder.setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newEmail = newEmailEditText.getText().toString().trim();
                String currentPassword = currentPasswordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(newEmail)) {
                    Toast.makeText(ProfileActivity.this, R.string.error_empty_email, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(currentPassword)) {
                    Toast.makeText(ProfileActivity.this, R.string.error_empty_password, Toast.LENGTH_SHORT).show();
                    return;
                }

                profileVM.cambiarCorreoElectronico(newEmail, currentPassword, ProfileActivity.this);
            }
        });
        builder.setNegativeButton(R.string.cancelar, null);
        builder.show();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            profileVM.cambiarImagenPerfil(mImageUri, ProfileActivity.this);
        }
    }

    private void cambiarUsername() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.input_edittext_dialog, null);
        builder.setView(view);
        builder.setTitle("Cambiar nombre de usuario");
        builder.setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText input_editText = view.findViewById(R.id.new_editText);
                String new_input = input_editText.getText().toString().trim();
                if(!new_input.isEmpty()) {
                    profileVM.cambiarNombreUsuario(new_input,ProfileActivity.this);
                }
            }
        });
        builder.setNegativeButton(R.string.cancelar, null);
        builder.show();
    }
}