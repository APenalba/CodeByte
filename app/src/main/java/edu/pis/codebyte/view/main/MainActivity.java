package edu.pis.codebyte.view.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import edu.pis.codebyte.view.main.allLanguages.AllLanguagesFragment;
import edu.pis.codebyte.view.main.home.HomeFragment;
import edu.pis.codebyte.view.main.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private static MutableLiveData<ArrayList<ProgrammingLanguage>> languages = new MutableLiveData<>();
    BottomNavigationView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setEnterTransition(new MaterialFadeThrough());
        getWindow().setExitTransition(new MaterialFadeThrough());
        getWindow().setEnterTransition(new MaterialFadeThrough());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationBar_setup();
    }

    private void navigationBar_setup() {
        navigation = findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(this);

        // Mostrar el primer fragment por defecto
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (fragment instanceof AllLanguagesFragment) {
            if (((AllLanguagesFragment) fragment).getLvl() == 1) {
                ((AllLanguagesFragment) fragment).onBack();
            }else {
                navigation.setSelectedItemId(R.id.navigation_bar_home);
                /*getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment.newInstance())
                        .commit();*/
            }
        } else if (fragment instanceof HomeFragment || fragment instanceof ProfileFragment) {
            new AlertDialog.Builder(this)
                    .setTitle("Cerrar aplicación")
                    .setMessage("¿Estás seguro de que quieres cerrar la aplicación?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Cierra la aplicación
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }else {
            super.onBackPressed();
        }
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

}