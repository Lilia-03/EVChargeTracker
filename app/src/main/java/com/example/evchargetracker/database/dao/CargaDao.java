package com.example.evchargetracker.database.dao;

import androidx.room.*;
import com.example.evchargetracker.database.entities.Carga;
import java.util.List;

@Dao
public interface CargaDao {
    @Insert
    long insert(Carga carga);

    @Insert
    void insertAll(Carga... cargas);

    @Query("SELECT * FROM cargas")
    List<Carga> getAllCargas();

    @Query("SELECT * FROM cargas WHERE lugar LIKE :ubicacion")
    List<Carga> getCargasByUbicacion(String ubicacion);

    @Query("SELECT * FROM cargas WHERE lugar LIKE :tipo")
    List<Carga> getCargasByTipo(String tipo);

    @Query("SELECT * FROM cargas ORDER BY fecha DESC LIMIT :limit")
    List<Carga> getCargasRecientes(int limit);

    @Query("SELECT * FROM cargas WHERE idVehiculo = :vehiculoId")
    List<Carga> getCargasByVehiculo(int vehiculoId);

    @Delete
    void delete(Carga carga);

    @Query("DELETE FROM cargas")
    void deleteAll();

    @Update
    void update(Carga carga);
}
