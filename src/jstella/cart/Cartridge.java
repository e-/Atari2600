/*
 *
 *
 *Tutorial :
 *
 * How to make (port) a Cartridge subclass (from a Stella class)
 *      1. Constructor 
 *            Use the copyImage(...) on the passed int array to copy the ROM's image
 *            If the ROM has RAM, it can be randomized by randomizeRAM(...) call 
 *      2. Add a serialVersionUID - this simply has to be an original (random) number that you make up; used for serialization (i.e. saved games)
 *      2. Install and Bank methods
 *            Don't worry about the shift and mask stuff...it already knows
 *            If a page access is not a direct poke/peek, use the addIndirectAccess.
 *            If a page access is peek or poke, use the corresponding addDirect___Access.  If there is an offset, use the one
 *                    that has an offset parameter.
 *
 *
 *
 *         For example, let's say this is the original C++ code for Stella :
 *             uInt16 offset = myCurrentBank * 4096;
               uInt16 shift = mySystem->pageShift();
               uInt16 mask = mySystem->pageMask();
               System::PageAccess access;
                access.device = this;
                for(uInt32 address = 0x1200; address < (0x1FF8U & ~mask); address += (1 << shift))
                       {
                        access.directPokeBase = 0;
                        access.directPeekBase = &myImage[offset + (address & 0x0FFF)];
                        mySystem->setPageAccess(address >> shift, access);
                       }
 *
 *
 *
 *
 *
 *            Here is the JStella version :
 *                  addDirectPeekAccess(0x1200, 0x1FF8, myImage, 0x0FFF, myCurrentBank*4096);
 *
 *
 *            
 *
 */

package jstella.cart;
import java.io.*;

import static jstella.core.JSConstants.*;
import jstella.core.*;
import jstella.core.IfcDevice;

/**
 * A cartridge is a device which contains the machine code for a
 * game and handles any bankswitching performed by the cartridge.
 * <p>
 * <ul>
 * Terms:
 * <li> ROM : read-only-memory; used interchangeably with cartridge/game, specifically,
 * the data that a cartridge holds
 * <li> Image : the data that a cartridge holds (not an "image" in the typical
 * sense...)
 * <li> MD5 : message digest 5, a string that is produced by looking at (in this case)
 * an array of bytes, and using mathematical formula to derive a unique sort of "serial number"
 * for the given data.
 * </ul>
 * @author Bradford W. Mott
 */
public abstract class Cartridge implements IfcDevice, java.io.Serializable {
    private final static long serialVersionUID = 7979700597113264401L;
    // public final static String AUTODETECT="AUTO-DETECT";
    
    public final static String TYPE_2K="2K";
    public final static String TYPE_4K="4K";
    public final static String TYPE_F8="F8";
    public final static String TYPE_F8SWAPPED="F8 swapped";
    public final static String TYPE_F8SC="F8SC";
    public final static String TYPE_FASC="FASC";
    public final static String TYPE_F6="F6";
    public final static String TYPE_F6SC="F6SC";
    public final static String TYPE_F4="F4";
    public final static String TYPE_FE="FE";
    public final static String TYPE_DPC="DPC";
    public final static String TYPE_E0="E0";
    public final static String TYPE_E7="E7";
    public final static String TYPE_3F="3F";
    public final static String TYPE_F4SC="F4SC";
    
    
    
    
    
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     *
     */
    protected Cartridge() {
        unlockBank();
    }
    
    public void lockBank()   { myBankLocked = true;  }
    public void unlockBank() { myBankLocked = false; }
    /**
     * Returns the MD5 (message digest 5) associated with this ROM
     * @return the MD5 of this ROM
     */
    public String getMD5() { return myMD5; }
    public void setMD5(String aMD5) { myMD5=aMD5; }
    
    protected JSSystem mySystem=null;
    protected boolean myBankLocked=false;
    protected String myMD5="";
    //protected int[] myImage=null;
    
    protected abstract int[] getImage();
    protected abstract void setCurrentBank(int bank);
    protected abstract int getCurrentBank();
    protected abstract int bankCount();
    public abstract boolean patch(int address, int value);
    
    protected JSConsole myConsole=null;
    public void setConsole(JSConsole aConsole) { myConsole=aConsole; }
    
   
    
