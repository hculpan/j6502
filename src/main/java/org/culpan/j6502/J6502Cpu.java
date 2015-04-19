package org.culpan.j6502;

import javax.swing.*;
import java.lang.reflect.Member;

/**
 * Created by harryculpan on 4/18/15.
 */
public class J6502Cpu extends J6502Instructions {
    protected CpuListener cpuListener;

    public J6502Cpu(CpuListener cpuListener) {
        this.cpuListener = cpuListener;
    }

    int programCounter;

    int stackPointer = 0x01FF;

    int a_reg = 0;

    int x_reg = 0;

    int y_reg = 0;

    boolean singleStep = true;

    boolean doNextStep = false;

    FlagStatus flags[] = new FlagStatus[8];

    final static int NEGATIVE_FLAG  = 7;
    final static int OVERFLOW_FLAG  = 6;
    final static int UNUSED_FLAG    = 5;
    final static int BREAK_FLAG     = 4;
    final static int DECIMAL_FLAG   = 3;
    final static int INTERRUPT_FLAG = 2;
    final static int ZERO_FLAG      = 1;
    final static int CARRY_FLAG     = 0;

    public void reset() {
        stackPointer = 0x01FF;
        setA_reg(0);
        setX_reg(0);
        setY_reg(0);
        singleStep = true;
        setProgramCounter((J6502Memory.get(0xFFFD) << 8) + J6502Memory.get(0xFFFC));

        for (int i = 0; i < flags.length; i++) {
            flags[i] = FlagStatus.Off;
        }
        flags[5] = FlagStatus.On;  // Always on
        updatedFlags();
    }

    public synchronized void run() {
        while (true) {
            while (isSingleStep() && !doNextStep) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            doNextStep = false;
            executeNextStep();
        }
    }

    public void executeNextStep() {
        int instr = J6502Memory.get(programCounter);
        int instrSize = instructionSizes[instr];
        int arg = 0;
        if (instrSize == 2) {
            arg = J6502Memory.get(programCounter + 1);
        } else if (instrSize == 3) {
            arg = (J6502Memory.get(programCounter + 2) << 8) + J6502Memory.get(programCounter + 1);
        }

        int effective_addr = arg;
        int value = arg;
        switch (instructionModes[instr]) {
            case ACC:
                value = getA_reg();
                break;
            case ABX:
                effective_addr = arg + getX_reg();
                break;
            case ABY:
                effective_addr = arg + getY_reg();
                break;
            case ABS:
                value = J6502Memory.get(arg);
                break;
            case IND:
                effective_addr = (J6502Memory.get(effective_addr + 1) << 8) + J6502Memory.get(effective_addr);
                break;
            case REL:
                value += programCounter + (byte)arg;
                break;
        }

        setProgramCounter(programCounter + instrSize);

        switch (instr) {
            case 0x4C:
                setProgramCounter(effective_addr);
                break;
            case 0x8D:
                J6502Memory.set(effective_addr, a_reg);
                break;
            case 0xA0:
                setY_reg(value);
                break;
            case 0xA9:
                setA_reg(value);
                break;
        }

        if (cpuListener != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    cpuListener.updateVideo();
                }
            });
        }
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        if (cpuListener != null) {
            cpuListener.programCounterChanged(this, this.programCounter, programCounter);
        }
        this.programCounter = programCounter;
    }

    public int getStackPointer() {
        return stackPointer;
    }

    public void setStackPointer(int stackPointer) {
        this.stackPointer = stackPointer;
    }

    public void push(int value) {
        int oldStackPointer = stackPointer;
        J6502Memory.set(stackPointer--, value);
        if (stackPointer < 0x0100) {
            stackPointer = 0x01FF;
        }
        if (cpuListener != null) {
            cpuListener.stackPointerChanged(this, oldStackPointer, stackPointer);
        }
    }

    public int pop() {
        int oldStackPointer = stackPointer;
        int result = J6502Memory.get(stackPointer++);
        if (stackPointer > 0x01FF) {
            stackPointer = 0x0100;
        }
        if (cpuListener != null) {
            cpuListener.stackPointerChanged(this, oldStackPointer, stackPointer);
        }
        return result;
    }

    public boolean isSingleStep() {
        return singleStep;
    }

    public void setSingleStep(boolean singleStep) {
        this.singleStep = singleStep;
    }

    public int getY_reg() {
        return y_reg;
    }

    public void setY_reg(int y_reg) {
        flags[ZERO_FLAG] = (y_reg == 0 ? FlagStatus.On : FlagStatus.Off);
        flags[NEGATIVE_FLAG] = ((y_reg & 128) != 0 ? FlagStatus.On : FlagStatus.Off);

        if (cpuListener != null) {
            cpuListener.yRegisterChanged(this, this.y_reg, y_reg);
            cpuListener.flagStatusChanged(this, flags);
        }

        this.y_reg = y_reg;
    }

    public int getX_reg() {
        return x_reg;
    }

    public void setX_reg(int x_reg) {
        flags[ZERO_FLAG] = (x_reg == 0 ? FlagStatus.On : FlagStatus.Off);
        flags[NEGATIVE_FLAG] = ((x_reg & 128) != 0 ? FlagStatus.On : FlagStatus.Off);

        if (cpuListener != null) {
            cpuListener.xRegisterChanged(this, this.x_reg, x_reg);
            cpuListener.flagStatusChanged(this, flags);
        }

        this.x_reg = x_reg;
    }

    public int getA_reg() {
        return a_reg;
    }

    public void setA_reg(int a_reg) {
        flags[ZERO_FLAG] = (a_reg == 0 ? FlagStatus.On : FlagStatus.Off);
        flags[NEGATIVE_FLAG] = ((a_reg & 128) != 0 ? FlagStatus.On : FlagStatus.Off);

        if (cpuListener != null) {
            cpuListener.accumulatorChanged(this, this.a_reg, a_reg);
            cpuListener.flagStatusChanged(this, flags);
        }

        this.a_reg = a_reg;
    }

    public CpuListener getCpuListener() {
        return cpuListener;
    }

    public void setCpuListener(CpuListener cpuListener) {
        this.cpuListener = cpuListener;
    }

    public void updatedFlags() {
        if (cpuListener != null) {
            cpuListener.flagStatusChanged(this, flags);
        }
    }

    public void nextStep() {
        doNextStep = true;
    }
}
