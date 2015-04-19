package org.culpan.j6502;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * Created by harryculpan on 4/17/15.
 */
public class BitFlagPanel extends JPanel {
    boolean flag;

    public BitFlagPanel() {
        setSize(12, 15);
        turnOff();
    }

    public void turnOff() {
        setBackground(Color.DARK_GRAY);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        flag = false;
    }

    public void turnOn() {
        setBackground(Color.GREEN);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        flag = true;
    }

    public boolean getStatus() {
        return flag;
    }
}
