package world;

import controller.WorldGen;
import master.MasterController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenerator {

    private final WorldGen worldGen;
    private final MasterController master;
    private final Random random = new Random();

    private final List<BufferedImage> backgrounds = new ArrayList<>();
    private final List<BufferedImage> decorationImages = new ArrayList<>();

    public WorldGenerator(WorldGen worldGen, MasterController master) {
        this.worldGen = worldGen;
        this.master = master;
        loadResources();
    }

    private void loadResources() {
        try {
            for (int i = 1; i <= 5; i++)
                backgrounds.add(ImageIO.read(getClass().getResource("/backgrounds/bg" + i + ".jpg")));

            for (int i = 1; i <= 5; i++)
                decorationImages.add(ImageIO.read(getClass().getResource("/decorations/dec" + i + ".png")));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage scaleImage(BufferedImage src, double scale) {
        int w = (int) (src.getWidth() * scale);
        int h = (int) (src.getHeight() * scale);

        BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        return resized;
    }

    public void generateWorld(int width, int height, int decorationCount) {

        BufferedImage selectedBackground = backgrounds.get(random.nextInt(backgrounds.size()));

        List<Decoration> decorations = new ArrayList<>();

        for (int i = 0; i < decorationCount; i++) {

            BufferedImage img = decorationImages.get(random.nextInt(decorationImages.size()));
            BufferedImage smallImg = scaleImage(img, 0.20);

            int x = random.nextInt(Math.max(1, width - smallImg.getWidth()));
            int y = random.nextInt(Math.max(1, height - smallImg.getHeight()));

            decorations.add(new Decoration(x, y, smallImg));
        }

        // Usar interfaz
        worldGen.setWorldBackground(selectedBackground);
        worldGen.setWorldDecorations(decorations);

        // Avisar al master si quieres
        master.onWorldGenerated(selectedBackground, decorations);
    }
}
