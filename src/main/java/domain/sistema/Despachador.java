package domain.sistema;

import app.Dashboard;
import domain.emergencia.ColaEmergencias;
import domain.emergencia.Emergencia;
import domain.recurso.GestorRecursos;
import domain.recurso.Recurso;
import domain.recurso.TipoRecurso;

import javax.swing.*;

public class Despachador implements Runnable {

    private final ColaEmergencias cola;
    private final GestorRecursos gestor;
    private final String nombre;
    private final Dashboard dashboard;

    public Despachador(String nombre, ColaEmergencias cola, GestorRecursos gestor, Dashboard dashboard) {
        this.nombre = nombre;
        this.cola = cola;
        this.gestor = gestor;
        this.dashboard = dashboard;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Emergencia e = cola.tomar();

                // Determinar tipo de recurso según gravedad
                TipoRecurso tipoNecesario = determinarTipoRecurso(e);

                // Intentar asignar un recurso
                Recurso recurso = gestor.asignarRecurso(tipoNecesario);
                SwingUtilities.invokeLater(() -> {
                    dashboard.agregarRecurso(new Object[]{
                            recurso.getId(),
                            recurso.getTipo().name(),
                            "Ocupado",
                            e.getId()
                    });
                });

                if (recurso != null) {
                    Metricas.emergenciasAtendidas.incrementAndGet();
                    Metricas.recursosLibres.decrementAndGet();

                    System.out.printf("[%s] Atendiendo emergencia %d con %s%n-----------------------------------------------------------------------%n",
                            nombre, e.getId(), recurso);

                    SwingUtilities.invokeLater(() -> {
                        dashboard.agregarDespachador(new Object[]{
                                nombre,
                                e.getId(),
                                recurso.toString()
                        });
                        dashboard.log("[%s] Atendiendo emergencia %d con %s".formatted(
                                nombre, e.getId(), recurso));
                    });

                    Thread.sleep(4000); // Simular tiempo de atención

                    // Calcular tiempo de espera después de ser atendida
                    long tiempoEsperaSeg = (System.currentTimeMillis() - e.getTimestampCreacion()) / 1000;
                    dashboard.actualizarEspera(e.getId(), tiempoEsperaSeg + " s");

                    gestor.liberarRecurso(recurso);
                    SwingUtilities.invokeLater(() -> {
                        dashboard.agregarRecurso(new Object[]{
                                recurso.getId(),
                                recurso.getTipo().name(),
                                "Libre",
                                "-"
                        });
                    });

                    // Actualizar métricas y dashboard
                    Metricas.recursosLibres.incrementAndGet();
                    SwingUtilities.invokeLater(() -> {
                        dashboard.log("[%s] Emergencia %d atendida y recurso %s liberado".formatted(
                                nombre, e.getId(), recurso));
                        dashboard.actualizarEstadoEmergencia(e.getId(), "Atendida");
                    });
                } else {
                    System.out.printf("[%s] Emergencia %d no atendida: no hay recursos disponibles de tipo %s%n-----------------------------------------------------------------------%n",
                            nombre, e.getId(), tipoNecesario);
                    SwingUtilities.invokeLater(() -> {
                        dashboard.log("[%s] Emergencia %d no atendida: no hay recursos disponibles de tipo %s".formatted(
                                nombre, e.getId(), tipoNecesario));
                    });
                }
            }
        } catch (InterruptedException ex) {
            System.out.printf("[%s] Despachador detenido%n", nombre);
            Thread.currentThread().interrupt();
        }
    }


    private TipoRecurso determinarTipoRecurso(Emergencia e) {
        // Lógica para determinar qué tipo de recurso se necesita
        return switch (e.getGravedad()) {
            case CRITICA, ALTA -> TipoRecurso.AMBULANCIA;
            case MEDIA -> TipoRecurso.MEDICO;
            case BAJA -> TipoRecurso.EQUIPO;
        };
    }
}
