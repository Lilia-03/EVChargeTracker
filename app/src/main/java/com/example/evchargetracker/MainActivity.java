package com.example.evchargetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evchargetracker.adapters.CargaAdapter;
import com.example.evchargetracker.database.AppDatabase;
import com.example.evchargetracker.database.entities.Carga;
import com.example.evchargetracker.models.Carga;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Actividad Principal - Dashboard de la aplicación
 *
 * Esta actividad muestra:
 * - Estadísticas del usuario actual logueado
 * - Cargas recientes del usuario
 * - Navegación a otras secciones
 * - Botón para agregar nueva carga
 *
 * IMPORTANTE: No usa datos quemados, todo se basa en el usuario actual
 *
 * @author EV Charge Tracker Team
 * @version 2.0 - Sin datos quemados, con sesión real
 */

public class MainActivity extends AppCompatActivity {

    // ==================== VARIABLES DE INTERFAZ ====================

    private TextView txtGastoTotal;
    private TextView txtCargasTotales;
    private TextView txtKwhCargados;
    private TextView txtPromedioCarga;
    private TextView txtCargasCasa;
    private RecyclerView recyclerViewRecientes;
    private CargaAdapter cargaAdapter;
    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabAgregarCarga;

    // ==================== VARIABLES DE DATOS ====================

    private AppDatabase db;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private int currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Verificar sesión activa
        if (!LoginActivity.isLoggedIn(this)) {
            redirectToLogin();
            return;
        }

        // Obtener ID del usuario logueado
        currentUserId = LoginActivity.getCurrentUserId(this);
        if (currentUserId == -1) {
            redirectToLogin();
            return;
        }

        // Inicializar base de datos
        db = AppDatabase.getDatabase(this);

        // Inicializar interfaz
        initializeViews();
        setupBottomNavigation();
        setupFloatingActionButton();
        setupRecyclerView();

