package com.example.evchargetracker.models;
import java.util.Date;
/**
 * Modelo Carga para la Interfaz de Usuario
 *
 * Esta clase representa una carga eléctrica desde la perspectiva de la UI.
 * Es diferente de la entidad de base de datos porque:
 * - Usa Date en lugar de String para mejor manejo de fechas
 * - Incluye métodos utilitarios para la interfaz
 * - Facilita la conversión entre la BD y la UI
 *
 * Se usa principalmente en:
 * - Adaptadores de RecyclerView
 * - Pantallas de detalle
 * - Cálculos y estadísticas en tiempo real
 *
 * @author EV Charge Tracker Team
 * @version 2.0 - Actualizado con nuevos campos
 */

public class Carga {
    // Campos principales
    private long id;
    private String ubicacion;
    private Date fecha;
    private double kwhCargados;
    private int duracionMinutos;
    private double costo;
    private String tipoCarga;        // Nuevo campo
    private double tarifaUtilizada;  // Nuevo campo

    /**
     * Constructor completo para crear una carga desde la UI
     *
     * @param ubicacion Lugar donde se realizó la carga
     * @param fecha Fecha y hora de la carga
     * @param kwhCargados Energía cargada en kWh
     * @param duracionMinutos Duración total en minutos
     * @param costo Costo total de la carga
     */
    public Carga(String ubicacion, Date fecha, double kwhCargados,
                 int duracionMinutos, double costo) {
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.kwhCargados = kwhCargados;
        this.duracionMinutos = duracionMinutos;
        this.costo = costo;
    }

    /**
     * Constructor completo con ID (para cargas existentes)
     *
     * @param id ID único de la carga
     * @param ubicacion Lugar de la carga
     * @param fecha Fecha y hora
     * @param kwhCargados Energía en kWh
     * @param duracionMinutos Duración en minutos
     * @param costo Costo total
     */
    public Carga(long id, String ubicacion, Date fecha, double kwhCargados,
                 int duracionMinutos, double costo) {
        this.id = id;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.kwhCargados = kwhCargados;
        this.duracionMinutos = duracionMinutos;
        this.costo = costo;
    }

    /**
     * Constructor completo con todos los campos nuevos
     *
     * @param id ID único
     * @param ubicacion Lugar de la carga
     * @param fecha Fecha y hora
     * @param kwhCargados Energía en kWh
     * @param duracionMinutos Duración en minutos
     * @param costo Costo total
     * @param tipoCarga Tipo de carga (lenta/rápida)
     * @param tarifaUtilizada Tarifa usada para el cálculo
     */
    public Carga(long id, String ubicacion, Date fecha, double kwhCargados,
                 int duracionMinutos, double costo, String tipoCarga, double tarifaUtilizada) {
        this.id = id;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.kwhCargados = kwhCargados;
        this.duracionMinutos = duracionMinutos;
        this.costo = costo;
        this.tipoCarga = tipoCarga;
        this.tarifaUtilizada = tarifaUtilizada;
    }

    // ==================== GETTERS ====================

