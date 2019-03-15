package de.codesourcery.m68k.emulator;

import de.codesourcery.m68k.assembler.arch.CPUType;
import de.codesourcery.m68k.emulator.memory.*;

/**
 * ${FILE}
 * <p>
 * Federico Berti
 * <p>
 * Copyright 2019
 */
public class CpuTest extends AbstractCpuTest {

    private MMU mmu;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        final Amiga amiga = Amiga.AMIGA_500;
        final DMAController dmaCtrl = new DMAController();
        final Blitter blitter = new Blitter( dmaCtrl );
        final Video video = new Video(amiga,blitter,dmaCtrl);
        mmu = new MMU( new MMU.PageFaultHandler(amiga,blitter, video ) );
        Memory memory = new Memory(mmu);
        this.memory = memory;
        blitter.setMemory( memory );
        video.setMemory( memory );
        cpu = new CPU(CPUType.BEST,memory);
    }
}
