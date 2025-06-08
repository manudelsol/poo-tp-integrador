package main;

import gui.VentanaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    
    public static void main(String[] args) {
        // Ejecutar la interfaz gráfica en el Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Crear y mostrar la ventana principal
                    VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
                    ventanaPrincipal.setVisible(true);
                    
                    System.out.println("=== Sistema de Gestión de Eventos ===");
                    System.out.println("Aplicación iniciada correctamente");
                    System.out.println("Datos almacenados en: eventos.csv y asistentes.csv");
                    
                } catch (Exception e) {
                    System.err.println("Error al iniciar la aplicación: " + e.getMessage());
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });
    }
} 