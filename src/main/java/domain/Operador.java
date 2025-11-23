package domain;

import java.util.Random;

public class Operador implements Runnable {

    private final ColaEmergencias cola;
    private final MotorPrioridad motor;
    private final Random random = new Random();
    private final String nombre;

    public Operador(String nombre, ColaEmergencias cola, MotorPrioridad motor) {
        this.nombre = nombre;
        this.cola = cola;
        this.motor = motor;
    }

    @Override
    public void run() {
        try {
            long id = 1;
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

                Emergencia emergencia = new Emergencia(id++, gravedad, distanciaKm, ahora);

                // Calcular puntaje inicial
                double puntaje = motor.calcular(emergencia, ahora);
                emergencia.actualizarPuntaje(puntaje, ahora);

                // Agregar emergencia a la cola
                boolean agregada = cola.agregar(emergencia);
                if (agregada) {
                    System.out.printf("%s agregó emergencia %d (Gravedad: %s, Distancia: %.1f km, Puntaje: %.2f%n-----------------------------------------------------------------------%n",
                            nombre, emergencia.getId(), gravedad, distanciaKm, puntaje);
                } else {
                    System.out.printf("%s no pudo agregar emergencia %d: cola llena.%n-----------------------------------------------------------------------%n",
                            nombre, emergencia.getId());
                }

                // Esperar un tiempo antes de generar la siguiente emergencia
                Thread.sleep(500 + random.nextInt(1000) );
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Operador " + nombre + " interrumpido.");
        }
    }
}
