package app;

import estructuras.Lista;
import estructuras.ListaEnlazada;
import model.Registro;

import javax.swing.table.AbstractTableModel;

public class ModeloTablaRegistros extends AbstractTableModel {

    private Lista<Registro> lista = new ListaEnlazada<>();
    private String[] encabezados = new String[0];

    public void actualizar(Lista<Registro> lista, String[] encabezados) {
        this.lista = lista;
        this.encabezados = encabezados;
        fireTableStructureChanged();
    }

    public void actualizarDatos(Lista<Registro> lista) {
        this.lista = lista;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return lista.tamanio();
    }

    @Override
    public int getColumnCount() {
        return encabezados.length;
    }

    @Override
    public String getColumnName(int columna) {
        return encabezados[columna];
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        return lista.obtener(fila).valor(columna);
    }
}