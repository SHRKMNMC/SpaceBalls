package connectors;

import java.io.IOException;
import java.net.Socket;

/**
 * Cliente que se conecta a un servidor remoto.
 * Cuando se conecta, entrega el Socket al CommunicationController.
 */
public class PlayerConnector implements Runnable {

    private final String serverHost;
    private final int serverPort;

    private final CommunicationController communicationController;

    public PlayerConnector(String host, int port, CommunicationController communicationController) {
        this.serverHost = host;
        this.serverPort = port;
        this.communicationController = communicationController;
    }

    /** Inicia el hilo de conexión */
    public void start() {
        Thread connectionThread = new Thread(this);
        connectionThread.setDaemon(true);
        connectionThread.start();
    }

    @Override
    public void run() {
        connectWithRetry();
    }

    /**
     * Intenta conectar al servidor, reintentando cada pocos segundos si falla.
     */
    private void connectWithRetry() {
        while (true) {
            try {
                System.out.println("Conectando a servidor " + serverHost + ":" + serverPort);
                Socket socket = new Socket(serverHost, serverPort);
                System.out.println("Conectado al servidor.");

                communicationController.attachSocket(socket);
                break; // conexión establecida, salimos del bucle

            } catch (IOException e) {
                System.err.println("Error conectando al servidor: " + e.getMessage());
                try {
                    Thread.sleep(3000); // esperar antes de reintentar
                } catch (InterruptedException ignored) {}
            }
        }
    }

    /**
     * Llamado por CommunicationController cuando el Channel se desconecta.
     * Vuelve a intentar conectar al servidor.
     */
    public void reconnect() {
        System.out.println("Reintentando conexión con el servidor...");
        Thread t = new Thread(this::connectWithRetry);
        t.setDaemon(true);
        t.start();
    }
}
