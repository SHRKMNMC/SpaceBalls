package view;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Controla una animación basada en un sprite sheet.
 * Implementa Runnable para ejecutarse en su propio hilo.
 */
public class SpriteAnimator implements Runnable {

    private final BufferedImage spriteSheet;
    private final int rows, columns;
    private final int frameWidth, frameHeight;
    private final double frameDurationSeconds;

    private volatile boolean playing = false;
    private volatile int currentFrame = 0;

    public SpriteAnimator(BufferedImage sheet, int rows, int columns, double frameDurationSeconds) {
        this.spriteSheet = sheet;
        this.rows = rows;
        this.columns = columns;
        this.frameDurationSeconds = frameDurationSeconds;

        this.frameWidth = sheet.getWidth() / columns;
        this.frameHeight = sheet.getHeight() / rows;
    }

    /** Cargar sprite sheet desde recursos */
    public static BufferedImage loadSheet(String path) {
        try {
            return ImageIO.read(SpriteAnimator.class.getResource(path));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Inicia la animación en un hilo propio */
    public synchronized void playOnce() {
        if (playing) return;

        playing = true;
        currentFrame = 0;

        Thread animationThread = new Thread(this);
        animationThread.setDaemon(true);
        animationThread.start();
    }

    /** Avanza los frames en un hilo independiente */
    @Override
    public void run() {
        int totalFrames = rows * columns;

        try {
            while (playing && currentFrame < totalFrames) {

                Thread.sleep((long) (frameDurationSeconds * 1000));

                synchronized (this) {
                    currentFrame++;
                    if (currentFrame >= totalFrames) {
                        playing = false;
                    }
                }
            }

        } catch (InterruptedException e) {
            playing = false;
        }
    }

    /** Dibuja el frame actual escalado */
    public void draw(Graphics2D g, int x, int y, int width, int height) {
        if (!playing) return;

        int frame;
        synchronized (this) {
            frame = currentFrame;
        }

        int row = frame / columns;
        int col = frame % columns;

        int srcX = col * frameWidth;
        int srcY = row * frameHeight;

        g.drawImage(spriteSheet,
                x, y, x + width, y + height,
                srcX, srcY, srcX + frameWidth, srcY + frameHeight,
                null);
    }

    public boolean isPlaying() { return playing; }
    public int getFrameWidth() { return frameWidth; }
    public int getFrameHeight() { return frameHeight; }
}
