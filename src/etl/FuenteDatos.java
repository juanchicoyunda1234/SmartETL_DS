package etl;

import model.Aerolinea;
import model.Aeropuerto;
import model.Registro;
import model.Ruta;

public final class FuenteDatos {

    public static final FuenteDatos AEROPUERTOS = new FuenteDatos(
            "Aeropuertos", "data/airports.csv", Aeropuerto.CAMPOS,
            new String[]{"ID", "Nombre", "Ciudad", "País", "IATA", "ICAO", "Latitud", "Longitud",
                    "Altitud (ft)", "Zona UTC", "DST", "Zona horaria", "Tipo", "Fuente"},
            Aeropuerto::desdeCampos, 4);

    public static final FuenteDatos AEROLINEAS = new FuenteDatos(
            "Aerolíneas", "data/airlines.csv", Aerolinea.CAMPOS,
            new String[]{"ID", "Nombre", "Alias", "IATA", "ICAO", "Callsign", "País", "Activa"},
            Aerolinea::desdeCampos, 1);

    public static final FuenteDatos RUTAS = new FuenteDatos(
            "Rutas", "data/routes.csv", Ruta.CAMPOS,
            new String[]{"Aerolínea", "ID aerolínea", "Origen", "ID origen", "Destino", "ID destino",
                    "Codeshare", "Escalas", "Equipo"},
            Ruta::desdeCampos, 2);

    private final String nombre;
    private final String archivoPorDefecto;
    private final int camposEsperados;
    private final String[] encabezados;
    private final Mapeador<Registro> mapeador;
    private final int columnaBusquedaPorDefecto;

    private FuenteDatos(String nombre, String archivoPorDefecto, int camposEsperados, String[] encabezados,
                        Mapeador<Registro> mapeador, int columnaBusquedaPorDefecto) {
        this.nombre = nombre;
        this.archivoPorDefecto = archivoPorDefecto;
        this.camposEsperados = camposEsperados;
        this.encabezados = encabezados;
        this.mapeador = mapeador;
        this.columnaBusquedaPorDefecto = columnaBusquedaPorDefecto;
    }

    public static FuenteDatos[] todas() {
        return new FuenteDatos[]{AEROPUERTOS, AEROLINEAS, RUTAS};
    }

    public String getNombre() {
        return nombre;
    }

    public String getArchivoPorDefecto() {
        return archivoPorDefecto;
    }

    public int getCamposEsperados() {
        return camposEsperados;
    }

    public String[] getEncabezados() {
        return encabezados;
    }

    public Mapeador<Registro> getMapeador() {
        return mapeador;
    }

    public int getColumnaBusquedaPorDefecto() {
        return columnaBusquedaPorDefecto;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
