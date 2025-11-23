package domain;

public class RecalculadorPrioridades implements  Runnable {

    private final ColaEmergencias cola;
    private final MotorPrioridad motor;
    private final long intervaloMs;

    public RecalculadorPrioridades(ColaEmergencias cola, MotorPrioridad motor, long intervaloMs) {
        this.cola = cola;
        this.motor = motor;
        this.intervaloMs = intervaloMs;
    }

    @Override
    public void run() {
        try {
            while (true) {
                cola.recalcularPrioridades(motor);
                System.out.printf("[Recalculador] Prioridades actualizadas. Emergencias en cola: %d%n", cola.tamaño());
                System.out.println("-----------------------------------------------------------------------");
                Thread.sleep(intervaloMs);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println("[Recalculador] Hilo detenido.");
        }
    }
}
