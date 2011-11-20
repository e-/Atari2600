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
// $Id: CartridgeF8.java,v 1.2 2007/08/12 04:51:29 mauvila Exp $
//============================================================================
package jstella.cart;
import jstella.j6507.*;
import jstella.j6507.IfcSystem;

/**
 * Cartridge class used for Atari's 8K bankswitched games.  There
 * are two 4K banks.
 *
 * @author  Bradford W. Mott
 * @version $Id: CartridgeF8.java,v 1.2 2007/08/12 04:51:29 mauvila Exp $
 */

public class CartridgeF8 extends Cartridge {
    private final static long serialVersionUID = 3629215127350071982L;
    
    
    
    
    
    private int myCurrentBank=0;
    
    
    private int myResetBank=0;
    
    
    private int[] myImage=new int[8192];
    
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public CartridgeF8(int[] image, boolean swapbanks) {
        
        myImage=copyImage(image);
        
        
        // Normally bank 1 is the reset bank, unless we're dealing with ROMs
        // that have been incorrectly created with banks in the opposite order
        myResetBank = swapbanks ? 0 : 1;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    public String name() {
        return "CartridgeF8";
        
    }
    
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void reset() {
        // Upon reset we switch to the reset bank
        setCurrentBank((char)myResetBank);
    }
    
    
    public void install(jstella.core.JSSystem system) {
        mySystem = system;
        
        // Map ROM image into the system
        addIndirectAccess(0x1FF8, 0x2000);
        
        setCurrentBank(1);
    }
    
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int peek(int address) {
        int zNewAddress = address & 0x0FFF;
        
        // Switch banks if necessary
        switch(zNewAddress) {
            case 0x0FF8:
                // Set the current bank to the lower 4k bank
                setCurrentBank(0);
                break;
                
            case 0x0FF9:
                // Set the current bank to the upper 4k bank
                setCurrentBank(1);
                break;
                
            default:
                break;
        }
        
        return myImage[(myCurrentBank * 4096) + zNewAddress];
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void poke(int address, int aByteValue) {
        int zNewAddress = address & 0x0FFF;
        
        // Switch banks if necessary
        switch(zNewAddress) {
            case 0x0FF8:
                // Set the current bank to the lower 4k bank
                setCurrentBank(0);
                break;
                
            case 0x0FF9:
                // Set the current bank to the upper 4k bank
                setCurrentBank(1);
                break;
                
            default:
                break;
        }
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected void setCurrentBank(int bank) {
        if(myBankLocked) return;
        
        
        myCurrentBank = bank;
        
        
        
        addDirectPeekAccess(0x1000, 0x1FF8, myImage, 0x0FFF, myCurrentBank * 4096);  // Map ROM image into the system
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected int getCurrentBank() {
        return myCurrentBank;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected int bankCount() {
        return 2;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public boolean patch(int address, int aValue) {
        address &= 0xfff;
        myImage[myCurrentBank * 4096 + address] = aValue;
        setCurrentBank(myCurrentBank);
        return true;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int[] getImage() {
        
        return myImage;
    }
}

