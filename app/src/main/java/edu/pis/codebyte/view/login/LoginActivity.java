package edu.pis.codebyte.view.login;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.Objects;

import edu.pis.codebyte.R;
import edu.pis.codebyte.model.DataBaseManager;
import edu.pis.codebyte.model.LoginUtils;
import edu.pis.codebyte.model.exceptions.InvalidEmailException;
import edu.pis.codebyte.model.exceptions.InvalidPasswordException;
import edu.pis.codebyte.view.forgotPassword.ForgotPasswordActivity;
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
    private Button recuperaPassword_button;
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
        checkSession();
        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.signOut();
    }

    private void checkSession() {
        boolean saveSession = prefs.getBoolean("saveSession", false);
        if (saveSession) {
            if (mAuth.getCurrentUser() == null) {
                String token = prefs.getString("idToken", "");
                mAuth.signInWithCustomToken(token)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    goToHome();
                                } else {
                                    showSnackbar("Your session has expired");
                                }
                            }
                        });
            } else {
                goToHome();
            }
        } else {
            mAuth.signOut();
        }
    }

    private void setupViews() {
        email_text = findViewById(R.id.recupera_email_editText);
        password_text = findViewById(R.id.password_editText);
        keepSession_cb = findViewById(R.id.keepSession_checkBox);

        setupLoginButton();
        setupSignupButton();
        setupRecoverPassword();
        setupGoogleButton();
        setupGithubButton();
    }

    private void setupRecoverPassword() {
        recuperaPassword_button = findViewById(R.id.recuperaPassword_bttn);
        recuperaPassword_button.setOnClickListener(view -> goToNewActivity(ForgotPasswordActivity.class));
    }

    private void setupLoginButton() {
        login_button = findViewById(R.id.recupera_pwd_bttn);
        login_button.setOnClickListener(view -> emailPasswordAuth());
    }

    private void setupSignupButton() {
        signup_button = findViewById(R.id.signup_bttn);
        signup_button.setOnClickListener(view -> goToNewActivity(RegisterActivity.class));
    }

    private void setupGoogleButton() {
        google_button = findViewById(R.id.google_bttn);
        google_button.setOnClickListener(view -> googleAuth());
    }

    private void setupGithubButton() {
        github_button = findViewById(R.id.github_bttn);
        github_button.setOnClickListener(view -> githubAuth());
    }


    private void emailPasswordAuth() {
        String email = Objects.requireNonNull(email_text.getText()).toString();
        String password = Objects.requireNonNull(password_text.getText()).toString();
        try {
            LoginUtils.isValidEmail(email);
            if (TextUtils.isEmpty(password)) {
                throw new InvalidPasswordException("Contraseña no válida");
            }
            authenticateWithEmailAndPassword(email, password);
        } catch (InvalidEmailException e) {
            email_text.setError("El email proporcionado no es válido.");
            email_text.requestFocus();
            showSnackbar(e.toString());
        } catch (InvalidPasswordException e) {
            password_text.setError("La contraseña proporcionada no es válida.");
            password_text.requestFocus();
            showSnackbar(e.toString());
        }
    }

    private void authenticateWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        goToHome();
                    } else {
                        showSnackbar("La autenticación falló.");
                    }
                });
    }


    private void googleAuth() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);
        googleSignInClient.signOut();
        startActivityForResult(googleSignInClient.getSignInIntent(), GOOGLE_SIGN_IN);
    }

    public void githubAuth() {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
        provider.addCustomParameter("login", "");

        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            handleAuthResult(pendingResultTask.getResult());
        } else {
            mAuth.startActivityForSignInWithProvider(LoginActivity.this, provider.build())
                    .addOnSuccessListener(this::handleAuthResult)
                    .addOnFailureListener(this::handleAuthFailure);
        }
    }

    private void handleAuthResult(AuthResult authResult) {
        if (authResult.getAdditionalUserInfo().isNewUser()) {
            dbm.addUserToDatabase(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail(), "github.com");
        }
        goToHome();
    }

    private void handleAuthFailure(Exception e) {
        // TODO: Manejar el fallo.
    }


    private void keepSession(boolean saveSession) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("saveSession", saveSession);
        editor.commit();

        if (mAuth.getCurrentUser() != null && saveSession) {
            mAuth.getCurrentUser().getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String token = task.getResult().getToken();
                            editor.putString("idToken", token);
                            editor.commit();
                        } else {
                            throw new RuntimeException("No se ha conseguido el user idToken");
                        }
                    });
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            handleGoogleSignInResult(data);
        }
    }

    private void handleGoogleSignInResult(Intent data) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult();
            if (account == null) {
                // TODO: GESTIONAR ESTO
            }
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                            dbm.addUserToDatabase(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail(), "google.com");
                        }
                        goToHome();
                    } else {
                        showSnackbar("Authentication failed.");
                    }
                }
            });
        } catch (Exception e) {
            Log.w("ERROR", e.toString());
        }
    }


    private void goToNewActivity(Class<?> cls) {
        keepSession(keepSession_cb.isChecked());
        Intent intent = new Intent(this, cls);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        onPause();
    }

    private void goToHome() {
        goToNewActivity(MainActivity.class);
    }

    private void showSnackbar(String message) {
        Snackbar.make(login_button, message, Snackbar.LENGTH_SHORT).show();
    }

}

