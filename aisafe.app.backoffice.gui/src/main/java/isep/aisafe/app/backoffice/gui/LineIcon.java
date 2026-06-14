package isep.aisafe.app.backoffice.gui;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

final class LineIcon implements Icon {
    private final String type;
    private final int size;
    private final Color color;

    LineIcon(final String type, final int size, final Color color) {
        this.type = type;
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
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.translate(x, y);
        g.setColor(color);
        g.setStroke(new BasicStroke(Math.max(1.4f, size / 18f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        final double s = size;
        switch (type) {
            case "shieldPlane":
                new BrandIcon(size, color).paintIcon(c, g, 0, 0);
                break;
            case "shield":
                drawShield(g, s);
                break;
            case "plane":
                g.drawLine((int) (s * .18), (int) (s * .55), (int) (s * .82), (int) (s * .35));
                g.drawLine((int) (s * .43), (int) (s * .47), (int) (s * .30), (int) (s * .20));
                g.drawLine((int) (s * .52), (int) (s * .44), (int) (s * .75), (int) (s * .70));
                break;
            case "cloud":
                drawCloud(g, s);
                break;
            case "cloudSearch":
                drawCloud(g, s);
                g.drawOval((int) (s * .58), (int) (s * .56), (int) (s * .20), (int) (s * .20));
                g.drawLine((int) (s * .73), (int) (s * .72), (int) (s * .84), (int) (s * .83));
                break;
            case "cloudPlus":
                drawCloud(g, s);
                g.drawLine((int) (s * .68), (int) (s * .55), (int) (s * .68), (int) (s * .81));
                g.drawLine((int) (s * .55), (int) (s * .68), (int) (s * .81), (int) (s * .68));
                break;
            case "cloudUpload":
                drawCloud(g, s);
                g.drawLine((int) (s * .68), (int) (s * .82), (int) (s * .68), (int) (s * .55));
                g.drawLine((int) (s * .56), (int) (s * .66), (int) (s * .68), (int) (s * .54));
                g.drawLine((int) (s * .80), (int) (s * .66), (int) (s * .68), (int) (s * .54));
                break;
            case "search":
                g.drawOval((int) (s * .20), (int) (s * .18), (int) (s * .46), (int) (s * .46));
                g.drawLine((int) (s * .58), (int) (s * .58), (int) (s * .82), (int) (s * .82));
                break;
            case "plus":
                g.drawLine((int) (s * .50), (int) (s * .20), (int) (s * .50), (int) (s * .80));
                g.drawLine((int) (s * .20), (int) (s * .50), (int) (s * .80), (int) (s * .50));
                break;
            case "upload":
                g.drawLine((int) (s * .50), (int) (s * .78), (int) (s * .50), (int) (s * .24));
                g.drawLine((int) (s * .30), (int) (s * .44), (int) (s * .50), (int) (s * .24));
                g.drawLine((int) (s * .70), (int) (s * .44), (int) (s * .50), (int) (s * .24));
                g.drawLine((int) (s * .24), (int) (s * .82), (int) (s * .76), (int) (s * .82));
                break;
            case "chart":
                g.drawRoundRect((int) (s * .15), (int) (s * .15), (int) (s * .70), (int) (s * .70), 5, 5);
                g.drawLine((int) (s * .32), (int) (s * .70), (int) (s * .32), (int) (s * .50));
                g.drawLine((int) (s * .50), (int) (s * .70), (int) (s * .50), (int) (s * .32));
                g.drawLine((int) (s * .68), (int) (s * .70), (int) (s * .68), (int) (s * .42));
                break;
            case "user":
                g.drawOval((int) (s * .35), (int) (s * .18), (int) (s * .30), (int) (s * .30));
                g.drawArc((int) (s * .22), (int) (s * .50), (int) (s * .56), (int) (s * .42), 20, 140);
                break;
            case "mail":
                g.drawRoundRect((int) (s * .16), (int) (s * .26), (int) (s * .68), (int) (s * .50), 4, 4);
                g.drawLine((int) (s * .18), (int) (s * .30), (int) (s * .50), (int) (s * .55));
                g.drawLine((int) (s * .82), (int) (s * .30), (int) (s * .50), (int) (s * .55));
                break;
            case "lock":
                g.drawRoundRect((int) (s * .25), (int) (s * .45), (int) (s * .50), (int) (s * .36), 4, 4);
                g.drawArc((int) (s * .34), (int) (s * .18), (int) (s * .32), (int) (s * .42), 0, 180);
                break;
            case "info":
                g.drawOval((int) (s * .20), (int) (s * .20), (int) (s * .60), (int) (s * .60));
                g.drawLine((int) (s * .50), (int) (s * .46), (int) (s * .50), (int) (s * .66));
                g.fillOval((int) (s * .47), (int) (s * .32), (int) (s * .06), (int) (s * .06));
                break;
            case "check":
                g.drawOval((int) (s * .20), (int) (s * .20), (int) (s * .60), (int) (s * .60));
                g.drawLine((int) (s * .36), (int) (s * .51), (int) (s * .46), (int) (s * .62));
                g.drawLine((int) (s * .46), (int) (s * .62), (int) (s * .66), (int) (s * .40));
                break;
            case "warning":
                final Path2D triangle = new Path2D.Double();
                triangle.moveTo(s * .50, s * .18);
                triangle.lineTo(s * .82, s * .76);
                triangle.lineTo(s * .18, s * .76);
                triangle.closePath();
                g.draw(triangle);
                g.drawLine((int) (s * .50), (int) (s * .38), (int) (s * .50), (int) (s * .56));
                g.fillOval((int) (s * .47), (int) (s * .64), (int) (s * .06), (int) (s * .06));
                break;
            case "thermometer":
                g.drawRoundRect((int) (s * .43), (int) (s * .18), (int) (s * .14), (int) (s * .48), 8, 8);
                g.drawOval((int) (s * .35), (int) (s * .58), (int) (s * .30), (int) (s * .30));
                g.drawLine((int) (s * .50), (int) (s * .33), (int) (s * .50), (int) (s * .62));
                break;
            case "wind":
                g.drawArc((int) (s * .14), (int) (s * .24), (int) (s * .56), (int) (s * .26), 0, 210);
                g.drawLine((int) (s * .18), (int) (s * .50), (int) (s * .82), (int) (s * .50));
                g.drawArc((int) (s * .44), (int) (s * .48), (int) (s * .34), (int) (s * .26), -25, 220);
                g.drawLine((int) (s * .18), (int) (s * .68), (int) (s * .56), (int) (s * .68));
                break;
            case "compass":
                g.drawOval((int) (s * .18), (int) (s * .18), (int) (s * .64), (int) (s * .64));
                g.drawLine((int) (s * .50), (int) (s * .24), (int) (s * .50), (int) (s * .36));
                g.drawLine((int) (s * .50), (int) (s * .64), (int) (s * .50), (int) (s * .76));
                g.drawLine((int) (s * .24), (int) (s * .50), (int) (s * .36), (int) (s * .50));
                g.drawLine((int) (s * .64), (int) (s * .50), (int) (s * .76), (int) (s * .50));
                g.drawLine((int) (s * .50), (int) (s * .50), (int) (s * .66), (int) (s * .34));
                break;
            case "gauge":
                g.drawArc((int) (s * .20), (int) (s * .28), (int) (s * .60), (int) (s * .60), 0, 180);
                g.drawLine((int) (s * .50), (int) (s * .58), (int) (s * .68), (int) (s * .42));
                g.drawLine((int) (s * .28), (int) (s * .72), (int) (s * .72), (int) (s * .72));
                break;
            case "droplet":
                final Path2D drop = new Path2D.Double();
                drop.moveTo(s * .50, s * .16);
                drop.curveTo(s * .72, s * .42, s * .78, s * .56, s * .66, s * .72);
                drop.curveTo(s * .56, s * .86, s * .34, s * .82, s * .28, s * .66);
                drop.curveTo(s * .22, s * .52, s * .32, s * .36, s * .50, s * .16);
                drop.closePath();
                g.draw(drop);
                break;
            case "logout":
                g.drawLine((int) (s * .18), (int) (s * .20), (int) (s * .18), (int) (s * .80));
                g.drawLine((int) (s * .18), (int) (s * .20), (int) (s * .48), (int) (s * .20));
                g.drawLine((int) (s * .18), (int) (s * .80), (int) (s * .48), (int) (s * .80));
                g.drawLine((int) (s * .42), (int) (s * .50), (int) (s * .82), (int) (s * .50));
                g.drawLine((int) (s * .68), (int) (s * .36), (int) (s * .82), (int) (s * .50));
                g.drawLine((int) (s * .68), (int) (s * .64), (int) (s * .82), (int) (s * .50));
                break;
            case "map":
                g.drawRoundRect((int) (s * .18), (int) (s * .18), (int) (s * .64), (int) (s * .64), 5, 5);
                g.drawLine((int) (s * .40), (int) (s * .20), (int) (s * .40), (int) (s * .80));
                g.drawLine((int) (s * .60), (int) (s * .20), (int) (s * .60), (int) (s * .80));
                break;
            case "settings":
                drawGear(g, s);
                break;
            default:
                g.drawRoundRect((int) (s * .18), (int) (s * .18), (int) (s * .25), (int) (s * .25), 4, 4);
                g.drawRoundRect((int) (s * .57), (int) (s * .18), (int) (s * .25), (int) (s * .25), 4, 4);
                g.drawRoundRect((int) (s * .18), (int) (s * .57), (int) (s * .25), (int) (s * .25), 4, 4);
                g.drawRoundRect((int) (s * .57), (int) (s * .57), (int) (s * .25), (int) (s * .25), 4, 4);
                break;
        }
        g.dispose();
    }

    private void drawShield(final Graphics2D g, final double s) {
        final Path2D shield = new Path2D.Double();
        shield.moveTo(s * .50, s * .08);
        shield.lineTo(s * .82, s * .22);
        shield.lineTo(s * .76, s * .62);
        shield.quadTo(s * .50, s * .90, s * .24, s * .62);
        shield.lineTo(s * .18, s * .22);
        shield.closePath();
        g.draw(shield);
    }

    private void drawCloud(final Graphics2D g, final double s) {
        g.drawArc((int) (s * .18), (int) (s * .38), (int) (s * .28), (int) (s * .24), 90, 180);
        g.drawArc((int) (s * .34), (int) (s * .22), (int) (s * .32), (int) (s * .32), 35, 180);
        g.drawArc((int) (s * .55), (int) (s * .35), (int) (s * .26), (int) (s * .25), -20, 160);
        g.drawLine((int) (s * .28), (int) (s * .62), (int) (s * .76), (int) (s * .62));
    }

    private void drawGear(final Graphics2D g, final double s) {
        final Path2D gear = new Path2D.Double();
        for (int i = 0; i < 16; i++) {
            final double a = -Math.PI / 2 + Math.PI * 2 * i / 16.0;
            final double r = (i % 2 == 0) ? s * .39 : s * .30;
            final double x = s * .50 + Math.cos(a) * r;
            final double y = s * .50 + Math.sin(a) * r;
            if (i == 0) gear.moveTo(x, y); else gear.lineTo(x, y);
        }
        gear.closePath();
        g.draw(gear);
        g.drawOval((int) (s * .41), (int) (s * .41), (int) (s * .18), (int) (s * .18));
    }
}