    public void systemCyclesReset() {    }
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 
    
    
    /**
     * Converts a byte array to an int array.  ROMs are stored as bytes, but JStella
     * works with them as ints.  So conversion must occur after loading the data from
     * a disk, etc.
     * @param aByteArray a byte array
     * @return an int array
     */
    public static int[] toIntArray(byte[] aByteArray) {
        int[] zReturn=new int[aByteArray.length];
        for (int i=0; i<zReturn.length; i++) {
            zReturn[i]=((int)aByteArray[i]) & 0xFF;  //The 0xFF mask makes sure that the new int is positive unsigned version
        }
        return zReturn;
    }
    
    public static byte[] toByteArray(int[] aIntArray) {
        byte[] zReturn=new byte[aIntArray.length];
        for (int i=0; i<zReturn.length; i++) {
            zReturn[i]=(byte)aIntArray[i];
        }
        return zReturn;
    }
    
    
    /**
     * Looks at the byte data given and calculates a unique identifier string (Message Digest 5)
     * based on that data.
     * @param aData the byte data to analyze
     * @return the MD5 string
     */
    public static String calculateMD5(byte[] aData) {
        String zReturn="";
        try{
            java.security.MessageDigest zMD=java.security.MessageDigest.getInstance("MD5");
            zMD.update(aData);
            byte[] zSum=zMD.digest();
            java.math.BigInteger zBig=new java.math.BigInteger(1, zSum);
            zReturn=zBig.toString(16);
        }//end : try
        catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return zReturn;
    }
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Creates a cartridge from the given data.  This is the preferred method
     * for creating cartridge objects.  It detects what type of cartidge needs to be created
     * based on the data, and returns a newly created one.
     * @param image the ROM data
     * @return a cartridge object
     */
    public static Cartridge create(byte[] image)  throws JSException {
        String zMD5 = calculateMD5(image); //Calculate the MD5 based on the byte array
        System.out.println("LOADING ROM : " + zMD5);
       
        Cartridge cartridge = null;
        
       
        String zType=detectTypeByMD5(zMD5);
        if (zType==null) zType= detectTypeByImage(image);
        
        
        java.lang.System.out.println("JStella - detected cartridge type: " + zType);
        // We should know the cart's zType by now so let's create it
        cartridge=create(image, zType);
      
        return cartridge;
    }
    
    
    public static Cartridge create(byte[] image, String aType)  throws JSException {
        Cartridge cartridge=null;
        String zUCType=aType.toUpperCase();
         int[] zIntImage=toIntArray(image); //create an int array from the byte array
        if(zUCType.equals(TYPE_2K.toUpperCase())) cartridge = new Cartridge2K(zIntImage);
        else if(zUCType.equals(TYPE_4K.toUpperCase())) cartridge = new Cartridge4K(zIntImage);
        else if(zUCType.equals(TYPE_F8.toUpperCase())) cartridge = new CartridgeF8(zIntImage, false);
        else if(zUCType.equals(TYPE_F8SWAPPED.toUpperCase())) cartridge = new CartridgeF8(zIntImage, true);
        else if(zUCType.equals(TYPE_F8SC.toUpperCase())) cartridge = new CartridgeF8SC(zIntImage);
        else if(zUCType.equals(TYPE_F6.toUpperCase())) cartridge = new CartridgeF6(zIntImage);
        else if(zUCType.equals(TYPE_F6SC.toUpperCase())) cartridge = new CartridgeF6SC(zIntImage);      
        else if(zUCType.equals(TYPE_F4SC.toUpperCase())) cartridge = new CartridgeF4SC(zIntImage);
        else if(zUCType.equals(TYPE_FE.toUpperCase())) cartridge = new CartridgeFE(zIntImage);
        else if(zUCType.equals(TYPE_DPC.toUpperCase())) cartridge = new CartridgeDPC(zIntImage);
        else if(zUCType.equals(TYPE_E0.toUpperCase())) cartridge = new CartridgeE0(zIntImage);
         else if(zUCType.equals(TYPE_E7.toUpperCase())) cartridge = new CartridgeE7(zIntImage);
         else if(zUCType.equals(TYPE_3F.toUpperCase())) cartridge = new Cartridge3F(zIntImage);
         else if(zUCType.equals(TYPE_F4.toUpperCase())) cartridge = new CartridgeF4(zIntImage);
         else if(zUCType.equals(TYPE_FASC.toUpperCase()))  cartridge = new CartridgeFASC(zIntImage);
      /*
  else if(zUCType.equals("3E")
    cartridge = new Cartridge3E(image, size);
 
  else if(zUCType.equals("4A50")
    cartridge = new Cartridge4A50(image);
       
  else if(zUCType.equals("AR")
    cartridge = new CartridgeAR(image, size, true); //settings.getboolean("fastscbios")
       
       
 
 
  
       
       
       
       
  
       
  else if(zUCType.equals("MC")
    cartridge = new CartridgeMC(image, size);
  else if(zUCType.equals("MB")
    cartridge = new CartridgeMB(image);
  else if(zUCType.equals("CV")
    cartridge = new CartridgeCV(image, size);
  else if(zUCType.equals("UA")
    cartridge = new CartridgeUA(image);
  else if(zUCType.equals("0840")
    cartridge = new Cartridge0840(image);
       */
        else {
            //java.lang.System.out.println("NOT YET SUPPORTED: Invalid cartridge type " + aType + " ...");
            String zMsg="JStella does not yet support Cartridge Type " + aType + ".";
            throw(new JSException(JSException.ExceptionType.CART_NOT_SUPPORTED, zMsg));
        }
        //cartridge.createImage(zIntImage);
         String zMD5 = calculateMD5(image); //Calculate the MD5 based on the byte array 
        cartridge.setMD5(zMD5);
        return cartridge;
    }
    
