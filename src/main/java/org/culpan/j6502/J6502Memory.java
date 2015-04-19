package org.culpan.j6502;

/**
 * Created by harryculpan on 4/18/15.
 */
public class J6502Memory {
    protected static int ram[] = new int[65536];

    static public int get(int addr) {
        if (addr < 0 || addr > 65535) {
            throw new RuntimeException("Invalid address: '" + addr + "'");
        }
        return ram[addr];
    }

    static public void set(int addr, int value) {
        if (addr < 0 || addr > 65535) {
            throw new RuntimeException("Invalid address: '" + addr + "'");
        }
        if (value < 0 || value > 255) {
            throw new RuntimeException("Invalid value: '" + value + "'");
        }
        ram[addr] = value;
    }

    static public void reset() {
        for (int i = 0; i < ram.length; i++) {
            ram[i] = 0;
        }

        ram[0x0001] = 0x00;
        ram[0x0002] = 0xA0;

        ram[0xD000] = 0xA0;         // ldy #
        ram[0xD001] = 0x00;
        ram[0xD002] = 0xA9;         // lda #
        ram[0xD003] = 'H';
        ram[0xD004] = 0x8D;         // sta abs
        ram[0xD005] = 0x00;
        ram[0xD006] = 0xF0;         // Start of video ram
        ram[0xD007] = 0x4C;         // jmp abs
        ram[0xD008] = 0x07;
        ram[0xD009] = 0xD0;

        ram[0xA000] = 'H';
        ram[0xA001] = 'e';
        ram[0xA002] = 'l';
        ram[0xA003] = 'l';
        ram[0xA004] = 'o';

        ram[0xFFFC] = 0x00;
        ram[0xFFFD] = 0xD0;
    }
}
