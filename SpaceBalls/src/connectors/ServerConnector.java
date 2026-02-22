package connectors;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor que espera a un cliente.
 * Cuando se conecta, entrega el Socket al CommunicationController.
 */
public class ServerConnector implements Runnable {

    private final int listenPort;
    private final CommunicationController communicationController;

    public ServerConnector(int port, CommunicationController communicationController) {
        this.listenPort = port;
        this.communicationController = communicationController;
    }

    /** Inicia el hilo del servidor */
    public void start() {
        Thread serverThread = new Thread(this);
        serverThread.setDaemon(true);
        serverThread.start();
    }

    @Override
    public void run() {
        listenOnce();
    }

    /**
     * Espera un cliente y establece comunicación una vez.
     */
    private void listenOnce() {
        try (ServerSocket serverSocket = new ServerSocket(listenPort)) {
            System.out.println("Servidor esperando en puerto " + listenPort + "...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado.");

            communicationController.attachSocket(clientSocket);

        } catch (IOException e) {
            System.err.println("Error en servidor: " + e.getMessage());
        }
    }

    /**
     * Llamado por CommunicationController cuando el Channel se desconecta.
     * Vuelve a esperar a que un cliente se conecte.
     */
    public void relisten() {
        System.out.println("Esperando nueva conexión de cliente...");
        Thread t = new Thread(this::listenOnce);
        t.setDaemon(true);
        t.start();
    }
}
