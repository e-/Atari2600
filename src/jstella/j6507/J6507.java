/*
 * J6507.java
 *
 * Created on July 10, 2007, 9:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

//TODO : change all chars to ints...there is probably no advantage to using chars, seeing as how
//            they are probably cast into ints by the system anyway...as they are currently, 
//            they probably just slow stuff down.
//TODO : remove debugging messages
package jstella.j6507;

import java.io.*;
import java.util.Random;
/**
 * This class is a emulation of the 6502 microprocessor.
 * It was derived from C++ code by Bradford W. Mott and the Stella team,
 * but it has been substantially reorganized for this Java translation. 
 * The 6502 (or the scaled down version 6507) was used in a number of devices, and
 * this code was designed to operate independently of any single
 * implementation (e.g. Atari 2600 emulator).  There is an accompanying
 * interface, IfcSystem, which the 6502 uses to communicate with "the
 * outside world."
 * 
 * <P>
 * Note: There is a lot of redundancy/useless relics left over from the 
 * C++ to Java translation.  I changed the name of the class from 6502 to
 * 6507 because I'm fairly sure the interrupt system was probably lost
 * in this translation.
 * <P>
 * Note: The original class made heavy use of unsigned variables.
 * Java doesn't have, for example, an unsigned byte (uInt8), and with most of 
 * these cases, an int has been substituted.  Most of the cases of 
 * unsigned 16 bit integers (uInt16) have been replaced with either
 * char (which is also 16 bit unsigned) or a plain ole int.
 * Ints that represent uInt8s frequently are  and-masked with 0xFF (to
 * chop off all but the lowest 8 bits).
 * <P>
 * (See a 6502 reference for more information on what the things
 * in this class represent.  Numerous references are available 
 * on the Internet.)
 * <P>
 * Ideally, this package should be oblivous to other packages and classes outside of standard java
 * @author Bradford W. Mott and the Stella team (original)
 * J.L. Allen (Java translation)
 */
public class J6507 implements java.io.Serializable {
    private final static long serialVersionUID = 2315253717432920075L;
    private final static boolean DEBUG_MODE_ON=false;
   
    /** @see #getRegisterSnapshot() */
    public final static int INDEX_PC=0;
     /** @see #getRegisterSnapshot() */
    public final static int INDEX_A=1;
     /** @see #getRegisterSnapshot() */
    public final static int INDEX_X=2;
     /** @see #getRegisterSnapshot() */
    public final static int INDEX_Y=3;
     /** @see #getRegisterSnapshot() */
    public final static int INDEX_SP=4;
     /** @see #getRegisterSnapshot() */
    public final static int INDEX_FLAGS=5;
    
    
    
    /**
     * Binary Coded Decimal table.  Used in operations involving numbers with decimals.
     */
    private static final int[][] BCDTable=new int[2][256];
    static {
        
        for(int t = 0; t < 256; ++t) {
            getBCDTable()[0][t] = ((t >> 4) * 10) + (t & 0x0f);
            getBCDTable()[1][t] = (((t % 100) / 10) << 4) | (t % 10);
        }//end : for int t
    }//END STATIC
    
    
    /**
     * An enumeration of the different ways that the 6502 
     * addresses memory, etc.
     */
    public enum AddressingMode {
        Absolute, AbsoluteX, AbsoluteY, 
        /**
         * 
         */
        Immediate, 
        /**
         * Implied addressing
         */
        Implied,
        Indirect, IndirectX, IndirectY, Invalid, 
        /**
         * 
         */
        Relative,
        /**
         * Zero page addressing
         */
        Zero, 
        /**
         * Zero page X addressing
         */
        ZeroX, 
        /**
         * Zero page Y addressing
         */
        ZeroY
    };
    
