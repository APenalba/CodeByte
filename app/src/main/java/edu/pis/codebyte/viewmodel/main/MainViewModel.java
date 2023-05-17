package edu.pis.codebyte.viewmodel.main;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.Date;

import edu.pis.codebyte.model.DataBaseManager;
import edu.pis.codebyte.model.ProgrammingLanguage;
import edu.pis.codebyte.model.User;
import edu.pis.codebyte.model.UserProgress;

public class MainViewModel extends ViewModel implements DataBaseManager.OnLoadProgrammingLanguagesListener, DataBaseManager.OnLoadUserListener, DataBaseManager.OnLoadUserProgressListener {

    private static MainViewModel mainViewModel;




    private static String uId;
    private MutableLiveData<String> uEmail;
    private MutableLiveData<String> uImageURL;
    private MutableLiveData<String> uProvider;
    private MutableLiveData<String> uUsername;

    private MutableLiveData<UserProgress> userProgress;
    private MutableLiveData<ArrayList<ProgrammingLanguage>> languages;
    private DataBaseManager dbm;
    private FirebaseUser firebaseUser;

    public MainViewModel() {
        dbm = DataBaseManager.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dbm.setOnLoadUserListener(this);
        dbm.setOnLoadProgrammingLanguagesListener(this);
        dbm.setOnLoadUserProgressListener(this);

        uId = firebaseUser.getUid();
        uEmail = new MutableLiveData<>("LOADING...");
        uImageURL = new MutableLiveData<>();
        uProvider = new MutableLiveData<>("LOADING...");
        uUsername = new MutableLiveData<>("LOADING...");
        userProgress = new MutableLiveData<>();
        languages = new MutableLiveData<>();

        dbm.loadProgrammingLanguages();
        dbm.getUserFromDatabase(uId);
        dbm.getUserProgressFromDatabase(uId);
    }

    public static MainViewModel getInstance(){
        if(mainViewModel == null || FirebaseAuth.getInstance().getCurrentUser().getUid() != uId) {
            mainViewModel = new MainViewModel();
        }
        return mainViewModel;

    }

    @Override
    public void onLoadProgrammingLanguages(ArrayList<ProgrammingLanguage> languages) {
        this.languages.setValue(languages);
    }

    @Override
    public void onLoadUser(User user) {
        uEmail.setValue(user.getEmail());
        uImageURL.setValue(user.getuImageURL());
        uProvider.setValue(user.getProvider());
        uUsername.setValue(user.getUsername());
    }

    @Override
    public void onLoadUserImageURL(String imageURL) {
        this.uImageURL.setValue(imageURL);
    }

    @Override
    public void onLoadUserProvider(String userProvider) {
        this.uProvider.setValue(userProvider);
    }

    @Override
    public void onLoadUserUsername(String username) {
        this.uUsername.setValue(username);
    }

    @Override
    public void onLoadUserEmail(String email) {
        this.uEmail.setValue(email);
    }

    @Override
    public void onLoadUserProgress(UserProgress up) {
        this.userProgress.setValue(up);
    }

    public String getuId() {
        return uId;
    }

    public MutableLiveData<String> getuEmail() {
        return uEmail;
    }

    public MutableLiveData<String> getuImageURL() {
        return uImageURL;
    }

    public MutableLiveData<String> getuProvider() {
        return uProvider;
    }

    public MutableLiveData<String> getuUsername() {
        return uUsername;
    }

    public MutableLiveData<UserProgress> getUserProgress() {
        return userProgress;
    }

    public MutableLiveData<ArrayList<ProgrammingLanguage>> getLanguages() {
        return languages;
    }

    public void changeuUsername(String new_username, View view) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(new_username)
                .build();
        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // El nombre de usuario se ha actualizado correctamente
                            //Toast.makeText(context, "Nombre de usuario actualizado", Toast.LENGTH_SHORT).show();
                            Snackbar.make(view, "Nombre de usuario actualizado",Snackbar.LENGTH_SHORT).show();
                            dbm.updateUserUsername(firebaseUser.getUid(), new_username);
                        } else {
                            // Se produjo un error al actualizar el nombre de usuario
                            //Toast.makeText(context, "Error al actualizar el nombre de usuario", Toast.LENGTH_SHORT).show();
                            Snackbar.make(view, "Error al actualizar el nombre de usuario",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void changeuEmail(String new_email, String password, Context context) {
        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseUser.updateEmail(new_email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // El correo electrónico se ha actualizado correctamente
                                                //Toast.makeText(context, "Correo electrónico actualizado", Toast.LENGTH_SHORT).show();
                                                Snackbar.make(new View(context), "Correo electrónico actualizado",Snackbar.LENGTH_SHORT).show();
                                                dbm.updateUserEmail(firebaseUser.getUid(), new_email);
                                            } else {
                                                // Se produjo un error al actualizar el correo electrónico
                                                //Toast.makeText(context, "Error al actualizar el correo electrónico", Toast.LENGTH_SHORT).show();
                                                Snackbar.make(new View(context), "Error al actualizar el correo electrónico",Snackbar.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // Se produjo un error al autenticar al usuario
                            //Toast.makeText(context, "Error al autenticar al usuario", Toast.LENGTH_SHORT).show();
                            Snackbar.make(new View(context), "Error al autenticar al usuario", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void changeuPassword(String current_password, String new_password, Context context) {
        if (firebaseUser != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), current_password);

            firebaseUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseUser.updatePassword(new_password)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                                                    Snackbar.make(new View(context), "Contraseña actualizada",Snackbar.LENGTH_SHORT).show();
                                                } else {
                                                    //Toast.makeText(context, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                                                    Snackbar.make(new View(context), "Error al actualizar la contraseña",Snackbar.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                // Se produjo un error al reautenticar al usuario
                                //Toast.makeText(context, "Error al reautenticar al usuario", Toast.LENGTH_SHORT).show();
                                Snackbar.make(new View(context), "Error al reautenticar al usuario",Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void changeuImageURL(Uri imagenPerfilUri, Context context) {
        dbm.subirImagenPerfil(firebaseUser.getUid(), imagenPerfilUri, url -> {
                    // La imagen se subió exitosamente y se obtuvo la URL
                    dbm.updateUserImageUrl(firebaseUser.getUid(), url.toString());
                });
    }

    public void enviarComentario(String contenidoComentario, View view) {
        // Obtener la fecha actual
        Date fechaActual = new Date();

        // Obtener el usuario actual (por ejemplo, de la sesión)
        String usuarioActual = firebaseUser.getUid();

        // Guardar el comentario en Firestore
        dbm.agregarComentario(usuarioActual, contenidoComentario, fechaActual, view);
    }
}
