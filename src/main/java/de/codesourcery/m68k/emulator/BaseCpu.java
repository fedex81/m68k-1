package de.codesourcery.m68k.emulator;

import de.codesourcery.m68k.emulator.memory.MemoryInterface;

/**
 * ${FILE}
 * <p>
 * Federico Berti
 * <p>
 * Copyright 2019
 */
public interface BaseCpu {

    public BaseCpu setFlags(int bitMask);

    boolean isFlagSet(int bitMask);

    public void clearFlags(int bitMask);

    int getStatusRegister();

    public void externalReset();

    void executeOneInstruction();

    int getAddrRegisterLong(int reg);

    int getDataRegisterLong(int reg);

    public int getIRQLevel();

    public void setIRQLevel(int level);

    MemoryInterface getMemory();

    CPU.IRQ getActiveIRQ();

    int getPC();

    int getCycles();

    public boolean isStopped();

    public boolean isSupervisorMode();

    public boolean isZero();

    public boolean isCarry();

    public boolean isNegative();

    public boolean isOverflow();

    public boolean isExtended();

    default void printState(String str){}

    default boolean isNotZero(){
        return !isZero();
    }

    default boolean isNotCarry(){
        return !isCarry();
    }

    default boolean isNotNegative(){
        return !isNegative();
    }

    default boolean isNotOverflow(){
        return !isOverflow();
    }

    default boolean isNotExtended(){
        return !isExtended();
    }

}
