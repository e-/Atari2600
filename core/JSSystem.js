//============================================================================
//
// MM     MM  6666  555555  0000   2222
// MMMM MMMM 66  66 55     00  00 22  22
// MM MMM MM 66     55     00  00     22
// MM  M  MM 66666  55555  00  00  22222  --  "A 6502 Microprocessor Emulator"
// MM     MM 66  66     55 00  00 22
// MM     MM 66  66 55  55 00  00 22
// MM     MM  6666   5555   0000  222222
//
// Copyright (c) 1995-2007 by Bradford W. Mott and the Stella team
//
// See the file "license" for information on usage and redistribution of
// this file, and for a DISCLAIMER OF ALL WARRANTIES.
//
// $Id: JSSystem.java,v 1.8 2007/09/08 05:47:50 mauvila Exp $
//============================================================================

/**
 * This class is the intermediary between the CPU (J6507) and the rest of the
 * emulator - it is the "CPU's secretary".  Unlike the J6507 class, this class is specifically tailored for
 * the JStella system.
 * <p> 
 * This class implements the IfcSystem interface in the J6507 package, which enables
 * a J6507 object (the CPU) to interact with it.  Each instruction cycle goes roughly like
 * this: <br>
 * The CPU, when it is executed, will ask the JSSystem object for the next instruction.  The
 * JSSystem will forward that request to the ROM (game).  So the ROM returns the instruction,
 * and based on what that instruction was, the CPU may do various things...if that instruction
 * was to change the sound a note, the CPU sends the JSSystem a poke() (a write command)...
 * the JSSystem matches the given address up and sees that the TIA is the intended target, and thus
 * forwards it to that object.  Or if the instruction was to see if the joystick is pointing left,
 * the CPU sends a peek() (a read command) to the JSSystem, which forwards that peek to the RIOT
 * object.
 *     
 * </p>
 * <p>
 *     The JSSystem knows which object to forward a peek/poke to by looking at the address.
 *     Each possible recipient ("device") has previously specified ranges of addresses which it looks
 *     at ("maps to"), and the JSSystem object keeps a list of what device covers what address. (See
 *     the IfcDevice interface.)  If you are curious about what device maps to what range of addresses,
 *     consult the Internet for a memory map for the Atari 2600.
 * </p>
 * @author Bradford W. Mott and the Stella team (original)
 * J.L. Allen (Java translation)
 */
