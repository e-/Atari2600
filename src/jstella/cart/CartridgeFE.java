//============================================================================
//
//   SSSS    tt          lll  lll
//  SS  SS   tt           ll   ll
//  SS     tttttt  eeee   ll   ll   aaaa
//   SSSS    tt   ee  ee  ll   ll      aa
//      SS   tt   eeeeee  ll   ll   aaaaa  --  "An Atari 2600 VCS Emulator"
//  SS  SS   tt   ee      ll   ll  aa  aa
//   SSSS     ttt  eeeee llll llll  aaaaa
//
// Copyright (c) 1995-2007 by Bradford W. Mott and the Stella team
//
// See the file "license" for information on usage and redistribution of
// this file, and for a DISCLAIMER OF ALL WARRANTIES.
//
// $Id: CartridgeFE.java,v 1.2 2007/08/12 04:51:29 mauvila Exp $
//============================================================================
package jstella.cart;

import jstella.core.*;


/**
 * Bankswitching method used by Activison's Robot Tank and Decathlon.
 *
 * Kevin Horton describes FE as follows:
 *
 * Used only on two carts (Robot Tank and Decathlon).  These
 * carts are very weird.  It does not use accesses to the stack
 * like was previously thought.  Instead, if you watch the called
 * addresses very carefully, you can see that they are either Dxxx
 * or Fxxx.  This determines the bank to use.  Just monitor A13 of
 * the processor and use it to determine your bank! :-)  Of course
 * the 6507 in the 2600 does not have an A13, so the cart must have
 * an extra bit in the ROM matrix to tell when to switch banks.
 * There is *no* way to determine which bank you want to be in from
 * monitoring the bus.
 *
 * @author  Bradford W. Mott
 * @version $Id: CartridgeFE.java,v 1.2 2007/08/12 04:51:29 mauvila Exp $
 */
public class CartridgeFE extends Cartridge {
    private final static long serialVersionUID = 3716862330037765895L;
    
    private int[] myImage=new int[8192];
    
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public CartridgeFE(int[] image) {
        
        myImage=copyImage(image);
        
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    public String name() {
        return "CartridgeFE";
        
    }
    
    public void reset() {
        
    }
    
    
    public void install(jstella.core.JSSystem system) {
        mySystem = system;
        // Map ROM image into the system
        addIndirectAccess(0x1000, 0x2000);
        
        
    }
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int peek(int address) {
        // The bank is determined by A13 of the processor
        return myImage[(address & 0x0FFF) + (((address & 0x2000) == 0) ? 4096 : 0)];
    }
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void poke(int address, int aByteValue) {
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected void setCurrentBank(int bank) {
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected int getCurrentBank() {
        return 0;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected int bankCount() {
        return 1;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public boolean patch(int address, int aValue) {
        
        myImage[(address & 0x0FFF) + (((address & 0x2000) == 0) ? 4096 : 0)] = aValue;
        return true;
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int[] getImage() {
        
        return myImage;
    }
    
    
    
    
    
}
