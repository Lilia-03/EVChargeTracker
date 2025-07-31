package com.example.evchargetracker.database.dao;

import androidx.room.*;
import com.example.evchargetracker.database.entities.Carga;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Carga
 *
 * Esta interface define todas las operaciones que se pueden realizar
 * sobre la tabla 'cargas' en la base de datos.
 *
 * Room genera automáticamente la implementación de estos métodos.
 *
 * @version 2.0 - Actualizado con nuevos campos
 */
@Dao
public interface CargaDao {

    // ==================== OPERACIONES DE INSERCIÓN ====================

    /**
     * Inserta una nueva carga en la base de datos
     *
     * @param carga Carga a insertar
     * @return ID de la carga insertada
     */
    @Insert
    long insert(Carga carga);

    /**
     * Inserta múltiples cargas de una vez
     *
     * @param cargas Array de cargas a insertar
     */
    @Insert
    void insertAll(Carga... cargas);

    // ==================== OPERACIONES DE CONSULTA BÁSICAS ====================

    /**
     * Obtiene todas las cargas de la base de datos
     *
     * @return Lista de todas las cargas
     */
    @Query("SELECT * FROM cargas ORDER BY fecha DESC")
    List<Carga> getAllCargas();

    /**
     * Busca una carga por su ID
     *
     * @param id ID de la carga a buscar
     * @return Carga encontrada o null si no existe
     */
    @Query("SELECT * FROM cargas WHERE id = :id")
    Carga getById(int id);

    /**
     * Obtiene las cargas más recientes (limitadas)
     *
     * @param limit Número máximo de cargas a retornar
     * @return Lista de cargas recientes
     */
    @Query("SELECT * FROM cargas ORDER BY fecha DESC LIMIT :limit")
    List<Carga> getCargasRecientes(int limit);

    // ==================== CONSULTAS POR VEHÍCULO ====================

    /**
     * Obtiene todas las cargas de un vehículo específico
     *
     * @param vehiculoId ID del vehículo
     * @return Lista de cargas del vehículo
     */
    @Query("SELECT * FROM cargas WHERE idVehiculo = :vehiculoId ORDER BY fecha DESC")
    List<Carga> getCargasByVehiculo(int vehiculoId);

    /**
     * Cuenta las cargas de un vehículo específico
     *
     * @param vehiculoId ID del vehículo
     * @return Número de cargas del vehículo
     */
    @Query("SELECT COUNT(*) FROM cargas WHERE idVehiculo = :vehiculoId")
    int getCountByVehiculo(int vehiculoId);

    // ==================== CONSULTAS POR UBICACIÓN ====================

    /**
     * Obtiene cargas filtradas por ubicación
     *
     * @param ubicacion Ubicación a buscar (permite wildcards con %)
     * @return Lista de cargas en esa ubicación
     */
    @Query("SELECT * FROM cargas WHERE lugar LIKE :ubicacion ORDER BY fecha DESC")
    List<Carga> getCargasByUbicacion(String ubicacion);

    /**
     * Obtiene cargas en casa (contiene "casa")
     *
     * @return Lista de cargas realizadas en casa
     */
    @Query("SELECT * FROM cargas WHERE lugar LIKE '%Casa%' OR lugar LIKE '%casa%' ORDER BY fecha DESC")
    List<Carga> getCargasEnCasa();

    /**
     * Obtiene cargas fuera de casa
     *
     * @return Lista de cargas realizadas fuera de casa
     */
    @Query("SELECT * FROM cargas WHERE lugar NOT LIKE '%Casa%' AND lugar NOT LIKE '%casa%' ORDER BY fecha DESC")
    List<Carga> getCargasFueraDeCasa();

    // ==================== CONSULTAS POR TIPO DE CARGA ====================

    /**
     * Obtiene cargas filtradas por tipo
     *
     * @param tipo Tipo de carga ("Carga lenta" o "Carga rápida")
     * @return Lista de cargas de ese tipo
     */
    @Query("SELECT * FROM cargas WHERE tipo_carga = :tipo ORDER BY fecha DESC")
    List<Carga> getCargasByTipo(String tipo);

    /**
     * Obtiene solo cargas lentas
     *
     * @return Lista de cargas lentas
     */
    @Query("SELECT * FROM cargas WHERE tipo_carga = 'Carga lenta' ORDER BY fecha DESC")
    List<Carga> getCargasLentas();

    /**
     * Obtiene solo cargas rápidas
     *
     * @return Lista de cargas rápidas
     */
    @Query("SELECT * FROM cargas WHERE tipo_carga = 'Carga rápida' ORDER BY fecha DESC")
    List<Carga> getCargasRapidas();

