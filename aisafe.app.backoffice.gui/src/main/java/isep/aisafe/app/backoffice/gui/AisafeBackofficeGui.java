package isep.aisafe.app.backoffice.gui;

import com.formdev.flatlaf.FlatLightLaf;
import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.domain.model.PlainTextEncoder;
import isep.aisafe.app.backoffice.console.AisafeBootstrapper;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.usermanagement.domain.AisafePasswordPolicy;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;

public final class AisafeBackofficeGui {

    private AisafeBackofficeGui() {
    }

    public static void main(final String[] args) {
        AuthzRegistry.configure(
                PersistenceContext.repositories().users(),
                new AisafePasswordPolicy(),
                new PlainTextEncoder()
        );
        AisafeBootstrapper.bootstrap();

        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.selectionBackground", new Color(219, 234, 254));
            UIManager.put("Table.selectionForeground", new Color(15, 23, 42));
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.trackArc", 999);
            new MainWindow().setVisible(true);
        });
    }
}
