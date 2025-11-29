package domain;

public class Despachador implements Runnable {

    private final ColaEmergencias cola;
    private final GestorRecursos gestor;
    private final String nombre;

    public Despachador(String nombre, ColaEmergencias cola, GestorRecursos gestor) {
        this.nombre = nombre;
        this.cola = cola;
        this.gestor = gestor;
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
                if (recurso != null) {
                    System.out.printf("[%s] Atendiendo emergencia %d con %s%n-----------------------------------------------------------------------%n",
                            nombre, e.getId(), recurso);
                    Thread.sleep(2000); // Simular tiempo de atención
                    gestor.liberarRecurso(recurso);
                } else {
                    System.out.printf("[%s] Emergencia %d no atendida: no hay recursos disponibles de tipo %s%n-----------------------------------------------------------------------%n",
                            nombre, e.getId(), tipoNecesario);
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