/*

    private void method() {
        // Crear la lista de lenguajes
        ArrayList<ProgrammingLanguage> lenguajes = new ArrayList<>();

        // Kotlin
        ArrayList<Course> kotlinCourses = new ArrayList<>();
        Course kotlinCourse = new Course("Curso de Kotlin básico", "Introducción a Kotlin", "Kotlin");
        kotlinCourse.addLesson(new Lesson("Declaración de variables"," En Kotlin, puedes declarar una variable utilizando la palabra clave val o var. La diferencia entre ambas es que val declara una variable inmutable (no se puede cambiar después de asignar un valor), mientras que var declara una variable mutable (se puede cambiar su valor).", kotlinCourse));
        kotlinCourse.addLesson(new Lesson("Inferencia de tipo","Kotlin tiene inferencia de tipo, lo que significa que no siempre es necesario especificar explícitamente el tipo de dato de una variable. El compilador de Kotlin puede deducir el tipo de dato en función del valor asignado.", kotlinCourse));
        kotlinCourses.add(kotlinCourse);
        kotlinCourse = new Course("Curso de Kotlin avanzado", "Programación orientada a objetos en Kotlin", "Kotlin");
        kotlinCourse.addLesson(new Lesson("Clases","En Kotlin, las clases son la base de la POO. Se utilizan para definir modelos de objetos que encapsulan atributos (variables) y comportamientos (funciones) relacionados. Puedes crear una clase utilizando la palabra clave \"class\" seguida del nombre de la clase.", kotlinCourse));
        kotlinCourse.addLesson(new Lesson("Objetos","Un objeto es una instancia de una clase. Puedes crear objetos utilizando la palabra clave \"val\" o \"var\" seguida del nombre del objeto, seguido por el operador de asignación y la llamada al constructor de la clase. Por ejemplo, si tienes una clase llamada \"Persona\", puedes crear un objeto llamado \"persona\" de la siguiente manera: \"val persona = Persona()\".", kotlinCourse));
        kotlinCourses.add(kotlinCourse);
        HashSet<String> kotlinTags = new HashSet<>();
        kotlinTags.add("OOP");
        kotlinTags.add("Lenguaje Funcional");
        ProgrammingLanguage kotlin = new ProgrammingLanguage("Kotlin", "Kotlin es un lenguaje de programación multiplataforma, orientado a objetos y funcional. Es un lenguaje estáticamente tipado que se ejecuta en la JVM y que también se puede compilar a JavaScript o nativo.", kotlinCourses, kotlinTags, R.drawable.logo_kotlin);
        lenguajes.add(kotlin);

        //Java
        ArrayList<Course> javaCourses = new ArrayList<>();
        Course javaCourse = new Course("Curso de Java básico", "Introducción a Java", "Java");
        javaCourse.addLesson(new Lesson("Declaración de variables", "En Java, puedes declarar una variable utilizando la palabra clave 'int', 'float', 'boolean', entre otros. Debes especificar el tipo de dato y luego el nombre de la variable.", javaCourse));
        javaCourse.addLesson(new Lesson("Estructuras de control", "Java proporciona estructuras de control como 'if', 'for', 'while', entre otras, para controlar el flujo del programa.", javaCourse));
        javaCourses.add(javaCourse);
        javaCourse = new Course("Curso de Java avanzado", "Programación orientada a objetos en Java", "Java");
        javaCourse.addLesson(new Lesson("Clases", "En Java, las clases son la base de la POO. Se utilizan para definir modelos de objetos que encapsulan atributos (variables) y comportamientos (métodos) relacionados. Puedes crear una clase utilizando la palabra clave 'class' seguida del nombre de la clase.", javaCourse));
        javaCourse.addLesson(new Lesson("Herencia", "Java admite la herencia de clases mediante la palabra clave 'extends'. Puedes crear una clase base y luego crear subclases que hereden sus propiedades y métodos.", javaCourse));
        javaCourses.add(javaCourse);
        HashSet<String> javaTags = new HashSet<>();
        javaTags.add("POO");
        javaTags.add("Desarrollo de aplicaciones");
        ProgrammingLanguage java = new ProgrammingLanguage("Java", "Java es un lenguaje de programación orientado a objetos ampliamente utilizado. Es un lenguaje de propósito general que se ejecuta en la JVM y es conocido por su portabilidad y seguridad.", javaCourses, javaTags, R.drawable.logo_java);
        lenguajes.add(java);

        // Python
        ArrayList<Course> pythonCourses = new ArrayList<>();
        Course pythonCourse = new Course("Curso de Python básico", "Introducción a Python", "Python");
        pythonCourse.addLesson(new Lesson("Declaración de variables", "En Python, no es necesario declarar explícitamente el tipo de dato de una variable. Puedes asignar un valor directamente a una variable y el intérprete de Python deducirá el tipo de dato automáticamente.", pythonCourse));
        pythonCourse.addLesson(new Lesson("Estructuras de control", "Python proporciona estructuras de control como 'if', 'for', 'while', entre otras, para controlar el flujo del programa.", pythonCourse));
        pythonCourses.add(pythonCourse);
        pythonCourse = new Course("Curso de Python avanzado", "Programación orientada a objetos en Python", "Python");
        pythonCourse.addLesson(new Lesson("Clases", "En Python, las clases son la base de la POO. Se utilizan para definir modelos de objetos que encapsulan atributos (variables) y comportamientos (métodos) relacionados. Puedes crear una clase utilizando la palabra clave 'class' seguida del nombre de la clase.", pythonCourse));
        pythonCourse.addLesson(new Lesson("Herencia", "Python admite la herencia de clases mediante la palabra clave 'extends'. Puedes crear una clase base y luego crear subclases que hereden sus propiedades y métodos.", pythonCourse));
        pythonCourses.add(pythonCourse);
        HashSet<String> pythonTags = new HashSet<>();
        pythonTags.add("POO");
        pythonTags.add("Inteligencia Artificial");
        ProgrammingLanguage python = new ProgrammingLanguage("Python", "Python es un lenguaje de programación interpretado, de alto nivel, orientado a objetos y funcional. Es conocido por su sintaxis clara y concisa, lo que lo hace fácil de aprender y leer.", pythonCourses, pythonTags, R.drawable.logo_python);
        lenguajes.add(python);

        //JavaScript
        ArrayList<Course> jsCourses = new ArrayList<>();
        Course jsCourse = new Course("Curso de JavaScript básico", "Introducción a JavaScript", "JavaScript");
        jsCourse.addLesson(new Lesson("Declaración de variables", "En JavaScript, puedes declarar una variable utilizando la palabra clave 'var', 'let' o 'const'. La diferencia entre ellas es que 'var' es una variable de alcance global o de función, 'let' es una variable de alcance de bloque y 'const' es una variable inmutable.", jsCourse));
        jsCourse.addLesson(new Lesson("Estructuras de control", "JavaScript proporciona estructuras de control como 'if', 'for', 'while', entre otras, para controlar el flujo del programa.", jsCourse));
        jsCourses.add(jsCourse);
        jsCourse = new Course("Curso de JavaScript avanzado", "Programación orientada a objetos en JavaScript", "JavaScript");
        jsCourse.addLesson(new Lesson("Clases", "En JavaScript, las clases son la base de la POO. Se utilizan para definir modelos de objetos que encapsulan atributos (variables) y comportamientos (métodos) relacionados. Puedes crear una clase utilizando la palabra clave 'class' seguida del nombre de la clase.", jsCourse));
        jsCourse.addLesson(new Lesson("Herencia", "JavaScript admite la herencia de clases mediante la palabra clave 'extends'. Puedes crear una clase base y luego crear subclases que hereden sus propiedades y métodos.", jsCourse));
        jsCourses.add(jsCourse);
        HashSet<String> jsTags = new HashSet<>();
        jsTags.add("POO");
        jsTags.add("Desarrollo web");
        ProgrammingLanguage javascript = new ProgrammingLanguage("JavaScript", "JavaScript es un lenguaje de programación interpretado, orientado a objetos y utilizado principalmente para el desarrollo web. Es compatible con la mayoría de los navegadores web y se utiliza para agregar interactividad y funcionalidad a los sitios web.", jsCourses, jsTags, R.drawable.logo_js);
        lenguajes.add(javascript);

        //C++
        ArrayList<Course> cppCourses = new ArrayList<>();
        Course cppCourse = new Course("Curso de C++ básico", "Introducción a C++", "C++");
        cppCourse.addLesson(new Lesson("Declaración de variables", "En C++, puedes declarar una variable utilizando el tipo de dato seguido del nombre de la variable. Debes especificar explícitamente el tipo de dato que contendrá la variable.", cppCourse));
        cppCourse.addLesson(new Lesson("Estructuras de control", "C++ proporciona estructuras de control como 'if', 'for', 'while', entre otras, para controlar el flujo del programa.", cppCourse));
        cppCourses.add(cppCourse);
        cppCourse = new Course("Curso de C++ avanzado", "Programación orientada a objetos en C++", "C++");
        cppCourse.addLesson(new Lesson("Clases", "En C++, las clases son la base de la POO. Se utilizan para definir modelos de objetos que encapsulan atributos (variables) y comportamientos (métodos) relacionados. Puedes crear una clase utilizando la palabra clave 'class' seguida del nombre de la clase.", cppCourse));
        cppCourse.addLesson(new Lesson("Herencia", "C++ admite la herencia de clases mediante la palabra clave 'extends'. Puedes crear una clase base y luego crear subclases que hereden sus propiedades y métodos.", cppCourse));
        cppCourses.add(cppCourse);
        HashSet<String> cppTags = new HashSet<>();
        cppTags.add("POO");
        cppTags.add("Desarrollo de juegos");
        ProgrammingLanguage cpp = new ProgrammingLanguage("C++", "C++ es un lenguaje de programación de propósito general y de alto rendimiento. Es ampliamente utilizado en el desarrollo de juegos, sistemas embebidos y aplicaciones que requieren un alto nivel de control del hardware.", cppCourses, cppTags, R.drawable.logo_cpp);
        lenguajes.add(cpp);

        // Lenguaje C
        String descripcionC = "C es un lenguaje de programación de bajo nivel utilizado para programar sistemas operativos, controladores de dispositivos y otros programas que requieren un acceso directo a la memoria y al hardware.";
        ArrayList<Course> cursosC = new ArrayList<>();
        cursosC.add(new Course("Introducción a C", "Aprende los fundamentos de la programación en C", "C"));
        cursosC.add(new Course("Estructuras de datos en C", "Aprende a trabajar con estructuras de datos en C", "C"));
        HashSet<String> tagsC = new HashSet<>();
        tagsC.add("Bajo Nivel");
        tagsC.add("Lenguaje Imperativo");
        ProgrammingLanguage c = new ProgrammingLanguage("C", descripcionC, cursosC, tagsC, R.drawable.logo_c);
        lenguajes.add(c);

        // Lenguaje C++
        */
