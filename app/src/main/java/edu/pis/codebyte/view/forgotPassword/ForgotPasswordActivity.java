package edu.pis.codebyte.view.forgotPassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import edu.pis.codebyte.R;
import edu.pis.codebyte.model.LoginUtils;
import edu.pis.codebyte.model.exceptions.InvalidEmailException;
import edu.pis.codebyte.view.login.LoginActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button recuperarBtn;
    EditText emailEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_layout);

        recuperarBtn = findViewById(R.id.RecuperarPassword_ForgotPasswordLayout_bttn);
        emailEditText = findViewById(R.id.RecuperarContraseña_ForgotPasswordLayout_editText);
        recuperarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    LoginUtils.isValidEmail(emailEditText.getText().toString());
                } catch (InvalidEmailException e) {
                    Toast.makeText(ForgotPasswordActivity.this, "Correo inválido...", Toast.LENGTH_SHORT).show();
                }
                sendEmail(emailEditText.getText().toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void sendEmail(String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAdress = email;

        auth.sendPasswordResetEmail(emailAdress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Correo enviado!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Correo inválido...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}