package connectors;

/**
 * Hilo que envía periódicamente mensajes de comprobación (PING) a través del Channel.
 */
public class HealthChannel implements Runnable {

    private final Channel channel;
    private final long intervalMs;

    public HealthChannel(Channel channel, long intervalMs) {
        this.channel = channel;
        this.intervalMs = intervalMs;
    }

    /** Inicia el hilo de salud */
    public void start() {
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(intervalMs);
                channel.sendPing();
            } catch (InterruptedException ignored) {}
        }
    }
}
