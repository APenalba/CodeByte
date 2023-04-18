package edu.pis.codebyte.view.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.pis.codebyte.R;
import edu.pis.codebyte.view.login.LoginActivity;
import edu.pis.codebyte.view.register.*;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView email_text;
    private TextView password_text;
    private Button signUp_button;
    private Button login_button;
    private CheckBox terminosYcondiciones;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        email_text = findViewById(R.id.reg_email_emailText);
        password_text = findViewById(R.id.reg_password_editText);
        signUp_button = findViewById(R.id.reg_signup_bttn);
        login_button = findViewById(R.id.reg_login_bttn);
        terminosYcondiciones = findViewById(R.id.terminosCondiciones_checkBox);
        signUp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email_text.getText().toString().isEmpty()) {
                    email_text.setError("Correo electrónico no válido");
                }else if (password_text.getText().toString().isEmpty()) {
                    password_text.setError("Contraseña no válida");
                }else if (! terminosYcondiciones.isChecked()) {
                    terminosYcondiciones.setError("Para crear una cuenta debes aceptar los terminos y condiciones");
                } else {
                    email = email_text.getText().toString();
                    password = password_text.getText().toString();
                    createAccount();
                }
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void createAccount() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}