/*String descripcionCpp = "C++ es un lenguaje de programación de alto nivel utilizado para el desarrollo de aplicaciones de software, videojuegos y otros programas complejos.";
        ArrayList<Course> cursosCpp = new ArrayList<>();
        cursosCpp.add(new Course("Introducción a C++", "Aprende los fundamentos de la programación en C++", "C++"));
        cursosCpp.add(new Course("Programación orientada a objetos en C++", "Aprende a programar utilizando la metodología de programación orientada a objetos en C++","C++"));
        HashSet<String> tagsCpp = new HashSet<>();
        tagsCpp.add("Alto Nivel");
        tagsCpp.add("OOP");
        ProgrammingLanguage cpp = new ProgrammingLanguage("C++", descripcionCpp, cursosCpp, tagsCpp, R.drawable.logo_cpp);
        lenguajes.add(cpp);*//*


        // Lenguaje C#
        String descripcionCSharp = "C# es un lenguaje de programación de alto nivel desarrollado por Microsoft. Es utilizado principalmente para el desarrollo de aplicaciones en la plataforma .NET.";
        ArrayList<Course> cursosCSharp = new ArrayList<>();
        cursosCSharp.add(new Course("Introducción a C#", "Aprende los fundamentos de la programación en C#","C#"));
        cursosCSharp.add(new Course("Programación orientada a objetos en C#", "Aprende a programar utilizando la metodología de programación orientada a objetos en C#","C#"));
        HashSet<String> tagsCSharp = new HashSet<>();
        tagsCSharp.add("Alto Nivel");
        tagsCSharp.add("OOP");
        ProgrammingLanguage csh = new ProgrammingLanguage("C#", descripcionCSharp, cursosCSharp, tagsCSharp, R.drawable.logo_csh);
        lenguajes.add(csh);

        // Lenguaje Java
        */
