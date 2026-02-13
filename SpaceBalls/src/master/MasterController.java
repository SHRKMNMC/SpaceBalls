package master;

import connectors.PlayerConnector;
import connectors.ServerConnector;
import controller.Controller;
import model.BallDTO;

public class MasterController {

    private final Controller localController;

    private ServerConnector serverConnector;
    private PlayerConnector playerConnector;

    public MasterController(Controller controller) {
        this.localController = controller;
    }

    /** Modo servidor */
    public void startAsServer(int port) {
        serverConnector = new ServerConnector(port, this::onBallReceived);
        serverConnector.start();
    }

    /** Modo cliente */
    public void startAsClient(String host, int port) {
        playerConnector = new PlayerConnector(host, port, this::onBallReceived);
        playerConnector.start();
    }

    /** Cuando llega una pelota desde la red */
    private void onBallReceived(BallDTO ball) {
        System.out.println("Pelota recibida desde red.");
        localController.onReceiveBall(ball);
    }

    /** Enviar pelota al otro jugador */
    public void sendBall(BallDTO ball) {
        if (serverConnector != null) serverConnector.sendBall(ball);
        if (playerConnector != null) playerConnector.sendBall(ball);
    }
}
