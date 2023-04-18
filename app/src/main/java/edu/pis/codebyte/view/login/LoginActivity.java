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
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

import edu.pis.codebyte.R;
import edu.pis.codebyte.view.profile.ProfileActivity;
import edu.pis.codebyte.view.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "GoogleActivity";
    private static final int GOOGLE_SIGN_IN = 100;
    public static final String PREFERENCES_FILE = "MyPrefs";

    private Button login_button;
    private Button signup_button;
    private Button google_button;
    private Button github_button;
    private Button recuperaPassword_button; //TODO
    private TextView email_text;
    private TextView password_text;
    private CheckBox keepSession_cb;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);

        checkSession();

        activity_setup();

    }

    private void checkSession() {
        boolean saveSession = prefs.getBoolean("saveSession", false);

        if (saveSession) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                String token = "";
                prefs.getString("idToken",token);
                mAuth.signInWithCustomToken(token)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    goToHome();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Your session has expired", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }else {
            mAuth.signOut();
        }
    }

    private void activity_setup() {

        email_text = (TextView) findViewById(R.id.email_textView);
        password_text = (TextView) findViewById(R.id.password_editText);

        login_button_setup();
        signup_button_setup();
        google_button_setup();
        gihub_button_setup();
        keepSession_cb_setup();

        //TODO
        recuperaPassword_button = findViewById(R.id.recuperaPassword_bttn);
    }

    private void login_button_setup() {
        login_button = (Button) findViewById(R.id.login_bttn);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (email_text.getText().toString().isEmpty()) {
                        email_text.setError("Correo electrónico no válido");
                    } else if (password_text.getText().toString().isEmpty()) {
                        password_text.setError("Contraseña no válida");
                    } else {
                        mAuth.
                                signInWithEmailAndPassword(email_text.getText().toString(),
                                        password_text.getText().toString()).
                                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            goToHome();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                } catch (Exception e) {
                    showError(e);
                }
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

    private void gihub_button_setup() {
        github_button = (Button) findViewById(R.id.github_bttn);
        github_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                github_auth();
            }
        });

    }

    private void keepSession_cb_setup () {
        keepSession_cb = (CheckBox) findViewById(R.id.keepSession_checkBox);
        keepSession_cb.setChecked(true);
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
        // [START auth_github_provider_create]
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
        // [END auth_github_provider_create]

        // [START auth_github_provider_params]
        // Target specific email with login hint.
        provider.addCustomParameter("login", "");
        // [END auth_github_provider_params]

        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
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

    private void keepSession() throws Exception {
        if (currentUser == null) {
            throw new Exception("No hay ningun usuario logeado");
        }
        currentUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult().getToken();
                            SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE).edit();
                            editor.putString("idToken", token);
                            editor.apply();
                        } else {
                            throw new RuntimeException("No se ha conseguido el user idToken");
                        }
                    }
                });
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

    private void showError(Exception e) {
        Toast.makeText(this, e.toString(),
                Toast.LENGTH_SHORT).show();
    }

    private void goToNewActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
    private void goToHome() {
        currentUser = mAuth.getCurrentUser();
        if (keepSession_cb.isChecked()) {
            try {
                keepSession();
            } catch (Exception e) {
                Log.e("KEEP_SESSION", e.toString());
            }
        }
        goToNewActivity(ProfileActivity.class);
        finish();
    }
}