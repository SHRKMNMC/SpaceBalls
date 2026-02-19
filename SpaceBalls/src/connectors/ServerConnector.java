package connectors;

import model.BallDTO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Servidor que espera a un cliente.
 * Cuando se conecta, crea un CommunicationController.
 */
public class ServerConnector implements Runnable {

    private final int listenPort;

    private CommunicationController communicationController;

    /** Callback para recibir pelotas desde red */
    private final Consumer<BallDTO> onBallReceived;

    public ServerConnector(int port, Consumer<BallDTO> onBallReceived) {
        this.listenPort = port;
        this.onBallReceived = onBallReceived;
    }

    /** Inicia el hilo del servidor */
    public void start() {
        Thread serverThread = new Thread(this);
        serverThread.setDaemon(true);
        serverThread.start();
    }

    /**
     * Espera un cliente y establece comunicación.
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(listenPort)) {

            System.out.println("Servidor esperando en puerto " + listenPort + "...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado.");

            communicationController = new CommunicationController(clientSocket, onBallReceived);
            communicationController.start();

        } catch (IOException e) {
            System.err.println("Error en servidor: " + e.getMessage());
        }
    }

    /** Envía pelota al cliente */
    public void sendBall(BallDTO ball) {
        if (communicationController != null) {
            communicationController.sendBall(ball);
        }
    }
}
