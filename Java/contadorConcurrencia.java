public class contadorConcurrencia extends Thread {

    private int id;
    private int limiteInf;
    private int limiteSup;

    public contadorConcurrencia(int id, int limiteInf, int limiteSup) {
        this.id = id;
        this.limiteInf = limiteInf;
        this.limiteSup = limiteSup;
    }

    @Override
    public void run() {
        long inicio = System.currentTimeMillis();

        long suma = 0;
        for (int i = limiteInf; i <= limiteSup; i++) {
            suma += i;
        }

        long fin = System.currentTimeMillis();
        long tiempo = fin - inicio;

        System.out.println("hilo " + id + " terminado (" + limiteInf + " - " + limiteSup + ") - suma: " + suma + " - tiempo: " + tiempo + " ms");
    }
}