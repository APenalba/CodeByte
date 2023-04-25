package edu.pis.codebyte.viewmodel.profile;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

import edu.pis.codebyte.model.DataBaseManager;


public class ProfileViewModel extends ViewModel {

    private MutableLiveData<String> username;
    private MutableLiveData<String> email;
    private MutableLiveData<String> imageURL;
    private MutableLiveData<String> userProvider;
    private DataBaseManager dbm;
    private FirebaseUser firebaseUser;

    public ProfileViewModel() {
        dbm = DataBaseManager.getInstance();
        dbm.setOnLoadUserPicture(new DataBaseManager.OnLoadUserPictureUrlListener() {
            @Override
            public void onLoadUserPictureUrl(String pictureUrl) {
                imageURL.setValue(pictureUrl);
            }
        });
        dbm.setProviderListener(new DataBaseManager.OnLoadUserProviderListener() {
            @Override
            public void onLoadUserProvider(String newProvider) {
                userProvider.setValue(newProvider);
            }
        });
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        username = new MutableLiveData<>(firebaseUser.getDisplayName());
        email = new MutableLiveData<>(firebaseUser.getEmail());
        imageURL = new MutableLiveData<>("");
        dbm.loadImageURL();
        dbm.loadUserProvider();
    }

    public MutableLiveData<String> getUsername() {
        return username;
    }

    public MutableLiveData<String> getUserProvider() {
        return userProvider;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public MutableLiveData<String> getImageURL() {
        return imageURL;
    }

    public void getCurrentUserProvider(OnCompleteListener<DocumentSnapshot> listener) {
        dbm.getUserDocument(firebaseUser.getUid(), listener);
    }

    public void cambiarNombreUsuario(String new_username, Context context) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(new_username)
                .build();
        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // El nombre de usuario se ha actualizado correctamente
                            Toast.makeText(context, "Nombre de usuario actualizado", Toast.LENGTH_SHORT).show();
                            username.setValue(firebaseUser.getDisplayName());
                            dbm.updateUserUsername(firebaseUser.getUid(), new_username);
                        } else {
                            // Se produjo un error al actualizar el nombre de usuario
                            Toast.makeText(context, "Error al actualizar el nombre de usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void cambiarContrasena(String current_password, String new_password, Context context) {
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
                                                    Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(context, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                // Se produjo un error al reautenticar al usuario
                                Toast.makeText(context, "Error al reautenticar al usuario", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void cambiarCorreoElectronico(String new_email, String password, Context context) {
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
                                                Toast.makeText(context, "Correo electrónico actualizado", Toast.LENGTH_SHORT).show();
                                                email.setValue(firebaseUser.getEmail());
                                                dbm.updateUserEmail(firebaseUser.getUid(), new_email);
                                            } else {
                                                // Se produjo un error al actualizar el correo electrónico
                                                Toast.makeText(context, "Error al actualizar el correo electrónico", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // Se produjo un error al autenticar al usuario
                            Toast.makeText(context, "Error al autenticar al usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void cambiarImagenPerfil(Uri imagenPerfilUri, Context context) {
        dbm.subirImagenPerfil(firebaseUser.getUid(), imagenPerfilUri,
                url -> {
                    // La imagen se subió exitosamente y se obtuvo la URL
                    dbm.updateUserImageUrl(firebaseUser.getUid(), url.toString());
                    imageURL.setValue(url.toString());
                });
    }

    public void enviarComentario(String contenidoComentario, Context context) {
        // Obtener la fecha actual
        Date fechaActual = new Date();

        // Obtener el usuario actual (por ejemplo, de la sesión)
        String usuarioActual = firebaseUser.getUid();

        // Guardar el comentario en Firestore
        dbm.agregarComentario(usuarioActual, contenidoComentario, fechaActual, context);
    }


}
