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
// $Id: CartridgeDPC.java,v 1.2 2007/08/12 04:51:29 mauvila Exp $
//============================================================================
package jstella.cart;

import jstella.j6507.*;
import jstella.core.JSSystem;
import jstella.core.PageAccess;
import jstella.j6507.IfcSystem;

/**
 * Cartridge class used for Pitfall II.  There are two 4K program banks, a
 * 2K display bank, and the DPC chip.  For complete details on the DPC chip
 * see David P. Crane's United States Patent Number 4,644,495.
 *
 * @author  Bradford W. Mott
 * @version $Id: CartridgeDPC.java,v 1.2 2007/08/12 04:51:29 mauvila Exp $
 */
public class CartridgeDPC extends Cartridge {
    private final static long serialVersionUID = -7081138710236297651L;
    
    
    private final static  int[] f = { 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1 };
    private final static int[] musicAmplitudes = {0x00, 0x04, 0x05, 0x09, 0x06, 0x0a, 0x0b, 0x0f};
    
    private java.util.Random myRandomGenerator=new java.util.Random();
    // Indicates which bank is currently active
    int myCurrentBank;
    
    // The 8K program ROM image of the cartridge
    int[] myProgramImage=new int[8192];
    
    // The 2K display ROM image of the cartridge
    int[] myDisplayImage=new int[2048];
    
    // Copy of the raw image, for use by getImage()
    int[] myImageCopy=new int[8192 + 2048 + 255];
    
    // The top registers for the data fetchers
    int[] myTops=new int[8];
    
    // The bottom registers for the data fetchers
    int[] myBottoms=new int[8];
    
    // The counter registers for the data fetchers
    int[] myCounters=new int[8];
    
    // The flag registers for the data fetchers
    int[] myFlags=new int[8];
    
    // The music mode DF5, DF6, & DF7 enabled flags
    boolean[] myMusicMode=new boolean[3];
    
    // The random number generator register
    int myRandomNumber=0;
    
    // CartridgeDPC cycle count when the last update to music data fetchers occurred
    int mySystemCycles=0;
    
