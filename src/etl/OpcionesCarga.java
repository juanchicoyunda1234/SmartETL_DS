package etl;

public final class OpcionesCarga {

    private final ModoCarga modo;
    private final int cantidad;
    private final int desde;
    private final int hasta;
    private final Long semilla;

    private OpcionesCarga(ModoCarga modo, int cantidad, int desde, int hasta, Long semilla) {
        this.modo = modo;
        this.cantidad = cantidad;
        this.desde = desde;
        this.hasta = hasta;
        this.semilla = semilla;
    }

    public static OpcionesCarga todos() {
        return new OpcionesCarga(ModoCarga.TODOS, 0, 0, 0, null);
    }

    public static OpcionesCarga primeros(int cantidad) {
        if (cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser al menos 1");
        }
        return new OpcionesCarga(ModoCarga.PRIMEROS, cantidad, 0, 0, null);
    }

    public static OpcionesCarga rango(int desde, int hasta) {
        if (desde < 1) {
            throw new IllegalArgumentException("El inicio del rango debe ser al menos 1");
        }
        if (hasta < desde) {
            throw new IllegalArgumentException("El final del rango no puede ser menor que el inicio");
        }
        return new OpcionesCarga(ModoCarga.RANGO, 0, desde, hasta, null);
    }

    public static OpcionesCarga aleatorio(int cantidad, Long semilla) {
        if (cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser al menos 1");
        }
        return new OpcionesCarga(ModoCarga.ALEATORIO, cantidad, 0, 0, semilla);
    }

    public ModoCarga getModo() {
        return modo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getDesde() {
        return desde;
    }

    public int getHasta() {
        return hasta;
    }

    public Long getSemilla() {
        return semilla;
    }

    public String resumen() {
        switch (modo) {
            case PRIMEROS:
                return "primeros " + cantidad;
            case RANGO:
                return "filas " + desde + " a " + hasta;
            case ALEATORIO:
                return "aleatorio " + cantidad + (semilla == null ? " (sin semilla)" : " (semilla " + semilla + ")");
            default:
                return "todos";
        }
    }
}
