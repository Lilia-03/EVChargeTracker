package com.example.evchargetracker.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "vehiculos",
        foreignKeys = @ForeignKey(entity = Usuario.class,
                parentColumns = "id",
                childColumns = "idUsuario",
                onDelete = ForeignKey.CASCADE))
public class Vehiculo {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int idUsuario;
    public String marca;
    public String modelo;
    public int anio;
    public float capacidad_kwh;
    public String placa;
}
