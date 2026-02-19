package connectors;

import model.BallDTO;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Controla la comunicación bidireccional con un socket.
 * Envía y recibe BallDTO usando Object streams.
 */
public class CommunicationController implements Runnable {

    private final Socket socket;

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    /** Callback que se ejecuta cuando llega una pelota desde la red */
    private final Consumer<BallDTO> onBallReceived;

    public CommunicationController(Socket socket, Consumer<BallDTO> onBallReceived) {
        this.socket = socket;
        this.onBallReceived = onBallReceived;
    }

    /**
     * Inicializa streams y lanza el hilo de escucha.
     */
    public void start() {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream  = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error creando streams: " + e.getMessage());
            return;
        }

        Thread listenerThread = new Thread(this);
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    /**
     * Hilo que escucha mensajes entrantes.
     */
    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                Object received = inputStream.readObject();

                if (received instanceof BallDTO ball) {
                    onBallReceived.accept(ball);
                }
            }
        } catch (Exception e) {
            System.out.println("Conexión cerrada.");
        }
    }

    /**
     * Envía una pelota al otro extremo.
     */
    public synchronized void sendBall(BallDTO ball) {
        try {
            outputStream.writeObject(ball);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Error enviando pelota: " + e.getMessage());
        }
    }

    /** Cierra el socket */
    public void close() {
        try { socket.close(); } catch (IOException ignored) {}
    }
}
