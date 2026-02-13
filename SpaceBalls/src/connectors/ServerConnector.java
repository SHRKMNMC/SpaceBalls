package connectors;

import model.BallDTO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class ServerConnector implements Runnable {

    private final int port;
    private CommunicationController communication;
    private final Consumer<BallDTO> onBallReceived;

    public ServerConnector(int port, Consumer<BallDTO> onBallReceived) {
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
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor esperando en puerto " + port + "...");
            Socket client = serverSocket.accept();
            System.out.println("Cliente conectado.");

            communication = new CommunicationController(client, onBallReceived);
            communication.start();

        } catch (IOException e) {
            System.err.println("Error en servidor: " + e.getMessage());
        }
    }

    public void sendBall(BallDTO ball) {
        if (communication != null) {
            communication.sendBall(ball);
        }
    }
}