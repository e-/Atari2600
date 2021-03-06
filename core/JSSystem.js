function JSSystem(aConsole){
    this.serialVersionUID = "258470027807684384L";
    
    /**
     * The log(base2) of the size of the memory (addressable space).
     * In other words, 2 to the power of LOG_MEMSIZE equals the size of the addressable
     * space, in bytes.
     */
    
    this.pageOffset = function(aAddress) { return (aAddress & PAGE_MASK) /*% 256 */;  }
    this.myPageAccessTable=(function(){
			var t = [];
			for(var i =0; i< PAGE_COUNT;i++){
				t[i] = (new PageAccess());
			}
			return t;})();
    
    
    this.myDeviceList= [];
    this.myNumberOfDevices=0;
    this.myCPU=null;
    this.myConsole=null;
    
    this.myCycles=0;
    this.myNullDevice=new NullDevice();
    this.myDataBusState=0;
    this.myDataBusLocked=false;
    
    this.readObject = function(inn){
        inn.defaultReadObject();
    }
    
    
    
    this.incrementCycles = function( amount)  {   this.myCycles += amount;    }
    /**
     * This method is called by the CPU to tell the system that a specified number of cycles have
     * elapsed. See the documentation under the IfcSystem class.
     * @param aCyclesElapsed the number of processor cycles that have elapsed
     */
    this.processorCycle = function(aCyclesElapsed) {this.incrementCycles(aCyclesElapsed); }
    /**
     * Returns the number of processor cycles that have elapsed since the last reset.
     * Don't confuse processor cycles with "instruction cycles"...a given instruction will
     * last multiple processor cycles.
     * @return the number of processor cycles that have elapsed
     */
    this.getCycles = function()    {  return this.myCycles;    }
    this.getDataBusState = function() { return this.myDataBusState; }
    /**
     * Returns the CPU object (although it's probably better for objects to go
     * through the JSSystem--the secretary--than go directly to the CPU, just for
     * encapsulation's sake, not for performance reasons.
     * @return the CPU object
     */
    this.getCPU = function() { return this.myCPU; }
    
    /**
     * Halts the CPU.  When the TIA encounters the signal that tells it a visual frame 
     * is complete, it will call this method, and the CPU will not execute any more 
     * instructions until it is instructed to do so again.
     */
    this.stopCPU = function() {  this.getCPU().stop();  }
   // public PageAccess[] getPages() {  return myPageAccessTable;  }

    
    this.getNullDevice = function()  {   return this.myNullDevice;  }
    
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Assigns the PageAccess to the given page. <br>
     * <p>
     *     PageAccess objects are used to register what device has claimed a given chunk ("page") of
     *     memory.  Each chunck is 64 bytes of memory.   
     * </p>
     * Rather than use the PaceAccess object
     * passed as an argument, it only makes a copy of the data.  Therefore, later changes
     * to the object that is passed as an argument will not be reflected in the PageAccess
     * that is stored in this JSSystem class.
     * @param page Page number to associate this PageAccess information with
     * @param access a PageAccess class containing the information that will be copied to the
     * PageAccess object at the specified page number
     */
    this.setPageAccess = function(page, access) {
        assert(page <= PAGE_COUNT);  // Make sure the page is within range
        assert(access.getDevice() != null);   // Make sure the access methods make sense
        
        this.myPageAccessTable[page].copyDataFrom(access);
        
        //System.out.println("DEBUG - assigning page " + page + " to " + access.getDevice().name());
    }
    
    /**
     * This erases all the the PageAccess objects from the list...it essentially
     * clears all the "claims" that devices have on ranges of memory
     */
    this.clearPageAccesses = function() {
        // Initialize page access table
        var access=new PageAccess(this.myNullDevice);
        for(var page = 0; page < PAGE_COUNT; page++) {
            this.myPageAccessTable[page]=new PageAccess(this.myNullDevice);
            this.setPageAccess(page /*% 256 */, access);
        } //end : for page loop
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * This will cause the CPU to execute instructions, up to the number supplied.  If the 
     * CPU signals another device which, in turn, stops the CPU, this method will exit before
     * the specified number of instructions have been executed.
     * @param aInstructionCount the (maximum) number of instructions that will be executed
     * @throws jstella.core.JSException if an instruction is not recognized
     * @return the number of CPU instructions executed
     */
    this.executeCPU = function(aInstructionCount){
        var zReturn=0; 
        //try{
            zReturn=this.myCPU.execute(aInstructionCount);
        //}//end : try
        return zReturn; 
    }
  
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * This will do two things:<br>
     * 1. It will add the device to a list of devices that it maintains
     * 2. It will tell the device to "install" itself...that is, to register a claim
     * over certain addresses by calling the setPageAccess(...) method.
     * @param aDevice the device which should be installed
     */
    this.attach = function(aDevice) {
				if(aDevice instanceof J6507){
					console.log("a CPU attached!");
		     	this.myCPU = aDevice;  // Remember the processor
    	    this.myCPU.install(this);   // Ask the processor to install itself
				}
				else {
					console.log("a Device attached!");
        assert(this.myNumberOfDevices < 100);
       	
				var flag = false;
				var length = this.myDeviceList.length;
				for(var i=0;i<length;i++){
					if(this.myDeviceList[i] == aDevice) flag=true;
				}
				if(!flag)
					this.myDeviceList.push(aDevice);
        aDevice.install(this);// Ask the device to install itself
				}
    }
    
    /**
     * This does the opposite of attach(...)
     * @param aDevice device to uninstall
     */
    this.unattach = function(aDevice) {
        //PART 1 : remove the device from the device list
        var zFound=false;
        do{
					zFound = false;
					t = this.myDeviceList.length;
					for(var i=0;i<t;i++){
						if(this.myDeviceList[i] == aDevice)
						{
							this.myDeviceList.remove(i);
							zFound = true;
							break;
						}
					}
           // zFound=this.myDeviceList.remove(aDevice);
        }while (zFound==true);
        
        //PART 2 : replace the page accesses of the device with those of the null device
        var zPA=new PageAccess(this.getNullDevice());
        for (var i=0; i<this.myPageAccessTable.length; i++) {
            if (aDevice == this.myPageAccessTable[i].getDevice()) {
                //System.out.println("DEBUG - unattaching/replacing page " + i + " with null device");
                this.setPageAccess(i,zPA);
            }//end : found page access
        }//end : for i loop
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   
    /**
     * This resets the cycle count that the JSSystem object maintains...it is generally 
     * called by the TIA at the start of a new visual frame.
     */
    this.resetCycles = function(){
				length = this.myDeviceList.length;

        for (var i=0;i<length;i++) {  this.myDeviceList[i].systemCyclesReset();  } // First we let all of the device attached to me know about the reset
       // if (myCycles!=JSTIA.CPU_CYCLES_PER_FRAME) System.out.println("JSSystem warning: resetting cycles at " + myCycles);
        this.myCycles = 0;  // Now, we reset cycle count to zero
    }
    
  
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Resets the CPU, and then resets all of the devices currently installed
     * in this JSSystem object.
     */
    this.reset = function() {
        this.resetCycles();  // Reset system cycle counter
        for (var zDev=0;zDev<this.myDeviceList.length;zDev++) { this.myDeviceList[zDev].reset();   } //resets every device
        if(this.myCPU != null)  this.myCPU.reset();  // Now we reset the processor if it exists
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Retrieves the PageAccess for a given page.
     * @param page the page number
     * @return the corresponding PageAccess
     */
    this.getPageAccess = function(page) {
        assert(page <= PAGE_COUNT);  // Make sure the page is within range
        return this.myPageAccessTable[page];
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
  
    
    this.peek = function( addr) {
        assert(addr>=0);
        var result=0;
        result=this.pageAccessAtAddress(addr).peek(addr);
        
        this.myDataBusState = result;
        return result;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
  
    
    /**
     * This is called by the CPU to write to an address.  The system is responsible for
     * routing this request to the appropriate device.
     * @param addr the address to poke
     * @param aByteValue the byte to associate with that address
     */
    this.poke = function(addr, aByteValue) {
        assert((aByteValue>=0)&&(aByteValue<0x100));
      
        this.pageAccessAtAddress(addr).poke(addr, aByteValue);
        this.myDataBusState = aByteValue;
    }
    
    this.pageAccessAtAddress = function(aAddress) {
        return this.myPageAccessTable[(aAddress & ADDRESS_MASK) >>> PAGE_SHIFT];
    }
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
   
    
    
    /**
     * This is called by the CPU to determine what address it should start with after
     * the CPU has been reset.
     * @return the address that the CPU should start with
     */
    this.getResetPC = function() {
        
        var zReturn= ((this.peek(0xfffc)) | (this.peek(0xfffd) << 8));
        //  dbg.out("getResetPC()", zReturn);
        return zReturn;
    }
    
    
    
 
    
    
    
    
    
    
    
    /**
     * Creates a new JSSystem object.  This will automatically create a J6507 (CPU) object.
     * The other JStella classes should not concern themselves with interacting directly with 
     * the CPU, but instead should act through this class, the CPU's secretary.
     * @param aConsole The 2600 console that this JSSystem belongs to
     */

	   if(!((1 <= LOG_PAGESIZE) && (LOG_PAGESIZE <= LOG_MEMSIZE) && (LOG_MEMSIZE <= 16)))
				{
					console.log("ASSERT ERROR");
				}
        this.myConsole=aConsole;

        this.clearPageAccesses(); // Installs null devices for every page

        this.myDataBusLocked = false;  // Bus starts out unlocked (in other words, peek() changes myDataBusState)
        this.attach	(new J6507(this)); //creates the CPU and installs it
}