/*String descripcionJava = "Java es un lenguaje de programación de alto nivel utilizado principalmente para el desarrollo de aplicaciones empresariales y de servidores.";
        ArrayList<Course> cursosJava = new ArrayList<>();
        cursosJava.add(new Course("Introducción a Java", "Aprende los fundamentos de la programación en Java","Java"));
        cursosJava.add(new Course("Programación orientada a objetos en Java", "Aprende a programar utilizando la metodología de programación orientada a objetos en Java","Java"));
        HashSet<String> tagsJava = new HashSet<>();
        tagsJava.add("Alto Nivel");
        tagsJava.add("OOP");
        ProgrammingLanguage java = new ProgrammingLanguage("Java", descripcionJava, cursosJava, tagsJava, R.drawable.logo_java);
        lenguajes.add(java);*//*


        // Kotlin
        */
/*ArrayList<Course> kotlinCourses = new ArrayList<>();
        kotlinCourses.add(new Course("Curso de Kotlin básico", "Introducción a Kotlin", "Kotlin"));
        kotlinCourses.add(new Course("Curso de Kotlin avanzado", "Programación orientada a objetos en Kotlin", "Kotlin"));
        HashSet<String> kotlinTags = new HashSet<>();
        kotlinTags.add("OOP");
        kotlinTags.add("Lenguaje Funcional");
        ProgrammingLanguage kotlin = new ProgrammingLanguage("Kotlin", "Kotlin es un lenguaje de programación multiplataforma, orientado a objetos y funcional. Es un lenguaje estáticamente tipado que se ejecuta en la JVM y que también se puede compilar a JavaScript o nativo.", kotlinCourses, kotlinTags, R.drawable.logo_kotlin);
        lenguajes.add(kotlin);
        *//*



        // TypeScript
        ArrayList<Course> tsCourses = new ArrayList<>();
        tsCourses.add(new Course("Curso de TypeScript básico", "Introducción a TypeScript","TypeScript"));
        tsCourses.add(new Course("Curso de TypeScript avanzado", "Programación orientada a objetos en TypeScript","TypeScript"));
        HashSet<String> tsTags = new HashSet<>();
        tsTags.add("Alto Nivel");
        ProgrammingLanguage typescript = new ProgrammingLanguage("TypeScript", "TypeScript es un lenguaje de programación de código abierto que se construye sobre JavaScript, añadiendo nuevas características y mejorando la seguridad del código. Es un lenguaje estáticamente tipado que se compila a JavaScript.", tsCourses, tsTags, R.drawable.logo_ts);
        lenguajes.add(typescript);

        // Lenguaje R
        ArrayList<Course> rCourses = new ArrayList<>();
        rCourses.add(new Course("Introduction to R", "A beginner's guide to R programming","R"));
        rCourses.add(new Course("Data Visualization in R", "Learn to create data visualizations using R","R"));
        HashSet<String> rTags = new HashSet<>(Arrays.asList("Data Science", "Statistics", "Data Visualization"));
        ProgrammingLanguage r = new ProgrammingLanguage("R", "R is a programming language and free software environment for statistical computing and graphics.", rCourses, rTags, R.drawable.logo_r);
        lenguajes.add(r);

        // Lenguaje Rust
        ArrayList<Course> rustCourses = new ArrayList<>();
        rustCourses.add(new Course("Rust Basics", "Introduction to Rust programming language","Rust"));
        rustCourses.add(new Course("Advanced Rust", "Advanced concepts and features of Rust programming language","Rust"));
        HashSet<String> rustTags = new HashSet<>(Arrays.asList("Systems Programming", "Memory Safety", "Concurrency"));
        ProgrammingLanguage rust = new ProgrammingLanguage("Rust", "Rust is a systems programming language focused on safety, speed, and concurrency.", rustCourses, rustTags, R.drawable.logo_rust);
        lenguajes.add(rust);

        // Lenguaje MATLAB
        ArrayList<Course> matlabCourses = new ArrayList<>();
        matlabCourses.add(new Course("Introduction to MATLAB", "Learn the basics of MATLAB programming language","MATLAB"));
        matlabCourses.add(new Course("MATLAB for Engineers", "Advanced MATLAB concepts for engineering applications","MATLAB"));
        HashSet<String> matlabTags = new HashSet<>(Arrays.asList("Numerical Computing", "Engineering", "Scientific Computing"));
        ProgrammingLanguage matlab = new ProgrammingLanguage("MATLAB", "MATLAB is a high-performance language for technical computing.", matlabCourses, matlabTags, R.drawable.logo_matlab);
        lenguajes.add(matlab);

        // Lenguaje PHP
        ArrayList<Course> phpCourses = new ArrayList<>();
        phpCourses.add(new Course("PHP Basics", "Introduction to PHP programming language","PHP"));
        phpCourses.add(new Course("PHP Web Development", "Building web applications using PHP","PHP"));
        HashSet<String> phpTags = new HashSet<>(Arrays.asList("Web Development", "Server-Side Scripting", "Database Integration"));
        ProgrammingLanguage php = new ProgrammingLanguage("PHP", "PHP is a popular general-purpose scripting language that is especially suited to web development.", phpCourses, phpTags, R.drawable.logo_php);
        lenguajes.add(php);

        // Lenguaje HTML
        ArrayList<Course> htmlCourses = new ArrayList<>();
        htmlCourses.add(new Course("HTML Fundamentals", "Introduction to HTML markup language","HTML"));
        htmlCourses.add(new Course("CSS Basics", "Introduction to CSS styling language","HTML"));
        HashSet<String> htmlTags = new HashSet<>(Arrays.asList("Web Development", "Front-End Development", "Markup Languages"));
        ProgrammingLanguage html = new ProgrammingLanguage("HTML", "HTML is the standard markup language for creating web pages, and CSS is used to style them.", htmlCourses, htmlTags, R.drawable.logo_html);
        lenguajes.add(html);

        // Lenguaje Go
        ArrayList<Course> goCourses = new ArrayList<>();
        goCourses.add(new Course("Go Basics", "Introduction to Go programming language","Go"));
        goCourses.add(new Course("Advanced Go", "Advanced concepts and features of Go programming language","Go"));
        HashSet<String> goTags = new HashSet<>(Arrays.asList("Systems Programming", "Concurrent Programming"));
        ProgrammingLanguage go = new ProgrammingLanguage("Go", "Go is a statically typed, compiled language designed for efficient and concurrent programming.", goCourses, goTags, R.drawable.logo_go);
        lenguajes.add(go);

        // Lenguaje Objective-C
        String descripcionObjC = "Objective-C is a general-purpose, object-oriented programming language that adds Smalltalk-style messaging to the C programming language.";
        ArrayList<Course> objCCourses = new ArrayList<>();
        objCCourses.add(new Course("Objective-C Basics", "Introduction to Objective-C programming language","Objective-C"));
        objCCourses.add(new Course("Objective-C for iOS Development", "Building iOS applications using Objective-C","Objective-C"));
        HashSet<String> objCTags = new HashSet<>(Arrays.asList("iOS Development", "Object-Oriented Programming"));
        ProgrammingLanguage objC = new ProgrammingLanguage("Objective-C", descripcionObjC, objCCourses, objCTags, R.drawable.logo_obj_c);
        lenguajes.add(objC);

        // Lenguaje Ruby
        ArrayList<Course> rubyCourses = new ArrayList<>();
        rubyCourses.add(new Course("Ruby Basics", "Introduction to Ruby programming language","Ruby"));
        rubyCourses.add(new Course("Ruby on Rails", "Building web applications using Ruby on Rails framework","Ruby"));
        HashSet<String> rubyTags = new HashSet<>(Arrays.asList("Web Development", "Scripting", "Object-Oriented Programming"));
        ProgrammingLanguage ruby = new ProgrammingLanguage("Ruby", "Ruby is a dynamic, reflective, object-oriented, general-purpose programming language.", rubyCourses, rubyTags, R.drawable.logo_ruby);
        lenguajes.add(ruby);

        // Lenguaje SQL
        ArrayList<Course> sqlCourses = new ArrayList<>();
        sqlCourses.add(new Course("SQL Fundamentals", "Introduction to SQL programming language","SQL"));
        sqlCourses.add(new Course("Advanced SQL", "Advanced concepts of SQL and database management","SQL"));
        HashSet<String> sqlTags = new HashSet<>(Arrays.asList("Database Management", "Query Language"));
        ProgrammingLanguage sql = new ProgrammingLanguage("SQL", "SQL is a domain-specific language used in programming and designed for managing data held in a relational database management system.", sqlCourses, sqlTags, R.drawable.logo_sql);
        lenguajes.add(sql);

        // Lenguaje Swift
        ArrayList<Course> swiftCourses = new ArrayList<>();
        swiftCourses.add(new Course("Swift Basics", "Introduction to Swift programming language","Swift"));
        swiftCourses.add(new Course("iOS App Development with Swift", "Building iOS applications using Swift","Swift"));
        HashSet<String> swiftTags = new HashSet<>(Arrays.asList("iOS Development", "Object-Oriented Programming"));
        ProgrammingLanguage swift = new ProgrammingLanguage("Swift", "Swift is a powerful and intuitive programming language created by Apple for building iOS, macOS, watchOS, and tvOS apps.", swiftCourses, swiftTags, R.drawable.logo_swift);
        lenguajes.add(swift);

        // Lenguaje Scala
        ArrayList<Course> scalaCourses = new ArrayList<>();
        scalaCourses.add(new Course("Scala Basics", "Introduction to Scala programming language","Scala"));
        scalaCourses.add(new Course("Functional Programming in Scala", "Learn functional programming concepts in Scala","Scala"));
        HashSet<String> scalaTags = new HashSet<>(Arrays.asList("Functional Programming", "JVM", "Concurrency"));
        ProgrammingLanguage scala = new ProgrammingLanguage("Scala", "Scala is a modern multi-paradigm programming language designed to express common programming patterns in a concise, elegant, and type-safe way.", scalaCourses, scalaTags, R.drawable.logo_scala);
        lenguajes.add(scala);

        // Lenguaje Pascal
        ArrayList<Course> pascalCourses = new ArrayList<>();
        pascalCourses.add(new Course("Pascal Basics", "Introduction to Pascal programming language","Pascal"));
        pascalCourses.add(new Course("Data Structures in Pascal", "Learn about data structures in Pascal","Pascal"));
        HashSet<String> pascalTags = new HashSet<>();
        pascalTags.add("Structured Programming");
        pascalTags.add("Algorithm Design");
        ProgrammingLanguage pascal = new ProgrammingLanguage("Pascal", "Pascal is a procedural programming language designed for teaching programming and producing reliable and efficient programs.", pascalCourses, pascalTags, R.drawable.logo_pascal);
        lenguajes.add(pascal);

        // Lenguaje Elixir
        ArrayList<Course> elixirCourses = new ArrayList<>();
        elixirCourses.add(new Course("Elixir Basics", "Introduction to Elixir programming language","Elixir"));
        elixirCourses.add(new Course("Concurrent Programming with Elixir", "Learn concurrent programming using Elixir","Elixir"));
        HashSet<String> elixirTags = new HashSet<>(Arrays.asList("Functional Programming", "Concurrency", "Scalability"));
        ProgrammingLanguage elixir = new ProgrammingLanguage("Elixir", "Elixir is a functional, concurrent, and extensible programming language built on the Erlang virtual machine (BEAM).", elixirCourses, elixirTags, R.drawable.logo_elixir);
        lenguajes.add(elixir);

        // Lenguaje Erlang
        ArrayList<Course> erlangCourses = new ArrayList<>();
        erlangCourses.add(new Course("Erlang Basics", "Introduction to Erlang programming language","Erlang"));
        erlangCourses.add(new Course("OTP Framework in Erlang", "Learn to build fault-tolerant systems with OTP in Erlang","Erlang"));
        HashSet<String> erlangTags = new HashSet<>(Arrays.asList("Concurrent Programming", "Fault-Tolerant Systems", "Telecommunications"));
        ProgrammingLanguage erlang = new ProgrammingLanguage("Erlang", "Erlang is a general-purpose, concurrent, and functional programming language used primarily in telecommunication, banking, and e-commerce.", erlangCourses, erlangTags, R.drawable.logo_erlang);
        lenguajes.add(erlang);

        // Lenguaje Scheme
        ArrayList<Course> schemeCourses = new ArrayList<>();
        schemeCourses.add(new Course("Scheme Basics", "Introduction to Scheme programming language","Scheme"));
        schemeCourses.add(new Course("Functional Programming in Scheme", "Learn functional programming concepts in Scheme","Scheme"));
        HashSet<String> schemeTags = new HashSet<>(Arrays.asList("Lisp", "Functional Programming", "Language Design"));
        ProgrammingLanguage scheme = new ProgrammingLanguage("Scheme", "Scheme is a dialect of the Lisp programming language known for its simplicity and elegance. It is often used for teaching programming and implementing programming language concepts.", schemeCourses, schemeTags, R.drawable.logo_scheme);
        lenguajes.add(scheme);

        // Lenguaje Postscript
        ArrayList<Course> postscriptCourses = new ArrayList<>();
        postscriptCourses.add(new Course("Introduction to Postscript", "Learn the basics of Postscript Postscript","Scheme"));
        postscriptCourses.add(new Course("Advanced Postscript", "Advanced concepts and features of Postscript language","Postscript"));
        HashSet<String> postscriptTags = new HashSet<>(Arrays.asList("Page Description Language", "Printing", "Graphics"));
        ProgrammingLanguage postscript = new ProgrammingLanguage("Postscript", "Postscript is a programming language that is primarily used for describing the appearance of printed pages.", postscriptCourses, postscriptTags, R.drawable.logo_postiscript);
        lenguajes.add(postscript);

        // Lenguaje Basic
        ArrayList<Course> basicCourses = new ArrayList<>();
        basicCourses.add(new Course("Introduction to Basic", "Learn the basics of Basic programming language","Basic"));
        basicCourses.add(new Course("Advanced Basic", "Advanced concepts and features of Basic programming language","Basic"));
        HashSet<String> basicTags = new HashSet<>();
        basicTags.add("Beginner");
        ProgrammingLanguage basic = new ProgrammingLanguage("Basic", "Basic is a family of general-purpose programming languages that are simple to learn and widely used.", basicCourses, basicTags, R.drawable.logo_basic);
        lenguajes.add(basic);

        // Lenguaje Cobol
        ArrayList<Course> cobolCourses = new ArrayList<>();
        cobolCourses.add(new Course("Introduction to Cobol", "Learn the basics of Cobol programming language","Cobol"));
        cobolCourses.add(new Course("Advanced Cobol", "Advanced concepts and features of Cobol programming language","Cobol"));
        HashSet<String> cobolTags = new HashSet<>(Arrays.asList("Business Applications", "Legacy Systems", "Mainframe Programming"));
        ProgrammingLanguage cobol = new ProgrammingLanguage("Cobol", "Cobol is a high-level programming language designed for business applications.", cobolCourses, cobolTags, R.drawable.logo_cobol);
        lenguajes.add(cobol);

        // Lenguaje Fortran
        ArrayList<Course> fortranCourses = new ArrayList<>();
        fortranCourses.add(new Course("Introduction to Fortran", "Learn the basics of Fortran programming language","Fortran"));
        fortranCourses.add(new Course("Advanced Fortran", "Advanced concepts and features of Fortran programming language","Fortran"));
        HashSet<String> fortranTags = new HashSet<>(Arrays.asList("Scientific Computing", "Numerical Analysis", "High-Performance Computing"));
        ProgrammingLanguage fortran = new ProgrammingLanguage("Fortran", "Fortran is a general-purpose, imperative programming language that is particularly suited to numeric and scientific computing.", fortranCourses, fortranTags, R.drawable.logo_fortran);
        lenguajes.add(fortran);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference languagesCollectionRef = db.collection("programmingLanguages");

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
                DocumentReference courseDocRef = coursesCollectionRef.document(course.getName());
                Map<String, Object> courseData = new HashMap<>();
                courseData.put("descripcion", course.getDescription());
                courseDocRef.set(courseData);

                // Crear la colección de lessons para el lenguaje
                CollectionReference lessonCollectionRef = courseDocRef.collection("lessons");
                for (Lesson lesson : course.getLessons()) {
                    DocumentReference lessonDocRef = lessonCollectionRef.document(lesson.getName());
                    Map<String, Object> lessonData = new HashMap<>();
                    lessonData.put("descripcion", lesson.getLesson());
                    lessonDocRef.set(lessonData);
                }
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
*/
