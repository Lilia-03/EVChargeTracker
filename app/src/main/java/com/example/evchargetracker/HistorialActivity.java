package com.example.evchargetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evchargetracker.adapters.CargaAdapter;
import com.example.evchargetracker.database.AppDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {

        //variables
        private Button btnTodos, btnCasa, btnEstacion, btnCargaLenta;
    private RecyclerView recyclerViewHistorial;
    private CargaAdapter cargaAdapter;
    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabAgregarCarga;

    private AppDatabase db;
    private String filtroActual = "todos";
    private List<com.example.evchargetracker.models.Carga> cargasModel = new ArrayList<>();
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial);

        db = AppDatabase.getDatabase(this);
        preferences = getSharedPreferences("EVChargeTracker", MODE_PRIVATE);

    }

    private void initializeViews() {
        btnTodos = findViewById(R.id.btn_todos);
        btnCasa = findViewById(R.id.btn_casa);
        btnEstacion = findViewById(R.id.btn_estacion);
        btnCargaLenta = findViewById(R.id.btn_carga_lenta);
        recyclerViewHistorial = findViewById(R.id.recycler_historial);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        fabAgregarCarga = findViewById(R.id.fab_agregar_carga);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_historial);
        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_dashboard) {
                    startActivity(new Intent(HistorialActivity.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.nav_historial) {
                    return true;
                } else if (itemId == R.id.nav_perfil) {
                    startActivity(new Intent(HistorialActivity.this, PerfilActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
    //para que cuando toques el boton del mas te redirija a agregar carga
    private void setupFloatingActionButton() {
        fabAgregarCarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistorialActivity.this, AgregarCargaActivity.class);
                startActivity(intent);
            }
        });
    }



}