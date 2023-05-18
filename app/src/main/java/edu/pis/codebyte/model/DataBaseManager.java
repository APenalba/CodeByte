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
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class DataBaseManager {
    private static DataBaseManager dbm;
    private FirebaseFirestore db;
    private OnLoadProgrammingLanguagesListener languagesListener;
    private OnLoadUserListener userListener;

    public interface OnLoadProgrammingLanguagesListener {
        void onLoadProgrammingLanguages(ArrayList<ProgrammingLanguage> languages);
    }

    public interface OnLoadUserListener {
        void onLoadUser(User user);

        void onLoadUserImageURL(String imageURL);

        void onLoadUserUsername(String username);

        void onLoadUserEmail(String email);
        //void onLoadUserProgress();
    }

    private DataBaseManager() {
        db = FirebaseFirestore.getInstance();
    }

    public static DataBaseManager getInstance() {
        if (dbm == null) dbm = new DataBaseManager();
        return dbm;
    }

    public void setOnLoadUserListener(OnLoadUserListener userListener) {
        this.userListener = userListener;
    }

    public void setOnLoadProgrammingLanguagesListener(OnLoadProgrammingLanguagesListener listener) {
        this.languagesListener = listener;
    }

    /**
     * Este metodo añade un usuario a la coleccion "users" de Firestore
     *
     * @param uId
     * @param username
     * @param email
     * @param provider (google.com / github.com / email_password)
     */
    public void addUserToDatabase(String uId, String username, String email, String provider) {
        CollectionReference usersCollection = db.collection("users");
        CollectionReference progressCollection = db.collection("progress");

        DocumentReference userDocument = usersCollection.document(uId);
        DocumentReference userProgressDocument = progressCollection.document(uId);


        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(userDocument);
                if (!snapshot.exists()) {
                    CollectionReference progressCollectionRef = userDocument.collection("progress");
                    DocumentReference progresoCourseTest = progressCollectionRef.document("null");
                    Map<String, Object> progresoCourseData = new HashMap<>();
                    progresoCourseData.put("language", "null");
                    progresoCourseData.put("lessons", Arrays.asList("null"));
                    progresoCourseTest.set(progresoCourseData);

                    Map<String, Object> user = new HashMap<>();
                    user.put("username", username);
                    user.put("email", email);
                    user.put("profileImageURL", "");
                    user.put("provider", provider);
                    transaction.set(userDocument, user);
                    Log.d(TAG, "Usuario con ID " + uId + " añadido a la base de datos");
                } else {
                    Log.d(TAG, "Ya existe un usuario con ID" + uId);
                }
                return null;
            }
        });
    }

    public void loadUserFromDatabase(String uId) {
        db.collection("users").document(uId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    User user = new User(uId, document.getString("username"), document.getString("email"), document.getString("profileImageURL"), document.getString("provider"));
                    userListener.onLoadUser(user);
                    document.getReference().collection("progress").get()
                            .addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    UserProgress up = new UserProgress(uId);
                                    for (QueryDocumentSnapshot document_aux : task2.getResult()) {
                                        List<String> completedLessons = (List<String>) document_aux.get("lessons");
                                        String courseName = document_aux.getId();
                                        String language = document_aux.getString("language");
                                        if (courseName == null || language == null) break;
                                        for (String lesson : completedLessons) {
                                            up.addLessonToProgress(lesson, courseName, language);
                                        }
                                    }
                                    user.setProgress(up);
                                    userListener.onLoadUser(user);
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task2.getException());
                                }
                            });
                }
            }
        });
    }

    /**
     * Actualiza el username de un usuario
     *
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
                Log.d(TAG, "Username del usuario con ID " + uId + " actualizado correctamente");
                userListener.onLoadUserUsername(new_username);
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
     *
     * @param uId
     * @param new_email
     */
    public void updateUserEmail(String uId, String new_email) {
        DocumentReference docRef = db.collection("users").document(uId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("email", new_email);

        docRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Email del usuario con ID " + uId + "actualizado correctamente");
                userListener.onLoadUserEmail(new_email);
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
     *
     * @param uId
     * @param imagenPerfilUri
     * @param successListener
     */
    public void subirImagenPerfilStorage(String uId, Uri imagenPerfilUri, OnSuccessListener<Uri> successListener) {
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
     *
     * @param uId
     * @param imageUrl
     */
    public void updateUserImageUrl(String uId, String imageUrl) {
        DocumentReference userRef = db.collection("users").document(uId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("profileImageURL", imageUrl);

        userRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Imagen del usuario con ID " + uId + "actualizada correctamente");
                        userListener.onLoadUserImageURL(imageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error al actualizar la imagen del usuario con ID " + uId, e);
                    }
                });

    }

    /**
     * Este metodo añade a la coleccion "comments" de Firestore un comentario o problema indicado por un usuario
     *
     * @param usuario
     * @param comentario
     * @param fecha
     * @param view
     */
    public void agregarComentario(String usuario, String comentario, Date fecha, View view) {

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
                        Snackbar.make(view, "Problema enviado correctamente", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(context, "Problema al enviar el problema", Toast.LENGTH_SHORT).show();
                        Snackbar.make(view, "Problema al enviar el problema", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    public void registrarLeccionEnProgresso(String uId, String lesson, String course, String language) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("lessons", FieldValue.arrayUnion("nuevo_valor"));
        db.collection("users").document(uId).collection("progress").document(course).update(updates);
    }

    public void loadProgrammingLanguages() {
        ArrayList<ProgrammingLanguage> programmingLanguages = new ArrayList<>();
        db.collection("programmingLanguages")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String languageName = document.getId();
                            String languageDescription = document.getString("descripcion").trim();
                            int resourceImageId = document.getLong("resourceImageId").intValue();
                            ArrayList<String> tags = (ArrayList<String>) document.get("tags");
                            HashSet<String> tags_set = new HashSet<>(tags);

                            ArrayList<Course> arrayList = new ArrayList<>();
                            document.getReference().collection("courses").get().addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    for (QueryDocumentSnapshot document2 : task2.getResult()) {
                                        Course course = new Course(document2.getId(), document2.getString("description"), languageName);
                                        arrayList.add(course);
                                        document2.getReference().collection("lessons").get().addOnCompleteListener(task3 -> {
                                            if (task3.isSuccessful()) {
                                                for (QueryDocumentSnapshot document3 : task3.getResult()) {
                                                    Lesson lesson = new Lesson(document3.getId(), document3.getString("lesson"), course);
                                                    course.addLesson(lesson);
                                                }
                                                // Verificar si todas las lecciones se han agregado al curso
                                                if (arrayList.indexOf(course) == arrayList.size() - 1) {
                                                    // Todas las lecciones han sido agregadas, llamar a onLoadProgrammingLanguages
                                                    ProgrammingLanguage programmingLanguage = new ProgrammingLanguage(languageName, languageDescription, arrayList, tags_set, resourceImageId);
                                                    programmingLanguages.add(programmingLanguage);
                                                    if (programmingLanguages.size() == task.getResult().size()) {
                                                        languagesListener.onLoadProgrammingLanguages(programmingLanguages);
                                                    }
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting lessons: ", task3.getException());
                                            }
                                        });
                                    }
                                } else {
                                    Log.d(TAG, "Error getting courses: ", task2.getException());
                                }
                            });
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error loading programming languages: ", e);
                });
        System.out.println();
    }

}