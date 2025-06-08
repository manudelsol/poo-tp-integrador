package servicio;

import excepciones.EventoException;
import excepciones.PersistenciaException;
import modelo.Evento;
import modelo.Asistente;
import persistencia.PersistenciaArchivos;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class GestorEventos {
    private List<Evento> eventos;
    private int contadorId;
    private int contadorIdAsistente;
    private PersistenciaArchivos persistencia;
    private List<Asistente> asistentes;
    
    public GestorEventos() {
        this.eventos = new ArrayList<>();
        this.asistentes = new ArrayList<>();
        this.persistencia = new PersistenciaArchivos();
        this.contadorId = 1;
        this.contadorIdAsistente = 1;
        
        try {
            cargarDatos();
        } catch (PersistenciaException e) {
            System.err.println("Advertencia al cargar datos: " + e.getMessage());
        }
    }
    
    public Evento crearEvento(String nombre, LocalDate fecha, String ubicacion, String descripcion) 
            throws EventoException {
        
        // Validaciones adicionales de negocio
        if (fecha.isBefore(LocalDate.now())) {
            throw new EventoException("La fecha del evento no puede ser anterior a hoy");
        }
        
        // Verificar si ya existe un evento con el mismo nombre en la misma fecha
        boolean existeEvento = eventos.stream()
            .anyMatch(e -> e.getNombre().equalsIgnoreCase(nombre) && e.getFecha().equals(fecha));
            
        if (existeEvento) {
            throw new EventoException("Ya existe un evento con el mismo nombre en esa fecha");
        }
        
        try {
            Evento nuevoEvento = new Evento(contadorId++, nombre, fecha, ubicacion, descripcion);
            eventos.add(nuevoEvento);
            guardarDatos();
            return nuevoEvento;
        } catch (Exception e) {
            throw new EventoException("Error al crear evento: " + e.getMessage(), e);
        }
    }
    
    public void modificarEvento(int id, String nombre, LocalDate fecha, String ubicacion, String descripcion) 
            throws EventoException {
        
        Optional<Evento> eventoOpt = buscarEventoPorId(id);
        if (!eventoOpt.isPresent()) {
            throw new EventoException("No se encontró el evento con ID: " + id);
        }
        
        Evento evento = eventoOpt.get();
        
        // Si es un evento pasado, no permitir modificaciones
        if (evento.esPasado()) {
            throw new EventoException("No se puede modificar un evento que ya pasó");
        }
        
        try {
            evento.setNombre(nombre);
            evento.setFecha(fecha);
            evento.setUbicacion(ubicacion);
            evento.setDescripcion(descripcion);
            guardarDatos();
        } catch (Exception e) {
            throw new EventoException("Error al modificar evento: " + e.getMessage(), e);
        }
    }
    
    public void eliminarEvento(int id) throws EventoException {
        Optional<Evento> eventoOpt = buscarEventoPorId(id);
        if (!eventoOpt.isPresent()) {
            throw new EventoException("No se encontró el evento con ID: " + id);
        }
        
        Evento evento = eventoOpt.get();
        eventos.remove(evento);
        
        try {
            guardarDatos();
        } catch (PersistenciaException e) {
            // Rollback: volver a agregar el evento
            eventos.add(evento);
            throw new EventoException("Error al eliminar evento: " + e.getMessage(), e);
        }
    }
    
    public Optional<Evento> buscarEventoPorId(int id) {
        return eventos.stream()
            .filter(e -> e.getId() == id)
            .findFirst();
    }
    
    public List<Evento> listarEventosFuturos() {
        return eventos.stream()
            .filter(Evento::esFuturo)
            .sorted(Comparator.comparing(Evento::getFecha))
            .collect(Collectors.toList());
    }
    
    public List<Evento> listarEventosPasados() {
        return eventos.stream()
            .filter(Evento::esPasado)
            .sorted(Comparator.comparing(Evento::getFecha).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene lista de todos los eventos
     * @return Lista de todos los eventos ordenados por fecha
     */
    public List<Evento> listarTodosLosEventos() {
        return eventos.stream()
            .sorted(Comparator.comparing(Evento::getFecha))
            .collect(Collectors.toList());
    }
    
    /**
     * Crea un nuevo asistente
     * @param nombre Nombre del asistente
     * @param email Email del asistente
     * @param telefono Teléfono del asistente
     * @return El asistente creado
     * @throws EventoException si hay error en la validación
     */
    public Asistente crearAsistente(String nombre, String email, String telefono) throws EventoException {
        // Verificar si ya existe un asistente con el mismo email
        boolean existeAsistente = asistentes.stream()
            .anyMatch(a -> a.getEmail().equalsIgnoreCase(email));
            
        if (existeAsistente) {
            throw new EventoException("Ya existe un asistente con ese email");
        }
        
        try {
            Asistente nuevoAsistente = new Asistente(contadorIdAsistente++, nombre, email, telefono);
            asistentes.add(nuevoAsistente);
            guardarDatos();
            return nuevoAsistente;
        } catch (Exception e) {
            throw new EventoException("Error al crear asistente: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca un asistente por ID
     * @param id ID del asistente
     * @return Optional con el asistente si existe
     */
    public Optional<Asistente> buscarAsistentePorId(int id) {
        return asistentes.stream()
            .filter(a -> a.getId() == id)
            .findFirst();
    }
    
    /**
     * Obtiene lista de todos los asistentes
     * @return Lista de asistentes
     */
    public List<Asistente> listarAsistentes() {
        return new ArrayList<>(asistentes);
    }
    
    /**
     * Agrega un asistente existente a un evento
     * @param idEvento ID del evento
     * @param idAsistente ID del asistente
     * @throws EventoException si no existe el evento o asistente
     */
    public void agregarAsistenteAEvento(int idEvento, int idAsistente) throws EventoException {
        Optional<Evento> eventoOpt = buscarEventoPorId(idEvento);
        if (!eventoOpt.isPresent()) {
            throw new EventoException("No se encontró el evento");
        }
        
        Optional<Asistente> asistenteOpt = buscarAsistentePorId(idAsistente);
        if (!asistenteOpt.isPresent()) {
            throw new EventoException("No se encontró el asistente");
        }
        
        Evento evento = eventoOpt.get();
        Asistente asistente = asistenteOpt.get();
        
        evento.agregarAsistente(asistente);
        
        try {
            guardarDatos();
        } catch (PersistenciaException e) {
            throw new EventoException("Error al agregar asistente al evento: " + e.getMessage(), e);
        }
    }
    
    /**
     * Carga los datos desde archivos
     * @throws PersistenciaException si hay error en la carga
     */
    public void cargarDatos() throws PersistenciaException {
        try {
            asistentes = persistencia.cargarAsistentes();
            eventos = persistencia.cargarEventos();
            
            // Actualizar contadores
            if (!eventos.isEmpty()) {
                contadorId = eventos.stream()
                    .mapToInt(Evento::getId)
                    .max()
                    .orElse(0) + 1;
            }
            
            if (!asistentes.isEmpty()) {
                contadorIdAsistente = asistentes.stream()
                    .mapToInt(Asistente::getId)
                    .max()
                    .orElse(0) + 1;
            }
            
            System.out.println("Datos cargados: " + eventos.size() + " eventos, " + asistentes.size() + " asistentes");
            
        } catch (PersistenciaException e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Guarda los datos en archivos
     * @throws PersistenciaException si hay error en el guardado
     */
    public void guardarDatos() throws PersistenciaException {
        try {
            persistencia.guardarEventos(eventos);
            persistencia.guardarAsistentes(asistentes);
        } catch (PersistenciaException e) {
            System.err.println("Error al guardar datos: " + e.getMessage());
            throw e;
        }
    }
} 