package view;

import controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Ventana principal del juego.
 * Ahora también arranca el hilo del Viewer.
 */
public class View extends JFrame {

    private Controller controller;
    private Viewer viewer;
    private DataPanel dataPanel;

    private Thread viewerThread;

    public View() {
        super("Juego MVC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /** Inicializa toda la interfaz */
    public void initUI() {
        viewer = new Viewer(controller);
        dataPanel = new DataPanel(controller);

        add(new ControlPanel(controller), BorderLayout.NORTH);
        add(viewer, BorderLayout.CENTER);
        add(dataPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(true);

        // Avisar al modelo del tamaño real del área de juego
        controller.updateBounds(viewer.getWidth(), viewer.getHeight());

        // Si cambia el tamaño, actualizar límites
        viewer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                controller.updateBounds(viewer.getWidth(), viewer.getHeight());
            }
        });

        viewer.requestFocusInWindow();

        // ============================================================
        // ARRANCAR EL HILO DEL VIEWER
        // ============================================================
        viewerThread = new Thread(viewer);
        viewerThread.setDaemon(true);
        viewerThread.start();
    }

    /** Ya no se usa para render, pero sí para actualizar datos */
    public void repaintViewer() {
        dataPanel.updateData();
    }
}
