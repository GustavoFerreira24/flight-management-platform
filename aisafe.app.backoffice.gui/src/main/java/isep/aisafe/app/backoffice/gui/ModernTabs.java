package isep.aisafe.app.backoffice.gui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

final class ModernTabs extends JTabbedPane {
    ModernTabs() {
        super(TOP);
        setOpaque(false);
        setBackground(UiTheme.PAGE);
        setForeground(UiTheme.TEXT);
        setFont(getFont().deriveFont(Font.BOLD, 13f));
        setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        addChangeListener(e -> refreshTabComponents());
    }

    @Override
    public void addTab(final String title, final Component component) {
        final JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(UiTheme.PAGE);
        page.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 1;
        c.weighty = 1;
        page.add(component, c);
        final JScrollPane scroll = new JScrollPane(page);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UiTheme.PAGE);
        super.addTab(title, scroll);
        setTabComponentAt(getTabCount() - 1, tabComponent(title, getTabCount() - 1 == getSelectedIndex()));
        refreshTabComponents();
    }

    private void refreshTabComponents() {
        for (int i = 0; i < getTabCount(); i++) {
            setTabComponentAt(i, tabComponent(getTitleAt(i), i == getSelectedIndex()));
        }
    }

    private Component tabComponent(final String title, final boolean active) {
        final JPanel tab = new JPanel(new BorderLayout(0, 7));
        tab.setOpaque(false);
        tab.setBorder(BorderFactory.createEmptyBorder(7, 14, 0, 14));
        final JLabel label = new JLabel(title, new LineIcon(IconFactory.iconName(title), 15, active ? UiTheme.BLUE : UiTheme.MUTED), JLabel.CENTER);
        label.setIconTextGap(7);
        label.setForeground(active ? UiTheme.BLUE : UiTheme.MUTED);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        tab.add(label, BorderLayout.CENTER);
        final JPanel underline = new JPanel();
        underline.setPreferredSize(new Dimension(1, 3));
        underline.setBackground(active ? UiTheme.BLUE : new Color(0, 0, 0, 0));
        underline.setOpaque(active);
        tab.add(underline, BorderLayout.SOUTH);
        return tab;
    }
}
