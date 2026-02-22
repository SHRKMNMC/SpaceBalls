package world;

import java.awt.image.BufferedImage;

public class Decoration {

    private final int x;
    private final int y;
    private final BufferedImage image;

    public Decoration(int x, int y, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public BufferedImage getImage() { return image; }

    public int getWidth() { return image.getWidth(); }
    public int getHeight() { return image.getHeight(); }
}