        // Cargar datos del usuario actual
        loadUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos cuando regresamos a esta actividad
        loadUserData();
        bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
    }

    // ==================== INICIALIZACIÓN DE INTERFAZ ====================

    /**
     * Inicializa todas las vistas de la interfaz
     */
    private void initializeViews() {
        txtGastoTotal = findViewById(R.id.txt_gasto_total);
        txtCargasTotales = findViewById(R.id.txt_cargas_totales);
        txtKwhCargados = findViewById(R.id.txt_kwh_cargados);
        txtPromedioCarga = findViewById(R.id.txt_promedio_carga);
        txtCargasCasa = findViewById(R.id.txt_cargas_casa);
        recyclerViewRecientes = findViewById(R.id.recycler_cargas_recientes);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        fabAgregarCarga = findViewById(R.id.fab_agregar_carga);
    }

    /**
     * Configura la navegación inferior
     */
    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_dashboard) {
                    return true;
                } else if (itemId == R.id.nav_historial) {
                    startActivity(new Intent(MainActivity.this, HistorialActivity.class));
                    return true;
                } else if (itemId == R.id.nav_perfil) {
                    startActivity(new Intent(MainActivity.this, PerfilActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Configura el botón flotante para agregar cargas
     */
    private void setupFloatingActionButton() {
        fabAgregarCarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AgregarCargaActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Configura el RecyclerView para mostrar cargas recientes
     */
    private void setupRecyclerView() {
        recyclerViewRecientes.setLayoutManager(new LinearLayoutManager(this));
        // El adapter se configurará cuando se carguen los datos
    }

    // ==================== CARGA DE DATOS DEL USUARIO ====================

    /**
     * Carga todos los datos del usuario actual desde la base de datos
     */
    private void loadUserData() {
        new Thread(() -> {
            try {
                // Obtener todos los vehículos del usuario
                var vehiculosUsuario = db.vehiculoDao().getByUsuario(currentUserId);

                if (vehiculosUsuario.isEmpty()) {
                    // El usuario no tiene vehículos, mostrar estado vacío
                    runOnUiThread(this::showEmptyState);
                    return;
                }

                // Obtener todas las cargas de todos los vehículos del usuario
                List<Carga> todasLasCargas = new ArrayList<>();
                for (var vehiculo : vehiculosUsuario) {
                    var cargasVehiculo = db.cargaDao().getCargasByVehiculo(vehiculo.id);
                    todasLasCargas.addAll(cargasVehiculo);
                }

                if (todasLasCargas.isEmpty()) {
                    // El usuario no tiene cargas, mostrar estado vacío
                    runOnUiThread(this::showEmptyState);
                    return;
                }

                // Calcular estadísticas
                EstadisticasUsuario stats = calcularEstadisticas(todasLasCargas);

                // Obtener cargas recientes (últimas 5)
                List<CargaModel> cargasRecientes = convertirCargasParaUI(
                        todasLasCargas.subList(0, Math.min(5, todasLasCargas.size()))
                );

                // Actualizar UI en el hilo principal
                runOnUiThread(() -> {
                    updateUIWithData(stats, cargasRecientes);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    showError("Error cargando datos: " + e.getMessage());
                });
            }
        }).start();
    }

    /**
     * Calcula estadísticas basadas en las cargas del usuario
     */
    private EstadisticasUsuario calcularEstadisticas(List<Carga> cargas) {
        EstadisticasUsuario stats = new EstadisticasUsuario();

        // Obtener mes y año actuales
        Calendar cal = Calendar.getInstance();
        int mesActual = cal.get(Calendar.MONTH) + 1; // Calendar es 0-based
        int añoActual = cal.get(Calendar.YEAR);

        double gastoTotal = 0;
        double kwhTotal = 0;
        int cargasEnCasa = 0;
        int cargasDelMes = 0;
        double gastoDelMes = 0;

        for (Carga carga : cargas) {
            // Estadísticas generales
            gastoTotal += carga.costo;
            kwhTotal += carga.energia_kwh;

            if (carga.lugar.toLowerCase().contains("casa")) {
                cargasEnCasa++;
            }

            // Verificar si la carga es del mes actual
            if (esCargaDelMesActual(carga.fecha, mesActual, añoActual)) {
                cargasDelMes++;
                gastoDelMes += carga.costo;
            }
        }

        stats.totalCargas = cargas.size();
        stats.totalKwh = kwhTotal;
        stats.gastoTotal = gastoTotal;
        stats.gastoDelMes = gastoDelMes;
        stats.promedioPorCarga = cargas.size() > 0 ? gastoTotal / cargas.size() : 0;
        stats.porcentajeCargasEnCasa = cargas.size() > 0 ? (cargasEnCasa * 100) / cargas.size() : 0;

        return stats;
    }

    /**
     * Verifica si una carga pertenece al mes actual
     */
    private boolean esCargaDelMesActual(String fecha, int mesActual, int añoActual) {
        try {
            // Formato esperado: "dd/MM/yyyy"
            String[] partes = fecha.split("/");
            if (partes.length == 3) {
                int mes = Integer.parseInt(partes[1]);
                int año = Integer.parseInt(partes[2]);
                return mes == mesActual && año == añoActual;
            }
        } catch (Exception e) {
            // Ignorar errores de parsing
        }
        return false;
    }

    /**
     * Convierte entidades de carga a modelos para la UI
     */
    private List<CargaModel> convertirCargasParaUI(List<Carga> cargas) {
        List<CargaModel> cargasUI = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (Carga carga : cargas) {
            try {
                Date fecha = sdf.parse(carga.fecha);
                CargaModel cargaUI = new CargaModel(
                        carga.id,
                        carga.lugar,
                        fecha,
                        carga.energia_kwh,
                        carga.duracion_min,
                        carga.costo,
                        carga.tipo_carga,
                        carga.tarifa_utilizada
                );
                cargasUI.add(cargaUI);
            } catch (Exception e) {
                // Ignorar cargas con fechas inválidas
            }
        }

        return cargasUI;
    }

    /**
     * Actualiza la interfaz con los datos calculados
     */
    private void updateUIWithData(EstadisticasUsuario stats, List<CargaModel> cargasRecientes) {
        // Actualizar estadísticas
        txtGastoTotal.setText("$" + decimalFormat.format(stats.gastoDelMes));
        txtCargasTotales.setText(String.valueOf(stats.totalCargas));
        txtKwhCargados.setText(decimalFormat.format(stats.totalKwh));
        txtPromedioCarga.setText("$" + decimalFormat.format(stats.promedioPorCarga));
        txtCargasCasa.setText(stats.porcentajeCargasEnCasa + "%");

        // Configurar adapter para cargas recientes
        if (cargaAdapter == null) {
            cargaAdapter = new CargaAdapter(cargasRecientes, true);
            recyclerViewRecientes.setAdapter(cargaAdapter);
        } else {
            cargaAdapter.updateCargas(cargasRecientes);
        }
    }

    /**
     * Muestra el estado cuando el usuario no tiene cargas
     */
    private void showEmptyState() {
        txtGastoTotal.setText("$0.00");
        txtCargasTotales.setText("0");
        txtKwhCargados.setText("0");
        txtPromedioCarga.setText("$0.00");
        txtCargasCasa.setText("0%");

        // Mostrar mensaje de estado vacío en el RecyclerView
        List<CargaModel> emptyList = new ArrayList<>();
        if (cargaAdapter == null) {
            cargaAdapter = new CargaAdapter(emptyList, true);
            recyclerViewRecientes.setAdapter(cargaAdapter);
        } else {
            cargaAdapter.updateCargas(emptyList);
        }
    }

    // ==================== UTILIDADES ====================

    /**
     * Redirige al usuario al login si no hay sesión activa
     */
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Muestra un mensaje de error
     */
    private void showError(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show();
    }

    // ==================== CLASE INTERNA PARA ESTADÍSTICAS ====================

    /**
     * Clase para encapsular las estadísticas del usuario
     */
    private static class EstadisticasUsuario {
        int totalCargas = 0;
        double totalKwh = 0;
        double gastoTotal = 0;
        double gastoDelMes = 0;
        double promedioPorCarga = 0;
        int porcentajeCargasEnCasa = 0;
    }


    }
}