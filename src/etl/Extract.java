package etl;

import estructuras.Lista;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public final class Extract {

    private static final char BOM = '﻿';

    private Extract() {
    }

    public static <T> ResultadoExtraccion<T> extraer(String ruta, int camposEsperados, Mapeador<T> mapeador,
                                                     Lista<T> destino, OpcionesCarga opciones) throws IOException {
        long inicio = System.nanoTime();
        Path archivo = Paths.get(ruta);
        int totalLineas = contarLineas(archivo);
        boolean[] marcadas = null;
        if (opciones.getModo() == ModoCarga.ALEATORIO) {
            marcadas = elegirAleatorias(totalLineas, opciones);
        }

        int numero = 0;
        int seleccionadas = 0;
        int cargados = 0;
        int noLeidas = 0;
        boolean primera = true;
        try (BufferedReader lector = abrir(archivo)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (primera) {
                    primera = false;
                    if (!linea.isEmpty() && linea.charAt(0) == BOM) {
                        linea = linea.substring(1);
                    }
                }
                if (linea.trim().isEmpty()) {
                    continue;
                }
                numero++;
                if (!estaSeleccionada(numero, opciones, marcadas)) {
                    continue;
                }
                seleccionadas++;
                String[] campos = LectorCSV.separar(linea);
                if (campos.length != camposEsperados) {
                    noLeidas++;
                    continue;
                }
                destino.insertarFinal(mapeador.crear(campos));
                cargados++;
            }
        }
        double milisegundos = (System.nanoTime() - inicio) / 1_000_000.0;
        return new ResultadoExtraccion<>(destino, ruta, totalLineas, seleccionadas, cargados, noLeidas, milisegundos);
    }

    private static BufferedReader abrir(Path archivo) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(archivo), StandardCharsets.UTF_8));
    }

    private static int contarLineas(Path archivo) throws IOException {
        int total = 0;
        try (BufferedReader lector = abrir(archivo)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    total++;
                }
            }
        }
        return total;
    }

    private static boolean[] elegirAleatorias(int totalLineas, OpcionesCarga opciones) {
        int cantidad = Math.min(opciones.getCantidad(), totalLineas);
        Random azar = opciones.getSemilla() == null ? new Random() : new Random(opciones.getSemilla());
        int[] numeros = new int[totalLineas];
        for (int i = 0; i < totalLineas; i++) {
            numeros[i] = i + 1;
        }
        boolean[] marcadas = new boolean[totalLineas + 1];
        for (int i = 0; i < cantidad; i++) {
            int j = i + azar.nextInt(totalLineas - i);
            int temporal = numeros[i];
            numeros[i] = numeros[j];
            numeros[j] = temporal;
            marcadas[numeros[i]] = true;
        }
        return marcadas;
    }

    private static boolean estaSeleccionada(int numero, OpcionesCarga opciones, boolean[] marcadas) {
        switch (opciones.getModo()) {
            case PRIMEROS:
                return numero <= opciones.getCantidad();
            case RANGO:
                return numero >= opciones.getDesde() && numero <= opciones.getHasta();
            case ALEATORIO:
                return marcadas[numero];
            default:
                return true;
        }
    }
}
