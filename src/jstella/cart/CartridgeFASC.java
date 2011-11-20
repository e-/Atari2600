package jstella.cart;

import jstella.j6507.*;
import jstella.j6507.IfcSystem;



/**
  Cartridge class used for CBS' RAM Plus cartridges.  There are
  three 4K banks and 256 bytes of RAM.

  @author  Bradford W. Mott
  @version $Id: CartridgeFASC.java,v 1.1 2007/09/08 05:47:50 mauvila Exp $
*/
public class CartridgeFASC extends Cartridge
{
 private final static long serialVersionUID = -7642942968613484045L;
 





 

 
   private int myCurrentBank=0;

    // The 12K ROM image of the cartridge
   private int[] myImage=new int[12288];

    // The 256 bytes of RAM on the cartridge
    private int[]  myRAM=new int[256];



// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public CartridgeFASC(int[] aImage)
{
 myImage=copyImage(aImage);  
 

  randomizeRAM(myRAM);

}
 
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -


// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 public String name() {

  return "CartridgeFASC";
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public void reset()
{
  // Upon reset we switch to bank 2
  setCurrentBank(2);
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 public void install(jstella.core.JSSystem system) {
        mySystem = system;        
        // Map ROM image into the system        
        //The following sets up a PageAcces for the page containing the "Switch Bank" addresses"
        addIndirectAccess(0x1FF8, 0x2000);        
        addDirectPokeAccess(0x1000, 0x1100, myRAM, 0x00FF); 
        addDirectPeekAccess(0x1100, 0x1200, myRAM, 0x00FF); 
        setCurrentBank(2); //This sets up addressing for Bank 0
    }



// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public int peek(int address)
{
  int zMaskedAddress = address & 0x0FFF;

  // Switch banks if necessary
  switch(zMaskedAddress)
  {
    case 0x0FF8:
      // Set the current bank to the lower 4k bank
      setCurrentBank(0);
      break;

    case 0x0FF9:
      // Set the current bank to the middle 4k bank
      setCurrentBank(1);
      break;

    case 0x0FFA:
      // Set the current bank to the upper 4k bank
      setCurrentBank(2);
      break;

    default:
      break;
  }

  // NOTE: This does not handle accessing RAM, however, this function
  // should never be called for RAM because of the way page accessing
  // has been setup
  return myImage[(myCurrentBank * 4096) + zMaskedAddress];
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public void poke(int address, int aByte)
{
  address = address & 0x0FFF;

  // Switch banks if necessary
  switch(address)
  {
    case 0x0FF8:
      // Set the current bank to the lower 4k bank
      setCurrentBank(0);
      break;

    case 0x0FF9:
      // Set the current bank to the middle 4k bank
      setCurrentBank(1);
      break;

    case 0x0FFA:
      // Set the current bank to the upper 4k bank
      setCurrentBank(2);
      break;

    default:
      break;
  }

  // NOTE: This does not handle accessing RAM, however, this function
  // should never be called for RAM because of the way page accessing
  // has been setup
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -



// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public void setCurrentBank(int bank)
{
  if(myBankLocked) return;
myCurrentBank=bank;


  // Map ROM image into the system
  addDirectPeekAccess(0x1200, 0x1FF8, myImage, 0x0FFF, myCurrentBank*4096);
  
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public int getCurrentBank()
{
  return myCurrentBank;
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public int bankCount()
{
  return 3;
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public boolean patch(int address, int value)
{
  address = address & 0x0FFF;
  myImage[(myCurrentBank * 4096) + address] = value;
  return true;
} 

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public int[] getImage()
{
  
  return myImage;
}


}
