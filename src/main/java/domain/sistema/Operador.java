package domain.sistema;

import app.Dashboard;
import domain.emergencia.ColaEmergencias;
import domain.emergencia.Emergencia;
import domain.emergencia.Gravedad;
import domain.emergencia.MotorPrioridad;

import javax.swing.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Operador implements Runnable {

    private static final AtomicLong contadorGlobal = new AtomicLong(1); // Contador global para IDs únicos

    private final ColaEmergencias cola;
    private final MotorPrioridad motor;
    private final Random random = new Random();
    private final String nombre;
    private final Dashboard dashboard;

    public Operador(String nombre, ColaEmergencias cola, MotorPrioridad motor, Dashboard dashboard) {
        this.nombre = nombre;
        this.cola = cola;
        this.motor = motor;
        this.dashboard = dashboard;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Simular llegada de una nueva emergencia
                Gravedad gravedad = switch (random.nextInt(4)) {
                    case 0 -> Gravedad.BAJA;
                    case 1 -> Gravedad.MEDIA;
                    case 2 -> Gravedad.ALTA;
                    default -> Gravedad.CRITICA;
                };
                double distanciaKm = 0.5 + random.nextDouble() * 15; // Entre 0.5 y 15 km
                long ahora = System.currentTimeMillis();

                long id = contadorGlobal.getAndIncrement(); // ID único compartido
                Emergencia emergencia = new Emergencia(id, gravedad, distanciaKm, ahora);

                // Calcular puntaje inicial
                double puntaje = motor.calcular(emergencia, ahora);
                emergencia.actualizarPuntaje(puntaje, ahora);

                // Agregar emergencia a la cola
                boolean agregada = cola.agregar(emergencia);
                if (agregada) {
                    Metricas.totalEmergencias.incrementAndGet(); // Incrementar contador de emergencias recibidas

                    System.out.printf("%s agregó emergencia %d (Gravedad: %s, Distancia: %.1f km, Puntaje: %.2f%n-----------------------------------------------------------------------%n",
                            nombre, emergencia.getId(), gravedad, distanciaKm, puntaje);
                    // Actualizar dashboard
                    SwingUtilities.invokeLater(() -> {
                        dashboard.agregarEmergencia(new Object[]{
                                emergencia.getId(),
                                emergencia.getGravedad(),
                                String.format("%.1f km", distanciaKm),
                                "-", // Tiempo de espera inicial
                                String.format("%.2f", puntaje),
                                "En espera"
                        });
                        dashboard.log("[%s] Emergencia %d agregada (Gravedad: %s, Distancia: %.1f km, Puntaje: %.2f)".formatted(
                                nombre, emergencia.getId(), gravedad, distanciaKm, puntaje));
                    });
                } else {
                    System.out.printf("%s no pudo agregar emergencia %d: cola llena.%n-----------------------------------------------------------------------%n",
                            nombre, emergencia.getId());
                    SwingUtilities.invokeLater(() -> {
                        dashboard.log("[%s] No se pudo agregar emergencia %d: cola llena.".formatted(
                                nombre, emergencia.getId()));
                    });
                }

                // Esperar un tiempo antes de generar la siguiente emergencia
                Thread.sleep(2000 + random.nextInt(4000)); // Entre 2 y 6 segundos
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Operador " + nombre + " interrumpido.");
        }
    }
}