package world;

import controller.Generator;
import master.MasterController;

public class LifeGenerator implements Runnable {

    private final Generator generator;
    private final MasterController master;
    private volatile boolean running = true;

    private final int intervalMs;

    public LifeGenerator(Generator generator, MasterController master, int intervalSeconds) {
        this.generator = generator;
        this.master = master;
        this.intervalMs = intervalSeconds * 1000;
    }

    @Override
    public void run() {
        while (running) {
            try { Thread.sleep(intervalMs); }
            catch (InterruptedException ignored) {}

            int radius = 10 + (int)(Math.random() * 30);
            int speed  = 50 + (int)(Math.random() * 150);

            // Llama al Controller a través de la interfaz
            generator.onCreateBall(radius, speed);

            // Notifica al MasterController (si quieres usarlo)
            master.onLifeGeneratorEvent(radius, speed);
        }
    }

    public void stop() {
        running = false;
    }
}
