package isep.aisafe.app.backoffice.gui;

import isep.aisafe.infrastructure.authz.AuthenticationCredentialHandler;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;

import static isep.aisafe.app.backoffice.gui.MainWindow.*;
import static isep.aisafe.app.backoffice.gui.UiTheme.*;

final class LoginPanel extends JPanel {

    LoginPanel(final Runnable onAuthenticated) {
        super(new BorderLayout());
        setBackground(new Color(248, 250, 252));

        final JPanel body = new JPanel(new BorderLayout());
        body.setBackground(new Color(248, 250, 252));
        add(body, BorderLayout.CENTER);

        final JPanel hero = new LoginHeroPanel();
        hero.setPreferredSize(new Dimension(470, 720));
        body.add(hero, BorderLayout.WEST);

        final JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(new Color(248, 250, 252));
        body.add(right, BorderLayout.CENTER);

        final JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        final GridBagConstraints outer = new GridBagConstraints();
        outer.weightx = 1;
        outer.weighty = 1;
        outer.anchor = GridBagConstraints.CENTER;
        right.add(wrapper, outer);

        final JPanel card = loginCard(onAuthenticated);
        final GridBagConstraints wc = new GridBagConstraints();
        wc.gridx = 0;
        wc.gridy = 0;
        wrapper.add(card, wc);
        wc.gridy++;
        wc.insets = new Insets(22, 0, 0, 0);
        wrapper.add(footer(), wc);
    }

