package com.example.evchargetracker.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Entidad Vehiculo - Representa un vehículo eléctrico
 *
 * Esta clase define la estructura de la tabla 'vehiculos' en la base de datos.
 * Cada usuario puede tener múltiples vehículos, y cada vehículo puede tener
 * múltiples cargas asociadas.
 *
 * Campos principales:
 * - Información del vehículo: marca, modelo, año
 * - Especificaciones técnicas: capacidad de batería
 * - Identificación: placa
 *
 * @version 1.0
 */
@Entity(
        tableName = "vehiculos",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "id",
                childColumns = "idUsuario",
                onDelete = ForeignKey.CASCADE // Si se elimina el usuario, eliminar sus vehículos
        )
)
public class Vehiculo {
    //ID único del vehículo (clave primaria) se genera automaticamente al insertar en bd
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int idUsuario; //ID del usuario propietario del vehículo
    public String marca;
    public String modelo;
    public int anio;
    public float capacidad_kwh;
    public String placa;

    //constructor vacio para room
    public Vehiculo() {}

    public Vehiculo(int idUsuario, String marca, String modelo, int anio,
                    float capacidad_kwh, String placa) {
        this.idUsuario = idUsuario;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.capacidad_kwh = capacidad_kwh;
        this.placa = placa;
    }

    //obtener nombre completo del auto marca modelo año
    public String getNombreCompleto() {
        return String.format("%s %s (%d)", marca, modelo, anio);
    }
    //obtener nombre corto del auto marca modelo
    public String getNombreCorto() {
        return String.format("%s %s", marca, modelo);
    }
    //Calcula el porcentaje de carga basado en kWh cargados
    public float calcularPorcentajeCarga(float kwhCargados) {
        if (capacidad_kwh <= 0) {
            return 0f;
        }

        float porcentaje = (kwhCargados / capacidad_kwh) * 100f;
        return Math.min(100f, Math.max(0f, porcentaje)); // Limitar entre 0 y 100
    }
    //Estima autonomía restante después de una carga
    public float estimarAutonomia(float kwhCargados, float autonomiaPorKwh) {
        return kwhCargados * autonomiaPorKwh;
    }
    public boolean esVehiculoModerno() {
        return anio >= 2020;
    }
    //Obtiene la categoría de capacidad de batería
    public String getCategoriaBateria() {
        if (capacidad_kwh < 40) {
            return "Compacta"; // Carros urbanos pequeños
        } else if (capacidad_kwh < 70) {
            return "Estándar"; // Mayoría de vehículos
        } else if (capacidad_kwh < 100) {
            return "Grande"; // Vehículos premium
        } else {
            return "Extra Grande"; // Vehículos de lujo/comerciales
        }
    }

    //validar que los datos del auto esten completos
    public boolean isDataValid() {
        return marca != null && !marca.trim().isEmpty() &&
                modelo != null && !modelo.trim().isEmpty() &&
                anio > 1990 && anio <= (java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) + 2) &&
                capacidad_kwh > 0 &&
                placa != null && !placa.trim().isEmpty();
    }
    //metodo para debuggin
    @Override
    public String toString() {
        return String.format("Vehiculo{id=%d, usuario=%d, vehiculo='%s', bateria=%.1fkWh, placa='%s'}",
                id, idUsuario, getNombreCompleto(), capacidad_kwh, placa);
    }
    //metodo basado en id
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Vehiculo vehiculo = (Vehiculo) obj;
        return id == vehiculo.id;
    }
    //Hash code basado en iD
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
