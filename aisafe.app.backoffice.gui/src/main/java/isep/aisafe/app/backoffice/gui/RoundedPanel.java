package isep.aisafe.app.backoffice.gui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

class RoundedPanel extends JPanel {
    private final int arc;
    private final Color fill;
    private final Color border;
    private final boolean shadow;

    RoundedPanel(final int arc, final Color fill) {
        this(arc, fill, null, true);
    }

    RoundedPanel(final int arc, final Color fill, final Color border, final boolean shadow) {
        this.arc = arc;
        this.fill = fill;
        this.border = border;
        this.shadow = shadow;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (shadow) {
            g.setColor(new Color(15, 23, 42, 13));
            g.fill(new RoundRectangle2D.Double(3, 5, getWidth() - 7, getHeight() - 8, arc, arc));
        }
        g.setColor(fill);
        g.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 6, getHeight() - 7, arc, arc));
        if (border != null) {
            g.setColor(border);
            g.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 6, getHeight() - 7, arc, arc));
        }
        g.dispose();
        super.paintComponent(graphics);
    }
}