    private static boolean arrayCompare(byte[] aArray, int aIndexA, int aIndexB, int aCompCount) {
        boolean zReturn=true;
        
        for (int i=0; i<aCompCount; i++) {
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
    private static String detectTypeByMD5(String aMD5) {
        String zReturn=null;
        aMD5=aMD5.toLowerCase().trim();
        
        if (aMD5.equals("bc24440b59092559a1ec26055fd1270e")) zReturn=TYPE_F8SWAPPED;
        else if (aMD5.equals("75ee371ccfc4f43e7d9b8f24e1266b55")) zReturn=TYPE_F8SWAPPED;
        else if (aMD5.equals("6dda84fb8e442ecf34241ac0d1d91d69")) zReturn=TYPE_F6SC;
                       


        return zReturn;
    }
    
    
    
    /**
     * Looks at the data to determine (or guess) what type of cartridge the data
     * represents.
     * @param image the ROM data
     * @return the cartridge type
     */
    private static String detectTypeByImage(byte[] image) {
        // Guess type based on size
        String type ="";
        int size=image.length;
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
            if(isProbably3E(image, size))
                type = "3E";
            else if(isProbably3F(image, size))
                type = TYPE_3F;
            else
                type = TYPE_4K;  // Most common bankswitching type
        }
        
        return type;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    static boolean searchForBytes(byte[] image, byte[] signature,  int sigsize, int minhits) {
        int count = 0;
        int imagesize=image.length;
        for(int i = 0; i < imagesize - sigsize; /*++i*/ i++) {
             int matches = 0;
            for(int j = 0; j < sigsize; /*++j*/j++) {
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
    }
    
    
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    static boolean isProbablySC(byte[] image, int size) {
        // We assume a Superchip cart contains the same bytes for its entire
        // RAM area; obviously this test will fail if it doesn't
        // The RAM area will be the first 256 bytes of each 4K bank
        int banks = (int)((double)size / 4096);
        for(int i = 0; i < banks; ++i) {
            byte first = image[i*4096];
            for(int j = 0; j < 256; ++j) {
                if(image[(i*4096)+j] != first)
                    return false;
            }
        }
        return true;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    static boolean isProbably3F(byte[] image, int size) {
        // 3F cart bankswitching is triggered by storing the bank number
        // in address 3F using 'STA $3F'
        // We expect it will be present at least 2 times, since there are
        // at least two banks
        int[] intsignature= { 0x85, 0x3F };  // STA $3F
        byte[] signature=toDataUByte(intsignature);
        return searchForBytes(image, signature, 2, 2);
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    static boolean isProbably3E(byte[] image, int size) {
        // 3E cart bankswitching is triggered by storing the bank number
        // in address 3E using 'STA $3E', commonly followed by an
        // immediate mode LDA
        int[] intsignature = { 0x85, 0x3E, 0xA9, 0x00 };  // STA $3E; LDA #$00
        byte[] signature=toDataUByte(intsignature);
        return searchForBytes(image, signature, 4, 1);
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    static boolean isProbablyE0(byte[] image, int size) {
        // E0 cart bankswitching is triggered by accessing addresses
        // $FE0 to $FF9 using absolute non-indexed addressing
        // To eliminate false positives (and speed up processing), we
        // search for only certain known signatures
        // Thanks to "stella@casperkitty.com" for this advice
        // These signatures are attributed to the MESS project
        int[][] intsignature = { /*[6][3]*/
            { 0x8D, 0xE0, 0x1F },  // STA $1FE0
            { 0x8D, 0xE0, 0x5F },  // STA $5FE0
            { 0x8D, 0xE9, 0xFF },  // STA $FFE9
            { 0xAD, 0xE9, 0xFF },  // LDA $FFE9
            { 0xAD, 0xED, 0xFF },  // LDA $FFED
            { 0xAD, 0xF3, 0xBF }   // LDA $BFF3
        };
        byte[][] signature=toDataUByte(intsignature);
        for(int i = 0; i < 6; ++i) {
            if(searchForBytes(image, signature[i], 3, 1))
                return true;
        }
        return false;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    static boolean isProbablyE7(byte[] image, int size) {
        // E7 carts map their second 1K block of RAM at addresses
        // $800 to $8FF.  However, since this occurs in the upper 2K address
        // space, and the last 2K in the cart always points to the last 2K of the
        // ROM image, the RAM area should fall in addresses $3800 to $38FF
        // Similar to the Superchip cart, we assume this RAM block contains
        // the same bytes for its entire area
        // Also, we want to distinguish between ROMs that have large blocks
        // of the same amount of (probably unused) data by making sure that
        // something differs in the previous 32 or next 32 bytes
        byte first = image[0x3800];
        for(int i = 0x3800; i < 0x3A00; ++i) {
            if(first != image[i])
                return false;
        }
        
        // OK, now scan the surrounding 32 byte blocks
        int count1 = 0, count2 = 0;
        for(int i = 0x3800 - 32; i < 0x3800; ++i) {
            if(first != image[i])
                ++count1;
        }
        for(int i = 0x3A00; i < 0x3A00 + 32; ++i) {
            if(first != image[i])
                ++count2;
        }
        
        return (count1 > 0 || count2 > 0);
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    static boolean isProbablyUA(byte[] image, int size) {
        // UA cart bankswitching switches to bank 1 by accessing address 0x240
        // using 'STA $240'
        int[] intsignature = { 0x8D, 0x40, 0x02 };  // STA $240
        byte[] signature=toDataUByte(intsignature);
        return searchForBytes(image, signature, 3, 1);
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    static boolean isProbablyCV(byte[] image, int size) {
        // CV RAM access occurs at addresses $f3ff and $f400
        // These signatures are attributed to the MESS project
        int[][] intsignature /*[2][3]*/ = {
            { 0x9D, 0xFF, 0xF3 },  // STA $F3FF
            { 0x99, 0x00, 0xF4 }   // STA $F400
        };
        byte[][] signature=toDataUByte(intsignature);
        if(searchForBytes(image, signature[0], 3, 1))
            return true;
        else
            return searchForBytes(image, signature[1], 3, 1);
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    static boolean isProbablyFE(byte[] image, int size) {
        // FE bankswitching is very weird, but always seems to include a
        // 'JSR $xxxx'
        // These signatures are attributed to the MESS project
        int[][] intsignature/*[4][5]*/ = {
            { 0x20, 0x00, 0xD0, 0xC6, 0xC5 },  // JSR $D000; DEC $C5
            { 0x20, 0xC3, 0xF8, 0xA5, 0x82 },  // JSR $F8C3; LDA $82
            { 0xD0, 0xFB, 0x20, 0x73, 0xFE },  // BNE $FB; JSR $FE73
            { 0x20, 0x00, 0xF0, 0x84, 0xD6 }   // JSR $F000; STY $D6
        };
        byte[][] signature=toDataUByte(intsignature);
        for(int i = 0; i < 4; ++i) {
            if(searchForBytes(image, signature[i], 5, 1))
                return true;
        }
        return false;
    }
    
    
    
    
   // ============== Subclass convenience methods =====================
    
    protected static int[] copyImage(int[] aSourceImage)
    {
        int[] zReturn=new int[aSourceImage.length];
        for (int i=0; i<aSourceImage.length; i++)
        {
            zReturn[i]=aSourceImage[i];
        }//end : for i loop
        return zReturn;
    }
    
    protected static void randomizeRAM(int[] aRAM)
    {
     java.util.Random zRandom=new java.util.Random();
        
        for(int i = 0; i < aRAM.length; i++) {
            aRAM[i] = zRandom.nextInt() & 0xFF;
        }//end : for i loop
    }
    
    

    
    
    protected final void addIndirectAccess(int aStartAddress, int aEndAddress)
    {
        
        for(int zAddress = (aStartAddress & ~PAGE_MASK); zAddress < (aEndAddress & ~PAGE_MASK); zAddress += PAGE_SIZE)
        {
            mySystem.setPageAccess(zAddress >> PAGE_SHIFT, PageAccess.createIndirectAccess(this));
        } 
    }
    
    
     
    
    
    protected final void addDirectPeekAccess(int aStartAddress, int aEndAddress, int[] aMemory, int aBaseAddressMask, int aBaseAddressOffset)
    {
        for(int zAddress = (aStartAddress & ~PAGE_MASK); zAddress < (aEndAddress & ~PAGE_MASK); zAddress += PAGE_SIZE)
        {
            mySystem.setPageAccess(zAddress >> PAGE_SHIFT, PageAccess.createDirectPeekAccess(this, aMemory, aBaseAddressOffset + (zAddress & aBaseAddressMask)));
        }    
    }
    
    protected final void addDirectPeekAccess(int aStartAddress, int aEndAddress, int[] aMemory, int aBaseAddressMask)
     {
         addDirectPeekAccess(aStartAddress, aEndAddress, aMemory, aBaseAddressMask, 0);
     }
    
    protected final void addDirectPokeAccess(int aStartAddress, int aEndAddress, int[] aMemory, int aBaseAddressMask, int aBaseAddressOffset)
    {
        for(int zAddress = (aStartAddress & ~PAGE_MASK); zAddress < (aEndAddress & ~PAGE_MASK); zAddress += PAGE_SIZE)
        {
            mySystem.setPageAccess(zAddress >> PAGE_SHIFT, PageAccess.createDirectPokeAccess(this, aMemory, aBaseAddressOffset + (zAddress & aBaseAddressMask)));
        } 
    }
    
    protected final void addDirectPokeAccess(int aStartAddress, int aEndAddress, int[] aMemory, int aBaseAddressMask)
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
    public static byte toDataUByte(int aByteValue) {
        return (byte)(aByteValue & 0xff);
    }
    
    
    /**
     * Performs toDataUByte(int) on an array
     * @param aArray Array of values
     * @return Array of bytes
     */
    public static  byte[] toDataUByte(int[] aArray) {
        byte[] zReturn=new byte[aArray.length];
        for (int i=0; i<aArray.length; i++) {
            zReturn[i]=toDataUByte(aArray[i]);
        }//end : for i loop
        return zReturn;
    }
    
    /**
     * Performs toDataUByte(int) on a double array
     * @param aArray a double array
     * @return a double array of byte
     */
    public static byte[][] toDataUByte(int[][] aArray) {
        byte[][] zReturn=new byte[aArray.length][];
        for (int i=0; i<aArray.length; i++) {
            zReturn[i]=new byte[aArray[i].length];
            for (int j=0; j<aArray[i].length; j++) {
                zReturn[i][j]=toDataUByte(aArray[i][j]);
            }//end : for j loop
        }//end : for i loop
        return zReturn;
    }

   /* public int hashCode() {
       return myMD5.hashCode();
    }
    */

    public String toString() {
        return "Cartridge : " + this.name() + "; md5=" + myMD5;
    }
    
    
    
}
