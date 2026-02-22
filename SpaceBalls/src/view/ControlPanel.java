package view;

import controller.Controller;
import master.MasterController;

import javax.swing.*;
import java.awt.*;

/**
 * Panel superior con controles para crear pelotas y controlar el LifeGenerator.
 */
public class ControlPanel extends JPanel {

    private final Controller controller;

    public ControlPanel(Controller controller) {
        this.controller = controller;

        setLayout(new FlowLayout(FlowLayout.LEFT));

        // Controles de tamaño y velocidad
        JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(20, 5, 100, 1));
        JSpinner speedSpinner = new JSpinner(new SpinnerNumberModel(150, 10, 500, 10));

        JButton createBallButton = new JButton("Añadir pelota");
        JButton createPlayerBallButton = new JButton("Pelota controlable");

        // Botón LifeGenerator
        JButton toggleLifeButton = new JButton("Iniciar LifeGen");

        add(new JLabel("Tamaño:"));
        add(sizeSpinner);
        add(new JLabel("Velocidad:"));
        add(speedSpinner);
        add(createBallButton);
        add(createPlayerBallButton);

        add(toggleLifeButton);

        // Crear pelota normal
        createBallButton.addActionListener(e ->
                controller.onCreateBall((int) sizeSpinner.getValue(), (int) speedSpinner.getValue())
        );

        // Crear pelota controlable
        createPlayerBallButton.addActionListener(e ->
                controller.onCreateControlableBall((int) sizeSpinner.getValue(), (int) speedSpinner.getValue())
        );

        // ============================================================
        // CONTROL DEL LIFE GENERATOR
        // ============================================================
        toggleLifeButton.addActionListener(e -> {
            MasterController mc = controller.getMasterController();

            if (mc.isLifeGeneratorRunning()) {
                mc.stopLifeGenerator();
                toggleLifeButton.setText("Iniciar LifeGen");
            } else {
                mc.startLifeGenerator();
                toggleLifeButton.setText("Detener LifeGen");
            }
        });
    }
}
