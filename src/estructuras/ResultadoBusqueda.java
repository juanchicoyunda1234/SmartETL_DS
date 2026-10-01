package estructuras;

public class ResultadoBusqueda<T> {

    private final T elemento;
    private final int indice;
    private final int comparaciones;

    public ResultadoBusqueda(T elemento, int indice, int comparaciones) {
        this.elemento = elemento;
        this.indice = indice;
        this.comparaciones = comparaciones;
    }

    public boolean encontrado() {
        return indice >= 0;
    }

    public T getElemento() {
        return elemento;
    }

    public int getIndice() {
        return indice;
    }

    public int getComparaciones() {
        return comparaciones;
    }
}
