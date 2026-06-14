package isep.aisafe.app.backoffice.gui;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

final class BrandIcon implements Icon {
    private final int size;
    private final Color color;

    BrandIcon(final int size, final Color color) {
        this.size = size;
        this.color = color;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(final Component c, final Graphics graphics, final int x, final int y) {
        final Graphics2D g = (Graphics2D) graphics.create();

        g.translate(x, y);
        g.scale(size / 100.0, size / 100.0);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (size >= 42) {
            drawGlow(g);
        }

        drawShield(g);
        drawPlane(g);

        g.dispose();
    }

    private void drawGlow(final Graphics2D g) {
        final Path2D shield = shieldShape();

        g.setColor(new Color(37, 99, 235, 55));
        g.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(shield);

        g.setColor(new Color(96, 165, 250, 45));
        g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(shield);

        g.setColor(new Color(37, 99, 235, 35));
        g.fill(new Ellipse2D.Double(12, 46, 34, 34));

        g.setColor(new Color(96, 165, 250, 28));
        g.fill(new Ellipse2D.Double(58, 18, 26, 26));
    }

    private void drawShield(final Graphics2D g) {
        final Path2D shield = shieldShape();

        g.setColor(new Color(37, 99, 235, 28));
        g.fill(shield);

        g.setColor(color);
        g.setStroke(new BasicStroke(4.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(shield);
    }

    private Path2D shieldShape() {
        final Path2D shield = new Path2D.Double();

        shield.moveTo(50, 6);

        shield.curveTo(60, 13, 73, 18, 85, 22);

        shield.curveTo(84, 52, 76, 77, 50, 94);

        shield.curveTo(24, 77, 16, 52, 15, 22);

        shield.curveTo(27, 18, 40, 13, 50, 6);

        shield.closePath();
        return shield;
    }

    private void drawPlane(final Graphics2D g) {
        final Path2D plane = new Path2D.Double();

        /*
         * Avião pequeno e centrado.
         * Agora vamos só subi-lo ligeiramente no fim.
         */

        // Nose
        plane.moveTo(50, 31);

        // Upper fuselage
        plane.curveTo(53, 35, 53.5, 41, 52.5, 47);
        plane.lineTo(52.5, 51);

        // Right wing
        plane.lineTo(69, 61);
        plane.curveTo(72, 63, 71, 66, 67.5, 65.5);
        plane.lineTo(52.5, 58.5);

        // Lower fuselage
        plane.lineTo(52.5, 67);

        // Right tail wing
        plane.lineTo(59.5, 73);
        plane.curveTo(61.5, 75, 59.5, 77.5, 56.8, 76.2);
        plane.lineTo(50, 72.2);

        // Left tail wing
        plane.lineTo(43.2, 76.2);
        plane.curveTo(40.5, 77.5, 38.5, 75, 40.5, 73);
        plane.lineTo(47.5, 67);

        // Left wing root
        plane.lineTo(47.5, 58.5);

        // Left wing
        plane.lineTo(32.5, 65.5);
        plane.curveTo(29, 66, 28, 63, 31, 61);
        plane.lineTo(47.5, 51);

        // Close upper fuselage
        plane.lineTo(47.5, 47);
        plane.curveTo(46.5, 41, 47, 35, 50, 31);

        plane.closePath();

        // Sobe o avião um bocadinho
        final java.awt.Shape shiftedPlane =
                java.awt.geom.AffineTransform.getTranslateInstance(0, -7.0)
                        .createTransformedShape(plane);

        g.setColor(color);
        g.fill(shiftedPlane);
    }
}