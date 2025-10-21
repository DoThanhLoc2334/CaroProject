package Model;

import javax.swing.JButton;
import java.awt.*;
import javax.swing.BorderFactory;

public class XOButton extends JButton {
    public final int row;
    public final int col;

    private char mark = '\0';

    public XOButton(int row, int col) {
        this.row = row;
        this.col = col;

        setText("");
        setFocusPainted(false);
        setContentAreaFilled(true);
        setOpaque(true);
        setBackground(new Color(245, 248, 252));
        setBorder(BorderFactory.createLineBorder(new Color(180, 188, 200)));

        setPreferredSize(new Dimension(32, 32));
        setMinimumSize(new Dimension(28, 28));
    }

    public char getMark() { return mark; }
    public boolean isEmpty() { return mark == '\0'; }

    public void setMark(char m) {
        if (m != 'X' && m != 'O' && m != '\0') return;
        this.mark = m;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mark == '\0') return;

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int w = getWidth(), h = getHeight();
            int pad = Math.max(4, Math.min(w, h) / 6);
            int x1 = pad, y1 = pad, x2 = w - pad, y2 = h - pad;

            if (mark == 'X') {
                g2.setColor(new Color(200, 50, 50));
                g2.drawLine(x1, y1, x2, y2);
                g2.drawLine(x1, y2, x2, y1);
            } else if (mark == 'O') {
                g2.setColor(new Color(50, 80, 200));
                g2.drawOval(x1, y1, x2 - x1, y2 - y1);
            }
        } finally {
            g2.dispose();
        }
    }
}
