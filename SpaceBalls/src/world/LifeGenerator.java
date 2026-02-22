package world;

import controller.Controller;

/**
 * Genera bolas automáticamente cada X segundos.
 * Corre en un hilo independiente.
 */
public class LifeGenerator implements Runnable {

    private final Controller controller;
    private volatile boolean running = true;

    private final int intervalMs;

    /**
     * @param controller controlador principal
     * @param intervalSeconds intervalo entre bolas nuevas (solo configurable desde código)
     */
    public LifeGenerator(Controller controller, int intervalSeconds) {
        this.controller = controller;
        this.intervalMs = intervalSeconds * 1000;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException ignored) {}

            int radius = 10 + (int)(Math.random() * 30);
            int speed  = 50 + (int)(Math.random() * 150);

            controller.onCreateBall(radius, speed);
        }
    }

    /** Detiene el hilo de generación */
    public void stop() {
        running = false;
    }
}
