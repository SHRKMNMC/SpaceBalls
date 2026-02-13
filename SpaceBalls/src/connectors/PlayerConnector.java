package connectors;

import model.BallDTO;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

public class PlayerConnector implements Runnable {

    private final String host;
    private final int port;
    private CommunicationController communication;
    private final Consumer<BallDTO> onBallReceived;

    public PlayerConnector(String host, int port, Consumer<BallDTO> onBallReceived) {
        this.host = host;
        this.port = port;
        this.onBallReceived = onBallReceived;
    }

    public void start() {
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void run() {
        try {
            System.out.println("Conectando a servidor " + host + ":" + port);
            Socket socket = new Socket(host, port);
            System.out.println("Conectado al servidor.");

            communication = new CommunicationController(socket, onBallReceived);
            communication.start();

        } catch (IOException e) {
            System.err.println("Error conectando al servidor: " + e.getMessage());
        }
    }

    public void sendBall(BallDTO ball) {
        if (communication != null) {
            communication.sendBall(ball);
        }
    }
}