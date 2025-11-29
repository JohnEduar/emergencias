package app;

import domain.emergencia.ColaEmergencias;
import domain.emergencia.MotorPrioridad;
import domain.recurso.GestorRecursos;
import domain.recurso.Recurso;
import domain.recurso.TipoRecurso;
import domain.sistema.Despachador;
import domain.sistema.Metricas;
import domain.sistema.Operador;
import domain.sistema.RecalculadorPrioridades;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Sistema de Gestión de Emergencias iniciado.");

        Dashboard dashboard = new Dashboard();
        dashboard.mostrar();

        // Crear la cola con la capacidad máxima
        ColaEmergencias cola = new ColaEmergencias(50);

        // Crear el motor de prioridad con pesos y SLA críticos
        MotorPrioridad motor = new MotorPrioridad(
                10, // Peso gravedad
                2, // Peso distancia
                1, // Peso tiempo espera
                10_000 // SLA crítica: 10 segundos
        );

        // Crear recursos iniciales
        List<Recurso> recursos = List.of(
                new Recurso(1, TipoRecurso.AMBULANCIA),
                new Recurso(2, TipoRecurso.AMBULANCIA),
                new Recurso(3, TipoRecurso.AMBULANCIA),
                new Recurso(4, TipoRecurso.AMBULANCIA),
                new Recurso(5, TipoRecurso.EQUIPO),
                new Recurso(6, TipoRecurso.EQUIPO),
                new Recurso(7, TipoRecurso.EQUIPO),
                new Recurso(8, TipoRecurso.MEDICO),
                new Recurso(9, TipoRecurso.MEDICO),
                new Recurso(10, TipoRecurso.MEDICO)
        );
        // Actualizar métricas iniciales y dashboard
        Metricas.recursosLibres.set(recursos.size());
        for (Recurso r : recursos) {
            dashboard.agregarRecurso(new Object[]{
                    r.getId(),
                    r.getTipo(),
                    "LIBRE"
            });
        }
        GestorRecursos gestor = new GestorRecursos(recursos);


        // Iniciar hilos de operadores y despachadores
        Thread operador1 = new Thread(new Operador("Operador-1", cola, motor, dashboard));
        Thread operador2 = new Thread(new Operador("Operador-2", cola, motor, dashboard));

        Thread despachador1 = new Thread(new Despachador("Despachador-1", cola, gestor, dashboard));
        Thread despachador2 = new Thread(new Despachador("Despachador-2", cola, gestor, dashboard));

        Thread recalculador = new Thread(new RecalculadorPrioridades(cola, motor, 5000, dashboard)); // Recalcula cada 5 segundos

        // Iniciar los hilos
        operador1.start();
        operador2.start();
        despachador1.start();
        despachador2.start();
        recalculador.start();
    }
}