    /**
     * This table represents the addressing mode associated with 
     * the opcode values.  Check a 6502 reference (on the Internet)
     * for more information.
     */
    public final static AddressingMode[] ourAddressingModeTable = {
        AddressingMode.Implied,    AddressingMode.IndirectX, AddressingMode.Invalid,   AddressingMode.IndirectX,    // 0x0?
        AddressingMode.Zero,       AddressingMode.Zero,      AddressingMode.Zero,      AddressingMode.Zero,
        AddressingMode.Implied,    AddressingMode.Immediate, AddressingMode.Implied,   AddressingMode.Immediate,
        AddressingMode.Absolute,   AddressingMode.Absolute,  AddressingMode.Absolute,  AddressingMode.Absolute,
        
        AddressingMode.Relative,   AddressingMode.IndirectY, AddressingMode.Invalid,   AddressingMode.IndirectY,    // 0x1?
        AddressingMode.ZeroX,      AddressingMode.ZeroX,     AddressingMode.ZeroX,     AddressingMode.ZeroX,
        AddressingMode.Implied,    AddressingMode.AbsoluteY, AddressingMode.Implied,   AddressingMode.AbsoluteY,
        AddressingMode.AbsoluteX,  AddressingMode.AbsoluteX, AddressingMode.AbsoluteX, AddressingMode.AbsoluteX,
        
        AddressingMode.Absolute,   AddressingMode.IndirectX, AddressingMode.Invalid,   AddressingMode.IndirectX,    // 0x2?
        AddressingMode.Zero,       AddressingMode.Zero,      AddressingMode.Zero,      AddressingMode.Zero,
        AddressingMode.Implied,    AddressingMode.Immediate, AddressingMode.Implied,   AddressingMode.Immediate,
        AddressingMode.Absolute,   AddressingMode.Absolute,  AddressingMode.Absolute,  AddressingMode.Absolute,
        
        AddressingMode.Relative,   AddressingMode.IndirectY, AddressingMode.Invalid,   AddressingMode.IndirectY,    // 0x3?
        AddressingMode.ZeroX,      AddressingMode.ZeroX,     AddressingMode.ZeroX,     AddressingMode.ZeroX,
        AddressingMode.Implied,    AddressingMode.AbsoluteY, AddressingMode.Implied,   AddressingMode.AbsoluteY,
        AddressingMode.AbsoluteX,  AddressingMode.AbsoluteX, AddressingMode.AbsoluteX, AddressingMode.AbsoluteX,
        
        AddressingMode.Implied,    AddressingMode.IndirectX, AddressingMode.Invalid,   AddressingMode.IndirectX,    // 0x4?
        AddressingMode.Zero,       AddressingMode.Zero,      AddressingMode.Zero,      AddressingMode.Zero,
        AddressingMode.Implied,    AddressingMode.Immediate, AddressingMode.Implied,   AddressingMode.Immediate,
        AddressingMode.Absolute,   AddressingMode.Absolute,  AddressingMode.Absolute,  AddressingMode.Absolute,
        
        AddressingMode.Relative,   AddressingMode.IndirectY, AddressingMode.Invalid,   AddressingMode.IndirectY,    // 0x5?
        AddressingMode.ZeroX,      AddressingMode.ZeroX,     AddressingMode.ZeroX,     AddressingMode.ZeroX,
        AddressingMode.Implied,    AddressingMode.AbsoluteY, AddressingMode.Implied,   AddressingMode.AbsoluteY,
        AddressingMode.AbsoluteX,  AddressingMode.AbsoluteX, AddressingMode.AbsoluteX, AddressingMode.AbsoluteX,
        
        AddressingMode.Implied,    AddressingMode.IndirectX, AddressingMode.Invalid,   AddressingMode.IndirectX,    // 0x6?
        AddressingMode.Zero,       AddressingMode.Zero,      AddressingMode.Zero,      AddressingMode.Zero,
        AddressingMode.Implied,    AddressingMode.Immediate, AddressingMode.Implied,   AddressingMode.Immediate,
        AddressingMode.Indirect,   AddressingMode.Absolute,  AddressingMode.Absolute,  AddressingMode.Absolute,
        
        AddressingMode.Relative,   AddressingMode.IndirectY, AddressingMode.Invalid,   AddressingMode.IndirectY,    // 0x7?
        AddressingMode.ZeroX,      AddressingMode.ZeroX,     AddressingMode.ZeroX,     AddressingMode.ZeroX,
        AddressingMode.Implied,    AddressingMode.AbsoluteY, AddressingMode.Implied,   AddressingMode.AbsoluteY,
        AddressingMode.AbsoluteX,  AddressingMode.AbsoluteX, AddressingMode.AbsoluteX, AddressingMode.AbsoluteX,
        
        AddressingMode.Immediate,  AddressingMode.IndirectX, AddressingMode.Immediate, AddressingMode.IndirectX,    // 0x8?
        AddressingMode.Zero,       AddressingMode.Zero,      AddressingMode.Zero,      AddressingMode.Zero,
        AddressingMode.Implied,    AddressingMode.Immediate, AddressingMode.Implied,   AddressingMode.Immediate,
        AddressingMode.Absolute,   AddressingMode.Absolute,  AddressingMode.Absolute,  AddressingMode.Absolute,
        
        AddressingMode.Relative,   AddressingMode.IndirectY, AddressingMode.Invalid,   AddressingMode.IndirectY,    // 0x9?
        AddressingMode.ZeroX,      AddressingMode.ZeroX,     AddressingMode.ZeroY,     AddressingMode.ZeroY,
        AddressingMode.Implied,    AddressingMode.AbsoluteY, AddressingMode.Implied,   AddressingMode.AbsoluteY,
        AddressingMode.AbsoluteX,  AddressingMode.AbsoluteX, AddressingMode.AbsoluteY, AddressingMode.AbsoluteY,
        
        AddressingMode.Immediate,  AddressingMode.IndirectX, AddressingMode.Immediate, AddressingMode.IndirectX,    // 0xA?
        AddressingMode.Zero,       AddressingMode.Zero,      AddressingMode.Zero,      AddressingMode.Zero,
        AddressingMode.Implied,    AddressingMode.Immediate, AddressingMode.Implied,   AddressingMode.Immediate,
        AddressingMode.Absolute,   AddressingMode.Absolute,  AddressingMode.Absolute,  AddressingMode.Absolute,
        
        AddressingMode.Relative,   AddressingMode.IndirectY, AddressingMode.Invalid,   AddressingMode.IndirectY,    // 0xB?
        AddressingMode.ZeroX,      AddressingMode.ZeroX,     AddressingMode.ZeroY,     AddressingMode.ZeroY,
        AddressingMode.Implied,    AddressingMode.AbsoluteY, AddressingMode.Implied,   AddressingMode.AbsoluteY,
        AddressingMode.AbsoluteX,  AddressingMode.AbsoluteX, AddressingMode.AbsoluteY, AddressingMode.AbsoluteY,
        
        AddressingMode.Immediate,  AddressingMode.IndirectX, AddressingMode.Immediate, AddressingMode.IndirectX,    // 0xC?
        AddressingMode.Zero,       AddressingMode.Zero,      AddressingMode.Zero,      AddressingMode.Zero,
        AddressingMode.Implied,    AddressingMode.Immediate, AddressingMode.Implied,   AddressingMode.Immediate,
        AddressingMode.Absolute,   AddressingMode.Absolute,  AddressingMode.Absolute,  AddressingMode.Absolute,
        
        AddressingMode.Relative,   AddressingMode.IndirectY, AddressingMode.Invalid,   AddressingMode.IndirectY,    // 0xD?
        AddressingMode.ZeroX,      AddressingMode.ZeroX,     AddressingMode.ZeroX,     AddressingMode.ZeroX,
        AddressingMode.Implied,    AddressingMode.AbsoluteY, AddressingMode.Implied,   AddressingMode.AbsoluteY,
        AddressingMode.AbsoluteX,  AddressingMode.AbsoluteX, AddressingMode.AbsoluteX, AddressingMode.AbsoluteX,
        
        AddressingMode.Immediate,  AddressingMode.IndirectX, AddressingMode.Immediate, AddressingMode.IndirectX,    // 0xE?
        AddressingMode.Zero,       AddressingMode.Zero,      AddressingMode.Zero,      AddressingMode.Zero,
        AddressingMode.Implied,    AddressingMode.Immediate, AddressingMode.Implied,   AddressingMode.Immediate,
        AddressingMode.Absolute,   AddressingMode.Absolute,  AddressingMode.Absolute,  AddressingMode.Absolute,
        
        AddressingMode.Relative,   AddressingMode.IndirectY, AddressingMode.Invalid,   AddressingMode.IndirectY,    // 0xF?
        AddressingMode.ZeroX,      AddressingMode.ZeroX,     AddressingMode.ZeroX,     AddressingMode.ZeroX,
        AddressingMode.Implied,    AddressingMode.AbsoluteY, AddressingMode.Implied,   AddressingMode.AbsoluteY,
        AddressingMode.AbsoluteX,  AddressingMode.AbsoluteX, AddressingMode.AbsoluteX, AddressingMode.AbsoluteX
    };
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * This table represents the minimum number of processor cycles a given
     * opcode takes.  (A number of opcodes can last longer, depending
     * on the result of the operation...e.g. page crossing.)
     */
    public final static int[] ourInstructionProcessorCycleTable = {
    //  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f
        7, 6, 2, 8, 3, 3, 5, 5, 3, 2, 2, 2, 4, 4, 6, 6,  // 0
        2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,  // 1
        6, 6, 2, 8, 3, 3, 5, 5, 4, 2, 2, 2, 4, 4, 6, 6,  // 2
        2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,  // 3
        6, 6, 2, 8, 3, 3, 5, 5, 3, 2, 2, 2, 3, 4, 6, 6,  // 4
        2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,  // 5
        6, 6, 2, 8, 3, 3, 5, 5, 4, 2, 2, 2, 5, 4, 6, 6,  // 6
        2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,  // 7
        2, 6, 2, 6, 3, 3, 3, 3, 2, 2, 2, 2, 4, 4, 4, 4,  // 8
        2, 6, 2, 6, 4, 4, 4, 4, 2, 5, 2, 5, 5, 5, 5, 5,  // 9
        2, 6, 2, 6, 3, 3, 3, 4, 2, 2, 2, 2, 4, 4, 4, 4,  // a
        2, 5, 2, 5, 4, 4, 4, 4, 2, 4, 2, 4, 4, 4, 4, 4,  // b
        2, 6, 2, 8, 3, 3, 5, 5, 2, 2, 2, 2, 4, 4, 6, 6,  // c
        2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,  // d
        2, 6, 2, 8, 3, 3, 5, 5, 2, 2, 2, 2, 4, 4, 6, 6,  // e
        2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7   // f
    };
    
/**
     * This table specifies whether a given opcode is subject
     * to a variable number of processor cycles.
     * Value of 1 = add 1 cycle if this operation crosses a page
     * Value of 2 = this opcode is a branch instruction--
     *          add 1 cycle if branch is successful
     *          add another 1 if a page is crossed
     *          (i.e. add 2 for a sucessful branch that crosses page boundary).
     */
    public final static int[] ourInstructionPageCrossDelay = {
    //  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 0
        2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0,  // 1
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 2
        2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0,  // 3
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 4
        2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0,  // 5
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 6
        2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0,  // 7
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 8
        2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // 9
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // a
        2, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1,  // b
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // c
        2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0,  // d
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  // e
        2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0  // f
    };
    
    //TODO : add unofficial opcode data to ourInstructionPageCrossDelay
       // - added so far:
       //      - lax
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * This table holds the assembler mnemonic (used in 
     * assembly language) for the given opcode. This table is
     * mainly used in debugging.
     */
    public final static String[] ourInstructionMnemonicTable = {
        "BRK",  "ORA",  "n/a",  "slo",  "nop",  "ORA",  "ASL",  "slo",    // 0x0?
        "PHP",  "ORA",  "ASLA", "anc",  "nop",  "ORA",  "ASL",  "slo",
        
        "BPL",  "ORA",  "n/a",  "slo",  "nop",  "ORA",  "ASL",  "slo",    // 0x1?
        "CLC",  "ORA",  "nop",  "slo",  "nop",  "ORA",  "ASL",  "slo",
        
        "JSR",  "AND",  "n/a",  "rla",  "BIT",  "AND",  "ROL",  "rla",    // 0x2?
        "PLP",  "AND",  "ROLA", "anc",  "BIT",  "AND",  "ROL",  "rla",
        
        "BMI",  "AND",  "n/a",  "rla",  "nop",  "AND",  "ROL",  "rla",    // 0x3?
        "SEC",  "AND",  "nop",  "rla",  "nop",  "AND",  "ROL",  "rla",
        
        "RTI",  "EOR",  "n/a",  "sre",  "nop",  "EOR",  "LSR",  "sre",    // 0x4?
        "PHA",  "EOR",  "LSRA", "asr",  "JMP",  "EOR",  "LSR",  "sre",
        
        "BVC",  "EOR",  "n/a",  "sre",  "nop",  "EOR",  "LSR",  "sre",    // 0x5?
        "CLI",  "EOR",  "nop",  "sre",  "nop",  "EOR",  "LSR",  "sre",
        
        "RTS",  "ADC",  "n/a",  "rra",  "nop",  "ADC",  "ROR",  "rra",    // 0x6?
        "PLA",  "ADC",  "RORA", "arr",  "JMP",  "ADC",  "ROR",  "rra",
        
        "BVS",  "ADC",  "n/a",  "rra",  "nop",  "ADC",  "ROR",  "rra",    // 0x7?
        "SEI",  "ADC",  "nop",  "rra",  "nop",  "ADC",  "ROR",  "rra",
        
        "nop",  "STA",  "nop",  "sax",  "STY",  "STA",  "STX",  "sax",    // 0x8?
        "DEY",  "nop",  "TXA",  "ane",  "STY",  "STA",  "STX",  "sax",
        
        "BCC",  "STA",  "n/a",  "sha",  "STY",  "STA",  "STX",  "sax",    // 0x9?
        "TYA",  "STA",  "TXS",  "shs",  "shy",  "STA",  "shx",  "sha",
        
        "LDY",  "LDA",  "LDX",  "lax",  "LDY",  "LDA",  "LDX",  "lax",    // 0xA?
        "TAY",  "LDA",  "TAX",  "lxa",  "LDY",  "LDA",  "LDX",  "lax",
        
        "BCS",  "LDA",  "n/a",  "lax",  "LDY",  "LDA",  "LDX",  "lax",    // 0xB?
        "CLV",  "LDA",  "TSX",  "las",  "LDY",  "LDA",  "LDX",  "lax",
        
        "CPY",  "CMP",  "nop",  "dcp",  "CPY",  "CMP",  "DEC",  "dcp",    // 0xC?
        "INY",  "CMP",  "DEX",  "sbx",  "CPY",  "CMP",  "DEC",  "dcp",
        
        "BNE",  "CMP",  "n/a",  "dcp",  "nop",  "CMP",  "DEC",  "dcp",    // 0xD?
        "CLD",  "CMP",  "nop",  "dcp",  "nop",  "CMP",  "DEC",  "dcp",
        
        "CPX",  "SBC",  "nop",  "isb",  "CPX",  "SBC",  "INC",  "isb",    // 0xE?
        "INX",  "SBC",  "NOP",  "sbc",  "CPX",  "SBC",  "INC",  "isb",
        
        "BEQ",  "SBC",  "n/a",  "isb",  "nop",  "SBC",  "INC",  "isb",    // 0xF?
        "SED",  "SBC",  "nop",  "isb",  "nop",  "SBC",  "INC",  "isb"
    };
    
    
    private final static int StopExecutionBit = 0x01;
    private final static int FatalErrorBit = 0x02;
    private final static int MaskableInterruptBit = 0x04;
    private final static int NonmaskableInterruptBit = 0x08;
    
    
    /**
     * The current system for this processor.
     */
    private IfcSystem myCurrentSystem=null;
    
