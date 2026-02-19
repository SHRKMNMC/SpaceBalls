package connectors;

import model.BallDTO;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Cliente que se conecta a un servidor remoto.
 * Una vez conectado, crea un CommunicationController.
 */
public class PlayerConnector implements Runnable {

    private final String serverHost;
    private final int serverPort;

    private CommunicationController communicationController;

    /** Callback para recibir pelotas desde red */
    private final Consumer<BallDTO> onBallReceived;

    public PlayerConnector(String host, int port, Consumer<BallDTO> onBallReceived) {
        this.serverHost = host;
        this.serverPort = port;
        this.onBallReceived = onBallReceived;
    }

    /** Inicia el hilo de conexión */
    public void start() {
        Thread connectionThread = new Thread(this);
        connectionThread.setDaemon(true);
        connectionThread.start();
    }

    /**
     * Intenta conectar al servidor y crear el controlador de comunicación.
     */
    @Override
    public void run() {
        try {
            System.out.println("Conectando a servidor " + serverHost + ":" + serverPort);
            Socket socket = new Socket(serverHost, serverPort);
            System.out.println("Conectado al servidor.");

            communicationController = new CommunicationController(socket, onBallReceived);
            communicationController.start();

        } catch (IOException e) {
            System.err.println("Error conectando al servidor: " + e.getMessage());
        }
    }

    /** Envía pelota al servidor */
    public void sendBall(BallDTO ball) {
        if (communicationController != null) {
            communicationController.sendBall(ball);
        }
    }
}
