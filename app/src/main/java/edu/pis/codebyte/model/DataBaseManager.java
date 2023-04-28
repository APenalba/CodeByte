package edu.pis.codebyte.model;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class DataBaseManager {
    private static DataBaseManager dbm;
    private FirebaseFirestore db;
    private OnLoadUserPictureUrlListener userPictureUrlListener;
    private OnLoadUserProviderListener providerListener;
    private OnLoadProgrammingLanguages languagesListener;



    public interface OnLoadUserPictureUrlListener {
        public void onLoadUserPictureUrl(String pictureUrl);
    }

    public interface OnLoadUserProviderListener {
        public void onLoadUserProvider(String userProvider);
    }

    public interface OnLoadProgrammingLanguages {
        public void onLoadProgrammingLanguages(ArrayList<ProgrammingLanguage> languages);
    }

    private DataBaseManager() {
        db = FirebaseFirestore.getInstance();
    }

    public static DataBaseManager getInstance() {
        if (dbm == null) dbm = new DataBaseManager();
        return dbm;
    }

    public void setProviderListener(OnLoadUserProviderListener providerListener) {
        this.providerListener = providerListener;
    }

    public void setOnLoadUserPicture(OnLoadUserPictureUrlListener listener) {
        this.userPictureUrlListener = listener;
    }

    public void setOnLoadProgrammingLanguages(OnLoadProgrammingLanguages listener) {
        this.languagesListener = listener;
    }

    /**
     * Este metodo añade un usuario a la coleccion "users" de Firestore
     * @param uId
     * @param username
     * @param email
     * @param provider (google.com / github.com / email_password)
     */
    public void addUserToDatabase(String uId, String username, String email, String provider) {
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
                    Log.d(TAG, "Usuario con ID " + uId + " añadido a la base de datos");
                } else {
                    Log.d(TAG, "Ya existe un usuario con ID" + uId);
                }
                return null;
            }
        });
    }

    /**
     * Actualiza el username de un usuario
     * @param uId
     * @param new_username
     */
    public void updateUserUsername(String uId, String new_username) {
        DocumentReference docRef = db.collection("users").document(uId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("username", new_username);

        docRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Username del usuario con ID \" + uId + \"actualizado correctamente");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error al actualizar el username del usuario con ID " + uId, e);
            }
        });
    }


    /**
     * Este metodo actualiza el correo de un usuario
     * @param uId
     * @param new_email
     */
    public void updateUserEmail(String uId, String new_email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("email", new_email);

        docRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Email del usuario con ID \" + uId + \"actualizado correctamente");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error al actualizar el email del usuario con ID \" + uId", e);
            }
        });
    }

    /**
     * Metodo para subir una imagen a Firebase Storage
     * @param uId
     * @param imagenPerfilUri
     * @param successListener
     */
    public void subirImagenPerfil(String uId, Uri imagenPerfilUri, OnSuccessListener<Uri> successListener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference userImageRef = storageRef.child("userImages/" + uId + ".jpg");

        userImageRef.putFile(imagenPerfilUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "Imagen subida correctamente");
                    userImageRef.getDownloadUrl().addOnSuccessListener(successListener);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error al subir la imagen a la base de datos", e);
                    }
                });
    }

    /**
     * Metodo para actualizar la imagen de un usuario
     * @param uId
     * @param imageUrl
     */
    public void updateUserImageUrl(String uId, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("profileImageURL", imageUrl);

        userRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Imagen del usuario con ID \" + uId + \"actualizada correctamente");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error al actualizar la imagen del usuario con ID \" + uId", e);
                    }
                });

    }

    /**
     * Este metodo devuelve el documento de un usuario a traves de un listener por parametros
     * @param uid
     * @param listener OnCompleteListener<DocumentSnapshot>
     */
    public void getUserDocument(String uid, OnCompleteListener<DocumentSnapshot> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid).get().addOnCompleteListener(listener);
    }

    /**
     * Este metodo añade a la coleccion "comments" de Firestore un comentario o problema indicado por un usuario
     * @param usuario
     * @param comentario
     * @param fecha
     * @param view
     */
    public void agregarComentario(String usuario, String comentario, Date fecha, View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> comentarioData = new HashMap<>();
        comentarioData.put("usuario", usuario);
        comentarioData.put("comentario", comentario);
        comentarioData.put("fecha", fecha);

        db.collection("comments")
                .add(comentarioData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Toast.makeText(context, "Problema enviado correctamente", Toast.LENGTH_SHORT).show();
                        Snackbar.make(view, "Problema enviado correctamente",Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(context, "Problema al enviar el problema", Toast.LENGTH_SHORT).show();
                        Snackbar.make(view, "Problema al enviar el problema",Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    public void loadImageURL() {
        dbm.getUserDocument(FirebaseAuth.getInstance().getCurrentUser().getUid(), new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Crear objeto User con los datos del usuario de Firestore
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String profileImageURL = document.getString("profileImageURL");
                        //imageURL.setValue(profileImageURL);ç
                        userPictureUrlListener.onLoadUserPictureUrl(profileImageURL);
                    }
                } else {
                    // Error al obtener datos del usuario de Firestore
                }
            }
        });
    }

    public void loadUserProvider() {
        dbm.getUserDocument(FirebaseAuth.getInstance().getCurrentUser().getUid(), new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Crear objeto User con los datos del usuario de Firestore
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String provider = document.getString("provider");
                        //imageURL.setValue(profileImageURL);ç
                        providerListener.onLoadUserProvider(provider);
                    }
                } else {
                    // Error al obtener datos del usuario de Firestore
                }
            }
        });
    }

    public void recuperarTemasCompletados(String uId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Recuperar los temas completados del usuario
        db.collection("Progreso").document(uId).collection("TemasCompletados").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Log.d(TAG, "Tema completado: " + documentSnapshot.getId());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error al recuperar el progreso del usuario", e);
                    }
                });
    }

    public void registrarTemaCompletado(String uId, String idTema) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Agregar un tema completado al progreso del usuario
        db.collection("Progreso").document(uId).collection("TemasCompletados").document(idTema).set(new HashMap<String, Object>())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Tema completado añadido al progreso del usuario");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error al añadir el tema completado al progreso del usuario", e);
                    }
                });

    }

    public void loadProgrammingLanguages(){

        ArrayList<ProgrammingLanguage> programmingLanguages = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ProgrammingLanguages")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String languageName = document.getId();
                            String languageDescription = document.getString("descripcion").trim();
                            int resourceImageId = document.getLong("resourceImageId").intValue();
                            ArrayList<String> tags =(ArrayList<String>) document.get("tags");
                            HashSet<String> tags_set = new HashSet<>();
                            tags_set.addAll(tags);
                            ProgrammingLanguage programmingLanguage = new ProgrammingLanguage(languageName, languageDescription, tags_set, resourceImageId);
                            //ProgrammingLanguage programmingLanguage = new ProgrammingLanguage(languageName, languageDescription,courses, resourceImageId);
                            programmingLanguages.add(programmingLanguage);
                        }
                        languagesListener.onLoadProgrammingLanguages(programmingLanguages);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });


    }

    private void loadLanguageCourses(QueryDocumentSnapshot doc, ProgrammingLanguage pl) {
        doc.getReference().collection("courses").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for( QueryDocumentSnapshot document :task.getResult()) {
                    Course course = new Course(document.getId(), document.getString("description"));
                    pl.addCourse(course);
                }
            }
        });
    }
}
