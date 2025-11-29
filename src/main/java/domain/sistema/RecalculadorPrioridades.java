package domain.sistema;

import app.Dashboard;
import domain.emergencia.ColaEmergencias;
import domain.emergencia.MotorPrioridad;

import javax.swing.*;

public class RecalculadorPrioridades implements  Runnable {

    private final ColaEmergencias cola;
    private final MotorPrioridad motor;
    private final long intervaloMs;
    private final Dashboard dashboard;

    public RecalculadorPrioridades(ColaEmergencias cola, MotorPrioridad motor, long intervaloMs, Dashboard dashboard) {
        this.cola = cola;
        this.motor = motor;
        this.intervaloMs = intervaloMs;
        this.dashboard = dashboard;
    }

    @Override
    public void run() {
        try {
            while (true) {
                cola.recalcularPrioridades(motor);
                System.out.printf("[Recalculador] Prioridades actualizadas. Emergencias en cola: %d%n", cola.tamaño());
                System.out.println("-----------------------------------------------------------------------");
                // Actualizar dashboard
                SwingUtilities.invokeLater(() -> {;
                    dashboard.log("[Recalculador] Prioridades actualizadas. Emergencias en cola: %d".formatted(cola.tamaño()));
                    dashboard.actualizarKPIs(
                            Metricas.totalEmergencias.get(),
                            Metricas.emergenciasAtendidas.get(),
                            Metricas.recursosLibres.get()
                    );
                });
                Thread.sleep(intervaloMs);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println("[Recalculador] Hilo detenido.");
        }
    }
}