    // Fractional DPC music OSC clocks unused during the last update
    double myFractionalClocks=0.0;
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public CartridgeDPC( int[] image) {
        int addr;
        
        // Make a copy of the entire image as-is, for use by getImage()
        myImageCopy=copyImage(image);
        //for(addr = 0; addr < image.length; ++addr)
        //  myImageCopy[addr] = image[addr];
        
        // Copy the program ROM image into my buffer
        for(addr = 0; addr < 8192; ++addr) {
            myProgramImage[addr] = image[addr];
        }
        
        // Copy the display ROM image into my buffer
        for(addr = 0; addr < 2048; ++addr) {
            myDisplayImage[addr] = image[8192 + addr];
        }
        
        // Initialize the DPC data fetcher registers
        for(int i = 0; i < 8; ++i) {
            myTops[i] = myBottoms[i] = myCounters[i] = myFlags[i] = 0;
        }
        
        // None of the data fetchers are in music mode
        myMusicMode[0] = myMusicMode[1] = myMusicMode[2] = false;
        
        // Initialize the DPC's random number generator register (must be non-zero)
        myRandomNumber = 1;
        
        // Initialize the system cycles counter & fractional clock values
        mySystemCycles = 0;
        myFractionalClocks = 0.0;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public String name() {
        return "CartridgeDPC";
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void reset() {
        // Update cycles to the current system cycles
        mySystemCycles = mySystem.getCycles();
        myFractionalClocks = 0.0;
        
        // Upon reset we switch to bank 1
        setCurrentBank(1);
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void systemCyclesReset() {
        // Get the current system cycle
        int cycles = mySystem.getCycles();
        
        // Adjust the cycle counter so that it reflects the new value
        mySystemCycles -= cycles;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
    public void install(JSSystem system) {
        mySystem = system;
        
        addIndirectAccess(0x1FF8, 0x2000);    // Set the page accessing methods for the hot spots
        addIndirectAccess(0x1000, 0x1080);    // Set the page accessing method for the DPC reading & writing pages
        
        setCurrentBank(1); // Install pages for bank 1
    }
    
    private boolean bool(int aValue) {
        return (aValue!=0);
    }
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private void clockRandomNumberGenerator() {
        // Table for computing the input bit of the random number generator's
        // shift register (it's the NOT of the EOR of four bits)
        
        
        // Using bits 7, 5, 4, & 3 of the shift register compute the input
        // bit for the shift register
        int bit = f[((myRandomNumber >> 3) & 0x07) |
                (bool(myRandomNumber & 0x80) ? 0x08 : 0x00)];
        
        // Update the shift register
        myRandomNumber = ((myRandomNumber << 1) | bit) & 0xFF;
        
        // myRandomNumber=((int)(myRandomGenerator.nextDouble() * 256))&0xFF;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private void updateMusicModeDataFetchers() {
        // Calculate the number of cycles since the last update
        int cycles = mySystem.getCycles() - mySystemCycles;
        mySystemCycles = mySystem.getCycles();
        
        // Calculate the number of DPC OSC clocks since the last update
        double clocks = ((15750.0 * cycles) / 1193191.66666667) + myFractionalClocks;
        int wholeClocks = (int)clocks;
        myFractionalClocks = clocks - (double)wholeClocks;
        
        if(wholeClocks <= 0) {
            return;
        }
        
        // Let's update counters and flags of the music mode data fetchers
        for(int x = 5; x <= 7; ++x) {
            // Update only if the data fetcher is in music mode
            if(myMusicMode[x - 5]) {
                int top = myTops[x] + 1;
                int newLow = (int)(myCounters[x] & 0x00ff);
                
                if(myTops[x] != 0) {
                    newLow -= (wholeClocks % top);
                    if(newLow < 0) {
                        newLow += top;
                    }
                } else {
                    newLow = 0;
                }
                
                // Update flag register for this data fetcher
                if(newLow <= myBottoms[x]) {
                    myFlags[x] = 0x00;
                } else if(newLow <= myTops[x]) {
                    myFlags[x] = 0xff;
                }
                
                myCounters[x] = (myCounters[x] & 0x0700) | (int)newLow;
            }
        }
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int peek(int address) {
        int zNewAddress = (address & 0x0FFF);
        
        // Clock the random number generator.  This should be done for every
        // cartridge access, however, we're only doing it for the DPC and
        // hot-spot accesses to save time.
        clockRandomNumberGenerator();
        
        if(zNewAddress < 0x0040) {
            int result = 0;
            
            // Get the index of the data fetcher that's being accessed
            int index = zNewAddress & 0x07;
            int function = (zNewAddress >> 3) & 0x07;
            
            // Update flag register for selected data fetcher
            if((myCounters[index] & 0x00ff) == myTops[index]) {
                myFlags[index] = 0xff;
            } else if((myCounters[index] & 0x00ff) == myBottoms[index]) {
                myFlags[index] = 0x00;
            }
            
            switch(function) {
                case 0x00:
                {
                    // Is this a random number read
                    if(index < 4) {
                        result = myRandomNumber;
                    }
                    // No, it's a music read
                    else {
                        
                        
                        // Update the music data fetchers (counter & flag)
                        updateMusicModeDataFetchers();
                        
                        int i = 0;
                        if(myMusicMode[0] && bool(myFlags[5])) {
                            i |= 0x01;
                        }
                        if(myMusicMode[1] && bool(myFlags[6])) {
                            i |= 0x02;
                        }
                        if(myMusicMode[2] && bool(myFlags[7])) {
                            i |= 0x04;
                        }
                        
                        result = musicAmplitudes[i];
                    }
                    break;
                }
                
                // DFx display data read
                case 0x01:
                {
                    result = myDisplayImage[2047 - myCounters[index]];
                    break;
                }
                
                // DFx display data read AND'd w/flag
                case 0x02:
                {
                    result = myDisplayImage[2047 - myCounters[index]] & myFlags[index];
                    break;
                }
                
                // DFx flag
                case 0x07:
                {
                    result = myFlags[index];
                    break;
                }
                
                default:
                {
                    result = 0;
                }
            }
            
            // Clock the selected data fetcher's counter if needed
            if((index < 5) || ((index >= 5) && (!myMusicMode[index - 5]))) {
                myCounters[index] = (myCounters[index] - 1) & 0x07ff;
            }
            
            return result;
        } else {
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
            return myProgramImage[(myCurrentBank * 4096) + zNewAddress];
        }
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void poke(int address, int value) {
        assert((value<0x100)&&(value>=0));
        int zNewAddress = (address & 0x0FFF);
        
        // Clock the random number generator.  This should be done for every
        // cartridge access, however, we're only doing it for the DPC and
        // hot-spot accesses to save time.
        clockRandomNumberGenerator();
        
        if((zNewAddress >= 0x0040) && (zNewAddress < 0x0080)) {
            // Get the index of the data fetcher that's being accessed
            int index = zNewAddress & 0x07;
            int function = (zNewAddress >> 3) & 0x07;
            
            switch(function) {
                // DFx top count
                case 0x00:
                {
                    myTops[index] = value;
                    myFlags[index] = 0x00;
                    break;
                }
                
                // DFx bottom count
                case 0x01:
                {
                    myBottoms[index] = value;
                    break;
                }
                
                // DFx counter low
                case 0x02:
                {
                    if((index >= 5) && myMusicMode[index - 5]) {
                        // Data fecther is in music mode so its low counter value
                        // should be loaded from the top register not the poked value
                        myCounters[index] = (myCounters[index] & 0x0700) |
                                (int)myTops[index];
                    } else {
                        // Data fecther is either not a music mode data fecther or it
                        // isn't in music mode so it's low counter value should be loaded
                        // with the poked value
                        myCounters[index] = (myCounters[index] & 0x0700) | (int)value;
                    }
                    break;
                }
                
                // DFx counter high
                case 0x03:
                {
                    myCounters[index] = ((value & 0x07) << 8) |
                            (myCounters[index] & 0x00ff);
                    
                    // Execute special code for music mode data fetchers
                    if(index >= 5) {
                        myMusicMode[index - 5] = bool(value & 0x10);
                        
                        // NOTE: We are not handling the clock source input for
                        // the music mode data fetchers.  We're going to assume
                        // they always use the OSC input.
                    }
                    break;
                }
                
                // Random Number Generator Reset
                case 0x06:
                {
                    myRandomNumber = 1;
                    break;
                }
                
                default:
                {
                    break;
                }
            }
        } else {
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
    }
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected void setCurrentBank(int bank) {
        if(myBankLocked) return;
        
        // Remember what bank we're in
        myCurrentBank = bank;
        
        // Map Program ROM image into the system
        addDirectPeekAccess(0x1080, 0x1FF8, myProgramImage, 0x0FFF, myCurrentBank * 4096);
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected int getCurrentBank() {
        return myCurrentBank;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected int bankCount() {
        return 2; // TODO: support the display ROM somehow
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public boolean patch(int address, int value) {
        address = (char)(address & 0x0FFF);
        myProgramImage[(myCurrentBank * 4096) + address] = value;
        return true;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int[] getImage() {
        //size = 8192 + 2048 + 255;
        
        int i;
        for(i = 0; i < 8192; i++)
            myImageCopy[i] = myProgramImage[i];
        
        for(i = 0; i < 2048; i++)
            myImageCopy[i + 8192] = myDisplayImage[i];
        
        return myImageCopy;
    }
    
}
