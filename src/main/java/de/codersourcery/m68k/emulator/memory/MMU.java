package de.codersourcery.m68k.emulator.memory;

import de.codersourcery.m68k.emulator.Amiga;
import de.codersourcery.m68k.emulator.chips.CIA8520;
import de.codersourcery.m68k.emulator.exceptions.MemoryAccessException;
import de.codersourcery.m68k.emulator.exceptions.PageNotMappedException;
import de.codersourcery.m68k.utils.Misc;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Arrays;

/**
 * Memory management unit that takes care to enforce memory protection and
 * takes care of setting-up {@link MemoryPage memory pages} when they're
 * being accessed for the very first time.
 *
 * @author tobias.gierke@code-sourcery.de
 *
 * @see PageFaultHandler
 */
public class MMU
{
    private static final boolean DEBUG = false;

    private static final int PAGE_SIZE_LEFT_SHIFT = 12;
    public  static final int PAGE_SIZE = 0x1000;
    public  static final int PAGE_SIZE_MASK = PAGE_SIZE-1;
    private static final int PAGE_OFFSET_MASK = 0xfff;

    private final TIntObjectHashMap<MemoryPage> pageMap = new TIntObjectHashMap<>();

    private final PageFaultHandler faultHandler;
    private int faultCount;

    public static class PageFaultHandler
    {
        private static final int LAST_CHIPRAM_PAGENO = (0x07FFFF & PAGE_SIZE_MASK);
        private static final int FIRST_CIA_PAGENO = (0xBF0000 & PAGE_SIZE_MASK);
        private static final int LAST_CIA_PAGENO = (0xBFFFFF & PAGE_SIZE_MASK);

        private static final int FIRST_CUSTOM_CHIP_PAGENO = (0xDF0000 & PAGE_SIZE_MASK);
        private static final int LAST_CUSTOM_CHIP_PAGENO = (0xDFFFFF & PAGE_SIZE_MASK);

        private final int firstRomPageNo;
        private final int lastRomPageNo;

        private final Amiga amiga;

        private final Blitter blitter;
        private CIA8520 ciaa;
        private CIA8520 ciab;

        public PageFaultHandler(Amiga amiga,Blitter blitter)
        {
            this.amiga = amiga;
            this.blitter = blitter;
            this.firstRomPageNo = amiga.getKickRomStartAddress() & PAGE_SIZE_MASK;
            this.lastRomPageNo = (amiga.getKickRomEndAddress()-1) & PAGE_SIZE_MASK;
        }

        public void reset() {
            ciaa.reset();
            ciab.reset();
            blitter.reset();
        }

        public void setCIAA(CIA8520 cia) {
            this.ciaa = cia;
        }

        public void setCIAB(CIA8520 cia) {
            this.ciab = cia;
        }

        public MemoryPage getPage(int pageNo) throws MemoryAccessException
        {
            /*
Amiga 500

000000-03FFFF 256 KB Chip RAM
040000-07FFFF 256 KB Chip RAM
080000-0FFFFF 512 KB --
100000-1FFFFF   1 MB --
200000-5FFFFF   4 MB --
600000-9FFFFF   4 MB --
A00000-A7FFFF 512 KB --
A80000-BEFFFF 1472 KB --
BF0000-BFFFFF  64 KB CIA address space
C00000-C7FFFF 512 KB --
C80000-CFFFFF 512 KB --
D00000-D7FFFF 512 KB --
D80000-D8FFFF  64 KB --
D90000-D9FFFF  64 KB --
DA0000-DBFFFF 128 KB --
DC0000-DCFFFF  64 KB Real-Time clock
DD0000-DD0FFF   4 KB --
DD1000-DD3FFF  12 KB --
DD4000-DDFFFF  48 KB --
DE0000-DEFFFF  64 KB Mainboard resources
DF0000-DFFFFF  64 KB Custom Chip Registers
E00000-E7FFFF 512 KB --
E80000-E8FFFF  64 KB --
E90000-EFFFFF 448 KB --
F00000-F7FFFF 512 KB --
F80000-FBFFFF 256 KB --
FC0000-FFFFFF 256 KB -- Kickstart ROM
             */

            // Chip RAM
            if ( pageNo <= LAST_CHIPRAM_PAGENO) {
                return new RegularPage(PAGE_SIZE);
            }
            // CIA address range
            if ( pageNo >= FIRST_CIA_PAGENO && pageNo <= LAST_CIA_PAGENO ) {
                return new CIAPage(pageNo*PAGE_SIZE,ciaa,ciab);
            }
            // ROM
            if ( pageNo >= firstRomPageNo && pageNo <= lastRomPageNo) {
                return new RegularPage(PAGE_SIZE);
            }
            // custom chips
            if ( pageNo >= FIRST_CUSTOM_CHIP_PAGENO && pageNo <= LAST_CUSTOM_CHIP_PAGENO) {
                return new CustomChipPage(pageNo*PAGE_SIZE, blitter);
            }
            return AbsentPage.SINGLETON;
        }
    }

