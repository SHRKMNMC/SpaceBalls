package master;

import connectors.CommunicationController;
import connectors.PlayerConnector;
import connectors.ServerConnector;
import controller.Controller;
import model.BallDTO;
import world.WorldGenerator;
import world.LifeGenerator;

/**
 * Controlador maestro que coordina:
 * - Comunicación en red
 * - Generación del mundo (fondo + decoraciones)
 * - Generación de vida (bolas automáticas)
 */
public class MasterController {

    private final Controller localController;

    private final CommunicationController communicationController;
    private ServerConnector serverConnector;
    private PlayerConnector playerConnector;

    private WorldGenerator worldGenerator;

    private LifeGenerator lifeGenerator;
    private Thread lifeThread;

    // Intervalo interno del LifeGenerator (modificable SOLO desde código)
    private int lifeIntervalSeconds = 5;

    public MasterController(Controller controller) {
        this.localController = controller;
        this.communicationController = new CommunicationController(this::onBallReceived);
    }

    // ============================================================
    // WORLD GENERATION
    // ============================================================

    public void generateWorld(int width, int height) {
        worldGenerator = new WorldGenerator(localController);
        worldGenerator.generateWorld(width, height, 10);
    }

    // ============================================================
    // LIFE GENERATOR
    // ============================================================

    /** Inicia el generador de vida con el intervalo interno. */
    public void startLifeGenerator() {
        if (lifeGenerator != null) return;

        lifeGenerator = new LifeGenerator(localController, lifeIntervalSeconds);
        lifeThread = new Thread(lifeGenerator);
        lifeThread.setDaemon(true);
        lifeThread.start();

        System.out.println("LifeGenerator iniciado.");
    }

    /** Detiene el generador de vida. */
    public void stopLifeGenerator() {
        if (lifeGenerator != null) {
            lifeGenerator.stop();
            lifeGenerator = null;
            System.out.println("LifeGenerator detenido.");
        }
    }

    /** Saber si está activo. */
    public boolean isLifeGeneratorRunning() {
        return lifeGenerator != null;
    }

    // ============================================================
    // NETWORK
    // ============================================================

    public void startAsServer(int port) {
        serverConnector = new ServerConnector(port, communicationController);
        communicationController.registerServerConnector(serverConnector);
        serverConnector.start();
    }

    public void startAsClient(String host, int port) {
        playerConnector = new PlayerConnector(host, port, communicationController);
        communicationController.registerPlayerConnector(playerConnector);
        playerConnector.start();
    }

    private void onBallReceived(BallDTO ball) {
        System.out.println("Pelota recibida desde red.");
        localController.onReceiveBall(ball);
    }

    public void sendBall(BallDTO ball) {
        communicationController.sendBall(ball);
    }
}
