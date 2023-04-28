package edu.pis.codebyte.view.login;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.platform.MaterialFadeThrough;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.pis.codebyte.R;
import edu.pis.codebyte.model.Course;
import edu.pis.codebyte.model.DataBaseManager;
import edu.pis.codebyte.model.LoginUtils;
import edu.pis.codebyte.model.ProgrammingLanguage;
import edu.pis.codebyte.model.exceptions.InvalidEmailException;
import edu.pis.codebyte.model.exceptions.InvalidPasswordException;
import edu.pis.codebyte.view.main.MainActivity;
import edu.pis.codebyte.view.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    private static final int GOOGLE_SIGN_IN = 100;
    public static final String PREFERENCES_FILE = "login_prefs";
    private static final String TAG = "log";

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
        getWindow().setEnterTransition(new MaterialFadeThrough());
        getWindow().setExitTransition(new MaterialFadeThrough());
        setContentView(R.layout.activity_login);
        dbm = DataBaseManager.getInstance();
        prefs = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        activity_setup();
        recuperaPassword_button = findViewById(R.id.recuperaPassword_bttn);
        recuperaPassword_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method();
            }
        });
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
                                    //Toast.makeText(LoginActivity.this, "Your session has expired", Toast.LENGTH_SHORT).show();
                                    Snackbar.make(login_button, "Your session has expired", Snackbar.LENGTH_SHORT).show();

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
                                //Toast.makeText(LoginActivity.this, "La autenticación falló.", Toast.LENGTH_SHORT).show();
                                Snackbar.make( login_button, "La autenticación falló.", Snackbar.LENGTH_SHORT).show();

                            }
                        }
                    });
        } catch (InvalidEmailException e) {
            // Si el email no es válido, marcar el TextView de email con error y mostrar un mensaje de error
            email_text.setError("El email proporcionado no es válido.");
            email_text.requestFocus();
            //Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            Snackbar.make(login_button, e.toString(), Snackbar.LENGTH_SHORT).show();
        } catch (InvalidPasswordException e) {
            password_text.setError("La contraseña proporcionada no es valida.");
            password_text.requestFocus();
            //Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            Snackbar.make(login_button, e.toString(), Snackbar.LENGTH_SHORT).show();
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
                                        dbm.addUserToDatabase(mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getEmail(), "github.com");
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
                                        dbm.addUserToDatabase(mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getEmail(), "github.com");
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
                                dbm.addUserToDatabase(mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getEmail(), "google.com");
                            }
                            goToHome();
                        } else {
                            //Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            Snackbar.make(login_button, "Authentication failed.", Snackbar.LENGTH_SHORT).show();

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
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        this.onPause();
    }
    private void goToHome() {
        goToNewActivity(MainActivity.class);
    }

    private void method() {
        // Crear la lista de lenguajes
        ArrayList<ProgrammingLanguage> lenguajes = new ArrayList<>();

        // Lenguaje C
        String descripcionC = "C es un lenguaje de programación de bajo nivel utilizado para programar sistemas operativos, controladores de dispositivos y otros programas que requieren un acceso directo a la memoria y al hardware.";
        ArrayList<Course> cursosC = new ArrayList<>();
        cursosC.add(new Course("Introducción a C", "Aprende los fundamentos de la programación en C"));
        cursosC.add(new Course("Estructuras de datos en C", "Aprende a trabajar con estructuras de datos en C"));
        HashSet<String> tagsC = new HashSet<>();
        tagsC.add("Bajo Nivel");
        tagsC.add("Lenguaje Imperativo");
        lenguajes.add(new ProgrammingLanguage("C", descripcionC, cursosC, tagsC, R.drawable.logo_c));

        // Lenguaje C++
        String descripcionCpp = "C++ es un lenguaje de programación de alto nivel utilizado para el desarrollo de aplicaciones de software, videojuegos y otros programas complejos.";
        ArrayList<Course> cursosCpp = new ArrayList<>();
        cursosCpp.add(new Course("Introducción a C++", "Aprende los fundamentos de la programación en C++"));
        cursosCpp.add(new Course("Programación orientada a objetos en C++", "Aprende a programar utilizando la metodología de programación orientada a objetos en C++"));
        HashSet<String> tagsCpp = new HashSet<>();
        tagsCpp.add("Alto Nivel");
        tagsCpp.add("OOP");
        lenguajes.add(new ProgrammingLanguage("C++", descripcionCpp, cursosCpp, tagsCpp, R.drawable.logo_cpp));

        // Lenguaje C#
        String descripcionCSharp = "C# es un lenguaje de programación de alto nivel desarrollado por Microsoft. Es utilizado principalmente para el desarrollo de aplicaciones en la plataforma .NET.";
        ArrayList<Course> cursosCSharp = new ArrayList<>();
        cursosCSharp.add(new Course("Introducción a C#", "Aprende los fundamentos de la programación en C#"));
        cursosCSharp.add(new Course("Programación orientada a objetos en C#", "Aprende a programar utilizando la metodología de programación orientada a objetos en C#"));
        HashSet<String> tagsCSharp = new HashSet<>();
        tagsCSharp.add("Alto Nivel");
        tagsCSharp.add("OOP");
        lenguajes.add(new ProgrammingLanguage("C#", descripcionCSharp, cursosCSharp, tagsCSharp, R.drawable.logo_csh));

        // Lenguaje Java
        String descripcionJava = "Java es un lenguaje de programación de alto nivel utilizado principalmente para el desarrollo de aplicaciones empresariales y de servidores.";
        ArrayList<Course> cursosJava = new ArrayList<>();
        cursosJava.add(new Course("Introducción a Java", "Aprende los fundamentos de la programación en Java"));
        cursosJava.add(new Course("Programación orientada a objetos en Java", "Aprende a programar utilizando la metodología de programación orientada a objetos en Java"));
        HashSet<String> tagsJava = new HashSet<>();
        tagsJava.add("Alto Nivel");
        tagsJava.add("OOP");
        lenguajes.add(new ProgrammingLanguage("Java", descripcionJava, cursosJava, tagsJava, R.drawable.logo_java));

        // Kotlin
        ArrayList<Course> kotlinCourses = new ArrayList<>();
        kotlinCourses.add(new Course("Curso de Kotlin básico", "Introducción a Kotlin"));
        kotlinCourses.add(new Course("Curso de Kotlin avanzado", "Programación orientada a objetos en Kotlin"));
        HashSet<String> kotlinTags = new HashSet<>();
        kotlinTags.add("OOP");
        kotlinTags.add("Lenguaje Funcional");
        ProgrammingLanguage kotlin = new ProgrammingLanguage("Kotlin", "Kotlin es un lenguaje de programación multiplataforma, orientado a objetos y funcional. Es un lenguaje estáticamente tipado que se ejecuta en la JVM y que también se puede compilar a JavaScript o nativo.", kotlinCourses, kotlinTags, R.drawable.logo_kotlin);
        lenguajes.add(kotlin);

        // Python
        ArrayList<Course> pythonCourses = new ArrayList<>();
        pythonCourses.add(new Course("Curso de Python básico", "Introducción a Python"));
        pythonCourses.add(new Course("Curso de Python avanzado", "Programación orientada a objetos en Python"));
        HashSet<String> pythonTags = new HashSet<>();
        pythonTags.add("Lenguaje Funcional");
        ProgrammingLanguage python = new ProgrammingLanguage("Python", "Python es un lenguaje de programación interpretado, orientado a objetos y de alto nivel. Es un lenguaje fácil de aprender y muy versátil, utilizado en una amplia variedad de aplicaciones, desde el desarrollo web hasta la inteligencia artificial.", pythonCourses, pythonTags, R.drawable.logo_python);
        lenguajes.add(python);

        // JavaScript
        ArrayList<Course> jsCourses = new ArrayList<>();
        jsCourses.add(new Course("Curso de JavaScript básico", "Introducción a JavaScript"));
        jsCourses.add(new Course("Curso de JavaScript avanzado", "Programación orientada a objetos en JavaScript"));
        HashSet<String> jsTags = new HashSet<>();
        jsTags.add("Alto Nivel");
        ProgrammingLanguage javascript = new ProgrammingLanguage("JavaScript", "JavaScript es un lenguaje de programación interpretado, de alto nivel y orientado a objetos. Es utilizado principalmente para el desarrollo web, pero también se puede utilizar en el lado del servidor y en el desarrollo de aplicaciones móviles.", jsCourses, jsTags, R.drawable.logo_js);
        lenguajes.add(javascript);

        // TypeScript
        ArrayList<Course> tsCourses = new ArrayList<>();
        tsCourses.add(new Course("Curso de TypeScript básico", "Introducción a TypeScript"));
        tsCourses.add(new Course("Curso de TypeScript avanzado", "Programación orientada a objetos en TypeScript"));
        HashSet<String> tsTags = new HashSet<>();
        tsTags.add("Alto Nivel");
        ProgrammingLanguage typescript = new ProgrammingLanguage("TypeScript", "TypeScript es un lenguaje de programación de código abierto que se construye sobre JavaScript, añadiendo nuevas características y mejorando la seguridad del código. Es un lenguaje estáticamente tipado que se compila a JavaScript.", tsCourses, tsTags, R.drawable.logo_ts);
        lenguajes.add(typescript);

        // Lenguaje R
        ArrayList<Course> rCourses = new ArrayList<>();
        rCourses.add(new Course("Introduction to R", "A beginner's guide to R programming"));
        rCourses.add(new Course("Data Visualization in R", "Learn to create data visualizations using R"));
        HashSet<String> rTags = new HashSet<>(Arrays.asList("Data Science", "Statistics", "Data Visualization"));
        ProgrammingLanguage r = new ProgrammingLanguage("R", "R is a programming language and free software environment for statistical computing and graphics.", rCourses, rTags, R.drawable.logo_r);
        lenguajes.add(r);

        // Lenguaje Rust
        ArrayList<Course> rustCourses = new ArrayList<>();
        rustCourses.add(new Course("Rust Basics", "Introduction to Rust programming language"));
        rustCourses.add(new Course("Advanced Rust", "Advanced concepts and features of Rust programming language"));
        HashSet<String> rustTags = new HashSet<>(Arrays.asList("Systems Programming", "Memory Safety", "Concurrency"));
        ProgrammingLanguage rust = new ProgrammingLanguage("Rust", "Rust is a systems programming language focused on safety, speed, and concurrency.", rustCourses, rustTags, R.drawable.logo_rust);
        lenguajes.add(rust);

        // Lenguaje MATLAB
        ArrayList<Course> matlabCourses = new ArrayList<>();
        matlabCourses.add(new Course("Introduction to MATLAB", "Learn the basics of MATLAB programming language"));
        matlabCourses.add(new Course("MATLAB for Engineers", "Advanced MATLAB concepts for engineering applications"));
        HashSet<String> matlabTags = new HashSet<>(Arrays.asList("Numerical Computing", "Engineering", "Scientific Computing"));
        ProgrammingLanguage matlab = new ProgrammingLanguage("MATLAB", "MATLAB is a high-performance language for technical computing.", matlabCourses, matlabTags, R.drawable.logo_matlab);
        lenguajes.add(matlab);

        // Lenguaje PHP
        ArrayList<Course> phpCourses = new ArrayList<>();
        phpCourses.add(new Course("PHP Basics", "Introduction to PHP programming language"));
        phpCourses.add(new Course("PHP Web Development", "Building web applications using PHP"));
        HashSet<String> phpTags = new HashSet<>(Arrays.asList("Web Development", "Server-Side Scripting", "Database Integration"));
        ProgrammingLanguage php = new ProgrammingLanguage("PHP", "PHP is a popular general-purpose scripting language that is especially suited to web development.", phpCourses, phpTags, R.drawable.logo_php);
        lenguajes.add(php);

        // Lenguaje HTML
        ArrayList<Course> htmlCourses = new ArrayList<>();
        htmlCourses.add(new Course("HTML Fundamentals", "Introduction to HTML markup language"));
        htmlCourses.add(new Course("CSS Basics", "Introduction to CSS styling language"));
        HashSet<String> htmlTags = new HashSet<>(Arrays.asList("Web Development", "Front-End Development", "Markup Languages"));
        ProgrammingLanguage html = new ProgrammingLanguage("HTML", "HTML is the standard markup language for creating web pages, and CSS is used to style them.", htmlCourses, htmlTags, R.drawable.logo_html);
        lenguajes.add(html);

        // Lenguaje Go
        ArrayList<Course> goCourses = new ArrayList<>();
        goCourses.add(new Course("Go Basics", "Introduction to Go programming language"));
        goCourses.add(new Course("Advanced Go", "Advanced concepts and features of Go programming language"));
        HashSet<String> goTags = new HashSet<>(Arrays.asList("Systems Programming", "Concurrent Programming"));
        ProgrammingLanguage go = new ProgrammingLanguage("Go", "Go is a statically typed, compiled language designed for efficient and concurrent programming.", goCourses, goTags, R.drawable.logo_go);
        lenguajes.add(go);

        // Lenguaje Objective-C
        String descripcionObjC = "Objective-C is a general-purpose, object-oriented programming language that adds Smalltalk-style messaging to the C programming language.";
        ArrayList<Course> objCCourses = new ArrayList<>();
        objCCourses.add(new Course("Objective-C Basics", "Introduction to Objective-C programming language"));
        objCCourses.add(new Course("Objective-C for iOS Development", "Building iOS applications using Objective-C"));
        HashSet<String> objCTags = new HashSet<>(Arrays.asList("iOS Development", "Object-Oriented Programming"));
        ProgrammingLanguage objC = new ProgrammingLanguage("Objective-C", descripcionObjC, objCCourses, objCTags, 0);
        lenguajes.add(objC);

        // Lenguaje Ruby
        ArrayList<Course> rubyCourses = new ArrayList<>();
        rubyCourses.add(new Course("Ruby Basics", "Introduction to Ruby programming language"));
        rubyCourses.add(new Course("Ruby on Rails", "Building web applications using Ruby on Rails framework"));
        HashSet<String> rubyTags = new HashSet<>(Arrays.asList("Web Development", "Scripting", "Object-Oriented Programming"));
        ProgrammingLanguage ruby = new ProgrammingLanguage("Ruby", "Ruby is a dynamic, reflective, object-oriented, general-purpose programming language.", rubyCourses, rubyTags, R.drawable.logo_ruby);
        lenguajes.add(ruby);

        // Lenguaje SQL
        ArrayList<Course> sqlCourses = new ArrayList<>();
        sqlCourses.add(new Course("SQL Fundamentals", "Introduction to SQL programming language"));
        sqlCourses.add(new Course("Advanced SQL", "Advanced concepts of SQL and database management"));
        HashSet<String> sqlTags = new HashSet<>(Arrays.asList("Database Management", "Query Language"));
        ProgrammingLanguage sql = new ProgrammingLanguage("SQL", "SQL is a domain-specific language used in programming and designed for managing data held in a relational database management system.", sqlCourses, sqlTags, R.drawable.logo_sql);
        lenguajes.add(sql);

        // Lenguaje Swift
        ArrayList<Course> swiftCourses = new ArrayList<>();
        swiftCourses.add(new Course("Swift Basics", "Introduction to Swift programming language"));
        swiftCourses.add(new Course("iOS App Development with Swift", "Building iOS applications using Swift"));
        HashSet<String> swiftTags = new HashSet<>(Arrays.asList("iOS Development", "Object-Oriented Programming"));
        ProgrammingLanguage swift = new ProgrammingLanguage("Swift", "Swift is a powerful and intuitive programming language created by Apple for building iOS, macOS, watchOS, and tvOS apps.", swiftCourses, swiftTags, R.drawable.logo_swift);
        lenguajes.add(swift);

        // Lenguaje Scala
        ArrayList<Course> scalaCourses = new ArrayList<>();
        scalaCourses.add(new Course("Scala Basics", "Introduction to Scala programming language"));
        scalaCourses.add(new Course("Functional Programming in Scala", "Learn functional programming concepts in Scala"));
        HashSet<String> scalaTags = new HashSet<>(Arrays.asList("Functional Programming", "JVM", "Concurrency"));
        ProgrammingLanguage scala = new ProgrammingLanguage("Scala", "Scala is a modern multi-paradigm programming language designed to express common programming patterns in a concise, elegant, and type-safe way.", scalaCourses, scalaTags, 0);
        lenguajes.add(scala);

        // Lenguaje Pascal
        ArrayList<Course> pascalCourses = new ArrayList<>();
        pascalCourses.add(new Course("Pascal Basics", "Introduction to Pascal programming language"));
        pascalCourses.add(new Course("Data Structures in Pascal", "Learn about data structures in Pascal"));
        HashSet<String> pascalTags = new HashSet<>();
        pascalTags.add("Structured Programming");
        pascalTags.add("Algorithm Design");
        ProgrammingLanguage pascal = new ProgrammingLanguage("Pascal", "Pascal is a procedural programming language designed for teaching programming and producing reliable and efficient programs.", pascalCourses, pascalTags, 0);
        lenguajes.add(pascal);

        // Lenguaje Elixir
        ArrayList<Course> elixirCourses = new ArrayList<>();
        elixirCourses.add(new Course("Elixir Basics", "Introduction to Elixir programming language"));
        elixirCourses.add(new Course("Concurrent Programming with Elixir", "Learn concurrent programming using Elixir"));
        HashSet<String> elixirTags = new HashSet<>(Arrays.asList("Functional Programming", "Concurrency", "Scalability"));
        ProgrammingLanguage elixir = new ProgrammingLanguage("Elixir", "Elixir is a functional, concurrent, and extensible programming language built on the Erlang virtual machine (BEAM).", elixirCourses, elixirTags, 0);
        lenguajes.add(elixir);

        // Lenguaje Erlang
        ArrayList<Course> erlangCourses = new ArrayList<>();
        erlangCourses.add(new Course("Erlang Basics", "Introduction to Erlang programming language"));
        erlangCourses.add(new Course("OTP Framework in Erlang", "Learn to build fault-tolerant systems with OTP in Erlang"));
        HashSet<String> erlangTags = new HashSet<>(Arrays.asList("Concurrent Programming", "Fault-Tolerant Systems", "Telecommunications"));
        ProgrammingLanguage erlang = new ProgrammingLanguage("Erlang", "Erlang is a general-purpose, concurrent, and functional programming language used primarily in telecommunication, banking, and e-commerce.", erlangCourses, erlangTags, 0);
        lenguajes.add(erlang);

        // Lenguaje Scheme
        ArrayList<Course> schemeCourses = new ArrayList<>();
        schemeCourses.add(new Course("Scheme Basics", "Introduction to Scheme programming language"));
        schemeCourses.add(new Course("Functional Programming in Scheme", "Learn functional programming concepts in Scheme"));
        HashSet<String> schemeTags = new HashSet<>(Arrays.asList("Lisp", "Functional Programming", "Language Design"));
        ProgrammingLanguage scheme = new ProgrammingLanguage("Scheme", "Scheme is a dialect of the Lisp programming language known for its simplicity and elegance. It is often used for teaching programming and implementing programming language concepts.", schemeCourses, schemeTags, 0);
        lenguajes.add(scheme);

        // Lenguaje Postscript
        ArrayList<Course> postscriptCourses = new ArrayList<>();
        postscriptCourses.add(new Course("Introduction to Postscript", "Learn the basics of Postscript language"));
        postscriptCourses.add(new Course("Advanced Postscript", "Advanced concepts and features of Postscript language"));
        HashSet<String> postscriptTags = new HashSet<>(Arrays.asList("Page Description Language", "Printing", "Graphics"));
        ProgrammingLanguage postscript = new ProgrammingLanguage("Postscript", "Postscript is a programming language that is primarily used for describing the appearance of printed pages.", postscriptCourses, postscriptTags, 0);
        lenguajes.add(postscript);

        // Lenguaje Basic
        ArrayList<Course> basicCourses = new ArrayList<>();
        basicCourses.add(new Course("Introduction to Basic", "Learn the basics of Basic programming language"));
        basicCourses.add(new Course("Advanced Basic", "Advanced concepts and features of Basic programming language"));
        HashSet<String> basicTags = new HashSet<>();
        basicTags.add("Beginner");
        ProgrammingLanguage basic = new ProgrammingLanguage("Basic", "Basic is a family of general-purpose programming languages that are simple to learn and widely used.", basicCourses, basicTags, 0);
        lenguajes.add(basic);

        // Lenguaje Cobol
        ArrayList<Course> cobolCourses = new ArrayList<>();
        cobolCourses.add(new Course("Introduction to Cobol", "Learn the basics of Cobol programming language"));
        cobolCourses.add(new Course("Advanced Cobol", "Advanced concepts and features of Cobol programming language"));
        HashSet<String> cobolTags = new HashSet<>(Arrays.asList("Business Applications", "Legacy Systems", "Mainframe Programming"));
        ProgrammingLanguage cobol = new ProgrammingLanguage("Cobol", "Cobol is a high-level programming language designed for business applications.", cobolCourses, cobolTags, 0);
        lenguajes.add(cobol);

        // Lenguaje Fortran
        ArrayList<Course> fortranCourses = new ArrayList<>();
        fortranCourses.add(new Course("Introduction to Fortran", "Learn the basics of Fortran programming language"));
        fortranCourses.add(new Course("Advanced Fortran", "Advanced concepts and features of Fortran programming language"));
        HashSet<String> fortranTags = new HashSet<>(Arrays.asList("Scientific Computing", "Numerical Analysis", "High-Performance Computing"));
        ProgrammingLanguage fortran = new ProgrammingLanguage("Fortran", "Fortran is a general-purpose, imperative programming language that is particularly suited to numeric and scientific computing.", fortranCourses, fortranTags, 0);
        lenguajes.add(fortran);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference languagesCollectionRef = db.collection("ProgrammingLanguages");

        for (ProgrammingLanguage language : lenguajes) {
            String languageId = language.getName(); // Utilizamos el nombre del lenguaje como ID del documento

            // Crear el documento para el lenguaje
            DocumentReference languageDocRef = languagesCollectionRef.document(languageId);

            // Crear los datos del lenguaje
            Map<String, Object> languageData = new HashMap<>();
            languageData.put("descripcion", language.getDescription());
            languageData.put("resourceImageId", language.getImageResourceId());
            languageData.put("tags", new ArrayList<>(language.getTags()));

            // Crear la colección de cursos para el lenguaje
            CollectionReference coursesCollectionRef = languageDocRef.collection("courses");

            // Agregar los cursos al lenguaje
            for (Course course : language.getCourses()) {
                DocumentReference courseDocRef = coursesCollectionRef.document();
                Map<String, Object> courseData = new HashMap<>();
                courseData.put("descripcion", course.getDescription());
                courseDocRef.set(courseData);
            }

            // Subir los datos del lenguaje a Firestore
            languageDocRef.set(languageData)
                    .addOnSuccessListener(aVoid -> {
                        // Se subió correctamente el lenguaje a Firestore
                        System.out.println("Lenguaje " + languageId + " subido correctamente a Firestore");
                    })
                    .addOnFailureListener(e -> {
                        // Ocurrió un error al subir el lenguaje a Firestore
                        System.out.println("Error al subir el lenguaje " + languageId + " a Firestore: " + e.getMessage());
                    });
        }
    }
}