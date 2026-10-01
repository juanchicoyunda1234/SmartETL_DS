package etl;

public interface Mapeador<T> {

    T crear(String[] campos);
}