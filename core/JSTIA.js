function JSTIA(aConsole) {
    this.serialVersionUID = -1703217043035095708;
    //TODO : Maybe get rid of the old offset (0-3) system, as currently used in these masks
    //TODO : Get rid of disabled missile mask table...
    
    
    
    this.COSMICBUG_MOVEMENT = [18, 33, 0, 17];
    
    
    this.M0Disabled=false;
    this.M1Disabled=false;
    
    this.myCurrentM0Mask=[0,0,0,0];
    this.myCurrentM1Mask=[0,0,0,0];
    this.myCurrentP0Mask=[0,0,0,0];
    this.myCurrentP1Mask=[0,0,0,0];
    this.myCurrentBLMask=[0,0,0];
    
  
    
    
    this.myTIAPokeRegister=[]; // TIA_POKE_REGISTER_COUNT;
    for (var i=0;i<TIA_POKE_REGISTER_COUNT;i++) {
      this.myTIAPokeRegister[i] = 0;
    }
    
    
    
    this.myConsole=null;
    this.mySystem=null;
    
    
    this.myColorLossEnabled;  //No clue what this means, except it has something to do with PAL (-JLA)
    this.myPartialFrameFlag;
   // private int myFrameCounter=0;   // Number of frames displayed by this TIA
    this.myFramePointer=0;  // Index to the next pixel that will be drawn in the current frame buffer
    this.myFrameXStart=0;  // Indicates where the scanline should start being displayed
   
    
    

    this.myClockWhenFrameStarted=0;
    this.myClockStartDisplay=0; // Indicates color clocks when frame should begin to be drawn
    this.myClockStopDisplay=0;
    this.myClockAtLastUpdate=0;
    this.myClocksToEndOfScanLine=0;
    this.myScanlineCountForLastFrame=0;
    this.myCurrentScanline=0;  // Indicates the current scanline during a partial frame.
    this.myMaximumNumberOfScanlines=0;
    this.myVSYNCFinishClock=0;  // Color clock when VSYNC ending causes a new frame to be started
    
    this.myEnabledObjects=0;
    
    
    this.myPlayfieldPriorityAndScore=0;
    
    this.myVBlankOff=0;
    //private int myPreviousVBlankOff=0;
    this.myVBlankOn=0;
    this.myVSyncOn=0;
    this.myDetectedYStart=0;
    this.myDetectedYStop=0;
    
    
    
    
    // ---------- TIA register variables -----------
  
    
    
    
   
    
   
    
    this.myDGRP0=0;        // Player 0 delayed graphics register
    this.myDGRP1=0;        // Player 1 delayed graphics register
    

    this.myDENABL;        // Indicates if the vertically delayed ball is enabled
    
 

    
    
    this.myCollision=0;    // Collision register
    
    
    this.myPOSP0=0;         // Player 0 position register
    this.myPOSP1=0;         // Player 1 position register
    this.myPOSM0=0;         // Missile 0 position register
    this.myPOSM1=0;         // Missile 1 position register
    this.myPOSBL=0;         // Ball position register
    
    this.myCurrentGRP0=0;
    
    this.myCurrentGRP1=0;
    
    
    
    
    
    // Audio values. Only used by TIADebug.
 /*   private int myAUDV0;
    private int myAUDV1;
    private int myAUDC0;
    private int myAUDC1;
    private int myAUDF0;
    private int myAUDF1;
  */
    
    
    //------ Other variables --------
    
    this.myDumpDisabledCycle=0;  // Indicates when the dump for paddles was last set
    this.myDumpEnabled=false; // Indicates if the dump is current enabled for the paddles
    
    this.myLastHMOVEClock=0;  // Color clock when last HMOVE occured
    this.myHMOVEBlankEnabled=false; // Indicates if HMOVE blanks are currently enabled 
    this.myAllowHMOVEBlanks=false; // Indicates if we're allowing HMOVE blanks to be enabled (?-JLA)
    this.myM0CosmicArkMotionEnabled=true; // TIA M0 "bug" used for stars in Cosmic Ark flag
    this.myM0CosmicArkCounter=0;
   // private boolean[] myBitEnabled=new boolean[6];
    
    
    
    
    this.debugInstructionsExecuted=0;
    this.debugHasExecutionOverrun=false;
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.myConsole=aConsole;
        
    this.myColorLossEnabled=false;
    this.myMaximumNumberOfScanlines=LINES_PER_FRAME_TOTAL; //262 scanlines        
    
   
    
   
    
    
    this.writeObject = function(out) 
    {
      out.defaultWriteObject();
    } 
    
    this.readObject = function(inn) {
        inn.defaultReadObject();
    }

    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //========================== SIMPLE ACCESSOR METHODS ===================================
    // Note : some of these may seem superfluous, especially for internal use, but they
    // can come in handy when debugging.  (One can put an assert() in the accessor methods to see
    // exactly what bad data comes in and what goes out, and when these things occur.)
    
    
    
    this.getCOLUBK = function(){  return this.myTIAPokeRegister[COLUBK];  }
    this.setCOLUBK = function(aValue)    {   this.myTIAPokeRegister[COLUBK]=aValue;     }
    this.getCOLUPF = function()    {  return this.myTIAPokeRegister[COLUPF];   }
    this.setCOLUPF = function(aValue)   {   this.myTIAPokeRegister[COLUPF]=aValue;    }
    this.getCOLUP0 = function()   {   return this.myTIAPokeRegister[COLUP0];   }
    this.setCOLUP0 = function(aValue)    {   this.myTIAPokeRegister[COLUP0]=aValue;    }
    this.getCOLUP1 = function()   {   return this.myTIAPokeRegister[COLUP1];    }
    this.setCOLUP1 = function(aValue)    {    this.myTIAPokeRegister[COLUP1]=aValue;     }
 
    this.getCurrentFrameBuffer = function() { return this.myConsole.getVideo().getCurrentFrameBuffer(); }
    
  
    
   // public int getVBlankOn() { return myVBlankOn; }
   // public int getVBlankOff() { return myVBlankOff; }
    this.getVSyncOn = function() { return this.myVSyncOn; }
    this.getDetectedYStart = function() { return this.myDetectedYStart; }
    this.getDetectedYStop = function() { return this.myDetectedYStop; }
            
    
    /**
     * Answers the total number of scanlines the media source generated
     * in producing the current frame buffer. For partial frames, this
     * will be the current scanline.
     * @return total number of scanlines generated
     */
    this.scanlines = function() { return Math.floor(((this.mySystem.getCycles() * CLOCKS_PER_CPU_CYCLE) - this.myClockWhenFrameStarted)/CLOCKS_PER_LINE_TOTAL);}
    this.getConsole = function() { return this.myConsole; }
    
    
    //The get and set accessors for the current masks were originally done to ease the change from C++ to Java...
    //i.e. it involved less typing for me. -JLA
    this.getCurrentP0Mask = function(aIndex) {
        return PLAYER_MASK_TABLE[this.myCurrentP0Mask[0]][this.myCurrentP0Mask[1]][this.myCurrentP0Mask[2]][this.myCurrentP0Mask[3]+aIndex];
        
    }
    
    this.getCurrentP1Mask = function(aIndex) {
        return PLAYER_MASK_TABLE[this.myCurrentP1Mask[0]][this.myCurrentP1Mask[1]][this.myCurrentP1Mask[2]][this.myCurrentP1Mask[3]+aIndex];
        
    }
    
    
    this.getCurrentM0Mask = function(aIndex) {
        if (this.M0Disabled==true) return this.bool(DISABLED_MASK_TABLE[aIndex]);
        else {
            assert(this.myCurrentM0Mask[3] + aIndex < 360);
            return MISSILE_MASK_TABLE[this.myCurrentM0Mask[0]][this.myCurrentM0Mask[1]][this.myCurrentM0Mask[2]][this.myCurrentM0Mask[3]+aIndex];
            //return getOMMT(myCurrentM0MaskIndex + aIndex);
        }//end : not disabled
    }
    
    this.getCurrentM1Mask = function(aIndex) {
        if (this.M1Disabled==true) return this.bool(DISABLED_MASK_TABLE[aIndex]);
        else {
            assert(this.myCurrentM1Mask[3] + aIndex < 360);
            return MISSILE_MASK_TABLE[this.myCurrentM1Mask[0]][this.myCurrentM1Mask[1]][this.myCurrentM1Mask[2]][this.myCurrentM1Mask[3]+aIndex];
            
        }//end : not disabled
    }
    
    this.setCurrentM1Mask = function(aA, aB, aC, aD) {
        this.myCurrentM1Mask[0]=aA;
        this.myCurrentM1Mask[1]=aB;
        this.myCurrentM1Mask[2]=aC;
        this.myCurrentM1Mask[3]=aD;
        this.M1Disabled=false;
    }
    
    this.setCurrentP0Mask = function(aA, aB, aC, aD) {
        if (this.debugLockP0Mask==false) {
            this.myCurrentP0Mask[0]=aA;
            this.myCurrentP0Mask[1]=aB;
            this.myCurrentP0Mask[2]=aC;
            this.myCurrentP0Mask[3]=aD;
        }//end : is false
    }
    
    this.setCurrentP1Mask = function(aA, aB, aC, aD) {
        if (this.debugLockP1Mask==false) {
            this.myCurrentP1Mask[0]=aA;
            this.myCurrentP1Mask[1]=aB;
            this.myCurrentP1Mask[2]=aC;
            this.myCurrentP1Mask[3]=aD;
        }//end : is false
    }
    
    
    
    
    this.setCurrentM0Mask = function(aA, aB, aC, aD) {
        this.myCurrentM0Mask[0]=aA;
        this.myCurrentM0Mask[1]=aB;
        this.myCurrentM0Mask[2]=aC;
        this.myCurrentM0Mask[3]=aD;
        
        //myCurrentM0MaskIndex=getMasterIndexOMMT(aA, aB, aC, aD);
        this.M0Disabled=false;
    }
    
    
    this.setCurrentM0MaskDisabled = function() {
        this.M0Disabled=true;
    }
    
    
    
    
    this.getCurrentBLMask = function(aIndex) {
        return BALL_MASK_TABLE[this.myCurrentBLMask[0]][this.myCurrentBLMask[1]][this.myCurrentBLMask[2]+aIndex];
        
    }
    
    this.setCurrentBLMask = function(aA, aB, aC) {
        this.myCurrentBLMask[0]=aA;
        this.myCurrentBLMask[1]=aB;
        this.myCurrentBLMask[2]=aC;
        
    }
    
    
    this.getYStart = function()    {    return this.myConsole.getYStart();   }
    this.getDisplayHeight = function() { return this.myConsole.getDisplayHeight(); }
 
    
    this.isBitOn = function(aBitNumber, aValue) {
        return ((aValue & (1<<aBitNumber))!=0);
    }
    
    
    
   this.getAudio = function() { console.log("NO AUDIO!! in JSTIA"); 
	 return null; } 
    
    
    
    
    
    
    this.getCurrentClockCount = function() { return this.mySystem.getCycles() * CLOCKS_PER_CPU_CYCLE;    }
    this.getCurrentXPos = function() { return ((this.getCurrentClockCount() - this.myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL); }
    this.getCurrentScanline = function() { return ((this.getCurrentClockCount() - this.myClockWhenFrameStarted) / CLOCKS_PER_LINE_TOTAL); }
    
    
    
    
    
    
    
    this.getColor = function(aIndex) {
        switch (aIndex) {
            case 0 : return this.getCOLUBK();//=myColor[0];
            case 1 : return this.myTIAPokeRegister[COLUPF]; //=myColor[1];
            case 2 : return this.myTIAPokeRegister[COLUP0]; //=myColor[2];
            case 3 : return this.myTIAPokeRegister[COLUP1]; //=myColor[3];
            default : assert(false); return 0;
            
        }//end : switch
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.name = function() {
        return "TIA";
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.reset = function() {
				console.log("JS TIA RESET!");
        // Reset the sound device
        // dbg.out("RESETTING TIA");
        //this.getAudio().reset(); //The TIA is in charge of the audio, at least as far as system is concerned
        
        for (var i=0; i<this.myTIAPokeRegister.length; i++) {
            this.myTIAPokeRegister[i]=0;
        }//end : for i loop
        
        // Currently no objects are enabled
        this.myEnabledObjects = 0;
        
        // Some default values for the registers
//   
        this.myPlayfieldPriorityAndScore = 0;
    
      //  myPF = 0;
       // myGRP0 = 0;
       // myGRP1 = 0;
        this.myDGRP0 = 0;
        this.myDGRP1 = 0;
    
        this.myDENABL = false;
    
        //myRESMP0 = false;
        //this.myRESMP1 = false;
        this.myCollision = 0;
        this.myPOSP0 = 0;
        this.myPOSP1 = 0;
        this.myPOSM0 = 0;
        this.myPOSM1 = 0;
        this.myPOSBL = 0;
        
        // Some default values for the "current" variables
        this.myCurrentGRP0 = 0;
        this.myCurrentGRP1 = 0;
        this.setCurrentBLMask(0,0,0);// = ourBallMaskTable[0][0];
        
        this.setCurrentM0Mask(0,0,0,0);
        this.setCurrentM1Mask(0,0,0,0);
        this.setCurrentP0Mask(0,0,0,0); //ourPlayerMaskTable[0][0][0];
        this.setCurrentP1Mask(0,0,0,0);
        //myCurrentP1Mask = ourPlayerMaskTable[0][0][0];
        //myCurrentPFMask = PLAYFIELD_TABLE[0];
        
        this.myLastHMOVEClock = 0;
        this.myHMOVEBlankEnabled = false;
        this.myM0CosmicArkMotionEnabled = false;
        this.myM0CosmicArkCounter = 0;
        
     
        
        this.myDumpEnabled = false;
        this.myDumpDisabledCycle = 0;
        
        this.myAllowHMOVEBlanks = true;
        
        if((this.myConsole.getDisplayFormat()==DisplayFormat.PAL)||(this.myConsole.getDisplayFormat()==DisplayFormat.PAL60)) {
            this.myColorLossEnabled = true;
            this.myMaximumNumberOfScanlines = 342;
        } else  // NTSC
        {
            this.myColorLossEnabled = false;
            this.myMaximumNumberOfScanlines = 290;
        }
        
        this.myVBlankOff=0;
        this.myVBlankOn=0;
        this.myVSyncOn=-1;
        this.myDetectedYStart=0;
        this.myDetectedYStop=0;
        
        
        this.debugHasExecutionOverrun=false;
        
        // Recalculate the size of the display
        this.frameReset();
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Resets certain variables about the frame.
     * This is called by the TIA's reset method.  It is also called whenever the JSConsole
     * changes a relevant attribute about the frame (e.g. the DisplayHeight).
     */
    this.frameReset = function() {
        
        this.myConsole.getVideo().clearBuffers();   // Clear frame buffers
       
        this.myFramePointer = 0;    // Reset pixel pointer and drawing flag
        
        // Calculate color clock offsets for starting and stoping frame drawing
        //myStartDisplayOffset = CLOCKS_PER_LINE_TOTAL * getYStart();
       // myStopDisplayOffset = myStartDisplayOffset + (CLOCKS_PER_LINE_TOTAL * getDisplayHeight());
        
        // Reasonable values to start and stop the current frame drawing
        this.myClockWhenFrameStarted = this.mySystem.getCycles() * CLOCKS_PER_CPU_CYCLE; //now
        this.myClockStartDisplay = this.myClockWhenFrameStarted + (CLOCKS_PER_LINE_TOTAL * this.getYStart()); //what the clock will be when the visible part of the frame starts
        this.myClockStopDisplay = this.myClockWhenFrameStarted +  (CLOCKS_PER_LINE_TOTAL * (this.getYStart() + this.getDisplayHeight())); //when the visible part of the frame stops
        this.myClockAtLastUpdate = this.myClockWhenFrameStarted;  
        this.myClocksToEndOfScanLine = CLOCKS_PER_LINE_TOTAL;  //currently at beginning of a line
        this.myVSYNCFinishClock = 0x7FFFFFFF;
        this.myScanlineCountForLastFrame = 0;
        this.myCurrentScanline = 0; //currently on the first line
        
        this.myFrameXStart = 0;    // Hardcoded in preparation for new TIA class
        //this.myFrameWidth  = CLOCKS_PER_LINE_VISIBLE;  // Hardcoded in preparation for new TIA class
        
        
       
        
       
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * This is called by the JSSystem whenever it resets its cycle counter, which it 
     * is supposed to do every frame.  This method makes sure that all of the variables
     * in the TIA class (and those that it controls) compensate for this, mostly by
     * subtracting from all the relevant variables the same value that was subtracted
     * from the JSSystem's counter.
     */
    this.systemCyclesReset = function() {
        // Get the current system cycle
        var cycles = this.mySystem.getCycles();
        
        //If we reset the cycle number from x to zero (i.e. subtract x from cycle #), we should subtract the same number from the previousCycles variable in Audio.
        //This way, (currentCycles - previousCycles) will remain as it would have without the reset
        if (this.getAudio()!=null) this.getAudio().systemCyclesReset(cycles);   // Adjust the sound cycle counter
        
        
        this.myDumpDisabledCycle -= cycles;   // Adjust the dump cycle
        
        
        var clocks = cycles * CLOCKS_PER_CPU_CYCLE;    // Get the current color clock the system is using
        
        // Adjust the clocks by this amount since we're reseting the clock to zero
        this.myClockWhenFrameStarted -= clocks;
        this.myClockStartDisplay -= clocks;
        this.myClockStopDisplay -= clocks;
        this.myClockAtLastUpdate -= clocks;
        this.myVSYNCFinishClock -= clocks;
        this.myLastHMOVEClock -= clocks;
    }
    
    /**
     * This method existed originally for debugging.  Performs the obvious.
     * @param aIndex array index
     * @param aValue new value
     */
    this.setCurrentFrameBuffer = function(aIndex, aValue) {
        
        this.getCurrentFrameBuffer()[aIndex]=aValue;
        
    }
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.install = function(system) {
        // Remember which system I'm installed in
        this.mySystem = system;
        
        var shift = PAGE_SHIFT;
        this.mySystem.resetCycles();
        
        // All accesses are to this device
        var access=new PageAccess(this);
        access.setIndirectMode();
        
        // We're installing in a 2600 system
        for(var i = 0; i < 8192; i += (1 << shift)) {
            if((i & 0x1080) == 0x0000) {
                this.mySystem.setPageAccess((i >> shift) /*% 256*/, access);
            }
        }
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * This method is the one that causes the CPU to execute.  It should be run once per frame.
     */
    this.processFrame = function() {
        
     
        
        if (!this.myPartialFrameFlag) this.startFrame();
        this.myPartialFrameFlag=true;
        
        
        
        // Execute instructions until frame is finished, or a breakpoint/trap hits
        // 25000 is an arbitrary high number...this is called with the assumption that it will stop
        // before then (e.g. if the TIA is poked at VSYNC, depending on its state, it may halt the CPU,
        // thus completing the frame
        
        var zExecutions=0;
        var zInstructionsExecuted=0;
      //    long zTimeA=System.nanoTime();
        do{
            
            //A not very elegant work-around :
            //Some ROMs have a BRK instruction which terminates the execution before the frame is done...
            //this will keep calling executeCPU (up to 9 or 10 times) to see if it finishes the frame
            
           zInstructionsExecuted += this.mySystem.executeCPU(25000);
            zExecutions++;
            if (this.myPartialFrameFlag==false) break;
            
        }while (zExecutions<3);
        //todo : this execution do-while think may be unneeded-figure out
         if (zExecutions>=3) 
         {
            if (this.debugHasExecutionOverrun==false) console.log("debug: ********** Execution overrun in TIA *********");
            this.debugHasExecutionOverrun=true;
            //assert(false);  //california games sets this assertion off
         }//end : too many executions
        //  long zTimeB=System.nanoTime();  
        /* if (JSConsole.DEBUG_MODE_ON==true)
        {
            int zDeltaBA=(int)(zTimeB - zTimeA) / 1000;
             
            System.out.println("debug JSTIA execute CPU : " + zDeltaBA + " microsec");
        }//end : debug mode on
         */
          
       // if (JSConsole.DEBUG_MODE_ON==true) System.out.println("debug JSTIA - " + zInstructionsExecuted + " instructions executed");;
        var totalClocks = (this.mySystem.getCycles() * CLOCKS_PER_CPU_CYCLE) - this.myClockWhenFrameStarted;
        this.myCurrentScanline = totalClocks / CLOCKS_PER_LINE_TOTAL;
        
        if (!this.myPartialFrameFlag) this.endFrame();
       
        
    }
    
//----------
    
    /**
     * (A method that allowed me to type less when converting C++ to Java. It makes for
     * more readable code as well. -JLA)
     * @param aValue an integer
     * @return the boolean equivalent (in C++) of the integer
     */
    this.bool = function(aValue) {
        if (aValue==0) return false;
        else return true;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Called by update() at the start of a new frame.
     */
    this.startFrame = function() {
        // This stuff should only happen at the beginning of a new frame.
     
     
        this.myConsole.getVideo().swapFrameBuffers();
        // Remember the number of clocks which have passed on the current scanline
        // so that we can adjust the frame's starting clock by this amount.  This
        // is necessary since some games position objects during VSYNC and the
        // TIA's internal counters are not reset by VSYNC.
        var clocks = ((this.mySystem.getCycles() * CLOCKS_PER_CPU_CYCLE) - this.myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL;
        
        // Ask the system to reset the cycle count so it doesn't overflow
        this.mySystem.resetCycles();
        
        // Setup clocks that'll be used for drawing this frame
        this.myClockWhenFrameStarted = -1 * clocks;
        
        
        //this.myClockWhenFrameStarted=0;
        this.myClockStartDisplay = this.myClockWhenFrameStarted + (CLOCKS_PER_LINE_TOTAL * this.getYStart());
        this.myClockStopDisplay = this.myClockWhenFrameStarted + (CLOCKS_PER_LINE_TOTAL * (this.getYStart() + this.getDisplayHeight())); //myStopDisplayOffset;
        this.myClockAtLastUpdate = this.myClockStartDisplay;
        this.myClocksToEndOfScanLine = CLOCKS_PER_LINE_TOTAL;
        
        // Reset frame buffer pointer
        this.myFramePointer = 0;//myCurrentFrameBuffer;
        
        // If color loss is enabled then update the color registers based on
        // the number of scanlines in the last frame that was generated
        if(this.myColorLossEnabled) {
            if((this.myScanlineCountForLastFrame & 0x01)!=0) {
                this.myTIAPokeRegister[COLUP0] |= 0x01010101;
                this.myTIAPokeRegister[COLUP1] |= 0x01010101;
                this.myTIAPokeRegister[COLUPF] |= 0x01010101;
                this.myTIAPokeRegister[COLUBK] |= 0x01010101;
            } else {
                this.myTIAPokeRegister[COLUP0] &= 0xfefefefe;
                this.myTIAPokeRegister[COLUP1] &= 0xfefefefe;
                this.myTIAPokeRegister[COLUPF] &= 0xfefefefe;
                this.myTIAPokeRegister[COLUBK] &= 0xfefefefe;
            }
        }
        
        
        
      
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Called by update() at the end of a frame.
     */
    this.endFrame = function() {
        // This stuff should only happen at the end of a frame
        // Compute the number of scanlines in the frame
        this.myScanlineCountForLastFrame = this.myCurrentScanline;
        
    
    }
    
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.clocksThisLine = function() {
        // calculate the current scanline
        var totalClocks = (this.mySystem.getCycles() * CLOCKS_PER_CPU_CYCLE) - this.myClockWhenFrameStarted;
        return totalClocks % CLOCKS_PER_LINE_TOTAL;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    this.isPlayfieldPixelOn = function(aHPos)
    {
        var zPFBlock=aHPos / CLOCKS_PER_PLAYFIELD_BIT;
        if (zPFBlock >= 20)
        {
            if (this.isBitOn(0, this.myTIAPokeRegister[CTRLPF])==true) zPFBlock=39 - zPFBlock; //reflected
            else zPFBlock=zPFBlock - 20;
        }//end : right half of screen
        if (zPFBlock < 4) return this.isBitOn(4 + zPFBlock, this.myTIAPokeRegister[PF0]); //7 - zPFBlock, myTIAPokeRegister[PF0]);
        else if (zPFBlock < 12) return this.isBitOn(11 - zPFBlock, this.myTIAPokeRegister[PF1]);
        else return this.isBitOn(zPFBlock - 12, this.myTIAPokeRegister[PF2]); //(19 - zPFBlock, myTIAPokeRegister[PF2]);
    }
    
    this.isPlayer0PixelOn = function (aHPos) {   return ((this.myCurrentGRP0 & this.getCurrentP0Mask(aHPos))!=0);  }    
    this.isPlayer1PixelOn = function(aHPos) {   return ((this.myCurrentGRP1 & this.getCurrentP1Mask(aHPos))!=0);  }    
    this.isMissile0PixelOn = function(aHPos) {  return this.getCurrentM0Mask(aHPos);  }    
    this.isMissile1PixelOn = function(aHPos) {  return this.getCurrentM1Mask(aHPos);   }    
    this.isRESMP0 = function()  {    return ((this.myTIAPokeRegister[RESMP0] & BIT1) != 0);  }    
    this.isRESMP1 = function()  {    return ((this.myTIAPokeRegister[RESMP1] & BIT1) != 0);  }
            
    
    this.updatePlayfieldStatus = function()
    {
        if (((this.myTIAPokeRegister[PF0]&0xF0)==0)&&(this.myTIAPokeRegister[PF1]==0)&&(this.myTIAPokeRegister[PF2]==0)) this.myEnabledObjects &= ~BIT_PF;
        else this.myEnabledObjects |= BIT_PF;
         
        
    }
  
    
    this.memsetFrameBuffer = function(aIndex, aByteValue, aCount) {
        for (var i=0; i<aCount; i++) {   this.setCurrentFrameBuffer(aIndex+i,(aByteValue & 0xFF)); }//end : for i loop
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * This is the method that takes the values in the registers and uses them to 
     * create graphics (one horizontal line's worth) in JSVideo's FrameBuffer object.  
     * This is the very large version, which is slightly faster (although may not be 
     * significant on today's machines) than the smaller one (called updateFrameScanlineSimple at the moment.)
     * @param clocksToUpdate how many clocks to update
     * @param hpos current horizontal position
     */
    this.updateFrameScanline = function(clocksToUpdate, hpos) {
        // Calculate the zEnding frame pointer value
        var zEnding=this.myFramePointer + clocksToUpdate;
        
        
        // See if we're in the vertical blank region
        // if(bool(myVBLANK & 0x02)) {
        if (this.isBitOn(1, this.myTIAPokeRegister[VBLANK]))  {
            this.memsetFrameBuffer(this.myFramePointer, 0, clocksToUpdate);
        }
        // Handle all other possible combinations
        else {
            var zDebugSwitch=this.myEnabledObjects | this.myPlayfieldPriorityAndScore;
            if ((zDebugSwitch>=0)&&(zDebugSwitch<0x100)) this.debugRenderTypes[zDebugSwitch]=true;
            switch(this.myEnabledObjects | this.myPlayfieldPriorityAndScore) {
                // Background
                case 0x00:
                case 0x00 | BIT_SCORE:
                case 0x00 | BIT_PRIORITY:
                case 0x00 | BIT_PRIORITY | BIT_SCORE:
                {
                    this.memsetFrameBuffer(this.myFramePointer, this.myTIAPokeRegister[COLUBK], clocksToUpdate);
                    break;
                }
                
                // Playfield is enabled and the score bit is not set
                case BIT_PF:
                case BIT_PF | BIT_PRIORITY:
                {
                   // int mask = hpos; //this.myCurrentPFMask[hpos];
                    while (this.myFramePointer<zEnding) {
                       
                        this.setCurrentFrameBuffer(this.myFramePointer, this.isPlayfieldPixelOn(hpos) ? this.myTIAPokeRegister[COLUPF] : this.myTIAPokeRegister[COLUBK]);
                       
                        this.myFramePointer++;
                   
                        hpos++;
                    }//end : while loop
                    
                    break;
                }
                
                // Playfield is enabled and the score bit is set
                case BIT_PF | BIT_SCORE:
                case BIT_PF | BIT_SCORE | BIT_PRIORITY:
                {
                   // int mask = hpos;//&myCurrentPFMask[hpos];
                    while (this.myFramePointer<zEnding) {
                    
                        
                            this.setCurrentFrameBuffer(this.myFramePointer, this.isPlayfieldPixelOn(hpos) ?
                            (hpos < 80 ? this.myTIAPokeRegister[COLUP0] : this.myTIAPokeRegister[COLUP1]) : this.myTIAPokeRegister[COLUBK]);
                           
                        this.myFramePointer++;
                     
                        hpos++;
                    }//end : while loop
                    // Update a uInt8 at a time until reaching a int boundary
                    
                    break;
                }
                
                // Player 0 is enabled
                case BIT_P0:
                case BIT_P0 | BIT_SCORE:
                case BIT_P0 | BIT_PRIORITY:
                case BIT_P0 | BIT_SCORE | BIT_PRIORITY:
                {
                   // int mP0 = hpos;
                    
                    while(this.myFramePointer < zEnding) {
                        var zPlayer0Pixel=this.isPlayer0PixelOn(hpos);
                        
                         this.setCurrentFrameBuffer(this.myFramePointer, zPlayer0Pixel ? this.myTIAPokeRegister[COLUP0] : this.myTIAPokeRegister[COLUBK]);
                    
                         hpos++;
                        this.myFramePointer++;
                        
                    }
                    break;
                }
                
                // Player 1 is enabled
                case BIT_P1:
                case BIT_P1 | BIT_SCORE:
                case BIT_P1 | BIT_PRIORITY:
                case BIT_P1 | BIT_SCORE | BIT_PRIORITY:
                {
                 
                    
                    while(this.myFramePointer < zEnding) {
                       this.setCurrentFrameBuffer(this.myFramePointer, this.isPlayer1PixelOn(hpos) ? this.myTIAPokeRegister[COLUP1] : this.myTIAPokeRegister[COLUBK]);
                    
                       this.myFramePointer++;
                        hpos++;
                        
                    }
                    break;
                }
                
                // Player 0 and 1 are enabled
                case BIT_P0 | BIT_P1:
                case BIT_P0 | BIT_P1 | BIT_SCORE:
                case BIT_P0 | BIT_P1 | BIT_PRIORITY:
                case BIT_P0 | BIT_P1 | BIT_SCORE | BIT_PRIORITY:
                {
                  
                    
                    while(this.myFramePointer < zEnding) {
                        var zPlayer0Pixel=this.isPlayer0PixelOn(hpos);
                        var zPlayer1Pixel=this.isPlayer1PixelOn(hpos);
                        this.setCurrentFrameBuffer(this.myFramePointer, zPlayer0Pixel ?
                            this.myTIAPokeRegister[COLUP0] : (zPlayer1Pixel ? this.myTIAPokeRegister[COLUP1] : this.myTIAPokeRegister[COLUBK]));
                        
                        if(zPlayer0Pixel && zPlayer1Pixel) this.myCollision |= COLLISION_TABLE[BIT_P0 | BIT_P1];
                        
                       
                     
                        this.myFramePointer++;
                        hpos++;
                        
                        
                    }
                    break;
                }
                
                // Missile 0 is enabled
                case BIT_M0:
                case BIT_M0 | BIT_SCORE:
                case BIT_M0 | BIT_PRIORITY:
                case BIT_M0 | BIT_SCORE | BIT_PRIORITY:
                {
                    var mM0 = hpos;
                    
                    while(this.myFramePointer < zEnding) {
                        var zMMask=this.getCurrentM0Mask(mM0);
                        
                        this.setCurrentFrameBuffer(this.myFramePointer, zMMask ? this.myTIAPokeRegister[COLUP0] : this.myTIAPokeRegister[COLUBK]);
                        ++mM0; 
                        ++this.myFramePointer;
                          hpos++;
                        
                    }
                    break;
                }
                
                // Missile 1 is enabled
                case BIT_M1:
                case BIT_M1 | BIT_SCORE:
                case BIT_M1 | BIT_PRIORITY:
                case BIT_M1 | BIT_SCORE | BIT_PRIORITY:
                {
                    var mM1 = hpos;//this.myCurrentM1Mask[hpos];
                    
                    while(this.myFramePointer < zEnding) {
                        this.setCurrentFrameBuffer(this.myFramePointer, this.getCurrentM1Mask(mM1) ? this.myTIAPokeRegister[COLUP1] : this.myTIAPokeRegister[COLUBK]);
                        ++mM1;
                        ++this.myFramePointer;
                         hpos++; 
                        
                    }
                    break;
                }
                
                // Ball is enabled
                case BIT_BL:
                case BIT_BL | BIT_SCORE:
                case BIT_BL | BIT_PRIORITY:
                case BIT_BL | BIT_SCORE | BIT_PRIORITY:
                {
                    var mBL = hpos;// &this.myCurrentBLMask[hpos];
                    
                    while(this.myFramePointer < zEnding) {
                        this.setCurrentFrameBuffer(this.myFramePointer, this.getCurrentBLMask(mBL) ? this.myTIAPokeRegister[COLUPF] : this.myTIAPokeRegister[COLUBK]);
                        ++mBL; ++this.myFramePointer;
                          hpos++;
                        
                    }
                    break;
                }
                
                // Missile 0 and 1 are enabled
                case BIT_M0 | BIT_M1:
                case BIT_M0 | BIT_M1 | BIT_SCORE:
                case BIT_M0 | BIT_M1 | BIT_PRIORITY:
                case BIT_M0 | BIT_M1 | BIT_SCORE | BIT_PRIORITY:
                {
                    var mM0  = hpos;//this.myCurrentM0Mask[hpos];
                    var mM1  = hpos;//this.myCurrentM1Mask[hpos];
                    
                    while(this.myFramePointer < zEnding) {
                        this.setCurrentFrameBuffer(this.myFramePointer, this.getCurrentM0Mask(mM0) ? this.myTIAPokeRegister[COLUP0] : (this.getCurrentM1Mask(mM1) ? this.myTIAPokeRegister[COLUP1] : this.myTIAPokeRegister[COLUBK]));
                        
                        if(this.getCurrentM0Mask(mM0) && this.getCurrentM1Mask(mM1))
                            this.myCollision |= COLLISION_TABLE[BIT_M0 | BIT_M1];
                          hpos++;
                        ++mM0; ++mM1; ++this.myFramePointer;
                        
                    }
                    break;
                }
                
                // Ball and Missile 0 are enabled and playfield priority is not set
                case BIT_BL | BIT_M0:
                case BIT_BL | BIT_M0 | BIT_SCORE:
                {
                    var mBL = hpos;//this.myCurrentBLMask[hpos];
                    var mM0 = hpos;//this.myCurrentM0Mask[hpos];
                    
                    while(this.myFramePointer < zEnding) {
                        this.setCurrentFrameBuffer(this.myFramePointer, (this.getCurrentM0Mask(mM0) ? this.myTIAPokeRegister[COLUP0] : (this.getCurrentBLMask(mBL) ? this.myTIAPokeRegister[COLUPF] : this.myTIAPokeRegister[COLUBK])));
                        
                        if(this.getCurrentBLMask(mBL) && this.getCurrentM0Mask(mM0))
                            this.myCollision |= COLLISION_TABLE[BIT_BL | BIT_M0];
                        
                        ++mBL; ++mM0; ++this.myFramePointer;
                          hpos++;
                    }
                    break;
                }
                
                // Ball and Missile 0 are enabled and playfield priority is set
                case BIT_BL | BIT_M0 | BIT_PRIORITY:
                case BIT_BL | BIT_M0 | BIT_SCORE | BIT_PRIORITY:
                {
                    var mBL = hpos;//myCurrentBLMask[hpos];
                    var mM0 = hpos;//myCurrentM0Mask[hpos];
                    
                    while(this.myFramePointer < zEnding) {
                        this.setCurrentFrameBuffer(this.myFramePointer, (this.getCurrentBLMask(mBL) ? this.myTIAPokeRegister[COLUPF] : (this.getCurrentM0Mask(mM0) ? this.myTIAPokeRegister[COLUP0] : this.myTIAPokeRegister[COLUBK])));
                        
                        if(this.getCurrentBLMask(mBL) && this.getCurrentM0Mask(mM0))
                            this.myCollision |= COLLISION_TABLE[BIT_BL | BIT_M0];
                        
                        ++mBL; ++mM0; ++this.myFramePointer;
                          hpos++;
                    }
                    break;
                }
                
                // Ball and Missile 1 are enabled and playfield priority is not set
                case BIT_BL | BIT_M1:
                case BIT_BL | BIT_M1 | BIT_SCORE:
                {
                    var mBL = hpos;//this.myCurrentBLMask[hpos];
                    var mM1 = hpos;//this.myCurrentM1Mask[hpos];
                    
                    while(this.myFramePointer < zEnding) {
                        this.setCurrentFrameBuffer(this.myFramePointer, (this.getCurrentM1Mask(mM1) ? this.myTIAPokeRegister[COLUP1] : (this.getCurrentBLMask(mBL) ? this.myTIAPokeRegister[COLUPF] : this.myTIAPokeRegister[COLUBK])));
                        
                        if(this.getCurrentBLMask(mBL) && this.getCurrentM1Mask(mM1))
                            this.myCollision |= COLLISION_TABLE[BIT_BL | BIT_M1];
                        
                        ++mBL; ++mM1; ++this.myFramePointer;
                          hpos++;
                        
                    }
                    break;
                }
                
                // Ball and Missile 1 are enabled and playfield priority is set
                case BIT_BL | BIT_M1 | BIT_PRIORITY:
                case BIT_BL | BIT_M1 | BIT_SCORE | BIT_PRIORITY:
                {
                    var mBL = hpos;//this.myCurrentBLMask[hpos];
                    var mM1 = hpos;//this.myCurrentM1Mask[hpos];
                    
                    while(this.myFramePointer < zEnding) {
                        this.setCurrentFrameBuffer(this.myFramePointer, (this.getCurrentBLMask(mBL) ? this.myTIAPokeRegister[COLUPF] : (this.getCurrentM1Mask(mM1) ? this.myTIAPokeRegister[COLUP1] : this.myTIAPokeRegister[COLUBK])));
                        
                        if(this.getCurrentBLMask(mBL) && this.getCurrentM1Mask(mM1))
                            this.myCollision |= COLLISION_TABLE[BIT_BL | BIT_M1];
                        
                        ++mBL; ++mM1; ++this.myFramePointer;
                          hpos++;
                    }
                    break;
                }
                
                // Ball and Player 1 are enabled and playfield priority is not set
                case BIT_BL | BIT_P1:
                case BIT_BL | BIT_P1 | BIT_SCORE:
                {
                   var mBL = hpos;//this.myCurrentBLMask[hpos];
                   // int mP1 = hpos;//this.myCurrentP1Mask[hpos];
                    
                    while(this.myFramePointer < zEnding) {
                        var zPlayer1Pixel=this.isPlayer1PixelOn(hpos);
                        this.setCurrentFrameBuffer(this.myFramePointer, zPlayer1Pixel ? this.myTIAPokeRegister[COLUP1] :
                            (this.getCurrentBLMask(mBL) ? this.myTIAPokeRegister[COLUPF] : this.myTIAPokeRegister[COLUBK]));
                        
                        if(this.getCurrentBLMask(mBL) && zPlayer1Pixel)
                            this.myCollision |= COLLISION_TABLE[BIT_BL | BIT_P1];
                        
                        ++mBL; 
                        //++mP1; 
                        ++this.myFramePointer;
                          hpos++;
                        
                    }
                    break;
                }
                
                // Ball and Player 1 are enabled and playfield priority is set
                case BIT_BL | BIT_P1 | BIT_PRIORITY:
                case BIT_BL | BIT_P1 | BIT_PRIORITY | BIT_SCORE:
                {
                    var mBL = hpos;//this.myCurrentBLMask[hpos];
                 
                    
                    while(this.myFramePointer < zEnding) {
                        var zPlayer1Pixel=this.isPlayer1PixelOn(hpos);
                        this.setCurrentFrameBuffer(this.myFramePointer, this.getCurrentBLMask(mBL) ? this.myTIAPokeRegister[COLUPF] :
                            (zPlayer1Pixel ? this.myTIAPokeRegister[COLUP1] : this.myTIAPokeRegister[COLUBK]));
                        
                        if(this.getCurrentBLMask(mBL) && zPlayer1Pixel)
                            this.myCollision |= COLLISION_TABLE[BIT_BL | BIT_P1];
                        
                        ++mBL; 
                        //++mP1; 
                        ++this.myFramePointer;
                          hpos++;
                    }
                    break;
                }
                
                // Playfield and Player 0 are enabled and playfield priority is not set
                case BIT_PF | BIT_P0:
                {
                   // int mPF = hpos;// &this.myCurrentPFMask[hpos];
                   // int mP0 = hpos;//this.myCurrentP0Mask[hpos];
                    
                    while(this.myFramePointer < zEnding) {
                    
                         var zPlayfieldIsOn=this.isPlayfieldPixelOn(hpos);
                         var zPlayer0Pixel=this.isPlayer0PixelOn(hpos);
                         this.setCurrentFrameBuffer(this.myFramePointer, zPlayer0Pixel ?
                            this.myTIAPokeRegister[COLUP0] : (zPlayfieldIsOn ? this.myTIAPokeRegister[COLUPF] : this.myTIAPokeRegister[COLUBK]));
                        
                        if(zPlayfieldIsOn && zPlayer0Pixel)
                            this.myCollision |= COLLISION_TABLE[BIT_PF | BIT_P0];
                        
                        hpos++;
                        
                       
                        ++this.myFramePointer;
                        
                    }
                    
                    break;
                }
                
                // Playfield and Player 0 are enabled and playfield priority is set
                case BIT_PF | BIT_P0 | BIT_PRIORITY:
                {
                  //  int mPF = hpos; // &this.myCurrentPFMask[hpos];
                    //int mP0 = hpos;//this.myCurrentP0Mask[hpos];
                    
                    while(this.myFramePointer < zEnding) {
                      
                        var zPlayfieldIsOn=this.isPlayfieldPixelOn(hpos);
                        var zPlayer0Pixel=this.isPlayer0PixelOn(hpos);
                            this.setCurrentFrameBuffer(this.myFramePointer, zPlayfieldIsOn ? this.myTIAPokeRegister[COLUPF] :
                            (zPlayer0Pixel ? this.myTIAPokeRegister[COLUP0] : this.myTIAPokeRegister[COLUBK]));
                        if(zPlayfieldIsOn && zPlayer0Pixel)
                            this.myCollision |= COLLISION_TABLE[BIT_PF | BIT_P0];
                        
                        hpos++;
                      
                        ++this.myFramePointer;
                        
                        
                        
                    }
                    
                    break;
                }
                
                // Playfield and Player 1 are enabled and playfield priority is not set
                case BIT_PF | BIT_P1:
                {
                 
                    
                    while(this.myFramePointer < zEnding) {
                           var zPlayfieldIsOn=this.isPlayfieldPixelOn(hpos);
                           var zPlayer1Pixel=this.isPlayer1PixelOn(hpos);
                     
                          this.setCurrentFrameBuffer(this.myFramePointer, zPlayer1Pixel ?
                            this.myTIAPokeRegister[COLUP1] : (zPlayfieldIsOn ? this.myTIAPokeRegister[COLUPF] : this.myTIAPokeRegister[COLUBK]));
                        
                        if(zPlayfieldIsOn && zPlayer1Pixel)
                            this.myCollision |= COLLISION_TABLE[BIT_PF | BIT_P1];  
                           
                        hpos++;
                         //++mP1; 
                         ++this.myFramePointer;
                        
                    }
                    
                    break;
                }
                
                // Playfield and Player 1 are enabled and playfield priority is set
                case BIT_PF | BIT_P1 | BIT_PRIORITY:
                {
                   
                    
                    while(this.myFramePointer < zEnding) {
                            var zPlayfieldIsOn=this.isPlayfieldPixelOn(hpos);
                            var zPlayer1Pixel=this.isPlayer1PixelOn(hpos);
                       
                          this.setCurrentFrameBuffer(this.myFramePointer, zPlayfieldIsOn ? this.myTIAPokeRegister[COLUPF] :
                            (zPlayer1Pixel ? this.myTIAPokeRegister[COLUP1] : this.myTIAPokeRegister[COLUBK]));
                        if(zPlayfieldIsOn && zPlayer1Pixel)
                            this.myCollision |= COLLISION_TABLE[BIT_PF | BIT_P1];   
                            
                        hpos++;    
                         //++mP1; 
                         ++this.myFramePointer;
                        
                        
                    }
                    
                    break;
                }
                
                // Playfield and Ball are enabled
                case BIT_PF | BIT_BL:
                case BIT_PF | BIT_BL | BIT_PRIORITY:
                {
                 
                    var mBL = hpos;//this.myCurrentBLMask[hpos];
                    
                    while(this.myFramePointer < zEnding) {
                        var zPlayfieldIsOn=this.isPlayfieldPixelOn(hpos);
                    
                         this.setCurrentFrameBuffer(this.myFramePointer, (zPlayfieldIsOn || this.getCurrentBLMask(mBL)) ? this.myTIAPokeRegister[COLUPF] : this.myTIAPokeRegister[COLUBK]);
                        
                        if(zPlayfieldIsOn && this.getCurrentBLMask(mBL))
                            this.myCollision |= COLLISION_TABLE[BIT_PF | BIT_BL];
                        
                        hpos++;
                        ++mBL; ++this.myFramePointer;
                        
                    }
                    break;
                }
                
                // Handle all of the other cases
                default:
                {
                    for(; this.myFramePointer < zEnding; ++this.myFramePointer, ++hpos) {
                        var enabled = this.isPlayfieldPixelOn(hpos) ? BIT_PF : 0;            //bool(this.myPF  & this.myCurrentPFMask[hpos]) ? BIT_PF : 0;
                        
                        if(this.bool(this.myEnabledObjects & BIT_BL) && this.getCurrentBLMask(hpos))    enabled |= BIT_BL;
                        if (this.isPlayer1PixelOn(hpos))                 enabled |= BIT_P1;
                        if(this.bool(this.myEnabledObjects & BIT_M1) && this.getCurrentM1Mask(hpos))    enabled |= BIT_M1;
                        if (this.isPlayer0PixelOn(hpos))                 enabled |= BIT_P0;
                        if(this.bool(this.myEnabledObjects & BIT_M0) && this.getCurrentM0Mask(hpos))    enabled |= BIT_M0;
                        
                        this.myCollision |= COLLISION_TABLE[enabled];
                        this.setCurrentFrameBuffer(this.myFramePointer, this.getColor(PRIORITY_ENCODER[hpos < 80 ? 0 : 1][enabled | this.myPlayfieldPriorityAndScore]));
                    }//end : for loop
                    break;
                }//end : default case
            }//end : switch
        }
        this.myFramePointer = zEnding;
    }
    
    /**
     * This is a trimmed-up version of updateFrameScanline.  It is
     * much smaller, but takes a little more time to execute.  
     * This method perhaps should replace the 
     * updateFrameScanline method, for simplicity reasons.
     * (It needs to be worked on--some games (e.g. med. mayhem) shake with this method.
     * @param aClocksToUpdate How many clocks to update
     * @param aHPos Current horizontal position
     */
    this.updateFrameScanlineSimple = function(aClocksToUpdate, aHPos)
    {
        var zEnding=this.myFramePointer + aClocksToUpdate;       
        
        // See if we're in the vertical blank region
        // if(bool(this.myVBLANK & 0x02)) {
        if (this.isBitOn(1, this.myTIAPokeRegister[VBLANK]))  {
            this.memsetFrameBuffer(this.myFramePointer, 0, aClocksToUpdate);
        }
       
        else {
        var zBallEnabled=((this.myEnabledObjects & BIT_BL)!=0);
        var zPlayfieldEnabled=((this.myEnabledObjects & BIT_PF)!=0);
        var zPlayer0Enabled=((this.myEnabledObjects & BIT_P0)!=0);
        var zPlayer1Enabled=((this.myEnabledObjects & BIT_P1)!=0);
        var zMissile0Enabled=((this.myEnabledObjects & BIT_M0)!=0);
        var zMissile1Enabled=((this.myEnabledObjects & BIT_M1)!=0);
         
             
        for(; this.myFramePointer < zEnding; ++this.myFramePointer, ++aHPos) {
                        var enabled = 0;
                        
                        if (zPlayfieldEnabled && this.isPlayfieldPixelOn(aHPos)) enabled |= BIT_PF; // : 0;            //bool(this.myPF  & this.myCurrentPFMask[aHPos]) ? BIT_PF : 0;
                        
                        if(zBallEnabled && this.getCurrentBLMask(aHPos))    enabled |= BIT_BL;
                        if (zPlayer1Enabled && this.isPlayer1PixelOn(aHPos))                 enabled |= BIT_P1;
                        if(zMissile1Enabled && this.getCurrentM1Mask(aHPos))    enabled |= BIT_M1;
                        if (zPlayer0Enabled && this.isPlayer0PixelOn(aHPos))                 enabled |= BIT_P0;
                        if(zMissile0Enabled && this.getCurrentM0Mask(aHPos))    enabled |= BIT_M0;
                        
                        this.myCollision |= COLLISION_TABLE[enabled];
                        this.setCurrentFrameBuffer(this.myFramePointer, this.getColor(PRIORITY_ENCODER[aHPos < 80 ? 0 : 1][enabled | this.myPlayfieldPriorityAndScore]));
                    }//end : for loop 
        
        }//end : vblank not on
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.updateFrame = function(clock) {
        
        // See if we're in the nondisplayable portion of the screen or if
        // we've already updated this portion of the screen
        if((clock < this.myClockStartDisplay) ||  (clock <= this.myClockAtLastUpdate)|| (this.myClockAtLastUpdate >= this.myClockStopDisplay) ) {
            return;
        }
        
        // Truncate the number of cycles to update to the stop display point
        if(clock > this.myClockStopDisplay)  clock = this.myClockStopDisplay;
        
        // Update frame one scanline at a time
        do //START OF LOOP
        {
            // Compute the number of clocks we're going to update
            var clocksToUpdate = 0;
            
            // Remember how many clocks we are from the left side of the screen
            var clocksFromStartOfScanLine = CLOCKS_PER_LINE_TOTAL - this.myClocksToEndOfScanLine;
            
            // See if we're updating more than the current scanline
            if(clock > (this.myClockAtLastUpdate + this.myClocksToEndOfScanLine)) {
                // Yes, we have more than one scanline to update so finish current one
                clocksToUpdate = this.myClocksToEndOfScanLine;
                this.myClocksToEndOfScanLine = CLOCKS_PER_LINE_TOTAL;
                this.myClockAtLastUpdate += clocksToUpdate;
            } else {
                // No, so do as much of the current scanline as possible
                clocksToUpdate = clock - this.myClockAtLastUpdate;
                this.myClocksToEndOfScanLine -= clocksToUpdate;
                this.myClockAtLastUpdate = clock;
            }
            
            var startOfScanLine = CLOCKS_PER_LINE_BLANK + this.myFrameXStart;
            
            // Skip over as many horizontal blank clocks as we can
            if(clocksFromStartOfScanLine < startOfScanLine) {
                var tmp;
                
                if((startOfScanLine - clocksFromStartOfScanLine) < clocksToUpdate)
                    tmp = startOfScanLine - clocksFromStartOfScanLine;
                else
                    tmp = clocksToUpdate;
                
                clocksFromStartOfScanLine += tmp;
                clocksToUpdate -= tmp;
            }
            
            // Remember frame pointer in case HMOVE blanks need to be handled
            var oldFramePointer = this.myFramePointer;
            
            // Update as much of the scanline as we can
            if(clocksToUpdate != 0) {
                this.updateFrameScanline(clocksToUpdate, clocksFromStartOfScanLine - CLOCKS_PER_LINE_BLANK);
               //updateFrameScanlineSimple(clocksToUpdate, clocksFromStartOfScanLine - CLOCKS_PER_LINE_BLANK);
            }
            
            // Handle HMOVE blanks if they are enabled
            if(this.myHMOVEBlankEnabled && (startOfScanLine < CLOCKS_PER_LINE_BLANK + 8) &&(clocksFromStartOfScanLine < (CLOCKS_PER_LINE_BLANK + 8))) {
                var blanks = (CLOCKS_PER_LINE_BLANK + 8) - clocksFromStartOfScanLine;
                this.memsetFrameBuffer(oldFramePointer, 0, blanks);
                if((clocksToUpdate + clocksFromStartOfScanLine) >= (CLOCKS_PER_LINE_BLANK + 8))  this.myHMOVEBlankEnabled = false;
            }
            
            // See if we're at the end of a scanline
            if(this.myClocksToEndOfScanLine == CLOCKS_PER_LINE_TOTAL) {
                this.myFramePointer -= (CLOCKS_PER_LINE_VISIBLE - this.myConsole.getDisplayWidth() - this.myFrameXStart);
                
                // Yes, so set PF mask based on current CTRLPF reflection state
               // this.myCurrentPFMask = PLAYFIELD_TABLE[this.myTIAPokeRegister[CTRLPF] & 0x01];
                //TODO : figure out what this did exactly
                
                // TODO: These should be reset right after the first copy of the player
                // has passed.  However, for now we'll just reset at the end of the
                // scanline since the other way would be too slow (01/21/99).
                
                this.setCurrentP0Mask(this.myPOSP0 & 0x03,0,this.myTIAPokeRegister[NUSIZ0] & 0x07,CLOCKS_PER_LINE_VISIBLE - (this.myPOSP0 & 0xFC));
                this.setCurrentP1Mask(this.myPOSP1 & 0x03,0,this.myTIAPokeRegister[NUSIZ1] & 0x07,CLOCKS_PER_LINE_VISIBLE - (this.myPOSP1 & 0xFC));
                
                
                // Handle the "Cosmic Ark" TIA bug if it's enabled
                if(this.myM0CosmicArkMotionEnabled) this.emulateCosmicBug();
                
                
            }//end: clocks to end of scanline == CLOCKS_PER_LINE_TOTAL
        }
        while(this.myClockAtLastUpdate < clock);
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.emulateCosmicBug = function() {
        // Movement table associated with the bug
        
        this.myM0CosmicArkCounter = (this.myM0CosmicArkCounter + 1) & 3;
        this.myPOSM0 -= COSMICBUG_MOVEMENT[this.myM0CosmicArkCounter];
        
        if(this.myPOSM0 >= CLOCKS_PER_LINE_VISIBLE)
            this.myPOSM0 -= CLOCKS_PER_LINE_VISIBLE;
        else if(this.myPOSM0 < 0)
            this.myPOSM0 += CLOCKS_PER_LINE_VISIBLE;
        
        if(this.myM0CosmicArkCounter == 1) {
            // Stretch this missile so it's at least 2 pixels wide
            
            this.setCurrentM0Mask(this.myPOSM0 & 0x03, this.myTIAPokeRegister[NUSIZ0] & 0x07, ((this.myTIAPokeRegister[NUSIZ0] & 0x30) >> 4) | 0x01, CLOCKS_PER_LINE_VISIBLE - (this.myPOSM0 & 0xFC));
        } else if(this.myM0CosmicArkCounter == 2) {
            // Missile is disabled on this line
            
            this.setCurrentM0MaskDisabled();
        } else {
            
            this.setCurrentM0Mask(this.myPOSM0 & 0x03, this.myTIAPokeRegister[NUSIZ0] & 0x07, ((this.myTIAPokeRegister[NUSIZ0] & 0x30) >> 4), CLOCKS_PER_LINE_VISIBLE - (this.myPOSM0 & 0xFC));
            
        }
    }
    
    
    
    this.waitHorizontalSync = function() {
        var cyclesToEndOfLine = 76 - ((this.mySystem.getCycles() -
                (this.myClockWhenFrameStarted / CLOCKS_PER_CPU_CYCLE)) % 76);
        
        if(cyclesToEndOfLine < 76) {
            this.mySystem.incrementCycles(cyclesToEndOfLine);
        }
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 /*private void greyOutFrame() {
        int c = scanlines();
        if(c < this.myYStart) c = this.myYStart;
  
        for( int s = c; s < (this.myHeight + this.myYStart); s++)
            for( int i = 0; i < 160; i++) {
            int tmp = this.myCurrentFrameBuffer[ (s - this.myYStart) * 160 + i] & 0x0f;
            tmp >>= 1;
            setCurrentFrameBuffer((s - this.myYStart) * 160 + i, tmp);
            }
        System.out.println("debug: FRAME GREY : " + System.currentTimeMillis());
  
    }
  */
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   /* protected void clearBuffers() {
        for(int i = 0; i < CLOCKS_PER_LINE_VISIBLE * 300; ++i) {
            setCurrentFrameBuffer(i,  0);
            this.myPreviousFrameBuffer[i] = 0;
        }
        //  System.out.println("debug: CLEARING BUFFERS");
    }
    */
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Returns a value associated with the given address.
     * The peekable addresses of the TIA are those that involve either a collision
     * register or an external input (e.g. paddles).
     * @param addr Address to peek
     * @return Byte value associated with the address
     */
    this.peek = function(addr) {
        var zReturn=0;
        // Update frame to current color clock before we look at anything!
        // dbg.out("Peek - TIA:", (int)addr);
        assert(addr>=0);
        this.updateFrame(this.mySystem.getCycles() * CLOCKS_PER_CPU_CYCLE);
        
        var noise = this.mySystem.getDataBusState() & 0x3F;
        
        switch(addr & 0x000f) {
            case CXM0P://0x00:    // CXM0P
                zReturn=(this.bool(this.myCollision & 0x0001) ? 0x80 : 0x00) |
                        (this.bool(this.myCollision & 0x0002) ? 0x40 : 0x00) | noise; break;
                        
            case CXM1P: //0x01:    // CXM1P
                zReturn=(this.bool(this.myCollision & 0x0004) ? 0x80 : 0x00) |
                        (this.bool(this.myCollision & 0x0008) ? 0x40 : 0x00) | noise; break;
                        
            case CXP0FB: //0x02:    // CXP0FB
                zReturn=(this.bool(this.myCollision & 0x0010) ? 0x80 : 0x00) |
                        (this.bool(this.myCollision & 0x0020) ? 0x40 : 0x00) | noise; break;
                        
            case CXP1FB: //0x03:    // CXP1FB
                zReturn=(this.bool(this.myCollision & 0x0040) ? 0x80 : 0x00) |
                        (this.bool(this.myCollision & 0x0080) ? 0x40 : 0x00) | noise; break;
                        
            case CXM0FB: //0x04:    // CXM0FB
                zReturn=(this.bool(this.myCollision & 0x0100) ? 0x80 : 0x00) |
                        (this.bool(this.myCollision & 0x0200) ? 0x40 : 0x00) | noise; break;
                        
            case CXM1FB: // 0x05:    // CXM1FB
                zReturn=(this.bool(this.myCollision & 0x0400) ? 0x80 : 0x00) |
                        (this.bool(this.myCollision & 0x0800) ? 0x40 : 0x00) | noise; break;
                        
            case CXBLPF: //0x06:    // CXBLPF
                zReturn=(this.bool(this.myCollision & 0x1000) ? 0x80 : 0x00) | noise; break;
                
            case CXPPMM: //0x07:    // CXPPMM
                zReturn=(this.bool(this.myCollision & 0x2000) ? 0x80 : 0x00) |
                        (this.bool(this.myCollision & 0x4000) ? 0x40 : 0x00) | noise; break;
                        
            case INPT0: //0x08:    // INPT0
            {
                var r = this.myConsole.getController(Jack.LEFT).read(AnalogPin.Nine);
                if(r == RESISTANCE_MIN) {
                    zReturn=0x80 | noise; break;
                } else if((r == RESISTANCE_MAX) || this.myDumpEnabled) {
                    zReturn=noise; break;
                } else {
                    var t = (1.6 * r * 0.01E-6);
                    var needed = Math.floor(t * 1.19E6);
                    if(this.mySystem.getCycles() > (this.myDumpDisabledCycle + needed)) {
                        zReturn=0x80 | noise; break;
                    } else {
                        zReturn=noise; break;
                    }
                }
            }
            
            case INPT1: //0x09:    // INPT1
            {
                var r = this.myConsole.getController(Jack.LEFT).read(AnalogPin.Five);
                if(r == RESISTANCE_MIN) {
                    zReturn=0x80 | noise; break;
                } else if((r == RESISTANCE_MAX) || this.myDumpEnabled) {
                    zReturn=noise; break;
                } else {
                    var t = (1.6 * r * 0.01E-6);
                    var needed = Math.floor(t * 1.19E6);
                    if(this.mySystem.getCycles() > (this.myDumpDisabledCycle + needed)) {
                        zReturn=0x80 | noise; break;
                    } else {
                        zReturn=noise; break;
                    }
                }
            }
            
            case INPT2: //0x0A:    // INPT2
            {
                var r = this.myConsole.getController(Jack.RIGHT).read(AnalogPin.Nine);
                if(r == RESISTANCE_MIN) {
                    zReturn=0x80 | noise; break;
                } else if((r == RESISTANCE_MAX) || this.myDumpEnabled) {
                    zReturn=noise; break;
                } else {
                    var t = (1.6 * r * 0.01E-6);
                    var needed = Math.floor(t * 1.19E6);
                    if(this.mySystem.getCycles() > (this.myDumpDisabledCycle + needed)) {
                        zReturn=0x80 | noise; break;
                    } else {
                        zReturn=noise; break;
                    }
                }
            }
            
            case INPT3: //0x0B:    // INPT3
            {
                var r = this.myConsole.getController(Jack.RIGHT).read(AnalogPin.Five);
                if(r == RESISTANCE_MIN) {
                    zReturn=0x80 | noise; break;
                } else if((r == RESISTANCE_MAX) || this.myDumpEnabled) {
                    zReturn=noise; break;
                } else {
                    var t = (1.6 * r * 0.01E-6);
                    var needed = Math.floor(t * 1.19E6);
                    if(this.mySystem.getCycles() > (this.myDumpDisabledCycle + needed)) {
                        zReturn=0x80 | noise; break;
                    } else {
                        zReturn=noise; break;
                    }
                }
            }
            
            case INPT4: //0x0C:    // INPT4
                zReturn=this.myConsole.getController(Jack.LEFT).read(DigitalPin.Six) ?
                    (0x80 | noise) : noise; break;
                    
            case INPT5: //0x0D:    // INPT5
                zReturn=this.myConsole.getController(Jack.RIGHT).read(DigitalPin.Six) ?
                    (0x80 | noise) : noise; break;
                    
            case 0x0E:
                zReturn=noise; break;
                
            default:
                zReturn=noise;
            
                break;
        }
        assert((zReturn>=0)&&(zReturn<0x100));
        return zReturn;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Writes a byte to the TIA chip.
     * Depending on the address supplied, either a byte is stored in a TIA register or
     * the TIA performs some function (i.e. STROBE registers), in this case ignoring the byte.
     * This TIA emulator class will outsource any poke of an audio register to the
     * JSAudio object of the console.
     * <p>
     * Note: the TIA will only look at the 6 lowest bits in the address supplied.  This
     * 6 bit number corresponds with the values found in the TIA guide found on the
     * Internet.
     * @param aAddress address to poke
     * @param aByteValue the byte to write to the TIA chip
     */
    this.poke = function (aAddress, aByteValue) {
        assert((aByteValue>=0)&&(aByteValue<0x100));
        
        var addr = aAddress & 0x003f;
        
        var clock = this.getCurrentClockCount(); //this.mySystem.getCycles() * 3;
        var delay = POKE_DELAY_TABLE[addr];
        
        // See if this is a poke to a PF register
        if(delay == -1) {
            var d = [4, 5, 2, 3];
            var x = this.getCurrentXPos();//((clock - this.myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL);
            delay = (d[(x / 3) & 3]);
        }
        
        // Update frame to current CPU cycle before we make any changes!
        this.updateFrame(clock + delay);
        
        // If a VSYNC hasn't been generated in time go ahead and end the frame
        if(this.getCurrentScanline() > this.myMaximumNumberOfScanlines) {
            this.mySystem.stopCPU();//.stop();
            this.myPartialFrameFlag = false;
        }
        
                
       
        
    
      if (addr < TIA_POKE_REGISTER_COUNT) // is the address is a pokable TIA register?
      {
       
        var zPreviousValue=this.myTIAPokeRegister[addr]; //remember what the previous value was, just in case someone below wants to know
       //ASDF 
        this.myTIAPokeRegister[addr]=aByteValue;  //SETS THE NEW VALUE!
             
        switch(addr) {
            case VSYNC:    // VSYNC (vertical sync set/clear)
            {
                // this.myVSYNC = aByteValue;
              	//console.log("Debug : VSYNC poked, value=" + aByteValue + ", scanlines()==" + this.scanlines()); 
                if (((aByteValue & BIT1) != 0) &&((zPreviousValue & BIT1)==0))
                {
                    
                    this.myVSyncOn=this.scanlines();
//                   	console.log("Debug : VSYNC ON, value=" + aByteValue + ", scanlines()==" + this.scanlines()); 
                }//end : turned VBlank ON
               
                
                
                if (this.bool(this.myTIAPokeRegister[VSYNC] & BIT1)) //is bit #1 on?
                {
                    // Indicate when VSYNC should be finished.  This should really
                    // be 3 * 228 according to Atari's documentation, however, some
                    // games don't supply the full 3 scanlines of VSYNC.
                    this.myVSYNCFinishClock = clock + CLOCKS_PER_LINE_TOTAL;
                } else if(!this.bool(this.myTIAPokeRegister[VSYNC] & BIT1) && (clock >= this.myVSYNCFinishClock)) {
                    // We're no longer interested in this.myVSYNCFinishClock
                    this.myVSYNCFinishClock = 0x7FFFFFFF;
                    
                    // Since we're finished with the frame tell the processor to halt
                    this.mySystem.stopCPU();
                    this.myPartialFrameFlag = false;
                }
                break;
            }
            
            case VBLANK:    // VBLANK (vertical blank set/clear)
            {
                // Is the dump to ground path being set for I0, I1, I2, and I3?
                if (((aByteValue & BIT1) != 0) &&((zPreviousValue & BIT1)==0)) //AUTO DETECT FRAME HEIGHT
                {
                    //TODO : have this done only when in detection mode
                    this.myVBlankOn=this.scanlines();
                    
                    var zHeight=this.myVBlankOn - this.myVBlankOff;
                    if (zHeight < 0) zHeight += this.myVSyncOn;
                    if (zHeight >= FRAME_Y_MIN)
                    {
                        this.myDetectedYStart=this.myVBlankOff;
                        if (this.myDetectedYStart>=this.myVSyncOn) this.myDetectedYStart -= this.myVSyncOn;
                        this.myDetectedYStop=this.myDetectedYStart + zHeight;
												//console.log('YStart 대입' + this.myVBlankOff);
                    }
  //                	console.log("Debug : VBLANK ON, value=" + aByteValue + ", scanlines()==" + this.scanlines()); 
                }//end : turned VBlank ON
                else if (((aByteValue & BIT1) == 0) && ((zPreviousValue & BIT1)!=0)) 
                {
                    this.myVBlankOff=this.scanlines();
                    
    //              console.log("Debug : VBLANK OFF, value=" + aByteValue + ", scanlines()==" + this.scanlines()); 
                }//end : turned VBlank OFF
                
                
                if      ((this.bool(zPreviousValue & BIT7)==false) && (bool(this.myTIAPokeRegister[VBLANK] & BIT7)==true))  this.myDumpEnabled=true;            
                else if ((this.bool(zPreviousValue & BIT7)==true)  && (bool(this.myTIAPokeRegister[VBLANK] & BIT7)==false)) {
                    this.myDumpEnabled=false;
                    this.myDumpDisabledCycle = this.mySystem.getCycles();
                }          
                
             
                break;
            }
            
            case WSYNC:    // WSYNC : Wait for leading edge of HBLANK
            {
                // It appears that the 6507 only halts during a read cycle so
                // we test here for follow-on writes which should be ignored as
                // far as halting the processor is concerned.
                //
                // TODO - 08-30-2006: This halting isn't correct since it's
                // still halting on the original write.  The 6507 emulation
                // should be expanded to include a READY line.
                if(this.mySystem.getCPU().lastAccessWasRead()) {
                    // Tell the cpu to waste the necessary amount of time
                    this.waitHorizontalSync();
                }
                break;
            }
            
            case RSYNC:    // Reset horizontal sync counter
            {   //Not really supposed to be poked, except for during the initial memory clearing? - JLA
                 break;
            }
            
            case NUSIZ0:    // Number-size of player-missile 0
            {
                             
                // TODO: Technically the "enable" part, [0], should depend on the current
                // enabled or disabled state.  This mean we probably need a data member
                // to maintain that state (01/21/99).                
                this.setCurrentP0Mask(this.myPOSP0 & 0x03, 0, this.myTIAPokeRegister[NUSIZ0] & 0x07, CLOCKS_PER_LINE_VISIBLE - (this.myPOSP0 & 0xFC));
                this.setCurrentM0Mask(this.myPOSM0 & 0x03, this.myTIAPokeRegister[NUSIZ0] & 0x07, ((this.myTIAPokeRegister[NUSIZ0] & 0x30) >> 4) , CLOCKS_PER_LINE_VISIBLE - (this.myPOSM0 & 0xFC));
                                
                break;
            }
            
            case NUSIZ1:    // Number-size of player-missile 1
            {
               
                
                // TODO: Technically the "enable" part, [0], should depend on the current
                // enabled or disabled state.  This mean we probably need a data member
                // to maintain that state (01/21/99).
                
                this.setCurrentP1Mask(this.myPOSP1 & 0x03, 0, this.myTIAPokeRegister[NUSIZ1] & 0x07, CLOCKS_PER_LINE_VISIBLE - (this.myPOSP1 & 0xFC));                
                this.setCurrentM1Mask(this.myPOSM1 & 0x03, this.myTIAPokeRegister[NUSIZ1] & 0x07, ((this.myTIAPokeRegister[NUSIZ1] & 0x30) >> 4) , CLOCKS_PER_LINE_VISIBLE - (this.myPOSM1 & 0xFC));
                
                break;
            }
            
            case COLUP0:    // Color-Luminance Player 0
            case COLUP1:    // COLUM P1
            case COLUPF:    // COLUM PF
            case COLUBK:    // COLUM BK
            {
                var zColor = Math.floor(aByteValue & 0xfe);
                if(this.myColorLossEnabled && this.bool(this.myScanlineCountForLastFrame & BIT0))  zColor |= BIT0;
                this.myTIAPokeRegister[addr]=zColor;
                break;
            }
            
             
            
            case 0x0A:    // Control Playfield, Ball size, Collisions
            {
                //this.myCTRLPF = aByteValue;
                
                // The playfield priority and score bits from the control register
                // are accessed when the frame is being drawn.  We precompute the
                // necessary aByteValue here so we can save time while drawing.
                this.myPlayfieldPriorityAndScore = ((this.myTIAPokeRegister[CTRLPF] & 0x06) << 5);
                
                // Update the playfield mask based on reflection state if
                // we're still on the left hand side of the playfield
                if(((clock - this.myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL) < (68 + 79)) {
                  //  this.myCurrentPFMask = PLAYFIELD_TABLE[this.myTIAPokeRegister[CTRLPF] & 0x01];
                    //TODO : figure out what this did exactly
                }
                
                this.setCurrentBLMask(this.myPOSBL & 0x03,(this.myTIAPokeRegister[CTRLPF] & 0x30) >> 4,CLOCKS_PER_LINE_VISIBLE - (this.myPOSBL & 0xFC));
                
                
                break;
            }
            
            case REFP0:    // Reflect Player 0
            {
                // See if the reflection state of the player is being changed
                if ((zPreviousValue & BIT3) != (aByteValue & BIT3)) this.myCurrentGRP0 = PLAYER_REFLECT_TABLE[this.myCurrentGRP0];
                break;
            }
            
            case REFP1:    // Reflect Player 1
            {
                // See if the reflection state of the player is being changed
                if((zPreviousValue & BIT3) != (aByteValue & BIT3))  this.myCurrentGRP1 = PLAYER_REFLECT_TABLE[this.myCurrentGRP1];
                
                break;
            }
            
            case PF0 :    // Playfield register byte 0
            {
              
                
                this.updatePlayfieldStatus();
             
                break;
            }
            
            case PF1:    // Playfield register byte 1
            {
              //  this.myPF = (this.myPF & 0x000FF00F) | ((int)aByteValue << 4);
                
                this.updatePlayfieldStatus();
          
                
                break;
            }
            
            case PF2 :    // Playfield register byte 2
            {
               // this.myPF = (this.myPF & 0x00000FFF) | ((int)aByteValue << 12);
                
                this.updatePlayfieldStatus();
              
                
                break;
            }
            
            case RESP0:    // Reset Player 0
            {
                var hpos = (clock - this.myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL;
                var newx = hpos < CLOCKS_PER_LINE_BLANK ? 3 : (((hpos - CLOCKS_PER_LINE_BLANK) + 5) % CLOCKS_PER_LINE_VISIBLE);
                
                // Find out under what condition the player is being reset
                var when = PLAYER_POSITION_RESET_WHEN_TABLE[this.myTIAPokeRegister[NUSIZ0] & 7][this.myPOSP0][newx];
                
                // Player is being reset during the display of one of its copies
                if(when == 1) {
                    // So we go ahead and update the display before moving the player
                    // TODO: The 11 should depend on how much of the player has already
                    // been displayed.  Probably change table to return the amount to
                    // delay by instead of just 1 (01/21/99).
                    this.updateFrame(clock + 11);
                    
                    this.myPOSP0 = newx;
                    
                    // Setup the mask to skip the first copy of the player
                    
                    this.setCurrentP0Mask(this.myPOSP0 & 0x03,1,this.myTIAPokeRegister[NUSIZ0] & 0x07,CLOCKS_PER_LINE_VISIBLE - (this.myPOSP0 & 0xFC));
                }
                // Player is being reset in neither the delay nor display section
                else if(when == 0) {
                    this.myPOSP0 = newx;
                    
                    // So we setup the mask to skip the first copy of the player
                    this.setCurrentP0Mask(this.myPOSP0 & 0x03,1,this.myTIAPokeRegister[NUSIZ0] & 0x07,CLOCKS_PER_LINE_VISIBLE - (this.myPOSP0 & 0xFC));
                    
                }
                // Player is being reset during the delay section of one of its copies
                else if(when == -1) {
                    this.myPOSP0 = newx;
                    
                    // So we setup the mask to display all copies of the player
                    this.setCurrentP0Mask(this.myPOSP0 & 0x03,0,this.myTIAPokeRegister[NUSIZ0] & 0x07,CLOCKS_PER_LINE_VISIBLE - (this.myPOSP0 & 0xFC));
                    
                }
                break;
            }
            
            case RESP1:    // Reset Player 1
            {
                var hpos = (clock - this.myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL;
                var newx = hpos < CLOCKS_PER_LINE_BLANK ? 3 : (((hpos - CLOCKS_PER_LINE_BLANK) + 5) % CLOCKS_PER_LINE_VISIBLE);
                
                // Find out under what condition the player is being reset
                var when = PLAYER_POSITION_RESET_WHEN_TABLE[this.myTIAPokeRegister[NUSIZ1] & 7][this.myPOSP1][newx];
                
                // Player is being reset during the display of one of its copies
                if(when == 1) {
                    // So we go ahead and update the display before moving the player
                    // TODO: The 11 should depend on how much of the player has already
                    // been displayed.  Probably change table to return the amount to
                    // delay by instead of just 1 (01/21/99).
                    this.updateFrame(clock + 11);
                    
                    this.myPOSP1 = newx;
                    
                    // Setup the mask to skip the first copy of the player
                    
                    this.setCurrentP1Mask(this.myPOSP1 & 0x03, 1, this.myTIAPokeRegister[NUSIZ1] & 0x07, CLOCKS_PER_LINE_VISIBLE - (this.myPOSP1 & 0xFC));
                }
                // Player is being reset in neither the delay nor display section
                else if(when == 0) {
                    this.myPOSP1 = newx;
                    
                    // So we setup the mask to skip the first copy of the player
                    this.setCurrentP1Mask(this.myPOSP1 & 0x03, 1, this.myTIAPokeRegister[NUSIZ1] & 0x07, CLOCKS_PER_LINE_VISIBLE - (this.myPOSP1 & 0xFC));
                    
                }
                // Player is being reset during the delay section of one of its copies
                else if(when == -1) {
                    this.myPOSP1 = newx;
                    
                    // So we setup the mask to display all copies of the player
                    this.setCurrentP1Mask(this.myPOSP1 & 0x03, 0, this.myTIAPokeRegister[NUSIZ1] & 0x07, CLOCKS_PER_LINE_VISIBLE - (this.myPOSP1 & 0xFC));
                    
                }
                break;
            }
            
            case RESM0:    // Reset Missile 0
            {
                var hpos = (clock - this.myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL;
                this.myPOSM0 = hpos < CLOCKS_PER_LINE_BLANK ? 2 : (((hpos - CLOCKS_PER_LINE_BLANK) + 4) % CLOCKS_PER_LINE_VISIBLE);
                
                // TODO: Remove the following special hack for Dolphin by
                // figuring out what really happens when Reset Missile
                // occurs 20 cycles after an HMOVE (04/13/02).
                if(((clock - this.myLastHMOVEClock) == (20 * 3)) && (hpos == 69)) {
                    this.myPOSM0 = 8;
                }
                this.setCurrentM0Mask(this.myPOSM0 & 0x03, this.myTIAPokeRegister[NUSIZ0] & 0x07, ((this.myTIAPokeRegister[NUSIZ0] & 0x30) >> 4), CLOCKS_PER_LINE_VISIBLE - (this.myPOSM0 & 0xFC));
                break;
            }
            
            case RESM1:    // Reset Missile 1
            {
                var hpos = (clock - this.myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL;
                this.myPOSM1 = hpos < CLOCKS_PER_LINE_BLANK ? 2 : (((hpos - CLOCKS_PER_LINE_BLANK) + 4) % CLOCKS_PER_LINE_VISIBLE);
                
                // TODO: Remove the following special hack for Pitfall II by
                // figuring out what really happens when Reset Missile
                // occurs 3 cycles after an HMOVE (04/13/02).
                if(((clock - this.myLastHMOVEClock) == (3 * 3)) && (hpos == 18)) {
                    this.myPOSM1 = 3;
                }
                this.setCurrentM1Mask(this.myPOSM1 & 0x03, this.myTIAPokeRegister[NUSIZ1] & 0x07, ((this.myTIAPokeRegister[NUSIZ1] & 0x30) >> 4) , CLOCKS_PER_LINE_VISIBLE - (this.myPOSM1 & 0xFC));
                
                
                break;
            }
            
            case RESBL:    // Reset Ball
            {
                var hpos = (clock - this.myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL ;
                this.myPOSBL = hpos < CLOCKS_PER_LINE_BLANK ? 2 : (((hpos - CLOCKS_PER_LINE_BLANK) + 4) % CLOCKS_PER_LINE_VISIBLE);
                
                // TODO: Remove the following special hack for Escape from the
                // Mindmaster by figuring out what really happens when Reset Ball
                // occurs 18 cycles after an HMOVE (01/09/99).
                if(((clock - this.myLastHMOVEClock) == (18 * 3)) &&
                        ((hpos == 60) || (hpos == 69))) {
                    this.myPOSBL = 10;
                }
                // TODO: Remove the following special hack for Decathlon by
                // figuring out what really happens when Reset Ball
                // occurs 3 cycles after an HMOVE (04/13/02).
                else if(((clock - this.myLastHMOVEClock) == (3 * 3)) && (hpos == 18)) {
                    this.myPOSBL = 3;
                }
                // TODO: Remove the following special hack for Robot Tank by
                // figuring out what really happens when Reset Ball
                // occurs 7 cycles after an HMOVE (04/13/02).
                else if(((clock - this.myLastHMOVEClock) == (7 * 3)) && (hpos == 30)) {
                    this.myPOSBL = 6;
                }
                // TODO: Remove the following special hack for Hole Hunter by
                // figuring out what really happens when Reset Ball
                // occurs 6 cycles after an HMOVE (04/13/02).
                else if(((clock - this.myLastHMOVEClock) == (6 * 3)) && (hpos == 27)) {
                    this.myPOSBL = 5;
                }
                this.setCurrentBLMask(this.myPOSBL & 0x03,(this.myTIAPokeRegister[CTRLPF] & 0x30) >> 4, CLOCKS_PER_LINE_VISIBLE - (this.myPOSBL & 0xFC));
                
                break;
            }
            
            // AUDIO REGISTER POKE
            case AUDC0:    // Audio control 0
            case AUDC1:    // Audio control 1
            case AUDF0:    // Audio frequency 0
            case AUDF1:    // Audio frequency 1
            case AUDV0:    // Audio volume 0
            case AUDV1:    // Audio volume 1
//                this.getAudio().pokeAudioRegister(addr, aByteValue, this.mySystem.getCycles()); //outsource to JSAudio
                break;
                
                
            case GRP0: //0x1B:    // Graphics Player 0
            {
                // Set player 0 graphics
               // this.myGRP0 = aByteValue; //(this.myBitEnabled[TIABitP0] ? aByteValue : 0);
                
                // Copy player 1 graphics into its delayed register
                this.myDGRP1 = this.myTIAPokeRegister[GRP1];
                
                // Get the "current" data for GRP0 base on delay register and reflect
                var grp0 = this.bool(this.myTIAPokeRegister[VDELP0] & BIT0) ? this.myDGRP0 : this.myTIAPokeRegister[GRP0];
                this.myCurrentGRP0 = this.bool(this.myTIAPokeRegister[REFP0] & BIT3) ? PLAYER_REFLECT_TABLE[grp0] : grp0;
                
                // Get the "current" data for GRP1 base on delay register and reflect
                var grp1 = this.bool(this.myTIAPokeRegister[VDELP1] & BIT0) ? this.myDGRP1 : this.myTIAPokeRegister[GRP1];
                this.myCurrentGRP1 = this.bool(this.myTIAPokeRegister[REFP1] & BIT3) ? PLAYER_REFLECT_TABLE[grp1] : grp1;
                
                // Set enabled object bits
                if(this.myCurrentGRP0 != 0)
                    this.myEnabledObjects |= BIT_P0;
                else
                    this.myEnabledObjects &= ~BIT_P0;
                
                if(this.myCurrentGRP1 != 0)
                    this.myEnabledObjects |= BIT_P1;
                else
                    this.myEnabledObjects &= ~BIT_P1;
                
                break;
            }
            
            case GRP1: //0x1C:    // Graphics Player 1
            {
                // Set player 1 graphics
               // this.myGRP1 = aByteValue; //(this.myBitEnabled[TIABitP1] ? aByteValue : 0);
                
                // Copy player 0 graphics into its delayed register
                this.myDGRP0 = this.myTIAPokeRegister[GRP0];
                
                // Copy ball graphics into its delayed register
                this.myDENABL = this.bool(this.myTIAPokeRegister[ENABL] & BIT1);
                
                // Get the "current" data for GRP0 base on delay register
                var grp0 = this.bool(this.myTIAPokeRegister[VDELP0] & BIT0) ? this.myDGRP0 : this.myTIAPokeRegister[GRP0];
                this.myCurrentGRP0 = this.bool(this.myTIAPokeRegister[REFP0] & BIT3) ? PLAYER_REFLECT_TABLE[grp0] : grp0;
                
                // Get the "current" data for GRP1 base on delay register
                var grp1 = this.bool(this.myTIAPokeRegister[VDELP1] & BIT0) ? this.myDGRP1 : this.myTIAPokeRegister[GRP1];
                this.myCurrentGRP1 = this.bool(this.myTIAPokeRegister[REFP1] & BIT3) ? PLAYER_REFLECT_TABLE[grp1] : grp1;
                
                // Set enabled object bits
                if(this.myCurrentGRP0 != 0)
                    this.myEnabledObjects |= BIT_P0;
                else
                    this.myEnabledObjects &= ~BIT_P0;
                
                if(this.myCurrentGRP1 != 0)
                    this.myEnabledObjects |= BIT_P1;
                else
                    this.myEnabledObjects &= ~BIT_P1;
                
                if(this.bool(this.myTIAPokeRegister[VDELBL] & BIT0) ? this.myDENABL : this.bool(this.myTIAPokeRegister[ENABL] & BIT1))
                    this.myEnabledObjects |= BIT_BL;
                else
                    this.myEnabledObjects &= ~BIT_BL;
                
                break;
            }
            
            case ENAM0:    // Enable Missile 0 graphics
            {
               // this.myENAM0 = bool(this.myBitEnabled[TIABitM0] ? aByteValue & 0x02 : 0);
             //   if (this.myBitEnabled[TIABitM0] == false) this.myTIAPokeRegister[ENAM0]=0;
                if(this.bool(this.myTIAPokeRegister[ENAM0] & BIT1) && !this.isRESMP0())
                    this.myEnabledObjects |= BIT_M0;
                else
                    this.myEnabledObjects &= ~BIT_M0;
                break;
            }
            
            case ENAM1:    // Enable Missile 1 graphics
            {
                //this.myENAM1 = bool(this.myBitEnabled[TIABitM1] ? aByteValue & 0x02 : 0);
               //  if (this.myBitEnabled[TIABitM1] == false) this.myTIAPokeRegister[ENAM1]=0;
                if(this.bool(this.myTIAPokeRegister[ENAM1] & BIT1) && !this.isRESMP1())
                    this.myEnabledObjects |= BIT_M1;
                else
                    this.myEnabledObjects &= ~BIT_M1;
                break;
            }
            
            case ENABL:    // Enable Ball graphics
            {
              //  this.myENABL = bool(this.myBitEnabled[TIABitBL] ? aByteValue & 0x02 : 0);
               //    if (this.myBitEnabled[TIABitBL] == false) this.myTIAPokeRegister[ENABL]=0;
                if(this.bool(this.myTIAPokeRegister[VDELBL] & BIT0) ? this.myDENABL : this.bool(this.myTIAPokeRegister[ENABL] & BIT1))
                    this.myEnabledObjects |= BIT_BL;
                else
                    this.myEnabledObjects &= ~BIT_BL;
                
                break;
            }
            
            case HMP0:    // Horizontal Motion Player 0
            {
                //  int zSignedVal=com.mauvila.mvunsigned.MvUnsignedUtil.toSignedByteValue(aByteValue)
               // this.myHMP0 = aByteValue >> 4;
                break;
            }
            
            case HMP1:    // Horizontal Motion Player 1
            {
               // this.myHMP1 = aByteValue >> 4;
                break;
            }
            
            case HMM0:    // Horizontal Motion Missile 0
            {
                var tmp = aByteValue >> 4;
                
                // Should we enabled TIA M0 "bug" used for stars in Cosmic Ark?
                if((clock == (this.myLastHMOVEClock + 21 * 3)) && ((zPreviousValue >> 4)== 7) && (tmp == 6)) {
                    this.myM0CosmicArkMotionEnabled = true;
                    this.myM0CosmicArkCounter = 0;
                }
                
               // this.myHMM0 = tmp;
                break;
            }
            
            case HMM1:    // Horizontal Motion Missile 1
            {
               // this.myHMM1 = aByteValue >> 4;
                break;
            }
            
            case HMBL:    // Horizontal Motion Ball
            {
              //  this.myHMBL = aByteValue >> 4;
                break;
            }
            
            case VDELP0:    // Vertial Delay Player 0
            {
                //this.myVDELP0 = bool(aByteValue & 0x01);
                
                var grp0 = this.bool(this.myTIAPokeRegister[VDELP0] & BIT0) ? this.myDGRP0 : this.myTIAPokeRegister[GRP0];
                this.myCurrentGRP0 = this.bool(this.myTIAPokeRegister[REFP0] & BIT3) ? PLAYER_REFLECT_TABLE[grp0] : grp0;
                
                if(this.myCurrentGRP0 != 0)
                    this.myEnabledObjects |= BIT_P0;
                else
                    this.myEnabledObjects &= ~BIT_P0;
                break;
            }
            
            case VDELP1:    // Vertial Delay Player 1
            {
               // this.myVDELP1 = bool(aByteValue & 0x01);
                var grp1 = this.bool(this.myTIAPokeRegister[VDELP1] & BIT0) ? this.myDGRP1 : this.myTIAPokeRegister[GRP1];
                this.myCurrentGRP1 = this.bool(this.myTIAPokeRegister[REFP1] & BIT3) ? PLAYER_REFLECT_TABLE[grp1] : grp1;
                if(this.myCurrentGRP1 != 0) this.myEnabledObjects |= BIT_P1;
                else this.myEnabledObjects &= ~BIT_P1;
                break;
            }
            
            case VDELBL:    // Vertial Delay Ball
            {
              //  this.myVDELBL = bool(aByteValue & 0x01);
                
                if(this.bool(this.myTIAPokeRegister[VDELBL] & BIT0) ? this.myDENABL : this.bool(this.myTIAPokeRegister[ENABL] & BIT1))
                    this.myEnabledObjects |= BIT_BL;
                else
                    this.myEnabledObjects &= ~BIT_BL;
                break;
            }
            
            case RESMP0 :    // Reset missile 0 to player 0
            {
                if(((zPreviousValue & BIT1)!=0) && !this.bool(aByteValue & 0x02)) {
                    var middle;
                    
                    if((this.myTIAPokeRegister[NUSIZ0] & 0x07) == 0x05)
                        middle = 8;
                    else if((this.myTIAPokeRegister[NUSIZ0] & 0x07) == 0x07)
                        middle = 16;
                    else
                        middle = 4;
                    
                    this.myPOSM0 = (this.myPOSP0 + middle) % CLOCKS_PER_LINE_VISIBLE;
                    this.setCurrentM0Mask(this.myPOSM0 & 0x03, this.myTIAPokeRegister[NUSIZ0] & 0x07, ((this.myTIAPokeRegister[NUSIZ0] & 0x30) >> 4), CLOCKS_PER_LINE_VISIBLE - (this.myPOSM0 & 0xFC));
                    
                    
                }
                
                //this.myRESMP0 = bool(aByteValue & 0x02);
                
                if(this.bool(this.myTIAPokeRegister[ENAM0] & BIT1) && !this.isRESMP0())
                    this.myEnabledObjects |= BIT_M0;
                else
                    this.myEnabledObjects &= ~BIT_M0;
                
                break;
            }
            
            case RESMP1:    // Reset missile 1 to player 1
            {
                if(((zPreviousValue & BIT1)!=0) && !this.bool(aByteValue & 0x02)) {
                    var middle;
                    
                    if((this.myTIAPokeRegister[NUSIZ1] & 0x07) == 0x05)
                        middle = 8;
                    else if((this.myTIAPokeRegister[NUSIZ1] & 0x07) == 0x07)
                        middle = 16;
                    else
                        middle = 4;
                    
                    this.myPOSM1 = (this.myPOSP1 + middle) % CLOCKS_PER_LINE_VISIBLE;
                    
                    this.setCurrentM1Mask(this.myPOSM1 & 0x03, this.myTIAPokeRegister[NUSIZ1] & 0x07, ((this.myTIAPokeRegister[NUSIZ1] & 0x30) >> 4) , CLOCKS_PER_LINE_VISIBLE - (this.myPOSM1 & 0xFC));
                    
                }
                
                //this.myRESMP1 = bool(aByteValue & 0x02);
                
                if(this.bool(this.myTIAPokeRegister[ENAM1] & BIT1) && !this.isRESMP1())
                    this.myEnabledObjects |= BIT_M1;
                else
                    this.myEnabledObjects &= ~BIT_M1;
                break;
            }
            
            case HMOVE: //0x2A:    // Apply horizontal motion HMOVE
            {
                // Figure out what cycle we're at
                var x = ((clock - this.myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL) / 3;
                
                // See if we need to enable the HMOVE blank bug
                if(this.myAllowHMOVEBlanks && HMOVE_BLANK_ENABLE_CYCLES[x]) {
                    // TODO: Allow this to be turned off using properties...
                    this.myHMOVEBlankEnabled = true;
                }
                
                this.myPOSP0 += COMPLETE_MOTION_TABLE[x][this.myTIAPokeRegister[HMP0]  >> 4];
                this.myPOSP1 += COMPLETE_MOTION_TABLE[x][this.myTIAPokeRegister[HMP1] >> 4];
                this.myPOSM0 += COMPLETE_MOTION_TABLE[x][this.myTIAPokeRegister[HMM0] >> 4];
                this.myPOSM1 += COMPLETE_MOTION_TABLE[x][this.myTIAPokeRegister[HMM1] >> 4];
                this.myPOSBL += COMPLETE_MOTION_TABLE[x][this.myTIAPokeRegister[HMBL] >> 4];
                
                if(this.myPOSP0 >= CLOCKS_PER_LINE_VISIBLE)
                    this.myPOSP0 -= CLOCKS_PER_LINE_VISIBLE;
                else if(this.myPOSP0 < 0)
                    this.myPOSP0 += CLOCKS_PER_LINE_VISIBLE;
                
                if(this.myPOSP1 >= CLOCKS_PER_LINE_VISIBLE)
                    this.myPOSP1 -= CLOCKS_PER_LINE_VISIBLE;
                else if(this.myPOSP1 < 0)
                    this.myPOSP1 += CLOCKS_PER_LINE_VISIBLE;
                
                if(this.myPOSM0 >= CLOCKS_PER_LINE_VISIBLE)
                    this.myPOSM0 -= CLOCKS_PER_LINE_VISIBLE;
                else if(this.myPOSM0 < 0)
                    this.myPOSM0 += CLOCKS_PER_LINE_VISIBLE;
                
                if(this.myPOSM1 >= CLOCKS_PER_LINE_VISIBLE)
                    this.myPOSM1 -= CLOCKS_PER_LINE_VISIBLE;
                else if(this.myPOSM1 < 0)
                    this.myPOSM1 += CLOCKS_PER_LINE_VISIBLE;
                
                if(this.myPOSBL >= CLOCKS_PER_LINE_VISIBLE)
                    this.myPOSBL -= CLOCKS_PER_LINE_VISIBLE;
                else if(this.myPOSBL < 0)
                    this.myPOSBL += CLOCKS_PER_LINE_VISIBLE;
                this.setCurrentBLMask(this.myPOSBL & 0x03,(this.myTIAPokeRegister[CTRLPF] & 0x30) >> 4,CLOCKS_PER_LINE_VISIBLE - (this.myPOSBL & 0xFC));
                
                this.setCurrentP0Mask(this.myPOSP0 & 0x03,0,this.myTIAPokeRegister[NUSIZ0] & 0x07,CLOCKS_PER_LINE_VISIBLE - (this.myPOSP0 & 0xFC));
                
                this.setCurrentP1Mask(this.myPOSP1 & 0x03, 0, this.myTIAPokeRegister[NUSIZ1] & 0x07, CLOCKS_PER_LINE_VISIBLE - (this.myPOSP1 & 0xFC));
                
                
                this.setCurrentM0Mask(this.myPOSM0 & 0x03, this.myTIAPokeRegister[NUSIZ0] & 0x07, ((this.myTIAPokeRegister[NUSIZ0] & 0x30) >> 4) /*| 0x01*/, CLOCKS_PER_LINE_VISIBLE - (this.myPOSM0 & 0xFC));
                this.setCurrentM1Mask(this.myPOSM1 & 0x03, this.myTIAPokeRegister[NUSIZ1] & 0x07, ((this.myTIAPokeRegister[NUSIZ1] & 0x30) >> 4) , CLOCKS_PER_LINE_VISIBLE - (this.myPOSM1 & 0xFC));
                
                
                
                // Remember what clock HMOVE occured at
                this.myLastHMOVEClock = clock;
                
                // Disable TIA M0 "bug" used for stars in Cosmic ark
                this.myM0CosmicArkMotionEnabled = false;
                break;
            }
            
            case HMCLR: //0x2b:    // Clear horizontal motion registers
            {
                this.myTIAPokeRegister[HMP0]  = 0;
                this.myTIAPokeRegister[HMP1] = 0;
                this.myTIAPokeRegister[HMM0] = 0;
                this.myTIAPokeRegister[HMM1] = 0;
                this.myTIAPokeRegister[HMBL] = 0;
                break;
            }
            
            case CXCLR: //0x2c:    // Clear collision latches
            {
                this.myCollision = 0;
                break;
            }
            
            default:
            {
                
                break;
            }
        }//end : switch (address)
      }//end : addr is a pokable TIA register
        // }//end : if reg is not locked
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
//
    //TODO : fix bug with "electrophoresis ladder" looking thing on left side of screen
    //             -maybe a TIA bug, or maybe a CPU bug
    
    
    
    
    
    
    
    
    //------- DEBUG START ------------
    
    
    this.debugRenderTypes= []; // boolean 256
    
    this.debugStraightPoke=false;
    this.debugLockP0Mask=false;
    this.debugLockP1Mask=false;
    
    
    
    this.debugRegLocked= []; // boolean 0x2c
    
    
    this.debugResetRenderTypes = function () {this.debugRenderTypes= [];}
    this.debugGetRenderTypes = function() {return debugRenderTypes;}
    this.setDebugLockRegister = function(aItem, aValue) {this.debugRegLocked[aItem]=aValue;}
    this.getDebugLockRegister = function(aItem) {return this.debugRegLocked[aItem];}
    this.debugUnlockAllRegisters = function() {
        this.debugRegLocked=[];
        this.debugLockP0Mask=false;
        this.debugLockP1Mask=false;
    }
    this.debugGetNUSIZ0 = function() {return this.myTIAPokeRegister[NUSIZ0];}
    this.debugGetNUSIZ1 = function() {return this.myTIAPokeRegister[NUSIZ1];}
    
    this.debugSetP0Mask = function(aA,aB,aC,aD) {
        this.setCurrentP0Mask(aA, aB, aC, aD);
        this.debugLockP0Mask=true;
    }
    
    this.debugSetP1Mask = function(aA, aB,  aC, aD) {
        this.setCurrentP1Mask(aA, aB, aC, aD);
        this.debugLockP1Mask=true;
    }
    
    this.debugDumpRegs = function() {
    /*
        var zSB=new StringBuffer();
        zSB.append("ENAM0=" + bool(this.myTIAPokeRegister[ENAM0] & BIT1) + "; NUSIZ0=" + dbgHex(this.myTIAPokeRegister[NUSIZ0]) + "; ENAM1=" + bool(this.myTIAPokeRegister[ENAM1] & BIT1) + "; NUSIZ1=" + dbgHex(this.myTIAPokeRegister[NUSIZ1]) + "\n");
        // zSB.append("ENAM0=" + bool(this.myTIAPokeRegister[ENAM0] & BIT1) + "\n");
        zSB.append("CurrentM0Mask : alignment=" + this.myCurrentM0Mask[0] + ", num=" + this.myCurrentM0Mask[1] + ", size=" + this.myCurrentM0Mask[2] + ", x=" + this.myCurrentM0Mask[3] + "\n");
        zSB.append("CurrentM1Mask : alignment=" + this.myCurrentM1Mask[0] + ", num=" + this.myCurrentM1Mask[1] + ", size=" + this.myCurrentM1Mask[2] + ", x=" + this.myCurrentM1Mask[3] + "\n");
        zSB.append("CurrentP0Mask : alignment=" + this.myCurrentP0Mask[0] + ", num=" + this.myCurrentP0Mask[1] + ", size=" + this.myCurrentP0Mask[2] + ", x=" + this.myCurrentP0Mask[3] + "\n");
        zSB.append("CurrentP1Mask : alignment=" + this.myCurrentP1Mask[0] + ", num=" + this.myCurrentP1Mask[1] + ", size=" + this.myCurrentP1Mask[2] + ", x=" + this.myCurrentP1Mask[3] + "\n");
        
        zSB.append("M0Pos=" + this.myPOSM0 + "; M1Pos=" + this.myPOSM1 + "\n");
        zSB.append("P0Pos=" + this.myPOSP0 + "; P1Pos=" + this.myPOSP1 + "\n");
        zSB.append("COLUBK=0x" + Integer.toHexString(this.myTIAPokeRegister[COLUBK]) + "\n");
        return zSB.toString();
    */
    }
    this.debugPoke = function(aAddr, aValue) {
        this.debugStraightPoke=true;
        this.poke(aAddr, aValue);
        this.debugStraightPoke=false;}
    
    this.dbgHex = function(aNum) {
        return "0x"+ Num.toString(16);
    }
    
    //^^^^^^^^^^ DEBUG STOP ^^^^^^^^^^^^^^^^^^^^
    
    
    
    
    
    
    
    
    
    
    
    
}
