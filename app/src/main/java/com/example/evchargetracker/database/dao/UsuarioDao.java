package com.example.evchargetracker.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.evchargetracker.database.entities.Usuario;

import java.util.List;

@Dao
public interface UsuarioDao {
    @Insert
    long insert(Usuario usuario);

    @Insert
    void insertAll(Usuario... usuarios);

    @Query("SELECT * FROM usuarios")
    List<Usuario> getAll();

    @Query("SELECT * FROM usuarios WHERE id = :id")
    Usuario getById(int id);

    @Delete
    void delete(Usuario usuario);

    @Query("DELETE FROM usuarios")
    void deleteAll();
}