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
// $Id: CartridgeF6.java,v 1.3 2007/09/08 05:47:50 mauvila Exp $
//============================================================================


package jstella.cart;
import jstella.j6507.*;
import jstella.j6507.IfcSystem;

/**
 * Cartridge class used for Atari's 8K bankswitched games.  There
 * are two 4K banks.
 *
 * @author  Bradford W. Mott
 * @version $Id: CartridgeF6.java,v 1.3 2007/09/08 05:47:50 mauvila Exp $
 */

public class CartridgeF6 extends Cartridge {
    private final static long serialVersionUID = 9208964210037667554L;
    private final static int CART_SIZE=16384;
    private final static String CART_NAME="CartridgeF6";
    
    
    
    
    private int myCurrentBank=0;
    
    
    
    
    
    private int[] myImage=new int[CART_SIZE];
    
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public CartridgeF6(int[] image) {
        // Copy the ROM image into my buffer
        
        
        myImage=copyImage(image);
        //assert(myImage.length == CART_SIZE);
        
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
        addIndirectAccess(0x1FF6, 0x2000);        
        setCurrentBank(0); //This sets up addressing for Bank 0
    }
    
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int peek(int address) {
        int zNewAddress = address & 0x0FFF;
        
        // Switch banks if necessary
        switch(zNewAddress) {
            case 0x0FF6:
                // Set the current bank to the first 4k bank
                setCurrentBank(0);
                break;
                
            case 0x0FF7:
                // Set the current bank to the second 4k bank
                setCurrentBank(1);
                break;
                
            case 0x0FF8:
                // Set the current bank to the third 4k bank
                setCurrentBank(2);
                break;
                
            case 0x0FF9:
                // Set the current bank to the forth 4k bank
                setCurrentBank(3);
                break;
                
            default:
                break;
                
                
                
                
        }
        
        
        
        return myImage[myCurrentBank * 4096 + zNewAddress];
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void poke(int address, int aByteValue) {
        int zNewAddress = address & 0x0FFF;
        
        // Switch banks if necessary
        switch(zNewAddress) {
            case 0x0FF6:
                // Set the current bank to the first 4k bank
                setCurrentBank(0);
                break;
                
            case 0x0FF7:
                // Set the current bank to the second 4k bank
                setCurrentBank(1);
                break;
                
            case 0x0FF8:
                // Set the current bank to the third 4k bank
                setCurrentBank(2);
                break;
                
            case 0x0FF9:
                // Set the current bank to the forth 4k bank
                setCurrentBank(3);
                break;
                
            default:
                break;
        }
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected void setCurrentBank(int bank) {
        if(myBankLocked) return;
        
        // Remember what bank we're in
        myCurrentBank = bank;
        
        // Map ROM image into the system
        addDirectPeekAccess(0x1000, 0x1FF6, myImage, 0x0FFF, myCurrentBank * 4096);
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected int getCurrentBank() {
        return myCurrentBank;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected int bankCount() {
        return 4;
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






