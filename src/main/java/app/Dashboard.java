package app;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class Dashboard extends JFrame {
    private JTable tablaEmergencias;
    private JTable tablaRecursos;
    private JTable tablaDespachadores;
    private JTextArea logArea;
    private JLabel lblTotalEmergencias;
    private JLabel lblAtendidas;
    private JLabel lblRecursosLibres;

    Font tituloFont = new Font("SansSerif", Font.BOLD, 16);

    public Dashboard() {
        setTitle("Tablero de Emergencias");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // KPIs
        JPanel kpiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblTotalEmergencias = new JLabel("Emergencias recibidas: 0");
        lblAtendidas = new JLabel("Atendidas: 0");
        lblRecursosLibres = new JLabel("Recursos libres: 0");
        kpiPanel.add(lblTotalEmergencias);
        kpiPanel.add(lblAtendidas);
        kpiPanel.add(lblRecursosLibres);
        add(kpiPanel, BorderLayout.NORTH);

        // Tablas
        tablaEmergencias = new JTable(new DefaultTableModel(new Object[]{"ID", "Gravedad", "Distancia", "Espera", "Puntaje", "Estado"}, 0));
        tablaRecursos = new JTable(new DefaultTableModel(new Object[]{"ID", "Tipo", "Estado", "Emergencia"}, 0));
        tablaDespachadores = new JTable(new DefaultTableModel(new Object[]{"Nombre", "Emergencia", "Recurso"}, 0));

        JScrollPane emergScroll = new JScrollPane(tablaEmergencias);
        TitledBorder emergBorder = BorderFactory.createTitledBorder("Emergencias");
        emergBorder.setTitleFont(tituloFont);
        emergScroll.setBorder(emergBorder);

        JScrollPane recScroll = new JScrollPane(tablaRecursos);
        JComboBox<String> filtroEstado = new JComboBox<>(new String[]{"Todos", "Libre", "Ocupado"});
        filtroEstado.addActionListener(e -> {
            String estado = (String) filtroEstado.getSelectedItem();
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(tablaRecursos.getModel());
            if ("Todos".equals(estado)) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(estado, 2)); // columna "Estado"
            }
            tablaRecursos.setRowSorter(sorter);
        });
        JPanel recursosPanel = new JPanel(new BorderLayout());
        recursosPanel.add(filtroEstado, BorderLayout.NORTH);
        recursosPanel.add(recScroll, BorderLayout.CENTER);

        TitledBorder recBorder = BorderFactory.createTitledBorder("Recursos");
        recBorder.setTitleFont(tituloFont);
        recursosPanel.setBorder(recBorder);

        // SplitPane horizontal para las tablas de emergencias y recursos
        JSplitPane splitCentro = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, emergScroll, recursosPanel);
        splitCentro.setDividerLocation(600);
        splitCentro.setResizeWeight(0.6);

        // ScrollPane para la tabla de despachadores
        JScrollPane despScroll = new JScrollPane(tablaDespachadores);
        despScroll.setPreferredSize(new Dimension(1100, 150)); // Altura fija para despachadores
        TitledBorder despBorder = BorderFactory.createTitledBorder("Despachadores");
        despBorder.setTitleFont(tituloFont);
        despScroll.setBorder(despBorder);

        // SplitPane VERTICAL para dividir el espacio entre las tablas superiores y despachadores
        JSplitPane splitVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitCentro, despScroll);
        splitVertical.setDividerLocation(300);
        splitVertical.setResizeWeight(0.7);
        add(splitVertical, BorderLayout.CENTER);

        // Log de eventos
        logArea = new JTextArea(8,80);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(1100,150));
        add(logScroll, BorderLayout.SOUTH);
    }

    public void mostrar() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    // Métodos para actualizar tablas y KPIs
    public void agregarEmergencia(Object[] fila) {
        ((DefaultTableModel) tablaEmergencias.getModel()).addRow(fila);
    }

    public void agregarRecurso(Object[] fila) {
        ((DefaultTableModel) tablaRecursos.getModel()).addRow(fila);
    }

    public void agregarDespachador(Object[] fila) {
        ((DefaultTableModel) tablaDespachadores.getModel()).addRow(fila);
    }

    public void log(String mensaje) {
        logArea.append(mensaje + "\n");
    }

    public void actualizarKPIs(int total, int atendidas, int libres) {
        lblTotalEmergencias.setText("Emergencias recibidas: " + total);
        lblAtendidas.setText("Atendidas: " + atendidas);
        lblRecursosLibres.setText("Recursos libres: " + libres);
    }

    // Actualiza el estado de una emergencia
    public void actualizarEstadoEmergencia(long id, String nuevoEstado) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) tablaEmergencias.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                long actualId = Long.parseLong(model.getValueAt(i, 0).toString());
                if (actualId == id) {
                    model.setValueAt(nuevoEstado, i, 5); // columna "Estado"
                    model.fireTableRowsUpdated(i, i);
                    break;
                }
            }
        });
    }

    // Actualiza el tiempo de espera de una emergencia
    public void actualizarEspera(long id, String espera) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) tablaEmergencias.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                long actualId = Long.parseLong(model.getValueAt(i, 0).toString());
                if (actualId == id) {
                    model.setValueAt(espera, i, 3); // Columna 3 es "Espera"
                    model.fireTableRowsUpdated(i, i);
                    break;
                }
            }
        });
    }

}