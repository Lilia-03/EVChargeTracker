package com.example.evchargetracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evchargetracker.R;
import com.example.evchargetracker.models.Carga;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador para mostrar cargas eléctricas en un RecyclerView
 *
 * Este adaptador se encarga de:
 * - Convertir datos de cargas en elementos visuales
 * - Manejar diferentes tipos de visualización (completa vs resumida)
 * - Aplicar estilos según el tipo de carga
 * - Formatear fechas, costos y duraciones para mostrar al usuario
 *
 * Soporta dos modos de visualización:
 * - Completa: Muestra fecha y hora completas
 * - Resumida: Solo muestra la fecha
 *
 * @author EV Charge Tracker Team
 * @version 2.0 - Actualizado con nuevos campos y mejor UI
 */
public class CargaAdapter extends RecyclerView.Adapter<CargaAdapter.CargaViewHolder> {

    // Datos y configuración
    private List<Carga> cargas;
    private boolean mostrarFechaCompleta;

    // Formateadores para mostrar datos consistentemente
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());

    // Listener para clics en elementos (opcional)
    private OnCargaClickListener clickListener;

    /**
     * Interface para manejar clics en las cargas
     */
    public interface OnCargaClickListener {
        void onCargaClick(Carga carga);
        void onCargaLongClick(Carga carga);
    }

    /**
     * Constructor del adaptador
     *
     * @param cargas Lista de cargas a mostrar
     * @param mostrarFechaCompleta true para mostrar fecha y hora, false solo fecha
     */
    public CargaAdapter(List<Carga> cargas, boolean mostrarFechaCompleta) {
        this.cargas = cargas;
        this.mostrarFechaCompleta = mostrarFechaCompleta;
    }


     /* Establece un listener para clics en las cargas
     *
     * @param listener Listener a establecer
     */
    public void setOnCargaClickListener(OnCargaClickListener listener) {
        this.clickListener = listener;
    }


     /* Crea nuevas vistas para los elementos del RecyclerView
     *
     * @param parent ViewGroup padre
     * @param viewType Tipo de vista (no usado en este adaptador)
     * @return Nuevo ViewHolder
     */
    @NonNull
    @Override
    public CargaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carga, parent, false);
        return new CargaViewHolder(view);
    }

    /**
     * Vincula datos de una carga específica con una vista
     *
     * Este método es llamado por RecyclerView para mostrar cada elemento.
     * Aquí es donde se configuran todos los elementos visuales.
     *
     * @param holder ViewHolder que contiene las vistas
     * @param position Posición del elemento en la lista
     */
    @Override
    public void onBindViewHolder(@NonNull CargaViewHolder holder, int position) {
        Carga carga = cargas.get(position);

        // ==================== CONFIGURAR ICONO ====================

        // Elegir icono según la ubicación
        if (carga.esCargaEnCasa()) {
            holder.iconoCarga.setImageResource(R.drawable.ic_home);
            holder.iconoCarga.setColorFilter(Color.parseColor("#4CAF50")); // Verde para casa
        } else {
            holder.iconoCarga.setImageResource(R.drawable.ic_charging_station);
            holder.iconoCarga.setColorFilter(Color.parseColor("#FF9800")); // Naranja para estaciones
        }

        // ==================== CONFIGURAR TEXTOS ====================

        // Título: Ubicación + tipo de carga
        String titulo = carga.getUbicacion();
        if (carga.getTipoCarga() != null) {
            titulo += " - " + carga.getTipoCarga();
        }
        holder.txtTitulo.setText(titulo);

        // Fecha: Formato según configuración
        if (mostrarFechaCompleta) {
            holder.txtFecha.setText(dateTimeFormat.format(carga.getFecha()));
        } else {
            holder.txtFecha.setText(dateFormat.format(carga.getFecha()));
        }

        // Información detallada de la carga
        StringBuilder infoCarga = new StringBuilder();

        // Siempre mostrar kWh
        infoCarga.append(decimalFormat.format(carga.getKwhCargados())).append(" kWh");

        // Agregar duración si existe y es mayor a 0
        if (carga.getDuracionMinutos() > 0) {
            infoCarga.append(" • ").append(carga.getDuracionFormateada());
        }

        // Agregar tarifa utilizada si está disponible
        if (carga.getTarifaUtilizada() > 0) {
            infoCarga.append(" • $").append(decimalFormat.format(carga.getTarifaUtilizada())).append("/kWh");
        }

        holder.txtInfoCarga.setText(infoCarga.toString());

        // Costo total
        holder.txtCosto.setText("$" + decimalFormat.format(carga.getCosto()));

        // ==================== CONFIGURAR ESTILO ====================

        // Color de fondo según tipo de carga
        if (carga.esCargaLenta()) {
            holder.itemView.setBackgroundResource(R.drawable.background_carga_lenta);
            holder.txtCosto.setTextColor(Color.parseColor("#2E7D32")); // Verde oscuro
        } else {
            holder.itemView.setBackgroundResource(R.drawable.background_carga_rapida);
            holder.txtCosto.setTextColor(Color.parseColor("#E65100")); // Naranja oscuro
        }

        // ==================== CONFIGURAR LISTENERS ====================

        // Click normal
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onCargaClick(carga);
            }
        });

        // Click largo (para opciones adicionales)
        holder.itemView.setOnLongClickListener(v -> {
            if (clickListener != null) {
                clickListener.onCargaLongClick(carga);
                return true;
            }
            return false;
        });

        // ==================== ANIMACIÓN SUTIL ====================

        // Pequeña animación cuando aparece el elemento
        holder.itemView.setAlpha(0f);
        holder.itemView.animate()
                .alpha(1f)
                .setDuration(200)
                .setStartDelay(position * 50) // Delay escalonado
                .start();
    }

    /**
     * Retorna el número total de elementos
     *
     * @return Cantidad de cargas en la lista
     */
    @Override
    public int getItemCount() {
        return cargas != null ? cargas.size() : 0;
    }

    // ==================== MÉTODOS PÚBLICOS PARA ACTUALIZAR DATOS ====================

    /**
     * Actualiza la lista completa de cargas
     *
     * @param nuevasCargas Nueva lista de cargas
     */
    public void updateCargas(List<Carga> nuevasCargas) {
        this.cargas = nuevasCargas;
        notifyDataSetChanged(); // Recarga toda la lista
    }

    /**
     * Agrega una nueva carga al inicio de la lista
     *
     * @param nuevaCarga Carga a agregar
     */
    public void addCarga(Carga nuevaCarga) {
        if (cargas != null) {
            cargas.add(0, nuevaCarga); // Agregar al inicio
            notifyItemInserted(0);
        }
    }

    /**
     * Elimina una carga de la lista
     *
     * @param position Posición de la carga a eliminar
     */
    public void removeCarga(int position) {
        if (cargas != null && position >= 0 && position < cargas.size()) {
            cargas.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * Actualiza una carga específica
     *
     * @param position Posición de la carga
     * @param cargaActualizada Carga con datos actualizados
     */
    public void updateCarga(int position, Carga cargaActualizada) {
        if (cargas != null && position >= 0 && position < cargas.size()) {
            cargas.set(position, cargaActualizada);
            notifyItemChanged(position);
        }
    }

    /**
     * Cambia el modo de visualización de fecha
     *
     * @param mostrarCompleta true para fecha completa, false para resumida
     */
    public void setMostrarFechaCompleta(boolean mostrarCompleta) {
        this.mostrarFechaCompleta = mostrarCompleta;
        notifyDataSetChanged();
    }

    // ==================== VIEW HOLDER ====================

    /**
     * ViewHolder que contiene las referencias de las vistas
     *
     * Esta clase interna mantiene referencias a todos los elementos
     * visuales de cada item para evitar hacer findViewById repetidamente.
     */
    static class CargaViewHolder extends RecyclerView.ViewHolder {

        // Referencias a las vistas del layout
        ImageView iconoCarga;
        TextView txtTitulo;
        TextView txtFecha;
        TextView txtInfoCarga;
        TextView txtCosto;

        /**
         * Constructor del ViewHolder
         *
         * @param itemView Vista del elemento individual
         */
        public CargaViewHolder(@NonNull View itemView) {
            super(itemView);

            // Encontrar y guardar referencias a las vistas
            iconoCarga = itemView.findViewById(R.id.icono_carga);
            txtTitulo = itemView.findViewById(R.id.txt_titulo_carga);
            txtFecha = itemView.findViewById(R.id.txt_fecha_carga);
            txtInfoCarga = itemView.findViewById(R.id.txt_info_carga);
            txtCosto = itemView.findViewById(R.id.txt_costo_carga);
        }
    }

    // ==================== MÉTODOS UTILITARIOS ====================

    /**
     * Obtiene una carga específica por posición
     *
     * @param position Posición en la lista
     * @return Carga en esa posición, o null si no existe
     */
    public Carga getCarga(int position) {
        if (cargas != null && position >= 0 && position < cargas.size()) {
            return cargas.get(position);
        }
        return null;
    }

    /**
     * Busca la posición de una carga por su ID
     *
     * @param cargaId ID de la carga a buscar
     * @return Posición de la carga, o -1 si no se encuentra
     */
    public int findPositionById(long cargaId) {
        if (cargas != null) {
            for (int i = 0; i < cargas.size(); i++) {
                if (cargas.get(i).getId() == cargaId) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Verifica si la lista está vacía
     *
     * @return true si no hay cargas
     */
    public boolean isEmpty() {
        return cargas == null || cargas.isEmpty();
    }

    /**
     * Limpia todas las cargas de la lista
     */
    public void clear() {
        if (cargas != null) {
            int size = cargas.size();
            cargas.clear();
            notifyItemRangeRemoved(0, size);
        }
    }
}
