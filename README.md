# ContadorConcurrente

Programa que divide el conteo de un numero entre multiples hilos que trabajan en paralelo. Implementado en Java y Go.

---

## Como funciona

El usuario ingresa un numero limite y la cantidad de hilos. El programa divide el rango equitativamente entre los hilos y cada uno acumula la suma de su porcion de forma concurrente. Al final se muestra el tiempo de cada hilo y el tiempo total.

**Ejemplo con numero = 20 y 4 hilos:**

| Hilo | Desde | Hasta |
|------|-------|-------|
| 1    | 1     | 5     |
| 2    | 6     | 10    |
| 3    | 11    | 15    |
| 4    | 16    | 20    |

El ultimo hilo siempre absorbe el residuo de la division entera.

---

## Ciclo de vida de los hilos

Cada hilo pasa por los siguientes estados durante la ejecucion:

| Estado | Descripcion |
|--------|-------------|
| **New** | El hilo fue creado pero aun no ha iniciado |
| **Runnable** | El hilo esta listo para ejecutarse, esperando al scheduler del SO |
| **Running** | El hilo esta acumulando la suma de su rango activamente |
| **Waiting** | El hilo principal espera con `join()` / `wg.Wait()` a que todos terminen |
| **Terminated** | El hilo termino su rango e imprimio su tiempo |

El orden en que terminan los hilos no es determinista — cambia en cada ejecucion dependiendo del scheduler del sistema operativo.

---

## Estructura del proyecto

```
ContadorConcurrente/
├── Java/
│   ├── Main.java
│   └── contadorConcurrencia.java
├── Go/
│   └── main.go
└── README.md
```

---

## Casos de prueba

Pruebas realizadas con 500 millones de numeros para que el impacto de la concurrencia sea visible. Cada hilo acumula la suma de su rango — esto obliga al procesador a hacer trabajo real y evita que el compilador optimice el loop.

### Caso 1 — 2 hilos

| Hilo | Rango | Suma | Tiempo |
|------|-------|------|--------|
| 1 | 1 - 250000000 | 31250000125000000 | 127 ms |
| 2 | 250000001 - 500000000 | 93750000125000000 | 128 ms |
| **Total** | | | **149 ms** |
JAVA

<img width="865" height="166" alt="image" src="https://github.com/user-attachments/assets/f20e42f4-f0a0-4955-8670-8452e5776911" />

GO

<img width="845" height="168" alt="image" src="https://github.com/user-attachments/assets/9ba927e3-98a9-4f45-ac67-418da48ffaae" />


Con solo 2 hilos cada uno carga con 250 millones de numeros. El tiempo es el mas alto de los tres casos porque hay poca paralelizacion y cada hilo tiene un rango enorme que procesar solo.

---

### Caso 2 — 8 hilos

| Hilo | Rango | Tiempo |
|------|-------|--------|
| 1 | 1 - 62500000 | 69 ms |
| 2 | 62500001 - 125000000 | 71 ms |
| 3 | 125000001 - 187500000 | 72 ms |
| 4 | 187500001 - 250000000 | 70 ms |
| 5 | 250000001 - 312500000 | 69 ms |
| 6 | 312500001 - 375000000 | 79 ms |
| 7 | 375000001 - 437500000 | 58 ms |
| 8 | 437500001 - 500000000 | 71 ms |
| **Total** | | **102 ms** |
JAVA

<img width="858" height="305" alt="image" src="https://github.com/user-attachments/assets/a656c8f5-c060-4680-8e8f-d1f73b18af31" />

GO

<img width="780" height="276" alt="image" src="https://github.com/user-attachments/assets/846d0a26-61b5-4bd0-a414-4a030bcf2867" />



Con 8 hilos el rango baja a 62.5 millones por hilo. El tiempo total cae un 32% respecto al caso de 2 hilos porque el trabajo esta mejor distribuido y el procesador ejecuta mas hilos en paralelo.

---

### Caso 3 — 16 hilos

