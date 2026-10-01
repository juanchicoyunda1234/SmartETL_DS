package model;

public class Aeropuerto implements Registro {

    public static final int CAMPOS = 14;

    private final String id;
    private final String nombre;
    private final String ciudad;
    private final String pais;
    private final String iata;
    private final String icao;
    private final String latitud;
    private final String longitud;
    private final String altitud;
    private final String zonaUtc;
    private final String dst;
    private final String zonaHoraria;
    private final String tipo;
    private final String fuente;

    public Aeropuerto(String id, String nombre, String ciudad, String pais, String iata, String icao,
                      String latitud, String longitud, String altitud, String zonaUtc, String dst,
                      String zonaHoraria, String tipo, String fuente) {
        this.id = id;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.pais = pais;
        this.iata = iata;
        this.icao = icao;
        this.latitud = latitud;
        this.longitud = longitud;
        this.altitud = altitud;
        this.zonaUtc = zonaUtc;
        this.dst = dst;
        this.zonaHoraria = zonaHoraria;
        this.tipo = tipo;
        this.fuente = fuente;
    }

    public static Aeropuerto desdeCampos(String[] c) {
        if (c.length != CAMPOS) {
            throw new IllegalArgumentException("Un aeropuerto requiere " + CAMPOS + " campos y llegaron " + c.length);
        }
        return new Aeropuerto(c[0], c[1], c[2], c[3], c[4], c[5], c[6], c[7], c[8], c[9], c[10], c[11], c[12], c[13]);
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getPais() {
        return pais;
    }

    public String getIata() {
        return iata;
    }

    public String getIcao() {
        return icao;
    }

    public String getLatitud() {
        return latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public String getAltitud() {
        return altitud;
    }

    public String getZonaUtc() {
        return zonaUtc;
    }

    public String getDst() {
        return dst;
    }

    public String getZonaHoraria() {
        return zonaHoraria;
    }

    public String getTipo() {
        return tipo;
    }

    public String getFuente() {
        return fuente;
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
            case 2: return ciudad;
            case 3: return pais;
            case 4: return iata;
            case 5: return icao;
            case 6: return latitud;
            case 7: return longitud;
            case 8: return altitud;
            case 9: return zonaUtc;
            case 10: return dst;
            case 11: return zonaHoraria;
            case 12: return tipo;
            case 13: return fuente;
            default: throw new IndexOutOfBoundsException("Columna " + columna + " no existe en Aeropuerto");
        }
    }

    @Override
    public String toString() {
        return String.format("Aeropuerto[%s, %s, %s, %s, IATA=%s]", id, nombre, ciudad, pais, iata);
    }
}
