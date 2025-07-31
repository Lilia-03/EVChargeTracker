package com.example.evchargetracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.evchargetracker.database.AppDatabase;
import com.example.evchargetracker.database.entities.Usuario;

import java.util.regex.Pattern;

/**
 * Actividad de Registro - Pantalla para crear nueva cuenta
 *
 * Esta actividad maneja:
 * - Validación de datos de usuario
 * - Verificación de email único
 * - Creación de nuevo usuario en la base de datos
 * - Navegación de vuelta al login
 *
 * Flujo de funcionamiento:
 * 1. Usuario llena formulario de registro
 * 2. Validar todos los campos
 * 3. Verificar que el email no exista
 * 4. Crear usuario en la base de datos
 * 5. Navegar de vuelta al login
 *
 * @author EV Charge Tracker Team
 * @version 1.0
 */

public class RegisterActivity extends AppCompatActivity {
    // ==================== VARIABLES DE INTERFAZ ====================

    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private EditText editTarifaElectrica;
    private Button btnRegister;
    private Toolbar toolbar;

    //variables de datos
    private AppDatabase db;
    // ==================== CONSTANTES ====================

    private static final float DEFAULT_TARIFA = 0.25f;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MIN_NAME_LENGTH = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Inicializar base de datos
        db = AppDatabase.getDatabase(this);