    /**
     * Negative flag
     */
    private boolean N=false;     // N flag for processor status register
    /**
     * Overflow flag
     */
    private boolean V=false;     // V flag for processor status register
    /**
     * Break command bit
     */
    private boolean B=false;     // B flag for processor status register
    /**
     * Decimal flag
     */
    private boolean D=false;     // D flag for processor status register
    /**
     * Interrupt disable flag ("Do not disturb")
     */
    private boolean I=false;     // I flag for processor status register
    /**
     * The not-zero flag, holding the opposite of what the zero flag would 
     * have held.
     */
    private boolean notZ=false;  // Z flag complement for processor status register
    /**
     * Carry flag
     */
    private boolean C=false;     // C flag for processor status register
    
    
    /**
     * Accumulator register
     */
    private int A=0;   // Accumulator - byte
    /**
     * X register
     */
    private int X=0;   // X index register - byte
    /**
     * Y register
     */
    private int Y=0;    // Y index register - byte
    /**
     * Stack Pointer
     */
    private int SP=0;  // Stack Pointer - byte
    /**
     * Holds the opcode.
     */
    private int IR=0;   // Instruction register - byte
    /**
     * PC register (Program Counter)
     */
    private int PC=0;  // Program Counter - two bytes
    
   
  
    
    private int myExecutionStatus=0;
    
    /**
     * Set to wherever the last value was retrieved.
     */
    private int myLastOperandAddress=0;
    private int[] myLastImmediateValues=new int[2];
    /**
     * This is set to true if a page is crossed during the instruction.
     */
    private boolean myPageCrossed=false;
    /**
     * The results of the most recent branch (with the number returned representing 
     * the additional number of processor cycles used.)
     */
    private int myBranchResult=0;
    
    /**
     * The number of cycles that have been communicated to mySystem in this instruction
     * cycle.  At the end of the instruction cycle, this number will be used to determine
     * the remaining number of cycles that mySystem should be informed of.
     */
    private int myCyclesSignaled=0;
    
    private boolean myReadLast=false;
    
    private boolean debugStartDump=false;
    
  
    
    /**
     * Creates a new instance of J6507
     * 
     * @param aSystem The system that takes care of memory, storage, etc.
     * aka "the outside world".
     */
    public J6507(IfcSystem aSystem) {
        install(aSystem);
    }
    
     private void readObject(java.io.ObjectInputStream in)  throws IOException, ClassNotFoundException
    {
         in.defaultReadObject();         
    }
    
    

    
    /**
     * This method is used for debugging, etc.  It simply 
     * takes (some of) the register values and copies them into
     * an array.  The constants beginning with INDEX indicate
     * which member of the array is which register.
     * @return An array with register values
     */
    public int[] getRegisterSnapshot() {
        int[] zReturn=new int[6];
        zReturn[INDEX_A]=getA();
        zReturn[INDEX_X]=getX();
        zReturn[INDEX_Y]=getY();
        zReturn[INDEX_PC]=getPC();
        zReturn[INDEX_SP]=getSP();
        zReturn[INDEX_FLAGS]=getFlags();
        return zReturn;
    }
    
    /**
     * Sets the system
     * @param aSystem The intended system for this 6502
     */
    public void install(IfcSystem aSystem) {
        myCurrentSystem=aSystem;
    }
    
    /**
     * Stops the execution loop of the CPU
     */
    public void stop() {
        myExecutionStatus |= StopExecutionBit;
    }
    
    
    //*************************** ACCESSORS ************************************
    
    /**
     * Retrieves a BCD (binary coded decimal) table for operations
     * involving numbers with decimals.
     * @return BCD table
     */
    public static int[][] getBCDTable() {
        return BCDTable;
    }
    
    public boolean isN() {
        return N;
    }
    
    public void setN(boolean N) {
        this.N = N;
    }
    
    public void setN(int aInt) {
        setN(aInt!=0);
    }
    
    public boolean isV() {
        return V;
    }
    
    public void setV(boolean V) {
        this.V = V;
    }
    
    public void setV(int aInt) {
        setV(aInt!=0);
    }
    
    public boolean isB() {
        return B;
    }
    
    public void setB(boolean B) {
        this.B = B;
    }
    
    public void setB(int aInt) {
        setB(aInt!=0);
    }
    
   
    public boolean isD() {
        return D;
    }
    
    public void setD(boolean D) {
        this.D = D;
    }
    
    public void setD(int aInt) {
        setD(aInt!=0);
    }
    
    public boolean isI() {
        return I;
    }
    
    public void setI(boolean I) {
        this.I = I;
    }
    
    public void setI(int aInt) {
        setI(aInt!=0);
    }
    
    public boolean isNotZ() {
        return notZ;
    }
    
    public void setNotZ(boolean notZ) {
        this.notZ = notZ;
    }
    
    public void setNotZ(int aInt) {
        setNotZ(aInt!=0);
    }
    
    public boolean isC() {
        return C;
    }
    
    public void setC(boolean C) {
        this.C = C;
    }
    
    public void setC(int aInt) {
        setC(aInt!=0);
    }
    
    public int getA() {
        assert((A>=0)&&(A<0x100));
        return A;
    }
    
    public void setA(int aValue) {
        //debugMsg("Setting A", aA);
        assert((aValue>=0)&&(aValue<0x100));
        A = aValue & 0xFF; //masking
    }
    
    
    
    public int getX() {
        return X;
    }
    
    public void setX(int aValue) {
        assert((aValue>=0)&&(aValue<0x100));
        X=aValue & 0xFF; //masking
    }
    
    public int getY() {
        return Y;
    }
    
    public void setY(int aValue) {
        assert((aValue>=0)&&(aValue<0x100));
        Y=aValue & 0xFF;
    }
    
    public int getSP() {
        return SP;
    }
    
    public void setSP(int aValue) {
        assert((aValue>=0)&&(aValue<0x100));
        this.SP = aValue;
    }
    
    public int SPdec() {  //same as SP--, but does the byte length limit thing
        int zOldSP=SP; 
        SP = ((SP - 1) & 0xFF); 
        return zOldSP; }
    
    public int SPinc() { //same as SP++, but does the byte length limit thing
        int zOldSP=SP; 
        SP = ((SP + 1) & 0xFF); 
        return zOldSP;} 
    
    public int getIR() {
        return IR;
    }
    
    public void setIR(int aValue) {
        assert((aValue>=0)&&(aValue<0x100));
        this.IR = aValue;
    }
    
    public int getPC() {
        return PC;
    }
    
    public void setPC(int aPC) {
        this.PC = aPC;
    }
  
    
    // END ACCESSORS
    
    // ****************************** REGULAR METHODS ********************************
    /**
     * A not-very-elegant way of determining (I think) whether the most
     * recent operation of the CPU was a read (as opposed to a write).
     * @return true if last operation was a read (?)
     */
    public boolean lastAccessWasRead() {
        return myReadLast;
    }
    
    
    /**
     * Determines whether the two addresses are on the different pages.
     * @param aAddrA Address A
     * @param aAddrB Address B
     * @return true if the two addresses are on different pages
     */
    private boolean notSamePage(int aAddrA, int aAddrB) {
        return ((((aAddrA)^ (aAddrB)) & 0xFF00)!=0);
    }
    
