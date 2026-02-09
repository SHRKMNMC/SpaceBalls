package view;

import controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Ventana principal del juego.
 * Contiene el Viewer, el panel de control y el panel de datos.
 */
public class View extends JFrame {

    private Controller controller;
    private Viewer viewer;
    private DataPanel dataPanel;

    public View() {
        super("Juego MVC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /** Inicializa toda la interfaz. */
    public void initUI() {
        viewer = new Viewer(controller);
        dataPanel = new DataPanel(controller);

        add(new ControlPanel(controller), BorderLayout.NORTH);
        add(viewer, BorderLayout.CENTER);
        add(dataPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(true); // ahora puede cambiar tamaño si quieres

        // 🔥 Avisar al controlador del tamaño REAL del viewer
        controller.updateBounds(viewer.getWidth(), viewer.getHeight());

        // 🔥 Si el viewer cambia de tamaño → avisar al controlador
        viewer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                controller.updateBounds(viewer.getWidth(), viewer.getHeight());
            }
        });

        viewer.requestFocusInWindow();
    }

    /** Llamado por el game loop para redibujar. */
    public void repaintViewer() {
        viewer.render();
        dataPanel.updateData();
    }
}
