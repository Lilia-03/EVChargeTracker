package com.example.evchargetracker.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.evchargetracker.database.entities.Usuario;

import java.util.List;

@Dao
public interface UsuarioDao {
    // ==================== OPERACIONES DE INSERCIÓN ====================

    /**
     * Inserta un nuevo usuario en la base de datos
     *
     * @param usuario Usuario a insertar
     * @return ID del usuario insertado
     */
    @Insert
    long insert(Usuario usuario);


     /* Inserta múltiples usuarios de una vez
     *
     * @param usuarios Array de usuarios a insertar*/

    @Insert
    void insertAll(Usuario... usuarios);

    // ==================== OPERACIONES DE CONSULTA ====================

    /**
     * Obtiene todos los usuarios de la base de datos
     *
     * @return Lista de todos los usuarios
     */
    @Query("SELECT * FROM usuarios")
    List<Usuario> getAll();

    /**
     * Busca un usuario por su ID
     *
     * @param id ID del usuario a buscar
     * @return Usuario encontrado o null si no existe
     */
    @Query("SELECT * FROM usuarios WHERE id = :id")
    Usuario getById(int id);

    /**
     * Busca un usuario por su email (para login)
     *
     * @param email Email del usuario
     * @return Usuario encontrado o null si no existe
     */
    @Query("SELECT * FROM usuarios WHERE email = :email")
    Usuario getByEmail(String email);

    /**
     * Valida credenciales de login
     *
     * @param email Email del usuario
     * @param contrasena Contraseña del usuario
     * @return Usuario si las credenciales son correctas, null si no
     */
    @Query("SELECT * FROM usuarios WHERE email = :email AND contrasena = :contrasena")
    Usuario validateLogin(String email, String contrasena);

    /**
     * Verifica si un email ya existe en el sistema
     *
     * @param email Email a verificar
     * @return true si el email ya existe
     */
    @Query("SELECT COUNT(*) > 0 FROM usuarios WHERE email = :email")
    boolean emailExists(String email);

    /**
     * Busca usuarios por nombre (búsqueda parcial)
     *
     * @param nombre Parte del nombre a buscar
     * @return Lista de usuarios que coinciden
     */
    @Query("SELECT * FROM usuarios WHERE nombre LIKE '%' || :nombre || '%'")
    List<Usuario> searchByName(String nombre);

    /**
     * Obtiene la tarifa eléctrica de un usuario específico
     *
     * @param userId ID del usuario
     * @return Tarifa eléctrica del usuario
     */
    @Query("SELECT tarifa_electrica FROM usuarios WHERE id = :userId")
    Float getTarifaElectrica(int userId);

    // ==================== OPERACIONES DE ACTUALIZACIÓN ====================

    /**
     * Actualiza un usuario completo
     *
     * @param usuario Usuario con datos actualizados
     * @return Número de filas afectadas
     */
    @Update
    int update(Usuario usuario);

    /**
     * Actualiza solo el nombre de un usuario
     *
     * @param userId ID del usuario
     * @param nuevoNombre Nuevo nombre
     * @return Número de filas afectadas
     */
    @Query("UPDATE usuarios SET nombre = :nuevoNombre WHERE id = :userId")
    int updateNombre(int userId, String nuevoNombre);

    /**
     * Actualiza solo la tarifa eléctrica de un usuario
     *
     * @param userId ID del usuario
     * @param nuevaTarifa Nueva tarifa eléctrica
     * @return Número de filas afectadas
     */
    @Query("UPDATE usuarios SET tarifa_electrica = :nuevaTarifa WHERE id = :userId")
    int updateTarifaElectrica(int userId, float nuevaTarifa);

    /**
     * Actualiza la contraseña de un usuario
     *
     * @param userId ID del usuario
     * @param nuevaContrasena Nueva contraseña
     * @return Número de filas afectadas
     */
    @Query("UPDATE usuarios SET contrasena = :nuevaContrasena WHERE id = :userId")
    int updateContrasena(int userId, String nuevaContrasena);

    // ==================== OPERACIONES DE ELIMINACIÓN ====================

    /**
     * Elimina un usuario específico
     *
     * @param usuario Usuario a eliminar
     */
    @Delete
    void delete(Usuario usuario);

    /**
     * Elimina un usuario por su ID
     *
     * @param userId ID del usuario a eliminar
     * @return Número de filas eliminadas
     */
    @Query("DELETE FROM usuarios WHERE id = :userId")
    int deleteById(int userId);

    /**
     * Elimina todos los usuarios de la base de datos
     * ¡USAR CON PRECAUCIÓN!
     */
    @Query("DELETE FROM usuarios")
    void deleteAll();

    // ==================== CONSULTAS ESTADÍSTICAS ====================

    /**
     * Cuenta el total de usuarios registrados
     *
     * @return Número total de usuarios
     */
    @Query("SELECT COUNT(*) FROM usuarios")
    int getTotalUsuarios();

    /**
     * Obtiene la tarifa eléctrica promedio de todos los usuarios
     *
     * @return Tarifa promedio
     */
    @Query("SELECT AVG(tarifa_electrica) FROM usuarios")
    Float getTarifaPromedio();

    /**
     * Obtiene usuarios con tarifa eléctrica en un rango específico
     *
     * @param tarifaMin Tarifa mínima
     * @param tarifaMax Tarifa máxima
     * @return Lista de usuarios en ese rango de tarifa
     */
    @Query("SELECT * FROM usuarios WHERE tarifa_electrica BETWEEN :tarifaMin AND :tarifaMax")
    List<Usuario> getUsuariosByTarifaRange(float tarifaMin, float tarifaMax);
}