    /**
     * Executes a single execution cycle
     * @throws jstella.j6507.J6507.J6507Exception 
     * @return number of instructions executed
     */
    public int execute() throws J6507Exception
    {
        return execute(-1);
    }
    
    
    /**
     * This is the main method of this class.  It will execute for
     * the number of loops specified (unless something stops it early),
     * starting with the instruction at the address in the PC register.
     * 
     * An 'execution cycle' represents a single instruction.  It shouldn't
     * be confused with 'processor cycle'.  One execution cycle/loop usually takes
     * between 2 and 7 processor cycles.
     * @return number of instructions executed
     * @param aRepeats Number of loops (instructions) to execute
     * @throws jstella.j6507.J6507.J6507Exception 
     */
    public int execute(int aRepeats) throws J6507Exception
    {
       // boolean zReturn=false;
        //int zReturn=0;
        boolean zContinue=true;
        myExecutionStatus &= FatalErrorBit; //clears all of the bits except fatal error bit
        //for (int i=0; i<aNumber; i++)
        int zCounter=0;
        while (zContinue==true) {
            if ((aRepeats>=0)&&(zCounter==aRepeats)) {
               // System.out.println("DEBUG 6507 - repeats == counter");
                zContinue=false;
                break;
            }//end : reached # of repeats
            else zCounter++;
            
            if (zContinue==false) {
               // zReturn=zCounter; //BREAK
                break;
            }
            int[] zPreSnapshot=null;
            if (DEBUG_MODE_ON) zPreSnapshot=getRegisterSnapshot();
            int zOpPC=getPC();
            IR=peekImmediate(); //Cost : 1 cycle
            
            int zOperand=0;
            int zOperandAddress=0;
            
            //  debugMsg("OpCode", IR);
           
           // if (zOpPC==0xFB5E) debugStartDump=true;
           // if (debugStartDump==true) 
           //System.out.println("" + zCounter + " : DEBUG 6507 : address = " + Integer.toHexString(zOpPC) + " " + ourInstructionMnemonicTable[IR]);
            
            // int zCycles=ourInstructionProcessorCycleTable[IR];
            
           
            switch (IR) {
                // BRK
                case 0x00 :   INSTR_BRK();
               
               // zContinue=false;
                break;
                
                //ADC
                case 0x69 :
                case 0x65 :
                case 0x75 :
                case 0x6d :
                case 0x7d :
                case 0x79 :
                case 0x61 :
                case 0x71 :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    INSTR_ADC(zOperand);
                    break;
                    
                    
                    
                    
                    
                    
                    //LDA
                    
                case 0xA9 :
                case 0xA5 :
                case 0xB5 :
                case 0xAD :
                case 0xBD :
                case 0xB9 :
                case 0xA1 :
                case 0xB1 :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    INSTR_LDA(zOperand);
                    break;
                    
                    
                
                    
     
                    
                    //LDX
                case 0xA2 :
                case 0xA6 :
                case 0xB6 :
                case 0xAE :
                case 0xBE :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    INSTR_LDX(zOperand);
                    break;
                    
                    
                    
                    
                    
                    //LDY
                case 0xA0 :
                case 0xA4 :
                case 0xB4 :
                case 0xAC :
                case 0xBC :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    INSTR_LDY(zOperand);
                    break;
                    
                    
                    
                    //  AND
                case 0x29 :
                case 0x25 :
                case 0x35 :
                case 0x2D :
                case 0x3D :
                case 0x39 :
                case 0x21 :
                case 0x31 :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    INSTR_AND(zOperand);
                    break;
                    
                    //ASL
                    
                case 0x0A : INSTR_ASLA(); break;
                case 0x06 :
                case 0x16 :
                case 0x0E :
                case 0x1E :
                  
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    zOperandAddress=myLastOperandAddress;
                    INSTR_ASL(zOperand, zOperandAddress);
                    break;
                    
                    // BIT
                case 0x24 :
                case 0x2C :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    INSTR_BIT(zOperand);
                    break;
                    
                    
                    
                    
                    // FLAG STUFF
                    
                case 0x18 : INSTR_CLC(); break;
                case 0x38 : INSTR_SEC(); break;
                case 0x58 : INSTR_CLI(); break;
                case 0x78 : INSTR_SEI(); break;
                case 0xB8 : INSTR_CLV(); break;
                case 0xD8 : INSTR_CLD(); break;
                case 0xF8 : INSTR_SED(); break;
                
                
                // CMP
                case 0xC9 :
                case 0xC5 :
                case 0xD5 :
                case 0xCD :
                case 0xDD :
                case 0xD9 :
                case 0xC1 :
                case 0xD1 :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    INSTR_CMP(zOperand);
                    break;
                    
                    
                    // CPX
                case 0xE0 :
                case 0xE4 :
                case 0xEC :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    INSTR_CPX(zOperand);
                    break;
                    
                    // CPY
                case 0xC0 :
                case 0xC4 :
                case 0xCC :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    INSTR_CPY(zOperand);
                    break;
                    
                    // DEC
                case 0xC6 :
                case 0xD6 :
                case 0xCE :
                case 0xDE :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    zOperandAddress=myLastOperandAddress;
                    INSTR_DEC(zOperand, zOperandAddress);
                    break;
                    
                    
                    // EOR
                case 0x49 :
                case 0x45 :
                case 0x55 :
                case 0x4D :
                case 0x5D :
                case 0x59 :
                case 0x41 :
                case 0x51 :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    INSTR_EOR(zOperand);
                    break;
                    
                    // INC
                case 0xE6 :
                case 0xF6 :
                case 0xEE :
                case 0xFE :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    zOperandAddress=myLastOperandAddress;
                    INSTR_INC(zOperand, zOperandAddress);
                    break;
                    
                    //REG instructions
                case 0xAA : INSTR_TAX(); break;
                case 0x8A : INSTR_TXA(); break;
                case 0xCA : INSTR_DEX(); break;
                case 0xE8 : INSTR_INX(); break;
                case 0xA8 : INSTR_TAY(); break;
                case 0x98 : INSTR_TYA(); break;
                case 0x88 : INSTR_DEY(); break;
                case 0xC8 : INSTR_INY(); break;
                
                //JMP
                case 0x4C :
                     peekAbsoluteJMP();
                     INSTR_JMP(zOperand, myLastOperandAddress);
                     break;
                case 0x6C :
                    //zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    //zOperandAddress=myLastOperandAddress;
                    peekIndirect();
                    INSTR_JMP(zOperand, myLastOperandAddress);
                    break;
                    
                    
                    //JSR
                case 0x20 :  INSTR_JSR(); break;
                
                
                //LSR
                
                case 0x4A : INSTR_LSRA(); break;
                case 0x46 :
                case 0x56 :
                case 0x4E :
                case 0x5E :
                  
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    zOperandAddress=myLastOperandAddress;
                    INSTR_LSR(zOperand, zOperandAddress);
                    break;
                    
                    
                    
                case 0xEA : 
                //case 0xE2 : //unofficial
                //case 0x80 : //unofficial
                    INSTR_NOP(); break;
              
                
                // ORA
                case 0x09 :
                case 0x05 :
                case 0x15 :
                case 0x0D :
                case 0x1D :
                case 0x19 :
                case 0x01 :
                case 0x11 :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    INSTR_ORA(zOperand);
                    break;
                    
                    
                    //Stack instructions
                case 0x9A : INSTR_TXS(); break;
                case 0xBA : INSTR_TSX(); break;
                case 0x48 : INSTR_PHA(); break;
                case 0x68 : INSTR_PLA(); break;
                case 0x08 : INSTR_PHP(); break;
                case 0x28 : INSTR_PLP(); break;
                
                
                
                //ROL
                
                case 0x2A : INSTR_ROLA(); break;
                
                case 0x26 :
                case 0x36 :
                case 0x2E :
                case 0x3E :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    zOperandAddress=myLastOperandAddress;
                    INSTR_ROL(zOperand, zOperandAddress);
                    break;
                    
                    
                    //ROR
                    
                case 0x6A : INSTR_RORA(); break;
                
                case 0x66 :
                case 0x76 :
                case 0x6E :
                case 0x7E :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    zOperandAddress=myLastOperandAddress;
                    INSTR_ROR(zOperand, zOperandAddress);
                    break;
                    
                    
                    // RT
                case 0x40 : INSTR_RTI(); break;
                case 0x60 : INSTR_RTS(); break;
                
                
                // SBC
                case 0xE9 :
                case 0xE5 :
                case 0xF5 :
                case 0xED :
                case 0xFD :
                case 0xF9 :
                case 0xE1 :
                case 0xF1 :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    INSTR_SBC(zOperand);
                    break;
                    
                    
                    // STA
                    
                case 0x85 :
                case 0x95 :
                case 0x8D :
                case 0x9D :
                case 0x99 :
                case 0x81 :
                case 0x91 :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    zOperandAddress=myLastOperandAddress;
                    INSTR_STA(zOperand, zOperandAddress);
                    break;
                    
                    
                    // STX
                case 0x86 :
                case 0x96 :
                case 0x8E :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    zOperandAddress=myLastOperandAddress;
                    INSTR_STX(zOperand, zOperandAddress);
                    break;
                    
                    
                case 0x84 :
                case 0x94 :
                case 0x8C :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    zOperandAddress=myLastOperandAddress;
                    INSTR_STY(zOperand, zOperandAddress);
                    break;
                    
                    
                    
                    // BRANCH
                case 0x10 :
                    zOperand=peekImmediate();
                    INSTR_BPL(zOperand);
                    break;
                    
                case 0x30 :
                    zOperand=peekImmediate();
                    INSTR_BMI(zOperand);
                    break;
                    
                case 0x50 :
                    zOperand=peekImmediate();
                    INSTR_BVC(zOperand);
                    break;
                    
                case 0x70 :
                    zOperand=peekImmediate();
                    INSTR_BVS(zOperand);
                    break;
                    
                case 0x90 :
                    zOperand=peekImmediate();
                    INSTR_BCC(zOperand);
                    break;
                    
                case 0xB0 :
                    zOperand=peekImmediate();
                    INSTR_BCS(zOperand);
                    break;
                    
                case 0xD0 :
                    zOperand=peekImmediate();
                    INSTR_BNE(zOperand);
                    break;
                    
                case 0xF0 :
                    zOperand=peekImmediate();
                    INSTR_BEQ(zOperand);
                    break;
                    
                    
                 //sax
                    
                case 0x87 :
                case 0x97 :
                case 0x83 :
                case 0x8F :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    zOperandAddress=myLastOperandAddress;
                    INSTR_sax(zOperand, zOperandAddress);
                    break;
                   
                    
                                           //lax
                case 0xA3 :
                case 0xA7 :
                case 0xB3 :
                case 0xAF :
                case 0xB7 :
                case 0xBF :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    INSTR_lax(zOperand);
                    break;
 

                                       //sbx
                case 0xCB :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    INSTR_sbx(zOperand);
                    break;
 
					// nop w/operand
                case 0x04 : 
                case 0x0C : 
                case 0x14 :
                case 0x1C :
                case 0x1A :
                case 0x34 :
                case 0x3C : 
                case 0x3A :
                case 0x44 :
                case 0x54 :
                case 0x5C :
                case 0x5A :
                case 0x64 :
                case 0x74 :
                case 0x7C :
                case 0x7A :
                case 0x80 :
                case 0x82 :
                case 0x89 : 
                case 0xC2 :
                case 0xD4 :
                case 0xDC :
                case 0xDA :
                case 0xE2 :
                case 0xF4 :
                case 0xFC :
                case 0xFA :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    INSTR_nop(zOperand);
                    break;
                    
                                  //dcp
                case 0xC3 :
                case 0xC7 :
                case 0xCF :
                case 0xD3 :
                case 0xD7 :
                case 0xDB :
                case 0xDF :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    zOperandAddress=myLastOperandAddress;
                    INSTR_dcp(zOperand, zOperandAddress);
                    break;

                    
                    //isb
                case 0xE3 :
                case 0xE7 :
                case 0xEF :
                case 0xF3 :
                case 0xF7 :
                case 0xFB :
                case 0xFF :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    zOperandAddress=myLastOperandAddress;
                    INSTR_isb(zOperand, zOperandAddress);
                    break;
                    
                    
                    

                    //slo
                case 0x03 :
                case 0x07 :
                case 0x0F :
                case 0x13 :
                case 0x17 :
                case 0x1B :
                case 0x1F :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    zOperandAddress=myLastOperandAddress;
                    INSTR_slo(zOperand, zOperandAddress);
                    break;

                    //asr
                case 0x4B :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    INSTR_asr(zOperand);
                    break;    
                
                    
                    //rla
                case 0x27 :
                case 0x37 :
                case 0x2F :
                case 0x3F :
                case 0x3B :
                case 0x23 :
                case 0x33 :
                    zOperand=retrieveOperand(ourAddressingModeTable[IR]);
                    zOperandAddress=myLastOperandAddress;
                    INSTR_rla(zOperand, zOperandAddress);
                    break;
       
     
                    
                default :
                    String zMsg="Instruction not recognized - " + ourInstructionMnemonicTable[IR] +  " (0x" + Integer.toHexString(IR) + ") at " + Integer.toHexString((int)getPC()) + "\n" + "Instructions in this cycle=" + zCounter;
                    throw(new J6507Exception(J6507Exception.ExceptionType.INSTRUCTION_NOT_RECOGNIZED, zMsg));
                    //dbgout("Not recognized");
                    //assert(false);
                    //return true;
            }//end : switch
            
            
            
            if (DEBUG_MODE_ON) {
                int[] zPostSnapshot=getRegisterSnapshot();
                debugCommand(zOpPC, IR, myLastImmediateValues, zPreSnapshot, zPostSnapshot );//formatValueString(IR, myLastImmediateValues));
            }
            
            int zCycles=calculateCycles(IR) - myCyclesSignaled;
            if (zCycles<0)
            {
                int zDebug=20;
            }
            assert(zCycles>=0); //make sure we haven't signalled more cycles than have occurred
            myCurrentSystem.processorCycle(zCycles);
            myCyclesSignaled=0;
            
            
            if(((myExecutionStatus & MaskableInterruptBit)!=0) ||  ((myExecutionStatus & NonmaskableInterruptBit)!=0)) {
                // Yes, so handle the interrupt
                assert(false); //INTERRUPTS haven't been tested
                //interruptHandler();
            }
            
            // See if execution has been stopped
            if((myExecutionStatus & StopExecutionBit)!=0) {
                // Yes, so answer that everything finished fine
               // return true;
                //zReturn=zCounter;
                break;
            }
            
        }//end : for i loop
        
        return zCounter;//zReturn;
    }
    
