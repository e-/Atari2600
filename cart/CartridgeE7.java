
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
// $Id: CartridgeE7.java,v 1.2 2007/08/20 05:11:26 mauvila Exp $
//============================================================================
package jstella.cart;

import jstella.core.JSSystem;
import jstella.core.PageAccess;
import static jstella.core.JSConstants.*;

/**
 * This is the cartridge class for M-Network bankswitched games.
 * In this bankswitching scheme the 2600's 4K cartridge address
 * space is broken into two 2K segments.
 *
 * Kevin Horton describes E7 as follows:
 *
 * Only M-Network used this scheme. This has to be the
 * most complex method used in any cart! :-)  It allows
 * for the capability of 2K of RAM; although it doesn't
 * have to be used (in fact, only one cart used it).
 * There are now 8 2K banks, instead of 4.  The last 2K
 * in the cart always points to the last 2K of the ROM
 * image, while the first 2K is selectable.  You access
 * 1FE0 to 1FE6 to select which 2K bank. Note that you
 * cannot select the last 2K of the ROM image into the
 * lower 2K of the cart!  Accessing 1FE7 selects 1K of
 * RAM at 1000-17FF instead of ROM!  The 2K of RAM is
 * broken up into two 1K sections.  One 1K section is
 * mapped in at 1000-17FF if 1FE7 has been accessed.
 * 1000-13FF is the write port, while 1400-17FF is the
 * read port.  The second 1K of RAM appears at 1800-19FF.
 * 1800-18FF is the write port while 1900-19FF is the
 * read port.  You select which 256 byte block appears
 * here by accessing 1FF8 to 1FFB.
 *
 * @author  Bradford W. Mott
 * @version $Id: CartridgeE7.java,v 1.2 2007/08/20 05:11:26 mauvila Exp $
 */
public class CartridgeE7 extends Cartridge {
    
    
    
    
    int[] myCurrentSlice=new int[2];
    
    // Indicates which 256 byte bank of RAM is being used
    int myCurrentRAM=0;
    
    // The 16K ROM image of the cartridge
    int[] myImage=new int[16384];
    
    // The 2048 bytes of RAM
    int[]  myRAM=new int[2048];
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    CartridgeE7(int[] image) {
        // Copy the ROM image into my buffer
        
        myImage=copyImage(image);
        
        
        // Initialize RAM with random values
        randomizeRAM(myRAM);
        
        
    }
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
    public String name() {
        
        return "CartridgeE7";
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void reset() {
        // Install some default banks for the RAM and first segment
        bankRAM(0);
        setCurrentBank(0);
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
    public void install(jstella.core.JSSystem system) {
        mySystem = system;
        
        // Map ROM image into the system
        
        addIndirectAccess(0x1FE0, 0x2000);
        addDirectPeekAccess(0x1A00, 0x1FE0, myImage, 0x07FF, 7 * 2048);
        
        myCurrentSlice[1] = 7;
        
        
        
        // Install some default banks for the RAM and first segment
        bankRAM(0);
        setCurrentBank(0);
        
        
        
        
        
    }
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int peek(int address) {
        address = (char)(address & 0x0FFF);
        
        // Switch banks if necessary
        if((address >= 0x0FE0) && (address <= 0x0FE7)) {
            setCurrentBank(address & 0x0007);
        } else if((address >= 0x0FE8) && (address <= 0x0FEB)) {
            bankRAM(address & 0x0003);
        }
        
        // NOTE: The following does not handle reading from RAM, however,
        // this function should never be called for RAM because of the
        // way page accessing has been setup
        return myImage[(myCurrentSlice[address >> 11] << 11) + (address & 0x07FF)];
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void poke(int address, int aByteValue) {
        address = (char)(address & 0x0FFF);
        
        // Switch banks if necessary
        if((address >= 0x0FE0) && (address <= 0x0FE7)) {
            setCurrentBank(address & 0x0007);
        } else if((address >= 0x0FE8) && (address <= 0x0FEB)) {
            bankRAM(address & 0x0003);
        }
        
        // NOTE: This does not handle writing to RAM, however, this
        // function should never be called for RAM because of the
        // way page accessing has been setup
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    void bankRAM(int bank) {
        // Remember what bank we're in
        myCurrentRAM = bank;
        int offset = bank << 8;
        int shift = PAGE_SHIFT;
        
        // Setup the page access methods for the current bank
        
        
        // Set the page accessing method for the 256 bytes of RAM writing pages
        addDirectPokeAccess(0x1800, 0x1900, myRAM, 0x00FF, 1024 + (bank << 8));
 /*
  for(int j = 0x1800; j < 0x1900; j += (1 << shift))
  {
  
    mySystem.setPageAccess(j >> shift, PageAccess.createDirectPokeAccess(this, myRAM, 1024 + offset + (j & 0x00FF)));
  }
  */
        
        // Set the page accessing method for the 256 bytes of RAM reading pages
        addDirectPeekAccess(0x1900, 0x1A00, myRAM, 0x00FF, 1024 + (bank << 8));
 /* for(int k = 0x1900; k < 0x1A00; k += (1 << shift))
  {
  
    mySystem.setPageAccess(k >> shift, PageAccess.createDirectPeekAccess(this, myRAM, 1024 + offset + (k & 0x00FF)));
  }
  */
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void setCurrentBank(int slice) {
        if(myBankLocked) return;
        
        // Remember what bank we're in
        myCurrentSlice[0] = slice;
        
        
        // Setup the page access methods for the current bank
        if(slice != 7) {
            
            
            // Map ROM image into first segment
            addDirectPeekAccess(0x1000, 0x1800, myImage, 0x07FF, slice << 11);
            
        } else {
            
            // Set the page accessing method for the 1K slice of RAM writing pages
            addDirectPokeAccess(0x1000, 0x1400, myRAM, 0x03FF);
            
            
            // Set the page accessing method for the 1K slice of RAM reading pages
            addDirectPeekAccess(0x1400, 0x1800, myRAM, 0x03FF);
            
        }
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int getCurrentBank() {
        return myCurrentSlice[0];
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int bankCount() {
        return 8;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public boolean patch(int address, int value) {
        address = (char)(address & 0x0FFF);
        myImage[(myCurrentSlice[address >> 11] << 11) + (address & 0x07FF)] = value;
        setCurrentBank(myCurrentSlice[0]);
        return true;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int[] getImage() {
        
        return myImage;
    }
    
}
