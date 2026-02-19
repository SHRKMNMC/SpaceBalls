package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Modelo principal del juego.
 * Gestiona:
 * - Pelotas
 * - Física
 * - Eventos de colisión
 * - Game loop
 */
public class Model {

    /** Lista sincronizada de pelotas activas */
    private final List<Ball> balls = Collections.synchronizedList(new ArrayList<>());

    /** Pelota controlada por el jugador */
    private ControlableBall playerBall;

    /** Sistema de eventos de colisión */
    private final EventDetector eventDetector = new EventDetector();

    /** Motor de física */
    private final Fisics physicsEngine = new SimpleFisics(this);

    /** Estado del game loop */
    private volatile boolean running = false;

    /** FPS calculados */
    private double currentFPS = 0.0;

    /** Límites del mundo */
    private Rectangle worldBounds = new Rectangle(0, 0, 1, 1);

    /** Dirección de movimiento del jugador */
    private int inputDirX = 0;
    private int inputDirY = 0;

    public EventDetector getEventDetector() { return eventDetector; }

    public void setBounds(int width, int height) {
        this.worldBounds = new Rectangle(0, 0, width, height);
    }

    /**
     * Inicia el game loop en un hilo propio.
     */
    public void startGameLoop(Runnable onFrameRendered) {
        if (running) return;
        running = true;

        Thread loopThread = new Thread(() -> {
            long lastTime = System.nanoTime();

            while (running) {
                long now = System.nanoTime();
                double deltaSeconds = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                update(deltaSeconds);
                onFrameRendered.run();

                currentFPS = 1.0 / Math.max(deltaSeconds, 1e-6);

                try { Thread.sleep(1); }
                catch (InterruptedException e) { running = false; }
            }
        });

        loopThread.setDaemon(true);
        loopThread.start();
    }

    /**
     * Actualiza física, movimiento del jugador y eventos.
     */
    private void update(double deltaSeconds) {
        synchronized (balls) {
            physicsEngine.update(balls, worldBounds, deltaSeconds);
        }

        if (playerBall != null)
            playerBall.move(inputDirX, inputDirY, deltaSeconds);

        eventDetector.update();
    }

    /** Crear pelota aleatoria */
    public void createRandomBall(int radius, int speed) {
        balls.add(Ball.randomBall(worldBounds, radius, speed));
    }

    /** Crear pelota controlable */
    public void createControlableBall(int radius, int speed) {
        playerBall = new ControlableBall(worldBounds.getCenterX(), worldBounds.getCenterY(), radius, speed, Color.WHITE);
        balls.add(playerBall);
    }

    /** Actualizar dirección del jugador */
    public void setControlDirection(int dx, int dy) {
        inputDirX = dx;
        inputDirY = dy;
    }

    /** Crear pelota recibida por red */
    public void createBallFromDTO(BallDTO dto) {
        Ball ball = new Ball(dto.getX(), dto.getY(), dto.getRadius(), dto.getVelX(), dto.getVelY(), dto.getColor());
        synchronized (balls) {
            balls.add(ball);
        }
    }

    /** Snapshot seguro para la vista */
    public List<BallDTO> getBallsSnapshot() {
        List<BallDTO> snapshot = new ArrayList<>();
        synchronized (balls) {
            for (Ball ball : balls) snapshot.add(ball.toDTO());
        }
        return snapshot;
    }

    public int getBallCount() { return balls.size(); }
    public double getCurrentFPS() { return currentFPS; }
}
