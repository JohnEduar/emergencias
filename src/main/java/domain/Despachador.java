package domain;

public class Despachador implements  Runnable {

    private final ColaEmergencias cola;
    private final String nombre;

    public Despachador(String nombre, ColaEmergencias cola) {
        this.nombre = nombre;
        this.cola = cola;
    }

    @Override
    public void run() {
        try {
            // Tomar la emergencia de mayor prioridad y despacharla
            while (true) {
                Emergencia emergencia = cola.tomar();

                System.out.printf("%s despachó emergencia %d (Gravedad: %s, Distancia: %.1f km, Puntaje: %.2f)%n-----------------------------------------------------------------------%n",
                        nombre, emergencia.getId(), emergencia.getGravedad(), emergencia.getDistanciaKm(), emergencia.getPuntaje());
                // Simular tiempo de despacho
                Thread.sleep(2000);
        }
    } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println("Despachador " + nombre + " interrumpido.");
        }
    }
}
