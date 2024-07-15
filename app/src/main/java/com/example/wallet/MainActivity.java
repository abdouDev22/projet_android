package com.example.wallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    FloatingActionButton add_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        add_button = findViewById(R.id.add_button);


        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (id == R.id.nav_add) {
            // Vérifier si l'utilisateur est connecté avant d'accéder à la fonctionnalité "Ajouter"
            if (isUserLoggedIn()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddFragment()).commit();
            } else {
                // Rediriger vers l'écran de connexion
                Intent intent = new Intent(MainActivity.this, Login_activity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_logout) {
            // Supprimer l'ID de l'utilisateur de la session
            clearUserSession();
            // Rediriger vers l'écran de connexion
            Intent intent = new Intent(MainActivity.this, Login_activity.class);
            startActivity(intent);
            // Terminer l'activité principale pour empêcher l'utilisateur de revenir en arrière
            finish();
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearUserSession() {
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        preferences.edit().remove("user_id").apply();
    }

    // Méthode pour vérifier si l'utilisateur est connecté
    private boolean isUserLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        long userId = preferences.getLong("user_id", -1); // -1 est la valeur par défaut si l'ID n'est pas trouvé
        return userId != -1; // Si l'ID est différent de -1, l'utilisateur est connecté
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}