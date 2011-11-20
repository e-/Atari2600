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
// $Id: CartridgeE0.java,v 1.2 2007/08/12 04:51:29 mauvila Exp $
//============================================================================
package jstella.cart;

import jstella.core.PageAccess;


/**
 * This is the cartridge class for Parker Brothers' 8K games.  In
 * this bankswitching scheme the 2600's 4K cartridge address space
 * is broken into four 1K segments.  The desired 1K slice of the
 * ROM is selected by accessing 1FE0 to 1FE7 for the first 1K.
 * 1FE8 to 1FEF selects the slice for the second 1K, and 1FF0 to
 * 1FF8 selects the slice for the third 1K.   The last 1K segment
 * always points to the last 1K of the ROM image.
 *
 * @author  Bradford W. Mott
 *
 */
public class CartridgeE0 extends Cartridge {
    private final static long serialVersionUID = 8410286352380616979L;
    
    
    private char myCurrentBank;
    
    
    private int myResetBank;
    
    
    private int[] myImage=new int[8192];
    
    private int[] myCurrentSlice=new int[4];
    
    
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public CartridgeE0(int[] image) {
        myImage=copyImage(image); // Copy the ROM image into my buffer
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    public String name() {
        return "CartridgeE0";
        
    }
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void reset() {
        
        // Setup segments to some default slices
        segmentZero(4);
        segmentOne(5);
        segmentTwo(6);
    }
    
    
    public void install(jstella.core.JSSystem system) {
        mySystem = system;
        
        myCurrentSlice[3] = 7;
        addDirectPeekAccess(0x1C00, 0x1FE0, myImage, 0x03FF, 7168);
        addIndirectAccess(0x1FE0, 0x2000);
        
        // Install some default slices for the other segments
        segmentZero(4);
        segmentOne(5);
        segmentTwo(6);
    }
    
    
    
    
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int peek(int address) {
        address = (char)(address & 0x0FFF);
        
        if(!myBankLocked) {
            // Switch banks if necessary
            if((address >= 0x0FE0) && (address <= 0x0FE7)) {
                segmentZero(address & 0x0007);
            } else if((address >= 0x0FE8) && (address <= 0x0FEF)) {
                segmentOne(address & 0x0007);
            } else if((address >= 0x0FF0) && (address <= 0x0FF7)) {
                segmentTwo(address & 0x0007);
            }
        }
        
        return myImage[(myCurrentSlice[address >> 10] << 10) + (address & 0x03FF)];
    }
    
    
    public void poke(int address, int aByteValue) {
        address = (char)(address & 0x0FFF);
        
        if(!myBankLocked) {
            // Switch banks if necessary
            if((address >= 0x0FE0) && (address <= 0x0FE7)) {
                segmentZero(address & 0x0007);
            } else if((address >= 0x0FE8) && (address <= 0x0FEF)) {
                segmentOne(address & 0x0007);
            } else if((address >= 0x0FF0) && (address <= 0x0FF7)) {
                segmentTwo(address & 0x0007);
            }
        }
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private void segmentZero(int slice) {
      
        myCurrentSlice[0] = slice;        
        addDirectPeekAccess(0x1000, 0x1400, myImage, 0x03FF, slice << 10);
     
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private void segmentOne(int slice) {
      
        myCurrentSlice[1] = slice;        
        addDirectPeekAccess(0x1400, 0x1800, myImage, 0x03FF, slice << 10);
        
    
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    void segmentTwo(int slice) {
     
        myCurrentSlice[2] = slice;
        addDirectPeekAccess(0x1800, 0x1C00, myImage, 0x03FF, slice << 10);        
     
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected void setCurrentBank(int bank) {
        // FIXME - get this working, so we can debug E0 carts
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected int getCurrentBank() {
        // FIXME - get this working, so we can debug E0 carts
        return 0;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected int bankCount() {
        
// FIXME - get this working, so we can debug E0 carts
        return 1;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public boolean patch(int address, int aValue) {
        address = (char)(address & 0x0FFF);
        myImage[(myCurrentSlice[address >> 10] << 10) + (address & 0x03FF)] = aValue;
        return true;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int[] getImage() {
        
        return myImage;
    }
    
    
    
    
    
}
