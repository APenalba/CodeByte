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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import edu.pis.codebyte.R;
import edu.pis.codebyte.model.LoginUtils;
import edu.pis.codebyte.model.exceptions.InvalidEmailException;
import edu.pis.codebyte.model.exceptions.WeakPasswordException;
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

        enviarProblema_setup();
        idioma = findViewById(R.id.spinner_idiomas);
    }

    /**
     * Este metodo inicializa el textView que muestra el email
     */
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

    /**
     * Este metodo inicializa el textView que muestra el username
     */
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

    /**
     * Este metodo inicializa el boton para cambiar el username del usuario
     */
    private void cambiarUsername_button_setup() {
        Button cambiarUsername_button = findViewById(R.id.btn_username);
        cambiarUsername_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarUsername();
            }
        });
    }

    /**
     * Este metodo inicializa ele imageView de la foto de perfil del usuario
     */
    private void cambiarProfileImage_button_setup() {
        pfp = findViewById(R.id.profile_picture);
        pfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarUserImage();
            }
        });
        final Observer<String> observerImage = new Observer<String>() {
            @Override
            public void onChanged(String imageURL) {
                if(!imageURL.isEmpty()) Picasso.get().load(imageURL)
                            .transform(new CropCircleTransformation())
                            .into(pfp);
                else {

               }
            }
        };
        profileVM.getImageURL().observe(this, observerImage);
    }

    /**
     * Este metodo carga el provider del usuario
     */
    private void getUserProvider() {
        final Observer<String> observerProvider = new Observer<String>() {
            @Override
            public void onChanged(String newUserProvider) {
                userProvider = newUserProvider;
                // Llamo aqui a estos metodos porque para la correcta ejecución necesitan saber el
                // userProvider ya que este determinara si los botones deben funcionar o no
                cambiarPassword_button_setup();
                cambiarCorreo_button_setup();
            }
        };

        profileVM.getUserProvider().observe(this,observerProvider);
    }

    /**
     * Inicializa el boton para cambiar la contraseña
     */
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

    /**
     * Inicializa el boton para enviar un problema o comentario
     */
    private void enviarProblema_setup() {
        enviaProblema_button = findViewById(R.id.btn_enviaProblema);
        problema = findViewById(R.id.editText_problem);
        enviaProblema_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contenidoComentario = problema.getText().toString();
                profileVM.enviarComentario(contenidoComentario, ProfileActivity.this);
                problema.setText("");
            }
        });
    }

    /**
     * Inicializa el boton para cambiar el correo del usuario
     */
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

    /**
     * Este metodo abre unintent para que el usuario pueda cambiar su username
     */
    private void cambiarUsername() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.change_username_dialog, null);
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

    /**
     * Este metodo inicializa un fragment para que el usuario pueda cambiar la contraseña
     */
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

                try {
                    LoginUtils.isSecurePassword(new_password);
                    profileVM.cambiarContrasena(current_password, new_password, ProfileActivity.this);
                } catch (WeakPasswordException e) {
                    Toast.makeText(ProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.cancelar, null);
        builder.show();
    }

    /**
     * Este metodo inicializa un fragment para que el usuario pueda cambiar el email
     */
    private void cambiarCorreo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.change_email_dialog, null);
        builder.setView(view);
        builder.setTitle("Cambiar correo");

        EditText newEmailEditText = view.findViewById(R.id.new_email_editText);
        EditText currentPasswordEditText = view.findViewById(R.id.password_editText);

        builder.setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newEmail = newEmailEditText.getText().toString().trim();
                String currentPassword = currentPasswordEditText.getText().toString().trim();
                try {
                    LoginUtils.isValidEmail(newEmail);
                } catch (InvalidEmailException e) {
                    Toast.makeText(ProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                profileVM.cambiarCorreoElectronico(newEmail, currentPassword, ProfileActivity.this);
            }
        });
        builder.setNegativeButton(R.string.cancelar, null);
        builder.show();
    }

    /**
     * Este metodo abre un intent para que el usuario pueda cambiar su imagen
     */
    private void cambiarUserImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Este metodo espera a que el usuario seleccione una imagen de su dispositivo
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            profileVM.cambiarImagenPerfil(mImageUri, ProfileActivity.this);
        }
    }
}