    // ==================== CONSULTAS POR FECHA ====================

    /**
     * Obtiene cargas de una fecha específica
     *
     * @param fecha Fecha en formato "dd/MM/yyyy"
     * @return Lista de cargas de esa fecha
     */
    @Query("SELECT * FROM cargas WHERE fecha = :fecha ORDER BY fecha DESC")
    List<Carga> getCargasByFecha(String fecha);

    /**
     * Obtiene cargas de un mes específico
     *
     * @param mes Mes en formato "MM" (ej: "12" para diciembre)
     * @param anio Año en formato "yyyy" (ej: "2024")
     * @return Lista de cargas de ese mes
     */
    @Query("SELECT * FROM cargas WHERE fecha LIKE '%/' || :mes || '/' || :anio ORDER BY fecha DESC")
    List<Carga> getCargasByMes(String mes, String anio);

    /**
     * Obtiene cargas de un año específico
     *
     * @param anio Año en formato "yyyy"
     * @return Lista de cargas de ese año
     */
    @Query("SELECT * FROM cargas WHERE fecha LIKE '%/' || :anio ORDER BY fecha DESC")
    List<Carga> getCargasByAnio(String anio);

    // ==================== CONSULTAS ESTADÍSTICAS ====================

    /**
     * Obtiene el total de energía cargada (suma de todos los kWh)
     *
     * @return Total de kWh cargados
     */
    @Query("SELECT SUM(energia_kwh) FROM cargas")
    Float getTotalEnergiaKwh();

    /**
     * Obtiene el costo total de todas las cargas
     *
     * @return Costo total acumulado
     */
    @Query("SELECT SUM(costo) FROM cargas")
    Float getTotalCosto();

    /**
     * Obtiene el costo promedio por carga
     *
     * @return Costo promedio
     */
    @Query("SELECT AVG(costo) FROM cargas")
    Float getCostoPromedio();

    /**
     * Obtiene la duración total de todas las cargas
     *
     * @return Duración total en minutos
     */
    @Query("SELECT SUM(duracion_min) FROM cargas")
    Integer getTotalDuracion();

    /**
     * Obtiene estadísticas de cargas en casa vs fuera
     *
     * @return Número de cargas en casa
     */
    @Query("SELECT COUNT(*) FROM cargas WHERE lugar LIKE '%Casa%' OR lugar LIKE '%casa%'")
    int getCargasEnCasaCount();

    /**
     * Obtiene la tarifa más alta utilizada
     *
     * @return Tarifa máxima registrada
     */
    @Query("SELECT MAX(tarifa_utilizada) FROM cargas")
    Float getTarifaMaxima();

    /**
     * Obtiene la tarifa más baja utilizada
     *
     * @return Tarifa mínima registrada
     */
    @Query("SELECT MIN(tarifa_utilizada) FROM cargas")
    Float getTarifaMinima();

    /**
     * Obtiene la tarifa promedio utilizada
     *
     * @return Tarifa promedio
     */
    @Query("SELECT AVG(tarifa_utilizada) FROM cargas")
    Float getTarifaPromedio();

    // ==================== CONSULTAS POR RANGO DE VALORES ====================

    /**
     * Obtiene cargas por rango de costo
     *
     * @param costoMin Costo mínimo
     * @param costoMax Costo máximo
     * @return Lista de cargas en ese rango de costo
     */
    @Query("SELECT * FROM cargas WHERE costo BETWEEN :costoMin AND :costoMax ORDER BY fecha DESC")
    List<Carga> getCargasByCostoRange(float costoMin, float costoMax);

    /**
     * Obtiene cargas por rango de energía
     *
     * @param kwhMin kWh mínimos
     * @param kwhMax kWh máximos
     * @return Lista de cargas en ese rango de energía
     */
    @Query("SELECT * FROM cargas WHERE energia_kwh BETWEEN :kwhMin AND :kwhMax ORDER BY fecha DESC")
    List<Carga> getCargasByEnergiaRange(float kwhMin, float kwhMax);

    /**
     * Obtiene cargas por duración mínima
     *
     * @param duracionMin Duración mínima en minutos
     * @return Lista de cargas con al menos esa duración
     */
    @Query("SELECT * FROM cargas WHERE duracion_min >= :duracionMin ORDER BY fecha DESC")
    List<Carga> getCargasByDuracionMinima(int duracionMin);

    // ==================== OPERACIONES DE ACTUALIZACIÓN ====================

