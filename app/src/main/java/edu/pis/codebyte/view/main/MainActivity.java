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

import java.util.ArrayList;

import edu.pis.codebyte.R;
import edu.pis.codebyte.model.DataBaseManager;
import edu.pis.codebyte.model.ProgrammingLanguage;
import edu.pis.codebyte.view.allLanguages.AllLanguagesFragment;
import edu.pis.codebyte.view.home.HomeFragment;
import edu.pis.codebyte.view.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, DataBaseManager.OnLoadProgrammingLanguages {

    private ArrayList<Fragment> fragments;
    private static final MutableLiveData<ArrayList<ProgrammingLanguage>> languages = new MutableLiveData<>();
    private DataBaseManager dbm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setEnterTransition(new MaterialFadeThrough());
        getWindow().setExitTransition(new MaterialFadeThrough());
        getWindow().setEnterTransition(new MaterialFadeThrough());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbm = DataBaseManager.getInstance();
        dbm.setOnLoadProgrammingLanguages(this);
        loadProgrammingLanguages();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(this);

        // Mostrar el primer fragment por defecto
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }

    private void loadProgrammingLanguages() {
        dbm.loadProgrammingLanguages();
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

    @Override
    public void onLoadProgrammingLanguages(ArrayList<ProgrammingLanguage> languages) {
        MainActivity.languages.setValue(languages);
    }

    public static MutableLiveData<ArrayList<ProgrammingLanguage>> getLanguages() {
        return languages;
    }
}