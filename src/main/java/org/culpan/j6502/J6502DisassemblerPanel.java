package org.culpan.j6502;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by harryculpan on 4/19/15.
 */
public class J6502DisassemblerPanel extends JPanel {
    protected List<String> lines = new ArrayList<>();

    public J6502DisassemblerPanel() {
        setBorder(BorderFactory.createEtchedBorder());
    }

    public void setLine(int index, String line) {
        if (lines.size())
        lines.set(index, line);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics2D = (Graphics2D)g;
        for (int i = 0; i < lines.size(); i++) {
            graphics2D.drawString(lines.get(i), 5, i * 10 + 5);
        }
    }
}
