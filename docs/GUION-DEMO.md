# Guion de demostración del Hito 1

Duración aproximada: 5 minutos. Todas las cifras de este guion se verificaron con los archivos de `data/`.

## Antes de empezar
- Abrir el proyecto en IntelliJ y dejar `Main.java` listo para ejecutar.
- Tener abierto el README en GitHub (arquitectura y reparto).

## Pasos

| # | Acción | Qué debe verse | Qué decir |
|---|---|---|---|
| 1 | Mostrar el repositorio y las carpetas `model`, `estructuras`, `etl`, `app` | Estructura por capas | "Trabajamos con OpenFlights: 7.698 aeropuertos, 67.663 rutas y 6.162 aerolíneas. El proyecto completo es un ETL con listas, pilas, colas, árbol y grafo" |
| 2 | Ejecutar `Main`. Datos: Aeropuertos. Guardar en: Lista enlazada. Modo: Todos. **Cargar** | Líneas 7698, cargadas 7698, no leídas 0, unos 100 a 150 ms | "El Extract lee el CSV línea por línea y guarda cada aeropuerto en nuestra lista enlazada" |
| 3 | Señalar las celdas rojas y amarillas | `\N` en rojo, vacíos en amarillo | "Los datos llegan sucios a propósito. Extract no los toca. Los limpia el Transform en el hito 2" |
| 4 | Campo IATA, valor `GKA`, **Buscar** | Posición 1 de 7698, 1 comparación | "Está al inicio de la lista, una sola comparación" |
| 5 | Campo IATA, valor `UIO`, **Buscar** | Posición 2556, 2556 comparaciones | "El aeropuerto de Quito está en la posición 2556, así que recorrió 2556 nodos" |
| 6 | Campo ICAO, valor `UKDM`, **Buscar** | Posición 7698, 7698 comparaciones | "Es el último. Buscar en una lista cuesta O(n). En noviembre lo comparamos con el BST" |
| 7 | Campo País, valor `Ecuador`, **Filtrar** | 36 coincidencias de 7698 | "Incluye el de Ambato, Chachoán (ATF). Ver todos para volver" |
| 8 | Campo IATA, valor `\N`, **Filtrar** | 1626 coincidencias | "Estos 1626 aeropuertos no tienen código IATA. El Transform decidirá qué hacer con ellos" |
| 9 | Guardar en: Lista secuencial. Modo: Aleatorio, N=12, Semilla=42. **Cargar** | 12 filas, capacidad 16 | "La secuencial es un arreglo que duplica su capacidad. Con la misma semilla sale siempre la misma muestra, y con 12 filas podemos explicar la estructura a mano" |
| 10 | Datos: Rutas. Modo: Todos. **Cargar** | 67663 cargadas, 0 no leídas | "Hay 18 filas con el último campo vacío. `split(\",\")` las habría roto. Nuestro `LectorCSV` las conserva" |

## Preguntas probables en la defensa

| Pregunta | Respuesta corta |
|---|---|
| ¿Por qué lista enlazada con `tail`? | Para insertar al final en O(1) y mantener el orden de llegada sin recorrer la lista |
| ¿Por qué dos listas? | El documento pide ambas. Comparten la interfaz `Lista<T>`, así el resto del sistema funciona con cualquiera |
| ¿Qué pasa cuando la secuencial se llena? | Crea un arreglo del doble de tamaño, copia los datos y sigue |
| ¿Por qué el Extract no normaliza? | Cada fase del ETL tiene una responsabilidad. Extract lee, Transform valida y limpia, Load guarda |
| ¿Por qué no `split(",")`? | Descarta los campos vacíos finales y parte los textos que llevan comas entre comillas |
| ¿Qué es la semilla? | Hace que el azar sea repetible. Con la misma semilla la muestra es la misma |
| ¿Qué pasa si pido más filas de las que hay? | Se cargan todas, sin repetir |
| ¿Por qué la búsqueda da el número de comparaciones? | Para demostrar con datos reales que es O(n) y poder comparar con el BST después |