function JSSystem(aConsole){
    this.serialVersionUID = "258470027807684384L";
    
    /**
     * The log(base2) of the size of the memory (addressable space).
     * In other words, 2 to the power of LOG_MEMSIZE equals the size of the addressable
     * space, in bytes.
     */
    
    this.pageOffset = function(aAddress) { return String.fromCharCode(aAddress & PAGE_MASK);  }
    this.myPageAccessTable=new PageAccess[PAGE_COUNT];
    
    
    this.myDeviceList= [];
    this.myNumberOfDevices=0;
    this.myCPU=null;
    this.myConsole=null;
    
    this.myCycles=0;
    this.myNullDevice=new NullDevice();
    this.myDataBusState=0;
    this.myDataBusLocked=false;
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Creates a new JSSystem object.  This will automatically create a J6507 (CPU) object.
     * The other JStella classes should not concern themselves with interacting directly with 
     * the CPU, but instead should act through this class, the CPU's secretary.
     * @param aConsole The 2600 console that this JSSystem belongs to
     */
//    public JSSystem(JSConsole aConsole) {
//        assert((1 <= LOG_PAGESIZE) && (LOG_PAGESIZE <= LOG_MEMSIZE) && (LOG_MEMSIZE <= 16));  // Make sure the settings are reasonable
//        myConsole=aConsole;
//        clearPageAccesses(); // Installs null devices for every page
//        myDataBusLocked = false;  // Bus starts out unlocked (in other words, peek() changes myDataBusState)
//        attach(new J6507(this)); //creates the CPU and installs it
//        
//    }
//    
//    private void readObject(java.io.ObjectInputStream in)  throws IOException, ClassNotFoundException {
//        
//        in.defaultReadObject();
//    }
//    
//    
//    
//    //public void lockDataBus() { myDataBusLocked = true; }
//    //public void unlockDataBus() { myDataBusLocked = false;  }
//    /**
//     * This method is called by the CPU to tell the system that a specified number of cycles have
//     * elapsed. See the documentation under the IfcSystem class.
//     * @param aCyclesElapsed the number of processor cycles that have elapsed
//     */
//    public void processorCycle(int aCyclesElapsed) { this.incrementCycles(aCyclesElapsed); }
//    /**
//     * Returns the number of processor cycles that have elapsed since the last reset.
//     * Don't confuse processor cycles with "instruction cycles"...a given instruction will
//     * last multiple processor cycles.
//     * @return the number of processor cycles that have elapsed
//     */
//    public int getCycles()    {  return myCycles;    }
//    /**
//     * This does the same exact thing as processorCycle().<br>
//     * (I'm can't remember why there are two different methods... JLA Sep 7 2007)
//     * @param amount The number of cycles to add to the growing count
//     */
//    public void incrementCycles(int amount)  {   myCycles += amount;    }
//    /**
//     * (Sep 7 2007 - JLA: I don't know what this does...originally from Stella code)
//     * @return ?
//     */
//    public int getDataBusState() { return myDataBusState; }
//    /**
//     * Halts the CPU.  When the TIA encounters the signal that tells it a visual frame 
//     * is complete, it will call this method, and the CPU will not execute any more 
//     * instructions until it is instructed to do so again.
//     */
//    public void stopCPU() {  getCPU().stop();  }
//   // public PageAccess[] getPages() {  return myPageAccessTable;  }
//    /**
//     * Returns the CPU object (although it's probably better for objects to go
//     * through the JSSystem--the secretary--than go directly to the CPU, just for
//     * encapsulation's sake, not for performance reasons.
//     * @return the CPU object
//     */
//    public J6507 getCPU() { return myCPU; }
//    
//    
//    private NullDevice getNullDevice()  {   return myNullDevice;  }
//    
//    /**
//     * This erases all the the PageAccess objects from the list...it essentially
//     * clears all the "claims" that devices have on ranges of memory
//     */
//    public void clearPageAccesses() {
//        // Initialize page access table
//        PageAccess access=new PageAccess(myNullDevice);
//        for(int page = 0; page < PAGE_COUNT; page++) {
//            myPageAccessTable[page]=new PageAccess(myNullDevice);
//            setPageAccess((char)page, access);
//        } //end : for page loop
//    }
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    /**
//     * This will cause the CPU to execute instructions, up to the number supplied.  If the 
//     * CPU signals another device which, in turn, stops the CPU, this method will exit before
//     * the specified number of instructions have been executed.
//     * @param aInstructionCount the (maximum) number of instructions that will be executed
//     * @throws jstella.core.JSException if an instruction is not recognized
//     * @return the number of CPU instructions executed
//     */
//    public int executeCPU(int aInstructionCount) throws JSException {
//        int zReturn=0; 
//        try{
//            zReturn=myCPU.execute(aInstructionCount);
//        }//end : try
//        catch (J6507Exception e) {
//            if (e.myExceptionType==J6507Exception.ExceptionType.INSTRUCTION_NOT_RECOGNIZED) {
//                throw new JSException(JSException.ExceptionType.INSTRUCTION_NOT_RECOGNIZED, e.myMessage);
//            }//end : inst not recog
//        }//end : catch
//        return zReturn; 
//    }
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    /**
//     * Resets the CPU, and then resets all of the devices currently installed
//     * in this JSSystem object.
//     */
//    public void reset() {
//        resetCycles();  // Reset system cycle counter
//        for (IfcDevice zDev : myDeviceList) { zDev.reset();   } //resets every device
//        if(myCPU != null)  myCPU.reset();  // Now we reset the processor if it exists
//    }
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    /**
//     * This will do two things:<br>
//     * 1. It will add the device to a list of devices that it maintains
//     * 2. It will tell the device to "install" itself...that is, to register a claim
//     * over certain addresses by calling the setPageAccess(...) method.
//     * @param aDevice the device which should be installed
//     */
//    public void attach(IfcDevice aDevice) {
//        assert(myNumberOfDevices < 100);
//        
//       
//        if (myDeviceList.contains(aDevice)==false) myDeviceList.add(aDevice);   // Add device to my collection of devices
//        aDevice.install(this);// Ask the device to install itself
//    }
//    
//    /**
//     * This does the opposite of attach(...)
//     * @param aDevice device to uninstall
//     */
//    public void unattach(IfcDevice aDevice) {
//        //PART 1 : remove the device from the device list
//        boolean zFound=false;
//        do{
//            
//            zFound=myDeviceList.remove(aDevice);
//        }while (zFound==true);
//        
//        //PART 2 : replace the page accesses of the device with those of the null device
//        PageAccess zPA=new PageAccess(getNullDevice());
//        for (int i=0; i<myPageAccessTable.length; i++) {
//            if (aDevice.equals(myPageAccessTable[i].getDevice())) {
//                //System.out.println("DEBUG - unattaching/replacing page " + i + " with null device");
//                setPageAccess(i,zPA);
//            }//end : found page access
//        }//end : for i loop
//        
//    }
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    /**
//     * This sets the CPU object that the system will use.  There should be no reason
//     * for outside classes to call this method... (but it will remain public for the time
//     * being).
//     * @param a6507 the CPU
//     */
//    public void attach(J6507 a6507) {
//        myCPU = a6507;  // Remember the processor
//        myCPU.install(this);   // Ask the processor to install itself
//    }
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    /**
//     * This resets the cycle count that the JSSystem object maintains...it is generally 
//     * called by the TIA at the start of a new visual frame.
//     */
//    public void resetCycles() {
//        for (IfcDevice zDev : myDeviceList) {  zDev.systemCyclesReset();  } // First we let all of the device attached to me know about the reset
//       // if (myCycles!=JSTIA.CPU_CYCLES_PER_FRAME) System.out.println("JSSystem warning: resetting cycles at " + myCycles);
//        myCycles = 0;  // Now, we reset cycle count to zero
//    }
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    /**
//     * Assigns the PageAccess to the given page. <br>
//     * <p>
//     *     PageAccess objects are used to register what device has claimed a given chunk ("page") of
//     *     memory.  Each chunck is 64 bytes of memory.   
//     * </p>
//     * Rather than use the PaceAccess object
//     * passed as an argument, it only makes a copy of the data.  Therefore, later changes
//     * to the object that is passed as an argument will not be reflected in the PageAccess
//     * that is stored in this JSSystem class.
//     * @param page Page number to associate this PageAccess information with
//     * @param access a PageAccess class containing the information that will be copied to the
//     * PageAccess object at the specified page number
//     */
//    public void setPageAccess(int page, PageAccess access) {
//        
//        assert(page <= PAGE_COUNT);  // Make sure the page is within range
//        assert(access.getDevice() != null);   // Make sure the access methods make sense
//        
//        myPageAccessTable[page].copyDataFrom(access);
//        
//        //System.out.println("DEBUG - assigning page " + page + " to " + access.getDevice().name());
//    }
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    /**
//     * Retrieves the PageAccess for a given page.
//     * @param page the page number
//     * @return the corresponding PageAccess
//     */
//    public PageAccess getPageAccess(int page) {
//        assert(page <= PAGE_COUNT);  // Make sure the page is within range
//        return myPageAccessTable[page];
//    }
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// 
//    
//    
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//  
//    
//    public int peek(int addr) {
//        assert(addr>=0);
//        int result=0;
//        result=pageAccessAtAddress(addr).peek(addr);
//        
//        myDataBusState = result;
//        return result;
//    }
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//  
//    
//    /**
//     * This is called by the CPU to write to an address.  The system is responsible for
//     * routing this request to the appropriate device.
//     * @param addr the address to poke
//     * @param aByteValue the byte to associate with that address
//     */
//    public void poke(int addr, int aByteValue) {
//        assert((aByteValue>=0)&&(aByteValue<0x100));
//      
//        pageAccessAtAddress(addr).poke(addr, aByteValue);
//        myDataBusState = aByteValue;
//    }
//    
//    private PageAccess pageAccessAtAddress(int aAddress) {
//        return myPageAccessTable[(aAddress & ADDRESS_MASK) >>> PAGE_SHIFT];
//    }
//    
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    
//   
//    
//    
//    /**
//     * This is called by the CPU to determine what address it should start with after
//     * the CPU has been reset.
//     * @return the address that the CPU should start with
//     */
//    public char getResetPC() {
//        
//        int zReturn= ((peek(0xfffc)) | (peek(0xfffd) << 8));
//        //  dbg.out("getResetPC()", zReturn);
//        return (char)zReturn;
//    }
//    
//    
//    
// 
//    
//    
//    
//    
//    
//    
//    
//}
