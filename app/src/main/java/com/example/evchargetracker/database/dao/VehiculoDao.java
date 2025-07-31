package com.example.evchargetracker.database.dao;

import androidx.room.*;


import com.example.evchargetracker.database.entities.Vehiculo;
import java.util.List;
@Dao
public interface VehiculoDao {
    @Insert
    long insert(Vehiculo vehiculo);

    @Insert
    void insertAll(Vehiculo... vehiculos);

    @Query("SELECT * FROM vehiculos WHERE idUsuario = :idUsuario")
    List<Vehiculo> getByUsuario(int idUsuario);

    @Query("SELECT * FROM vehiculos")
    List<Vehiculo> getAll();

    @Query("SELECT * FROM vehiculos WHERE id = :id")
    Vehiculo getById(int id);

    @Delete
    void delete(Vehiculo vehiculo);

    @Query("DELETE FROM vehiculos")
    void deleteAll();
}