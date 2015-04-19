package org.culpan.j6502;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by usucuha on 4/17/2015.
 */
public class J6502Main implements CpuListener {
    public static final Properties properties = new Properties();

    public static final File propertiesFile = new File(System.getProperty("user.home"), ".j6502");

    public static final int defaultWidth = (80 * 8) + 300;
    public static final int defaultHeight = (25 * 12) + 200;

    public final static int VIDEO_LOCATION = 0xF000;

    protected JLabel programCounterLabel;

    protected JLabel accumulator;

    protected JLabel xreg;

    protected JLabel yreg;

    protected JLabel stackPointer;

    protected BitFlagPanel bitFlagPanels[] = new BitFlagPanel[8];

    protected J6502Cpu cpu;

    protected J6502MainPanel j6502MainPanel;

    protected J6502DisassemblerPanel j6502DisassemblerPanel;

    public static void main(String [] args) {
        final J6502Main j6502Main = new J6502Main();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    j6502Main.createAndShowGUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void start6502() {
        J6502Memory.reset();
        int location = VIDEO_LOCATION;
        for (int x = 0; x < 80; x++) {
            for (int y = 0; y < 25; y++) {
                J6502Memory.set(location++, ' ');
            }
        }
        j6502MainPanel.repaint();

        cpu = new J6502Cpu(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                cpu.reset();
                cpu.run();
            }
        }).start();
    }

    protected void createAndShowGUI() throws IOException {
        if (propertiesFile.exists()) {
            properties.load(new FileInputStream(propertiesFile));
        }

        final JFrame frame = new JFrame("J6502");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {
                properties.put("frame.x", Integer.toString((int) frame.getLocation().getX()));
                properties.put("frame.y", Integer.toString((int) frame.getLocation().getY()));
                properties.put("frame.width", Integer.toString((int) frame.getSize().getWidth()));
                properties.put("frame.height", Integer.toString((int) frame.getSize().getHeight()));
                try {
                    properties.store(new FileOutputStream(propertiesFile), null);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("closing");
                System.exit(0);
            }
        });
        frame.setResizable(false);

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        j6502MainPanel = new J6502MainPanel(VIDEO_LOCATION);
        mainPanel.add(j6502MainPanel);

        JPanel panel = new JPanel();
        JLabel label = new JLabel("PC:");
        label.setSize(30, 10);
        label.setLocation(defaultWidth - 265, 65);
        mainPanel.add(label);
        programCounterLabel = new JLabel();
        programCounterLabel.setSize(50, 10);
        programCounterLabel.setLocation(defaultWidth - 235, 65);
        mainPanel.add(programCounterLabel);

        label = new JLabel("A:");
        label.setSize(100, 10);
        label.setLocation(defaultWidth - 195, 340);
        mainPanel.add(label);
        accumulator = new JLabel();
        accumulator.setSize(75, 18);
        accumulator.setLocation(defaultWidth - 175, 337);
        accumulator.setBorder(BorderFactory.createEtchedBorder());
        mainPanel.add(accumulator);

        label = new JLabel("X:");
        label.setSize(100, 10);
        label.setLocation(defaultWidth - 195, 360);
        mainPanel.add(label);
        xreg = new JLabel();
        xreg.setSize(75, 18);
        xreg.setLocation(defaultWidth - 175, 357);
        xreg.setBorder(BorderFactory.createEtchedBorder());
        mainPanel.add(xreg);

        label = new JLabel("Y:");
        label.setSize(100, 10);
        label.setLocation(defaultWidth - 195, 380);
        mainPanel.add(label);
        yreg = new JLabel();
        yreg.setSize(75, 18);
        yreg.setLocation(defaultWidth - 175, 377);
        yreg.setBorder(BorderFactory.createEtchedBorder());
        mainPanel.add(yreg);

        j6502DisassemblerPanel = new J6502DisassemblerPanel();
        j6502DisassemblerPanel.setSize(defaultWidth - ((80 * 8) + 45), defaultHeight - 254);
        j6502DisassemblerPanel.setLocation(defaultWidth - 265, 80);
        mainPanel.add(j6502DisassemblerPanel);

        setupFlagsDisplay(mainPanel);

        JButton button = new JButton(new AbstractAction("Step") {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        cpu.nextStep();
                    }
                });
            }
        });
        button.setSize(100, 20);
        button.setLocation(50, defaultHeight - 100);
        mainPanel.add(button);

        button = new JButton(new AbstractAction("Reset") {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        J6502Memory.reset();
                        cpu.reset();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                j6502MainPanel.repaint();
                            }
                        });
                    }
                });
            }
        });
        button.setSize(100, 20);
        button.setLocation(175, defaultHeight - 100);
        mainPanel.add(button);

        frame.setContentPane(mainPanel);
