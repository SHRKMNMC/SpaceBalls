package view;

import controller.Controller;
import model.BallDTO;
import model.CollissionEvent;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Lienzo principal donde se dibuja el juego.
 * Ahora implementa Runnable para poder ejecutarse en su propio hilo.
 */
public class Viewer extends Canvas implements Runnable {

    private final Controller controller;

    private final BufferedImage explosionSheet;
    private final List<ActiveAnimation> activeAnimations = new ArrayList<>();

    private BufferStrategy bufferStrategy;

    // Estado del teclado
    private boolean keyUp, keyDown, keyLeft, keyRight;

    // Para evitar repetir animaciones
    private CollissionEvent lastAnimatedEvent = null;

    // Control del hilo
    private volatile boolean running = false;

    public Viewer(Controller controller) {
        this.controller = controller;

        setPreferredSize(new Dimension(1000, 600));
        setBackground(Color.BLACK);
        setFocusable(true);

        explosionSheet = SpriteAnimator.loadSheet("/sprite2.png");

        // Captura de teclas
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { updateKey(e.getKeyCode(), true); }
            @Override public void keyReleased(KeyEvent e) { updateKey(e.getKeyCode(), false); }
        });
    }

    /** Estructura para animaciones activas */
    private static class ActiveAnimation {
        SpriteAnimator animator;
        Point position;

        ActiveAnimation(SpriteAnimator animator, Point position) {
            this.animator = animator;
            this.position = position;
        }
    }

    /** Actualiza dirección del jugador según teclas */
    private void updateMovementDirection() {
        int dx = (keyRight ? 1 : 0) - (keyLeft ? 1 : 0);
        int dy = (keyDown ? 1 : 0) - (keyUp ? 1 : 0);
        controller.setControlDirection(dx, dy);
    }

    private void updateKey(int key, boolean pressed) {
        switch (key) {
            case KeyEvent.VK_UP -> keyUp = pressed;
            case KeyEvent.VK_DOWN -> keyDown = pressed;
            case KeyEvent.VK_LEFT -> keyLeft = pressed;
            case KeyEvent.VK_RIGHT -> keyRight = pressed;
        }
        updateMovementDirection();
    }

    /** Inicializa doble buffer */
    private void initBuffer() {
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();
    }

    /** Render principal */
    public void render() {
        if (bufferStrategy == null) {
            initBuffer();
            return;
        }

        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

        drawScene(g);

        g.dispose();
        bufferStrategy.show();
        Toolkit.getDefaultToolkit().sync();
    }

    /** Dibuja pelotas y animaciones */
    private void drawScene(Graphics2D g) {

        // Fondo
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Dibujar pelotas
        for (BallDTO ball : controller.getBallsSnapshot()) {
            g.setColor(ball.getColor());
            int diameter = ball.getRadius() * 2;
            g.fillOval(
                    (int) (ball.getX() - ball.getRadius()),
                    (int) (ball.getY() - ball.getRadius()),
                    diameter, diameter
            );
        }

        // Detectar nuevo evento de colisión
        List<CollissionEvent> events = controller.getCollisionEvents();
        if (!events.isEmpty()) {
            CollissionEvent latest = events.get(events.size() - 1);

            if (latest != lastAnimatedEvent) {
                lastAnimatedEvent = latest;

                SpriteAnimator animator = new SpriteAnimator(explosionSheet, 4, 8, 0.03);
                animator.playOnce();

                activeAnimations.add(new ActiveAnimation(animator, new Point(latest.impactPoint)));
            }
        }

        // Dibujar animaciones activas
        for (int i = activeAnimations.size() - 1; i >= 0; i--) {
            ActiveAnimation anim = activeAnimations.get(i);

            if (!anim.animator.isPlaying()) {
                activeAnimations.remove(i);
                continue;
            }

            double scale = 0.5;
            int w = (int) (anim.animator.getFrameWidth() * scale);
            int h = (int) (anim.animator.getFrameHeight() * scale);

            anim.animator.draw(
                    g,
                    anim.position.x - w / 2,
                    anim.position.y - h / 2,
                    w, h
            );
        }
    }

    // ============================================================
    // HILO PROPIO DEL VIEWER
    // ============================================================

    @Override
    public void run() {
        running = true;

        while (running) {
            render();

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException ignored) {}
        }
    }

    public void stop() {
        running = false;
    }
}
