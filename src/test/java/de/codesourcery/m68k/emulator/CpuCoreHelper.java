package de.codesourcery.m68k.emulator;

/**
 * ${FILE}
 * <p>
 * Federico Berti
 * <p>
 * Copyright 2019
 */
public class CpuCoreHelper {

    public static BaseCpu overflow(BaseCpu core) {
        return core.setFlags(CPU.FLAG_OVERFLOW);
    }

    public static BaseCpu carry(BaseCpu core) {
        return core.setFlags(CPU.FLAG_CARRY);
    }

    public static BaseCpu negative(BaseCpu core) {
        return core.setFlags(CPU.FLAG_NEGATIVE);
    }

    public static BaseCpu zero(BaseCpu core) {
        return core.setFlags(CPU.FLAG_ZERO);
    }

    public static BaseCpu negativeAndOverflow(BaseCpu core) {
        return core.setFlags(CPU.FLAG_OVERFLOW | CPU.FLAG_NEGATIVE);
    }
}
