package model;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Física de pelotas:
 * - Usa SIEMPRE coordenadas de CENTRO (x, y)
 * - Rebote elástico limpio
 * - Corrección de penetración suave
 * - Intercambio real de fuerzas
 * - Evento de colisión solo una vez
 */
public class SimpleFisics implements Fisics {

    private final Model model;

    // Para evitar animaciones repetidas
    private final Set<String> colisionesPrevias = new HashSet<>();

    // Rebote (1 = perfecto, 0.9 = un poco amortiguado)
    private static final double REBOTE = 0.95;

    public SimpleFisics(Model model) {
        this.model = model;
    }

    @Override
    public void update(List<Ball> balls, Rectangle bounds, double dt) {

        // Mover y rebotar contra paredes
        for (Ball ball : balls) {
            mover(ball, bounds, dt);
        }

        // Colisiones entre pelotas
        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                resolverChoque(balls.get(i), balls.get(j), i, j);
            }
        }
    }

    // ----------------------------------------------------
    // MOVIMIENTO Y REBOTE CON PAREDES (x,y = CENTRO)
    // ----------------------------------------------------
    private void mover(Ball ball, Rectangle bounds, double dt) {

        double x = ball.getX() + ball.getVelX() * dt;
        double y = ball.getY() + ball.getVelY() * dt;
        int r = ball.getRadius();

        // Rebote horizontal (pared izquierda y derecha)
        if (x - r < bounds.x) {
            x = bounds.x + r;
            ball.setVelX(-ball.getVelX());
        } else if (x + r > bounds.x + bounds.width) {
            x = bounds.x + bounds.width - r;
            ball.setVelX(-ball.getVelX());
        }

        // Rebote vertical (pared superior e inferior)
        if (y - r < bounds.y) {
            y = bounds.y + r;
            ball.setVelY(-ball.getVelY());
        } else if (y + r > bounds.y + bounds.height) {
            y = bounds.y + bounds.height - r;
            ball.setVelY(-ball.getVelY());
        }

        ball.setX(x);
        ball.setY(y);
    }

    // ----------------------------------------------------
    // COLISIONES ENTRE PELOTAS (x,y = CENTRO)
    // ----------------------------------------------------
    private void resolverChoque(Ball a, Ball b, int i, int j) {

        // Centros (ya son centros en el modelo)
        double ax = a.getX();
        double ay = a.getY();
        double bx = b.getX();
        double by = b.getY();

        // Vector entre centros
        double dx = bx - ax;
        double dy = by - ay;

        double distancia2 = dx * dx + dy * dy;
        if (distancia2 == 0) return;

        double distancia = Math.sqrt(distancia2);
        double distanciaMinima = a.getRadius() + b.getRadius();

        // No chocan
        if (distancia >= distanciaMinima) {
            colisionesPrevias.remove(i + "-" + j);
            return;
        }

        // Normal de choque (dirección del impacto)
        double normalX = dx / distancia;
        double normalY = dy / distancia;

        // ------------------------------------------------
        // 1) Evento de colisión (solo una vez)
        // ------------------------------------------------
        String id = i + "-" + j;
        if (!colisionesPrevias.contains(id)) {
            colisionesPrevias.add(id);

            int impactoX = (int) (ax + normalX * a.getRadius());
            int impactoY = (int) (ay + normalY * a.getRadius());
            model.getEventDetector().registerCollision(new Point(impactoX, impactoY));
        }

        // ------------------------------------------------
        // 2) Corrección de penetración (evita enganches)
        // ------------------------------------------------
        double penetracion = distanciaMinima - distancia;

        double porcentaje = 0.8; // 80% de corrección
        double tolerancia = 0.01;

        double correccion = Math.max(penetracion - tolerancia, 0) * porcentaje / 2.0;

        a.setX(a.getX() - normalX * correccion);
        a.setY(a.getY() - normalY * correccion);
        b.setX(b.getX() + normalX * correccion);
        b.setY(b.getY() + normalY * correccion);

        // ------------------------------------------------
        // 3) Rebote elástico (intercambio real de fuerzas)
        // ------------------------------------------------
        double relVelX = b.getVelX() - a.getVelX();
        double relVelY = b.getVelY() - a.getVelY();

        // Velocidad relativa proyectada en la normal
        double velocidadNormal = relVelX * normalX + relVelY * normalY;

        // Si ya se separan → no rebote
        if (velocidadNormal > 0) return;

        // Impulso (masas iguales → dividir entre 2)
        double impulso = -(1 + REBOTE) * velocidadNormal / 2.0;

        double impulsoX = impulso * normalX;
        double impulsoY = impulso * normalY;

        // Aplicar impulso
        a.setVelX(a.getVelX() - impulsoX);
        a.setVelY(a.getVelY() - impulsoY);

        b.setVelX(b.getVelX() + impulsoX);
        b.setVelY(b.getVelY() + impulsoY);
    }
}
