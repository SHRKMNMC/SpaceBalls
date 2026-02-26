package connectors;

import model.BallDTO;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Canal de comunicación sobre un Socket.
 * Gestiona ObjectInputStream/ObjectOutputStream, recepción de pelotas y mensajes de salud.
 */
public class Channel implements Runnable {

    private final Socket socket;

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    /** Callback cuando llega una pelota */
    private final Consumer<BallDTO> onBallReceived;

    /** Callback cuando llega un mensaje de salud (PING) */
    private final Runnable onHealthReceived;

    /** Callback cuando el canal se cierra o falla */
    private final Runnable onDisconnect;

    public Channel(Socket socket,
                   Consumer<BallDTO> onBallReceived,
                   Runnable onHealthReceived,
                   Runnable onDisconnect) {
        this.socket = socket;
        this.onBallReceived = onBallReceived;
        this.onHealthReceived = onHealthReceived;
        this.onDisconnect = onDisconnect;
    }

    /** Inicializa streams y lanza el hilo de escucha */
    public void start() {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream  = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error creando streams: " + e.getMessage());
            onDisconnect.run();
            return;
        }

        Thread listenerThread = new Thread(this);
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    /** Hilo que escucha mensajes entrantes */
    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                Object received = inputStream.readObject();

                if (received instanceof BallDTO ball) {
                    onBallReceived.accept(ball);
                } else if (received instanceof String msg && "PING".equals(msg)) {
                    onHealthReceived.run();
                }
            }
        } catch (Exception e) {
            System.out.println("Conexión cerrada en Channel.");
        } finally {
            onDisconnect.run();
        }
    }

    /** Envía una pelota al otro extremo */
    public synchronized void sendBall(BallDTO ball) {
        try {
            outputStream.writeObject(ball);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Error enviando pelota: " + e.getMessage());
        }
    }

    /** Envía un mensaje de salud (PING) al otro extremo */
    public synchronized void sendPing() {
        try {
            outputStream.writeObject("PING");
            outputStream.flush();
        } catch (IOException ignored) {}
    }

    /** Cierra el socket */
    public void close() {
        try { socket.close(); } catch (IOException ignored) {}
    }
}
