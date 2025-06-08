package persistencia;

import excepciones.PersistenciaException;
import modelo.Evento;
import modelo.Asistente;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class PersistenciaArchivos {
    
    // Constantes para archivos y formato
    public static final String ARCHIVO_EVENTOS = "eventos.csv";
    public static final String ARCHIVO_ASISTENTES = "asistentes.csv";
    public static final String SEPARADOR = ",";
    
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public PersistenciaArchivos() {
        try {
            crearArchivosIniciales();
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudieron crear archivos iniciales: " + e.getMessage());
        }
    }
    
    public void guardarEventos(List<Evento> eventos) throws PersistenciaException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_EVENTOS))) {
            for (Evento evento : eventos) {
                String linea = formatearEvento(evento);
                writer.println(linea);
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar eventos: " + e.getMessage(), e);
        }
    }
    
    public List<Evento> cargarEventos() throws PersistenciaException {
        List<Evento> eventos = new ArrayList<>();
        List<Asistente> asistentes = cargarAsistentes();
        
        if (!Files.exists(Paths.get(ARCHIVO_EVENTOS))) {
            return eventos; // Retorna lista vacía si no existe el archivo
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_EVENTOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    try {
                        Evento evento = parsearEvento(linea, asistentes);
                        eventos.add(evento);
                    } catch (Exception e) {
                        System.err.println("Error al parsear línea: " + linea + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al cargar eventos: " + e.getMessage(), e);
        }
        
        return eventos;
    }
    
    public void guardarAsistentes(List<Asistente> asistentes) throws PersistenciaException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_ASISTENTES))) {
            for (Asistente asistente : asistentes) {
                String linea = formatearAsistente(asistente);
                writer.println(linea);
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar asistentes: " + e.getMessage(), e);
        }
    }
    
    public List<Asistente> cargarAsistentes() throws PersistenciaException {
        List<Asistente> asistentes = new ArrayList<>();
        
        if (!Files.exists(Paths.get(ARCHIVO_ASISTENTES))) {
            return asistentes; // Retorna lista vacía si no existe el archivo
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_ASISTENTES))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    try {
                        Asistente asistente = parsearAsistente(linea);
                        asistentes.add(asistente);
                    } catch (Exception e) {
                        System.err.println("Error al parsear asistente: " + linea + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al cargar asistentes: " + e.getMessage(), e);
        }
        
        return asistentes;
    }
    
    private void crearArchivosIniciales() throws PersistenciaException {
        try {
            Path archivoEventos = Paths.get(ARCHIVO_EVENTOS);
            Path archivoAsistentes = Paths.get(ARCHIVO_ASISTENTES);
            
            if (!Files.exists(archivoEventos)) {
                Files.createFile(archivoEventos);
            }
            
            if (!Files.exists(archivoAsistentes)) {
                Files.createFile(archivoAsistentes);
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al crear archivos iniciales: " + e.getMessage(), e);
        }
    }
    
    public void validarIntegridad() throws PersistenciaException {
        Path archivoEventos = Paths.get(ARCHIVO_EVENTOS);
        Path archivoAsistentes = Paths.get(ARCHIVO_ASISTENTES);
        
        if (!Files.exists(archivoEventos) || !Files.exists(archivoAsistentes)) {
            throw new PersistenciaException("Archivos de datos faltantes");
        }
        
        if (!Files.isReadable(archivoEventos) || !Files.isReadable(archivoAsistentes)) {
            throw new PersistenciaException("Archivos de datos no legibles");
        }
        
        if (!Files.isWritable(archivoEventos) || !Files.isWritable(archivoAsistentes)) {
            throw new PersistenciaException("Archivos de datos no escribibles");
        }
    }
    
    private String formatearEvento(Evento evento) {
        String idsAsistentes = evento.getAsistentes().stream()
            .map(a -> String.valueOf(a.getId()))
            .collect(Collectors.joining(";"));
            
        return String.join(SEPARADOR,
            String.valueOf(evento.getId()),
            escaparCampoCSV(evento.getNombre()),
            evento.getFecha().format(FORMATO_FECHA),
            escaparCampoCSV(evento.getUbicacion()),
            escaparCampoCSV(evento.getDescripcion()),
            idsAsistentes
        );
    }
    
    private Evento parsearEvento(String linea, List<Asistente> asistentesDisponibles) throws Exception {
        String[] partes = parsearLineaCSV(linea);
        if (partes.length < 5) {
            throw new IllegalArgumentException("Formato de evento inválido");
        }
        
        int id = Integer.parseInt(partes[0]);
        String nombre = desescaparCampoCSV(partes[1]);
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(partes[2], FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido: " + partes[2]);
        }
        String ubicacion = desescaparCampoCSV(partes[3]);
        String descripcion = desescaparCampoCSV(partes[4]);
        
        Evento evento = new Evento(id, nombre, fecha, ubicacion, descripcion);
        
        // Agregar asistentes si existen
        if (partes.length > 5 && !partes[5].trim().isEmpty()) {
            String[] idsAsistentes = partes[5].split(";");
            for (String idStr : idsAsistentes) {
                try {
                    int idAsistente = Integer.parseInt(idStr.trim());
                    Optional<Asistente> asistente = asistentesDisponibles.stream()
                        .filter(a -> a.getId() == idAsistente)
                        .findFirst();
                    if (asistente.isPresent()) {
                        evento.agregarAsistente(asistente.get());
                    }
                } catch (NumberFormatException e) {
                    System.err.println("ID de asistente inválido: " + idStr);
                }
            }
        }
        
        return evento;
    }
    
    private String formatearAsistente(Asistente asistente) {
        return String.join(SEPARADOR,
            String.valueOf(asistente.getId()),
            escaparCampoCSV(asistente.getNombre()),
            escaparCampoCSV(asistente.getEmail()),
            escaparCampoCSV(asistente.getTelefono())
        );
    }
    
    private Asistente parsearAsistente(String linea) throws Exception {
        String[] partes = parsearLineaCSV(linea);
        if (partes.length != 4) {
            throw new IllegalArgumentException("Formato de asistente inválido");
        }
        
        int id = Integer.parseInt(partes[0]);
        String nombre = desescaparCampoCSV(partes[1]);
        String email = desescaparCampoCSV(partes[2]);
        String telefono = desescaparCampoCSV(partes[3]);
        
        return new Asistente(id, nombre, email, telefono);
    }
    
    private String[] parsearLineaCSV(String linea) {
        List<String> campos = new ArrayList<>();
        boolean dentroComillas = false;
        StringBuilder campoActual = new StringBuilder();
        
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            
            if (c == '"') {
                // Si la siguiente también es comilla, es una comilla escapada
                if (i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    campoActual.append('"');
                    i++; // Saltar la siguiente comilla
                } else {
                    // Cambiar estado de estar dentro de comillas
                    dentroComillas = !dentroComillas;
                }
            } else if (c == ',' && !dentroComillas) {
                // Fin de campo
                campos.add(campoActual.toString());
                campoActual.setLength(0);
            } else {
                campoActual.append(c);
            }
        }
        
        // Agregar el último campo
        campos.add(campoActual.toString());
        
        return campos.toArray(new String[0]);
    }
    
    private String escaparCampoCSV(String campo) {
        if (campo == null) {
            return "";
        }
        // Si el campo contiene comas, comillas o saltos de línea, lo encerramos en comillas
        if (campo.contains(",") || campo.contains("\"") || campo.contains("\n") || campo.contains("\r")) {
            // Escapar comillas duplicándolas
            String campoEscapado = campo.replace("\"", "\"\"");
            return "\"" + campoEscapado + "\"";
        }
        return campo;
    }
    
    private String desescaparCampoCSV(String campo) {
        if (campo == null) {
            return "";
        }
        // Si el campo está entre comillas, removerlas y desescapar comillas internas
        if (campo.startsWith("\"") && campo.endsWith("\"") && campo.length() > 1) {
            String campoSinComillas = campo.substring(1, campo.length() - 1);
            return campoSinComillas.replace("\"\"", "\"");
        }
        return campo;
    }
} 
