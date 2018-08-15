package de.codersourcery.m68k.disassembler;

import de.codersourcery.m68k.emulator.MMU;
import de.codersourcery.m68k.emulator.Memory;
import de.codersourcery.m68k.assembler.Assembler;
import de.codersourcery.m68k.assembler.CompilationMessages;
import de.codersourcery.m68k.assembler.CompilationUnit;
import de.codersourcery.m68k.assembler.IResource;
import de.codersourcery.m68k.assembler.arch.Instruction;
import junit.framework.TestCase;

public class DisassemblerTest extends TestCase
{
    private Assembler asm;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        asm = new Assembler();
    }

    public void testBitOps() {
        testBitOp("btst");
        testBitOp("bset");
        testBitOp("bclr");
        testBitOp("bchg");
    }

    public void testTST()
    {
        compile( "tst.w $12(a0)" );
        compile( "tst.b d3" );
        compile( "tst.l (a4)+" );
    }

    private void testBitOp(String op)
    {
        compile(op+" #5,d6");
        compile(op+" d4,d6");
        compile(op+" #5,(a0)");
    }

    public void testMoveToCCR() {
        compile("move.w (a0)+,ccr");
        compile("move.w $1200,ccr");
    }

    public void testMoveP() {
        compile("movep.w d3,$80(a4)");
        compile("movep.l d3,$80(a4)");
        compile("movep.w $80(a4),d3");
        compile("movep.l $80(a4),d3");
    }

    public void testMOVEMToMemory()
    {
        compile("movem.w d3/a4-a5,-(a0)");
        compile("movem.l d3/a4-a5,-(a0)");
        compile("movem.w d3,$1200");
        compile("movem.w d3-d4,$1200");
        compile("movem.w d3/a4-a5,$1200");
        compile("movem.w d3/d5/a4/a6,$1200");
        compile("movem.l d3,$1200");
        compile("movem.l d3-d4,$1200");
        compile("movem.l d3/a4-a5,$1200");
        compile("movem.l d3/d5/a4/a6,$1200");
    }

    public void testMOVEMFromMemory()
    {
        compile("movem.w $1200,d3");
        compile("movem.w $1200,d3-d4");
        compile("movem.w $1200,d3/a4-a5");
        compile("movem.w $1200,d3/d5/a4/a6");
        compile("movem.l $1200,d3");
        compile("movem.l $1200,d3-d4");
        compile("movem.l $1200,d3/a4-a5");
        compile("movem.l $1200,d3/d5/a4/a6");
        compile("movem.w (a0)+,d3/a4-a5");
        compile("movem.l (a0)+,d3/a4-a5");
    }

    public void testIllegal()
    {
        compile("illegal");
    }

    public void testMoveToUSP() {
        compile("move a3,usp");
    }

    public void testMoveFromUSP() {

        compile("move usp,a3");
    }

    public void testORI() {
        compile("ori.w #$1234,$1200");
        compile("ori.b #$12,(a3)+");
        compile("ori.l #$12345678,-(a3)");
    }

    public void testORIToCCR() {
        compile("ori.b #$ff,ccr");
    }

    public void testORIToSR() {
        compile("ori.w #$ffff,sr");
    }

    public void testDivu()
    {
        compile("divu.w $1200,d3");
        compile("divu.w d4,d3");
    }

    public void testDivs()
    {
        compile("divs.w $1200,d3");
        compile("divs.w d4,d3");
    }

    public void testCmp() {
        compile("cmp.b $1200,d2");
        compile("cmp.w (a3)+,d3");
        compile("cmp.l -(a4),d4");
    }

    public void testNegx() {
        compile("negx.b $12000");
        compile("negx.w d3");
        compile("negx.l (a4)+");
    }

    public void testSubx()
    {
        compile("subx.b -(a1),-(a2)");
        compile("subx.w -(a1),-(a2)");
        compile("subx.l -(a1),-(a2)");
        compile("subx.b d1,d2");
        compile("subx.w d1,d2");
        compile("subx.l d1,d2");

    }

    public void testSub() {
        compile("sub.b d3,$1200");
        compile("sub.b $1200,d3");
        compile("sub.b d2,d3");
        compile("sub.w d3,$1200");
        compile("sub.w $1200,d3");
        compile("sub.w d2,d3");
        compile("sub.l d3,$1200");
        compile("sub.l $1200,d3");
        compile("sub.l d2,d3");
    }

    public void testAdd() {
        compile("add.b d3,$1200");
        compile("add.b $1200,d3");
        compile("add.b d2,d3");
        compile("add.w d3,$1200");
        compile("add.w $1200,d3");
        compile("add.w d2,d3");
        compile("add.l d3,$1200");
        compile("add.l $1200,d3");
        compile("add.l d2,d3");
    }

    public void testSwap()
    {
        compile("swap d3","00000000: swap.w d3");
        compile("swap.w d3");
    }

    public void testCmpm() {
        compile("cmpm.b (a3)+,(a4)+");
        compile("cmpm.w (a3)+,(a4)+");
        compile("cmpm.l (a3)+,(a4)+");
    }

    public void testCmpi() {
        compile("cmpi.b #$a,$1200");
        compile("cmpi.w #$a,$1200");
        compile("cmpi.l #$a,$1200");
    }

    public void testSubi() {
        compile("subi.b #$a,$1200");
        compile("subi.w #$a,$1200");
        compile("subi.l #$a,$1200");
    }

    public void testAddi() {
        compile("addi.b #$a,$1200");
        compile("addi.w #$a,$1200");
        compile("addi.l #$a,$1200");
    }

    public void testCmpa() {
        compile("cmpa.w $1200,a4");
        compile("cmpa.l $1200,a4");
        compile("cmpa.l (a3)+,a3");
        compile("cmpa.l (a3)+,a3");
        compile("cmpa.w a4,a5");
    }

    public void testSuba() {
        compile("suba.w $1200,a4");
        compile("suba.l $1200,a4");
        compile("suba.l (a3)+,a3");
    }

    public void testAdda() {
        compile("adda.w $1200,a4");
        compile("adda.l $1200,a4");
        compile("adda.l (a3)+,a3");
    }

    public void testSubq() {
        compile("subq.b #1,d3");
        compile("subq.b #1,$1200");
        compile("subq.w #8,d3");
        compile("subq.w #8,a3");
        compile("subq.w #8,$1200");
        compile("subq.l #8,d3");
        compile("subq.l #8,a3");
        compile("subq.l #8,$1200");
    }

    public void testADDQ() {
        compile("addq.b #1,d3");
        compile("addq.b #1,$1200");
        compile("addq.w #8,d3");
        compile("addq.w #8,a3");
        compile("addq.w #8,$1200");
        compile("addq.l #8,d3");
        compile("addq.l #8,a3");
        compile("addq.l #8,$1200");
    }

    public void testMulu()
    {
        compile("mulu.w $1200,d3");
        compile("mulu.w d4,d3");
    }

    public void testMuls()
    {
        compile("muls.w $1200,d3");
        compile("muls.w d4,d3");
    }

    public void testEORIToCCR() {
        compile("eori.b #$ff,ccr");
    }

    public void testEORIToSR() {
        compile("eori.w #$ffff,sr");
    }

    public void testEOR()
    {
        compile("eor.b d3,$1200");
        compile("eor.b d2,d3");
        compile("eor.w d3,$1200");
        compile("eor.w d2,d3");
        compile("eor.l d3,$1200");
        compile("eor.l d2,d3");
    }

    public void testOR()
    {
        compile("or.b d3,$1200");
        compile("or.b $1200,d3");
        compile("or.b d2,d3");
        compile("or.w d3,$1200");
        compile("or.w $1200,d3");
        compile("or.w d2,d3");
        compile("or.l d3,$1200");
        compile("or.l $1200,d3");
        compile("or.l d2,d3");
    }

    public void testAND()
    {
        compile("and.b d3,$1200");
        compile("and.b $1200,d3");
        compile("and.b d2,d3");
        compile("and.w d3,$1200");
        compile("and.w $1200,d3");
        compile("and.w d2,d3");
        compile("and.l d3,$1200");
        compile("and.l $1200,d3");
        compile("and.l d2,d3");
    }

    public void testANDI()
    {
        compile("and.b #$12,d1" , "00000000: andi.b #$12,d1");
        compile("and.w #$1234,d1", "00000000: andi.w #$1234,d1");
        compile("and.l #$123456,d1", "00000000: andi.l #$123456,d1");
    }

    public void testANDISR() {
        compile("AND #$1234,sr","00000000: andi.w #$1234,sr");
    }

    public void testANDICCR() {
        compile("AND #$34,ccr","00000000: andi.b #$34,ccr");
    }

    public void testDBRA()
    {
        testDBcc(Instruction.DBHI);
    }

    private void testDBcc(Instruction instruction)
    {
        final String expected = "00000000: "+instruction.getMnemonic()+" d1,$fffffffe";
        compile("loop: "+instruction.getMnemonic()+" d1,loop", expected );
    }

    public void testSCC() {
        compile("st $2000");
        compile("shi $2000");
        compile("sls $2000");
        compile("scc $2000");
        compile("scs $2000");
        compile("sne $2000");
        compile("seq $2000");
        compile("svc $2000");
        compile("svs $2000");
        compile("spl $2000");
        compile("smi $2000");
        compile("sge $2000");
        compile("slt $2000");
        compile("sgt $2000");
        compile("sle $2000");
        compile("sf $2000");

    }

    public void testRelativeBranching()
    {
        compile("BRA loop\nloop:","00000000: bra $2");
        compile("BHI loop\nloop:","00000000: bhi $2");
        compile("BLS loop\nloop:","00000000: bls $2");
        compile("BCC loop\nloop:","00000000: bcc $2");
        compile("BCS loop\nloop:","00000000: bcs $2");
        compile("BNE loop\nloop:","00000000: bne $2");
        compile("BEQ loop\nloop:","00000000: beq $2");
        compile("BVC loop\nloop:","00000000: bvc $2");
        compile("BVS loop\nloop:","00000000: bvs $2");
        compile("BPL loop\nloop:","00000000: bpl $2");
        compile("BMI loop\nloop:","00000000: bmi $2");
        compile("BGE loop\nloop:","00000000: bge $2");
        compile("BLT loop\nloop:","00000000: blt $2");
        compile("BGT loop\nloop:","00000000: bgt $2");
        compile("BLE loop\nloop:","00000000: ble $2");
    }

    public void testTAS() {
        compile("tas $1234");
        compile("tas d3"  );
    }
    public void testBSR() {
        compile("org $2000\n" +
                "bsr sub\n" +
                "illegal\n" +
                "sub:\n","00000000: bsr $4\n" +
                "00000002: illegal");
    }

    public void testSTOP()
    {
        compile("stop #$1234");
    }

    public void testChk()
    {
        compile("chk.w $1200,d3");
        compile("chk.l (a4),d7");
    }

    public void testCLR() {
        compile("clr.b d3");
        compile("clr.w d3");
        compile("clr.l d3");
    }

    public void testROL()
    {
        compile("rol.w d1,d2");
        compile("rol.b d1,d2");
        compile("rol.l d1,d2");

        compile("rol.w #1,d2");
        compile("rol.b #2,d2");
        compile("rol.l #8,d2");

        compile("rol $1234");
        compile("rol $12345678");
    }

    public void testROXL()
    {
        compile("roxl.w d1,d2");
        compile("roxl.b d1,d2");
        compile("roxl.l d1,d2");
        compile("roxl.w #1,d2");
        compile("roxl.b #2,d2");
        compile("roxl.l #8,d2");
        compile("roxl $1234");
        compile("roxl $12345678");
    }

    public void testROXR()
    {
        compile("roxr.w d1,d2");
        compile("roxr.b d1,d2");
        compile("roxr.l d1,d2");
        compile("roxr.w #1,d2");
        compile("roxr.b #2,d2");
        compile("roxr.l #8,d2");
        compile("roxr $1234");
        compile("roxr $12345678");
    }

    public void testROR()
    {
        compile("ror.w d1,d2");
        compile("ror.b d1,d2");
        compile("ror.l d1,d2");

        compile("ror.w #1,d2");
        compile("ror.b #2,d2");
        compile("ror.l #8,d2");

        compile("ror $1234");
        compile("ror $12345678");
    }

    public void testReset()
    {
        compile("reset");
    }

    public void testTrapV()
    {
        compile("trapv");
    }

    public void testTrap()
    {
        compile("trap #10");
    }

    public void testUnlink()
    {
        compile("unlk a3");
    }

    public void testPEA()
    {
        compile("pea (a3)");
    }

    public void testAddx() {
        compile("addx.b d3,d4");
        compile("addx.w d3,d4");
        compile("addx.l d3,d4");
        compile("addx.b -(a1),-(a2)");
        compile("addx.w -(a1),-(a2)");
        compile("addx.l -(a1),-(a2)");
    }

    public void testNeg()
    {
        compile("neg.b d3");
        compile("neg.w d3");
        compile("neg.l d3");
    }

    public void testNOT()
    {
        compile("not.b d3");
        compile("not.w d3");
        compile("not.l d3");
    }

    public void testMove_SR() {
        compile("move.w $1200,sr");
        compile("move.w sr,$1200");
    }

    public void testLink()
    {
        compile("link a3,#$4");
    }

    public void testRTR()
    {
        compile("rtr");
    }

    public void testRTE()
    {
        compile("rte");
    }

    public void testEXT()
    {
        compile("ext.w d4");
        compile("ext.l d4");
    }
    public void testJSR() {
        compile("jsr sub\nillegal\nsub:","00000000: jsr $6\n" +
                "00000004: illegal");
    }

    public void testRTS()
    {
        compile("rts");
    }

    public void testMovea() {
        compile("movea #$1234,a3","00000000: movea.w #$1234,a3");
        compile("movea.l #$123456,a3","00000000: movea.l #$123456,a3");
    }

    public void testLEA() {
        compile("lea $1234,a3","00000000: lea $1234,a3");
        compile("lea $12345678,a3","00000000: lea $12345678,a3");
    }

    public void testJMP() {
        compile("jmp $1234");
        compile("jmp $12345678");
    }

    public void testMove()
    {
        compile("move.b #$12,d1","00000000: move.b #$12,d1");
        compile("move   $123(pc),d1","00000000: move.w $123(pc),d1");
        compile("move.l #$12345678,a0","00000000: movea.l #$12345678,a0");
        compile("move   #$1234,d1","00000000: move.w #$1234,d1");
        compile("move.b d0,d1","00000000: move.b d0,d1");
        compile("move   d0,d1","00000000: move.w d0,d1");
        compile("move.w d0,d1","00000000: move.w d0,d1");
        compile("move.l d0,d1","00000000: move.l d0,d1");
        compile("move.b (a0),d1","00000000: move.b (a0),d1");
        compile("move   (a0),d1","00000000: move.w (a0),d1");
        compile("move.w (a0),d1","00000000: move.w (a0),d1");
        compile("move.l (a0),d1","00000000: move.l (a0),d1");
        compile("move.b (a0)+,d1","00000000: move.b (a0)+,d1");
        compile("move   (a0)+,d1","00000000: move.w (a0)+,d1");
        compile("move.w (a0)+,d1","00000000: move.w (a0)+,d1");
        compile("move.l (a0)+,d1","00000000: move.l (a0)+,d1");
        compile("move.b -(a0),d1","00000000: move.b -(a0),d1");
        compile("move   -(a0),d1","00000000: move.w -(a0),d1");
        compile("move.w -(a0),d1","00000000: move.w -(a0),d1");
        compile("move.l -(a0),d1","00000000: move.l -(a0),d1");

        compile("move.b $1234,d1","00000000: move.b $1234,d1");
        compile("move   $1234,d1","00000000: move.w $1234,d1");
        compile("move.w $1234,d1","00000000: move.w $1234,d1");
        compile("move.l $1234,d1","00000000: move.l $1234,d1");

        compile("move.b $123456,d1","00000000: move.b $123456,d1");
        compile("move   $123456,d1","00000000: move.w $123456,d1");
        compile("move.w $123456,d1","00000000: move.w $123456,d1");
        compile("move.l $123456,d1","00000000: move.l $123456,d1");

        compile("move   $10(a0),d1","00000000: move.w $10(a0),d1");
        compile("move.b $10(a0),d1","00000000: move.b $10(a0),d1");
        compile("move.w $10(a0),d1","00000000: move.w $10(a0),d1");
        compile("move.l $10(a0),d1","00000000: move.l $10(a0),d1");
        compile("move.w #$1234,d1","00000000: move.w #$1234,d1");
        compile("move.l #$12345678,d1","00000000: move.l #$12345678,d1");

        // FIXME: Test PC-relative modes
    }

    private void compile(String s) {
        compile(s,"00000000: "+s);
    }

    private void compile(String s,String expectedSource)
    {
        final IResource source = IResource.stringResource(s);
        final CompilationUnit root = new CompilationUnit(source);

        final CompilationMessages messages = asm.compile(root);
        if ( messages.hasErrors() )
        {
            messages.getMessages().stream().forEach(System.out::println );
            throw new RuntimeException("Compilation failed with errors");
        }
        final byte[] executable = this.asm.getBytes(false);
        System.out.println("COMPILED: "+Memory.hexdump(0,executable,0,executable.length));

        final MMU mmu = new MMU( new MMU.PageFaultHandler() );
        final Memory memory = new Memory(mmu);
        memory.writeBytes( 0,executable );
        Disassembler asm = new Disassembler( memory );
        final String disassembled = asm.disassemble( 0, executable.length );
        assertEquals(expectedSource,disassembled);
    }
}