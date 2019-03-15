package de.codesourcery.m68k.emulator;

import de.codesourcery.m68k.assembler.arch.CPUType;
import de.codesourcery.m68k.emulator.memory.MemoryInterface;
import m68k.cpu.Cpu;
import m68k.cpu.MC68000;
import m68k.memory.AddressSpace;
import m68k.memory.MemorySpace;

/**
 * ${FILE}
 * <p>
 * Federico Berti
 * <p>
 * Copyright 2019
 */
public class TonyHCpuTest extends AbstractCpuTest {

    private static int MEMORY_SIZE_KB = 100;
    private static int MEMORY_SIZE_B = MEMORY_SIZE_KB * 1024;

    @Override
    protected void setUp() throws Exception {
        AddressSpace tonyMem = new MemorySpace(MEMORY_SIZE_KB);
        Cpu tonyCpu = new MC68000();
        tonyCpu.setAddressSpace(tonyMem);
        this.memory = createMemoryWrapper(tonyMem);
        this.cpu = createCpuWrapper(tonyCpu, memory);
        tonyCpu.reset();
        tonyCpu.setPC(PROGRAM_START_ADDRESS - 128);

    }

    private static BaseCpu createCpuWrapper(Cpu cpu, MemoryInterface memoryInterface){
        return new BaseCpu() {
            @Override
            public BaseCpu setFlags(int bitMask) {
                cpu.setFlags(bitMask);
                return this;
            }

            @Override
            public boolean isFlagSet(int bitMask) {
                return cpu.isFlagSet(bitMask);
            }

            @Override
            public void clearFlags(int bitMask) {
                cpu.clrFlags(bitMask);
            }

            @Override
            public int getStatusRegister() {
                return cpu.getSR();
            }

            @Override
            public void externalReset() {
                cpu.resetExternal();
                cpu.reset(); //TODO needed?
                System.out.println("CPU reset");

            }

            @Override
            public void executeOneInstruction() {
                cpu.execute();
            }

            @Override
            public int getAddrRegisterLong(int reg) {
                return cpu.getAddrRegisterLong(reg);
            }

            @Override
            public int getDataRegisterLong(int reg) {
                return cpu.getDataRegisterLong(reg);
            }

            @Override
            public int getIRQLevel() {
                return cpu.getInterruptLevel();
            }

            @Override
            public void setIRQLevel(int level) {
                cpu.raiseException(level);
            }

            @Override
            public MemoryInterface getMemory() {
                return memoryInterface;
            }

            @Override
            public CPU.IRQ getActiveIRQ() {
                return null; //tODO
            }

            @Override
            public int getPC() {
                return cpu.getPC();
            }

            @Override
            public int getCycles() {
                return 0; //TODO
            }

            @Override
            public boolean isStopped() {
                return false; //TODO
            }

            @Override
            public boolean isSupervisorMode() {
                return cpu.isSupervisorMode();
            }

            @Override
            public boolean isZero() {
                return cpu.isFlagSet(Cpu.Z_FLAG);
            }

            @Override
            public boolean isCarry() {
                return cpu.isFlagSet(Cpu.C_FLAG);
            }

            @Override
            public boolean isNegative() {
                return cpu.isFlagSet(Cpu.N_FLAG);
            }

            @Override
            public boolean isOverflow() {
                return cpu.isFlagSet(Cpu.V_FLAG);
            }

            @Override
            public boolean isExtended() {
                return cpu.isFlagSet(Cpu.X_FLAG);
            }
        };

    }

    private static MemoryInterface createMemoryWrapper(AddressSpace memory){
        return new MemoryInterface() {
            @Override
            public void writeLong(int address, int value) {
                memory.writeLong(address, value);
            }

            @Override
            public void writeWord(int address, int value) {
                memory.writeWord(address, value);
            }

            @Override
            public void writeByte(int address, int value) {
                memory.writeByte(address, value);
            }

            @Override
            public int readLong(int address) {
                return memory.readLong(address);
            }

            @Override
            public short readWord(int address) {
                return (short) memory.readWord(address);
            }

            @Override
            public byte readByte(int address) {
                return (byte) memory.readByte(address);
            }

            @Override
            public String hexdump(int address, int count) {
                return null; //TODO
            }
        };
    }
}
