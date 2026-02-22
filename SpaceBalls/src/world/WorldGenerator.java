package world;

import controller.Controller;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenerator {

    private final Controller controller;
    private final Random random = new Random();

    private final List<BufferedImage> backgrounds = new ArrayList<>();
    private final List<BufferedImage> decorationImages = new ArrayList<>();

    public WorldGenerator(Controller controller) {
        this.controller = controller;
        loadResources();
    }

    /** Cargar imágenes desde resources */
    private void loadResources() {
        try {
            // 5 fondos
            for (int i = 1; i <= 5; i++) {
                backgrounds.add(ImageIO.read(
                        getClass().getResource("/backgrounds/bg" + i + ".jpg")));
            }

            // 5 adornos
            for (int i = 1; i <= 5; i++) {
                decorationImages.add(ImageIO.read(
                        getClass().getResource("/decorations/dec" + i + ".png")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Escalar imagen */
    private BufferedImage scaleImage(BufferedImage src, double scale) {
        int w = (int) (src.getWidth() * scale);
        int h = (int) (src.getHeight() * scale);

        BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();

        return resized;
    }

    /** Generar mundo aleatorio */
    public void generateWorld(int width, int height, int decorationCount) {

        // Fondo aleatorio
        BufferedImage selectedBackground = backgrounds.get(random.nextInt(backgrounds.size()));

        // Generar adornos
        List<Decoration> decorations = new ArrayList<>();

        for (int i = 0; i < decorationCount; i++) {

            // Imagen original
            BufferedImage img = decorationImages.get(random.nextInt(decorationImages.size()));

            // Escalar a tamaño pequeño (20%)
            BufferedImage smallImg = scaleImage(img, 0.20);

            // Posición aleatoria dentro del fondo
            int x = random.nextInt(Math.max(1, width - smallImg.getWidth()));
            int y = random.nextInt(Math.max(1, height - smallImg.getHeight()));

            decorations.add(new Decoration(x, y, smallImg));
        }

        // Enviar a la vista a través del controller
        controller.setWorldBackground(selectedBackground);
        controller.setWorldDecorations(decorations);
    }
}
