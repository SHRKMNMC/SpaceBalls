package view;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Reproduce una animación basada en un sprite sheet.
 */
public class SpriteAnimator {

    private final BufferedImage sheet;
    private final int rows, cols;
    private final int frameWidth, frameHeight;
    private final double frameDuration;

    private double elapsed = 0;
    private int frame = 0;
    private boolean playing = false;

    public SpriteAnimator(BufferedImage sheet, int rows, int cols, double frameDuration) {
        this.sheet = sheet;
        this.rows = rows;
        this.cols = cols;
        this.frameDuration = frameDuration;

        this.frameWidth = sheet.getWidth() / cols;
        this.frameHeight = sheet.getHeight() / rows;
    }

    /** Carga un sprite sheet desde recursos. */
    public static BufferedImage loadSheet(String path) {
        try {
            return ImageIO.read(SpriteAnimator.class.getResource(path));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Inicia la animación desde el principio. */
    public void playOnce() {
        if (!playing) {
            playing = true;
            frame = 0;
            elapsed = 0;
        }
    }

    /** Avanza la animación según el tiempo. */
    public void update(double dt) {
        if (!playing) return;

        elapsed += dt;
        if (elapsed >= frameDuration) {
            elapsed -= frameDuration;
            frame++;

            if (frame >= rows * cols) {
                frame = rows * cols - 1;
                playing = false;
            }
        }
    }

    /** Dibuja el frame actual a tamaño original. */
    public void draw(Graphics2D g, int x, int y) {
        if (!playing) return;

        int row = frame / cols;
        int col = frame % cols;

        int sx = col * frameWidth;
        int sy = row * frameHeight;

        g.drawImage(sheet,
                x, y, x + frameWidth, y + frameHeight,
                sx, sy, sx + frameWidth, sy + frameHeight,
                null);
    }

    /** 🔥 NUEVO: dibujar con tamaño personalizado (escalado) */
    public void draw(Graphics2D g, int x, int y, int w, int h) {
        if (!playing) return;

        int row = frame / cols;
        int col = frame % cols;

        int sx = col * frameWidth;
        int sy = row * frameHeight;

        g.drawImage(sheet,
                x, y, x + w, y + h,
                sx, sy, sx + frameWidth, sy + frameHeight,
                null);
    }

    public boolean isPlaying() { return playing; }
    public int getFrameWidth() { return frameWidth; }
    public int getFrameHeight() { return frameHeight; }
}
