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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import edu.pis.codebyte.model.challenges.Challenge;
import edu.pis.codebyte.model.challenges.FillTheGapChallenge;
import edu.pis.codebyte.model.challenges.MultichoiceChallenge;


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
        DocumentReference userDocument = usersCollection.document(uId);

        userDocument.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                DocumentReference progressDocument = userDocument.collection("progress").document("null");

                Map<String, Object> progresoCourseData = new HashMap<>();
                progresoCourseData.put("language", "null");
                progresoCourseData.put("lessons", Collections.singletonList("null"));

                progressDocument.set(progresoCourseData).addOnSuccessListener(aVoid -> {
                    Map<String, Object> user = new HashMap<>();
                    user.put("username", username);
                    user.put("email", email);
                    user.put("profileImageURL", "");
                    user.put("provider", provider);

                    userDocument.set(user).addOnSuccessListener(aVoid1 -> {
                        Log.d(TAG, "Usuario con ID " + uId + " añadido a la base de datos");
                    }).addOnFailureListener(e -> {
                        Log.d(TAG, "Error al guardar los datos del usuario: ", e);
                    });
                }).addOnFailureListener(e -> {
                    Log.d(TAG, "Error al guardar el progreso del usuario: ", e);
                });
            } else {
                Log.d(TAG, "Ya existe un usuario con ID " + uId);
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "Error al comprobar si el usuario existe: ", e);
        });
    }


    public void loadUserFromDatabase(String uId) {
        db.collection("users").document(uId).get()
                .addOnSuccessListener(userDocumentSnapshot -> {
                    String username = userDocumentSnapshot.getString("username");
                    String email = userDocumentSnapshot.getString("email");
                    String profileImageURL = userDocumentSnapshot.getString("profileImageURL");
                    String provider = userDocumentSnapshot.getString("provider");

                    User user = new User(uId, username, email, profileImageURL, provider);

                    userDocumentSnapshot.getReference().collection("progress").get()
                            .addOnSuccessListener(progressQuerySnapshot -> {
                                UserProgress userProgress = new UserProgress(uId);

                                for (QueryDocumentSnapshot progressDocument : progressQuerySnapshot) {
                                    List<String> completedLessons = (List<String>) progressDocument.get("lessons");
                                    String courseName = progressDocument.getId();
                                    String language = progressDocument.getString("language");

                                    if (courseName == null || language == null || courseName.equals("null") || language.equals("null") ) {
                                        break;
                                    }
                                    userProgress.addCourseToProgress(courseName, language);
                                    if (completedLessons != null) {
                                        for (String lesson : completedLessons) {
                                            userProgress.addLessonToProgress(lesson, courseName, language);
                                        }
                                    }
                                }

                                user.setProgress(userProgress);
                                userListener.onLoadUser(user);
                            })
                            .addOnFailureListener(e -> {
                                Log.d(TAG, "Error getting progress documents: ", e);
                            });

                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error getting user document: ", e);
                });
    }

    public void updateUserUsername(String uId, String new_username) {
        DocumentReference docRef = db.collection("users").document(uId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("username", new_username);

        docRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Username del usuario con ID " + uId + " actualizado correctamente");
                    userListener.onLoadUserUsername(new_username);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al actualizar el username del usuario con ID " + uId, e);
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
    public void uploadUserImage(String uId, Uri imagenPerfilUri, OnSuccessListener<Uri> successListener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference userImageRef = storageRef.child("userImages/" + uId + ".jpg");

        UploadTask uploadTask = userImageRef.putFile(imagenPerfilUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            Log.d(TAG, "Imagen subida correctamente");
            userImageRef.getDownloadUrl().addOnSuccessListener(successListener);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error al subir la imagen a la base de datos", e);
        });

        // Agregar seguimiento de progreso (opcional)
        uploadTask.addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            Log.d(TAG, "Progreso de carga: " + progress + "%");
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
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Imagen del usuario con ID " + uId + " actualizada correctamente");
                    userListener.onLoadUserImageURL(imageUrl);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al actualizar la imagen del usuario con ID " + uId, e);
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
                .addOnSuccessListener(documentReference -> {
                    Snackbar.make(view, "Problema enviado correctamente", Snackbar.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(view, "Problema al enviar el problema", Snackbar.LENGTH_SHORT).show();
                });
    }


    public void registrarLeccionEnProgresso(String uId, String lesson, String course, String language) {
        //TODO
        DocumentReference docRef = db.collection("users").document(uId).collection("progress").document(course);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // El documento ya existe, actualizar los campos necesarios
                        docRef.update("language", language, "lessons", FieldValue.arrayUnion(lesson))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Actualización exitosa
                                        } else {
                                            // Error al actualizar
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        // Error al actualizar
                                    }
                                });
                    } else {
                        // El documento no existe, crearlo con los campos requeridos
                        Map<String, Object> progressData = new HashMap<>();
                        progressData.put("language", language);
                        progressData.put("lessons", Arrays.asList(lesson));

                        docRef.set(progressData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Creación exitosa
                                        } else {
                                            // Error al crear el documento
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        // Error al crear el documento
                                    }
                                });
                    }
                } else {
                    // Error al obtener el documento
                }
            }
        });
    }

    public void loadProgrammingLanguages() {
        ArrayList<ProgrammingLanguage> programmingLanguages = new ArrayList<>();
        db.collection("programmingLanguages")
                .get()
                .addOnCompleteListener(getLanguagesTask -> {
                    if (getLanguagesTask.isSuccessful()) {
                        for (QueryDocumentSnapshot document : getLanguagesTask.getResult()) {
                            String languageName = document.getId();
                            String languageDescription = document.getString("descripcion").trim();
                            int resourceImageId = document.getLong("resourceImageId").intValue();
                            ArrayList<String> tags = (ArrayList<String>) document.get("tags");
                            HashSet<String> tags_set = new HashSet<>(tags);

                            ArrayList<Course> coursesArrayList = new ArrayList<>();
                            document.getReference().collection("courses").get().addOnCompleteListener(getCoursesTask -> {
                                if (getCoursesTask.isSuccessful()) {
                                    for (QueryDocumentSnapshot document2 : getCoursesTask.getResult()) {
                                        if (document2.get("descripcion") == null) {
                                            System.out.println();
                                        }
                                        Course course = new Course(document2.getId(), document2.getString("descripcion"), languageName);
                                        coursesArrayList.add(course);
                                        document2.getReference().collection("lessons").get().addOnCompleteListener(getLessonsTask -> {
                                            if (getLessonsTask.isSuccessful()) {
                                                for (QueryDocumentSnapshot document3 : getLessonsTask.getResult()) {
                                                    int id = (document3.getString("exercice") == null)? -1:Integer.parseInt(document3.getString("exercice"));
                                                    Lesson lesson = new Lesson(document3.getId(), document3.getString("descripcion"), course, id);
                                                    course.addLesson(lesson);
                                                }
                                                // Verificar si todas las lecciones se han agregado al curso
                                                if (coursesArrayList.indexOf(course) == coursesArrayList.size() - 1) {
                                                    // Todas las lecciones han sido agregadas, llamar a onLoadProgrammingLanguages
                                                    ProgrammingLanguage programmingLanguage = new ProgrammingLanguage(languageName, languageDescription, coursesArrayList, tags_set, resourceImageId);
                                                    programmingLanguages.add(programmingLanguage);
                                                    if (programmingLanguages.size() == getLanguagesTask.getResult().size()) {
                                                        languagesListener.onLoadProgrammingLanguages(programmingLanguages);
                                                    }
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting lessons: ", getLessonsTask.getException());
                                            }
                                        });
                                        document2.getReference().collection("exercices").get().addOnCompleteListener(getExercicesTask -> {
                                            if (getExercicesTask.isSuccessful()) {
                                                for (QueryDocumentSnapshot document4 : getExercicesTask.getResult()) {
                                                    Challenge challenge = null;

                                                    if ( document4.getString("type") == null || document4.getString("type").equals("1")) {
                                                        challenge = new FillTheGapChallenge(document4.getString("question"), document4.getString("correct_answer"));
                                                    }else if(document4.getString("type").equals("0")) {
                                                        ArrayList<String> responses = (ArrayList<String>) document4.get("responses");
                                                        challenge = new MultichoiceChallenge(document4.getString("question"), document4.getString("correct_answer"), (ArrayList<String>) document4.get("responses"));
                                                    }

                                                    if (challenge != null) course.addChallenge(challenge, Integer.parseInt(document4.getId()));
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting lessons: ", getExercicesTask.getException());
                                            }
                                        });
                                    }
                                } else {
                                    Log.d(TAG, "Error getting courses: ", getCoursesTask.getException());
                                }
                            });
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", getLanguagesTask.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error loading programming languages: ", e);
                });
    }

}