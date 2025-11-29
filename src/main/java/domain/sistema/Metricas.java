package domain.sistema;

import java.util.concurrent.atomic.AtomicInteger;

public class Metricas {

    public static final AtomicInteger totalEmergencias = new AtomicInteger(0);
    public static final AtomicInteger emergenciasAtendidas = new AtomicInteger(0);
    public static final AtomicInteger recursosLibres = new AtomicInteger(0);

}
