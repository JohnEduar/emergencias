package domain.emergencia;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public final class ColaEmergencias {

    private final PriorityBlockingQueue<Emergencia> cola;
    private final int capacidad;

    public ColaEmergencias(int capacidad) {
        this.capacidad = capacidad;
        this.cola = new PriorityBlockingQueue<>();
    }

    // Insertar una emergencia en la cola
    public synchronized boolean agregar(Emergencia e) {
        if (cola.size() >= capacidad) {
            return false; // Cola llena
        }
        return cola.offer(e);
    }

    // Tomar la siguiente emergencia de mayor prioridad
    public Emergencia tomar() throws InterruptedException {
        return cola.take();
    }

    // Recalcular el puntaje de todas las emergencias en la cola
    public void recalcularPrioridades(MotorPrioridad motor){
        long ahora = System.currentTimeMillis();
        List<Emergencia> lista = new ArrayList<>();
        int tamañoActual = cola.size();
        cola.drainTo(lista); // Vaciar la cola temporalmente
        for (Emergencia e : lista) {
            double nuevoPuntaje = motor.calcular(e, ahora);
            e.actualizarPuntaje(nuevoPuntaje, ahora);
        }
        cola.addAll(lista); // Reinsertar las emergencias con puntajes actualizados
    }

    public int tamaño() {
        return cola.size();
    }
}
