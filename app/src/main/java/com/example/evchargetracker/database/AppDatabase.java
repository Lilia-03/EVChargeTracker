package com.example.evchargetracker.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.evchargetracker.database.entities.*;
import com.example.evchargetracker.database.dao.*;

@Database(entities = {Usuario.class, Vehiculo.class, Carga.class},
        version = 4, // ✅ AUMENTAR VERSIÓN A 4
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsuarioDao usuarioDao();
    public abstract VehiculoDao vehiculoDao();
    public abstract CargaDao cargaDao();

    private static volatile AppDatabase INSTANCE;

    // Migración de versión 2 a 3 (agregar campo tipo_carga)
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Agregar columna tipo_carga a la tabla cargas
            database.execSQL("ALTER TABLE cargas ADD COLUMN tipo_carga TEXT DEFAULT 'Carga lenta'");

            // Actualizar registros existentes basado en duración
            database.execSQL("UPDATE cargas SET tipo_carga = 'Carga rápida' WHERE duracion_min <= 60");
            database.execSQL("UPDATE cargas SET tipo_carga = 'Carga lenta' WHERE duracion_min > 60");
        }
    };

    // ✅ NUEVA MIGRACIÓN: de versión 3 a 4 (agregar campo tarifa_utilizada)
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Agregar columna tarifa_utilizada a la tabla cargas
            database.execSQL("ALTER TABLE cargas ADD COLUMN tarifa_utilizada REAL DEFAULT 0.50");

            // Para registros existentes, calcular la tarifa basada en costo/energia_kwh
            database.execSQL(
                    "UPDATE cargas SET tarifa_utilizada = " +
                            "CASE " +
                            "  WHEN energia_kwh > 0 THEN ROUND(costo / energia_kwh, 2) " +
                            "  ELSE 0.50 " +
                            "END"
            );
        }
    };

    public static AppDatabase getDatabase(android.content.Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "mi_base_de_datos")
                            .addMigrations(MIGRATION_2_3, MIGRATION_3_4) // ✅ AGREGAR AMBAS MIGRACIONES
                            .allowMainThreadQueries() // Only for development
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}