    /**
     * Actualiza una carga completa
     *
     * @param carga Carga con datos actualizados
     * @return Número de filas afectadas
     */
    @Update
    int update(Carga carga);

    /**
     * Actualiza solo el costo de una carga
     *
     * @param cargaId ID de la carga
     * @param nuevoCosto Nuevo costo
     * @return Número de filas afectadas
     */
    @Query("UPDATE cargas SET costo = :nuevoCosto WHERE id = :cargaId")
    int updateCosto(int cargaId, float nuevoCosto);

    /**
     * Actualiza solo el lugar de una carga
     *
     * @param cargaId ID de la carga
     * @param nuevoLugar Nuevo lugar
     * @return Número de filas afectadas
     */
    @Query("UPDATE cargas SET lugar = :nuevoLugar WHERE id = :cargaId")
    int updateLugar(int cargaId, String nuevoLugar);

    // ==================== OPERACIONES DE ELIMINACIÓN ====================

    /**
     * Elimina una carga específica
     *
     * @param carga Carga a eliminar
     */
    @Delete
    void delete(Carga carga);

    /**
     * Elimina una carga por su ID
     *
     * @param cargaId ID de la carga a eliminar
     * @return Número de filas eliminadas
     */
    @Query("DELETE FROM cargas WHERE id = :cargaId")
    int deleteById(int cargaId);

    /**
     * Elimina todas las cargas de un vehículo específico
     *
     * @param vehiculoId ID del vehículo
     * @return Número de cargas eliminadas
     */
    @Query("DELETE FROM cargas WHERE idVehiculo = :vehiculoId")
    int deleteByVehiculo(int vehiculoId);

    /**
     * Elimina cargas anteriores a una fecha específica
     *
     * @param fecha Fecha límite en formato "dd/MM/yyyy"
     * @return Número de cargas eliminadas
     */
    @Query("DELETE FROM cargas WHERE fecha < :fecha")
    int deleteAnterioresA(String fecha);

    /**
     * Elimina todas las cargas de la base de datos
     * ¡USAR CON PRECAUCIÓN!
     */
    @Query("DELETE FROM cargas")
    void deleteAll();

    // ==================== CONSULTAS ESPECIALES ====================

    /**
     * Obtiene la ubicación más utilizada
     *
     * @return Ubicación más frecuente
     */
    @Query("SELECT lugar FROM cargas GROUP BY lugar ORDER BY COUNT(*) DESC LIMIT 1")
    String getUbicacionMasFrecuente();

    /**
     * Obtiene las cargas más costosas
     *
     * @param limit Número de cargas a retornar
     * @return Lista de cargas más costosas
     */
    @Query("SELECT * FROM cargas ORDER BY costo DESC LIMIT :limit")
    List<Carga> getCargasMasCostosas(int limit);

    /**
     * Obtiene las cargas más económicas
     *
     * @param limit Número de cargas a retornar
     * @return Lista de cargas más baratas
     */
    @Query("SELECT * FROM cargas ORDER BY costo ASC LIMIT :limit")
    List<Carga> getCargasMasEconomicas(int limit);

    /**
     * Busca cargas que contengan texto en el lugar
     *
     * @param texto Texto a buscar en el lugar
     * @return Lista de cargas que coinciden
     */
    @Query("SELECT * FROM cargas WHERE lugar LIKE '%' || :texto || '%' ORDER BY fecha DESC")
    List<Carga> searchByLugar(String texto);

    /**
     * Obtiene el total de cargas por tipo
     *
     * @return Lista con conteos por tipo de carga
     */
    @Query("SELECT tipo_carga, COUNT(*) as cantidad FROM cargas GROUP BY tipo_carga")
    List<TipoCargaCount> getCountByTipo();

    /**
     * Clase para representar conteos por tipo de carga
     */
    class TipoCargaCount {
        public String tipo_carga;
        public int cantidad;
    }

    /**
     * Obtiene estadísticas mensuales
     *
     * @param mes Mes en formato "MM"
     * @param anio Año en formato "yyyy"
     * @return Estadísticas del mes
     */
    @Query("SELECT COUNT(*) as total_cargas, SUM(energia_kwh) as total_kwh, SUM(costo) as total_costo, AVG(costo) as promedio_costo FROM cargas WHERE fecha LIKE '%/' || :mes || '/' || :anio")
    EstadisticasMensuales getEstadisticasMensuales(String mes, String anio);

    /**
     * Clase para estadísticas mensuales
     */
    class EstadisticasMensuales {
        public int total_cargas;
        public float total_kwh;
        public float total_costo;
        public float promedio_costo;
    }
}