| Hilo | Rango | Tiempo |
|------|-------|--------|
| 1 | 1 - 31250000 | 71 ms |
| 2 | 31250001 - 62500000 | 39 ms |
| 3 | 62500001 - 93750000 | 57 ms |
| 4 | 93750001 - 125000000 | 15 ms |
| 5 | 125000001 - 156250000 | 39 ms |
| 6 | 156250001 - 187500000 | 69 ms |
| 7 | 187500001 - 218750000 | 56 ms |
| 8 | 218750001 - 250000000 | 39 ms |
| 9 | 250000001 - 281250000 | 17 ms |
| 10 | 281250001 - 312500000 | 31 ms |
| 11 | 312500001 - 343750000 | 31 ms |
| 12 | 343750001 - 375000000 | 17 ms |
| 13 | 375000001 - 406250000 | 72 ms |
| 14 | 406250001 - 437500000 | 15 ms |
| 15 | 437500001 - 468750000 | 15 ms |
| 16 | 468750001 - 500000000 | 15 ms |
| **Total** | | **82 ms** |
JAVA

<img width="887" height="539" alt="image" src="https://github.com/user-attachments/assets/c0ec7dc6-a08d-4da4-b6c5-8ae2227b721a" />

GO
<img width="809" height="453" alt="image" src="https://github.com/user-attachments/assets/4b42f502-e833-4219-a4a2-ca1867c579ea" />



Con 16 hilos el rango baja a 31.25 millones por hilo. El tiempo total baja otro 20% respecto a 8 hilos. Sin embargo la reduccion ya no es tan proporcional como entre 2 y 8 hilos — esto se debe a que el procesador tiene un numero fijo de nucleos fisicos y el overhead de coordinar mas hilos empieza a pesar.

---

## Resumen de resultados

| Hilos | Tiempo total | Reduccion vs anterior |
|-------|-------------|----------------------|
| 2     | 149 ms      | —                    |
| 8     | 102 ms      | 32% mas rapido       |
| 16    | 82 ms       | 20% mas rapido       |

---

## Observaciones
 
**Mas hilos no siempre es mejor.** En Java el tiempo bajo consistentemente de 149ms a 102ms a 82ms al agregar mas hilos. En Go sin embargo el punto optimo fue 8 goroutines con 36ms — al pasar a 16 el tiempo subio a 47ms. Esto ocurre porque con 16 goroutines se supera el numero de nucleos fisicos disponibles y el overhead de coordinar y hacer context switching entre tantas goroutines empieza a costar mas de lo que se gana con la paralelizacion.
 
**El no determinismo es real.** En el caso de 16 hilos en Java, algunos terminan en 15ms y otros en 72ms teniendo exactamente el mismo rango. Esto demuestra que el scheduler del sistema operativo decide cuando le da tiempo de CPU a cada hilo y eso cambia segun la carga del sistema en ese momento. No hay garantia de que el hilo 1 termine antes que el hilo 16.
 
**Go es consistentemente mas rapido que Java.** Con 2 hilos Go es el doble de rapido; con 8 hilos casi el triple. Esto se debe a que las goroutines son hilos livianos manejados por el propio runtime de Go, mientras que los threads de Java son hilos del sistema operativo que tienen mayor overhead de creacion y gestion. Go ademas tiene un scheduler propio que distribuye las goroutines entre los nucleos de forma mas eficiente.
---

## Java vs Go

| | Java | Go |
|-|------|----|
| Unidad de concurrencia | Thread (hilo del SO) | Goroutine (hilo liviano) |
| Sincronizacion | `thread.join()` | `sync.WaitGroup` |
| Overhead | Mayor | Menor |

---

## Requisitos

**Java**
- Java 8 o superior
- IntelliJ IDEA

**Go**
- Go 1.18 o superior
- Terminal

## Como ejecutar

**Java**
```
Abrir Main.java en IntelliJ y dar Run
```

**Go**
```bash
go run main.go
```