    public long getId() { return id; }
    public String getUbicacion() { return ubicacion; }
    public Date getFecha() { return fecha; }
    public double getKwhCargados() { return kwhCargados; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public double getCosto() { return costo; }
    public String getTipoCarga() { return tipoCarga; }
    public double getTarifaUtilizada() { return tarifaUtilizada; }

    // ==================== SETTERS ====================

    public void setId(long id) { this.id = id; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public void setKwhCargados(double kwhCargados) { this.kwhCargados = kwhCargados; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public void setCosto(double costo) { this.costo = costo; }
    public void setTipoCarga(String tipoCarga) { this.tipoCarga = tipoCarga; }
    public void setTarifaUtilizada(double tarifaUtilizada) { this.tarifaUtilizada = tarifaUtilizada; }

    // ==================== MÉTODOS UTILITARIOS ====================

    /**
     * Formatea la duración en un string legible
     *
     * Ejemplos:
     * - 30 minutos → "30m"
     * - 90 minutos → "1h 30m"
     * - 120 minutos → "2h"
     *
     * @return String formateado de la duración
     */
    public String getDuracionFormateada() {
        if (duracionMinutos <= 0) {
            return "Sin especificar";
        }

        int horas = duracionMinutos / 60;
        int minutos = duracionMinutos % 60;

        if (horas > 0 && minutos > 0) {
            return horas + "h " + minutos + "m";
        } else if (horas > 0) {
            return horas + "h";
        } else {
            return minutos + "m";
        }
    }

    /**
     * Determina si la carga se realizó en casa
     *
     * @return true si la ubicación contiene "casa" (ignora mayúsculas/minúsculas)
     */
    public boolean esCargaEnCasa() {
        return ubicacion != null && ubicacion.toLowerCase().contains("casa");
    }

    /**
     * Determina si es una carga lenta basándose en varios criterios
     *
     * Criterios para carga lenta:
     * - Duración mayor a 60 minutos, O
     * - Tipo de carga especificado como "Carga lenta", O
     * - Ubicación contiene "casa" (normalmente son cargas lentas)
     *
     * @return true si es carga lenta
     */
    public boolean esCargaLenta() {
        // Si tenemos el tipo específico, usarlo
        if (tipoCarga != null) {
            return tipoCarga.toLowerCase().contains("lenta");
        }

        // Criterio por duración: más de 60 minutos = lenta
        if (duracionMinutos > 60) {
            return true;
        }

        // Criterio por ubicación: casa normalmente = lenta
        return esCargaEnCasa();
    }

    /**
     * Obtiene el tipo de carga, calculándolo si no está especificado
     *
     * @return "Carga lenta" o "Carga rápida"
     */
    public String getTipoCalculado() {
        if (tipoCarga != null && !tipoCarga.isEmpty()) {
            return tipoCarga;
        }

        // Calcular basándose en criterios
        return esCargaLenta() ? "Carga lenta" : "Carga rápida";
    }

    /**
     * Calcula el costo por kWh basándose en los datos actuales
     *
     * @return Costo por kWh, o la tarifa utilizada si está disponible
     */
    public double getCostoPorKwh() {
        if (tarifaUtilizada > 0) {
            return tarifaUtilizada;
        }

        if (kwhCargados > 0) {
            return costo / kwhCargados;
        }

        return 0.0; // No se puede calcular
    }

    /**
     * Calcula la eficiencia de la carga (kWh por minuto)
     * Útil para comparar velocidades de carga
     *
     * @return kWh por minuto, o 0 si no hay duración
     */
    public double getEficienciaCarga() {
        if (duracionMinutos > 0) {
            return kwhCargados / duracionMinutos;
        }
        return 0.0;
    }

    /**
     * Obtiene una descripción completa de la carga para mostrar en listas
     *
     * @return String con información resumida
     */
    public String getDescripcionCompleta() {
        StringBuilder descripcion = new StringBuilder();
        descripcion.append(String.format("%.1f kWh", kwhCargados));

        if (duracionMinutos > 0) {
            descripcion.append(" • ").append(getDuracionFormateada());
        }

        if (tarifaUtilizada > 0) {
            descripcion.append(String.format(" • $%.2f/kWh", tarifaUtilizada));
        }

        return descripcion.toString();
    }

    /**
     * Determina el color a usar en la UI según el tipo de carga
     *
     * @return ID de recurso de color o nombre de color
     */
    public String getColorTipo() {
        return esCargaLenta() ? "#E8F5E8" : "#FFF3E0"; // Verde claro vs Naranja claro
    }

    /**
     * Obtiene el icono apropiado según la ubicación
     *
     * @return Nombre del recurso de icono
     */
    public String getIconoUbicacion() {
        if (esCargaEnCasa()) {
            return "ic_home";
        } else {
            return "ic_charging_station";
        }
    }

    /**
     * Compara esta carga con otra para ordenamiento
     * Por defecto ordena por fecha (más reciente primero)
     *
     * @param otra Carga a comparar
     * @return int para ordenamiento
     */
    public int compareTo(Carga otra) {
        if (this.fecha == null || otra.fecha == null) {
            return 0;
        }
        return otra.fecha.compareTo(this.fecha); // Orden descendente (más reciente primero)
    }

    /**
     * Determina si esta carga es rentable comparada con otra
     *
     * @param otra Carga para comparar
     * @return true si esta carga tiene mejor costo por kWh
     */
    public boolean esMasRentableQue(Carga otra) {
        double miCostoPorKwh = getCostoPorKwh();
        double otroCostoPorKwh = otra.getCostoPorKwh();

        return miCostoPorKwh > 0 && otroCostoPorKwh > 0 && miCostoPorKwh < otroCostoPorKwh;
    }

    /**
     * Genera un resumen textual de la carga para logs o debugging
     */
    @Override
    public String toString() {
        return String.format(
                "Carga{id=%d, ubicacion='%s', kwh=%.1f, duracion=%dm, costo=$%.2f, tipo='%s', tarifa=$%.2f}",
                id, ubicacion, kwhCargados, duracionMinutos, costo, getTipoCalculado(), tarifaUtilizada
        );
    }

    /**
     * Compara dos cargas para igualdad
     * Dos cargas son iguales si tienen el mismo ID
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Carga carga = (Carga) obj;
        return id == carga.id;
    }

    /**
     * Genera hash code basado en el ID
     */
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    // ==================== MÉTODOS ESTÁTICOS UTILITARIOS ====================

    /**
     * Convierte una entidad de base de datos a modelo de UI
     *
     * @param entity Entidad de la base de datos
     * @return Modelo para la UI
     */
    public static Carga fromEntity(com.example.evchargetracker.database.entities.Carga entity) {
        // Convertir fecha string a Date
        Date fecha = parseFecha(entity.fecha);

        return new Carga(
                entity.id,
                entity.lugar,
                fecha,
                entity.energia_kwh,
                entity.duracion_min,
                entity.costo,
                entity.tipo_carga,
                entity.tarifa_utilizada
        );
    }

    /**
     * Convierte un modelo de UI a entidad de base de datos
     *
     * @return Entidad para guardar en la BD
     */
    public com.example.evchargetracker.database.entities.Carga toEntity() {
        com.example.evchargetracker.database.entities.Carga entity =
                new com.example.evchargetracker.database.entities.Carga();

        entity.id = (int) this.id;
        entity.lugar = this.ubicacion;
        entity.fecha = formatFecha(this.fecha);
        entity.energia_kwh = (float) this.kwhCargados;
        entity.duracion_min = this.duracionMinutos;
        entity.costo = (float) this.costo;
        entity.tipo_carga = this.tipoCarga;
        entity.tarifa_utilizada = (float) this.tarifaUtilizada;

        return entity;
    }

    /**
     * Helper para convertir string fecha a Date
     * Formato esperado: "dd/MM/yyyy"
     */
    private static Date parseFecha(String fechaStr) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            return sdf.parse(fechaStr);
        } catch (Exception e) {
            return new Date(); // Fecha actual si hay error
        }
    }

    /**
     * Helper para convertir Date a string
     * Formato: "dd/MM/yyyy"
     */
    private static String formatFecha(Date fecha) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(fecha);
    }
}
