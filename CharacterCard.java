package game;

import javax.swing.*;
import java.awt.*;

public class CharacterCard extends JPanel {
    private GameCharacter character;
    private double shownHp;
    private int flashFrames = 0;

    public CharacterCard() {
        setPreferredSize(new Dimension(260, 215));
        setOpaque(false);
        Timer timer = new Timer(25, e -> tick());
        timer.start();
    }

    public void setCharacter(GameCharacter c) {
        if (c != character) {
            character = c;
            shownHp = (c == null) ? 0 : c.getHealth();
        }
        repaint();
    }

    public void flash() {
        flashFrames = 12;
    }

    // Animation: health bar slides smoothly, damage flash fades out
    private void tick() {
        boolean changed = false;
        if (character != null) {
            double diff = character.getHealth() - shownHp;
            if (Math.abs(diff) > 0.1) {
                shownHp += Math.signum(diff) * Math.min(Math.abs(diff), 1.5);
                changed = true;
            }
        }
        if (flashFrames > 0) {
            flashFrames--;
            changed = true;
        }
        if (changed) repaint();
    }

    private void drawCentered(Graphics2D g2, String text, int y) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();

        g2.setColor(new Color(40, 40, 65));
        g2.fillRoundRect(0, 0, w - 1, getHeight() - 1, 25, 25);

        if (character == null) {
            g2.dispose();
            return;
        }

        Color c = character.getColor();
        g2.setColor(c);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(1, 1, w - 3, getHeight() - 3, 25, 25);

        // Avatar circle with the class initial
        g2.fillOval(w / 2 - 30, 15, 60, 60);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 30));
        drawCentered(g2, character.getType().substring(0, 1), 56);

        // Name and class
        g2.setFont(new Font("SansSerif", Font.BOLD, 17));
        drawCentered(g2, character.getName(), 102);
        g2.setColor(c);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        drawCentered(g2, character.getType(), 120);

        // Health bar (green -> yellow -> red)
        int barX = 20, barY = 133, barW = w - 40, barH = 18;
        double frac = Math.max(0, Math.min(1.0, shownHp / character.getMaxHealth()));
        g2.setColor(new Color(20, 20, 30));
        g2.fillRoundRect(barX, barY, barW, barH, 10, 10);
        Color barColor = frac > 0.5 ? new Color(60, 200, 90)
                       : frac > 0.25 ? new Color(240, 190, 40)
                       : new Color(220, 60, 60);
        g2.setColor(barColor);
        g2.fillRoundRect(barX, barY, (int) (barW * frac), barH, 10, 10);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        drawCentered(g2, "HP: " + character.getHealth() + " / " + character.getMaxHealth(), 175);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.setColor(new Color(200, 200, 220));
        String info = "Special charges: " + character.getSpecialCharges();
        if (character.isDefending()) info += "  |  DEFENDING";
        drawCentered(g2, info, 196);

        // Red flash when hit
        if (flashFrames > 0) {
            g2.setColor(new Color(255, 0, 0, Math.min(150, flashFrames * 13)));
            g2.fillRoundRect(0, 0, w - 1, getHeight() - 1, 25, 25);
        }
        g2.dispose();
    }
}