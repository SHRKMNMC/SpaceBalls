package view;

import controller.Controller;

import javax.swing.*;
import java.awt.*;

/**
 * Panel inferior que muestra FPS y número de pelotas.
 */
public class DataPanel extends JPanel {

    private final Controller controller;
    private final JLabel fpsLabel;
    private final JLabel countLabel;

    public DataPanel(Controller controller) {
        this.controller = controller;
        setLayout(new FlowLayout(FlowLayout.LEFT));

        fpsLabel = new JLabel("FPS: 0");
        countLabel = new JLabel("Pelotas: 0");

        add(fpsLabel);
        add(countLabel);
    }

    /** Actualiza los datos mostrados. */
    public void updateData() {
        fpsLabel.setText(String.format("FPS: %.1f", controller.getFPS()));
        countLabel.setText("Pelotas: " + controller.getBallCount());
    }
}
