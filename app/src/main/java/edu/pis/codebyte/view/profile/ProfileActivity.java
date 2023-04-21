package edu.pis.codebyte.view.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import edu.pis.codebyte.R;
import edu.pis.codebyte.viewmodel.profile.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {

    private TextView username_textView;
    private TextView email_textView;
    private Button enviaProblema_button;
    private EditText problema;
    private Spinner idioma;
    private ImageView pfp;
    private ProfileViewModel profileVM;
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

        username_textView_setup();
        email_textView_setup();
        cambiarNombre_button_setup();
        cambiarPassword_button_setup();
        cambiarCorreo_button_setup();
        cambiarProfileImage_button_setup();

        enviaProblema_button = findViewById(R.id.btn_enviaProblema);

        problema = findViewById(R.id.editText_problem);

        idioma = findViewById(R.id.spinner_idiomas);

        pfp = findViewById(R.id.profile_picture);

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

    private void cambiarNombre_button_setup() {
        Button cambiarNombre_button = findViewById(R.id.btn_nombre);
        cambiarNombre_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogo("Cambiar nombre de usuario");
            }
        });
    }

    private void cambiarPassword_button_setup() {
        Button cambiarContraseña_button = findViewById(R.id.btn_contrasena);
        cambiarContraseña_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO cambiar contraseña

            }
        });
    }

    private void cambiarCorreo_button_setup() {
        Button cambiarCorreo_button = findViewById(R.id.btn_correo);
        cambiarCorreo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO cambiar correo

            }
        });
    }

    private void cambiarProfileImage_button_setup() {
        Button chooseImageButton = findViewById(R.id.logo_add_photo_asset);
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        final Observer<String> observerImage = new Observer<String>() {
            @Override
            public void onChanged(String imageURL) {
                Picasso.get().load(imageURL).into(pfp);
            }
        };
        profileVM.getImageURL().observe(this, observerImage);
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
            profileVM.uploadImage(ProfileActivity.this, mImageUri);
        }
    }

    private void mostrarDialogo(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.input_edittext_dialog, null);
        builder.setView(view);
        builder.setTitle(title);
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