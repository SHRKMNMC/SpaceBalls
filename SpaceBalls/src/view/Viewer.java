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
 */
public class Viewer extends Canvas {

    private final Controller controller;

    private final BufferedImage explosionSheet;
    private final List<ActiveAnimation> animations = new ArrayList<>();

    private BufferStrategy buffer;
    private long lastTime = System.nanoTime();

    private boolean up, down, left, right;

    // Para evitar animar el mismo evento dos veces
    private CollissionEvent lastAnimatedEvent = null;

    public Viewer(Controller controller) {
        this.controller = controller;

        setPreferredSize(new Dimension(1000, 600));
        setBackground(Color.BLACK);
        setFocusable(true);

        explosionSheet = SpriteAnimator.loadSheet("/sprite2.png");

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { setKey(e.getKeyCode(), true); }
            @Override public void keyReleased(KeyEvent e) { setKey(e.getKeyCode(), false); }
        });
    }

    private static class ActiveAnimation {
        SpriteAnimator anim;
        Point pos;

        ActiveAnimation(SpriteAnimator anim, Point pos) {
            this.anim = anim;
            this.pos = pos;
        }
    }

    private void updateDirection() {
        int dx = (right ? 1 : 0) - (left ? 1 : 0);
        int dy = (down ? 1 : 0) - (up ? 1 : 0);
        controller.setControlDirection(dx, dy);
    }

    private void setKey(int key, boolean pressed) {
        switch (key) {
            case KeyEvent.VK_UP -> up = pressed;
            case KeyEvent.VK_DOWN -> down = pressed;
            case KeyEvent.VK_LEFT -> left = pressed;
            case KeyEvent.VK_RIGHT -> right = pressed;
        }
        updateDirection();
    }

    private void initBuffer() {
        createBufferStrategy(2);
        buffer = getBufferStrategy();
    }

    public void render() {
        if (buffer == null) {
            initBuffer();
            return;
        }

        Graphics2D g = (Graphics2D) buffer.getDrawGraphics();

        long now = System.nanoTime();
        double dt = (now - lastTime) / 1_000_000_000.0;
        lastTime = now;

        drawScene(g, dt);

        g.dispose();
        buffer.show();
        Toolkit.getDefaultToolkit().sync();
    }

    private void drawScene(Graphics2D g, double dt) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Dibujar pelotas
        for (BallDTO b : controller.getBallsSnapshot()) {
            g.setColor(b.getColor());
            int d = b.getRadius() * 2;
            g.fillOval((int) (b.getX() - b.getRadius()),
                    (int) (b.getY() - b.getRadius()),
                    d, d);
        }

        // Obtener último evento
        List<CollissionEvent> events = controller.getCollisionEvents();
        if (!events.isEmpty()) {
            CollissionEvent last = events.get(events.size() - 1);

            if (last != lastAnimatedEvent) {
                lastAnimatedEvent = last;

                SpriteAnimator anim = new SpriteAnimator(explosionSheet, 4, 8, 0.03);
                anim.playOnce();

                animations.add(new ActiveAnimation(anim, new Point(last.position)));
            }
        }

        // Actualizar y dibujar animaciones
        for (int i = animations.size() - 1; i >= 0; i--) {
            ActiveAnimation a = animations.get(i);
            a.anim.update(dt);

            if (!a.anim.isPlaying()) {
                animations.remove(i);
                continue;
            }

            int fw = a.anim.getFrameWidth();
            int fh = a.anim.getFrameHeight();

            // ESCALA DE LA EXPLOSIÓN (ajustable)
            double scale = 0.5; // 50% del tamaño original

            int drawW = (int) (fw * scale);
            int drawH = (int) (fh * scale);

            a.anim.draw(g,
                    a.pos.x - drawW / 2,
                    a.pos.y - drawH / 2,
                    drawW,
                    drawH
            );
        }
    }
}
