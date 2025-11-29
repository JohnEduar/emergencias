package domain;

import java.util.Random;

public enum TipoRecurso {
    AMBULANCIA,
    MEDICO,
    EQUIPO;

    // Generador de números aleatorios
    private static final Random random = new Random();

    // Método para obtener un tipo de recurso aleatorio
    public static TipoRecurso getRandomTipo() {
        TipoRecurso[] tipos = values();
        return tipos[random.nextInt(tipos.length)];
    }
}

