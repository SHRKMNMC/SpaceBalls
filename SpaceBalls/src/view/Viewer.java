package view;

import controller.Controller;
import model.BallDTO;
import model.CollissionEvent;
import world.Decoration;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Viewer extends Canvas implements Runnable {

    private final Controller controller;

    private BufferedImage worldBackground;
    private List<Decoration> worldDecorations = new ArrayList<>();

    private final BufferedImage explosionSheet;
    private final List<ActiveAnimation> activeAnimations = new ArrayList<>();

    private BufferStrategy bufferStrategy;

    private boolean keyUp, keyDown, keyLeft, keyRight;
    private CollissionEvent lastAnimatedEvent = null;

    private volatile boolean running = false;

    public Viewer(Controller controller) {
        this.controller = controller;

        setPreferredSize(new Dimension(1000, 600));
        setBackground(Color.BLACK);
        setFocusable(true);

        explosionSheet = SpriteAnimator.loadSheet("/sprites/sprite2.png");

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { updateKey(e.getKeyCode(), true); }
            @Override public void keyReleased(KeyEvent e) { updateKey(e.getKeyCode(), false); }
        });
    }

    // ============================================================
    // WORLD VISUAL DATA
    // ============================================================

    public void setWorldBackground(BufferedImage bg) {
        this.worldBackground = bg;
    }

    public void setWorldDecorations(List<Decoration> decorations) {
        this.worldDecorations = decorations;
    }

    // ============================================================
    // INPUT
    // ============================================================

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

    // ============================================================
    // RENDER
    // ============================================================

    private void initBuffer() {
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();
    }

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

    private void drawScene(Graphics2D g) {

        // Fondo
        if (worldBackground != null) {
            g.drawImage(worldBackground, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Adornos
        for (Decoration d : worldDecorations) {
            g.drawImage(d.getImage(), d.getX(), d.getY(), null);
        }

        // Pelotas
        for (BallDTO ball : controller.getBallsSnapshot()) {
            g.setColor(ball.getColor());
            int diameter = ball.getRadius() * 2;
            g.fillOval(
                    (int) (ball.getX() - ball.getRadius()),
                    (int) (ball.getY() - ball.getRadius()),
                    diameter, diameter
            );
        }

        // Animaciones
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

    @Override
    public void run() {
        running = true;

        while (running) {
            render();
            try { Thread.sleep(16); } catch (InterruptedException ignored) {}
        }
    }

    public void stop() {
        running = false;
    }

    private static class ActiveAnimation {
        SpriteAnimator animator;
        Point position;

        ActiveAnimation(SpriteAnimator animator, Point position) {
            this.animator = animator;
            this.position = position;
        }
    }
}
