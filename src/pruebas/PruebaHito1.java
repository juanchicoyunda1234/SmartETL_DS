package pruebas;

import estructuras.Lista;
import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import estructuras.ResultadoBusqueda;
import etl.Extract;
import etl.FuenteDatos;
import etl.LectorCSV;
import etl.OpcionesCarga;
import etl.ResultadoExtraccion;
import model.Registro;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class PruebasHito1 {

    private static int pasaron = 0;
    private static int fallaron = 0;

    public static void main(String[] args) throws Exception {
        pruebasLectorCSV();
        pruebasOpcionesCarga();
        pruebasListaEnlazada();
        pruebasListaSecuencial();
        pruebasExtractArchivoTemporal();
        pruebasExtractDatosReales();
        System.out.println();
        System.out.println("Pruebas que pasaron: " + pasaron + " | fallaron: " + fallaron);
        if (fallaron > 0) {
            System.exit(1);
        }
    }

    private static void verificar(String nombre, boolean condicion) {
        if (condicion) {
            pasaron++;
            System.out.println("  OK   " + nombre);
        } else {
            fallaron++;
            System.out.println("  FALLA " + nombre);
        }
    }

    private static void pruebasLectorCSV() {
        System.out.println("LectorCSV");
        String[] a = LectorCSV.separar("1,\"Goroka Airport\",\"Goroka\",\"PNG\"");
        verificar("separa 4 campos y quita comillas", a.length == 4 && a[1].equals("Goroka Airport") && a[3].equals("PNG"));

        String[] b = LectorCSV.separar("a,,c");
        verificar("conserva campo vacio en el medio", b.length == 3 && b[1].isEmpty());

        String[] c = LectorCSV.separar("a,b,");
        verificar("conserva campo vacio al final", c.length == 3 && c[2].isEmpty());
        verificar("split(\",\") de Java pierde ese campo final", "a,b,".split(",").length == 2);

        String[] d = LectorCSV.separar("1,\"Nuuk, Kangerlussuaq\",x");
        verificar("coma dentro de comillas no separa", d.length == 3 && d[1].equals("Nuuk, Kangerlussuaq"));

        String[] e = LectorCSV.separar("\"Dijo \"\"hola\"\"\",z");
        verificar("comillas escapadas", e.length == 2 && e[0].equals("Dijo \"hola\""));

        String[] f = LectorCSV.separar("1,\\N,\\N");
        verificar("el marcador \\N se conserva como texto", f.length == 3 && f[1].equals("\\N"));

        String[] g = LectorCSV.separar("");
        verificar("linea vacia da un campo vacio", g.length == 1 && g[0].isEmpty());
    }

    private static void pruebasOpcionesCarga() {
        System.out.println("OpcionesCarga");
        verificar("rango con inicio mayor que el final se rechaza", lanza(() -> OpcionesCarga.rango(20, 5)));
        verificar("rango con inicio menor que 1 se rechaza", lanza(() -> OpcionesCarga.rango(0, 5)));
        verificar("primeros con cantidad 0 se rechaza", lanza(() -> OpcionesCarga.primeros(0)));
        verificar("aleatorio con cantidad negativa se rechaza", lanza(() -> OpcionesCarga.aleatorio(-3, 42L)));
        verificar("opciones validas se aceptan", !lanza(() -> OpcionesCarga.rango(5, 9))
                && !lanza(() -> OpcionesCarga.aleatorio(10, null)));
    }

    private static boolean lanza(Runnable accion) {
        try {
            accion.run();
            return false;
        } catch (IllegalArgumentException ex) {
            return true;
        }
    }

    private static void pruebasListaEnlazada() {
        System.out.println("ListaEnlazada");
        Lista<Integer> lista = new ListaEnlazada<>();
        verificar("nace vacia", lista.estaVacia() && lista.tamanio() == 0);

        lista.insertarInicio(10);
        lista.insertarFinal(20);
        lista.insertarFinal(30);
        lista.insertarInicio(5);
        verificar("orden tras insertar inicio y final", lista.tamanio() == 4 && lista.obtener(0) == 5
                && lista.obtener(1) == 10 && lista.obtener(2) == 20 && lista.obtener(3) == 30);

        ResultadoBusqueda<Integer> hallado = lista.buscar(x -> x == 20);
        verificar("buscar encuentra en posicion 2 con 3 comparaciones",
                hallado.encontrado() && hallado.getIndice() == 2 && hallado.getComparaciones() == 3);

        ResultadoBusqueda<Integer> ausente = lista.buscar(x -> x == 99);
        verificar("buscar ausente recorre todo", !ausente.encontrado() && ausente.getComparaciones() == 4);

        Lista<Integer> mayores = lista.filtrar(x -> x >= 20);
        verificar("filtrar devuelve lista nueva", mayores.tamanio() == 2 && mayores.obtener(0) == 20);
        verificar("contar coincide con filtrar", lista.contar(x -> x >= 20) == 2);

        boolean lanzo = false;
        try {
            lista.obtener(4);
        } catch (IndexOutOfBoundsException ex) {
            lanzo = true;
        }
        verificar("obtener fuera de rango lanza excepcion", lanzo);

        lista.limpiar();
        verificar("limpiar deja la lista vacia", lista.estaVacia());

        Lista<String> soloInicio = new ListaEnlazada<>();
        soloInicio.insertarInicio("b");
        soloInicio.insertarFinal("c");
        soloInicio.insertarInicio("a");
        verificar("tail correcto tras insertarInicio en lista vacia",
                soloInicio.obtener(0).equals("a") && soloInicio.obtener(1).equals("b") && soloInicio.obtener(2).equals("c"));
    }

    private static void pruebasListaSecuencial() {
        System.out.println("ListaSecuencial");
        ListaSecuencial<Integer> lista = new ListaSecuencial<>();
        for (int i = 0; i < 100; i++) {
            lista.insertarFinal(i);
        }
        verificar("crece automaticamente sin limite fijo", lista.tamanio() == 100 && lista.capacidad() >= 100);
        verificar("conserva el orden al crecer", lista.obtener(0) == 0 && lista.obtener(57) == 57 && lista.obtener(99) == 99);

        lista.insertarInicio(-1);
        verificar("insertarInicio desplaza los demas", lista.tamanio() == 101 && lista.obtener(0) == -1 && lista.obtener(1) == 0
                && lista.obtener(100) == 99);

        ResultadoBusqueda<Integer> hallado = lista.buscar(x -> x == 50);
        verificar("buscar da posicion y comparaciones", hallado.getIndice() == 51 && hallado.getComparaciones() == 52);
        verificar("filtrar y contar", lista.filtrar(x -> x % 2 == 0).tamanio() == lista.contar(x -> x % 2 == 0));

        boolean lanzo = false;
        try {
            lista.obtener(101);
        } catch (IndexOutOfBoundsException ex) {
            lanzo = true;
        }
        verificar("obtener fuera de rango lanza excepcion", lanzo);
    }

    private static void pruebasExtractArchivoTemporal() throws IOException {
        System.out.println("Extract con archivo temporal");
        Path temporal = Files.createTempFile("smartetl", ".csv");
        String contenido = "1,\"Uno\",\\N\n"
                + "\n"
                + "2,\"Dos, con coma\",\n"
                + "3,\"Tres\"\n"
                + "4,\"Cuatro\",x\n";
        Files.write(temporal, contenido.getBytes(StandardCharsets.UTF_8));

        Lista<Registro> destino = new ListaEnlazada<>();
        ResultadoExtraccion<Registro> r = Extract.extraer(temporal.toString(), 3, campos -> new Registro() {
            @Override
            public int numeroCampos() {
                return campos.length;
            }

            @Override
            public String valor(int columna) {
                return campos[columna];
            }
        }, destino, OpcionesCarga.todos());
        verificar("ignora lineas en blanco", r.getTotalLineas() == 4);
        verificar("carga las lineas validas", r.getCargados() == 3);
        verificar("cuenta las lineas con campos de menos", r.getNoLeidas() == 1);
        verificar("conserva el campo vacio final", destino.obtener(1).valor(2).isEmpty());
        Files.delete(temporal);

        boolean lanzo = false;
        try {
            Extract.extraer("data/no_existe.csv", 3, campos -> null, new ListaEnlazada<>(), OpcionesCarga.todos());
        } catch (NoSuchFileException ex) {
            lanzo = true;
        }
        verificar("archivo inexistente lanza NoSuchFileException", lanzo);
    }

    private static void pruebasExtractDatosReales() throws IOException {
        System.out.println("Extract con datos reales de OpenFlights");
        FuenteDatos aeropuertos = FuenteDatos.AEROPUERTOS;

        ResultadoExtraccion<Registro> enlazada = cargar(aeropuertos, new ListaEnlazada<>(), OpcionesCarga.todos());
        verificar("airports: 7698 cargados con ListaEnlazada", enlazada.getCargados() == 7698 && enlazada.getLista().tamanio() == 7698);
        verificar("airports: ninguna linea sin leer", enlazada.getNoLeidas() == 0);

        ResultadoExtraccion<Registro> secuencial = cargar(aeropuertos, new ListaSecuencial<>(), OpcionesCarga.todos());
        verificar("airports: 7698 cargados con ListaSecuencial", secuencial.getCargados() == 7698);

        Lista<Registro> le = enlazada.getLista();
        Lista<Registro> ls = secuencial.getLista();
        boolean iguales = true;
        int[] muestras = {0, 1, 100, 3500, 7697};
        for (int m : muestras) {
            if (!le.obtener(m).valor(0).equals(ls.obtener(m).valor(0))) {
                iguales = false;
            }
        }
        verificar("ambas listas guardan lo mismo en las mismas posiciones", iguales);

        Registro primero = le.obtener(0);
        verificar("primer registro es Goroka (GKA)", primero.valor(0).equals("1") && primero.valor(1).equals("Goroka Airport")
                && primero.valor(4).equals("GKA"));

        verificar("IATA con \\N: 1626", le.contar(r -> r.valor(4).equals("\\N")) == 1626);
        verificar("ciudad vacia: 49", le.contar(r -> r.valor(2).isEmpty()) == 49);

        ResultadoBusqueda<Registro> gka = le.buscar(r -> r.valor(4).equals("GKA"));
        verificar("GKA se encuentra con 1 comparacion", gka.encontrado() && gka.getComparaciones() == 1);

        ResultadoBusqueda<Registro> ultimo = le.buscar(r -> r.valor(0).equals(le.obtener(7697).valor(0)));
        verificar("el ultimo aeropuerto cuesta 7698 comparaciones", ultimo.getIndice() == 7697 && ultimo.getComparaciones() == 7698);

        ResultadoExtraccion<Registro> diez = cargar(aeropuertos, new ListaEnlazada<>(), OpcionesCarga.primeros(10));
        verificar("primeros 10", diez.getCargados() == 10 && diez.getTotalLineas() == 7698);

        ResultadoExtraccion<Registro> rango = cargar(aeropuertos, new ListaEnlazada<>(), OpcionesCarga.rango(5, 9));
        verificar("rango 5 a 9 carga 5 filas", rango.getCargados() == 5);
        boolean mismoRango = true;
        for (int i = 0; i < 5; i++) {
            if (!rango.getLista().obtener(i).valor(0).equals(le.obtener(4 + i).valor(0))) {
                mismoRango = false;
            }
        }
        verificar("el rango coincide con las filas 5 a 9 de la carga completa", mismoRango);

        ResultadoExtraccion<Registro> azarA = cargar(aeropuertos, new ListaEnlazada<>(), OpcionesCarga.aleatorio(10, 42L));
        ResultadoExtraccion<Registro> azarB = cargar(aeropuertos, new ListaSecuencial<>(), OpcionesCarga.aleatorio(10, 42L));
        ResultadoExtraccion<Registro> azarC = cargar(aeropuertos, new ListaEnlazada<>(), OpcionesCarga.aleatorio(10, 43L));
        boolean reproducible = azarA.getCargados() == 10 && azarB.getCargados() == 10;
        boolean distinta = false;
        for (int i = 0; i < 10; i++) {
            if (!azarA.getLista().obtener(i).valor(0).equals(azarB.getLista().obtener(i).valor(0))) {
                reproducible = false;
            }
            if (!azarA.getLista().obtener(i).valor(0).equals(azarC.getLista().obtener(i).valor(0))) {
                distinta = true;
            }
        }
        verificar("aleatorio con la misma semilla repite la muestra", reproducible);
        verificar("aleatorio con otra semilla cambia la muestra", distinta);

        boolean enOrden = true;
        int ultimaPosicion = -1;
        for (int i = 0; i < 10; i++) {
            String id = azarA.getLista().obtener(i).valor(0);
            int posicion = le.buscar(r -> r.valor(0).equals(id)).getIndice();
            if (posicion <= ultimaPosicion) {
                enOrden = false;
            }
            ultimaPosicion = posicion;
        }
        verificar("la muestra aleatoria conserva el orden del archivo", enOrden);

        ResultadoExtraccion<Registro> mas = cargar(aeropuertos, new ListaEnlazada<>(), OpcionesCarga.aleatorio(99999, 1L));
        verificar("pedir mas de los que hay carga todos sin repetir", mas.getCargados() == 7698);

        ResultadoExtraccion<Registro> rutas = cargar(FuenteDatos.RUTAS, new ListaSecuencial<>(), OpcionesCarga.todos());
        verificar("routes: 67663 cargadas, ninguna sin leer", rutas.getCargados() == 67663 && rutas.getNoLeidas() == 0);
        verificar("routes: 18 con equipo vacio al final de la linea", rutas.getLista().contar(r -> r.valor(8).isEmpty()) == 18);
        verificar("routes: 220 con ID origen \\N y 221 con ID destino \\N",
                rutas.getLista().contar(r -> r.valor(3).equals("\\N")) == 220
                        && rutas.getLista().contar(r -> r.valor(5).equals("\\N")) == 221);

        ResultadoExtraccion<Registro> aerolineas = cargar(FuenteDatos.AEROLINEAS, new ListaEnlazada<>(), OpcionesCarga.todos());
        verificar("airlines: 6162 cargadas, ninguna sin leer", aerolineas.getCargados() == 6162 && aerolineas.getNoLeidas() == 0);

        System.out.println(String.format("  Tiempos: airports %.1f ms, routes %.1f ms, airlines %.1f ms",
                enlazada.getMilisegundos(), rutas.getMilisegundos(), aerolineas.getMilisegundos()));
    }

    private static ResultadoExtraccion<Registro> cargar(FuenteDatos fuente, Lista<Registro> destino, OpcionesCarga opciones)
            throws IOException {
        return Extract.extraer(fuente.getArchivoPorDefecto(), fuente.getCamposEsperados(), fuente.getMapeador(), destino, opciones);
    }
}
