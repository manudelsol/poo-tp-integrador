package gui;

import excepciones.EventoException;
import modelo.Evento;
import modelo.Asistente;
import servicio.GestorEventos;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Diálogo para mostrar detalles del evento y gestionar asistentes
 */
public class DialogoDetallesEvento extends JDialog {
    
    private Evento evento;
    private GestorEventos gestorEventos;
    private JList<Asistente> listaAsistentes;
    private DefaultListModel<Asistente> modeloLista;
    private JTextField txtNombreAsistente;
    private JTextField txtEmailAsistente;
    private JTextField txtTelefonoAsistente;
    private JButton btnAgregarAsistente;
    private JButton btnEliminarAsistente;
    private JButton btnCerrar;
    
    // Labels para información del evento
    private JLabel lblNombre;
    private JLabel lblFecha;
    private JLabel lblUbicacion;
    private JTextArea txtDescripcion;
    
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Constructor del diálogo de detalles
     * @param parent Ventana padre
     * @param evento Evento a mostrar
     * @param gestorEventos Gestor de eventos
     */
    public DialogoDetallesEvento(JFrame parent, Evento evento, GestorEventos gestorEventos) {
        super(parent, true); // Modal
        this.evento = evento;
        this.gestorEventos = gestorEventos;
        
        inicializarComponentes();
        configurarLayout();
        configurarEventos();
        cargarDatosEvento();
        cargarAsistentes();
        
        // Configurar diálogo
        setTitle("Detalles del Evento - " + evento.getNombre());
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    
    /**
     * Inicializa los componentes del diálogo
     */
    private void inicializarComponentes() {
        // Labels para información del evento
        lblNombre = new JLabel();
        lblFecha = new JLabel();
        lblUbicacion = new JLabel();
        txtDescripcion = new JTextArea(3, 30);
        txtDescripcion.setEditable(false);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setBackground(getBackground());
        
        // Lista de asistentes
        modeloLista = new DefaultListModel<>();
        listaAsistentes = new JList<>(modeloLista);
        listaAsistentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Campos para nuevo asistente
        txtNombreAsistente = new JTextField(15);
        txtEmailAsistente = new JTextField(15);
        txtTelefonoAsistente = new JTextField(15);
        
        // Botones
        btnAgregarAsistente = new JButton("Agregar Asistente");
        btnEliminarAsistente = new JButton("Eliminar Asistente");
        btnCerrar = new JButton("Cerrar");
        
        // Estado inicial de botones
        btnEliminarAsistente.setEnabled(false);
    }
    
    /**
     * Configura el layout del diálogo
     */
    private void configurarLayout() {
        setLayout(new BorderLayout());
        
        // Panel superior - Información del evento
        JPanel panelInfoEvento = new JPanel(new GridBagLayout());
        panelInfoEvento.setBorder(BorderFactory.createTitledBorder("Información del Evento"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panelInfoEvento.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelInfoEvento.add(lblNombre, gbc);
        
        // Fecha
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelInfoEvento.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelInfoEvento.add(lblFecha, gbc);
        
        // Ubicación
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelInfoEvento.add(new JLabel("Ubicación:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelInfoEvento.add(lblUbicacion, gbc);
        
        // Descripción
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panelInfoEvento.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        panelInfoEvento.add(new JScrollPane(txtDescripcion), gbc);
        
        // Panel central - Gestión de asistentes
        JPanel panelAsistentes = new JPanel(new BorderLayout());
        panelAsistentes.setBorder(BorderFactory.createTitledBorder("Asistentes"));
        
        // Lista de asistentes
        JScrollPane scrollAsistentes = new JScrollPane(listaAsistentes);
        scrollAsistentes.setPreferredSize(new Dimension(250, 150));
        
        // Panel para agregar asistente
        JPanel panelAgregarAsistente = new JPanel(new GridBagLayout());
        panelAgregarAsistente.setBorder(BorderFactory.createTitledBorder("Agregar Asistente"));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nombre del asistente
        gbc.gridx = 0; gbc.gridy = 0;
        panelAgregarAsistente.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelAgregarAsistente.add(txtNombreAsistente, gbc);
        
        // Email del asistente
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelAgregarAsistente.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelAgregarAsistente.add(txtEmailAsistente, gbc);
        
        // Teléfono del asistente
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelAgregarAsistente.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelAgregarAsistente.add(txtTelefonoAsistente, gbc);
        
        // Botón agregar
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panelAgregarAsistente.add(btnAgregarAsistente, gbc);
        
        // Panel con lista y botón eliminar
        JPanel panelListaAsistentes = new JPanel(new BorderLayout());
        panelListaAsistentes.add(scrollAsistentes, BorderLayout.CENTER);
        
        JPanel panelBtnEliminar = new JPanel(new FlowLayout());
        panelBtnEliminar.add(btnEliminarAsistente);
        panelListaAsistentes.add(panelBtnEliminar, BorderLayout.SOUTH);
        
        // Organizar panel de asistentes
        panelAsistentes.add(panelListaAsistentes, BorderLayout.WEST);
        panelAsistentes.add(panelAgregarAsistente, BorderLayout.CENTER);
        
        // Panel inferior con botón cerrar
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnCerrar);
        
        // Agregar paneles al diálogo
        add(panelInfoEvento, BorderLayout.NORTH);
        add(panelAsistentes, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    /**
     * Configura los eventos de los componentes
     */
    private void configurarEventos() {
        btnAgregarAsistente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarAsistente();
            }
        });
        
        btnEliminarAsistente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarAsistenteSeleccionado();
            }
        });
        
        btnCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Listener para selección en la lista
        listaAsistentes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnEliminarAsistente.setEnabled(listaAsistentes.getSelectedValue() != null);
            }
        });
        
        // Validación en tiempo real para campos de asistente
        java.awt.event.KeyAdapter validadorCampos = new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                validarCamposAsistente();
            }
        };
        
        txtNombreAsistente.addKeyListener(validadorCampos);
        txtEmailAsistente.addKeyListener(validadorCampos);
        txtTelefonoAsistente.addKeyListener(validadorCampos);
        
        // Enter en campos para agregar asistente
        java.awt.event.KeyAdapter enterListener = new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && btnAgregarAsistente.isEnabled()) {
                    agregarAsistente();
                }
            }
        };
        
        txtNombreAsistente.addKeyListener(enterListener);
        txtEmailAsistente.addKeyListener(enterListener);
        txtTelefonoAsistente.addKeyListener(enterListener);
    }
    
    /**
     * Carga los datos del evento en los campos de información
     */
    private void cargarDatosEvento() {
        lblNombre.setText(evento.getNombre());
        lblFecha.setText(evento.getFecha().format(FORMATO_FECHA));
        lblUbicacion.setText(evento.getUbicacion());
        txtDescripcion.setText(evento.getDescripcion());
    }
    
    /**
     * Carga la lista de asistentes del evento
     */
    private void cargarAsistentes() {
        modeloLista.clear();
        List<Asistente> asistentes = evento.getAsistentes();
        for (Asistente asistente : asistentes) {
            modeloLista.addElement(asistente);
        }
    }
    
    /**
     * Agrega un nuevo asistente al evento
     */
    private void agregarAsistente() {
        if (!validarCamposAsistente()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor complete todos los campos del asistente correctamente", 
                "Campos inválidos", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String nombre = txtNombreAsistente.getText().trim();
            String email = txtEmailAsistente.getText().trim();
            String telefono = txtTelefonoAsistente.getText().trim();
            
            // Crear el asistente
            Asistente nuevoAsistente = gestorEventos.crearAsistente(nombre, email, telefono);
            
            // Agregarlo al evento
            gestorEventos.agregarAsistenteAEvento(evento.getId(), nuevoAsistente.getId());
            
            // Actualizar la vista
            cargarAsistentes();
            limpiarCamposAsistente();
            
            JOptionPane.showMessageDialog(this, 
                "Asistente agregado correctamente", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
                
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
     * Elimina el asistente seleccionado del evento
     */
    private void eliminarAsistenteSeleccionado() {
        Asistente asistenteSeleccionado = listaAsistentes.getSelectedValue();
        if (asistenteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un asistente para eliminar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar a " + asistenteSeleccionado.getNombre() + " del evento?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                evento.removerAsistente(asistenteSeleccionado.getId());
                gestorEventos.guardarDatos();
                cargarAsistentes();
                
                JOptionPane.showMessageDialog(this, 
                    "Asistente eliminado del evento", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
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
    }
    
    /**
     * Valida los campos del formulario de asistente
     * @return true si todos los campos son válidos
     */
    private boolean validarCamposAsistente() {
        boolean valido = true;
        
        String nombre = txtNombreAsistente.getText().trim();
        String email = txtEmailAsistente.getText().trim();
        String telefono = txtTelefonoAsistente.getText().trim();
        
        if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
            valido = false;
        }
        
        // Validación básica de email
        if (!email.isEmpty() && !email.contains("@")) {
            valido = false;
        }
        
        btnAgregarAsistente.setEnabled(valido);
        return valido;
    }
    
    /**
     * Limpia los campos del formulario de asistente
     */
    private void limpiarCamposAsistente() {
        txtNombreAsistente.setText("");
        txtEmailAsistente.setText("");
        txtTelefonoAsistente.setText("");
        txtNombreAsistente.requestFocus();
    }
} 