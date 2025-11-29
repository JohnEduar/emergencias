package domain.recurso;

import java.util.List;

public class GestorRecursos {
    private final List<Recurso> recursos;
    private int ultimoIndice = 0;

    public GestorRecursos(List<Recurso> recursos) {
        this.recursos = recursos;
    }

    // Asigna un recurso disponible del tipo solicitado, si hay alguno
    public synchronized Recurso asignarRecurso(TipoRecurso tipo) {
        int inicio = ultimoIndice;
        int intentos = 0;

        while (intentos < recursos.size()) {
            Recurso r = recursos.get(ultimoIndice);
            ultimoIndice = (ultimoIndice + 1) % recursos.size();
            intentos++;

            if (r.getTipo() == tipo && r.isDisponible()) {
                r.asignar();
                return r;
            }
        }

        return null; // No hay recursos disponibles de ese tipo
    }

    // Libera el recurso asignado
    public synchronized void liberarRecurso(Recurso r) {
        r.liberar();
    }

    // Muestra el estado actual de los recursos
    public void mostrarEstado() {
        recursos.forEach(System.out::println);
    }
}
