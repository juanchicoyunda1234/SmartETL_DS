 package app;

import estructuras.Lista;
import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import estructuras.ResultadoBusqueda;
import etl.Extract;
import etl.FuenteDatos;
import etl.ModoCarga;
import etl.OpcionesCarga;
import etl.ResultadoExtraccion;
import model.Registro;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class VentanaPrincipal extends JFrame {

    private static final Color FONDO_NULO = new Color(255, 228, 228);
    private static final Color TEXTO_NULO = new Color(160, 30, 30);
    private static final Color FONDO_VACIO = new Color(255, 246, 204);

    private final JComboBox<FuenteDatos> cmbFuente = new JComboBox<>(FuenteDatos.todas());
    private final JTextField txtArchivo = new JTextField(FuenteDatos.AEROPUERTOS.getArchivoPorDefecto(), 24);
    private final JButton btnExaminar = new JButton("Examinar...");
    private final JComboBox<String> cmbLista = new JComboBox<>(
            new String[]{"Lista enlazada (head y tail)", "Lista secuencial (arreglo)"});
    private final JComboBox<ModoCarga> cmbModo = new JComboBox<>(ModoCarga.values());
    private final JTextField txtCantidad = new JTextField("10", 5);
    private final JTextField txtDesde = new JTextField("1", 5);
    private final JTextField txtHasta = new JTextField("10", 5);
    private final JTextField txtSemilla = new JTextField("42", 5);
    private final JButton btnCargar = new JButton("Cargar");
    private final JComboBox<String> cmbColumna = new JComboBox<>();
    private final JTextField txtBusqueda = new JTextField(16);
    private final JButton btnBuscar = new JButton("Buscar (primera coincidencia)");
    private final JButton btnFiltrar = new JButton("Filtrar (contiene)");
    private final JButton btnVerTodos = new JButton("Ver todos");
    private final JLabel lblEstado = new JLabel(" ");
    private final JLabel lblConteo = new JLabel(" ");
    private final JLabel lblConsulta = new JLabel(" ");
    private final JLabel lblLeyenda = new JLabel(" ");
    private final ModeloTablaRegistros modelo = new ModeloTablaRegistros();
    private final JTable tabla = new JTable(modelo);

    private Lista<Registro> cargada = new ListaEnlazada<>();
    private Lista<Registro> mostrada = cargada;
    private FuenteDatos fuenteActual = FuenteDatos.AEROPUERTOS;

    public VentanaPrincipal() {
        super("SmartETL-DS  |  Hito 1: Extract y listas propias");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 6));
        add(construirPanelSuperior(), BorderLayout.NORTH);
        add(construirTabla(), BorderLayout.CENTER);
        add(construirPanelInferior(), BorderLayout.SOUTH);
        conectarEventos();
        modelo.actualizar(mostrada, fuenteActual.getEncabezados());
        ajustarColumnas();
        refrescarColumnasBusqueda();
        actualizarCamposModo();
        lblEstado.setText("Aún no hay datos cargados. Elige el modo de carga y pulsa Cargar.");
        lblConsulta.setText("Las consultas recorren la lista nodo por nodo y cuentan las comparaciones.");
        lblLeyenda.setText("Celdas: rojo = \\N (nulo en el origen), amarillo = campo vacío. Los datos se muestran tal como llegan del archivo.");
        setSize(1300, 680);
        setMinimumSize(new Dimension(1150, 560));
        setLocationRelativeTo(null);
    }

    private JPanel construirPanelSuperior() {
        JPanel origen = nuevaFila("1. Origen de datos");
        origen.add(new JLabel("Datos:"));
        origen.add(cmbFuente);
        origen.add(new JLabel("Archivo:"));
        origen.add(txtArchivo);
        origen.add(btnExaminar);

        JPanel carga = nuevaFila("2. Carga (Extract)");
        carga.add(new JLabel("Guardar en:"));
        carga.add(cmbLista);
        carga.add(new JLabel("Modo:"));
        carga.add(cmbModo);
        carga.add(new JLabel("N:"));
        carga.add(txtCantidad);
        carga.add(new JLabel("Desde:"));
        carga.add(txtDesde);
        carga.add(new JLabel("Hasta:"));
        carga.add(txtHasta);
        carga.add(new JLabel("Semilla:"));
        carga.add(txtSemilla);
        carga.add(btnCargar);

        JPanel consulta = nuevaFila("3. Consulta sobre la lista");
        consulta.add(new JLabel("Campo:"));
        consulta.add(cmbColumna);
        consulta.add(new JLabel("Valor:"));
        consulta.add(txtBusqueda);
        consulta.add(btnBuscar);
        consulta.add(btnFiltrar);
        consulta.add(btnVerTodos);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(origen);
        panel.add(carga);
        panel.add(consulta);
        return panel;
    }

    private JPanel nuevaFila(String titulo) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fila.setBorder(BorderFactory.createTitledBorder(titulo));
        return fila;
    }

    private JScrollPane construirTabla() {
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setDefaultRenderer(Object.class, new RenderizadorCeldas());
        return new JScrollPane(tabla);
    }

    private JPanel construirPanelInferior() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 0, 2));
        panel.setBorder(BorderFactory.createEmptyBorder(2, 10, 6, 10));
        panel.add(lblEstado);
        panel.add(lblConteo);
        panel.add(lblConsulta);
        panel.add(lblLeyenda);
        return panel;
    }

    private void conectarEventos() {
        cmbFuente.addActionListener(e -> {
            FuenteDatos fuente = (FuenteDatos) cmbFuente.getSelectedItem();
            txtArchivo.setText(fuente.getArchivoPorDefecto());
        });
        cmbModo.addActionListener(e -> actualizarCamposModo());
        btnExaminar.addActionListener(e -> examinar());
        btnCargar.addActionListener(e -> cargar());
        btnBuscar.addActionListener(e -> buscar());
        txtBusqueda.addActionListener(e -> buscar());
        btnFiltrar.addActionListener(e -> filtrar());
        btnVerTodos.addActionListener(e -> verTodos());
    }

    private void actualizarCamposModo() {
        ModoCarga modo = (ModoCarga) cmbModo.getSelectedItem();
        txtCantidad.setEnabled(modo == ModoCarga.PRIMEROS || modo == ModoCarga.ALEATORIO);
        txtDesde.setEnabled(modo == ModoCarga.RANGO);
        txtHasta.setEnabled(modo == ModoCarga.RANGO);
        txtSemilla.setEnabled(modo == ModoCarga.ALEATORIO);
    }

    private void examinar() {
        File carpeta = new File("data");
        JFileChooser selector = new JFileChooser(carpeta.exists() ? carpeta : new File("."));
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtArchivo.setText(selector.getSelectedFile().getPath());
        }
    }

    private void cargar() {
        FuenteDatos fuente = (FuenteDatos) cmbFuente.getSelectedItem();
        OpcionesCarga opciones;
        try {
            opciones = leerOpciones();
        } catch (IllegalArgumentException ex) {
            mostrarError("Opciones de carga no válidas", ex.getMessage());
            return;
        }
        Lista<Registro> destino = cmbLista.getSelectedIndex() == 0 ? new ListaEnlazada<>() : new ListaSecuencial<>();
        String ruta = txtArchivo.getText().trim();
        try {
            ResultadoExtraccion<Registro> resultado = Extract.extraer(
                    ruta, fuente.getCamposEsperados(), fuente.getMapeador(), destino, opciones);
            cargada = destino;
            mostrada = destino;
            fuenteActual = fuente;
            modelo.actualizar(mostrada, fuente.getEncabezados());
            ajustarColumnas();
            refrescarColumnasBusqueda();
            tabla.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
            lblEstado.setText(String.format(
                    "Archivo: %s  |  Modo: %s  |  Estructura: %s  |  Extract: %.1f ms",
                    Paths.get(ruta).getFileName(), opciones.resumen(), destino.descripcion(),
                    resultado.getMilisegundos()));
            lblConteo.setText(String.format(
                    "Líneas en el archivo: %d  |  Seleccionadas: %d  |  Cargadas en la lista: %d  |  No leídas: %d",
                    resultado.getTotalLineas(), resultado.getLineasSeleccionadas(), resultado.getCargados(),
                    resultado.getNoLeidas()));
            lblConsulta.setText("Datos cargados. Prueba una búsqueda: el programa recorre la lista y cuenta las comparaciones.");
        } catch (IOException ex) {
            mostrarError("No se pudo leer el archivo", ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }
    }

    private OpcionesCarga leerOpciones() {
        ModoCarga modo = (ModoCarga) cmbModo.getSelectedItem();
        switch (modo) {
            case PRIMEROS:
                return OpcionesCarga.primeros(entero(txtCantidad, "N"));
            case RANGO:
                return OpcionesCarga.rango(entero(txtDesde, "Desde"), entero(txtHasta, "Hasta"));
            case ALEATORIO:
                String texto = txtSemilla.getText().trim();
                Long semilla = null;
                if (!texto.isEmpty()) {
                    try {
                        semilla = Long.parseLong(texto);
                    } catch (NumberFormatException ex) {
                        throw new IllegalArgumentException("La semilla debe ser un número entero o quedar vacía");
                    }
                }
                return OpcionesCarga.aleatorio(entero(txtCantidad, "N"), semilla);
            default:
                return OpcionesCarga.todos();
        }
    }

    private int entero(JTextField campo, String nombre) {
        try {
            return Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El campo " + nombre + " debe ser un número entero");
        }
    }

    private void refrescarColumnasBusqueda() {
        cmbColumna.removeAllItems();
        for (String encabezado : fuenteActual.getEncabezados()) {
            cmbColumna.addItem(encabezado);
        }
        cmbColumna.setSelectedIndex(fuenteActual.getColumnaBusquedaPorDefecto());
    }

    private void ajustarColumnas() {
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            int ancho = 110;
            String encabezado = fuenteActual.getEncabezados()[i];
            if (encabezado.equals("Nombre")) {
                ancho = 260;
            } else if (encabezado.equals("Zona horaria")) {
                ancho = 160;
            } else if (encabezado.equals("País") || encabezado.equals("Ciudad")) {
                ancho = 140;
            }
            tabla.getColumnModel().getColumn(i).setPreferredWidth(ancho);
        }
    }

    private void buscar() {
        if (cargada.estaVacia()) {
            lblConsulta.setText("Primero carga datos.");
            return;
        }
        String texto = txtBusqueda.getText().trim();
        if (texto.isEmpty()) {
            lblConsulta.setText("Escribe el valor que quieres buscar.");
            return;
        }
        int columna = cmbColumna.getSelectedIndex();
        String nombreColumna = fuenteActual.getEncabezados()[columna];
        long inicio = System.nanoTime();
        ResultadoBusqueda<Registro> resultado = mostrada.buscar(r -> r.valor(columna).trim().equalsIgnoreCase(texto));
        double milisegundos = (System.nanoTime() - inicio) / 1_000_000.0;
        if (resultado.encontrado()) {
            int fila = resultado.getIndice();
            tabla.setRowSelectionInterval(fila, fila);
            tabla.scrollRectToVisible(tabla.getCellRect(fila, 0, true));
            lblConsulta.setText(String.format(
                    "Buscar %s = \"%s\": encontrado en la posición %d de %d tras %d comparaciones (%.3f ms)",
                    nombreColumna, texto, fila + 1, mostrada.tamanio(), resultado.getComparaciones(), milisegundos));
        } else {
            tabla.clearSelection();
            lblConsulta.setText(String.format(
                    "Buscar %s = \"%s\": no existe. Se recorrió toda la lista (%d comparaciones, %.3f ms)",
                    nombreColumna, texto, resultado.getComparaciones(), milisegundos));
        }
    }

    private void filtrar() {
        if (cargada.estaVacia()) {
            lblConsulta.setText("Primero carga datos.");
            return;
        }
        String texto = txtBusqueda.getText().trim();
        if (texto.isEmpty()) {
            verTodos();
            return;
        }
        int columna = cmbColumna.getSelectedIndex();
        String nombreColumna = fuenteActual.getEncabezados()[columna];
        String minuscula = texto.toLowerCase();
        long inicio = System.nanoTime();
        Lista<Registro> coincidencias = cargada.filtrar(r -> r.valor(columna).toLowerCase().contains(minuscula));
        double milisegundos = (System.nanoTime() - inicio) / 1_000_000.0;
        mostrada = coincidencias;
        modelo.actualizarDatos(mostrada);
        tabla.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
        lblConsulta.setText(String.format(
                "Filtro %s contiene \"%s\": %d coincidencias de %d registros (%d comparaciones, %.3f ms). Pulsa Ver todos para volver.",
                nombreColumna, texto, coincidencias.tamanio(), cargada.tamanio(), cargada.tamanio(), milisegundos));
    }

    private void verTodos() {
        mostrada = cargada;
        modelo.actualizarDatos(mostrada);
        tabla.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
        lblConsulta.setText("Mostrando todos los registros cargados: " + cargada.tamanio());
    }

    private void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }

    private static class RenderizadorCeldas extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                       boolean foco, int fila, int columna) {
            Component celda = super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
            if (!seleccionada) {
                String texto = valor == null ? "" : valor.toString();
                if (texto.equals("\\N")) {
                    celda.setBackground(FONDO_NULO);
                    celda.setForeground(TEXTO_NULO);
                } else if (texto.isEmpty()) {
                    celda.setBackground(FONDO_VACIO);
                    celda.setForeground(Color.BLACK);
                } else {
                    celda.setBackground(Color.WHITE);
                    celda.setForeground(Color.BLACK);
                }
            }
            return celda;
        }
    }
}


