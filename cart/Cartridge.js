function Cartridge() /* implements IfcDevice */
{
    this.serialVersionUID = "7979700597113264401L";
    //  AUTODETECT="AUTO-DETECT";
    
    this.TYPE_2K="2K";
    this.TYPE_4K="4K";
    this.TYPE_F8="F8";
    this.TYPE_F8SWAPPED="F8 swapped";
    this.TYPE_F8SC="F8SC";
    this.TYPE_FASC="FASC";
    this.TYPE_F6="F6";
    this.TYPE_F6SC="F6SC";
    this.TYPE_F4="F4";
    this.TYPE_FE="FE";
    this.TYPE_DPC="DPC";
    this.TYPE_E0="E0";
    this.TYPE_E7="E7";
    this.TYPE_3F="3F";
    this.TYPE_F4SC="F4SC";
    
    
    this.mySystem=null;
    this.myBankLocked=false;
    this.myMD5="";
 
    this.lockBank = function()
		{ 
			myBankLocked = true;  
		};
    this.unlockBank = function() 
		{ 
			myBankLocked = false; 
		};
		
		//unlockBank(); /* Constructor */

    this.getMD5 = function(){ return this. myMD5; };
    this.setMD5 = function(aMD5){ this.myMD5=aMD5; };
    
    this.myConsole=null;
    this.setConsole = function(aConsole) { myConsole=aConsole; };
    
  	this.systemCyclesReset = function() {    };
    
    
    /**
     * Converts a byte array to an int array.  ROMs are stored as bytes, but JStella
     * works with them as ints.  So conversion must occur after loading the data from
     * a disk, etc.
     * @param aByteArray a byte array
     * @return an int array
     */
    this.toIntArray = function(aByteArray) {
        var zReturn= []; 
				var length = aByteArray.length;
        for (var i=0; i<length; i++) {
            zReturn[i]=(aByteArray[i]) & 0xFF;  //The 0xFF mask makes sure that the new int is positive unsigned version
        }
        return zReturn;
    };
    
    this.toByteArray = function(aIntArray) {
        var zReturn= [];
				var length = aIntArray.length;
        for (var i=0; i<length; i++) {
            zReturn[i]=aIntArray[i];
        }
        return zReturn;
    }
    
    
    this.calculateMD5 = function(aData) { /* byte array */
//				return MD5(aDatai);
				return "6e372f076fb9586aff416144f5cfe1cb";
        var zReturn="";
				/* TODO DODO md5 hashing */
/*            java.security.MessageDigest zMD=java.security.MessageDigest.getInstance("MD5");
            zMD.update(aData);
            byte[] zSum=zMD.digest();
            java.math.BigInteger zBig=new java.math.BigInteger(1, zSum);
            zReturn=zBig.toString(16);
        }//end : try*/
        return zReturn;
    };
    
    
    
   this.arrayCompare = function(aArray, aIndexA, aIndexB, aCompCount) {
        var zReturn=true;
        
        for (var i=0; i<aCompCount; i++) {
            if (aArray[aIndexA + i]!=aArray[aIndexB+i]) {
                zReturn=false;
                break;
            }//end : not equal
        }//end : for loop
        return zReturn;
    }
    
    /**
     * Checks to see if an MD5 matches a previously compiled list of MD5s and cartridge
     * types.  The list in this method only contains those that the autodetection method
     * (detectTypeByImage) is incapable of identifying correctly.
     * @param aMD5 MD5 to check
     * @return the cartridge type if there is a match; null if no match was found
     */
    this.detectTypeByMD5 = function(aMD5) {
        var zReturn=null;
        aMD5=aMD5.toLowerCase().trim();
        
        if (aMD5 === "bc24440b59092559a1ec26055fd1270e") zReturn=TYPE_F8SWAPPED;
        else if (aMD5 === "75ee371ccfc4f43e7d9b8f24e1266b55") zReturn=TYPE_F8SWAPPED;
        else if (aMD5 === "6dda84fb8e442ecf34241ac0d1d91d69") zReturn=TYPE_F6SC;

        return zReturn;
    }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.searchForBytes = function(image, signature, sigsize, minhits) {
        var count = 0;
        var imagesize=image.length;
        for(var i = 0; i < imagesize - sigsize; /*++i*/ i++) {
            var matches = 0;
            for(var j = 0; j < sigsize; /*++j*/j++) {
                if(image[i+j] == signature[j])
                    ++matches;
                else
                    break;
            }
            if(matches == sigsize) {
                ++count;
                i += sigsize;  // skip past this signature 'window' entirely
            }
            if(count >= minhits)
                break;
        }
        
        return (count >= minhits);
    };
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.isProbablySC = function(image, size) {
        // We assume a Superchip cart contains the same bytes for its entire
        // RAM area; obviously this test will fail if it doesn't
        // The RAM area will be the first 256 bytes of each 4K bank
        var banks = Math.floor(size / 4096);
        for(var i = 0; i < banks; ++i) {
            var first = image[i*4096];
            for(var j = 0; j < 256; ++j) {
                if(image[(i*4096)+j] != first)
                    return false;
            }
        }
        return true;
    };
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   	this.isProbably3F = function(image, size) {
        // 3F cart bankswitching is triggered by storing the bank number
        // in address 3F using 'STA $3F'
        // We expect it will be present at least 2 times, since there are
        // at least two banks
        var intsignature= [ 0x85, 0x3F ]  // STA $3F
        var signature=this.toDataUByte(intsignature);
        return this.searchForBytes(image, signature, 2, 2);
    };
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.isProbably3E = function(image, size) {
        // 3E cart bankswitching is triggered by storing the bank number
        // in address 3E using 'STA $3E', commonly followed by an
        // immediate mode LDA
        var intsignature = [ 0x85, 0x3E, 0xA9, 0x00 ];  // STA $3E; LDA #$00
        var signature=this.toDataUByte(intsignature);
        return this.searchForBytes(image, signature, 4, 1);
    };
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.isProbablyE0 = function(image, size) {
        // E0 cart bankswitching is triggered by accessing addresses
        // $FE0 to $FF9 using absolute non-indexed addressing
        // To eliminate false positives (and speed up processing), we
        // search for only certain known signatures
        // Thanks to "stella@casperkitty.com" for this advice
        // These signatures are attributed to the MESS project
        var intsignature = [ /*[6][3]*/
            [ 0x8D, 0xE0, 0x1F ],  // STA $1FE0
            [ 0x8D, 0xE0, 0x5F ],  // STA $5FE0
            [ 0x8D, 0xE9, 0xFF ],  // STA $FFE9
            [ 0xAD, 0xE9, 0xFF ],  // LDA $FFE9
            [ 0xAD, 0xED, 0xFF ],  // LDA $FFED
            [ 0xAD, 0xF3, 0xBF ]   // LDA $BFF3
        ];
        var signature=toDataUByte(intsignature);
        for(var i = 0; i < 6; ++i) {
            if(searchForBytes(image, signature[i], 3, 1))
                return true;
        }
        return false;
    };
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.isProbablyE7 = function(image, size) {
        // E7 carts map their second 1K block of RAM at addresses
        // $800 to $8FF.  However, since this occurs in the upper 2K address
        // space, and the last 2K in the cart always points to the last 2K of the
        // ROM image, the RAM area should fall in addresses $3800 to $38FF
        // Similar to the Superchip cart, we assume this RAM block contains
        // the same bytes for its entire area
        // Also, we want to distinguish between ROMs that have large blocks
        // of the same amount of (probably unused) data by making sure that
        // something differs in the previous 32 or next 32 bytes
        var first = image[0x3800];
        for(var i = 0x3800; i < 0x3A00; ++i) {
            if(first != image[i])
                return false;
        }
        
        // OK, now scan the surrounding 32 byte blocks
        var count1 = 0, count2 = 0;
        for(var i = 0x3800 - 32; i < 0x3800; ++i) {
            if(first != image[i])
                ++count1;
        }
        for(var i = 0x3A00; i < 0x3A00 + 32; ++i) {
            if(first != image[i])
                ++count2;
        }
        
        return (count1 > 0 || count2 > 0);
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.isProbablyUA = function(image, size) {
        // UA cart bankswitching switches to bank 1 by accessing address 0x240
        // using 'STA $240'
        var intsignature = [ 0x8D, 0x40, 0x02 ];  // STA $240
        var signature=toDataUByte(intsignature);
        return searchForBytes(image, signature, 3, 1);
    };
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.isProbablyCV = function(image, size) {
        // CV RAM access occurs at addresses $f3ff and $f400
        // These signatures are attributed to the MESS project
        var intsignature /*[2][3]*/ = [
            [ 0x9D, 0xFF, 0xF3 ],  // STA $F3FF
            [ 0x99, 0x00, 0xF4 ]   // STA $F400
        ];
        var signature=toDataUByte(intsignature);
        if(searchForBytes(image, signature[0], 3, 1))
            return true;
        else
            return searchForBytes(image, signature[1], 3, 1);
    };
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.isProbablyFE = function(image, size) {
        // FE bankswitching is very weird, but always seems to include a
        // 'JSR $xxxx'
        // These signatures are attributed to the MESS project
        var intsignature/*[4][5]*/ = [
            [ 0x20, 0x00, 0xD0, 0xC6, 0xC5 ],  // JSR $D000; DEC $C5
            [ 0x20, 0xC3, 0xF8, 0xA5, 0x82 ],  // JSR $F8C3; LDA $82
            [ 0xD0, 0xFB, 0x20, 0x73, 0xFE ],  // BNE $FB; JSR $FE73
            [ 0x20, 0x00, 0xF0, 0x84, 0xD6 ]   // JSR $F000; STY $D6
        ];
        var signature=toDataUByte(intsignature);
        for(var i = 0; i < 4; ++i) {
            if(searchForBytes(image, signature[i], 5, 1))
                return true;
        }
        return false;
    };
    
    
    
    
   // ============== Subclass convenience methods =====================
    
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
    
    this.randomizeRAM = function(aRAM)
    {
       	var length = aRAM.length;
        for(var i = 0; i < length; i++) {
            aRAM[i] = Math.floor(Math.random() * 256) & 0xFF;
        }//end : for i loop
    };
    
   
    this.addIndirectAccess = function(aStartAddress, aEndAddress)
    {
        
        for(var zAddress = (aStartAddress & ~PAGE_MASK); zAddress < (aEndAddress & ~PAGE_MASK); zAddress += PAGE_SIZE)
        {
            mySystem.setPageAccess(zAddress >> PAGE_SHIFT, PageAccess.createIndirectAccess(this));
        } 
    };
    
    this.addDirectPeekAccess = function(aStartAddress, aEndAddress, aMemory, aBaseAddressMask, aBaseAddressOffset)
    {
        for(var zAddress = (aStartAddress & ~PAGE_MASK); zAddress < (aEndAddress & ~PAGE_MASK); zAddress += PAGE_SIZE)
        {
            mySystem.setPageAccess(zAddress >> PAGE_SHIFT, PageAccess.createDirectPeekAccess(this, aMemory, aBaseAddressOffset + (zAddress & aBaseAddressMask)));
        }    
    };
    
    this.addDirectPeekAccess = function(aStartAddress, aEndAddress, aMemory, aBaseAddressMask)
     {
         addDirectPeekAccess(aStartAddress, aEndAddress, aMemory, aBaseAddressMask, 0);
     };
    
    this.addDirectPokeAccess = function(aStartAddress, aEndAddress, aMemory, aBaseAddressMask, aBaseAddressOffset)
    {
        for(var zAddress = (aStartAddress & ~PAGE_MASK); zAddress < (aEndAddress & ~PAGE_MASK); zAddress += PAGE_SIZE)
        {
            mySystem.setPageAccess(zAddress >> PAGE_SHIFT, PageAccess.createDirectPokeAccess(this, aMemory, aBaseAddressOffset + (zAddress & aBaseAddressMask)));
        } 
    }
    
    this.addDirectPokeAccess = function(aStartAddress, aEndAddress,aMemory, aBaseAddressMask)
     {
         addDirectPokeAccess(aStartAddress, aEndAddress, aMemory, aBaseAddressMask, 0);
     }
    
    
 
    
  
    
    
  // ================ Unsigned/signed conversion methods =============
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Returns a byte that represents the value supplied.
     * The byte is actually signed, so it is important
     * to remember that accessing its value directly may not
     * return the value supplied to it.
     * @param aByteValue The value to be represented as a byte (0-255)
     * @return A byte that represents the value supplied.
     */
    this.toDataUByte = function(aByteValue) {
        return aByteValue & 0xff;
    };
    
    
    /**
     * Performs toDataUByte(int) on an array
     * @param aArray Array of values
     * @return Array of bytes
     */
 		this.toDataUByte = function(aArray) {
        var zReturn= [];
				var length = aArray.length;
        for (var i=0; i<length; i++) {
            zReturn[i]=toDataUByte(aArray[i]);
        }//end : for i loop
        return zReturn;
    };
    
    /**
     * Performs toDataUByte(int) on a double array
     * @param aArray a double array
     * @return a double array of byte
     */
    this.toDataUByte = function(aArray) {
        var zReturn= [];
				length = aArray.length;
        for (var i=0; i<length; i++) {
            zReturn[i]= [];
            for (var j=0; j<aArray[i].length; j++) {
                zReturn[i][j]=toDataUByte(aArray[i][j]);
            }//end : for j loop
        }//end : for i loop
        return zReturn;
    };

   /* public int hashCode() {
       return myMD5.hashCode();
    }
    */

    this.toString = function() {
        return "Cartridge : " + this.name() + "; md5=" + myMD5;
    };

    
    /**
     * Looks at the data to determine (or guess) what type of cartridge the data
     * represents.
     * @param image the ROM data
     * @return the cartridge type
     */
    this.detectTypeByImage = function(image) {
        // Guess type based on size
        var type ="";
        var size=image.length;
        if((size % 8448) == 0) {
            type = "AR";
        } else if((size == 2048) ||
                ((size == 4096) && (arrayCompare(image, 0, 2048, 2048)==true))) {
            if(isProbablyCV(image, size))
                type = "CV";
            else
                type = TYPE_2K;
        } else if(size == 4096) {
            if(isProbablyCV(image, size))
                type = "CV";
            else
                type = TYPE_4K;
        } else if(size == 8192)  // 8K
        {
            if(isProbablySC(image, size))
                type = TYPE_F8SC;
            else if(arrayCompare(image, 0, 4096, 4096)==true)
                type = TYPE_4K;
            else if(isProbablyE0(image, size))
                type = TYPE_E0;
            else if(isProbably3E(image, size))
                type = "3E";
            else if(isProbably3F(image, size))
                type = TYPE_3F;
            else if(isProbablyUA(image, size))
                type = "UA";
            else if(isProbablyFE(image, size))
                type = TYPE_FE;
            else
                type = TYPE_F8;
        } else if((size == 10495) || (size == 10496) || (size == 10240))  // 10K - Pitfall2
        {
            type = TYPE_DPC;
        } else if(size == 12288)  // 12K
        {
            // TODO - this should really be in a method that checks the first
            // 512 bytes of ROM and finds if either the lower 256 bytes or
            // higher 256 bytes are all the same.  For now, we assume that
            // all carts of 12K are CBS RAM Plus/FASC.
            type = "FASC";
        } else if(size == 16384)  // 16K
        {
            if(isProbablySC(image, size))
                type = TYPE_F6SC;
            else if(isProbablyE7(image, size))
                type = TYPE_E7;
            else if(isProbably3E(image, size))
                type = "3E";
            else if(isProbably3F(image, size))
                type = TYPE_3F;
            else
                type = TYPE_F6;
        } else if(size == 32768)  // 32K
        {
            if(isProbablySC(image, size))
                type = TYPE_F4SC;
            else if(isProbably3E(image, size))
                type = "3E";
            else if(isProbably3F(image, size))
                type = TYPE_3F;
            else
                type = "F4";
        } else if(size == 65536)  // 64K
        {
            // TODO - autodetect 4A50
            if(isProbably3E(image, size))
                type = "3E";
            else if(isProbably3F(image, size))
                type = TYPE_3F;
            else
                type = "MB";
        } else if(size == 131072)  // 128K
        {
            // TODO - autodetect 4A50
            if(isProbably3E(image, size))
                type = "3E";
            else if(isProbably3F(image, size))
                type = TYPE_3F;
            else
                type = "MC";
        } else  // what else can we do?
        {
            if(this.isProbably3E(image, size))
                type = "3E";
            else if(this.isProbably3F(image, size))
                type = this.TYPE_3F;
            else
                type = this.TYPE_4K;  // Most common bankswitching type
        }
       	type="4K"; 
        return type;
    }
    
    
 // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Creates a cartridge from the given data.  This is the preferred method
     * for creating cartridge objects.  It detects what type of cartidge needs to be created
     * based on the data, and returns a newly created one.
     * @param image the ROM data
     * @return a cartridge object
     */
    this.create = function(image){
        var zMD5 = this.calculateMD5(image); //Calculate the MD5 based on the byte array
        console.log("LOADING ROM : " + zMD5);
       
        cartridge = null;
        
        zType=this.detectTypeByMD5(zMD5);
        if (zType==null) zType= this.detectTypeByImage(image);
        console.log("JStella - detected cartridge type: " + zType);
        // We should know the cart's zType by now so let's create it
        cartridge=this.create2(image, zType); /* create2 -> has tow args. because javascript does not support overloading */
      
        return cartridge;
    };
    
    
   	this.create2 = function(image,aType){ /* original name of this function is "create", but javascript does not support function overloading */

        cartridge=null;

        zUCType=aType.toUpperCase();
        zIntImage=this.toIntArray(image); //create an int array from the byte array
        /*if(zUCType === TYPE_2K.toUpperCase()) cartridge = new Cartridge2K(zIntImage);
        else*/ if(zUCType===this.TYPE_4K.toUpperCase() || true) cartridge = new Cartridge4K(zIntImage);
        /*else if(zUCType===TYPE_F8.toUpperCase())) cartridge = new CartridgeF8(zIntImage, false);
        else if(zUCType===TYPE_F8SWAPPED.toUpperCase())) cartridge = new CartridgeF8(zIntImage, true);
        else if(zUCType===TYPE_F8SC.toUpperCase())) cartridge = new CartridgeF8SC(zIntImage);
        else if(zUCType===TYPE_F6.toUpperCase())) cartridge = new CartridgeF6(zIntImage);
        else if(zUCType===TYPE_F6SC.toUpperCase())) cartridge = new CartridgeF6SC(zIntImage);      
        else if(zUCType===TYPE_F4SC.toUpperCase())) cartridge = new CartridgeF4SC(zIntImage);
        else if(zUCType===TYPE_FE.toUpperCase())) cartridge = new CartridgeFE(zIntImage);
        else if(zUCType===TYPE_DPC.toUpperCase())) cartridge = new CartridgeDPC(zIntImage);
        else if(zUCType===TYPE_E0.toUpperCase())) cartridge = new CartridgeE0(zIntImage);
         else if(zUCType===TYPE_E7.toUpperCase())) cartridge = new CartridgeE7(zIntImage);
         else if(zUCType===TYPE_3F.toUpperCase())) cartridge = new Cartridge3F(zIntImage);
         else if(zUCType===TYPE_F4.toUpperCase())) cartridge = new CartridgeF4(zIntImage);
         else if(zUCType===TYPE_FASC.toUpperCase()))  cartridge = new CartridgeFASC(zIntImage);*/
        else {
            zMsg="JStella does not yet support Cartridge Type " + aType + ".";
            console.log(zMsg);
       	}
        zMD5 = "6e372f076fb9586aff416144f5cfe1cb";
				//MD5(image); //Calculate the MD5 based on the byte array 
        cartridge.setMD5(zMD5);
        return cartridge;
    }
    
    
    
}
