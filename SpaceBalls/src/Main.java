import controller.Controller;
import master.MasterController;
import model.Model;
import view.View;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            Model model = new Model();
            View view = new View();
            Controller controller = new Controller(model, view);

            view.setController(controller);

            // Crear MasterController y conectarlo al Controller
            MasterController master = new MasterController(controller);
            controller.setMasterController(master);

            controller.init();

            // Preguntar modo
            String[] options = {"Servidor", "Cliente"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "¿Quieres ser servidor o cliente?",
                    "Modo de red",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                // MODO SERVIDOR
                master.startAsServer(5000);
                JOptionPane.showMessageDialog(null, "Servidor iniciado en puerto 5000.\nEsperando cliente...");
            } else {

                // MODO CLIENTE
                String ip = JOptionPane.showInputDialog("IP del servidor:");

                if (ip == null || ip.isBlank()) {
                    JOptionPane.showMessageDialog(null, "IP inválida. Cancelando conexión.");
                    return;
                }

                JOptionPane.showMessageDialog(null, "Conectando a " + ip + ":5000 ...");

                master.startAsClient(ip.trim(), 5000);
            }
        });
    }
}
