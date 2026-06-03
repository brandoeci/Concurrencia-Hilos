import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Carrera de threads");

        System.out.println("que numero desea contar?");
        int number = scanner.nextInt();

        System.out.println("cuantos hilos?");
        int threadNumber = scanner.nextInt();

        long inicio = System.currentTimeMillis();

        List<Thread> counters = new ArrayList<>();

        int sequenceLength = number / threadNumber;

        System.out.println("dividiendo " + number + " entre " + threadNumber + " hilos (" + sequenceLength  + " numeros cada uno)");

        for (int i = 0; i < threadNumber; i++) {

            int limiteInf = i * sequenceLength + 1;

            int limiteSup = (i == threadNumber - 1) ? number : (i + 1) * sequenceLength;

            counters.add(new contadorConcurrencia(i + 1, limiteInf, limiteSup));
        }

        for (Thread t : counters) {
            t.start();
        }

        for (Thread t : counters) {
            t.join();
        }

        long fin = System.currentTimeMillis();

        System.out.println("todos los hilos han terminado");
        System.out.println("tiempo total: " + (fin - inicio) + " ms");
    }
}