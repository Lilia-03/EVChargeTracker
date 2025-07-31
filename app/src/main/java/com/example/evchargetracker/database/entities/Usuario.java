package com.example.evchargetracker.database.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entidad Usuario - Representa un usuario de la aplicación
 *
 * Esta clase define la estructura de la tabla 'usuarios' en la base de datos.
 * Cada usuario puede tener múltiples vehículos y por ende múltiples cargas.
 *
 * Campos principales:
 * - Información personal: nombre, email
 * - Autenticación: contraseña (debería estar encriptada en producción)
 * - Configuración: tarifa eléctrica personal
 *
 * @version 1.0
 */
@Entity(
        tableName = "usuarios",
        indices = {@Index(value = "email", unique = true)} // Email único
)

public class Usuario {

    /**
     * ID único del usuario (clave primaria)
     * Se genera automáticamente al insertar en la BD
     */
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nombre;
    public String email; //unico en el sistema, es identificador del login
    public String contrasena;
    public float tarifa_electrica = 0.25f;

    //constructor vacio para room
    public Usuario() {}
    public Usuario(String nombre, String email, String contrasena, float tarifa_electrica) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.tarifa_electrica = tarifa_electrica;
    }

    public Usuario(String nombre, String email, String contrasena) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.tarifa_electrica = 0.25f;
    }
    //Metodo para validar si el email tiene formato correcto
    public boolean isEmailValid() {
        return email != null &&
                email.contains("@") &&
                email.contains(".") &&
                email.length() > 5;
    }

    //Metodo para obtener las iniciales del usuario
    //Útil para mostrar avatares con las iniciales del ususario
    public String getIniciales() {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "??";
        }

        String[] partes = nombre.trim().split(" ");
        StringBuilder iniciales = new StringBuilder();

        for (String parte : partes) {
            if (!parte.isEmpty() && iniciales.length() < 2) {
                iniciales.append(parte.charAt(0));
            }
        }

        return iniciales.toString().toUpperCase();
    }

    /**
     * Metodo toString para debugging
     */
    @Override
    public String toString() {
        return String.format("Usuario{id=%d, nombre='%s', email='%s', tarifa=$%.2f}",
                id, nombre, email, tarifa_electrica);
    }

    /**
     * Metodo equals basado en email (ya que es único)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Usuario usuario = (Usuario) obj;
        return email != null ? email.equals(usuario.email) : usuario.email == null;
    }

    /**
     * Hash code basado en email
     */
    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }
}

