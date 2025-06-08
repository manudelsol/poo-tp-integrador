package excepciones;

/**
 * Excepción personalizada para manejar errores de lectura/escritura de archivos
 */
public class PersistenciaException extends Exception {
    
    public PersistenciaException(String mensaje) {
        super(mensaje);
    }
    
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
} 