# Diagramas del Hito 1

Diagramas del código que está en `src/`. Cola, pila, Transform, Load, BST y grafo se nombran solo en la arquitectura prevista: todavía no hay clases de esos módulos.

## Diagrama de clases

```mermaid
classDiagram
    direction LR

    class Registro {
        <<interface>>
        +numeroCampos() int
        +valor(columna) String
    }
    class Aeropuerto {
        +CAMPOS int
        +desdeCampos(campos) Aeropuerto
        +numeroCampos() int
        +valor(columna) String
    }
    class Aerolinea {
        +CAMPOS int
        +desdeCampos(campos) Aerolinea
        +numeroCampos() int
        +valor(columna) String
    }
    class Ruta {
        +CAMPOS int
        +desdeCampos(campos) Ruta
        +numeroCampos() int
        +valor(columna) String
    }

    class Lista~T~ {
        <<interface>>
        +insertarInicio(dato) void
        +insertarFinal(dato) void
        +obtener(indice) T
        +tamanio() int
        +estaVacia() boolean
        +limpiar() void
        +buscar(criterio) ResultadoBusqueda
        +filtrar(criterio) Lista
        +contar(criterio) int
        +descripcion() String
    }
    class ListaEnlazada~T~ {
        -Nodo~T~ head
        -Nodo~T~ tail
        -int tamanio
    }
    class ListaSecuencial~T~ {
        -Object[] datos
        -int tope
        +capacidad() int
    }
    class Nodo~T~ {
        -T dato
        -Nodo~T~ siguiente
        +getDato() T
        +setSiguiente(siguiente) void
    }
    class Criterio~T~ {
        <<interface>>
        +cumple(dato) boolean
    }
    class ResultadoBusqueda~T~ {
        -T elemento
        -int indice
        -int comparaciones
        +encontrado() boolean
        +getIndice() int
        +getComparaciones() int
    }

    class Mapeador~T~ {
        <<interface>>
        +crear(campos) T
    }
    class ModoCarga {
        <<enumeration>>
        TODOS
        PRIMEROS
        RANGO
        ALEATORIO
    }
    class OpcionesCarga {
        -ModoCarga modo
        -int cantidad
        -int desde
        -int hasta
        -Long semilla
        +todos() OpcionesCarga
        +primeros(cantidad) OpcionesCarga
        +rango(desde, hasta) OpcionesCarga
        +aleatorio(cantidad, semilla) OpcionesCarga
    }
    class LectorCSV {
        +separar(linea) String[]
    }
    class Extract {
        +extraer(ruta, camposEsperados, mapeador, destino, opciones) ResultadoExtraccion
    }
    class ResultadoExtraccion~T~ {
        -Lista~T~ lista
        -int totalLineas
        -int lineasSeleccionadas
        -int cargados
        -int noLeidas
        -double milisegundos
    }
    class FuenteDatos {
        +AEROPUERTOS FuenteDatos
        +AEROLINEAS FuenteDatos
        +RUTAS FuenteDatos
        +getArchivoPorDefecto() String
        +getCamposEsperados() int
        +getEncabezados() String[]
        +getMapeador() Mapeador
    }

    class Main {
        +main(args) void
    }
    class VentanaPrincipal {
        -Lista~Registro~ cargada
        -Lista~Registro~ mostrada
        -cargar() void
        -buscar() void
        -filtrar() void
    }
    class ModeloTablaRegistros {
        -Lista~Registro~ lista
        -String[] encabezados
        +actualizar(lista, encabezados) void
        +getValueAt(fila, columna) Object
    }

    Registro <|.. Aeropuerto
    Registro <|.. Aerolinea
    Registro <|.. Ruta

    Lista~T~ <|.. ListaEnlazada~T~
    Lista~T~ <|.. ListaSecuencial~T~
    ListaEnlazada~T~ o-- Nodo~T~ : head y tail
    Lista~T~ ..> Criterio~T~ : buscar, filtrar, contar
    Lista~T~ ..> ResultadoBusqueda~T~ : devuelve

    Extract ..> LectorCSV : separa cada linea
    Extract ..> Mapeador~T~ : crea el registro
    Extract --> OpcionesCarga : modo de carga
    Extract ..> Lista~T~ : insertarFinal
    Extract ..> ResultadoExtraccion~T~ : devuelve
    OpcionesCarga --> ModoCarga
    FuenteDatos --> Mapeador~T~
    FuenteDatos ..> Aeropuerto : desdeCampos
    FuenteDatos ..> Aerolinea : desdeCampos
    FuenteDatos ..> Ruta : desdeCampos
    ResultadoExtraccion~T~ o-- Lista~T~

    Main --> VentanaPrincipal : abre
    VentanaPrincipal --> ModeloTablaRegistros : tabla
    VentanaPrincipal --> FuenteDatos : origen
    VentanaPrincipal --> Extract : cargar
    VentanaPrincipal --> Lista~T~ : cargada y mostrada
    ModeloTablaRegistros --> Lista~T~
    ModeloTablaRegistros --> Registro
```

`Aeropuerto` tiene 14 campos, `Aerolinea` 8 y `Ruta` 9. Los tres guardan el texto tal como viene del CSV. `ListaEnlazada` inserta al final en O(1) porque conserva `tail`. `ListaSecuencial` duplica el arreglo al llenarse; la capacidad inicial es 16.

## Flujo entregado en el Hito 1

```mermaid
flowchart LR
    A["CSV en data: airports, airlines, routes"] --> B["LectorCSV: separa campos y conserva vacios"]
    B --> C["Extract: Todos, Primeros N, Rango o Aleatorio"]
    C --> D["Lista de Registro: enlazada o secuencial"]
    D --> E["Ventana Swing: mostrar, buscar y filtrar"]
```

## Arquitectura prevista del proyecto completo

Estos módulos no están implementados. El diagrama solo fija cómo se van a conectar en los hitos siguientes.

```mermaid
flowchart TD
    A["Fuentes CSV: airports, routes, airlines"] --> B["EXTRACT"]
    B --> C["Cola de registros"]
    C --> D["TRANSFORM: validar y normalizar"]
    D --> E["Lista de registros validos"]
    D --> F["Pila de errores"]
    E --> G["LOAD: archivos limpios en output"]
    F --> G
    G --> H["BST: indice por ID"]
    G --> I["Grafo: aeropuertos y rutas"]
    I --> J["BFS con cola y DFS con pila"]
    H --> K["Visualizacion y analisis"]
    J --> K
    K --> L["Flujogramas y reporte"]
```
