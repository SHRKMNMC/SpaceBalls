import controller.Controller;
import master.MasterController;
import model.Model;
import view.View;

import javax.swing.*;

/**
 * Punto de entrada del juego.
 * Inicializa MVC, genera el mundo
 * y pregunta el modo de red.
 */
public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // Crear modelo y vista
            Model gameModel = new Model();
            View gameView = new View();

            // Crear controlador principal
            Controller controller = new Controller(gameModel, gameView);
            gameView.setController(controller);

            // Crear controlador maestro
            MasterController masterController = new MasterController(controller);
            controller.setMasterController(masterController);

            // Inicializar interfaz y game loop
            controller.init();

            // ============================================================
            // GENERAR EL MUNDO (fondo + decoraciones)
            // ============================================================
            int w = gameView.getViewer().getWidth();
            int h = gameView.getViewer().getHeight();

            masterController.generateWorld(w, h);

            // ============================================================
            // IMPORTANTE:
            // YA NO INICIAMOS EL LIFE GENERATOR AQUÍ
            // Solo se inicia desde el botón del ControlPanel
            // ============================================================


            // ============================================================
            // PREGUNTAR MODO DE RED
            // ============================================================
            String[] options = {"Servidor", "Cliente"};

            int selection = JOptionPane.showOptionDialog(
                    null,
                    "¿Quieres ser servidor o cliente?",
                    "Modo de red",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (selection == 0) {
                // MODO SERVIDOR
                masterController.startAsServer(5000);
                JOptionPane.showMessageDialog(null,
                        "Servidor iniciado en puerto 5000.\nEsperando cliente...");
            } else {

                // MODO CLIENTE
                String ipAddress = JOptionPane.showInputDialog("IP del servidor:");

                if (ipAddress == null || ipAddress.isBlank()) {
                    JOptionPane.showMessageDialog(null,
                            "IP inválida. Cancelando conexión.");
                    return;
                }

                JOptionPane.showMessageDialog(null,
                        "Conectando a " + ipAddress + ":5000 ...");

                masterController.startAsClient(ipAddress.trim(), 5000);
            }
        });
    }
}
