package main

import (
	"fmt"
	"sync"
	"time"
)

func contar(id, inf, sup int, wg *sync.WaitGroup) {
	defer wg.Done()
	start := time.Now()

	suma := 0
	for i := inf; i <= sup; i++ {
		suma += i
	}

	fmt.Printf("goroutine %d lista (%d - %d) - suma: %d - %d ms\n", id, inf, sup, suma, time.Since(start).Milliseconds())
}

func main() {
	var n, g int
	fmt.Print("numero a contar: ")
	fmt.Scan(&n)
	fmt.Print("cuantas goroutines: ")
	fmt.Scan(&g)

	rango := n / g
	start := time.Now()

	var wg sync.WaitGroup
	for i := 0; i < g; i++ {
		inf := i*rango + 1
		sup := (i + 1) * rango
		if i == g-1 {
			sup = n
		}
		wg.Add(1)
		go contar(i+1, inf, sup, &wg)
	}

	wg.Wait()
	fmt.Printf("tiempo total: %d ms\n", time.Since(start).Milliseconds())
}