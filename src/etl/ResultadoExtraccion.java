package etl;

import estructuras.Lista;

public class ResultadoExtraccion<T> {

    private final Lista<T> lista;
    private final String ruta;
    private final int totalLineas;
    private final int lineasSeleccionadas;
    private final int cargados;
    private final int noLeidas;
    private final double milisegundos;

    public ResultadoExtraccion(Lista<T> lista, String ruta, int totalLineas, int lineasSeleccionadas,
                               int cargados, int noLeidas, double milisegundos) {
        this.lista = lista;
        this.ruta = ruta;
        this.totalLineas = totalLineas;
        this.lineasSeleccionadas = lineasSeleccionadas;
        this.cargados = cargados;
        this.noLeidas = noLeidas;
        this.milisegundos = milisegundos;
    }

    public Lista<T> getLista() {
        return lista;
    }

    public String getRuta() {
        return ruta;
    }

    public int getTotalLineas() {
        return totalLineas;
    }

    public int getLineasSeleccionadas() {
        return lineasSeleccionadas;
    }

    public int getCargados() {
        return cargados;
    }

    public int getNoLeidas() {
        return noLeidas;
    }

    public double getMilisegundos() {
        return milisegundos;
    }
}
