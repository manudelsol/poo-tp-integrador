package modelo;

import excepciones.EventoException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Clase que representa un evento individual
 */
public class Evento {
    private int id;
    private String nombre;
    private LocalDate fecha;
    private String ubicacion;
    private String descripcion;
    private List<Asistente> asistentes;
    
    public Evento(int id, String nombre, LocalDate fecha, String ubicacion, String descripcion) {
        setId(id);
        setNombre(nombre);
        setFecha(fecha);
        setUbicacion(ubicacion);
        setDescripcion(descripcion);
        this.asistentes = new ArrayList<>();
    }
    
    // Getters y Setters con validaciones
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del evento no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        this.fecha = fecha;
    }
    
    public String getUbicacion() {
        return ubicacion;
    }
    
    public void setUbicacion(String ubicacion) {
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La ubicación no puede estar vacía");
        }
        this.ubicacion = ubicacion.trim();
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía");
        }
        this.descripcion = descripcion.trim();
    }
    
    public List<Asistente> getAsistentes() {
        return new ArrayList<>(asistentes); // Retorna copia para evitar modificaciones externas
    }
    
    // Agregar asistente
    public void agregarAsistente(Asistente asistente) throws EventoException {
        if (asistente == null) {
            throw new EventoException("El asistente no puede ser nulo");
        }
        
        if (asistentes.contains(asistente)) {
            throw new EventoException("El asistente ya está registrado en este evento");
        }
        
        asistentes.add(asistente);
    }
    
    // Remover asistente
    public void removerAsistente(int idAsistente) throws EventoException {
        Optional<Asistente> asistenteARemover = asistentes.stream()
            .filter(a -> a.getId() == idAsistente)
            .findFirst();
            
        if (asistenteARemover.isPresent()) {
            asistentes.remove(asistenteARemover.get());
        } else {
            throw new EventoException("No se encontró el asistente con ID: " + idAsistente);
        }
    }
    
    public boolean esFuturo() {
        return fecha.isAfter(LocalDate.now()) || fecha.isEqual(LocalDate.now());
    }
    
    public boolean esPasado() {
        return fecha.isBefore(LocalDate.now());
    }
    
    public int getCantidadAsistentes() {
        return asistentes.size();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Evento evento = (Evento) obj;
        return id == evento.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Evento{id=%d, nombre='%s', fecha=%s, ubicacion='%s', asistentes=%d}",
                           id, nombre, fecha, ubicacion, asistentes.size());
    }
} 