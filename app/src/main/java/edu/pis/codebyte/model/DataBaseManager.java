package edu.pis.codebyte.model;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class DataBaseManager {
    private static DataBaseManager dbm;
    private StorageReference mStorageRef;

    private DataBaseManager() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public static DataBaseManager getInstance() {
        if (dbm == null) dbm = new DataBaseManager();
        return dbm;
    }


    public boolean addUserToDatabase(String uId, String username, String email, String provider) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection("users");

        DocumentReference document = collection.document(uId);
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(document);
                if (!snapshot.exists()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("username", username);
                    user.put("email", email);
                    user.put("profileImageURL", "" );
                    user.put("provider", provider);
                    transaction.set(document, user);
                    Log.d(TAG, "DocumentSnapshot added with ID: " + uId);
                } else {
                    Log.d(TAG, "User already exists with ID: " + uId);
                }
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction succeeded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failed", e);
            }
        });
        return true;
    }

    public void updateUserUsername(String uId, String new_username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("username", new_username);

        docRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Username updated on DB successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating username on DB", e);
            }
        });
    }


    public void updateUserEmail(String uId, String new_email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("email", new_email);

        docRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Email updated successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating email", e);
            }
        });
    }

    public void updateUserProfileImageURL(String uId, String newProfileImageURL) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("profileImageURL", newProfileImageURL);

        docRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Profile image URL updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating profile image URL", e);
                    }
                });
    }

    public void subirImagenPerfil(String uId, Uri imagenPerfilUri, OnSuccessListener<Uri> successListener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference userImageRef = storageRef.child("userImages/" + uId + ".jpg");

        userImageRef.putFile(imagenPerfilUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // La imagen se subió exitosamente
                    // Guarda el link de la imagen en Firestore
                    userImageRef.getDownloadUrl().addOnSuccessListener(successListener);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al subir la imagen
                    }
                });
    }

    public void updateUserImageUrl(String uId, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("profileImageURL", imageUrl);

        userRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Link de la imagen guardado exitosamente en Firestore
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al guardar el link de la imagen en Firestore
                    }
                });

    }

    public void getUser(String uid, OnCompleteListener<DocumentSnapshot> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid).get().addOnCompleteListener(listener);
    }
}