    public MMU(PageFaultHandler faultHandler) {
        this.faultHandler = faultHandler;
    }

    public int getPageStartAddress(int pageNo) {
        return pageNo * PAGE_SIZE;
    }

    public void setWriteProtection(int startaddress,int count,boolean onOff)
    {
        if ( onOff )
        {
            setPageFlags(startaddress, count, MemoryPage.FLAG_WRITE_PROTECTED );
        } else {
            clearPageFlags(startaddress, count, MemoryPage.FLAG_WRITE_PROTECTED );
        }
    }

    public MemoryPage getPage(int pageNo)
    {
        MemoryPage page = pageMap.get(pageNo);
        if ( page == null )
        {
            faultCount++;
            page = faultHandler.getPage( pageNo );
            if ( page == null )
            {
                final int adr = getPageStartAddress( pageNo );
                throw new PageNotMappedException("Page at "+
                        Misc.hex(adr)+" is not mapped?",
                        MemoryAccessException.Operation.UNSPECIFIED,adr,MemoryAccessException.ViolationType.PAGE_FAULT);
            }
            if ( DEBUG ) {
                System.out.println("Fault #"+faultCount+": Paged-in memory at "+Misc.hex( getPageStartAddress( pageNo )));
            }
            pageMap.put(pageNo,page);
        }
        return page;
    }

    public void setPageFlags(int startaddress,int count,byte flags)
    {
        int firstPage = getPageNo(startaddress );
        int lastPage = getPageNo(startaddress+count);
        for ( int pageNo = firstPage ; pageNo <= lastPage ; pageNo++) {
            getPage(pageNo).flags |= flags;
        }
    }

    public void clearPageFlags(int startaddress,int count,byte flags)
    {
        final byte negated = (byte) ~flags;
        int firstPage = getPageNo(startaddress );
        int lastPage = getPageNo(startaddress+count);
        for ( int pageNo = firstPage ; pageNo <= lastPage ; pageNo++) {
            getPage(pageNo).flags &= negated;
        }
    }

    public int getPageNo(int address)
    {
        return (address >>> PAGE_SIZE_LEFT_SHIFT);
    }

    public int getOffsetInPage(int address) {
        return (address & PAGE_OFFSET_MASK);
    }

    public void dumpPages() {

        final int[] keys = pageMap.keys();
        Arrays.sort(keys);
        final byte[] pageData = new byte[ MMU.PAGE_SIZE ];
        for ( int pageNo : keys )
        {
            MemoryPage page = getPage( pageNo );
            for ( int i = 0 ; i < MMU.PAGE_SIZE ; i++ ) {
                pageData[i] = page.readByte( i );
            }
            final int adr = getPageStartAddress( pageNo );
            System.out.println("===== Page "+Misc.hex(adr)+" =====");
            System.out.println( Memory.hexdump( adr,pageData,0, MMU.PAGE_SIZE ) );
        }
    }

    public int getPageSize() {
        return PAGE_SIZE;
    }

    public void reset()
    {
        System.out.println("MMU reset().");
        pageMap.clear();
        faultHandler.reset();
        faultCount = 0;
    }
}