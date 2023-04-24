package edu.pis.codebyte.view.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

import edu.pis.codebyte.R;
import edu.pis.codebyte.model.DataBaseManager;
import edu.pis.codebyte.model.LoginUtils;
import edu.pis.codebyte.model.exceptions.InvalidEmailException;
import edu.pis.codebyte.model.exceptions.InvalidPasswordException;
import edu.pis.codebyte.view.register.profile.ProfileActivity;
import edu.pis.codebyte.view.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    private static final int GOOGLE_SIGN_IN = 100;
    public static final String PREFERENCES_FILE = "login_prefs";

    private Button login_button;
    private Button signup_button;
    private Button google_button;
    private Button github_button;
    private Button recuperaPassword_button; //TODO
    private TextView email_text;
    private TextView password_text;
    private CheckBox keepSession_cb;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;
    private DataBaseManager dbm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbm = DataBaseManager.getInstance();
        prefs = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        activity_setup();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkSession();
    }

    private void checkSession() {
        boolean saveSession = prefs.getBoolean("saveSession", false);
        if (saveSession) {
            if (mAuth.getCurrentUser() == null) {
                String token = "";
                prefs.getString("idToken",token);
                mAuth.signInWithCustomToken(token)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    goToHome();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Your session has expired", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else {
                goToHome();
            }
        }else {
            mAuth.signOut();
        }
    }

    private void activity_setup() {

        email_text = (TextView) findViewById(R.id.email_emailText);
        password_text = (TextView) findViewById(R.id.password_editText);
        keepSession_cb = (CheckBox) findViewById(R.id.keepSession_checkBox);

        login_button_setup();
        signup_button_setup();
        google_button_setup();
        github_button_setup();

        //TODO
        recuperaPassword_button = findViewById(R.id.recuperaPassword_bttn);
    }

    private void login_button_setup() {
        login_button = (Button) findViewById(R.id.login_bttn);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailPassword_auth();
            }
        });
    }

    private void signup_button_setup() {
        signup_button = (Button) findViewById(R.id.signup_bttn);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNewActivity(RegisterActivity.class);
            }
        });
    }

    private void google_button_setup() {
        google_button = (Button) findViewById(R.id.google_bttn);
        google_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                google_auth();
            }
        });
    }

    private void github_button_setup() {
        github_button = (Button) findViewById(R.id.github_bttn);
        github_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                github_auth();
            }
        });

    }

    private void emailPassword_auth() {
        String email = email_text.getText().toString();
        String password = password_text.getText().toString();
        try {
            LoginUtils.isValidEmail(email);
            if (password.isEmpty()) throw new InvalidPasswordException("Contraseña no valida");
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                goToHome();
                            } else {
                                Toast.makeText(LoginActivity.this, "La autenticación falló.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (InvalidEmailException e) {
            // Si el email no es válido, marcar el TextView de email con error y mostrar un mensaje de error
            email_text.setError("El email proporcionado no es válido.");
            email_text.requestFocus();
            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        } catch (InvalidPasswordException e) {
            password_text.setError("La contraseña proporcionada no es valida.");
            password_text.requestFocus();
            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void google_auth() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id)).
                requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);
        googleSignInClient.signOut();
        startActivityForResult(googleSignInClient.getSignInIntent(), GOOGLE_SIGN_IN);
    }

    public void github_auth() {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
        provider.addCustomParameter("login", "");

        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    if (authResult.getAdditionalUserInfo().isNewUser()) {
                                        dbm.addUserToDatabase(mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getEmail());
                                    }
                                    goToHome();
                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // ((OAuthCredential)authResult.getCredential()).getAccessToken().
                                    // The OAuth secret can be retrieved by calling:
                                    // ((OAuthCredential)authResult.getCredential()).getSecret().
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                }
                            });
        } else {
            mAuth
                    .startActivityForSignInWithProvider(LoginActivity.this, provider.build())
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // ((OAuthCredential)authResult.getCredential()).getAccessToken().
                                    // The OAuth secret can be retrieved by calling:
                                    // ((OAuthCredential)authResult.getCredential()).getSecret().
                                    if (authResult.getAdditionalUserInfo().isNewUser()) {
                                        dbm.addUserToDatabase(mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getEmail());
                                    }
                                    goToHome();
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                }
                            });
        }
    }

    private void keepSession() {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("saveSession", keepSession_cb.isChecked());
        editor.apply();

        if (mAuth.getCurrentUser() != null && keepSession_cb.isChecked()) {
            mAuth.getCurrentUser().getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String token = task.getResult().getToken();
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("idToken", token);
                                editor.apply();
                            } else {
                                throw new RuntimeException("No se ha conseguido el user idToken");
                            }
                        }
                    });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {

            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult();
                if (account == null) {
                    //TODO GESTIONAR ESTO
                }
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                dbm.addUserToDatabase(mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getEmail());
                            }
                            goToHome();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                Log.w("ERROR", e.toString());
            }

        }
    }

    private void goToNewActivity(Class<?> cls) {
        keepSession();
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        this.onPause();
    }
    private void goToHome() {
        goToNewActivity(ProfileActivity.class);
        finish();
    }
}