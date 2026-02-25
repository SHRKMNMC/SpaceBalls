package controller;

import world.Decoration;
import java.awt.image.BufferedImage;
import java.util.List;

public interface WorldGen {
    void setWorldBackground(BufferedImage bg);
    void setWorldDecorations(List<Decoration> decorations);
}
