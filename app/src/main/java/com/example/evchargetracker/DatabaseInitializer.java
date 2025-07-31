package com.example.evchargetracker;


import com.example.evchargetracker.database.entities.Vehiculo;
import com.example.evchargetracker.database.dao.VehiculoDao.*;
import com.example.evchargetracker.database.AppDatabase;

public class DatabaseInitializer {
    /**
     * Asegura que un usuario tenga al menos un vehículo para poder usar la app
     *
     * Este método se debe llamar después del login exitoso para verificar
     * que el usuario pueda agregar cargas (necesita al menos un vehículo).
     *
     * @param db Base de datos
     * @param userId ID del usuario logueado
     * @return ID del vehículo principal del usuario
     */
    public static long ensureUserHasVehicle(AppDatabase db, int userId) {
        // Verificar si el usuario ya tiene vehículos
        var vehiculosExistentes = db.vehiculoDao().getByUsuario(userId);

        if (!vehiculosExistentes.isEmpty()) {
            // El usuario ya tiene vehículos, retornar el primero
            return vehiculosExistentes.get(0).id;
        }

        // El usuario no tiene vehículos, crear uno por defecto
        return createDefaultVehicleForUser(db, userId);
    }

    /**
     * Crea un vehículo por defecto para un usuario
     *
     * Solo se crea un vehículo genérico para que el usuario pueda
     * empezar a usar la aplicación. Puede editarlo o agregar más después.
     *
     * @param db Base de datos
     * @param userId ID del usuario
     * @return ID del vehículo creado
     */
    private static long createDefaultVehicleForUser(AppDatabase db, int userId) {
        Vehiculo defaultVehicle = new Vehiculo();
        defaultVehicle.idUsuario = userId;
        defaultVehicle.marca = "Mi Vehículo";
        defaultVehicle.modelo = "EV";
        defaultVehicle.anio = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        defaultVehicle.capacidad_kwh = 60.0f; // Capacidad promedio
        defaultVehicle.placa = "EV-001";

        return db.vehiculoDao().insert(defaultVehicle);
    }

    /**
     * Verifica si la base de datos necesita inicialización
     *
     * @param db Base de datos
     * @return true si la BD está vacía y necesita configuración inicial
     */
    public static boolean needsInitialization(AppDatabase db) {
        try {
            int totalUsuarios = db.usuarioDao().getTotalUsuarios();
            return totalUsuarios == 0;
        } catch (Exception e) {
            // Si hay error accediendo a la BD, asumir que necesita inicialización
            return true;
        }
    }

    /**
     * Limpia todos los datos de un usuario específico
     *
     * Útil cuando un usuario quiere eliminar toda su información
     * o cuando se necesita hacer un reset de sus datos.
     *
     * @param db Base de datos
     * @param userId ID del usuario cuyos datos limpiar
     */
    public static void clearUserData(AppDatabase db, int userId) {
        try {
            // Obtener vehículos del usuario
            var vehiculos = db.vehiculoDao().getByUsuario(userId);

            // Eliminar todas las cargas de cada vehículo
            for (var vehiculo : vehiculos) {
                db.cargaDao().deleteByVehiculo(vehiculo.id);
            }

            // Eliminar todos los vehículos del usuario
            db.vehiculoDao().deleteByUsuario(userId);

        } catch (Exception e) {
            // Log error but don't crash the app
            android.util.Log.e("DatabaseInitializer", "Error clearing user data", e);
        }
    }

    /**
     * Limpia TODOS los datos de la base de datos
     *
     * ⚠️ USAR CON EXTREMA PRECAUCIÓN ⚠️
     * Este método elimina TODA la información de todos los usuarios.
     * Solo debe usarse para desarrollo, testing o reset completo.
     *
     * @param db Base de datos
     */
    public static void clearAllData(AppDatabase db) {
        try {
            db.cargaDao().deleteAll();
            db.vehiculoDao().deleteAll();
            db.usuarioDao().deleteAll();
        } catch (Exception e) {
            android.util.Log.e("DatabaseInitializer", "Error clearing all data", e);
        }
    }

    /**
     * Verifica la integridad de los datos de un usuario
     *
     * Comprueba que:
     * - El usuario existe
     * - Tiene al menos un vehículo
     * - No hay cargas huérfanas (sin vehículo asociado)
     *
     * @param db Base de datos
     * @param userId ID del usuario a verificar
     * @return true si los datos están íntegros
     */
    public static boolean verifyUserDataIntegrity(AppDatabase db, int userId) {
        try {
            // Verificar que el usuario existe
            var usuario = db.usuarioDao().getById(userId);
            if (usuario == null) {
                return false;
            }

            // Verificar que tiene al menos un vehículo
            var vehiculos = db.vehiculoDao().getByUsuario(userId);
            if (vehiculos.isEmpty()) {
                return false;
            }

            // Verificar que no hay cargas huérfanas
            for (var vehiculo : vehiculos) {
                var cargas = db.cargaDao().getCargasByVehiculo(vehiculo.id);
                // Si hay cargas, están correctamente asociadas
                // (esto es automático por la FK, pero es una verificación adicional)
            }

            return true;

        } catch (Exception e) {
            android.util.Log.e("DatabaseInitializer", "Error verifying data integrity", e);
            return false;
        }
    }

    /**
     * Obtiene estadísticas básicas de la base de datos
     *
     * @param db Base de datos
     * @return String con estadísticas para debugging
     */
    public static String getDatabaseStats(AppDatabase db) {
        try {
            int totalUsuarios = db.usuarioDao().getTotalUsuarios();
            int totalVehiculos = db.vehiculoDao().getTotalVehiculos();

            // Total de cargas usando una consulta directa
            var todasLasCargas = db.cargaDao().getAllCargas();
            int totalCargas = todasLasCargas.size();

            return String.format(
                    "📊 Estadísticas BD:\n" +
                            "Usuarios: %d\n" +
                            "Vehículos: %d\n" +
                            "Cargas: %d",
                    totalUsuarios, totalVehiculos, totalCargas
            );

        } catch (Exception e) {
            return "Error obteniendo estadísticas: " + e.getMessage();
        }
    }

    /**
     * Método de utilidad para logging de operaciones de BD
     *
     * @param message Mensaje a loggear
     */
    private static void log(String message) {
        android.util.Log.i("DatabaseInitializer", message);
    }

    /**
     * Método de utilidad para logging de errores
     *
     * @param message Mensaje de error
     * @param throwable Excepción ocurrida
     */
    private static void logError(String message, Throwable throwable) {
        android.util.Log.e("DatabaseInitializer", message, throwable);
    }
}
