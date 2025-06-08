package gui;

import excepciones.EventoException;
import modelo.Evento;
import servicio.GestorEventos;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    
    private GestorEventos gestorEventos;
    private JTable tablaEventos;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnVerDetalles;
    private JRadioButton rbEventosFuturos;
    private JRadioButton rbEventosPasados;
    private JRadioButton rbTodosEventos;
    private ButtonGroup grupoFiltros;
    
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public VentanaPrincipal() {
        this.gestorEventos = new GestorEventos();
        inicializarComponentes();
        configurarLayout();
        configurarEventos();
        actualizarTablaEventos();
        
        // Configurar ventana
        setTitle("Sistema de Gestión de Eventos");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(600, 400));
    }
    
    private void inicializarComponentes() {
        // Crear tabla
        String[] columnas = {"ID", "Nombre", "Fecha", "Ubicación", "Asistentes"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }
        };
        tablaEventos = new JTable(modeloTabla);
        tablaEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEventos.getTableHeader().setReorderingAllowed(false);
        
        // Configurar ancho de columnas
        tablaEventos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaEventos.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaEventos.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaEventos.getColumnModel().getColumn(3).setPreferredWidth(150);
        tablaEventos.getColumnModel().getColumn(4).setPreferredWidth(80);
        
        // Crear botones
        btnAgregar = new JButton("Agregar Evento");
        btnEditar = new JButton("Editar Evento");
        btnEliminar = new JButton("Eliminar Evento");
        btnVerDetalles = new JButton("Ver Detalles");
        
        // Crear radio buttons para filtros
        rbEventosFuturos = new JRadioButton("Eventos Futuros", true);
        rbEventosPasados = new JRadioButton("Eventos Pasados");
        rbTodosEventos = new JRadioButton("Todos los Eventos");
        
        grupoFiltros = new ButtonGroup();
        grupoFiltros.add(rbEventosFuturos);
        grupoFiltros.add(rbEventosPasados);
        grupoFiltros.add(rbTodosEventos);
    }
    
    private void configurarLayout() {
        setLayout(new BorderLayout());
        
        // Panel superior con filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        panelFiltros.add(rbEventosFuturos);
        panelFiltros.add(rbEventosPasados);
        panelFiltros.add(rbTodosEventos);
        
        // Panel central con tabla
        JScrollPane scrollTabla = new JScrollPane(tablaEventos);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Lista de Eventos"));
        
        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnVerDetalles);
        
        // Agregar paneles a la ventana
        add(panelFiltros, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void configurarEventos() {
        // Eventos de radio buttons
        rbEventosFuturos.addActionListener(e -> filtrarEventos());
        rbEventosPasados.addActionListener(e -> filtrarEventos());
        rbTodosEventos.addActionListener(e -> filtrarEventos());
        
        // Eventos de botones
        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDialogoEvento(null);
            }
        });
        
        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarEventoSeleccionado();
            }
        });
        
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarEventoSeleccionado();
            }
        });
        
        btnVerDetalles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDetallesEvento();
            }
        });
        
        // Evento de cierre de ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarAplicacion();
            }
        });
        
        // Doble clic en tabla para ver detalles
        tablaEventos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    mostrarDetallesEvento();
                }
            }
        });
        
        // Listener para cambios de selección en la tabla
        tablaEventos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBotones();
            }
        });
    }

    private void actualizarTablaEventos() {
        modeloTabla.setRowCount(0);
        
        List<Evento> eventos;
        if (rbEventosFuturos.isSelected()) {
            eventos = gestorEventos.listarEventosFuturos();
        } else if (rbEventosPasados.isSelected()) {
            eventos = gestorEventos.listarEventosPasados();
        } else {
            eventos = gestorEventos.listarTodosLosEventos();
        }
        
        for (Evento evento : eventos) {
            Object[] fila = {
                evento.getId(),
                evento.getNombre(),
                evento.getFecha().format(FORMATO_FECHA),
                evento.getUbicacion(),
                evento.getCantidadAsistentes()
            };
            modeloTabla.addRow(fila);
        }
        
        // Actualizar estado de botones
        actualizarEstadoBotones();
    }
    
    private void filtrarEventos() {
        actualizarTablaEventos();
    }
    
    private void actualizarEstadoBotones() {
        boolean haySeleccion = tablaEventos.getSelectedRow() != -1;
        btnEditar.setEnabled(haySeleccion);
        btnEliminar.setEnabled(haySeleccion);
        btnVerDetalles.setEnabled(haySeleccion);
    }
    
    private void mostrarDialogoEvento(Evento evento) {
        DialogoEvento dialogo = new DialogoEvento(this, evento, gestorEventos);
        dialogo.setVisible(true);
        // Actualizar tabla después de cerrar el diálogo
        actualizarTablaEventos();
    }
    
    private void editarEventoSeleccionado() {
        int filaSeleccionada = tablaEventos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un evento para editar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
        gestorEventos.buscarEventoPorId(id).ifPresent(evento -> {
            if (evento.esPasado()) {
                JOptionPane.showMessageDialog(this, 
                    "No se puede editar un evento que ya pasó", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            } else {
                mostrarDialogoEvento(evento);
            }
        });
    }
    
    private void eliminarEventoSeleccionado() {
        int filaSeleccionada = tablaEventos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un evento para eliminar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar este evento?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
                gestorEventos.eliminarEvento(id);
                actualizarTablaEventos();
                JOptionPane.showMessageDialog(this, 
                    "Evento eliminado correctamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (EventoException e) {
                JOptionPane.showMessageDialog(this, 
                    e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void mostrarDetallesEvento() {
        int filaSeleccionada = tablaEventos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un evento para ver detalles", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
        gestorEventos.buscarEventoPorId(id).ifPresent(evento -> {
            DialogoDetallesEvento dialogo = new DialogoDetallesEvento(this, evento, gestorEventos);
            dialogo.setVisible(true);
            actualizarTablaEventos(); // Actualizar por si cambió algo
        });
    }
    
    private void cerrarAplicacion() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Desea salir de la aplicación?", 
            "Confirmar salida", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                gestorEventos.guardarDatos();
                dispose();
                System.exit(0);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al guardar datos: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 
