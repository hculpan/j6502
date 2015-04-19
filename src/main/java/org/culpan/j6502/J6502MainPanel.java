package org.culpan.j6502;

import javax.swing.*;
import java.awt.*;

/**
 * Created by usucuha on 4/17/2015.
 */
public class J6502MainPanel extends JPanel {
    int video_location;

    public J6502MainPanel(int video_location) {
        setOpaque(true);
        setBackground(Color.BLACK);
        setLocation(15, 15);
        setSize(80 * 8 + 10, 25 * 12 + 10);
        setMinimumSize(new Dimension(80 * 8, 25 * 24));
        setMaximumSize(new Dimension(80 * 8, 25 * 24));

        this.video_location = video_location;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics2D = (Graphics2D)g;
        graphics2D.setFont(new Font("Courier New", Font.PLAIN, 13));
        graphics2D.setColor(Color.white);

        int currlocation = video_location;
        for (int y = 0; y < 25; y++) {
            for (int x = 0; x < 80; x++) {
                char c = (char)J6502Memory.get(currlocation++);
                if (c < 32 || c > 128) {
                    graphics2D.drawString(" ", x * 8 + 5, y * 12 + 15);
                } else {
                    graphics2D.drawString("" + c, x * 8 + 5, y * 12 + 15);
                }
            }
        }
    }
}
