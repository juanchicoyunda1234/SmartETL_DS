package etl;

public enum ModoCarga {

    TODOS("Todos"),
    PRIMEROS("Primeros N"),
    RANGO("Rango"),
    ALEATORIO("Aleatorio N");

    private final String etiqueta;

    ModoCarga(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}