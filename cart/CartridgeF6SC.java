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
// $Id: CartridgeF6SC.java,v 1.2 2007/08/12 04:51:29 mauvila Exp $
//============================================================================

package jstella.cart;
import jstella.core.PageAccess;
import jstella.j6507.*;
import jstella.j6507.IfcSystem;

/**
 * Cartridge class used for Atari's 16K bankswitched games with
 * 128 bytes of RAM.  There are four 4K banks.
 *
 * @author  Bradford W. Mott
 * @version $Id: CartridgeF6SC.java,v 1.2 2007/08/12 04:51:29 mauvila Exp $
 */

public class CartridgeF6SC extends Cartridge {
    private final static long serialVersionUID = -1055099564873018150L;
    
    private final static int CART_SIZE=16384;
    private final static String CART_NAME="CartridgeF6SC";
    
    
    
    
    private char myCurrentBank;
    
    
    private int myResetBank;
    
    
    private int[] myImage=new int[CART_SIZE];
    
    private int[] myRAM=new int[128];
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public CartridgeF6SC(int[] image) {
      
        myImage=copyImage(image);
     
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
        
        addIndirectAccess(0x1FF6, 0x2000);
        addDirectPokeAccess(0x1000, 0x1080, myRAM, 0x007F);
        addDirectPeekAccess(0x1080, 0x1100, myRAM, 0x007F);
        
   
        
        setCurrentBank(0);
        
        
        
        
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
        
        // NOTE: This does not handle accessing RAM, however, this function
        // should never be called for RAM because of the way page accessing
        // has been setup
        
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
        myCurrentBank = (char)bank;
    
        
      
        
        // Map ROM image into the system
         addDirectPeekAccess(0x1100, 0x1FF6, myImage, 0x0FFF, myCurrentBank * 4096);
     
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







