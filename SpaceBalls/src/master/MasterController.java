package master;

import connectors.CommunicationController;
import connectors.PlayerConnector;
import connectors.ServerConnector;
import controller.Controller;
import controller.Generator;
import controller.WorldGen;
import model.BallDTO;
import world.WorldGenerator;
import world.LifeGenerator;
import world.Decoration;

import java.awt.image.BufferedImage;
import java.util.List;

public class MasterController {

    private final Controller localController;

    private final CommunicationController communicationController;
    private ServerConnector serverConnector;
    private PlayerConnector playerConnector;

    private WorldGenerator worldGenerator;

    private LifeGenerator lifeGenerator;
    private Thread lifeThread;

    private int lifeIntervalSeconds = 5;

    public MasterController(Controller controller) {
        this.localController = controller;
        this.localController.setMasterController(this);
        this.communicationController = new CommunicationController(this::onBallReceived);
    }

    // ============================================================
    // INYECCIÓN DEL WORLD GENERATOR DESDE EL MAIN
    // ============================================================

    public void setWorldGenerator(WorldGenerator generator) {
        this.worldGenerator = generator;
    }

    public void generateWorld(int width, int height) {
        if (worldGenerator == null) {
            System.err.println("WorldGenerator no ha sido inyectado desde el Main.");
            return;
        }
        worldGenerator.generateWorld(width, height, 10);
    }

    public void onWorldGenerated(BufferedImage bg, List<Decoration> decorations) {
        // Hook opcional
    }

    // ============================================================
    // LIFE GENERATOR
    // ============================================================

    public void setLifeGenerator(LifeGenerator generator) {
        this.lifeGenerator = generator;
    }

    public void startLifeGenerator() {
        if (lifeGenerator == null) {
            System.err.println("LifeGenerator no ha sido inyectado desde el Main.");
            return;
        }

        if (lifeThread != null) return;

        lifeThread = new Thread(lifeGenerator);
        lifeThread.setDaemon(true);
        lifeThread.start();

        System.out.println("LifeGenerator iniciado.");
    }

    public void stopLifeGenerator() {
        if (lifeGenerator != null) {
            lifeGenerator.stop();
            lifeThread = null;
            System.out.println("LifeGenerator detenido.");
        }
    }

    public boolean isLifeGeneratorRunning() {
        return lifeThread != null;
    }

    public void onLifeGeneratorEvent(int radius, int speed) {
        // Hook opcional
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
