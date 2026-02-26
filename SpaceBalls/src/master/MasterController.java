package master;

import connectors.CommunicationController;
import connectors.PlayerConnector;
import connectors.ServerConnector;
import controller.Controller;
import model.BallDTO;

/**
 * Controlador maestro que gestiona la comunicación en red.
 * Puede actuar como servidor o cliente.
 */
public class MasterController {

    private final Controller localController;

    private final CommunicationController communicationController;
    private ServerConnector serverConnector;
    private PlayerConnector playerConnector;

    public MasterController(Controller controller) {
        this.localController = controller;
        // CommunicationController centraliza la lógica de red
        this.communicationController = new CommunicationController(this::onBallReceived);
    }

    /** Inicia modo servidor */
    public void startAsServer(int port) {
        serverConnector = new ServerConnector(port, communicationController);
        communicationController.registerServerConnector(serverConnector);
        serverConnector.start();
    }

    /** Inicia modo cliente */
    public void startAsClient(String host, int port) {
        playerConnector = new PlayerConnector(host, port, communicationController);
        communicationController.registerPlayerConnector(playerConnector);
        playerConnector.start();
    }

    /** Callback cuando llega una pelota desde red */
    private void onBallReceived(BallDTO ball) {
        System.out.println("Pelota recibida desde red.");
        localController.onReceiveBall(ball);
    }

    /** Enviar pelota al otro jugador */
    public void sendBall(BallDTO ball) {
        communicationController.sendBall(ball);
    }
}
