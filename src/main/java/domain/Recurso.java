package domain;

public class Recurso {

    private final long id;
    private final TipoRecurso tipo;
    private boolean disponible;

    public Recurso(long id, TipoRecurso tipo) {
        this.id = id;
        this.tipo = tipo;
        this.disponible = true;
    }

    public long getId() {
        return id;
    }

    public TipoRecurso getTipo() {
        return tipo;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void asignar() {
        disponible = false;
    }
    public void liberar() {
        disponible = true;
    }

    // Override toString para mostrar el estado del recurso
    @Override
    public String toString() {
        return tipo + " #" + id + (disponible ? " (libre)" : " (asignado)");
    }
}
