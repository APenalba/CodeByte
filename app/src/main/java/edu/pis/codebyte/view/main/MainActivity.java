package edu.pis.codebyte.view.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.transition.platform.MaterialFadeThrough;

import java.util.ArrayList;

import edu.pis.codebyte.R;
import edu.pis.codebyte.view.home.HomeFragment;
import edu.pis.codebyte.view.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private ArrayList<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setEnterTransition(new MaterialFadeThrough());
        getWindow().setExitTransition(new MaterialFadeThrough());
        getWindow().setEnterTransition(new MaterialFadeThrough());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                selectedFragment = new HomeFragment();
                break;
            case R.id.navigation_bar_profile:
                selectedFragment = new ProfileFragment();
                break;
            case R.id.navigation_bar_lenguajes:
                selectedFragment = new HomeFragment();
                break;
            case R.id.navigation_bar_statistics:
                selectedFragment = new ProfileFragment();
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