package view;

import controller.Controller;

import javax.swing.*;
import java.awt.*;

/**
 * Panel inferior que muestra FPS y número de pelotas activas.
 */
public class DataPanel extends JPanel {

    private final Controller controller;
    private final JLabel fpsLabel;
    private final JLabel ballCountLabel;

    public DataPanel(Controller controller) {
        this.controller = controller;

        setLayout(new FlowLayout(FlowLayout.LEFT));

        fpsLabel = new JLabel("FPS: 0");
        ballCountLabel = new JLabel("Pelotas: 0");

        add(fpsLabel);
        add(ballCountLabel);
    }

    /** Actualiza los datos mostrados cada frame */
    public void updateData() {
        fpsLabel.setText(String.format("FPS: %.1f", controller.getFPS()));
        ballCountLabel.setText("Pelotas: " + controller.getBallCount());
    }
}
