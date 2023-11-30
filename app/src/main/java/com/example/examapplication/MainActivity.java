package com.example.examapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.examapplication.BlurtingFragment;
import com.example.examapplication.PomodoroFragment;
import com.example.examapplication.ThreeTwoOneFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar); // Ignore red line errors
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance(); // Inizializza l'istanza di FirebaseAuth

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PomodoroFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_pomodoro);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_pomodoro) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PomodoroFragment()).commit();
        } else if (itemId == R.id.nav_blurting) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new BlurtingFragment()).commit();
        } else if (itemId == R.id.nav_three_two_one) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ThreeTwoOneFragment()).commit();
        } else if (itemId == R.id.nav_logout) {
            // Esegui il logout
            firebaseAuth.signOut();

            // Avvia l'Activity di accesso (LoginActivity)
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

            // Chiudi tutte le attività precedenti nell'ordine dello stack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // Chiudi l'Activity corrente
            finish();
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
