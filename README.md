# Concurrencia-Hilos
# ContadorConcurrente

Programa que divide el conteo de un numero entre multiples hilos que trabajan en paralelo. Implementado en Java y Go.

---

## Como funciona

El usuario ingresa un numero limite y la cantidad de hilos. El programa divide el rango equitativamente entre los hilos y cada uno cuenta su porcion de forma concurrente.

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
| **Running** | El hilo esta contando activamente su rango |
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

Para evidenciar el comportamiento de la concurrencia se corrieron pruebas con numeros grandes donde el impacto de los hilos es visible. Con numeros pequeños la diferencia es minima porque el trabajo termina casi instantaneo; con rangos grandes se puede ver claramente como mas hilos reducen el tiempo total.

### Caso 1 — pocos hilos con numero grande

<img width="607" height="219" alt="image" src="https://github.com/user-attachments/assets/31682a4e-88c6-4028-8900-6966a37ba635" />

Con solo 2 hilos el trabajo se divide en dos mitades. Cada hilo carga con 25 millones de numeros. El tiempo total es alto porque hay poca paralelizacion y cada hilo tiene un rango muy grande que procesar solo.

| Hilo | Rango | Tiempo del hilo |
|------|-------|-----------------|
| 1 | 1 - 25000000 | (tiempo que salio) |
| 2 | 25000001 - 50000000 | (tiempo que salio) |
| **Total** | | **(tiempo total que salio)** |

---

### Caso 2 — hilos balanceados

<img width="598" height="154" alt="image" src="https://github.com/user-attachments/assets/799e1315-e948-4845-9f70-82be349e018d" />

Con 8 hilos cada uno procesa solo 6.25 millones de numeros. El tiempo total baja notablemente respecto al caso anterior porque el trabajo esta mas distribuido y el procesador puede ejecutar mas hilos en paralelo al mismo tiempo.

| Hilo | Rango | Tiempo del hilo |
|------|-------|-----------------|
| 1 | 1 - 6250000 | (tiempo que salio) |
| 2 | 6250001 - 12500000 | (tiempo que salio) |
| 3 | 12500001 - 18750000 | (tiempo que salio) |
| 4 | 18750001 - 25000000 | (tiempo que salio) |
| 5 | 25000001 - 31250000 | (tiempo que salio) |
| 6 | 31250001 - 37500000 | (tiempo que salio) |
| 7 | 37500001 - 43750000 | (tiempo que salio) |
| 8 | 43750001 - 50000000 | (tiempo que salio) |
| **Total** | | **(tiempo total que salio)** |

---

### Caso 3 — muchos hilos

<img width="634" height="311" alt="image" src="https://github.com/user-attachments/assets/1f93970a-efb9-4f57-90ba-9ef494979712" />

Con 16 hilos el rango por hilo baja a 3.1 millones. A partir de cierto punto agregar mas hilos no reduce tanto el tiempo porque el procesador tiene un limite de nucleos fisicos y el overhead de crear y coordinar hilos empieza a pesar. Aqui se puede observar si el tiempo total sigue bajando o se estabiliza respecto al caso 2.

| Hilo | Tiempo del hilo |
|------|-----------------|
| 1 - 16 | (tiempos que salieron) |
| **Total** | **(tiempo total que salio)** |

---

## Observaciones

A mayor numero de hilos el tiempo total disminuye hasta cierto punto. Esto se debe a que el procesador tiene un numero fijo de nucleos — si se crean mas hilos que nucleos disponibles, el SO empieza a repartir el tiempo entre ellos (context switching) y el beneficio de agregar mas hilos se reduce.

El hilo que termina primero no siempre es el mismo en cada corrida. Esto es normal y demuestra el no determinismo de la concurrencia: el scheduler del SO decide en que orden ejecuta cada hilo y eso cambia segun la carga del sistema en ese momento.

Go es consistentemente mas rapido que Java en estas pruebas porque las goroutines son hilos livianos manejados por el runtime de Go, mientras que los threads de Java son hilos del sistema operativo que tienen mayor overhead de creacion y gestion.

---

## Java vs Go

| | Java | Go |
|-|------|----|
| Unidad de concurrencia | Thread (hilo del SO) | Goroutine (hilo liviano) |
| Sincronizacion | `thread.join()` | `sync.WaitGroup` |
| Overhead | Mayor | Menor |
| Velocidad | Buena | Mejor |

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
