package excepciones;

/**
 * Excepción personalizada para manejar errores relacionados con la lógica de eventos
 */
public class EventoException extends Exception {
    
    public EventoException(String mensaje) {
        super(mensaje);
    }
    
    public EventoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
} 