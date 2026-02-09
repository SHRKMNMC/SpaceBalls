import controller.Controller;
import model.Model;
import view.View;

/**
 * Punto de entrada del juego.
 * Ensambla MVC y arranca la aplicación.
 */
public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            Model model = new Model();
            View view = new View();
            Controller controller = new Controller(model, view);

            view.setController(controller);
            controller.init();
        });
    }
}
