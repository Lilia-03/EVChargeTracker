package com.example.evchargetracker.database.dao;

import androidx.room.*;


import com.example.evchargetracker.database.entities.Vehiculo;
import java.util.List;
/**
 * DAO (Data Access Object) para la entidad Vehiculo
 *
 * Esta interface define todas las operaciones que se pueden realizar
 * sobre la tabla 'vehiculos' en la base de datos.
 *
 * Room genera automáticamente la implementación de estos métodos.
 *
 * @version 1.0
 */
@Dao
public interface VehiculoDao {

    // ==================== OPERACIONES DE INSERCIÓN ====================

    /**
     * Inserta un nuevo vehículo en la base de datos
     *
     * @param vehiculo Vehículo a insertar
     * @return ID del vehículo insertado
     */
    @Insert
    long insert(Vehiculo vehiculo);

    /**
     * Inserta múltiples vehículos de una vez
     *
     * @param vehiculos Array de vehículos a insertar
     */
    @Insert
    void insertAll(Vehiculo... vehiculos);

    // ==================== OPERACIONES DE CONSULTA ====================

    /**
     * Obtiene todos los vehículos de la base de datos
     *
     * @return Lista de todos los vehículos
     */
    @Query("SELECT * FROM vehiculos")
    List<Vehiculo> getAll();

    /**
     * Busca un vehículo por su ID
     *
     * @param id ID del vehículo a buscar
     * @return Vehículo encontrado o null si no existe
     */
    @Query("SELECT * FROM vehiculos WHERE id = :id")
    Vehiculo getById(int id);

    /**
     * Obtiene todos los vehículos de un usuario específico
     *
     * @param idUsuario ID del usuario propietario
     * @return Lista de vehículos del usuario
     */
    @Query("SELECT * FROM vehiculos WHERE idUsuario = :idUsuario")
    List<Vehiculo> getByUsuario(int idUsuario);

    /**
     * Busca vehículos por marca
     *
     * @param marca Marca a buscar
     * @return Lista de vehículos de esa marca
     */
    @Query("SELECT * FROM vehiculos WHERE marca = :marca")
    List<Vehiculo> getByMarca(String marca);

    /**
     * Busca vehículos por marca y modelo
     *
     * @param marca Marca del vehículo
     * @param modelo Modelo del vehículo
     * @return Lista de vehículos que coinciden
     */
    @Query("SELECT * FROM vehiculos WHERE marca = :marca AND modelo = :modelo")
    List<Vehiculo> getByMarcaModelo(String marca, String modelo);

    /**
     * Busca un vehículo por su placa
     *
     * @param placa Placa del vehículo
     * @return Vehículo encontrado o null si no existe
     */
    @Query("SELECT * FROM vehiculos WHERE placa = :placa")
    Vehiculo getByPlaca(String placa);

    /**
     * Verifica si una placa ya existe en el sistema
     *
     * @param placa Placa a verificar
     * @return true si la placa ya existe
     */
    @Query("SELECT COUNT(*) > 0 FROM vehiculos WHERE placa = :placa")
    boolean placaExists(String placa);

    /**
     * Obtiene el primer vehículo de un usuario (vehículo principal)
     *
     * @param idUsuario ID del usuario
     * @return Primer vehículo del usuario o null si no tiene
     */
    @Query("SELECT * FROM vehiculos WHERE idUsuario = :idUsuario LIMIT 1")
    Vehiculo getPrimaryVehicle(int idUsuario);

    /**
     * Busca vehículos por rango de años
     *
     * @param anioMinimo Año mínimo
     * @param anioMaximo Año máximo
     * @return Lista de vehículos en ese rango de años
     */
    @Query("SELECT * FROM vehiculos WHERE anio BETWEEN :anioMinimo AND :anioMaximo")
    List<Vehiculo> getByRangoAnios(int anioMinimo, int anioMaximo);

    /**
     * Busca vehículos por capacidad de batería mínima
     *
     * @param capacidadMinima Capacidad mínima en kWh
     * @return Lista de vehículos con al menos esa capacidad
     */
    @Query("SELECT * FROM vehiculos WHERE capacidad_kwh >= :capacidadMinima")
    List<Vehiculo> getByCapacidadMinima(float capacidadMinima);

