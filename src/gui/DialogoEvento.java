package gui;

import excepciones.EventoException;
import modelo.Evento;
import servicio.GestorEventos;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

/**
 * Diálogo para crear o editar eventos
 */
public class DialogoEvento extends JDialog {
    
    private Evento evento;
    private GestorEventos gestorEventos;
    private JTextField txtNombre;
    private JTextField txtUbicacion;
    private JTextArea txtDescripcion;
    private JDateChooser dateChooser;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private boolean esNuevoEvento;
    private boolean guardado = false;
    
    /**
     * Constructor para crear un nuevo evento
     * @param parent Ventana padre
     * @param gestorEventos Gestor de eventos
     */
    public DialogoEvento(JFrame parent, GestorEventos gestorEventos) {
        this(parent, null, gestorEventos);
    }
    
    /**
     * Constructor para crear nuevo evento o editar existente
     * @param parent Ventana padre
     * @param evento Evento a editar (null para nuevo)
     * @param gestorEventos Gestor de eventos
     */
    public DialogoEvento(JFrame parent, Evento evento, GestorEventos gestorEventos) {
        super(parent, true); // Modal
        this.evento = evento;
        this.gestorEventos = gestorEventos;
        this.esNuevoEvento = (evento == null);
        
        inicializarComponentes();
        configurarLayout();
        configurarEventos();
        
        if (!esNuevoEvento) {
            cargarDatosEvento();
        }
        
        // Configurar diálogo
        setTitle(esNuevoEvento ? "Nuevo Evento" : "Editar Evento");
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
    }
    
    /**
     * Inicializa los componentes del diálogo
     */
    private void inicializarComponentes() {
        // Campos de texto
        txtNombre = new JTextField(20);
        txtUbicacion = new JTextField(20);
        txtDescripcion = new JTextArea(5, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        
        // Selector de fecha JCalendar
        dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(200, 25));
        dateChooser.setDateFormatString("dd/MM/yyyy");
        
        // Establecer fecha inicial
        if (esNuevoEvento) {
            dateChooser.setDate(new Date());
            // Establecer fecha mínima (hoy)
            dateChooser.setMinSelectableDate(new Date());
        }
        
        // Botones
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        
        // Establecer botón por defecto
        getRootPane().setDefaultButton(btnGuardar);
    }
    
    /**
     * Configura el layout del diálogo
     */
    private void configurarLayout() {
        setLayout(new BorderLayout());
        
        // Panel principal con campos
        JPanel panelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panelCampos.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelCampos.add(txtNombre, gbc);
        
        // Fecha
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panelCampos.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelCampos.add(dateChooser, gbc);
        
        // Ubicación
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panelCampos.add(new JLabel("Ubicación:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelCampos.add(txtUbicacion, gbc);
        
        // Descripción
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panelCampos.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        panelCampos.add(new JScrollPane(txtDescripcion), gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        // Agregar paneles al diálogo
        add(panelCampos, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
        
        // Borde con padding
        panelCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    /**
     * Configura los eventos de los componentes
     */
    private void configurarEventos() {
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarEvento();
            }
        });
        
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelar();
            }
        });
        
