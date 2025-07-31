package com.example.evchargetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.evchargetracker.database.AppDatabase;
import com.example.evchargetracker.database.entities.Usuario;

public class LoginActivity extends AppCompatActivity {

    //elementos del view
    private EditText editEmail, editPassword;
    private Button btnLogin, btnRegister;
    private TextView txtForgotPassword;
    //variables de datos
    private AppDatabase db;
    private SharedPreferences preferences;

    // ==================== CONSTANTES ====================

    private static final String PREFS_NAME = "EVChargeTracker";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_TARIFA = "tarifa_electrica";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Inicializar base de datos y preferencias
        db = AppDatabase.getDatabase(this);
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Verificar si el usuario ya está logueado
        if (isUserLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        // Inicializar interfaz
        initializeViews();
        setupClickListeners();

    }
    // ==================== INICIALIZACIÓN DE INTERFAZ ====================

    /**
     * Inicializa todas las vistas de la interfaz
     */
    private void initializeViews() {
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        txtForgotPassword = findViewById(R.id.txt_forgot_password);
    }

    /**
     * Configura los listeners de clicks para todos los botones
     */
    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        btnRegister.setOnClickListener(v -> navigateToRegister());
        txtForgotPassword.setOnClickListener(v -> showForgotPasswordMessage());
    }

    // ==================== LÓGICA DE AUTENTICACIÓN ====================

    /**
     * Verifica si hay una sesión activa guardada
     *
     * @return true si el usuario ya está logueado
     */
    private boolean isUserLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Intenta realizar el login con las credenciales ingresadas
     */
    private void attemptLogin() {
        // Obtener valores de los campos
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Validar campos
        if (!validateInputs(email, password)) {
            return;
        }

        // Deshabilitar botón para evitar múltiples clicks
        btnLogin.setEnabled(false);
        btnLogin.setText("Iniciando sesión...");

        // Realizar login en un hilo separado para no bloquear UI
        new Thread(() -> {
            try {
                Usuario usuario = db.usuarioDao().validateLogin(email, password);

                // Volver al hilo principal para actualizar UI
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Iniciar Sesión");

                    if (usuario != null) {
                        onLoginSuccess(usuario);
                    } else {
                        onLoginFailed();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Iniciar Sesión");
                    showError("Error al conectar con la base de datos");
                });
            }
        }).start();
    }

    /**
     * Valida los campos de entrada del formulario
     *
     * @param email    Email ingresado
     * @param password Contraseña ingresada
     * @return true si todos los campos son válidos
     */
    private boolean validateInputs(String email, String password) {
        // Limpiar errores previos
        editEmail.setError(null);
        editPassword.setError(null);

        // Validar email
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("El email es requerido");
            editEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Ingrese un email válido");
            editEmail.requestFocus();
            return false;
        }

        // Validar contraseña
        if (TextUtils.isEmpty(password)) {
            editPassword.setError("La contraseña es requerida");
            editPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            editPassword.setError("La contraseña debe tener al menos 6 caracteres");
            editPassword.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Maneja el login exitoso
     *
     * @param usuario Usuario que hizo login correctamente
     */
    private void onLoginSuccess(Usuario usuario) {
        // Guardar sesión del usuario
        saveUserSession(usuario);

        // Asegurar que el usuario tiene al menos un vehículo
        ensureUserHasVehicle(usuario.id);

        showSuccess("¡Bienvenido, " + usuario.nombre + "!");

        // Navegar a la pantalla principal
        navigateToMainActivity();
    }

    /**
     * Maneja el login fallido
     */
    private void onLoginFailed() {
        showError("Email o contraseña incorrectos");
        editPassword.setText(""); // Limpiar contraseña
        editEmail.requestFocus();
    }

    /**
     * Guarda la sesión del usuario en SharedPreferences
     *
     * @param usuario Usuario a guardar en sesión
     */
    private void saveUserSession(Usuario usuario) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, usuario.id);
        editor.putString(KEY_USER_NAME, usuario.nombre);
        editor.putString(KEY_USER_EMAIL, usuario.email);
        editor.putFloat(KEY_USER_TARIFA, usuario.tarifa_electrica);
        editor.apply();
    }

    /**
     * Asegura que el usuario tenga al menos un vehículo para usar la app
     *
     * @param userId ID del usuario
     */
    private void ensureUserHasVehicle(int userId) {
        new Thread(() -> {
            try {
                DatabaseInitializer.ensureUserHasVehicle(db, userId);
            } catch (Exception e) {
                android.util.Log.e("LoginActivity", "Error ensuring user has vehicle", e);
            }
        }).start();
    }

    // ==================== NAVEGACIÓN ====================

    /**
     * Navega a la pantalla principal de la aplicación
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Navega a la pantalla de registro
     */
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    // ==================== UTILIDADES DE UI ====================

    /**
     * Muestra un mensaje de error al usuario
     *
     * @param message Mensaje de error a mostrar
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Muestra un mensaje de éxito al usuario
     *
     * @param message Mensaje de éxito a mostrar
     */
    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Muestra mensaje para recuperación de contraseña
     * (Funcionalidad futura)
     */
    private void showForgotPasswordMessage() {
        Toast.makeText(this,
                "La funcionalidad de recuperación de contraseña estará disponible pronto",
                Toast.LENGTH_LONG).show();
    }

    // ==================== MÉTODOS ESTÁTICOS UTILITARIOS ====================

    /**
     * Cierra la sesión del usuario
     *
     * @param context Contexto de la aplicación
     */
    public static void logout(android.content.Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear(); // Limpia todas las preferencias
        editor.apply();

        // Navegar de vuelta al login
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * Obtiene el ID del usuario logueado
     *
     * @param context Contexto de la aplicación
     * @return ID del usuario o -1 si no hay sesión activa
     */
    public static int getCurrentUserId(android.content.Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getInt(KEY_USER_ID, -1);
    }

    /**
     * Obtiene el nombre del usuario logueado
     *
     * @param context Contexto de la aplicación
     * @return Nombre del usuario o null si no hay sesión
     */
    public static String getCurrentUserName(android.content.Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getString(KEY_USER_NAME, null);
    }

    /**
     * Obtiene el email del usuario logueado
     *
     * @param context Contexto de la aplicación
     * @return Email del usuario o null si no hay sesión
     */
    public static String getCurrentUserEmail(android.content.Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Obtiene la tarifa eléctrica del usuario logueado
     *
     * @param context Contexto de la aplicación
     * @return Tarifa eléctrica del usuario
     */
    public static float getCurrentUserTarifa(android.content.Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getFloat(KEY_USER_TARIFA, 0.50f);
    }

    /**
     * Verifica si hay una sesión activa
     *
     * @param context Contexto de la aplicación
     * @return true si hay un usuario logueado
     */
    public static boolean isLoggedIn(android.content.Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Actualiza la tarifa del usuario en las preferencias
     *
     * @param context     Contexto de la aplicación
     * @param nuevaTarifa Nueva tarifa eléctrica
     */
    public static void updateUserTarifa(android.content.Context context, float nuevaTarifa) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(KEY_USER_TARIFA, nuevaTarifa);
        editor.apply();
    }
}

