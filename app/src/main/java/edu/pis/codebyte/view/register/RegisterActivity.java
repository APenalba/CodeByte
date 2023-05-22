package edu.pis.codebyte.view.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import edu.pis.codebyte.R;
import edu.pis.codebyte.model.DataBaseManager;
import edu.pis.codebyte.model.LoginUtils;
import edu.pis.codebyte.model.exceptions.InvalidEmailException;
import edu.pis.codebyte.model.exceptions.TermsAndConditionsNotAcceptedException;
import edu.pis.codebyte.model.exceptions.WeakPasswordException;
import edu.pis.codebyte.view.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView email_text;
    private TextView password_text;
    private Button signUp_button;
    private Button login_button;
    private CheckBox terminosYcondiciones;
    private String email, password;
    private DataBaseManager dbm;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        dbm = DataBaseManager.getInstance();
        email_text = findViewById(R.id.RegisterEmail_RegisterActivity_editText);
        password_text = findViewById(R.id.RegisterPassword_RegisterActivity_editText);
        signUp_button = findViewById(R.id.RegisterSignUp_LoginActivity_bttn);
        login_button = findViewById(R.id.RegisterLogin_LoginActivity_bttn);
        terminosYcondiciones = findViewById(R.id.TermsAndConditions_LoginActivity_checkBox);
        progressBar = findViewById(R.id.LoadingScreen_AlllanguagesFragment_titleBar);
        progressBar.bringToFront();
        progressBar.setVisibility(View.GONE);
        signUp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createAccount();
            }
        });


        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLogIn();
            }
        });
    }

    private void createAccount() {
        email = email_text.getText().toString();
        password = password_text.getText().toString();
        try{
            LoginUtils.isValidEmail(email);
            LoginUtils.isSecurePassword(password);
            LoginUtils.areTermsAndConditionsAccepted(terminosYcondiciones.isChecked());
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String username = LoginUtils.generateUsernameFromEmail(email);
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username)
                                        .build();
                                user.updateProfile(profileUpdates);
                                dbm.addUserToDatabase(user.getUid(), username, user.getEmail(), "email_password");
                                goToLogIn();

                            } else {
                                Snackbar.make(signUp_button, "Authentication failed.",Snackbar.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });

        } catch (InvalidEmailException e) {
            // Si el email no es válido, marcar el TextView de email con error y mostrar un mensaje de error
            email_text.setError("El email proporcionado no es válido.");
            email_text.requestFocus();
            Snackbar.make(email_text, e.toString(),Snackbar.LENGTH_SHORT).show();
        } catch (WeakPasswordException e) {
            // Si la contraseña no es segura, marcar el TextView de contraseña con error y mostrar un mensaje de error
            password_text.setError("La contraseña proporcionada no es segura. Por favor, utiliza una contraseña más segura.");
            password_text.requestFocus();
            Snackbar.make(email_text, e.toString(),Snackbar.LENGTH_SHORT).show();
        } catch (TermsAndConditionsNotAcceptedException e) {
            // Si el usuario no acepta los terminos y condiciones se marca el checkbox con error
            terminosYcondiciones.setError("Debes aceptar los terminos y condiciones");
            terminosYcondiciones.requestFocus();
            Snackbar.make(email_text, e.toString(),Snackbar.LENGTH_SHORT).show();
        }
    }

    private void goToLogIn() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}