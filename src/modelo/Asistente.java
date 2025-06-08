package modelo;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Clase que representa una persona que asiste a eventos
 */
public class Asistente {
    private int id;
    private String nombre;
    private String email;
    private String telefono;
    
    // Patrón básico para validación de email
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    public Asistente(int id, String nombre, String email, String telefono) {
        setId(id);
        setNombre(nombre);
        setEmail(email);
        setTelefono(telefono);
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
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (!validarEmail(email.trim())) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }
        this.email = email.trim().toLowerCase();
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono no puede estar vacío");
        }
        this.telefono = telefono.trim();
    }
    
    /**
     * Valida el formato del email usando regex básico
     * @param email Email a validar
     * @return true si el formato es válido
     */
    private boolean validarEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Asistente asistente = (Asistente) obj;
        return id == asistente.id || 
               Objects.equals(email, asistente.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s)", nombre, email);
    }
} 