package de.codesourcery.m68k.emulator.memory;

/**
 * ${FILE}
 * <p>
 * Federico Berti
 * <p>
 * Copyright 2019
 */
public interface MemoryInterface {
    void writeLong(int address,int value);
    void writeWord(int address,int value);
    void writeByte(int address,int value);

    int readLong(int address);
    short readWord(int address);
    byte readByte(int address);

    String hexdump(int address, int count);

    default void writeBytes(int address, byte[] data){
        int count = data.length;
        int srcIdx = 0;
        while (count > 0){
            writeByte(address++, data[srcIdx++]);
            count--;
        }
    }
}
