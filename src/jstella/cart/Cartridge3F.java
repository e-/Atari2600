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
// $Id: Cartridge3F.java,v 1.2 2007/08/18 09:10:38 mauvila Exp $
//============================================================================

package jstella.cart;

import jstella.core.*;

/**
  This is the cartridge class for Tigervision's bankswitched 
  games.  In this bankswitching scheme the 2600's 4K cartridge 
  address space is broken into two 2K segments.  The last 2K 
  segment always points to the last 2K of the ROM image.  The 
  desired bank number of the first 2K segment is selected by 
  storing its value into $3F.  Actually, any write to location
  $00 to $3F will change banks.  Although, the Tigervision games 
  only used 8K this bankswitching scheme supports up to 512K.
   
  @author  Bradford W. Mott
  @version $Id: Cartridge3F.java,v 1.2 2007/08/18 09:10:38 mauvila Exp $
*/
public class Cartridge3F extends Cartridge
{
 

  





 
    // Indicates which bank is currently active for the first segment
    private int myCurrentBank;

    // Pointer to a dynamically allocated ROM image of the cartridge
     private int[] myImage;



// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
Cartridge3F(int[] image)
{
  
  myImage=copyImage(image);
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -


// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public String name() 
{
  return "Cartridge3F";
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public void reset()
{
  // We'll map bank 0 into the first segment upon reset
  setCurrentBank(0);
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public void install(JSSystem system)
{
  mySystem = system;
  //int shift = mySystem.pageShift();
  //int mask = mySystem.pageMask();

  // Make sure the system we're being installed in has a page size that'll work
 // assert((0x1800 & mask) == 0);

  // Set the page accessing methods for the hot spots (for 100% emulation
  // we need to chain any accesses below 0x40 to the TIA. Our poke() method
  // does this via mySystem.tiaPoke(...), at least until we come up with a
  // cleaner way to do it.)
  //System::PageAccess access;
  addIndirectAccess(0x00, 0x40);  //Installing on zero page
 /* for(int i = 0x00; i < 0x40; i += (1 << shift))
  {
    access.directPeekBase = 0;
    access.directPokeBase = 0;
    access.device = this;
    mySystem.setPageAccess(i >> shift, access);
  }
  */

  // Setup the second segment to always point to the last ROM slice
  addDirectPeekAccess(0x1800, 0x2000, myImage, 0x07FF, myImage.length - 2048);
  /*for(int j = 0x1800; j < 0x2000; j += (1 << shift))
  {
    access.device = this;
    access.directPeekBase = &myImage[(mySize - 2048) + (j & 0x07FF)];
    access.directPokeBase = 0;
    mySystem.setPageAccess(j >> shift, access);
  }
   */

  // Install pages for bank 0 into the first segment
  setCurrentBank(0);
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public int peek(int address)
{
  address = address & 0x0FFF;

  if(address < 0x0800)
  {
    return myImage[(address & 0x07FF) + myCurrentBank * 2048];
  }
  else
  {
    return myImage[(address & 0x07FF) + myImage.length - 2048];
  }
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public void poke(int address, int value)
{
  address = address & 0x0FFF;

  // Switch banks if necessary
  if(address <= 0x003F)
  {
    setCurrentBank(value);
  }

  myConsole.getTIA().poke(address, value); // ??
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
protected void setCurrentBank(int bank)
{ 
  if(myBankLocked==true) return;

  // Make sure the bank they're asking for is reasonable
  if(bank * 2048 < myImage.length)
  {
    myCurrentBank = bank;
  }
  else
  {
    // Oops, the bank they're asking for isn't valid so let's wrap it
    // around to a valid bank number
    myCurrentBank = bank % (myImage.length / 2048);
  }

 // int offset = myCurrentBank * 2048;
 // int shift = mySystem.pageShift();

  // Setup the page access methods for the current bank
 // System::PageAccess access;
  //access.device = this;
  //access.directPokeBase = 0;

  // Map ROM image into the system
  addDirectPeekAccess(0x1000, 0x1800, myImage, 0x07FF, myCurrentBank * 2048);
  /*for(int address = 0x1000; address < 0x1800; address += (1 << shift))
  {
    access.directPeekBase = &myImage[offset + (address & 0x07FF)];
    mySystem.setPageAccess(address >> shift, access);
  }
   */
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
protected int getCurrentBank()
{
  return myCurrentBank;
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
protected int bankCount()
{
  return myImage.length / 2048;
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public boolean patch(int address, int value)
{
  address = address & 0x0FFF;
  if(address < 0x0800)
  {
    myImage[(address & 0x07FF) + myCurrentBank * 2048] = value;
  }
  else
  {
    myImage[(address & 0x07FF) + myImage.length - 2048] = value;
  }
  return true;
} 

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
protected int[] getImage()
{
 
  return myImage;
}

}
