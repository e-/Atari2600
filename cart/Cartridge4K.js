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
// $Id: Cartridge4K.java,v 1.2 2007/08/12 04:51:29 mauvila Exp $
//============================================================================
function Cartridge4K (image){
    this.serialVersionUID = "-3240308689643904765L";
    
    this.CARTRIDGE_MASK_VALUE=0x0FFF;
    this.CARTRIDGE_SIZE=4096;
    this.CARTRIDGE_BANK_COUNT=1;
    
    
    
    
    // The 4K ROM image for the cartridge
    this.myImage=[];
    
    
		//이하 원래 상속 

this.setConsole = function(aConsole) { myConsole=aConsole; };

  	this.systemCyclesReset = function() {    };
    
		this.getMD5 = function(){ return this. myMD5; };
    this.setMD5 = function(aMD5){ this.myMD5=aMD5; };
    
		this.copyImage = function(aSourceImage)
    {
        var zReturn = [];
				var length = aSourceImage.length;
        for (var i=0; i<length; i++)
        {
            zReturn[i]=aSourceImage[i];
        }//end : for i loop
        return zReturn;
    };
    

    this.addIndirectAccess = function(aStartAddress, aEndAddress)
    {
        
        for(var zAddress = (aStartAddress & ~PAGE_MASK); zAddress < (aEndAddress & ~PAGE_MASK); zAddress += PAGE_SIZE)
        {
            this.mySystem.setPageAccess(zAddress >> PAGE_SHIFT, (new PageAccess()).createIndirectAccess(this));
        } 
    };
    
    this.addDirectPeekAccess = function(aStartAddress, aEndAddress, aMemory, aBaseAddressMask, aBaseAddressOffset)
     {
			 	if(arguments.length == 4)
	         this.addDirectPeekAccess(aStartAddress, aEndAddress, aMemory, aBaseAddressMask, 0);
				else  {
		        for(var zAddress = (aStartAddress & ~PAGE_MASK); zAddress < (aEndAddress & ~PAGE_MASK); zAddress += PAGE_SIZE)
    	    {
      	      this.mySystem.setPageAccess(zAddress >> PAGE_SHIFT, (new PageAccess()).createDirectPeekAccess(this, aMemory, aBaseAddressOffset + (zAddress & aBaseAddressMask)));
        	}  
				}
     };
    
    this.addDirectPokeAccess = function(aStartAddress, aEndAddress, aMemory, aBaseAddressMask, aBaseAddressOffset)
    {
        for(var zAddress = (aStartAddress & ~PAGE_MASK); zAddress < (aEndAddress & ~PAGE_MASK); zAddress += PAGE_SIZE)
        {
            this.mySystem.setPageAccess(zAddress >> PAGE_SHIFT, (new PageAccess()).createDirectPokeAccess(this, aMemory, aBaseAddressOffset + (zAddress & aBaseAddressMask)));
        } 
    }
    
    this.addDirectPokeAccess = function(aStartAddress, aEndAddress,aMemory, aBaseAddressMask)
     {
         this.addDirectPokeAccess(aStartAddress, aEndAddress, aMemory, aBaseAddressMask, 0);
     }
    //상속 끝

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.myImage=this.copyImage(image); //Constructor
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.name = function() {
        return "Cartridge4K";
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.reset = function() {
    }
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
    this.install = function(system) { //JSSystem?
        this.mySystem = system;        
        this.addDirectPeekAccess(0x1000, 0x2000, this.myImage, this.CARTRIDGE_MASK_VALUE);  // Map ROM image into the system
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.peek = function(address) {
        return myImage[address & this.CARTRIDGE_MASK_VALUE];
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.poke = function(aC, aB) {
        // This is ROM so poking has no effect :-)
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.setCurrentBank = function(bank) {
        // Doesn't support bankswitching
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.getCurrentBank = function() {
        // Doesn't support bankswitching
        return 0;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.bankCount = function() {
        return this.CARTRIDGE_BANK_COUNT;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.patch = function(address, value) {
        myImage[address & this.CARTRIDGE_MASK_VALUE] = value;
        return true;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.getImage = function() {
        
        return myImage;
    }
    
    
    
}