    private void signalCycle() {
        myCurrentSystem.processorCycle(1);
        myCyclesSignaled++;
    }
    
    private int calculateCycles(int aIR) {
        int zCycleNum=ourInstructionProcessorCycleTable[aIR];
        boolean zIsBranch=(ourInstructionPageCrossDelay[aIR]==2);
        boolean zPageDependent=(ourInstructionPageCrossDelay[aIR]==1);
        if ((zIsBranch==true)) zCycleNum+=myBranchResult;
        else if ((zPageDependent==true)&&(myPageCrossed==true)) zCycleNum++;
        return zCycleNum;
        
    }
    
    private void debugCommand(int aOpPC, int aIR, int[] aOperands, int[] aPre, int[] aPost) {
        
        String zReturn="";
        zReturn="" + Integer.toHexString(aOpPC) + " " + ourInstructionMnemonicTable[IR];
        
        AddressingMode zMode=ourAddressingModeTable[aIR];
        switch (zMode) {
            
            case Implied : break;// (zMode==AddressingMode.Implied)
            
            case Absolute :
            case AbsoluteX :
            case AbsoluteY :
                zReturn+=" $" + Integer.toHexString(aOperands[0]) + Integer.toHexString(aOperands[1]);
                if (zMode==AddressingMode.AbsoluteX) zReturn+=",X";
                else if (zMode==AddressingMode.AbsoluteY) zReturn+=",Y";
                break;
                
                
            case Immediate :
                zReturn+=" #$" + Integer.toHexString(aOperands[0]);
                break;
                
            case Indirect :
                zReturn+=" ($" + Integer.toHexString(aOperands[0]) +  Integer.toHexString(aOperands[1]) + ")";
                break;
                
            case IndirectX :
                zReturn+=" ($" + Integer.toHexString(aOperands[0])  + ",X)";
                break;
                
            case IndirectY :
                zReturn+=" ($" + Integer.toHexString(aOperands[0])  + "),Y";
                break;
                
            case Relative :
                zReturn+=" $" + Integer.toHexString(aOperands[0])  + "";
                break;
                
            case Zero :
                zReturn+=" $" + Integer.toHexString(aOperands[0])  + "";
                break;
                
            case ZeroX :
                zReturn+=" $" + Integer.toHexString(aOperands[0])  + ",X";
                break;
                
            case ZeroY :
                zReturn+=" $" + Integer.toHexString(aOperands[0])  + ",Y";
                break;
                
                
        }//end :switch
       // myCurrentSystem.debugInstruction(zReturn, aPre, aPost);
        
        //return zReturn;
    }
    
    
    
    /**
     * Left over from tranlsation of Stella's 6502 emulator...
     * not even sure if the 6507 would need this method.
     */
//    private void interruptHandler()  //NO IDEA IF THIS IS FUNCTIONAL
//    {
//        // Handle the interrupt
//        if(((myExecutionStatus & MaskableInterruptBit)!=0) && !I) {
//            myCurrentSystem.processorCycle(7);// * mySystemCyclesPerProcessorCycle);
//            myCurrentSystem.poke((0x0100 + SP--), (PC - 1) >> 8);
//            myCurrentSystem.poke((0x0100 + SP--), (PC - 1) & 0x00ff);
//            myCurrentSystem.poke((0x0100 + SP--), getFlags() & (~0x10));
//            setD(false);
//            setI(true);
//            setPC((char)((myCurrentSystem.peek((char)0xFFFE)) | ((char)myCurrentSystem.peek((char)(0xFFFF)) << 8)));
//        } else if ((myExecutionStatus & NonmaskableInterruptBit)!=0) {
//            myCurrentSystem.processorCycle(7); //incrementCycles(7 * CurrentSystemCyclesPerProcessorCycle);
//            myCurrentSystem.poke((char)(0x0100 + SP--), (PC - 1) >> 8);
//            myCurrentSystem.poke((char)(0x0100 + SP--), (PC - 1) & 0x00ff);
//            myCurrentSystem.poke((char)(0x0100 + SP--), getFlags() & (~0x10));
//            setD(false);
//            setPC((char)((char)myCurrentSystem.peek((char)0xFFFA) | ((char)myCurrentSystem.peek((char)0xFFFB) << 8)));
//        }
//        // Clear the interrupt bits in myExecutionStatus
//        myExecutionStatus &= ~(MaskableInterruptBit | NonmaskableInterruptBit);
//    }
    
    
    /**
     * Resets the 6502.
     * The PC address is repositioned to the address supplied
     * by the previously designated IfcSystem.
     */
    public void reset() {
        myExecutionStatus=0;
        A=0x00;
        X=0x00;//.setValue(0);
        Y=0x00;//.setValue(0);
        
        SP=0xff;
        setFlags(0x20);
        
        
        setPC(myCurrentSystem.getResetPC());
        
        
    }
    
