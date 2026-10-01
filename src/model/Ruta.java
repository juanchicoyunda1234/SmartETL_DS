package model;

public class Ruta implements Registro {

    public static final int CAMPOS = 9;

    private final String aerolinea;
    private final String idAerolinea;
    private final String origen;
    private final String idOrigen;
    private final String destino;
    private final String idDestino;
    private final String codeshare;
    private final String escalas;
    private final String equipo;

    public Ruta(String aerolinea, String idAerolinea, String origen, String idOrigen, String destino,
                String idDestino, String codeshare, String escalas, String equipo) {
        this.aerolinea = aerolinea;
        this.idAerolinea = idAerolinea;
        this.origen = origen;
        this.idOrigen = idOrigen;
        this.destino = destino;
        this.idDestino = idDestino;
        this.codeshare = codeshare;
        this.escalas = escalas;
        this.equipo = equipo;
    }

    public static Ruta desdeCampos(String[] c) {
        if (c.length != CAMPOS) {
            throw new IllegalArgumentException("Una ruta requiere " + CAMPOS + " campos y llegaron " + c.length);
        }
        return new Ruta(c[0], c[1], c[2], c[3], c[4], c[5], c[6], c[7], c[8]);
    }

    public String getAerolinea() {
        return aerolinea;
    }

    public String getIdAerolinea() {
        return idAerolinea;
    }

    public String getOrigen() {
        return origen;
    }

    public String getIdOrigen() {
        return idOrigen;
    }

    public String getDestino() {
        return destino;
    }

    public String getIdDestino() {
        return idDestino;
    }

    public String getCodeshare() {
        return codeshare;
    }

    public String getEscalas() {
        return escalas;
    }

    public String getEquipo() {
        return equipo;
    }

    @Override
    public int numeroCampos() {
        return CAMPOS;
    }

    @Override
    public String valor(int columna) {
        switch (columna) {
            case 0: return aerolinea;
            case 1: return idAerolinea;
            case 2: return origen;
            case 3: return idOrigen;
            case 4: return destino;
            case 5: return idDestino;
            case 6: return codeshare;
            case 7: return escalas;
            case 8: return equipo;
            default: throw new IndexOutOfBoundsException("Columna " + columna + " no existe en Ruta");
        }
    }

    @Override
    public String toString() {
        return String.format("Ruta[%s: %s (%s) -> %s (%s)]", aerolinea, origen, idOrigen, destino, idDestino);
    }
}
