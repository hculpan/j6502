package org.culpan.j6502;

/**
 * Created by harryculpan on 4/18/15.
 */
public interface CpuListener {
    public void accumulatorChanged(J6502Cpu j6502Cpu, int oldValue, int newValue);

    public void xRegisterChanged(J6502Cpu j6502Cpu, int oldValue, int newValue);

    public void yRegisterChanged(J6502Cpu j6502Cpu, int oldValue, int newValue);

    public void programCounterChanged(J6502Cpu j6502Cpu, int oldValue, int newValue);

    public void stackPointerChanged(J6502Cpu j6502Cpu, int oldValue, int newValue);

    public void flagStatusChanged(J6502Cpu j6502Cpu, J6502Instructions.FlagStatus newFlagsStatus[]);

    public void updateVideo();
}