    private int peek(int aAddress, boolean aSignalCycle) { 
        assert(aAddress>=0);
        myReadLast=true;
        myLastOperandAddress=aAddress;
        if (aSignalCycle==true) signalCycle();
        return myCurrentSystem.peek(aAddress);      
    }
    
   
     private int peek(int aAddress) {
        return peek(aAddress, true);
    }
     
    
    private int peekImmediate() {
        
        int zReturn=peek(PC); // +1 cycle
        PC++;
        myLastImmediateValues[1]=myLastImmediateValues[0];
        myLastImmediateValues[0]=zReturn;
        return zReturn;
    }
    
    private int peekZeroPage() { //Total : 2 cycles
        int zAddr=peekImmediate(); // +1 cycle
        return peek(zAddr); // +1 cycle
    }
    
    private int peekZeroPage(int aAdd) {
        int zAddr=peekImmediate(); // +1 cycle
        peek(zAddr); // +1 cycle        
        zAddr+=aAdd;
        zAddr &= 0xFF;
        return peek(zAddr); // +1 cycle
    }
    
    private int peekAbsolute() {
        int zLowByte=peekImmediate(); // +1 cycle
        int zHighByte=peekImmediate(); // +1 cycle
        int zAddr=(zLowByte | (zHighByte << 8));
      
       // peek(zAddr); //FOR CYCLE ACCOUNTING PURPOSES?
        
        myPageCrossed=false;
        return peek(zAddr); // +1 cycle
    }
    
    private int peekAbsoluteJMP() {
        int zLowByte=peekImmediate(); // +1 cycle
        int zHighByte=peekImmediate(); // +1 cycle
        int zAddr=(zLowByte | (zHighByte << 8));
      
       // peek(zAddr); //FOR CYCLE ACCOUNTING PURPOSES?
        
        myPageCrossed=false;
        return peek(zAddr, false); // +0 cycle
    }
    
    private int peekAbsoluteIndex(int aAdd) {
        int zLowByte=peekImmediate(); // +1 cycle
        int zHighByte=peekImmediate();  // +1 cycle
        int zAddr=(zLowByte | (zHighByte << 8));
        zAddr+=aAdd;
       
        if (zLowByte + aAdd > 0xFF) 
        {
             peek(zAddr); // +1 cycle - FOR CYCLE ACCOUNTING PURPOSES?
            myPageCrossed=true;
        }//end : page crossed
        else myPageCrossed=false;
       
        return peek(zAddr); // +1
    }
    
    private int peekIndirect() {
        int zLowByte=peekImmediate(); // +1 cycle
        int zHighByte=peekImmediate(); // +1 cycle
        int zAddr=(zLowByte | (zHighByte << 8));
        int zLowByteB=peek(zAddr); // +1 cycle
        int zHighByteB=peek((zAddr+1)); //+1 cycle
        int zAddrB=(zLowByteB | (zHighByteB << 8));
        return peek(zAddrB, false); // +0 cycle
    }
    
    private int peekIndirectX() {
        int zZeroPage=(int)((peekImmediate() + X)&0xFF); //+1 cycle //the & 0xFF means address is confined to zero page
        int zLowByte=peek(zZeroPage); // +1 cycle
        int zHighByte=peek(((zZeroPage+1)&0xFF)); // +1 cycle
        int zAddr=(zLowByte | (zHighByte<<8));
        
        int zReturn= peek(zAddr); // +1 cycle
       
        return zReturn;
    }
    
  /*   private int peekIndirectQuasiY(int aAdder) //NOT A REAL INSTRUCTION
     {
         char zZeroPage=(peekImmediate());
         int zLowByte=peek(zZeroPage);
         int zHighByte=peek((zZeroPage+1));
         int zAddr=(zLowByte | (zHighByte<<8)) + aAdder;
         if (zLowByte + aAdder > 0xFF) peek(zAddr); //CYCLE?
         return peek(zAddr);
     }
   */
    
    private int peekIndirectY() {
        int zZeroPage=(peekImmediate()); // +1 cycle
        int zLowByte=peek(zZeroPage); // +1 cycle
        int zHighByte=peek((zZeroPage+1)); // +1 cycle
        int zAddr=(zLowByte | (zHighByte<<8)) + Y;
        if (zLowByte + Y > 0xFF) 
        {
            peek(zAddr); // (+1 cycle)
            myPageCrossed=true;
        }//end : page cross
        else myPageCrossed=false;
        return peek(zAddr); // +1 cycle
    }
    
    private int peekRelative() //note : actually retrieves the destination PC; not sure if this is used
    {
        int zOldPC=PC;
        int zByte=peekImmediate(); // +1
        int zAdd=toSignedByteValue(zByte);
        return zOldPC + zAdd;
        
    }
    
    /**
     * This method is used to convert from an unsigned byte value to a signed byte value.
     * @param aUnsignedByteValue The value that would be represented if a certain byte were unsigned.
     * @return The value that would be represented if the same certain byte were signed. 
     */
    private static int toSignedByteValue(int aUnsignedByteValue) {
        assert(aUnsignedByteValue>=0);
        assert(aUnsignedByteValue<256);
        if ((aUnsignedByteValue>=0)&&(aUnsignedByteValue<=127)) return aUnsignedByteValue;
        else {
            return aUnsignedByteValue - 256;
        }//end : is negative
    }
    
    
    /**
     * A method called from the execute() method that will retrieve the "operand"
     * value referred to by the instruction.
     * It will also set the myLastOperandAddress variable to wherever it finds this value.
     * @param aMode Addressing mode of the instruction
     * @return The value of the operand.
     */
    private int retrieveOperand(AddressingMode aMode) {
        if (aMode==AddressingMode.Immediate) return peekImmediate();
        else if (aMode==AddressingMode.Zero) return peekZeroPage();
        else if (aMode==AddressingMode.ZeroX) return peekZeroPage(X);
        else if (aMode==AddressingMode.ZeroY) return peekZeroPage(Y);
        else if (aMode==AddressingMode.Indirect) return peekIndirect();
        //  else if (aMode==AddressingMode.IndirectX) return peekIndirectQuasiY(X);  //TODO : change back to normal
        else if (aMode==AddressingMode.IndirectX) return peekIndirectX();
        else if (aMode==AddressingMode.IndirectY) return peekIndirectY();
        else if (aMode==AddressingMode.Absolute) return peekAbsolute();
        else if (aMode==AddressingMode.AbsoluteX) return peekAbsoluteIndex(X);
        else if (aMode==AddressingMode.AbsoluteY) return peekAbsoluteIndex(Y);
        else if (aMode==AddressingMode.Relative) return peekRelative();
        else {
            assert(false);
            return 0;
        }//end : error
        //else if (aMode==AddressingMode.)
        
    }
    
    
  /*  private void poke(int aAddress, int aByteValue) {
        
        poke((char)aAddress, aByteValue);
       
    }
   */
    
    private void poke(int aAddress, int aByteValue) {
         assert((aByteValue<0x100)&&(aByteValue>=0x00));
        if (aAddress>=0) {
      
            myCurrentSystem.poke(aAddress, aByteValue);
            //TODO : mask all bytes going in?
        }//end >=0
       
        myReadLast=false;
        
        
      //  poke((int)aAddress, aByteValue);
    }
    
    
    /**
     * Sets the flags as if all the flags were bits of a single byte.
     * @param aByteValue The byte value to set the flags to.  Each bit represents a single flag.
     */
    private void setFlags(int aByteValue) {
        N = ((aByteValue & 0x80)!=0);
        V = ((aByteValue & 0x40)!=0);
        B = ((aByteValue & 0x10)!=0); //;  The 6507's B flag always true
        D = ((aByteValue & 0x08)!=0);
        I = ((aByteValue & 0x04)!=0);
        notZ = !((aByteValue & 0x02)!=0);
        C = ((aByteValue & 0x01)!=0);
    }
    
