package com.example.evchargetracker.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "cargas",
        foreignKeys = @ForeignKey(entity = Vehiculo.class,
                parentColumns = "id",
                childColumns = "idVehiculo",
                onDelete = ForeignKey.CASCADE))
public class Carga {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int idVehiculo = 1; // Default to first vehicle
    public String fecha;
    public float energia_kwh;
    public int duracion_min;
    public float costo;
    public String lugar;
    public String tipo_carga; // "Carga lenta" o "Carga rápida"
    public float tarifa_utilizada; // Tarifa utilizada en el momento del cálculo
}
