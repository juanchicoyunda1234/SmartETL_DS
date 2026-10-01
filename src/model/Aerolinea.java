package model;

public class Aerolinea implements Registro {

    public static final int CAMPOS = 8;

    private final String id;
    private final String nombre;
    private final String alias;
    private final String iata;
    private final String icao;
    private final String callsign;
    private final String pais;
    private final String activa;

    public Aerolinea(String id, String nombre, String alias, String iata, String icao,
                     String callsign, String pais, String activa) {
        this.id = id;
        this.nombre = nombre;
        this.alias = alias;
        this.iata = iata;
        this.icao = icao;
        this.callsign = callsign;
        this.pais = pais;
        this.activa = activa;
    }

    public static Aerolinea desdeCampos(String[] c) {
        if (c.length != CAMPOS) {
            throw new IllegalArgumentException("Una aerolinea requiere " + CAMPOS + " campos y llegaron " + c.length);
        }
        return new Aerolinea(c[0], c[1], c[2], c[3], c[4], c[5], c[6], c[7]);
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getAlias() {
        return alias;
    }

    public String getIata() {
        return iata;
    }

    public String getIcao() {
        return icao;
    }

    public String getCallsign() {
        return callsign;
    }

    public String getPais() {
        return pais;
    }

    public String getActiva() {
        return activa;
    }

    @Override
    public int numeroCampos() {
        return CAMPOS;
    }

    @Override
    public String valor(int columna) {
        switch (columna) {
            case 0: return id;
            case 1: return nombre;
            case 2: return alias;
            case 3: return iata;
            case 4: return icao;
            case 5: return callsign;
            case 6: return pais;
            case 7: return activa;
            default: throw new IndexOutOfBoundsException("Columna " + columna + " no existe en Aerolinea");
        }
    }

    @Override
    public String toString() {
        return String.format("Aerolinea[%s, %s, %s, activa=%s]", id, nombre, pais, activa);
    }
}