/*        if (properties.containsKey("frame.x")) {
            frame.setLocation(Integer.parseInt(properties.getProperty("frame.x")), Integer.parseInt(properties.getProperty("frame.y")));
            frame.setSize(Integer.parseInt(properties.getProperty("frame.width")), Integer.parseInt(properties.getProperty("frame.height")));
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (frame.getX() + frame.getWidth() > screenSize.getWidth() || frame.getY() + frame.getHeight() > screenSize.getHeight()) {
                frame.setSize(defaultWidth, defaultHeight);
                frame.setLocationRelativeTo(null);
            }
        } else {*/
            frame.setSize(defaultWidth, defaultHeight);
            frame.setLocationRelativeTo(null);
//        }
        frame.setVisible(true);
        start6502();
        System.out.println("started cpu");
    }

    protected void setupFlagsDisplay(JPanel mainPanel)  {
        JLabel label;
        for (int i = 7; i >= 0; i--) {
            bitFlagPanels[i] = new BitFlagPanel();
            if (i == 5) {
                bitFlagPanels[i].turnOn();
            }
            label = new JLabel();
            switch (i) {
                case 7:
                    label.setText("N");
                    break;
                case 6:
                    label.setText("V");
                    break;
                case 5:
                    label.setText("");
                    break;
                case 4:
                    label.setText("B");
                    break;
                case 3:
                    label.setText("D");
                    break;
                case 2:
                    label.setText("I");
                    break;
                case 1:
                    label.setText("Z");
                    break;
                case 0:
                    label.setText("C");
                    break;
            }
            int xLocation = defaultWidth - (40 + (i * 20));
            label.setLocation(xLocation, 40);
            label.setSize(10, 10);
            mainPanel.add(label);
            bitFlagPanels[i].setLocation(xLocation, 24);
            mainPanel.add(bitFlagPanels[i]);
        }

    }

    @Override
    public void accumulatorChanged(J6502Cpu j6502Cpu, int oldValue, final int newValue) {
        if (j6502Cpu.isSingleStep()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    accumulator.setText((String.format("$%02X", newValue)));
                }
            });
        }
    }

    @Override
    public void xRegisterChanged(J6502Cpu j6502Cpu, int oldValue, final int newValue) {
        if (j6502Cpu.isSingleStep()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    xreg.setText((String.format("$%02X", newValue)));
                }
            });
        }
    }

    @Override
    public void yRegisterChanged(J6502Cpu j6502Cpu, int oldValue, final int newValue) {
        if (j6502Cpu.isSingleStep()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    yreg.setText((String.format("$%02X", newValue)));
                }
            });
        }
    }

    public String disassemblerLine(int pc) {
        StringBuffer result = new StringBuffer();
        int instr = J6502Memory.get(pc);
        int instrSize = J6502Instructions.instructionSizes[instr];
        if (instrSize == 1) {
            result.append(String.format("%02X ", instr));
            result.append("     ");
        } else if (instrSize == 2) {
            result.append(String.format("%02X %02X ", instr, J6502Memory.get(pc + 1)));
            result.append("  ");
        } else {
            result.append(String.format("%02X %02X %02X", instr, J6502Memory.get(pc + 1), J6502Memory.get(pc + 2)));
        }

        return result.toString();
    }

    public void updateDisassembler(int pc) {
        String codeLine = disassemblerLine(pc);
        j6502DisassemblerPanel.setLine(0, codeLine);
    }

    @Override
    public void programCounterChanged(J6502Cpu j6502Cpu, int oldValue, final int newValue) {
        if (j6502Cpu.isSingleStep()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    programCounterLabel.setText(String.format("$%04X", Integer.valueOf(newValue)));
                    updateDisassembler(newValue);
                    j6502DisassemblerPanel.repaint();
                }
            });
        }
    }

    @Override
    public void stackPointerChanged(J6502Cpu j6502Cpu, int oldValue, int newValue) {

    }

    @Override
    public void flagStatusChanged(J6502Cpu j6502Cpu, J6502Instructions.FlagStatus newFlagsStatus[]) {
        if (j6502Cpu.isSingleStep()) {
            for (int i = 0; i < newFlagsStatus.length; i++) {
                bitFlagPanels[i].setBackground((newFlagsStatus[i] == J6502Instructions.FlagStatus.On ? Color.GREEN : Color.DARK_GRAY));
                bitFlagPanels[i].repaint();
            }
        }
    }

    @Override
    public void updateVideo() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                j6502MainPanel.repaint();
            }
        });
    }
}
