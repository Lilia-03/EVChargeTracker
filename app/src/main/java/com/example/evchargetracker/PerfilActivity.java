package com.example.evchargetracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;

public class PerfilActivity extends AppCompatActivity {

    private TextView txtNombreUsuario, txtEmailUsuario;
    private EditText editTarifaElectrica, editPresupuestoMensual;
    private Button btnGuardarConfiguracion;
    private BottomNavigationView bottomNavigation;

    private SharedPreferences preferences;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

    }

    private void initializeViews() {
        txtNombreUsuario = findViewById(R.id.txt_nombre_usuario);
        txtEmailUsuario = findViewById(R.id.txt_email_usuario);
        editTarifaElectrica = findViewById(R.id.edit_tarifa_electrica);
        editPresupuestoMensual = findViewById(R.id.edit_presupuesto_mensual);
        btnGuardarConfiguracion = findViewById(R.id.btn_guardar_configuracion);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void loadUserData() {
        String nombre = preferences.getString("nombre_usuario", String.valueOf(txtNombreUsuario));
        String email = preferences.getString("email_usuario", String.valueOf(txtEmailUsuario));
        float tarifaElectrica = preferences.getFloat("tarifa_electrica", editTarifaElectrica );
        float presupuestoMensual = preferences.getFloat("presupuesto_mensual", editPresupuestoMensual);

        txtNombreUsuario.setText(nombre);
        txtEmailUsuario.setText(email);
        editTarifaElectrica.setText(decimalFormat.format(tarifaElectrica));
        editPresupuestoMensual.setText(decimalFormat.format(presupuestoMensual));
    }
    private void setupSaveButton() {
        btnGuardarConfiguracion.setOnClickListener(v -> {
            saveUserConfiguration();
        });
    }

    private void saveUserConfiguration() {
        try {
            String tarifaStr = editTarifaElectrica.getText().toString();
            String presupuestoStr = editPresupuestoMensual.getText().toString();

            float tarifa = Float.parseFloat(tarifaStr);
            float presupuesto = Float.parseFloat(presupuestoStr);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat("tarifa_electrica", tarifa);
            editor.putFloat("presupuesto_mensual", presupuesto);
            editor.apply();

            // Mostrar mensaje de confirmación
            showToast("Configuración guardada exitosamente");

        } catch (NumberFormatException e) {
            showToast("Por favor ingrese valores numéricos válidos");
        }
    }

    // Método para iniciar sesión (ejemplo)
    private void loginUser(String email, String password) {
        ApiInterface apiService = ApiClient.getApiService();
        LoginRequest request = new LoginRequest(email, password);

        Call<AuthResponse> call = apiService.loginUser(request);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    AuthResponse authResponse = response.body();
                    // Guardar token y userId en SharedPreferences
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("token", authResponse.getToken());
                    editor.putInt("userId", authResponse.getUserId());
                    editor.apply();

                    // Actualizar UI
                    loadUserData();
                } else {
                    showToast("Error en credenciales");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                showToast("Error de conexión");
            }
        });
    }

    // Método para registrar usuario (ejemplo)
    private void registerUser(String name, String email, String password) {
        ApiInterface apiService = ApiClient.getApiService();
        RegisterRequest request = new RegisterRequest(name, email, password, password);

        Call<AuthResponse> call = apiService.registerUser(request);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    AuthResponse authResponse = response.body();
                    // Guardar token y userId
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("token", authResponse.getToken());
                    editor.putInt("userId", authResponse.getUserId());
                    editor.putString("nombre_usuario", name);
                    editor.putString("email_usuario", email);
                    editor.apply();

                    // Actualizar UI
                    loadUserData();
                } else {
                    showToast("Error en registro");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                showToast("Error de conexión");
            }
        });
    }

    private void showToast(String mensaje) {
        android.widget.Toast.makeText(this, mensaje, android.widget.Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigation.setSelectedItemId(R.id.nav_perfil);
    }

}