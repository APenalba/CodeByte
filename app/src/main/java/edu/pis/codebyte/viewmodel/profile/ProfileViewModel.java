package edu.pis.codebyte.viewmodel.profile;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import edu.pis.codebyte.view.profile.ProfileActivity;

public class ProfileViewModel extends ViewModel {

    private FirebaseUser currentUser;

    private MutableLiveData<String> username;
    private MutableLiveData<String> email;

    public ProfileViewModel() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        username = new MutableLiveData<>(currentUser.getDisplayName());
        email = new MutableLiveData<>(currentUser.getEmail());
    }

    public void cambiarNombreUsuario(String new_username, Context context) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(new_username)
                .build();
        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // El nombre de usuario se ha actualizado correctamente
                            Toast.makeText(context, "Nombre de usuario actualizado", Toast.LENGTH_SHORT).show();
                            username.setValue(currentUser.getDisplayName());
                        } else {
                            // Se produjo un error al actualizar el nombre de usuario
                            Toast.makeText(context, "Error al actualizar el nombre de usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public MutableLiveData<String> getUsername() {
        return username;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }
}
