/*
 * IfcSystem.java
 *
 * Created on July 10, 2007, 10:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jstella.j6507;

/**
 * This is an interface for the 6507 (i.e. the CPU) to interact with other classes.
 * The class that overrides this is the eyes and ears (and hands) of
 * the CPU.  The CPU will peek and poke at given addresses, and it
 * is up to the implementing class to determine what to do for a given
 * address.
 * @author J.L. Allen
 */
public interface IfcSystem {
    
    /**
     * This method is called by the processor to read a byte from 
     * the given address.
     * <p>PEEK means READ</p>
     * @param aAddress The address (16 bit) to read
     * @return The byte (in int form) at the specified address.
     */
    public int peek(int aAddress);
    /**
     * This method is called by the processor to write a byte value to 
     * the specified address.
     * <p>POKE means WRITE</p>
     * @param aAddress The address (16 bit) to write to.
     * @param aByteValue The byte value (in int form) to write.
     */
    public void poke(int aAddress, int aByteValue);
    /**
     * This method is called by the processor to indicate that 
     * the specified number of processor cycles have elapsed.
     * @param aCyclesElapsed number of processor cycles that have elapsed
     */
    public void processorCycle(int aCyclesElapsed);
  //  public void debugInstruction(String aInstruction);
  //  public void debugInstruction(String aInstruction, int[] aRegBefore, int[] aRegAfter);
    /**
     * This is called by the processor to determine which address
     * the processor should set its PC register (program counter) when
     * the processor is reset.
     * @return the PC value that the processor should use after being reset
     */
    public char getResetPC();
  }
