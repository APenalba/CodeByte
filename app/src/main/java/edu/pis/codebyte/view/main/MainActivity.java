package edu.pis.codebyte.view.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

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
import edu.pis.codebyte.viewmodel.main.MainViewModel;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, DataBaseManager.OnLoadProgrammingLanguagesListener {

    private ArrayList<Fragment> fragments;
    public String uId;
    public String uEmail;
    public String uUsername;
    public String uProvider;
    public String uImageURL;
    private static MutableLiveData<ArrayList<ProgrammingLanguage>> languages = new MutableLiveData<>();
    private MainViewModel mainViewModel;
    private DataBaseManager dbm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setEnterTransition(new MaterialFadeThrough());
        getWindow().setExitTransition(new MaterialFadeThrough());
        getWindow().setEnterTransition(new MaterialFadeThrough());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbm = DataBaseManager.getInstance();
        mainViewModel = MainViewModel.getInstance();

        userSetUp();

        loadProgrammingLanguages();
        navigationBar_setup();
    }

    private void userSetUp() {
        this.uId = mainViewModel.getuId();

        uEmail = "loading...";
        final Observer<String> observeruEmail = new Observer<String>() {
            @Override
            public void onChanged(String new_uEmail) {
                uEmail = new_uEmail;
            }
        };
        mainViewModel.getuEmail().observe(this, observeruEmail);

        uUsername = "loading...";
        final Observer<String> observeruUsername = new Observer<String>() {
            @Override
            public void onChanged(String new_uUsername) {
                uUsername = new_uUsername;
            }
        };
        mainViewModel.getuUsername().observe(this, observeruUsername);

        uProvider = "loading...";
        final Observer<String> observeruProvider = new Observer<String>() {
            @Override
            public void onChanged(String new_uProvider) {
                uProvider = new_uProvider;
            }
        };
        mainViewModel.getuProvider().observe(this, observeruProvider);

        uImageURL = "";
        final Observer<String> observeruImageURL = new Observer<String>() {
            @Override
            public void onChanged(String new_uImageURL) {
                uImageURL = new_uImageURL;
            }
        };
        mainViewModel.getuImageURL().observe(this, observeruImageURL);
    }

    private void loadProgrammingLanguages() {
        languages = new MutableLiveData<>();
        dbm.setOnLoadProgrammingLanguagesListener(this);
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

}