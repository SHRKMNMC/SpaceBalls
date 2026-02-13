package connectors;

import model.BallDTO;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class CommunicationController implements Runnable {

    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private final Consumer<BallDTO> onBallReceived;

    public CommunicationController(Socket socket, Consumer<BallDTO> onBallReceived) {
        this.socket = socket;
        this.onBallReceived = onBallReceived;
    }

    public void start() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in  = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error creando streams: " + e.getMessage());
            return;
        }

        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                Object obj = in.readObject();
                if (obj instanceof BallDTO ball) {
                    onBallReceived.accept(ball);
                }
            }
        } catch (Exception e) {
            System.out.println("Conexión cerrada.");
        }
    }

    public synchronized void sendBall(BallDTO ball) {
        try {
            out.writeObject(ball);
            out.flush();
        } catch (IOException e) {
            System.err.println("Error enviando pelota: " + e.getMessage());
        }
    }

    public void close() {
        try { socket.close(); } catch (IOException ignored) {}
    }
}