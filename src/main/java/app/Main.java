package app;

import domain.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Sistema de Gestión de Emergencias iniciado.");
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
        GestorRecursos gestor = new GestorRecursos(recursos);

        // Aquí se podrían iniciar los hilos de Operadores, Despachadores y Recalculador de Prioridades
        Thread operador1 = new Thread(new Operador("Operador-1", cola, motor));
        Thread operador2 = new Thread(new Operador("Operador-2", cola, motor));

        Thread despachador1 = new Thread(new Despachador("Despachador-1", cola, gestor));
        Thread despachador2 = new Thread(new Despachador("Despachador-2", cola, gestor));

        Thread recalculador = new Thread(new RecalculadorPrioridades(cola, motor, 5000)); // Recalcula cada 5 segundos

        // Iniciar los hilos
        operador1.start();
        operador2.start();
        despachador1.start();
        despachador2.start();
        recalculador.start();
    }
}