    /**
     * Retrieves the flag values as if they were part of a single byte.  Each bit 
     * represents a flag.
     * @return All of the flags, combined in a single byte
     */
    private int getFlags() {
        
        int ps=0x20;
        if(N) ps |= 0x80;
        if(V) ps |= 0x40;
        if(B) ps |= 0x10;
        if(D) ps |= 0x08;
        if(I) ps |= 0x04;
        if(!notZ) ps |= 0x02;
        if(C) ps |= 0x01;
        
        return ps;
    }
    
    
    
    private static boolean getBit(int aByte, int aBitNumber) {
        //boolean zReturn=false;
        return ((aByte & (0x01 << aBitNumber))!=0);
    }
    
    
    
    //============== INSTRUCTIONS ============================
    
    
    private void INSTR_ADC(int operand) 
    {
        /*unsigned byte*/ int oldA = A;
        assert((operand>=0)&&(operand<0x100));
        if(!D) //not decimal
        {
            int zSignedSum=(int)((byte)A + operand);
            if (C==true) zSignedSum++;
            
            //short sum = (short)(((byte)A) + (short)((byte)operand) + (C ? 1 : 0));
            V = ((zSignedSum > 127) || (zSignedSum < -128)); //overflow
            
            int zUSum = A +operand;
            if (C==true) zUSum++;
            //(short)((short)A + (short)operand + (C ? 1 : 0));
            
            setC(zUSum > 0xff);
            setA(zUSum & 0xFF);
           
            setNotZ((zUSum & 0xff)!=0);
            setN(getBit(A, 7));
        } else {
            
            int sum = BCDTable[0][A] + BCDTable[0][operand] + (C ? 1 : 0);
            
            setC(sum > 99);
            setA(BCDTable[1][sum & 0xff]);
            setNotZ(A!=0);
            //  N = (A.getBit(7));
            setN(getBit(A, 7));
            V=(((oldA ^ A) & 0x80)!=0) && (((A ^ operand) & 0x80)!=0);
        }
    }
    
    private void INSTR_SBC(int operand) { 
       int oldA = A & 0xFF;
        assert((operand>=0)&&(operand<0x100));
        if(!D) {
          //TODO: This is a very awkward method...needs to be more straight-forward/transparent (JLA)
            
            int zRevOperand = (~operand) & 0xFF; 
            int zAmountToAdd=toSignedByteValue(zRevOperand) + (C ? 1 : 0); //if carry is on, amountToAdd= -1 * amountToSubtract, else it's one less (i.e. more negative)
            
            int zSignedResult = toSignedByteValue(A) + zAmountToAdd ;       
            setV(((zSignedResult > 127) || (zSignedResult < -128)));
            
            
         
            int zNewA = A + zAmountToAdd;
            
           
            int zAmountToSubtract=operand + (C ? 0 : 1);  
            setC(zAmountToSubtract<= oldA);
           
            
            //setC(!(difference > 0xff));
            setA(zNewA & 0xFF);
            setNotZ(A!=0);
            setN((A & 0x80)!=0);
        } else {
            int difference = BCDTable[0][A] - BCDTable[0][operand] - (C ? 0 : 1);
            
            if(difference < 0)
                difference += 100;
            
            setA(BCDTable[1][difference]);
            setNotZ(A!=0);
            setN((A & 0x80)!=0);
            
            setC((oldA >= (operand + (C ? 0 : 1))));
            setV((((oldA ^ A) & 0x80)!=0) && (((A ^ operand) & 0x80)!=0));
        }
    }
    
    
    private void INSTR_LDA(int aValue) {
        setA(aValue);
        notZ = (A!=0);
        N = ((A & 0x80)!=0);
    }
    
    
    private void INSTR_LDX(int operand) {
        assert(operand<0x100);
        setX(operand);
        notZ = (X!=0);
        N = ((X & 0x80)!=0);
        
    }//::
    
    private void INSTR_LDY(int operand) {
        
        Y = operand;
        notZ = (Y!=0);
        N = ((Y & 0x80)!=0);
        
    }//::
    
    
    // ================

    
    private void INSTR_AND(int aValue) { //OK
        int zNewA=getA() & aValue;
        setA(zNewA);
        setNotZ(zNewA!=0);
        setN((zNewA & 0x80)!=0);
    }
    
    private void INSTR_EOR(int aValue) { //OK
        int zNewA=getA() ^ aValue;
        setA(zNewA);
        setNotZ(zNewA!=0);
        setN((zNewA & 0x80)!=0);

    }
    
    private void INSTR_ORA(int aValue) { //OK
        int zNewA=getA() | aValue;
        setA(zNewA);
        setNotZ(zNewA!=0);
        setN((zNewA & 0x80)!=0);
//  A |= operand;
        // notZ = (A!=0);
        // N = ((A & 0x80)!=0);
    }
    

 

    
    private void INSTR_ASL(int aValue, int operandAddress) { //OK
        // Set carry flag according to the left-most bit in value
        setC(aValue & 0x80);
        
        aValue <<= 1;
        aValue &= 0xFF;
        poke(operandAddress, aValue);
        
        setNotZ(aValue!=0);
        setN(aValue & 0x80);
    }
    
    private void INSTR_ASLA() {
        // Set carry flag according to the left-most bit in A
        setC(A & 0x80);
        
        int zNewA= getA()  << 1;
        zNewA&=0xFF;
        setA(zNewA);
        
        setNotZ(A!=0);
        setN((A & 0x80)!=0);
    }
    
    

    
    private void branch(boolean aDoBranch, int aDelta) {
        if(aDoBranch==true) {
            peek(PC);
            int address = PC + toSignedByteValue(aDelta);
            if(notSamePage(PC, address)) myBranchResult=2;
            else myBranchResult=1;//myPageCrossed=false; //  peek((PC & 0xFF00) | (address & 0x00FF));
            setPC(address);
        } else myBranchResult=0;
    }
    
    
    private void INSTR_BCC(int operand) { branch(!C, operand); }    
    private void INSTR_BCS(int operand) { branch(C, operand);  }    
    private void INSTR_BEQ(int operand) { branch(!notZ, operand);  }    
    private void INSTR_BMI(int operand) { branch(N, operand);  }    
    private void INSTR_BNE(int operand) { branch(notZ, operand);  }    
    private void INSTR_BPL(int operand) { branch(!N, operand);   }    
    private void INSTR_BVC(int operand) { branch(!V, operand);   }    
    private void INSTR_BVS(int operand) { branch(V, operand);   }
    
    
    
    
    
    
    
    
    
    
    
    
    
    private void INSTR_BIT(int operand) { //OK
        setNotZ(A & operand);
        setN(operand & 0x80);
        setV(operand & 0x40);
    }
    
    private void INSTR_BRK() { //OK
        peek(PC++);
      //  System.out.println("6507 DEBUG : BRK");
        B = true;
        
        poke(0x0100 + SPdec(), PC >> 8);
        poke(0x0100 + SPdec(), PC & 0x00ff);
        poke(0x0100 + SPdec(), getFlags());
        
        I = true;
        
        PC=peek(0xfffe);
        PC |= (peek(0xffff) << 8);
    }
    
    private void INSTR_CLC() {setC(false); }
    private void INSTR_CLD() {setD(false);   }    
    private void INSTR_CLI() {setI(false);   }    
    private void INSTR_CLV() {setV(false); }
    
    private void INSTR_SEC() { setC(true); }    
    private void INSTR_SED() { setD(true); }    
    private void INSTR_SEI() { setI(true); }

    
    
    private void INSTR_CMP(int operand) { //OK
        int value = A - operand;
        
        setNotZ(value);
        setN(value & 0x0080);
        setC(((value & 0x0100)==0));
    }
    
    private void INSTR_CPX(int operand) { //OK
        int value = X - operand;
        
        setNotZ(value);
        setN(value & 0x0080);
        setC((value & 0x0100)==0);
    }
    
    private void INSTR_CPY(int operand) { //OK
        int value = Y - operand;
        
        setNotZ(value);
        setN(value & 0x0080);
        setC((value & 0x0100)==0);
    }
    

    
    private void INSTR_DEC(int operand, int operandAddress) { //OK
       int value = operand - 1;
        value &= 0xFF;
        poke(operandAddress, value);
        
        setNotZ(value);
        setN(value & 0x80);
    }
    
    private void INSTR_DEX() { //OK
        X--;
        X&=0xFF; //masking, in case it went below zero
        notZ = (X!=0);
        N = ((X & 0x80)!=0);
    }
    
    
    private void INSTR_DEY() { //OK
        Y--;
        
        Y&=0xFF; //masking, in case it went below zero
        notZ = (Y!=0);
        N = ((Y & 0x80)!=0);
    }
    
    
    
    private void INSTR_INC(int operand, int operandAddress) { //ok
        int value = operand + 1;
        value &=0xFF;
        poke(operandAddress, value);
        
        setNotZ(value);
        setN(value & 0x80);
    }
    
    private void INSTR_INX() { //OK
        X++;
        X &=0xFF;
        assert(X<0x100);
        notZ = (X!=0);
        N = ((X & 0x80)!=0);
    }
    
    private void INSTR_INY() { //OK
        Y++;
        Y &=0xFF;
        notZ = (Y!=0);
        N = ((Y & 0x80)!=0);
    }

    
    private void INSTR_JMP(int operand, int operandAddress) { //OK
        PC=operandAddress;
    }
    