        // Inicializar interfaz
        initializeViews();
        setupToolbar();
        setupClickListeners();
        setDefaultValues();

    }
    // ==================== INICIALIZACIÓN DE INTERFAZ ====================

    /**
     * Inicializa todas las vistas de la interfaz
     */
    private void initializeViews() {
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editConfirmPassword = findViewById(R.id.edit_confirm_password);
        editTarifaElectrica = findViewById(R.id.edit_tarifa_electrica);
        btnRegister = findViewById(R.id.btn_register);
        toolbar = findViewById(R.id.toolbar);
    }
    /**
     * Configura la toolbar con navegación de vuelta
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Crear Cuenta");
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }
    /**
     * Configura los listeners de clicks
     */
    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    /**
     * Establece valores por defecto en los campos
     */
    private void setDefaultValues() {
        editTarifaElectrica.setText(String.valueOf(DEFAULT_TARIFA));
    }
    /// Logica de registro///////////
    /**
     * Intenta registrar un nuevo usuario
     */
    private void attemptRegister() {
        // Obtener valores de los campos
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();
        String tarifaStr = editTarifaElectrica.getText().toString().trim();

        // Validar campos
        if (!validateInputs(name, email, password, confirmPassword, tarifaStr)) {
            return;
        }

        float tarifa = Float.parseFloat(tarifaStr);

        // Deshabilitar botón para evitar múltiples registros
        btnRegister.setEnabled(false);
        btnRegister.setText("Creando cuenta...");

        // Realizar registro en un hilo separado
        new Thread(() -> {
            try {
                // Verificar si el email ya existe
                if (db.usuarioDao().emailExists(email)) {
                    runOnUiThread(() -> {
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Crear Cuenta");
                        showError("Este email ya está registrado");
                        editEmail.setError("Email ya existe");
                        editEmail.requestFocus();
                    });
                    return;
                }

                // Crear nuevo usuario
                Usuario newUser = new Usuario(name, email, password, tarifa);
                long userId = db.usuarioDao().insert(newUser);

                runOnUiThread(() -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Crear Cuenta");

                    if (userId > 0) {
                        onRegistrationSuccess();
                    } else {
                        onRegistrationFailed();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Crear Cuenta");
                    showError("Error al crear la cuenta: " + e.getMessage());
                });
            }
        }).start();
    }

    /**
     * Valida todos los campos del formulario
     *
     * @param name Nombre del usuario
     * @param email Email del usuario
     * @param password Contraseña
     * @param confirmPassword Confirmación de contraseña
     * @param tarifaStr Tarifa eléctrica como string
     * @return true si todos los campos son válidos
     */
    private boolean validateInputs(String name, String email, String password,
                                   String confirmPassword, String tarifaStr) {

        // Limpiar errores previos
        clearErrors();

        boolean isValid = true;

        // Validar nombre
        if (TextUtils.isEmpty(name)) {
            editName.setError("El nombre es requerido");
            if (isValid) editName.requestFocus();
            isValid = false;
        } else if (name.length() < MIN_NAME_LENGTH) {
            editName.setError("El nombre debe tener al menos " + MIN_NAME_LENGTH + " caracteres");
            if (isValid) editName.requestFocus();
            isValid = false;
        } else if (!isValidName(name)) {
            editName.setError("El nombre solo puede contener letras y espacios");
            if (isValid) editName.requestFocus();
            isValid = false;
        }

        // Validar email
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("El email es requerido");
            if (isValid) editEmail.requestFocus();
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Ingrese un email válido");
            if (isValid) editEmail.requestFocus();
            isValid = false;
        }

        // Validar contraseña
        if (TextUtils.isEmpty(password)) {
            editPassword.setError("La contraseña es requerida");
            if (isValid) editPassword.requestFocus();
            isValid = false;
        } else if (password.length() < MIN_PASSWORD_LENGTH) {
            editPassword.setError("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres");
            if (isValid) editPassword.requestFocus();
            isValid = false;
        } else if (!isStrongPassword(password)) {
            editPassword.setError("La contraseña debe contener al menos una letra y un número");
            if (isValid) editPassword.requestFocus();
            isValid = false;
        }

        // Validar confirmación de contraseña
        if (TextUtils.isEmpty(confirmPassword)) {
            editConfirmPassword.setError("Confirme su contraseña");
            if (isValid) editConfirmPassword.requestFocus();
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            editConfirmPassword.setError("Las contraseñas no coinciden");
            if (isValid) editConfirmPassword.requestFocus();
            isValid = false;
        }

        // Validar tarifa eléctrica
        if (TextUtils.isEmpty(tarifaStr)) {
            editTarifaElectrica.setError("La tarifa eléctrica es requerida");
            if (isValid) editTarifaElectrica.requestFocus();
            isValid = false;
        } else {
            try {
                float tarifa = Float.parseFloat(tarifaStr);
                if (tarifa <= 0) {
                    editTarifaElectrica.setError("La tarifa debe ser mayor a 0");
                    if (isValid) editTarifaElectrica.requestFocus();
                    isValid = false;
                } else if (tarifa > 10) {
                    editTarifaElectrica.setError("La tarifa parece muy alta, verifique el valor");
                    if (isValid) editTarifaElectrica.requestFocus();
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                editTarifaElectrica.setError("Ingrese un valor numérico válido");
                if (isValid) editTarifaElectrica.requestFocus();
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Valida que el nombre solo contenga letras y espacios
     *
     * @param name Nombre a validar
     * @return true si es válido
     */
    private boolean isValidName(String name) {
        return Pattern.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", name);
    }

    /**
     * Valida que la contraseña sea fuerte (al menos una letra y un número)
     *
     * @param password Contraseña a validar
     * @return true si es fuerte
     */
    private boolean isStrongPassword(String password) {
        return Pattern.matches(".*[a-zA-Z].*", password) &&
                Pattern.matches(".*[0-9].*", password);
    }

    /**
     * Limpia todos los errores de los campos
     */
    private void clearErrors() {
        editName.setError(null);
        editEmail.setError(null);
        editPassword.setError(null);
        editConfirmPassword.setError(null);
        editTarifaElectrica.setError(null);
    }

    /**
     * Maneja el registro exitoso
     */
    private void onRegistrationSuccess() {
        showSuccess("¡Cuenta creada exitosamente! Ahora puede iniciar sesión");

        // Regresar al login después de un breve delay
        new android.os.Handler().postDelayed(() -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }, 1500);
    }

    /**
     * Maneja el registro fallido
     */
    private void onRegistrationFailed() {
        showError("Error al crear la cuenta. Intente nuevamente");
    }

    // ==================== UTILIDADES DE UI ====================

    /**
     * Muestra un mensaje de error
     *
     * @param message Mensaje a mostrar
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Muestra un mensaje de éxito
     *
     * @param message Mensaje a mostrar
     */
    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // ==================== MÉTODOS DE NAVEGACIÓN ====================

    /**
     * Override del botón de atrás del sistema
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Navegación automática de vuelta al login
    }
}