    // ==================== OPERACIONES DE ACTUALIZACIÓN ====================

    /**
     * Actualiza un vehículo completo
     *
     * @param vehiculo Vehículo con datos actualizados
     * @return Número de filas afectadas
     */
    @Update
    int update(Vehiculo vehiculo);

    /**
     * Actualiza solo la placa de un vehículo
     *
     * @param vehiculoId ID del vehículo
     * @param nuevaPlaca Nueva placa
     * @return Número de filas afectadas
     */
    @Query("UPDATE vehiculos SET placa = :nuevaPlaca WHERE id = :vehiculoId")
    int updatePlaca(int vehiculoId, String nuevaPlaca);

    /**
     * Actualiza la capacidad de batería de un vehículo
     *
     * @param vehiculoId ID del vehículo
     * @param nuevaCapacidad Nueva capacidad en kWh
     * @return Número de filas afectadas
     */
    @Query("UPDATE vehiculos SET capacidad_kwh = :nuevaCapacidad WHERE id = :vehiculoId")
    int updateCapacidad(int vehiculoId, float nuevaCapacidad);

    // ==================== OPERACIONES DE ELIMINACIÓN ====================

    /**
     * Elimina un vehículo específico
     *
     * @param vehiculo Vehículo a eliminar
     */
    @Delete
    void delete(Vehiculo vehiculo);

    /**
     * Elimina un vehículo por su ID
     *
     * @param vehiculoId ID del vehículo a eliminar
     * @return Número de filas eliminadas
     */
    @Query("DELETE FROM vehiculos WHERE id = :vehiculoId")
    int deleteById(int vehiculoId);

    /**
     * Elimina todos los vehículos de un usuario específico
     *
     * @param idUsuario ID del usuario
     * @return Número de vehículos eliminados
     */
    @Query("DELETE FROM vehiculos WHERE idUsuario = :idUsuario")
    int deleteByUsuario(int idUsuario);

    /**
     * Elimina todos los vehículos de la base de datos
     * ¡USAR CON PRECAUCIÓN!
     */
    @Query("DELETE FROM vehiculos")
    void deleteAll();

    // ==================== CONSULTAS ESTADÍSTICAS ====================

    /**
     * Cuenta el total de vehículos registrados
     *
     * @return Número total de vehículos
     */
    @Query("SELECT COUNT(*) FROM vehiculos")
    int getTotalVehiculos();

    /**
     * Cuenta los vehículos de un usuario específico
     *
     * @param idUsuario ID del usuario
     * @return Número de vehículos del usuario
     */
    @Query("SELECT COUNT(*) FROM vehiculos WHERE idUsuario = :idUsuario")
    int getCountByUsuario(int idUsuario);

    /**
     * Obtiene la capacidad promedio de batería de todos los vehículos
     *
     * @return Capacidad promedio en kWh
     */
    @Query("SELECT AVG(capacidad_kwh) FROM vehiculos")
    Float getCapacidadPromedio();

    /**
     * Obtiene las marcas más populares
     *
     * @return Lista de marcas ordenadas por popularidad
     */
    @Query("SELECT marca, COUNT(*) as cantidad FROM vehiculos GROUP BY marca ORDER BY cantidad DESC")
    List<MarcaPopularidad> getMarcasPopulares();

    /**
     * Clase para representar la popularidad de marcas
     */
    class MarcaPopularidad {
        public String marca;
        public int cantidad;
    }

    /**
     * Obtiene el año más común de los vehículos
     *
     * @return Año más común
     */
    @Query("SELECT anio FROM vehiculos GROUP BY anio ORDER BY COUNT(*) DESC LIMIT 1")
    Integer getAnioMasComun();

    /**
     * Busca vehículos con texto en marca o modelo
     *
     * @param texto Texto a buscar
     * @return Lista de vehículos que coinciden
     */
    @Query("SELECT * FROM vehiculos WHERE marca LIKE '%' || :texto || '%' OR modelo LIKE '%' || :texto || '%'")
    List<Vehiculo> searchVehiculos(String texto);
}