    private JPanel loginCard(final Runnable onAuthenticated) {
        final JPanel card = new RoundedPanel(18, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(740, 575));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(58, 56, 34, 56)
        ));

        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 8, 0);

        final JLabel title = new JLabel("Welcome back");
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 36f));
        card.add(title, c);

        c.gridy++;
        c.insets = new Insets(0, 0, 30, 0);
        final JLabel subtitle = new JLabel("Sign in to continue");
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(21f));
        card.add(subtitle, c);

        c.gridy++;
        c.insets = new Insets(0, 0, 8, 0);
        card.add(fieldLabel("Email / Username"), c);

        c.gridy++;
        c.insets = new Insets(0, 0, 24, 0);
        final JTextField username = styledLoginTextField("name@aisafe.com or username", new LineIcon("mail", 18, MUTED));
        card.add(username, c);

        c.gridy++;
        c.insets = new Insets(0, 0, 8, 0);
        card.add(fieldLabel("Password"), c);

        c.gridy++;
        c.insets = new Insets(0, 0, 20, 0);
        final JPasswordField password = styledLoginPasswordField("Password", new LineIcon("lock", 18, MUTED));
        card.add(password, c);

        c.gridy++;
        c.insets = new Insets(8, 0, 0, 0);

        final JButton login = primaryButton("Sign In");
        login.setPreferredSize(new Dimension(600, 52));
        login.setFont(login.getFont().deriveFont(Font.BOLD, 15f));
        login.addActionListener(e -> runSafely(() -> authenticate(username, password, onAuthenticated)));
        card.add(login, c);

        password.addActionListener(e -> login.doClick());
        username.addActionListener(e -> login.doClick());

        card.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "signIn");

        card.getActionMap().put("signIn", new AbstractAction() {
            @Override
            public void actionPerformed(final java.awt.event.ActionEvent e) {
                login.doClick();
            }
        });

        c.gridy++;
        c.insets = new Insets(12, 0, 26, 0);
        final JButton cancel = secondaryButton("Cancel");
        cancel.setPreferredSize(new Dimension(600, 50));
        cancel.setFont(cancel.getFont().deriveFont(Font.BOLD, 15f));
        cancel.addActionListener(e -> {
            username.setText("");
            password.setText("");
        });
        card.add(cancel, c);

        c.gridy++;
        c.insets = new Insets(0, 0, 0, 0);
        final JLabel secure = new JLabel("Authorized users only", new LineIcon("lock", 16, MUTED), SwingConstants.CENTER);
        secure.setIconTextGap(8);
        secure.setForeground(MUTED);
        secure.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(secure, c);
        return card;
    }

    private void authenticate(final JTextField username, final JPasswordField password, final Runnable onAuthenticated) {
        final boolean ok = new AuthenticationCredentialHandler()
                .authWithCredentialData(required(username, "Username/Email"), passwordText(password, "Password"));
        if (!ok) {
            throw new IllegalArgumentException("Invalid credentials.");
        }
        onAuthenticated.run();
    }

    private JPanel footer() {
        final JPanel footer = new JPanel(new GridBagLayout());
        footer.setOpaque(false);
        footer.setPreferredSize(new Dimension(740, 52));
        final GridBagConstraints fc = new GridBagConstraints();
        fc.gridx = 0;
        fc.gridy = 0;
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1;
        final JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(740, 1));
        line.setBackground(BORDER);
        footer.add(line, fc);
        fc.gridy++;
        fc.insets = new Insets(14, 0, 0, 0);
        final JLabel security = new JLabel("Authentication handled by AISafe Security", new LineIcon("shield", 15, new Color(148, 163, 184)), SwingConstants.CENTER);
        security.setIconTextGap(8);
        security.setForeground(new Color(148, 163, 184));
        security.setHorizontalAlignment(SwingConstants.CENTER);
        footer.add(security, fc);
        return footer;
    }

    private static JTextField styledLoginTextField(final String placeholder, final Icon icon) {
        final JTextField field = new JTextField();
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.putClientProperty("JTextField.leadingIcon", icon);
        field.setPreferredSize(new Dimension(600, 51));
        field.setFont(field.getFont().deriveFont(16f));
        field.putClientProperty("FlatLaf.style", "arc: 12");
        return field;
    }

    private static JPasswordField styledLoginPasswordField(final String placeholder, final Icon icon) {
        final JPasswordField field = new JPasswordField();
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.putClientProperty("JTextField.leadingIcon", icon);
        field.setPreferredSize(new Dimension(600, 51));
        field.setFont(field.getFont().deriveFont(16f));
        field.putClientProperty("FlatLaf.style", "arc: 12");
        return field;
    }

    private static final class LoginHeroPanel extends JPanel {
        LoginHeroPanel() {
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(46, 62, 46, 62));
        }

        @Override
        protected void paintComponent(final Graphics graphics) {
            super.paintComponent(graphics);
            final Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(new GradientPaint(0, 0, new Color(3, 16, 34), getWidth(), getHeight(), new Color(10, 42, 82)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(37, 99, 235, 24));
            g.fillOval(-110, getHeight() - 170, 310, 220);
            g.setColor(new Color(96, 165, 250, 42));
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 9f, 10f }, 0));
            final Path2D path = new Path2D.Double();
            path.moveTo(0, getHeight() - 52);
            path.curveTo(118, getHeight() - 92, 190, getHeight() - 24, 342, getHeight() - 150);
            g.draw(path);
            g.setColor(new Color(96, 165, 250, 55));
            drawPlane(g, getWidth() - 92, getHeight() - 152, 48);
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

        @Override
        public void addNotify() {
            super.addNotify();
            removeAll();
            final GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(8, 0, 12, 0);
            add(new JLabel(IconFactory.brand(92, Color.WHITE)), c);

            c.gridy++;
            final JLabel title = new JLabel("AISafe");
            title.setForeground(Color.WHITE);
            title.setFont(title.getFont().deriveFont(Font.PLAIN, 43f));
            add(title, c);

            c.gridy++;
            final JLabel backoffice = new JLabel("Backoffice");
            backoffice.setForeground(new Color(96, 165, 250));
            backoffice.setFont(backoffice.getFont().deriveFont(Font.BOLD, 24f));
            add(backoffice, c);

            c.gridy++;
            c.insets = new Insets(16, 0, 18, 0);
            final JPanel separator = new JPanel();
            separator.setPreferredSize(new Dimension(260, 1));
            separator.setBackground(new Color(59, 130, 246, 45));
            add(separator, c);

            c.gridy++;
            c.insets = new Insets(0, 0, 34, 0);
            final JLabel description = new JLabel("<html><div style='text-align:center'>Air traffic operations and<br>backoffice management.</div></html>");
            description.setForeground(new Color(203, 213, 225));
            description.setFont(description.getFont().deriveFont(16f));
            add(description, c);

            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(11, 0, 11, 0);
            addFeature(c, "shield", "Secure role-based access");
            addFeature(c, "cloud", "Weather and flight plan management");
            addFeature(c, "chart", "Reports and operational data");
        }

        private void addFeature(final GridBagConstraints c, final String icon, final String text) {
            c.gridy++;
            final JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
            row.setOpaque(false);
            final JLabel iconLabel = new JLabel(new LineIcon(icon, 25, new Color(147, 197, 253)));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setPreferredSize(new Dimension(42, 42));
            iconLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(96, 165, 250, 85)),
                    BorderFactory.createEmptyBorder(7, 7, 7, 7)
            ));
            final JLabel textLabel = new JLabel(text);
            textLabel.setForeground(Color.WHITE);
            textLabel.setFont(textLabel.getFont().deriveFont(14f));
            row.add(iconLabel);
            row.add(textLabel);
            add(row, c);
        }
    }
}