        // Validación en tiempo real para todos los campos de texto
        java.awt.event.KeyAdapter validadorTexto = new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                validarCampos();
            }
        };
        
        txtNombre.addKeyListener(validadorTexto);
        txtUbicacion.addKeyListener(validadorTexto);
        txtDescripcion.addKeyListener(validadorTexto);
        
        // Evento cuando cambia la fecha
        dateChooser.addPropertyChangeListener("date", e -> validarCampos());
        
        // Validación inicial
        SwingUtilities.invokeLater(() -> validarCampos());
    }
    
    /**
     * Carga los datos del evento si está en modo edición
     */
    private void cargarDatosEvento() {
        if (evento != null) {
            txtNombre.setText(evento.getNombre());
            txtUbicacion.setText(evento.getUbicacion());
            txtDescripcion.setText(evento.getDescripcion());
            
            // Convertir LocalDate a Date para JDateChooser
            Date date = Date.from(evento.getFecha().atStartOfDay(ZoneId.systemDefault()).toInstant());
            dateChooser.setDate(date);
        }
    }
    
    /**
     * Valida los campos del formulario
     * @return true si todos los campos son válidos
     */
    private boolean validarCampos() {
        boolean valido = true;
        String mensaje = "";
        
        // Validar nombre
        if (txtNombre.getText().trim().isEmpty()) {
            valido = false;
            mensaje += "- El nombre es obligatorio\n";
        }
        
        // Validar ubicación
        if (txtUbicacion.getText().trim().isEmpty()) {
            valido = false;
            mensaje += "- La ubicación es obligatoria\n";
        }
        
        // Validar descripción
        if (txtDescripcion.getText().trim().isEmpty()) {
            valido = false;
            mensaje += "- La descripción es obligatoria\n";
        }
        
        // Validar fecha
        Date fechaDate = dateChooser.getDate();
        if (fechaDate == null) {
            valido = false;
            mensaje += "- Debe seleccionar una fecha\n";
        } else if (esNuevoEvento) {
            LocalDate fecha = fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (fecha.isBefore(LocalDate.now())) {
                valido = false;
                mensaje += "- La fecha no puede ser anterior a hoy\n";
            }
        }
        
        // Actualizar estado del botón guardar
        btnGuardar.setEnabled(valido);
        
        if (!valido && !mensaje.isEmpty()) {
            // Opcional: mostrar mensaje de validación
            // JOptionPane.showMessageDialog(this, mensaje, "Campos inválidos", JOptionPane.WARNING_MESSAGE);
        }
        
        return valido;
    }
    
    /**
     * Guarda el evento (crear o modificar)
     */
    private void guardarEvento() {
        if (!validarCampos()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor complete todos los campos correctamente", 
                "Campos inválidos", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String nombre = txtNombre.getText().trim();
            String ubicacion = txtUbicacion.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            
            // Convertir Date a LocalDate
            Date fechaDate = dateChooser.getDate();
            LocalDate fecha = fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            if (esNuevoEvento) {
                // Crear nuevo evento
                gestorEventos.crearEvento(nombre, fecha, ubicacion, descripcion);
                JOptionPane.showMessageDialog(this, 
                    "Evento creado correctamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Modificar evento existente
                gestorEventos.modificarEvento(evento.getId(), nombre, fecha, ubicacion, descripcion);
                JOptionPane.showMessageDialog(this, 
                    "Evento modificado correctamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
            guardado = true;
            dispose();
            
        } catch (EventoException e) {
            JOptionPane.showMessageDialog(this, 
                e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error inesperado: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Cancela la operación y cierra el diálogo
     */
    private void cancelar() {
        // Verificar si hay cambios sin guardar
        if (hayCambiosSinGuardar()) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de cancelar? Se perderán los cambios no guardados.", 
                "Confirmar cancelación", 
                JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        guardado = false;
        dispose();
    }
    
    private boolean hayCambiosSinGuardar() {
        if (esNuevoEvento) {
            return !txtNombre.getText().trim().isEmpty() || 
                   !txtUbicacion.getText().trim().isEmpty() || 
                   !txtDescripcion.getText().trim().isEmpty();
        } else {
            // Comparar con datos originales del evento
            Date fechaActual = dateChooser.getDate();
            LocalDate fechaSeleccionada = fechaActual != null ? 
                fechaActual.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
            
            return !txtNombre.getText().trim().equals(evento.getNombre()) ||
                   !txtUbicacion.getText().trim().equals(evento.getUbicacion()) ||
                   !txtDescripcion.getText().trim().equals(evento.getDescripcion()) ||
                   (fechaSeleccionada != null && !fechaSeleccionada.equals(evento.getFecha()));
        }
    }
    
    public boolean fueGuardado() {
        return guardado;
    }
    

} 