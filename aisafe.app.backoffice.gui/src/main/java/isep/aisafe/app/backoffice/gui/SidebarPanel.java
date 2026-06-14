package isep.aisafe.app.backoffice.gui;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

final class SidebarPanel extends JPanel {
    SidebarPanel() {
        super(new java.awt.BorderLayout());
        setBackground(UiTheme.NAVY);
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);
        final Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(96, 165, 250, 28));
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 6f, 9f }, 0));
        final Path2D path = new Path2D.Double();
        final int base = getHeight() - 300;
        path.moveTo(8, base + 56);
        path.curveTo(78, base + 32, 128, base + 78, getWidth() - 28, base - 8);
        g.draw(path);
        drawPlane(g, getWidth() - 44, base - 8, 28);
        g.dispose();
    }

    private void drawPlane(final Graphics2D g, final double cx, final double cy, final double s) {
        final Path2D plane = new Path2D.Double();
        plane.moveTo(cx - s * .42, cy + s * .05);
        plane.lineTo(cx + s * .42, cy - s * .18);
        plane.lineTo(cx + s * .12, cy + s * .10);
        plane.lineTo(cx + s * .32, cy + s * .36);
        plane.lineTo(cx, cy + s * .20);
        plane.lineTo(cx - s * .26, cy + s * .38);
        plane.lineTo(cx - s * .18, cy + s * .14);
        plane.closePath();
        g.fill(plane);
    }
}
