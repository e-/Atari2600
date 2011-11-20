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
// $Id: Cartridge2K.java,v 1.3 2007/08/20 05:11:26 mauvila Exp $
//============================================================================

package jstella.cart;

import jstella.core.JSSystem;
import jstella.j6507.IfcSystem;
import static jstella.core.JSConstants.*;

/**
 * This is the standard Atari 2K cartridge.  These cartridges
 * are not bankswitched, however, the data repeats twice in the
 * 2600's 4K cartridge addressing space.
 *
 * @author  Bradford W. Mott
 * @version $Id: Cartridge2K.java,v 1.3 2007/08/20 05:11:26 mauvila Exp $
 */
class Cartridge2K extends Cartridge {
    private final static long serialVersionUID = 6519396049383803731L;
    
    
    private int[] myImage=new int[2048];
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public Cartridge2K(int[] image) {
       
         myImage=copyImage(image);  // Copy the ROM image into my buffer
    }
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public String name() {
        return "Cartridge2K";
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void reset() {
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void install(jstella.core.JSSystem system) {
        mySystem = system;
  
        // Make sure the system we're being installed in has a page size that'll work
        assert((0x1000 & PAGE_MASK) == 0);
      
        addDirectPeekAccess(0x1000, 0x2000, myImage, 0x07FF);
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int peek(int address) {
        return myImage[address & 0x07FF];
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void poke(int aC, int aB) {
        // This is ROM so poking has no effect :-)
    }
  
    
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
        return 1;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public boolean patch(int address, int value) {
        myImage[address & 0x07FF] = value;
        return true;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int[] getImage() {
      
        return myImage;
    }
    
    
}