    private void INSTR_JSR() { //OK
       int low = peekImmediate();//PC++);
        peek(0x0100 + SP);
      
        // It seems that the 650x does not push the address of the next instruction
        // on the stack it actually pushes the address of the next instruction
        // minus one.  This is compensated for in the RTS instruction
        poke(0x0100 + SPdec(), PC >>> 8);
        poke(0x0100 + SPdec(), PC & 0xff);
        int high=peekImmediate();
        PC=(low | (high << 8));
    }
    
    private void INSTR_RTS() { //OK
        peek(0x0100 + SPinc());
        
        int zAddr=0;
        
        zAddr=peek(0x100 + SPinc());
        //setPC(zAddr);
        
        int zNewPC= (zAddr | (peek(0x0100 + SP) << 8));
        setPC(zNewPC);
        // debugMsg("RTS", zNewPC);
        peek(PC++);
    }
    
    

    
    
    
    private void INSTR_LSR(int operand, int operandAddress) { //OK
        // Set carry flag according to the right-most bit in value
        setC(operand & 0x01);
        
        operand = (operand >> 1) & 0x7f;
        poke(operandAddress, operand);
        
        notZ = (operand!=0);
        setN(operand & 0x80);
    }
    
    
    private void INSTR_LSRA() { //OK
        // Set carry flag according to the right-most bit
        setC(A & 0x01);
        
        setA( (getA() >> 1) & 0x7f);
        
        setNotZ(A!=0);
        setN((A & 0x80)!=0);
    }
    
    

    
    private void INSTR_NOP() {  //OK
    }
    
    
    
    private void INSTR_PHA() { //OK
        poke(0x0100 + SPdec(), A);
    }
    
    private void INSTR_PHP() { //OK
        poke(0x0100 + SPdec(), getFlags());
    }
    
    private void INSTR_PLA() { //OK
        peek(0x0100 + SPinc());
        setA(peek(0x0100 + SP));
        setNotZ(A!=0);
        setN((A & 0x80)!=0);
    }
    
    private void INSTR_PLP() { //OK
        peek(0x0100 + SPinc());
        setFlags(peek(0x0100 + SP));
    }
    

    
    private void INSTR_ROL(int operand, int operandAddress) { //OK
        boolean oldC=C;
        
        // Set carry flag according to the left-most bit in operand
        setC(operand & 0x80);
        
        operand = ((operand << 1) | (oldC ? 1 : 0))& 0xFF;
        poke(operandAddress, operand);
        
        notZ = (operand!=0);
        setN(operand & 0x80);
    }
    
    private void INSTR_ROLA() { //OK
        boolean oldC=C;
        
        // Set carry flag according to the left-most bit
        setC(A & 0x80);
        int zNewA=(getA() << 1) | (oldC ? 1 : 0);
        setA(zNewA & 0xFF);
        
        setNotZ(A!=0);
        N = ((A & 0x80)!=0);
    }
    
    private void INSTR_ROR(int operand, int operandAddress) { 
        boolean oldC=C;
        
        // Set carry flag according to the right-most bit
        setC(operand & 0x01);
        
        operand = ((operand >> 1) & 0x7f) | (oldC ? 0x80 : 0x00);
        poke(operandAddress, operand);
        
        notZ = (operand!=0);
        setN(operand & 0x80);
    }
    
    private void INSTR_RORA() {
        boolean oldC=C;
        
        // Set carry flag according to the right-most bit
        setC(A & 0x01);
        int zOldA=getA();
        int zNewA=((getA() >> 1) & 0x7f) | (oldC ? 0x80 : 0x00);
        setA(zNewA);
        notZ = (zNewA!=0);
        N = ((zNewA & 0x80)!=0);
    }
    

    
    private void INSTR_RTI() { 
        peek(0x0100 + SPinc());
        setFlags(peek(0x0100 + SPinc()));
        PC=peek(0x0100 + SPinc());
        PC |= (peek(0x0100 + SP) << 8);
    }
    
    
  
    

    
   
    
    private void INSTR_STA(int operand, int operandAddress) { //OK
     
        poke(operandAddress, getA());
    }
    
    private void INSTR_STX(int operand, int operandAddress) { //ok
        poke(operandAddress, X);
    }
    
    private void INSTR_STY(int operand, int operandAddress) { //ok
        poke(operandAddress, Y);
    }
    
    private void INSTR_TAX() { //OK
        X = A;
        notZ = (X!=0);
        N = ((X & 0x80)!=0);
    }
    
    private void INSTR_TAY() { //OK
        Y = A;
        notZ = (Y!=0);
        N = ((Y & 0x80)!=0);
    }
    
    private void INSTR_TSX() { //OK
        X = SP;
        notZ = (X!=0);
        N = ((X & 0x80)!=0);
    }
    
    private void INSTR_TXA() { //OK
        setA(X);
        notZ = (A!=0);
        N = ((A & 0x80)!=0);
    }
    
    private void INSTR_TXS() { //OK
        setSP(X);
    }
    
    private void INSTR_TYA() { //OK
        setA(Y);
        notZ = (A!=0);
        N = ((A & 0x80)!=0);
    }
    
    
    
    
    
    
  // ************** UNOFFICIAL INSTRUCTIONS ****************************  
   
    private void INSTR_sax(int operand, int operandAddress) { 
        poke(operandAddress, A & X);
    }
     
    
   private void INSTR_lax(int aValue) { 
        setA(aValue);
        setX(aValue);
        notZ = (A!=0);
        N = ((A & 0x80)!=0);
    }
     
    

    private void INSTR_sbx(int operand) { 
        /*unsigned*/ int difference = ((A&X)&0xff)-operand;

            setC((difference & 0x100)==0);
            difference &= 0xff;
            setX(difference);
            setNotZ(difference!=0);
            setN((difference & 0x80)!=0);
    }


    private void INSTR_asr(int operand) { 
        int myA = A&operand;

            setC(myA & 0x01);
 	    myA = (myA >> 1) & 0x7f;

            setA(myA);
            setNotZ(myA!=0);
            setN((myA & 0x80)!=0);


     }
    
    
   private void INSTR_rla(int operand, int operandAddress) { //TODO: Double check code--it is untested
       int zValue = (operand << 1) | (C ? 1 : 0);
       poke(operandAddress, zValue);

       int zNewA = A & zValue; 
       setA(zNewA & 0xFF);
       setC(operand & 0x80);
       setNotZ(zNewA);
       setN(zNewA & 0x80);
     }
    

     private void INSTR_nop(int operand) {  //do nothing (??)
     }

     private void INSTR_dcp(int operand, int operandAddress) { //OK
        //this is DEC
       int value = operand - 1;
        value &= 0xFF;
        poke(operandAddress, value);
 
        //this is CMP
        value = A - value;
        
        setNotZ(value);
        setN(value & 0x0080);
        setC(((value & 0x0100)==0));
    }

     private void INSTR_isb(int operand, int operandAddress) { 
        // this is INC
// INSTR_INC(operand,operandAddress);
// INSTR_SBC(operand+1);
        int value = operand + 1;
        value &=0xFF;
        poke(operandAddress, value);
        


        // this is SBC
        int oldA = A;
        if(!D) {
          
            int zRevOperand = (~value) & 0xFF; 
         
            int Sdifference = toSignedByteValue(A) + toSignedByteValue(zRevOperand) + (C ? 1 : 0);
       
            setV(((Sdifference > 127) || (Sdifference < -128)));
            int zSBV=toSignedByteValue(zRevOperand);
         
            int difference = A + zSBV + (C ? 1 : 0);
            
           
            int zSubAmount=value + (C ? 0 : 1);
            setC(zSubAmount<= oldA);
            
            //setC(!(difference > 0xff));
            setA(difference & 0xFF);
            setNotZ(A!=0);
            setN((A & 0x80)!=0);
        } else {
            int difference = BCDTable[0][A&0xff] - BCDTable[0][value&0xff] - (C ? 0 : 1);
            
            if(difference < 0)
                difference += 100;
            
            setA(BCDTable[1][difference&0xff]);
            setNotZ(A!=0);
            setN((A & 0x80)!=0);
            
            setC((oldA >= (value + (C ? 0 : 1))));
            setV((((oldA ^ A) & 0x80)!=0) && (((A ^ value) & 0x80)!=0));
        }
    }



     private void INSTR_slo(int operand, int operandAddress) { 
        // Set carry flag according to the left-most bit in value
        setC(operand & 0x80);
        
        operand <<= 1;
        operand &= 0xFF;
        poke(operandAddress, operand);

        INSTR_ORA(operand);

    }

 
    
 
    //=================================================================================
    public static class J6507Exception extends Exception
    {        
       public enum ExceptionType { UNSPECIFIED, INSTRUCTION_NOT_RECOGNIZED};
       
       public String myMessage="";
       public ExceptionType myExceptionType=ExceptionType.UNSPECIFIED;
       
       public J6507Exception(ExceptionType aType, String aMsg)
       {
           super(aMsg);
           myExceptionType=aType;
           myMessage=aMsg;
       }
       
    }
    
    
    
}
