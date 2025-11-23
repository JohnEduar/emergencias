package domain;

public final class Emergencia implements Comparable<Emergencia> {

    private final long id;
    private final Gravedad gravedad;
    private final double distanciaKm;
    private final long recibidaEnMs;

    private volatile double puntaje;
    private volatile long ultimaActualizacionEnMs;

    public Emergencia(long id, Gravedad gravedad, double distanciaKm, long recibidaEnMs) {
        this.id = id;
        this.gravedad = gravedad;
        this.distanciaKm = distanciaKm;
        this.recibidaEnMs = recibidaEnMs;
        this.puntaje = 0.0;
        this.ultimaActualizacionEnMs = recibidaEnMs;
    }

    // Mayor puntaje primero
    @Override
    public int compareTo(Emergencia otraEmergencia) {
        return Double.compare(otraEmergencia.puntaje, this.puntaje);
    }

    public long getId() {
        return id;
    }

    public Gravedad getGravedad() {
        return gravedad;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public long getRecibidaEnMs() {
        return recibidaEnMs;
    }

    public double getPuntaje() {
        return puntaje;
    }

    public long getUltimaActualizacionEnMs() {
        return ultimaActualizacionEnMs;
    }

    // Actualización controlada de puntaje
    public void actualizarPuntaje(double nuevoPuntaje, long ahoraMs) {
        this.puntaje = nuevoPuntaje;
        this.ultimaActualizacionEnMs = ahoraMs;
    }
}
