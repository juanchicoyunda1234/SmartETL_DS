package estructuras;

public interface Lista<T> {

    void insertarInicio(T dato);

    void insertarFinal(T dato);

    T obtener(int indice);

    int tamanio();

    boolean estaVacia();

    void limpiar();

    ResultadoBusqueda<T> buscar(Criterio<T> criterio);

    Lista<T> filtrar(Criterio<T> criterio);

    int contar(Criterio<T> criterio);

    String descripcion();
}
