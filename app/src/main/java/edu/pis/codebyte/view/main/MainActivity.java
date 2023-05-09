package edu.pis.codebyte.view.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.transition.platform.MaterialFadeThrough;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

import edu.pis.codebyte.R;
import edu.pis.codebyte.model.DataBaseManager;
import edu.pis.codebyte.model.ProgrammingLanguage;
import edu.pis.codebyte.model.User;
import edu.pis.codebyte.view.allLanguages.AllLanguagesFragment;
import edu.pis.codebyte.view.home.HomeFragment;
import edu.pis.codebyte.view.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, DataBaseManager.OnLoadProgrammingLanguages, DataBaseManager.OnLoadUserListener {

    private ArrayList<Fragment> fragments;
    private MutableLiveData<User> user;
    private static MutableLiveData<ArrayList<ProgrammingLanguage>> languages = new MutableLiveData<>();
    private DataBaseManager dbm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setEnterTransition(new MaterialFadeThrough());
        getWindow().setExitTransition(new MaterialFadeThrough());
        getWindow().setEnterTransition(new MaterialFadeThrough());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbm = DataBaseManager.getInstance();

        loadProgrammingLanguages();
        loadCurrentUser();
        navigationBar_setup();
    }

    private void loadCurrentUser() {
        user = new MutableLiveData<>();
        dbm.setUserListener(this);
        dbm.getUserFromDatabase(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
    }
    @Override
    public void onLoadUser(User user) {
        this.user.setValue(user);
    }

    private void loadProgrammingLanguages() {
        languages = new MutableLiveData<>();
        dbm.setOnLoadProgrammingLanguages(this);
        dbm.loadProgrammingLanguages();
    }

    @Override
    public void onLoadProgrammingLanguages(ArrayList<ProgrammingLanguage> languages) {
        MainActivity.languages.setValue(languages);
    }

    private void navigationBar_setup() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(this);

        // Mostrar el primer fragment por defecto
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_bar_home:
                selectedFragment = HomeFragment.newInstance();
                break;
            case R.id.navigation_bar_profile:
                selectedFragment = ProfileFragment.newInstance();
                break;
            case R.id.navigation_bar_lenguajes:
                selectedFragment = AllLanguagesFragment.newInstance();
                break;
            case R.id.navigation_bar_statistics:
                selectedFragment = ProfileFragment.newInstance();
                break;
        }
        // Reemplazar el fragment actual con el nuevo fragment seleccionado
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit();

        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public static MutableLiveData<ArrayList<ProgrammingLanguage>> getLanguages() {
        return languages;
    }

    public MutableLiveData<User> getUser() {
        return user;
    }
}