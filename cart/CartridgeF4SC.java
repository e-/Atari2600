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
// $Id: CartridgeF4SC.java,v 1.1 2007/08/12 04:51:29 mauvila Exp $
//============================================================================


package jstella.cart;
import jstella.j6507.*;
import jstella.j6507.IfcSystem;

/**
 * Cartridge class used for Atari's 8K bankswitched games.  There
 * are eight 4K banks.
 *
 * @author  Bradford W. Mott
 * @version $Id: CartridgeF4SC.java,v 1.1 2007/08/12 04:51:29 mauvila Exp $
 */

public class CartridgeF4SC extends Cartridge {
    private final static long serialVersionUID = 9208964210037667554L;
    private final static int CART_SIZE=32768;
    private final static String CART_NAME="CartridgeF4SC";
    
     private int[] myRAM=new int[128];
    
    
    private int myCurrentBank=0;
    
    
    
    
    
    private int[] myImage=new int[CART_SIZE];
    
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public CartridgeF4SC(int[] image) {
        // Copy the ROM image into my buffer
        
        
        myImage=copyImage(image);
        //assert(myImage.length == CART_SIZE);
        randomizeRAM(myRAM);
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    public String name() {
        
        return CART_NAME;
        
    }
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void reset() {
        // Upon reset we switch to the reset bank
        setCurrentBank(0);
    }
    
    
    public void install(jstella.core.JSSystem system) {
        mySystem = system;
        
        // Map ROM image into the system
        
        //The following sets up a PageAcces for the page containing the "Switch Bank" addresses"
        addIndirectAccess(0x1FF4, 0x2000);
        addDirectPokeAccess(0x1000, 0x1080, myRAM, 0x007F);
         addDirectPeekAccess(0x1080, 0x1100, myRAM, 0x007F);
        setCurrentBank(0); //This sets up addressing for Bank 0
    }
    
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int peek(int address) {
        address = address & 0x0FFF;

  // Switch banks if necessary
  if((address >= 0x0FF4) && (address <= 0x0FFB))
  {
    setCurrentBank(address - 0x0FF4);
  }

  return myImage[myCurrentBank * 4096 + address];

        
        
      
                
                
          
        
        
       
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void poke(int address, int aByteValue) {
        address = address & 0x0FFF;

  // Switch banks if necessary
  if((address >= 0x0FF4) && (address <= 0x0FFB))
  {
    setCurrentBank(address - 0x0FF4);
  }

        
        
       
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected void setCurrentBank(int bank) {
        if(myBankLocked) return;
        
        // Remember what bank we're in
        myCurrentBank = bank;
        
        // Map ROM image into the system
        addDirectPeekAccess(0x1100, 0x1FF4, myImage, 0x0FFF, myCurrentBank * 4096);
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected int getCurrentBank() {
        return myCurrentBank;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected int bankCount() {
        return 8;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public boolean patch(int address, int aValue) {
        address &= 0xfff;
        myImage[(myCurrentBank * 4096) + address] = aValue;
        setCurrentBank(myCurrentBank);
        return true;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int[] getImage() {
        
        return myImage;
    }
}






