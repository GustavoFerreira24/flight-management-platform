package isep.aisafe.app.backoffice.gui;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

final class IconFactory {
    private IconFactory() {
    }

    static Icon brand(final int size, final Color color) {
        return new BrandIcon(size, color);
    }

    static Icon module(final String text, final int size, final Color color) {
        return new LineIcon(iconName(text), size, color);
    }

    static BufferedImage appImage() {
        final int size = 32;
        final BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        brand(size, UiTheme.BLUE).paintIcon(null, g, 0, 0);
        g.dispose();
        return image;
    }

    static String iconName(final String text) {
        final String value = text.toLowerCase();
        if (value.contains("consult weather")) return "search";
        if (value.contains("register weather")) return "plus";
        if (value.contains("import bulk weather")) return "upload";
        if (value.contains("weather")) return "cloud";
        if (value.contains("aircraft") || value.contains("flight")) return "plane";
        if (value.contains("report")) return "chart";
        if (value.contains("user") || value.contains("pilot") || value.contains("collaborator")) return "user";
        if (value.contains("airport") || value.contains("area") || value.contains("route")) return "map";
        if (value.contains("settings")) return "settings";
        if (value.contains("logout")) return "logout";
        return "grid";
    }
}
