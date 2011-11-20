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
// $Id: Cartridge4K.java,v 1.2 2007/08/12 04:51:29 mauvila Exp $
//============================================================================
package jstella.cart;


import jstella.j6507.*;
import jstella.j6507.IfcSystem;

public class Cartridge4K extends Cartridge {
    private final static long serialVersionUID = -3240308689643904765L;
    
    private final static int CARTRIDGE_MASK_VALUE=0x0FFF;
    private final static int CARTRIDGE_SIZE=4096;
    private final static int CARTRIDGE_BANK_COUNT=1;
    
    
    
    
    // The 4K ROM image for the cartridge
    int[] myImage=new int[CARTRIDGE_SIZE];
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public Cartridge4K(int[] image) {
        
        myImage=copyImage(image);
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public String name() {
        return "Cartridge4K";
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void reset() {
    }
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
    public void install(jstella.core.JSSystem system) {
        mySystem = system;        
        addDirectPeekAccess(0x1000, 0x2000, myImage, CARTRIDGE_MASK_VALUE);  // Map ROM image into the system
        
    }
    
    
    
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int peek(int address) {
        return myImage[address & CARTRIDGE_MASK_VALUE];
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void poke(int aC, int aB) {
        // This is ROM so poking has no effect :-)
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected void setCurrentBank(int bank) {
        // Doesn't support bankswitching
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected int getCurrentBank() {
        // Doesn't support bankswitching
        return 0;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected int bankCount() {
        return CARTRIDGE_BANK_COUNT;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public boolean patch(int address,int value) {
        myImage[address & CARTRIDGE_MASK_VALUE] = value;
        return true;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int[] getImage() {
        
        return myImage;
    }
    
    
    
}
