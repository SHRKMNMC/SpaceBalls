package view;

import controller.Controller;

import javax.swing.*;
import java.awt.*;

/**
 * Panel superior con controles para crear pelotas.
 */
public class ControlPanel extends JPanel {

    private final Controller controller;

    public ControlPanel(Controller controller) {
        this.controller = controller;
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JSpinner size = new JSpinner(new SpinnerNumberModel(20, 5, 100, 1));
        JSpinner speed = new JSpinner(new SpinnerNumberModel(150, 10, 500, 10));

        JButton addBall = new JButton("Añadir pelota");
        JButton addControl = new JButton("Pelota controlable");

        add(new JLabel("Tamaño:"));
        add(size);
        add(new JLabel("Velocidad:"));
        add(speed);
        add(addBall);
        add(addControl);

        addBall.addActionListener(e ->
                controller.onCreateBall((int) size.getValue(), (int) speed.getValue())
        );

        addControl.addActionListener(e ->
                controller.onCreateControlableBall((int) size.getValue(), (int) speed.getValue())
        );
    }
}
