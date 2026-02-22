package view;

import controller.Controller;
import world.Decoration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.List;

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

        controller.updateBounds(viewer.getWidth(), viewer.getHeight());

        viewer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                controller.updateBounds(viewer.getWidth(), viewer.getHeight());
            }
        });

        viewer.requestFocusInWindow();

        viewerThread = new Thread(viewer);
        viewerThread.setDaemon(true);
        viewerThread.start();
    }

    // ============================================================
    // WORLD VISUAL DATA
    // ============================================================

    public void setWorldBackground(BufferedImage bg) {
        viewer.setWorldBackground(bg);
    }

    public void setWorldDecorations(List<Decoration> decorations) {
        viewer.setWorldDecorations(decorations);
    }

    public void repaintViewer() {
        dataPanel.updateData();
    }
    public Viewer getViewer() {
        return viewer;
    }

}
