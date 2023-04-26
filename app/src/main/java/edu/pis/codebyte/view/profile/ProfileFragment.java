package edu.pis.codebyte.view.profile;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import edu.pis.codebyte.R;
import edu.pis.codebyte.model.LoginUtils;
import edu.pis.codebyte.model.exceptions.InvalidEmailException;
import edu.pis.codebyte.model.exceptions.WeakPasswordException;
import edu.pis.codebyte.view.login.LoginActivity;
import edu.pis.codebyte.viewmodel.profile.ProfileViewModel;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProfileFragment extends Fragment {

    private TextView username_textView;
    private TextView email_textView;
    private Button enviaProblema_button;
    private EditText problema;
    private Spinner idioma;
    private ImageView userImage;
    private ProfileViewModel profileVM;
    private String userProvider;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileVM = new ViewModelProvider(this).get(ProfileViewModel.class);
        view_setup(view);
        return view;
    }

    private void view_setup(View view) {
        getUserProvider(view);
        username_textView_setup(view);
        email_textView_setup(view);
        userImage_imageView_setup(view);
        cambiarUsername_button_setup(view);
        problema_editText_setup(view);
        enviarProblema_button_setup(view);
        idioma = view.findViewById(R.id.spinner_idiomas);
    }

    /**
     * Este metodo inicializa el textView que muestra el nombre de usuario
     */
    private void username_textView_setup(View view) {
        username_textView = view.findViewById(R.id.username_textView);
        final Observer<String> observerUsername = new Observer<String>() {
            @Override
            public void onChanged(String username) {
                username_textView.setText(username);
            }
        };
        profileVM.getUsername().observe(getViewLifecycleOwner(), observerUsername);
    }

    /**
     * Este metodo inicializa el textView que muestra el correo electrónico
     */
    private void email_textView_setup(View view) {
        email_textView = view.findViewById(R.id.correo_textView);
        final Observer<String> observerEmail = new Observer<String>() {
            @Override
            public void onChanged(String email) {
                email_textView.setText(email);
            }
        };
        profileVM.getEmail().observe(getViewLifecycleOwner(), observerEmail);
    }

    /**
     * Este método inicializa el ImageView que muestra la imagen de perfil
     */
    private void userImage_imageView_setup(View view) {
        userImage = view.findViewById(R.id.userImage_ImageView);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        final Observer<String> observerPfp = new Observer<String>() {
            @Override
            public void onChanged(String pfpUrl) {
                if (pfpUrl != null && !pfpUrl.isEmpty()) {
                    Picasso.get().load(pfpUrl).transform(new CropCircleTransformation()).into(userImage);
                }
            }
        };
        profileVM.getImageURL().observe(getViewLifecycleOwner(), observerPfp);
    }

    /**
     * Este método inicializa el botón para cambiar el nombre de usuario
     */
    private void cambiarUsername_button_setup(View view) {
        Button cambiarUsername_button = view.findViewById(R.id.cambiarUsername_button);
        cambiarUsername_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeUsernameDialog();
            }
        });
    }

    /**
     * Inicializa el boton para cambiar el correo del usuario
     */
    private void cambiarEmail_button_setup(View view) {
        Button cambiarEmail_button = view.findViewById(R.id.btn_correo);
        if (userProvider.equals("google.com") || userProvider.equals("github.com")) {
            cambiarEmail_button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D6BEF6")));
        }
        cambiarEmail_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userProvider.equals("google.com") || userProvider.equals("github.com")) {
                    //Toast.makeText(mContext, "Has iniciado sesion con Google o " + "Github, no puedes cambiar el email de la cuenta desde aqui", Toast.LENGTH_SHORT).show();
                    Snackbar.make(v, "Has iniciado sesion con Google o " +
                            "Github, no puedes cambiar el email de la cuenta desde aqui", Snackbar.LENGTH_SHORT).show();

                } else {
                    showChangeEmailDialog();
                }
            }
        });

    }

    /**
     * Inicializa el boton para cambiar la contraseña
     */
    private void cambiarPassword_button_setup(View view) {

        Button cambiarPassword_button = view.findViewById(R.id.btn_contrasena);
        if (userProvider.equals("google.com") || userProvider.equals("github.com")) {
            cambiarPassword_button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D6BEF6")));
        }
        cambiarPassword_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userProvider.equals("google.com") || userProvider.equals("github.com")) {
                    //Toast.makeText(mContext, "Has iniciado sesion con Google o " + "Github, no puedes cambiar la contraseña de la cuenta desde aqui", Toast.LENGTH_SHORT).show();
                    Snackbar.make(v, "Has iniciado sesion con Google o " +
                            "Github, no puedes cambiar la contraseña de la cuenta desde aqui", Snackbar.LENGTH_SHORT).show();
                } else {
                    showChangePasswordDialog();
                }
            }
        });
    }

    /**
     * Este método inicializa el EditText para ingresar el problema
     */
    private void problema_editText_setup(View view) {
        problema = view.findViewById(R.id.problema_editText);
    }

    /**
     * Este método inicializa el botón para enviar el problema
     */
    private void enviarProblema_button_setup(View view) {
        enviaProblema_button = view.findViewById(R.id.enviarProblema_button);
        enviaProblema_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String problemaText = problema.getText().toString();
                if (problemaText.isEmpty()) {
                    //Toast.makeText(mContext, "Ingrese el problema que tiene", Toast.LENGTH_SHORT).show();
                    Snackbar.make(view, "Ingrese el problema que tiene", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                profileVM.enviarComentario(problemaText,mContext);
            }
        });
    }

    /**
     * Este método muestra un diálogo para cambiar el nombre de usuario
     */
    private void showChangeUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Cambiar nombre de usuario");

        // Crear el EditText para ingresar el nuevo nombre de usuario
        final EditText input = new EditText(mContext);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newUsername = input.getText().toString();
                if (newUsername.isEmpty()) {
                    //Toast.makeText(mContext, "Ingrese un nombre de usuario válido", Toast.LENGTH_SHORT).show();
                    Snackbar.make(getView(), "Ingrese un nombre de usuario válido", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                profileVM.cambiarNombreUsuario(newUsername, mContext);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Mostrar el diálogo
        builder.show();
    }

    public void showChangeEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.change_email_dialog_title);

        // Add the inputs
        final EditText inputEmail = new EditText(mContext);
        inputEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputEmail.setHint(R.string.change_email_dialog_hint);

        final EditText inputPassword = new EditText(mContext);
        inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputPassword.setHint(R.string.change_email_dialog_password_hint);

        final EditText inputConfirmPassword = new EditText(mContext);
        inputConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputConfirmPassword.setHint(R.string.change_email_dialog_confirm_password_hint);

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputEmail);
        layout.addView(inputPassword);
        layout.addView(inputConfirmPassword);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton(R.string.change_email_dialog_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle positive button click
                String newEmail = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String confirmPassword = inputConfirmPassword.getText().toString().trim();

                if (!password.equals(confirmPassword)) {
                    // Show error message if passwords do not match
                    inputConfirmPassword.setError(getString(R.string.change_email_dialog_password_mismatch_error));
                    inputConfirmPassword.requestFocus();
                    //Toast.makeText(mContext, R.string.change_email_dialog_password_mismatch_error, Toast.LENGTH_SHORT).show();
                    Snackbar.make(getView(), R.string.change_email_dialog_password_mismatch_error, Snackbar.LENGTH_SHORT).show();

                    return;
                }

                try {
                    LoginUtils.isValidEmail(newEmail);
                } catch (InvalidEmailException e) {
                    //Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    Snackbar.make(getView(), e.toString(), Snackbar.LENGTH_SHORT).show();
                }
                profileVM.cambiarCorreoElectronico(newEmail, password, mContext);
            }
        });
        builder.setNegativeButton(R.string.change_email_dialog_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle negative button click
                dialog.cancel();
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.change_password_dialog_title);

        // Add the inputs
        final EditText inputCurrentPassword = new EditText(mContext);
        inputCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputCurrentPassword.setHint(R.string.change_password_dialog_current_password_hint);

        final EditText inputNewPassword = new EditText(mContext);
        inputNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputNewPassword.setHint(R.string.change_password_dialog_new_password_hint);

        final EditText inputConfirmPassword = new EditText(mContext);
        inputConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputConfirmPassword.setHint(R.string.change_password_dialog_confirm_password_hint);

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputCurrentPassword);
        layout.addView(inputNewPassword);
        layout.addView(inputConfirmPassword);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton(R.string.change_password_dialog_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle positive button click
                String currentPassword = inputCurrentPassword.getText().toString().trim();
                String newPassword = inputNewPassword.getText().toString().trim();
                String confirmPassword = inputConfirmPassword.getText().toString().trim();

                if (!newPassword.equals(confirmPassword)) {
                    // Show error message if passwords do not match
                    inputConfirmPassword.setError(getString(R.string.change_password_dialog_password_mismatch_error));
                    inputConfirmPassword.requestFocus();
                    //Toast.makeText(mContext, R.string.change_password_dialog_password_mismatch_error, Toast.LENGTH_SHORT).show();
                    Snackbar.make(inputConfirmPassword, R.string.change_password_dialog_password_mismatch_error, Snackbar.LENGTH_SHORT).show();
                    return;
                }
                try {
                    LoginUtils.isSecurePassword(newPassword);
                    profileVM.cambiarContrasena(currentPassword, newPassword, mContext);
                } catch (WeakPasswordException e) {
                    //Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    Snackbar.make(inputConfirmPassword, e.toString(), Snackbar.LENGTH_SHORT).show();

                }
            }
        });
        builder.setNegativeButton(R.string.change_password_dialog_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle negative button click
                dialog.cancel();
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * Este método abre el selector de imágenes para cambiar la imagen de perfil
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }



    /**
     * Este método obtiene el proveedor de autenticación del usuario
     */
    private void getUserProvider(View view) {
        final Observer<String> observerProvider = new Observer<String>() {
            @Override
            public void onChanged(String newUserProvider) {
                userProvider = newUserProvider;
                // Llamo aqui a estos metodos porque para la correcta ejecución necesitan saber el
                // userProvider ya que este determinara si los botones deben funcionar o no
                cambiarPassword_button_setup(view);
                cambiarEmail_button_setup(view);
            }
        };

        profileVM.getUserProvider().observe(getViewLifecycleOwner(),observerProvider);
    }

    /**
     * Este método se llama cuando se selecciona una imagen en el selector de imágenes
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            // Cargar la imagen en el ImageView
            Picasso.get().load(mImageUri).transform(new CropCircleTransformation()).into(userImage);

            // Subir la imagen al servidor
            profileVM.cambiarImagenPerfil(mImageUri, mContext);
        }
    }
}
