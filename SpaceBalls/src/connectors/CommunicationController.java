package connectors;

import model.BallDTO;

import java.net.Socket;
import java.util.function.Consumer;

/**
 * Controlador de comunicación central.
 * Gestiona el Channel, HealthChannel y coordina reconexiones.
 */
public class CommunicationController {

    private final Consumer<BallDTO> onBallReceived;

    private Channel channel;
    private HealthChannel healthChannel;

    private ServerConnector serverConnector;
    private PlayerConnector playerConnector;
    private boolean isClient; // true = cliente, false = servidor

    public CommunicationController(Consumer<BallDTO> onBallReceived) {
        this.onBallReceived = onBallReceived;
    }

    /**
     * Se llama cuando un conector (servidor/cliente) obtiene un Socket válido.
     * Crea el Channel y el HealthChannel.
     */
    public synchronized void attachSocket(Socket socket) {
        System.out.println("Socket adjuntado al Channel.");

        // Cerrar canal anterior si existía
        if (channel != null) {
            channel.close();
        }

        channel = new Channel(
                socket,
                onBallReceived,
                this::onHealthReceived,
                this::onChannelDisconnected
        );
        channel.start();

        healthChannel = new HealthChannel(channel, 2000);
        healthChannel.start();
    }

    /** Registrar conector servidor (para reconexión) */
    public void registerServerConnector(ServerConnector serverConnector) {
        this.serverConnector = serverConnector;
        this.isClient = false;
    }

    /** Registrar conector cliente (para reconexión) */
    public void registerPlayerConnector(PlayerConnector playerConnector) {
        this.playerConnector = playerConnector;
        this.isClient = true;
    }

    /** Llamado cuando llega un PING desde el otro lado */
    private void onHealthReceived() {
        System.out.println("PING recibido.");
    }

    /**
     * Llamado por Channel cuando la conexión se cierra o falla.
     * Aquí lanzamos un hilo que intenta reconectar.
     */
    private void onChannelDisconnected() {
        System.out.println("Channel desconectado. Intentando reconectar...");

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(2000); // pequeña espera antes de reintentar
            } catch (InterruptedException ignored) {}

            if (isClient && playerConnector != null) {
                playerConnector.reconnect();
            } else if (!isClient && serverConnector != null) {
                serverConnector.relisten();
            }
        });

        t.setDaemon(true);
        t.start();
    }

    /** Enviar pelota al otro extremo */
    public void sendBall(BallDTO ball) {
        if (channel != null) {
            channel.sendBall(ball);
        }
    }
}
