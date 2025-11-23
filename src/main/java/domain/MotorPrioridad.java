package domain;

public final class MotorPrioridad {

    private final double pesoGravedad;
    private final double pesoDistancia;
    private final double pesoEspera;
    private final long slaCriticaMs;

    public MotorPrioridad(double pesoGravedad, double pesoDistancia, double pesoEspera, long slaCriticaMs) {
        this.pesoGravedad = pesoGravedad;
        this.pesoDistancia = pesoDistancia;
        this.pesoEspera = pesoEspera;
        this.slaCriticaMs = slaCriticaMs;
    }

    public double calcular(Emergencia e, long ahoraMs){
        int valorGravedad = switch (e.getGravedad()) {
            case BAJA -> 0;
            case MEDIA -> 1;
            case ALTA -> 2;
            case CRITICA -> 3;
        };

        double esperaSegundos = (ahoraMs - e.getRecibidaEnMs()) / 1000.0;

        double puntajeBase = (valorGravedad * pesoGravedad)
                            - (e.getDistanciaKm() * pesoDistancia)
                            + (Math.log1p(esperaSegundos) * pesoEspera);

        // Ajuste por SLA crítica
        if (e.getGravedad() == Gravedad.CRITICA && (ahoraMs - e.getRecibidaEnMs()) >= slaCriticaMs) {
            return puntajeBase * 1.5; // Aumenta el puntaje en un 50%
        }
        return puntajeBase;
    }
}
