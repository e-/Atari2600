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
// $Id: JSTIA.java,v 1.12 2007/09/12 00:55:32 mauvila Exp $
//============================================================================

package jstella.core;
import jstella.j6507.IfcSystem;
import jstella.core.JSSystem;
import java.io.*;
import static jstella.core.JSConstants.*;



/**
 * This class is a device that emulates the Television Interface Adapator
 * found in the Atari 2600 and 7800 consoles.  The Television Interface
 * Adapator is an integrated circuit designed to interface between an
 * eight bit microprocessor and a television video modulator. It converts
 * eight bit parallel data into serial outputs for the color, luminosity,
 * and composite sync required by a video modulator.
 * 
 * This class outputs the serial data into a frame buffer which can then
 * be displayed on screen.
 * 
 * <p>
 * Consult the "Stella Programmer's Guide" (Steve Wright, 1979) for more information.
 * This guide was written back in the 1970s and was used originally by people who programmed
 * games for the Atari 2600.  It is widely available on the Internet.
 * 
 * Some definitions:
 * <dl>
 *  <dt>TIA</dt>
 *  <dd>a.k.a. Stella chip.The television interface
 * adapter, a chip found in the Atari 2600 that converted pokes from the
 * CPU into pictures and sound. It also managed the analog part
 * of the paddle controllers. </dd>
 * </dl>
 * <dl>
 *  <dt>color clock</dt>
 *  <dd>This can be thought of as a pixel, or
 * alternatively, as a unit of time (in which one pixel is drawn).
 * For every CPU cycle, there are three color clocks (often
 * abbreviated simply as "clocks") drawn. The width of the
 * visible part of a frame is 160 color clocks.</dd>
 * </dl>
 * @author Bradford W. Mott
 * @version $Id: JSTIA.java,v 1.12 2007/09/12 00:55:32 mauvila Exp $
 */
public class JSTIA implements IfcDevice, java.io.Serializable {
    private final static long serialVersionUID = -1703217043035095708L;
 
    
    
    //TODO : Maybe get rid of the old offset (0-3) system, as currently used in these masks
    //TODO : Get rid of disabled missile mask table...
    
    
    
    private final static int[] COSMICBUG_MOVEMENT = {18, 33, 0, 17};
    
    
    
  //  private  int[] myCurrentPFMask;
    private boolean M0Disabled=false;
    private boolean M1Disabled=false;
    
    private int[] myCurrentM0Mask=new int[4];
    private int[] myCurrentM1Mask=new int[4];
    private int[] myCurrentP0Mask=new int[4];
    private int[] myCurrentP1Mask=new int[4];
    private int[] myCurrentBLMask=new int[3];
    
  
    
    
    private int[] myTIAPokeRegister=new int[TIA_POKE_REGISTER_COUNT];
    
    
    
    
    private JSConsole myConsole=null;
    private JSSystem mySystem=null;
    
    
    private  boolean myColorLossEnabled;  //No clue what this means, except it has something to do with PAL (-JLA)
    private  boolean myPartialFrameFlag;
   // private int myFrameCounter=0;   // Number of frames displayed by this TIA
    private int myFramePointer=0;  // Index to the next pixel that will be drawn in the current frame buffer
    private int myFrameXStart=0;  // Indicates where the scanline should start being displayed
   
    
    

    private int myClockWhenFrameStarted=0;
    private int myClockStartDisplay=0; // Indicates color clocks when frame should begin to be drawn
    private int myClockStopDisplay=0;
    private int myClockAtLastUpdate=0;
    private int myClocksToEndOfScanLine=0;
    private int myScanlineCountForLastFrame=0;
    private int myCurrentScanline=0;  // Indicates the current scanline during a partial frame.
    private int myMaximumNumberOfScanlines=0;
    private int myVSYNCFinishClock=0;  // Color clock when VSYNC ending causes a new frame to be started
    
    private int myEnabledObjects=0;
    
    
    private int myPlayfieldPriorityAndScore=0;
    
    private int myVBlankOff=0;
    //private int myPreviousVBlankOff=0;
    private int myVBlankOn=0;
    private int myVSyncOn=0;
    private int myDetectedYStart=0;
    private int myDetectedYStop=0;
    
    
    
    
    // ---------- TIA register variables -----------
  
    
    
    
   
    
   
    
    private int myDGRP0=0;        // Player 0 delayed graphics register
    private int myDGRP1=0;        // Player 1 delayed graphics register
    

    private boolean myDENABL;        // Indicates if the vertically delayed ball is enabled
    
 

    
    
    private int myCollision=0;    // Collision register
    
    
    private int myPOSP0=0;         // Player 0 position register
    private int myPOSP1=0;         // Player 1 position register
    private int myPOSM0=0;         // Missile 0 position register
    private int myPOSM1=0;         // Missile 1 position register
    private int myPOSBL=0;         // Ball position register
    
    private int myCurrentGRP0=0;
    
    private int myCurrentGRP1=0;
    
    
    
    
    
    // Audio values. Only used by TIADebug.
 /*   private int myAUDV0;
    private int myAUDV1;
    private int myAUDC0;
    private int myAUDC1;
    private int myAUDF0;
    private int myAUDF1;
  */
    
    
    //------ Other variables --------
    
    private int myDumpDisabledCycle=0;  // Indicates when the dump for paddles was last set
    private boolean myDumpEnabled=false; // Indicates if the dump is current enabled for the paddles
    
    private int myLastHMOVEClock=0;  // Color clock when last HMOVE occured
    private boolean myHMOVEBlankEnabled=false; // Indicates if HMOVE blanks are currently enabled 
    private boolean myAllowHMOVEBlanks=false; // Indicates if we're allowing HMOVE blanks to be enabled (?-JLA)
    private boolean myM0CosmicArkMotionEnabled=true; // TIA M0 "bug" used for stars in Cosmic Ark flag
    private int myM0CosmicArkCounter=0;
   // private boolean[] myBitEnabled=new boolean[6];
    
    
    
    
    private transient long debugInstructionsExecuted=0L;
    private transient boolean debugHasExecutionOverrun=false;
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    protected JSTIA(JSConsole console) {
        myConsole=console;
        
        myColorLossEnabled=false;
        myMaximumNumberOfScanlines=LINES_PER_FRAME_TOTAL; //262 scanlines        
    
    }
    
   
    
    
    private void writeObject(ObjectOutputStream out) throws IOException
    {
      out.defaultWriteObject();
     } 
    
     private void readObject(ObjectInputStream in)  throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //========================== SIMPLE ACCESSOR METHODS ===================================
    // Note : some of these may seem superfluous, especially for internal use, but they
    // can come in handy when debugging.  (One can put an assert() in the accessor methods to see
    // exactly what bad data comes in and what goes out, and when these things occur.)
    
    
    
    private int getCOLUBK(){  return myTIAPokeRegister[COLUBK];  }
    private void setCOLUBK(int aValue)    {   myTIAPokeRegister[COLUBK]=aValue;     }
    private int getCOLUPF()    {  return myTIAPokeRegister[COLUPF];   }
    private void setCOLUPF(int aValue)   {   myTIAPokeRegister[COLUPF]=aValue;    }
    private int getCOLUP0()   {   return myTIAPokeRegister[COLUP0];   }
    private void setCOLUP0(int aValue)    {   myTIAPokeRegister[COLUP0]=aValue;    }
    private int getCOLUP1()   {   return myTIAPokeRegister[COLUP1];    }
    private void setCOLUP1(int aValue)    {    myTIAPokeRegister[COLUP1]=aValue;     }
 
    public int[] getCurrentFrameBuffer() { return myConsole.getVideo().getCurrentFrameBuffer(); }
    
  
    
   // public int getVBlankOn() { return myVBlankOn; }
   // public int getVBlankOff() { return myVBlankOff; }
    public int getVSyncOn() { return myVSyncOn; }
    public int getDetectedYStart() { return myDetectedYStart; }
    public int getDetectedYStop() { return myDetectedYStop; }
            
    
    /**
     * Answers the total number of scanlines the media source generated
     * in producing the current frame buffer. For partial frames, this
     * will be the current scanline.
     * @return total number of scanlines generated
     */
    protected int scanlines() { return (int)((mySystem.getCycles() * CLOCKS_PER_CPU_CYCLE) - myClockWhenFrameStarted)/CLOCKS_PER_LINE_TOTAL;}
    private JSConsole getConsole() { return myConsole; }
    
    
    //The get and set accessors for the current masks were originally done to ease the change from C++ to Java...
    //i.e. it involved less typing for me. -JLA
    private int getCurrentP0Mask(int aIndex) {
        return PLAYER_MASK_TABLE[myCurrentP0Mask[0]][myCurrentP0Mask[1]][myCurrentP0Mask[2]][myCurrentP0Mask[3]+aIndex];
        
    }
    
    private int getCurrentP1Mask(int aIndex) {
        return PLAYER_MASK_TABLE[myCurrentP1Mask[0]][myCurrentP1Mask[1]][myCurrentP1Mask[2]][myCurrentP1Mask[3]+aIndex];
        
    }
    
    
    private boolean getCurrentM0Mask(int aIndex) {
        if (M0Disabled==true) return bool(DISABLED_MASK_TABLE[aIndex]);
        else {
            assert(myCurrentM0Mask[3] + aIndex < 360);
            return MISSILE_MASK_TABLE[myCurrentM0Mask[0]][myCurrentM0Mask[1]][myCurrentM0Mask[2]][myCurrentM0Mask[3]+aIndex];
            //return getOMMT(myCurrentM0MaskIndex + aIndex);
        }//end : not disabled
    }
    
    private boolean getCurrentM1Mask(int aIndex) {
        if (M1Disabled==true) return bool(DISABLED_MASK_TABLE[aIndex]);
        else {
            assert(myCurrentM1Mask[3] + aIndex < 360);
            return MISSILE_MASK_TABLE[myCurrentM1Mask[0]][myCurrentM1Mask[1]][myCurrentM1Mask[2]][myCurrentM1Mask[3]+aIndex];
            
        }//end : not disabled
    }
    
    private void setCurrentM1Mask(int aA, int aB, int aC, int aD) {
        myCurrentM1Mask[0]=aA;
        myCurrentM1Mask[1]=aB;
        myCurrentM1Mask[2]=aC;
        myCurrentM1Mask[3]=aD;
        M1Disabled=false;
    }
    
    private void setCurrentP0Mask(int aA, int aB, int aC, int aD) {
        if (debugLockP0Mask==false) {
            myCurrentP0Mask[0]=aA;
            myCurrentP0Mask[1]=aB;
            myCurrentP0Mask[2]=aC;
            myCurrentP0Mask[3]=aD;
        }//end : is false
    }
    
    private void setCurrentP1Mask(int aA, int aB, int aC, int aD) {
        if (debugLockP1Mask==false) {
            myCurrentP1Mask[0]=aA;
            myCurrentP1Mask[1]=aB;
            myCurrentP1Mask[2]=aC;
            myCurrentP1Mask[3]=aD;
        }//end : is false
    }
    
    
    
    
    private void setCurrentM0Mask(int aA, int aB, int aC, int aD) {
        myCurrentM0Mask[0]=aA;
        myCurrentM0Mask[1]=aB;
        myCurrentM0Mask[2]=aC;
        myCurrentM0Mask[3]=aD;
        
        //myCurrentM0MaskIndex=getMasterIndexOMMT(aA, aB, aC, aD);
        M0Disabled=false;
    }
    
    
    private void setCurrentM0MaskDisabled() {
        M0Disabled=true;
        
    }
    
    
    
    
    private boolean getCurrentBLMask(int aIndex) {
        return BALL_MASK_TABLE[myCurrentBLMask[0]][myCurrentBLMask[1]][myCurrentBLMask[2]+aIndex];
        
    }
    
    private void setCurrentBLMask(int aA, int aB, int aC) {
        myCurrentBLMask[0]=aA;
        myCurrentBLMask[1]=aB;
        myCurrentBLMask[2]=aC;
        
    }
    
    
    private int getYStart()    {    return myConsole.getYStart();   }
    private int getDisplayHeight() { return myConsole.getDisplayHeight(); }
 
    
    private static boolean isBitOn(int aBitNumber, int aValue) {
        return ((aValue & (1<<aBitNumber))!=0);
    }
    
    
    
    private JSAudio getAudio() { return myConsole.getAudio(); }
    
    
    
    
    
    
    
    private int getCurrentClockCount() { return mySystem.getCycles() * CLOCKS_PER_CPU_CYCLE;    }
    private int getCurrentXPos() { return ((getCurrentClockCount() - myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL); }
    private int getCurrentScanline() { return ((getCurrentClockCount() - myClockWhenFrameStarted) / CLOCKS_PER_LINE_TOTAL); }
    
    
    
    
    
    
    
    private int getColor(int aIndex) {
        switch (aIndex) {
            case 0 : return getCOLUBK();//=myColor[0];
            case 1 : return myTIAPokeRegister[COLUPF]; //=myColor[1];
            case 2 : return myTIAPokeRegister[COLUP0]; //=myColor[2];
            case 3 : return myTIAPokeRegister[COLUP1]; //=myColor[3];
            default : assert(false); return 0;
            
        }//end : switch
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public String name() {
        return "TIA";
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void reset() {
        // Reset the sound device
        // dbg.out("RESETTING TIA");
        getAudio().reset(); //The TIA is in charge of the audio, at least as far as system is concerned
        
        for (int i=0; i<myTIAPokeRegister.length; i++) {
            myTIAPokeRegister[i]=0;
        }//end : for i loop
        
        // Currently no objects are enabled
        myEnabledObjects = 0;
        
        // Some default values for the registers
//   
        myPlayfieldPriorityAndScore = 0;
    
      //  myPF = 0;
       // myGRP0 = 0;
       // myGRP1 = 0;
        myDGRP0 = 0;
        myDGRP1 = 0;
    
        myDENABL = false;
    
        //myRESMP0 = false;
        //myRESMP1 = false;
        myCollision = 0;
        myPOSP0 = 0;
        myPOSP1 = 0;
        myPOSM0 = 0;
        myPOSM1 = 0;
        myPOSBL = 0;
        
        // Some default values for the "current" variables
        myCurrentGRP0 = 0;
        myCurrentGRP1 = 0;
        setCurrentBLMask(0,0,0);// = ourBallMaskTable[0][0];
        
        setCurrentM0Mask(0,0,0,0);
        setCurrentM1Mask(0,0,0,0);
        setCurrentP0Mask(0,0,0,0); //ourPlayerMaskTable[0][0][0];
        setCurrentP1Mask(0,0,0,0);
        //myCurrentP1Mask = ourPlayerMaskTable[0][0][0];
        //myCurrentPFMask = PLAYFIELD_TABLE[0];
        
        myLastHMOVEClock = 0;
        myHMOVEBlankEnabled = false;
        myM0CosmicArkMotionEnabled = false;
        myM0CosmicArkCounter = 0;
        
     
        
        myDumpEnabled = false;
        myDumpDisabledCycle = 0;
        
        myAllowHMOVEBlanks = true;
        
        if((myConsole.getDisplayFormat()==DisplayFormat.PAL)||(myConsole.getDisplayFormat()==DisplayFormat.PAL60)) {
            myColorLossEnabled = true;
            myMaximumNumberOfScanlines = 342;
        } else  // NTSC
        {
            myColorLossEnabled = false;
            myMaximumNumberOfScanlines = 290;
        }
        
        myVBlankOff=0;
        myVBlankOn=0;
        myVSyncOn=-1;
        myDetectedYStart=0;
        myDetectedYStop=0;
        
        
        debugHasExecutionOverrun=false;
        
        // Recalculate the size of the display
        frameReset();
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Resets certain variables about the frame.
     * This is called by the TIA's reset method.  It is also called whenever the JSConsole
     * changes a relevant attribute about the frame (e.g. the DisplayHeight).
     */
    protected void frameReset() {
        
        myConsole.getVideo().clearBuffers();   // Clear frame buffers
       
        myFramePointer = 0;    // Reset pixel pointer and drawing flag
        
        // Calculate color clock offsets for starting and stoping frame drawing
        //myStartDisplayOffset = CLOCKS_PER_LINE_TOTAL * getYStart();
       // myStopDisplayOffset = myStartDisplayOffset + (CLOCKS_PER_LINE_TOTAL * getDisplayHeight());
        
        // Reasonable values to start and stop the current frame drawing
        myClockWhenFrameStarted = mySystem.getCycles() * CLOCKS_PER_CPU_CYCLE; //now
        myClockStartDisplay = myClockWhenFrameStarted + (CLOCKS_PER_LINE_TOTAL * getYStart()); //what the clock will be when the visible part of the frame starts
        myClockStopDisplay = myClockWhenFrameStarted +  (CLOCKS_PER_LINE_TOTAL * (getYStart() + getDisplayHeight())); //when the visible part of the frame stops
        myClockAtLastUpdate = myClockWhenFrameStarted;  
        myClocksToEndOfScanLine = CLOCKS_PER_LINE_TOTAL;  //currently at beginning of a line
        myVSYNCFinishClock = Integer.MAX_VALUE; //0x7FFFFFFF;
        myScanlineCountForLastFrame = 0;
        myCurrentScanline = 0; //currently on the first line
        
        myFrameXStart = 0;    // Hardcoded in preparation for new TIA class
        //myFrameWidth  = CLOCKS_PER_LINE_VISIBLE;  // Hardcoded in preparation for new TIA class
        
        
       
        
       
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * This is called by the JSSystem whenever it resets its cycle counter, which it 
     * is supposed to do every frame.  This method makes sure that all of the variables
     * in the TIA class (and those that it controls) compensate for this, mostly by
     * subtracting from all the relevant variables the same value that was subtracted
     * from the JSSystem's counter.
     */
    public void systemCyclesReset() {
        // Get the current system cycle
        int cycles = mySystem.getCycles();
        
        //If we reset the cycle number from x to zero (i.e. subtract x from cycle #), we should subtract the same number from the previousCycles variable in Audio.
        //This way, (currentCycles - previousCycles) will remain as it would have without the reset
        if (getAudio()!=null) getAudio().systemCyclesReset(cycles);   // Adjust the sound cycle counter
        
        
        myDumpDisabledCycle -= cycles;   // Adjust the dump cycle
        
        
        int clocks = cycles * CLOCKS_PER_CPU_CYCLE;    // Get the current color clock the system is using
        
        // Adjust the clocks by this amount since we're reseting the clock to zero
        myClockWhenFrameStarted -= clocks;
        myClockStartDisplay -= clocks;
        myClockStopDisplay -= clocks;
        myClockAtLastUpdate -= clocks;
        myVSYNCFinishClock -= clocks;
        myLastHMOVEClock -= clocks;
    }
    
    /**
     * This method existed originally for debugging.  Performs the obvious.
     * @param aIndex array index
     * @param aValue new value
     */
    private void setCurrentFrameBuffer(int aIndex, int aValue) {
        
        getCurrentFrameBuffer()[aIndex]=aValue;
        
    }
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void install(jstella.core.JSSystem system) {
        // Remember which system I'm installed in
        mySystem = system;
        
        int shift = PAGE_SHIFT;
        mySystem.resetCycles();
        
        // All accesses are to this device
        PageAccess access=new PageAccess(this);
        access.setIndirectMode();
        
        // We're installing in a 2600 system
        for(int i = 0; i < 8192; i += (1 << shift)) {
            if((i & 0x1080) == 0x0000) {
                mySystem.setPageAccess((char)(i >> shift), access);
            }
        }
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * This method is the one that causes the CPU to execute.  It should be run once per frame.
     */
    protected void processFrame()  throws JSException {
        
     
        
        if (!myPartialFrameFlag) startFrame();
        myPartialFrameFlag=true;
        
        
        
        // Execute instructions until frame is finished, or a breakpoint/trap hits
        // 25000 is an arbitrary high number...this is called with the assumption that it will stop
        // before then (e.g. if the TIA is poked at VSYNC, depending on its state, it may halt the CPU,
        // thus completing the frame
        
        int zExecutions=0;
        int zInstructionsExecuted=0;
      //    long zTimeA=System.nanoTime();
        do{
            
            //A not very elegant work-around :
            //Some ROMs have a BRK instruction which terminates the execution before the frame is done...
            //this will keep calling executeCPU (up to 9 or 10 times) to see if it finishes the frame
            
           zInstructionsExecuted += mySystem.executeCPU(25000);
            zExecutions++;
            if (myPartialFrameFlag==false) break;
            
        }while (zExecutions<3);
        //todo : this execution do-while think may be unneeded-figure out
         if (zExecutions>=3) 
         {
            if (debugHasExecutionOverrun==false) System.out.println("debug: ********** Execution overrun in TIA *********");
            debugHasExecutionOverrun=true;
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
        int totalClocks = (mySystem.getCycles() * CLOCKS_PER_CPU_CYCLE) - myClockWhenFrameStarted;
        myCurrentScanline = totalClocks / CLOCKS_PER_LINE_TOTAL;
        
        if (!myPartialFrameFlag) endFrame();
       
        
    }
    
//----------
    
    /**
     * (A method that allowed me to type less when converting C++ to Java. It makes for
     * more readable code as well. -JLA)
     * @param aValue an integer
     * @return the boolean equivalent (in C++) of the integer
     */
    private static boolean bool(int aValue) {
        if (aValue==0) return false;
        else return true;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Called by update() at the start of a new frame.
     */
    private void startFrame() {
        // This stuff should only happen at the beginning of a new frame.
     
     
        myConsole.getVideo().swapFrameBuffers();
        // Remember the number of clocks which have passed on the current scanline
        // so that we can adjust the frame's starting clock by this amount.  This
        // is necessary since some games position objects during VSYNC and the
        // TIA's internal counters are not reset by VSYNC.
        int clocks = ((mySystem.getCycles() * CLOCKS_PER_CPU_CYCLE) - myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL;
        
        // Ask the system to reset the cycle count so it doesn't overflow
        mySystem.resetCycles();
        
        // Setup clocks that'll be used for drawing this frame
        myClockWhenFrameStarted = -1 * clocks;
        
        
        //myClockWhenFrameStarted=0;
        myClockStartDisplay = myClockWhenFrameStarted + (CLOCKS_PER_LINE_TOTAL * getYStart());
        myClockStopDisplay = myClockWhenFrameStarted + (CLOCKS_PER_LINE_TOTAL * (getYStart() + getDisplayHeight())); //myStopDisplayOffset;
        myClockAtLastUpdate = myClockStartDisplay;
        myClocksToEndOfScanLine = CLOCKS_PER_LINE_TOTAL;
        
        // Reset frame buffer pointer
        myFramePointer = 0;//myCurrentFrameBuffer;
        
        // If color loss is enabled then update the color registers based on
        // the number of scanlines in the last frame that was generated
        if(myColorLossEnabled) {
            if((myScanlineCountForLastFrame & 0x01)!=0) {
                myTIAPokeRegister[COLUP0] |= 0x01010101;
                myTIAPokeRegister[COLUP1] |= 0x01010101;
                myTIAPokeRegister[COLUPF] |= 0x01010101;
                myTIAPokeRegister[COLUBK] |= 0x01010101;
            } else {
                myTIAPokeRegister[COLUP0] &= 0xfefefefe;
                myTIAPokeRegister[COLUP1] &= 0xfefefefe;
                myTIAPokeRegister[COLUPF] &= 0xfefefefe;
                myTIAPokeRegister[COLUBK] &= 0xfefefefe;
            }
        }
        
        
        
      
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Called by update() at the end of a frame.
     */
    private void endFrame() {
        // This stuff should only happen at the end of a frame
        // Compute the number of scanlines in the frame
        myScanlineCountForLastFrame = myCurrentScanline;
        
    
    }
    
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private int clocksThisLine() {
        // calculate the current scanline
        int totalClocks = (mySystem.getCycles() * CLOCKS_PER_CPU_CYCLE) - myClockWhenFrameStarted;
        return totalClocks % CLOCKS_PER_LINE_TOTAL;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    private boolean isPlayfieldPixelOn(int aHPos)
    {
        int zPFBlock=aHPos / CLOCKS_PER_PLAYFIELD_BIT;
        if (zPFBlock >= 20)
        {
            if (isBitOn(0, myTIAPokeRegister[CTRLPF])==true) zPFBlock=39 - zPFBlock; //reflected
            else zPFBlock=zPFBlock - 20;
        }//end : right half of screen
        if (zPFBlock < 4) return isBitOn(4 + zPFBlock, myTIAPokeRegister[PF0]); //7 - zPFBlock, myTIAPokeRegister[PF0]);
        else if (zPFBlock < 12) return isBitOn(11 - zPFBlock, myTIAPokeRegister[PF1]);
        else return isBitOn(zPFBlock - 12, myTIAPokeRegister[PF2]); //(19 - zPFBlock, myTIAPokeRegister[PF2]);
    }
    
    private boolean isPlayer0PixelOn(int aHPos) {   return ((myCurrentGRP0 & getCurrentP0Mask(aHPos))!=0);  }    
    private boolean isPlayer1PixelOn(int aHPos) {   return ((myCurrentGRP1 & getCurrentP1Mask(aHPos))!=0);  }    
    private boolean isMissile0PixelOn(int aHPos) {  return getCurrentM0Mask(aHPos);  }    
    private boolean isMissile1PixelOn(int aHPos) {  return getCurrentM1Mask(aHPos);   }    
    private boolean isRESMP0()  {    return ((myTIAPokeRegister[RESMP0] & BIT1) != 0);  }    
    private boolean isRESMP1()  {    return ((myTIAPokeRegister[RESMP1] & BIT1) != 0);  }
            
    
    private void updatePlayfieldStatus()
    {
        if (((myTIAPokeRegister[PF0]&0xF0)==0)&&(myTIAPokeRegister[PF1]==0)&&(myTIAPokeRegister[PF2]==0)) myEnabledObjects &= ~BIT_PF;
        else myEnabledObjects |= BIT_PF;
         
        
    }
  
    
    private void memsetFrameBuffer(int aIndex,int aByteValue, int aCount) {
        java.util.Arrays.fill(getCurrentFrameBuffer(), aIndex, aIndex+aCount, aByteValue & 0xFF);    
       // for (int i=0; i<aCount; i++) {   setCurrentFrameBuffer(aIndex+i,(aByteValue & 0xFF)); }//end : for i loop
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
    private void updateFrameScanline(int clocksToUpdate, int hpos) {
        // Calculate the zEnding frame pointer value
        int zEnding=myFramePointer + clocksToUpdate;
        
        
        // See if we're in the vertical blank region
        // if(bool(myVBLANK & 0x02)) {
        if (isBitOn(1, myTIAPokeRegister[VBLANK]))  {
            memsetFrameBuffer(myFramePointer, 0, clocksToUpdate);
        }
        // Handle all other possible combinations
        else {
            int zDebugSwitch=myEnabledObjects | myPlayfieldPriorityAndScore;
            if ((zDebugSwitch>=0)&&(zDebugSwitch<0x100)) debugRenderTypes[zDebugSwitch]=true;
            switch(myEnabledObjects | myPlayfieldPriorityAndScore) {
                // Background
                case 0x00:
                case 0x00 | BIT_SCORE:
                case 0x00 | BIT_PRIORITY:
                case 0x00 | BIT_PRIORITY | BIT_SCORE:
                {
                    memsetFrameBuffer(myFramePointer, myTIAPokeRegister[COLUBK], clocksToUpdate);
                    break;
                }
                
                // Playfield is enabled and the score bit is not set
                case BIT_PF:
                case BIT_PF | BIT_PRIORITY:
                {
                   // int mask = hpos; //myCurrentPFMask[hpos];
                    while (myFramePointer<zEnding) {
                       
                        setCurrentFrameBuffer(myFramePointer, isPlayfieldPixelOn(hpos) ? myTIAPokeRegister[COLUPF] : myTIAPokeRegister[COLUBK]);
                       
                        myFramePointer++;
                   
                        hpos++;
                    }//end : while loop
                    
                    break;
                }
                
                // Playfield is enabled and the score bit is set
                case BIT_PF | BIT_SCORE:
                case BIT_PF | BIT_SCORE | BIT_PRIORITY:
                {
                   // int mask = hpos;//&myCurrentPFMask[hpos];
                    while (myFramePointer<zEnding) {
                    
                        
                            setCurrentFrameBuffer(myFramePointer, isPlayfieldPixelOn(hpos) ?
                            (hpos < 80 ? myTIAPokeRegister[COLUP0] : myTIAPokeRegister[COLUP1]) : myTIAPokeRegister[COLUBK]);
                           
                        myFramePointer++;
                     
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
                    
                    while(myFramePointer < zEnding) {
                        boolean zPlayer0Pixel=isPlayer0PixelOn(hpos);
                        
                         setCurrentFrameBuffer(myFramePointer, zPlayer0Pixel ? myTIAPokeRegister[COLUP0] : myTIAPokeRegister[COLUBK]);
                    
                         hpos++;
                        myFramePointer++;
                        
                    }
                    break;
                }
                
                // Player 1 is enabled
                case BIT_P1:
                case BIT_P1 | BIT_SCORE:
                case BIT_P1 | BIT_PRIORITY:
                case BIT_P1 | BIT_SCORE | BIT_PRIORITY:
                {
                 
                    
                    while(myFramePointer < zEnding) {
                        setCurrentFrameBuffer(myFramePointer, isPlayer1PixelOn(hpos) ? myTIAPokeRegister[COLUP1] : myTIAPokeRegister[COLUBK]);
                    
                       myFramePointer++;
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
                  
                    
                    while(myFramePointer < zEnding) {
                        boolean zPlayer0Pixel=isPlayer0PixelOn(hpos);
                        boolean zPlayer1Pixel=isPlayer1PixelOn(hpos);
                        setCurrentFrameBuffer(myFramePointer, zPlayer0Pixel ?
                            myTIAPokeRegister[COLUP0] : (zPlayer1Pixel ? myTIAPokeRegister[COLUP1] : myTIAPokeRegister[COLUBK]));
                        
                        if(zPlayer0Pixel && zPlayer1Pixel) myCollision |= COLLISION_TABLE[BIT_P0 | BIT_P1];
                        
                       
                     
                        myFramePointer++;
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
                    int mM0 = hpos;
                    
                    while(myFramePointer < zEnding) {
                        boolean zMMask=getCurrentM0Mask(mM0);
                        
                        setCurrentFrameBuffer(myFramePointer, zMMask ? myTIAPokeRegister[COLUP0] : myTIAPokeRegister[COLUBK]);
                        ++mM0; 
                        ++myFramePointer;
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
                    int mM1 = hpos;//myCurrentM1Mask[hpos];
                    
                    while(myFramePointer < zEnding) {
                        setCurrentFrameBuffer(myFramePointer, getCurrentM1Mask(mM1) ? myTIAPokeRegister[COLUP1] : myTIAPokeRegister[COLUBK]);
                        ++mM1;
                        ++myFramePointer;
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
                    int mBL = hpos;// &myCurrentBLMask[hpos];
                    
                    while(myFramePointer < zEnding) {
                        setCurrentFrameBuffer(myFramePointer, getCurrentBLMask(mBL) ? myTIAPokeRegister[COLUPF] : myTIAPokeRegister[COLUBK]);
                        ++mBL; ++myFramePointer;
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
                    int mM0  = hpos;//myCurrentM0Mask[hpos];
                    int mM1  = hpos;//myCurrentM1Mask[hpos];
                    
                    while(myFramePointer < zEnding) {
                        setCurrentFrameBuffer(myFramePointer, getCurrentM0Mask(mM0) ? myTIAPokeRegister[COLUP0] : (getCurrentM1Mask(mM1) ? myTIAPokeRegister[COLUP1] : myTIAPokeRegister[COLUBK]));
                        
                        if(getCurrentM0Mask(mM0) && getCurrentM1Mask(mM1))
                            myCollision |= COLLISION_TABLE[BIT_M0 | BIT_M1];
                          hpos++;
                        ++mM0; ++mM1; ++myFramePointer;
                        
                    }
                    break;
                }
                
                // Ball and Missile 0 are enabled and playfield priority is not set
                case BIT_BL | BIT_M0:
                case BIT_BL | BIT_M0 | BIT_SCORE:
                {
                    int mBL = hpos;//myCurrentBLMask[hpos];
                    int mM0 = hpos;//myCurrentM0Mask[hpos];
                    
                    while(myFramePointer < zEnding) {
                        setCurrentFrameBuffer(myFramePointer, (getCurrentM0Mask(mM0) ? myTIAPokeRegister[COLUP0] : (getCurrentBLMask(mBL) ? myTIAPokeRegister[COLUPF] : myTIAPokeRegister[COLUBK])));
                        
                        if(getCurrentBLMask(mBL) && getCurrentM0Mask(mM0))
                            myCollision |= COLLISION_TABLE[BIT_BL | BIT_M0];
                        
                        ++mBL; ++mM0; ++myFramePointer;
                          hpos++;
                    }
                    break;
                }
                
                // Ball and Missile 0 are enabled and playfield priority is set
                case BIT_BL | BIT_M0 | BIT_PRIORITY:
                case BIT_BL | BIT_M0 | BIT_SCORE | BIT_PRIORITY:
                {
                    int mBL = hpos;//myCurrentBLMask[hpos];
                    int mM0 = hpos;//myCurrentM0Mask[hpos];
                    
                    while(myFramePointer < zEnding) {
                        setCurrentFrameBuffer(myFramePointer, (getCurrentBLMask(mBL) ? myTIAPokeRegister[COLUPF] : (getCurrentM0Mask(mM0) ? myTIAPokeRegister[COLUP0] : myTIAPokeRegister[COLUBK])));
                        
                        if(getCurrentBLMask(mBL) && getCurrentM0Mask(mM0))
                            myCollision |= COLLISION_TABLE[BIT_BL | BIT_M0];
                        
                        ++mBL; ++mM0; ++myFramePointer;
                          hpos++;
                    }
                    break;
                }
                
                // Ball and Missile 1 are enabled and playfield priority is not set
                case BIT_BL | BIT_M1:
                case BIT_BL | BIT_M1 | BIT_SCORE:
                {
                    int mBL = hpos;//myCurrentBLMask[hpos];
                    int mM1 = hpos;//myCurrentM1Mask[hpos];
                    
                    while(myFramePointer < zEnding) {
                        setCurrentFrameBuffer(myFramePointer, (getCurrentM1Mask(mM1) ? myTIAPokeRegister[COLUP1] : (getCurrentBLMask(mBL) ? myTIAPokeRegister[COLUPF] : myTIAPokeRegister[COLUBK])));
                        
                        if(getCurrentBLMask(mBL) && getCurrentM1Mask(mM1))
                            myCollision |= COLLISION_TABLE[BIT_BL | BIT_M1];
                        
                        ++mBL; ++mM1; ++myFramePointer;
                          hpos++;
                        
                    }
                    break;
                }
                
                // Ball and Missile 1 are enabled and playfield priority is set
                case BIT_BL | BIT_M1 | BIT_PRIORITY:
                case BIT_BL | BIT_M1 | BIT_SCORE | BIT_PRIORITY:
                {
                    int mBL = hpos;//myCurrentBLMask[hpos];
                    int mM1 = hpos;//myCurrentM1Mask[hpos];
                    
                    while(myFramePointer < zEnding) {
                        setCurrentFrameBuffer(myFramePointer, (getCurrentBLMask(mBL) ? myTIAPokeRegister[COLUPF] : (getCurrentM1Mask(mM1) ? myTIAPokeRegister[COLUP1] : myTIAPokeRegister[COLUBK])));
                        
                        if(getCurrentBLMask(mBL) && getCurrentM1Mask(mM1))
                            myCollision |= COLLISION_TABLE[BIT_BL | BIT_M1];
                        
                        ++mBL; ++mM1; ++myFramePointer;
                          hpos++;
                    }
                    break;
                }
                
                // Ball and Player 1 are enabled and playfield priority is not set
                case BIT_BL | BIT_P1:
                case BIT_BL | BIT_P1 | BIT_SCORE:
                {
                    int mBL = hpos;//myCurrentBLMask[hpos];
                   // int mP1 = hpos;//myCurrentP1Mask[hpos];
                    
                    while(myFramePointer < zEnding) {
                        boolean zPlayer1Pixel=isPlayer1PixelOn(hpos);
                        setCurrentFrameBuffer(myFramePointer, zPlayer1Pixel ? myTIAPokeRegister[COLUP1] :
                            (getCurrentBLMask(mBL) ? myTIAPokeRegister[COLUPF] : myTIAPokeRegister[COLUBK]));
                        
                        if(getCurrentBLMask(mBL) && zPlayer1Pixel)
                            myCollision |= COLLISION_TABLE[BIT_BL | BIT_P1];
                        
                        ++mBL; 
                        //++mP1; 
                        ++myFramePointer;
                          hpos++;
                        
                    }
                    break;
                }
                
                // Ball and Player 1 are enabled and playfield priority is set
                case BIT_BL | BIT_P1 | BIT_PRIORITY:
                case BIT_BL | BIT_P1 | BIT_PRIORITY | BIT_SCORE:
                {
                    int mBL = hpos;//myCurrentBLMask[hpos];
                 
                    
                    while(myFramePointer < zEnding) {
                        boolean zPlayer1Pixel=isPlayer1PixelOn(hpos);
                        setCurrentFrameBuffer(myFramePointer, getCurrentBLMask(mBL) ? myTIAPokeRegister[COLUPF] :
                            (zPlayer1Pixel ? myTIAPokeRegister[COLUP1] : myTIAPokeRegister[COLUBK]));
                        
                        if(getCurrentBLMask(mBL) && zPlayer1Pixel)
                            myCollision |= COLLISION_TABLE[BIT_BL | BIT_P1];
                        
                        ++mBL; 
                        //++mP1; 
                        ++myFramePointer;
                          hpos++;
                    }
                    break;
                }
                
                // Playfield and Player 0 are enabled and playfield priority is not set
                case BIT_PF | BIT_P0:
                {
                   // int mPF = hpos;// &myCurrentPFMask[hpos];
                   // int mP0 = hpos;//myCurrentP0Mask[hpos];
                    
                    while(myFramePointer < zEnding) {
                    
                         boolean zPlayfieldIsOn=isPlayfieldPixelOn(hpos);
                         boolean zPlayer0Pixel=isPlayer0PixelOn(hpos);
                         setCurrentFrameBuffer(myFramePointer, zPlayer0Pixel ?
                            myTIAPokeRegister[COLUP0] : (zPlayfieldIsOn ? myTIAPokeRegister[COLUPF] : myTIAPokeRegister[COLUBK]));
                        
                        if(zPlayfieldIsOn && zPlayer0Pixel)
                            myCollision |= COLLISION_TABLE[BIT_PF | BIT_P0];
                        
                        hpos++;
                        
                       
                        ++myFramePointer;
                        
                    }
                    
                    break;
                }
                
                // Playfield and Player 0 are enabled and playfield priority is set
                case BIT_PF | BIT_P0 | BIT_PRIORITY:
                {
                  //  int mPF = hpos; // &myCurrentPFMask[hpos];
                    //int mP0 = hpos;//myCurrentP0Mask[hpos];
                    
                    while(myFramePointer < zEnding) {
                      
                        boolean zPlayfieldIsOn=isPlayfieldPixelOn(hpos);
                        boolean zPlayer0Pixel=isPlayer0PixelOn(hpos);
                            setCurrentFrameBuffer(myFramePointer, zPlayfieldIsOn ? myTIAPokeRegister[COLUPF] :
                            (zPlayer0Pixel ? myTIAPokeRegister[COLUP0] : myTIAPokeRegister[COLUBK]));
                        if(zPlayfieldIsOn && zPlayer0Pixel)
                            myCollision |= COLLISION_TABLE[BIT_PF | BIT_P0];
                        
                        hpos++;
                      
                        ++myFramePointer;
                        
                        
                        
                    }
                    
                    break;
                }
                
                // Playfield and Player 1 are enabled and playfield priority is not set
                case BIT_PF | BIT_P1:
                {
                 
                    
                    while(myFramePointer < zEnding) {
                           boolean zPlayfieldIsOn=isPlayfieldPixelOn(hpos);
                           boolean zPlayer1Pixel=isPlayer1PixelOn(hpos);
                     
                          setCurrentFrameBuffer(myFramePointer, zPlayer1Pixel ?
                            myTIAPokeRegister[COLUP1] : (zPlayfieldIsOn ? myTIAPokeRegister[COLUPF] : myTIAPokeRegister[COLUBK]));
                        
                        if(zPlayfieldIsOn && zPlayer1Pixel)
                            myCollision |= COLLISION_TABLE[BIT_PF | BIT_P1];  
                           
                        hpos++;
                         //++mP1; 
                         ++myFramePointer;
                        
                    }
                    
                    break;
                }
                
                // Playfield and Player 1 are enabled and playfield priority is set
                case BIT_PF | BIT_P1 | BIT_PRIORITY:
                {
                   
                    
                    while(myFramePointer < zEnding) {
                            boolean zPlayfieldIsOn=isPlayfieldPixelOn(hpos);
                            boolean zPlayer1Pixel=isPlayer1PixelOn(hpos);
                       
                          setCurrentFrameBuffer(myFramePointer, zPlayfieldIsOn ? myTIAPokeRegister[COLUPF] :
                            (zPlayer1Pixel ? myTIAPokeRegister[COLUP1] : myTIAPokeRegister[COLUBK]));
                        if(zPlayfieldIsOn && zPlayer1Pixel)
                            myCollision |= COLLISION_TABLE[BIT_PF | BIT_P1];   
                            
                        hpos++;    
                         //++mP1; 
                         ++myFramePointer;
                        
                        
                    }
                    
                    break;
                }
                
                // Playfield and Ball are enabled
                case BIT_PF | BIT_BL:
                case BIT_PF | BIT_BL | BIT_PRIORITY:
                {
                 
                    int mBL = hpos;//myCurrentBLMask[hpos];
                    
                    while(myFramePointer < zEnding) {
                        boolean zPlayfieldIsOn=isPlayfieldPixelOn(hpos);
                    
                         setCurrentFrameBuffer(myFramePointer, (zPlayfieldIsOn || getCurrentBLMask(mBL)) ? myTIAPokeRegister[COLUPF] : myTIAPokeRegister[COLUBK]);
                        
                        if(zPlayfieldIsOn && getCurrentBLMask(mBL))
                            myCollision |= COLLISION_TABLE[BIT_PF | BIT_BL];
                        
                        hpos++;
                        ++mBL; ++myFramePointer;
                        
                    }
                    break;
                }
                
                // Handle all of the other cases
                default:
                {
                    for(; myFramePointer < zEnding; ++myFramePointer, ++hpos) {
                        int enabled = isPlayfieldPixelOn(hpos) ? BIT_PF : 0;            //bool(myPF  & myCurrentPFMask[hpos]) ? BIT_PF : 0;
                        
                        if(bool(myEnabledObjects & BIT_BL) && getCurrentBLMask(hpos))    enabled |= BIT_BL;
                        if (isPlayer1PixelOn(hpos))                 enabled |= BIT_P1;
                        if(bool(myEnabledObjects & BIT_M1) && getCurrentM1Mask(hpos))    enabled |= BIT_M1;
                        if (isPlayer0PixelOn(hpos))                 enabled |= BIT_P0;
                        if(bool(myEnabledObjects & BIT_M0) && getCurrentM0Mask(hpos))    enabled |= BIT_M0;
                        
                        myCollision |= COLLISION_TABLE[enabled];
                        setCurrentFrameBuffer(myFramePointer, getColor(PRIORITY_ENCODER[hpos < 80 ? 0 : 1][enabled | myPlayfieldPriorityAndScore]));
                    }//end : for loop
                    break;
                }//end : default case
            }//end : switch
        }
        myFramePointer = zEnding;
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
    private void updateFrameScanlineSimple(int aClocksToUpdate, int aHPos)
    {
         int zEnding=myFramePointer + aClocksToUpdate;       
        
        // See if we're in the vertical blank region
        // if(bool(myVBLANK & 0x02)) {
        if (isBitOn(1, myTIAPokeRegister[VBLANK]))  {
            memsetFrameBuffer(myFramePointer, 0, aClocksToUpdate);
        }
       
        else {
        boolean zBallEnabled=((myEnabledObjects & BIT_BL)!=0);
        boolean zPlayfieldEnabled=((myEnabledObjects & BIT_PF)!=0);
        boolean zPlayer0Enabled=((myEnabledObjects & BIT_P0)!=0);
        boolean zPlayer1Enabled=((myEnabledObjects & BIT_P1)!=0);
        boolean zMissile0Enabled=((myEnabledObjects & BIT_M0)!=0);
        boolean zMissile1Enabled=((myEnabledObjects & BIT_M1)!=0);
         
             
        for(; myFramePointer < zEnding; ++myFramePointer, ++aHPos) {
                        int enabled = 0;
                        
                        if (zPlayfieldEnabled && isPlayfieldPixelOn(aHPos)) enabled |= BIT_PF; // : 0;            //bool(myPF  & myCurrentPFMask[aHPos]) ? BIT_PF : 0;
                        
                        if(zBallEnabled && getCurrentBLMask(aHPos))    enabled |= BIT_BL;
                        if (zPlayer1Enabled && isPlayer1PixelOn(aHPos))                 enabled |= BIT_P1;
                        if(zMissile1Enabled && getCurrentM1Mask(aHPos))    enabled |= BIT_M1;
                        if (zPlayer0Enabled && isPlayer0PixelOn(aHPos))                 enabled |= BIT_P0;
                        if(zMissile0Enabled && getCurrentM0Mask(aHPos))    enabled |= BIT_M0;
                        
                        myCollision |= COLLISION_TABLE[enabled];
                        setCurrentFrameBuffer(myFramePointer, getColor(PRIORITY_ENCODER[aHPos < 80 ? 0 : 1][enabled | myPlayfieldPriorityAndScore]));
                    }//end : for loop 
        
        }//end : vblank not on
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private void updateFrame(int clock) {
        
        // See if we're in the nondisplayable portion of the screen or if
        // we've already updated this portion of the screen
        if((clock < myClockStartDisplay) ||  (clock <= myClockAtLastUpdate)|| (myClockAtLastUpdate >= myClockStopDisplay) ) {
            return;
        }
        
        // Truncate the number of cycles to update to the stop display point
        if(clock > myClockStopDisplay)  clock = myClockStopDisplay;
        
        // Update frame one scanline at a time
        do //START OF LOOP
        {
            // Compute the number of clocks we're going to update
            int clocksToUpdate = 0;
            
            // Remember how many clocks we are from the left side of the screen
            int clocksFromStartOfScanLine = CLOCKS_PER_LINE_TOTAL - myClocksToEndOfScanLine;
            
            // See if we're updating more than the current scanline
            if(clock > (myClockAtLastUpdate + myClocksToEndOfScanLine)) {
                // Yes, we have more than one scanline to update so finish current one
                clocksToUpdate = myClocksToEndOfScanLine;
                myClocksToEndOfScanLine = CLOCKS_PER_LINE_TOTAL;
                myClockAtLastUpdate += clocksToUpdate;
            } else {
                // No, so do as much of the current scanline as possible
                clocksToUpdate = clock - myClockAtLastUpdate;
                myClocksToEndOfScanLine -= clocksToUpdate;
                myClockAtLastUpdate = clock;
            }
            
            int startOfScanLine = CLOCKS_PER_LINE_BLANK + myFrameXStart;
            
            // Skip over as many horizontal blank clocks as we can
            if(clocksFromStartOfScanLine < startOfScanLine) {
                int tmp;
                
                if((startOfScanLine - clocksFromStartOfScanLine) < clocksToUpdate)
                    tmp = startOfScanLine - clocksFromStartOfScanLine;
                else
                    tmp = clocksToUpdate;
                
                clocksFromStartOfScanLine += tmp;
                clocksToUpdate -= tmp;
            }
            
            // Remember frame pointer in case HMOVE blanks need to be handled
            int oldFramePointer = myFramePointer;
            
            // Update as much of the scanline as we can
            if(clocksToUpdate != 0) {
                updateFrameScanline(clocksToUpdate, clocksFromStartOfScanLine - CLOCKS_PER_LINE_BLANK);
               //updateFrameScanlineSimple(clocksToUpdate, clocksFromStartOfScanLine - CLOCKS_PER_LINE_BLANK);
            }
            
            // Handle HMOVE blanks if they are enabled
            if(myHMOVEBlankEnabled && (startOfScanLine < CLOCKS_PER_LINE_BLANK + 8) &&(clocksFromStartOfScanLine < (CLOCKS_PER_LINE_BLANK + 8))) {
                int blanks = (CLOCKS_PER_LINE_BLANK + 8) - clocksFromStartOfScanLine;
                memsetFrameBuffer(oldFramePointer, 0, blanks);
                if((clocksToUpdate + clocksFromStartOfScanLine) >= (CLOCKS_PER_LINE_BLANK + 8))  myHMOVEBlankEnabled = false;
            }
            
            // See if we're at the end of a scanline
            if(myClocksToEndOfScanLine == CLOCKS_PER_LINE_TOTAL) {
                myFramePointer -= (CLOCKS_PER_LINE_VISIBLE - myConsole.getDisplayWidth() - myFrameXStart);
                
                // Yes, so set PF mask based on current CTRLPF reflection state
               // myCurrentPFMask = PLAYFIELD_TABLE[myTIAPokeRegister[CTRLPF] & 0x01];
                //TODO : figure out what this did exactly
                
                // TODO: These should be reset right after the first copy of the player
                // has passed.  However, for now we'll just reset at the end of the
                // scanline since the other way would be too slow (01/21/99).
                
                setCurrentP0Mask(myPOSP0 & 0x03,0,myTIAPokeRegister[NUSIZ0] & 0x07,CLOCKS_PER_LINE_VISIBLE - (myPOSP0 & 0xFC));
                setCurrentP1Mask(myPOSP1 & 0x03,0,myTIAPokeRegister[NUSIZ1] & 0x07,CLOCKS_PER_LINE_VISIBLE - (myPOSP1 & 0xFC));
                
                
                // Handle the "Cosmic Ark" TIA bug if it's enabled
                if(myM0CosmicArkMotionEnabled) emulateCosmicBug();
                
                
            }//end: clocks to end of scanline == CLOCKS_PER_LINE_TOTAL
        }
        while(myClockAtLastUpdate < clock);
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private void emulateCosmicBug() {
        // Movement table associated with the bug
        
        myM0CosmicArkCounter = (myM0CosmicArkCounter + 1) & 3;
        myPOSM0 -= COSMICBUG_MOVEMENT[myM0CosmicArkCounter];
        
        if(myPOSM0 >= CLOCKS_PER_LINE_VISIBLE)
            myPOSM0 -= CLOCKS_PER_LINE_VISIBLE;
        else if(myPOSM0 < 0)
            myPOSM0 += CLOCKS_PER_LINE_VISIBLE;
        
        if(myM0CosmicArkCounter == 1) {
            // Stretch this missile so it's at least 2 pixels wide
            
            setCurrentM0Mask(myPOSM0 & 0x03, myTIAPokeRegister[NUSIZ0] & 0x07, ((myTIAPokeRegister[NUSIZ0] & 0x30) >> 4) | 0x01, CLOCKS_PER_LINE_VISIBLE - (myPOSM0 & 0xFC));
        } else if(myM0CosmicArkCounter == 2) {
            // Missile is disabled on this line
            
            setCurrentM0MaskDisabled();
        } else {
            
            setCurrentM0Mask(myPOSM0 & 0x03, myTIAPokeRegister[NUSIZ0] & 0x07, ((myTIAPokeRegister[NUSIZ0] & 0x30) >> 4), CLOCKS_PER_LINE_VISIBLE - (myPOSM0 & 0xFC));
            
        }
    }
    
    
    
    private   void waitHorizontalSync() {
        int cyclesToEndOfLine = 76 - ((mySystem.getCycles() -
                (myClockWhenFrameStarted / CLOCKS_PER_CPU_CYCLE)) % 76);
        
        if(cyclesToEndOfLine < 76) {
            mySystem.incrementCycles(cyclesToEndOfLine);
        }
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 /*private void greyOutFrame() {
        int c = scanlines();
        if(c < myYStart) c = myYStart;
  
        for( int s = c; s < (myHeight + myYStart); s++)
            for( int i = 0; i < 160; i++) {
            int tmp = myCurrentFrameBuffer[ (s - myYStart) * 160 + i] & 0x0f;
            tmp >>= 1;
            setCurrentFrameBuffer((s - myYStart) * 160 + i, tmp);
            }
        System.out.println("debug: FRAME GREY : " + System.currentTimeMillis());
  
    }
  */
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   /* protected void clearBuffers() {
        for(int i = 0; i < CLOCKS_PER_LINE_VISIBLE * 300; ++i) {
            setCurrentFrameBuffer(i,  0);
            myPreviousFrameBuffer[i] = 0;
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
    public  int peek(int addr) {
        int zReturn=0;
        // Update frame to current color clock before we look at anything!
        // dbg.out("Peek - TIA:", (int)addr);
        assert(addr>=0);
        updateFrame(mySystem.getCycles() * CLOCKS_PER_CPU_CYCLE);
        
        int noise = mySystem.getDataBusState() & 0x3F;
        
        switch(addr & 0x000f) {
            case CXM0P://0x00:    // CXM0P
                zReturn=(bool(myCollision & 0x0001) ? 0x80 : 0x00) |
                        (bool(myCollision & 0x0002) ? 0x40 : 0x00) | noise; break;
                        
            case CXM1P: //0x01:    // CXM1P
                zReturn=(bool(myCollision & 0x0004) ? 0x80 : 0x00) |
                        (bool(myCollision & 0x0008) ? 0x40 : 0x00) | noise; break;
                        
            case CXP0FB: //0x02:    // CXP0FB
                zReturn=(bool(myCollision & 0x0010) ? 0x80 : 0x00) |
                        (bool(myCollision & 0x0020) ? 0x40 : 0x00) | noise; break;
                        
            case CXP1FB: //0x03:    // CXP1FB
                zReturn=(bool(myCollision & 0x0040) ? 0x80 : 0x00) |
                        (bool(myCollision & 0x0080) ? 0x40 : 0x00) | noise; break;
                        
            case CXM0FB: //0x04:    // CXM0FB
                zReturn=(bool(myCollision & 0x0100) ? 0x80 : 0x00) |
                        (bool(myCollision & 0x0200) ? 0x40 : 0x00) | noise; break;
                        
            case CXM1FB: // 0x05:    // CXM1FB
                zReturn=(bool(myCollision & 0x0400) ? 0x80 : 0x00) |
                        (bool(myCollision & 0x0800) ? 0x40 : 0x00) | noise; break;
                        
            case CXBLPF: //0x06:    // CXBLPF
                zReturn=(bool(myCollision & 0x1000) ? 0x80 : 0x00) | noise; break;
                
            case CXPPMM: //0x07:    // CXPPMM
                zReturn=(bool(myCollision & 0x2000) ? 0x80 : 0x00) |
                        (bool(myCollision & 0x4000) ? 0x40 : 0x00) | noise; break;
                        
            case INPT0: //0x08:    // INPT0
            {
                int r = myConsole.getController(Jack.LEFT).read(AnalogPin.Nine);
                if(r == RESISTANCE_MIN) {
                    zReturn=0x80 | noise; break;
                } else if((r == RESISTANCE_MAX) || myDumpEnabled) {
                    zReturn=noise; break;
                } else {
                    double t = (1.6 * r * 0.01E-6);
                    int needed = (int)(t * 1.19E6);
                    if(mySystem.getCycles() > (myDumpDisabledCycle + needed)) {
                        zReturn=0x80 | noise; break;
                    } else {
                        zReturn=noise; break;
                    }
                }
            }
            
            case INPT1: //0x09:    // INPT1
            {
                int r = myConsole.getController(Jack.LEFT).read(AnalogPin.Five);
                if(r == RESISTANCE_MIN) {
                    zReturn=0x80 | noise; break;
                } else if((r == RESISTANCE_MAX) || myDumpEnabled) {
                    zReturn=noise; break;
                } else {
                    double t = (1.6 * r * 0.01E-6);
                    int needed = (int)(t * 1.19E6);
                    if(mySystem.getCycles() > (myDumpDisabledCycle + needed)) {
                        zReturn=0x80 | noise; break;
                    } else {
                        zReturn=noise; break;
                    }
                }
            }
            
            case INPT2: //0x0A:    // INPT2
            {
                int r = myConsole.getController(Jack.RIGHT).read(AnalogPin.Nine);
                if(r == RESISTANCE_MIN) {
                    zReturn=0x80 | noise; break;
                } else if((r == RESISTANCE_MAX) || myDumpEnabled) {
                    zReturn=noise; break;
                } else {
                    double t = (1.6 * r * 0.01E-6);
                    int needed = (int)(t * 1.19E6);
                    if(mySystem.getCycles() > (myDumpDisabledCycle + needed)) {
                        zReturn=0x80 | noise; break;
                    } else {
                        zReturn=noise; break;
                    }
                }
            }
            
            case INPT3: //0x0B:    // INPT3
            {
                int r = myConsole.getController(Jack.RIGHT).read(AnalogPin.Five);
                if(r == RESISTANCE_MIN) {
                    zReturn=0x80 | noise; break;
                } else if((r == RESISTANCE_MAX) || myDumpEnabled) {
                    zReturn=noise; break;
                } else {
                    double t = (1.6 * r * 0.01E-6);
                    int needed = (int)(t * 1.19E6);
                    if(mySystem.getCycles() > (myDumpDisabledCycle + needed)) {
                        zReturn=0x80 | noise; break;
                    } else {
                        zReturn=noise; break;
                    }
                }
            }
            
            case INPT4: //0x0C:    // INPT4
                zReturn=myConsole.getController(Jack.LEFT).read(DigitalPin.Six) ?
                    (0x80 | noise) : noise; break;
                    
            case INPT5: //0x0D:    // INPT5
                zReturn=myConsole.getController(Jack.RIGHT).read(DigitalPin.Six) ?
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
    public void poke(final int aAddress, final int aByteValue) {
        assert((aByteValue>=0)&&(aByteValue<0x100));
        
        int addr = aAddress & 0x003f;
        
        int clock = getCurrentClockCount(); //mySystem.getCycles() * 3;
        int delay = POKE_DELAY_TABLE[addr];
        
        // See if this is a poke to a PF register
        if(delay == -1) {
            /*static*/ int[] d = {4, 5, 2, 3};
            int x = getCurrentXPos();//((clock - myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL);
            delay = (char)d[(x / 3) & 3];
        }
        
        // Update frame to current CPU cycle before we make any changes!
        updateFrame(clock + delay);
        
        // If a VSYNC hasn't been generated in time go ahead and end the frame
        if(getCurrentScanline() > myMaximumNumberOfScanlines) {
            mySystem.stopCPU();//.stop();
            myPartialFrameFlag = false;
        }
        
                
       
        
    
      if (addr < TIA_POKE_REGISTER_COUNT) // is the address is a pokable TIA register?
      {
       
        int zPreviousValue=myTIAPokeRegister[addr]; //remember what the previous value was, just in case someone below wants to know
        
        myTIAPokeRegister[addr]=aByteValue;  //SETS THE NEW VALUE!
             
        switch(addr) {
            case VSYNC:    // VSYNC (vertical sync set/clear)
            {
                // myVSYNC = aByteValue;
              //  System.out.println("Debug : VSYNC poked, value=" + aByteValue + ", scanlines()==" + scanlines()); 
                if (((aByteValue & BIT1) != 0) &&((zPreviousValue & BIT1)==0))
                {
                    
                    myVSyncOn=scanlines();
                   // System.out.println("Debug : VSYNC ON, value=" + aByteValue + ", scanlines()==" + scanlines()); 
                }//end : turned VBlank ON
               
                
                
                if (bool(myTIAPokeRegister[VSYNC] & BIT1)) //is bit #1 on?
                {
                    // Indicate when VSYNC should be finished.  This should really
                    // be 3 * 228 according to Atari's documentation, however, some
                    // games don't supply the full 3 scanlines of VSYNC.
                    myVSYNCFinishClock = clock + CLOCKS_PER_LINE_TOTAL;
                } else if(!bool(myTIAPokeRegister[VSYNC] & BIT1) && (clock >= myVSYNCFinishClock)) {
                    // We're no longer interested in myVSYNCFinishClock
                    myVSYNCFinishClock = Integer.MAX_VALUE; //0x7FFFFFFF;
                    
                    // Since we're finished with the frame tell the processor to halt
                    mySystem.stopCPU();
                    myPartialFrameFlag = false;
                }
                break;
            }
            
            case VBLANK:    // VBLANK (vertical blank set/clear)
            {
                // Is the dump to ground path being set for I0, I1, I2, and I3?
                
                if (((aByteValue & BIT1) != 0) &&((zPreviousValue & BIT1)==0)) //AUTO DETECT FRAME HEIGHT
                {
                    //TODO : have this done only when in detection mode
                    myVBlankOn=scanlines();
                    
                    int zHeight=myVBlankOn - myVBlankOff;
                    if (zHeight < 0) zHeight += myVSyncOn;
                    if (zHeight >= FRAME_Y_MIN)
                    {
                        myDetectedYStart=myVBlankOff;
                        if (myDetectedYStart>=myVSyncOn) myDetectedYStart -= myVSyncOn;
                        myDetectedYStop=myDetectedYStart + zHeight;
                    }
                  //  System.out.println("Debug : VBLANK ON, value=" + aByteValue + ", scanlines()==" + scanlines()); 
                }//end : turned VBlank ON
                else if (((aByteValue & BIT1) == 0) && ((zPreviousValue & BIT1)!=0)) 
                {
                    
                    myVBlankOff=scanlines();
                    
                  // System.out.println("Debug : VBLANK OFF, value=" + aByteValue + ", scanlines()==" + scanlines()); 
                }//end : turned VBlank OFF
                
                
                if      ((bool(zPreviousValue & BIT7)==false) && (bool(myTIAPokeRegister[VBLANK] & BIT7)==true))  myDumpEnabled=true;            
                else if ((bool(zPreviousValue & BIT7)==true)  && (bool(myTIAPokeRegister[VBLANK] & BIT7)==false)) {
                    myDumpEnabled=false;
                    myDumpDisabledCycle = mySystem.getCycles();
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
                if(mySystem.getCPU().lastAccessWasRead()) {
                    // Tell the cpu to waste the necessary amount of time
                    waitHorizontalSync();
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
                setCurrentP0Mask(myPOSP0 & 0x03, 0, myTIAPokeRegister[NUSIZ0] & 0x07, CLOCKS_PER_LINE_VISIBLE - (myPOSP0 & 0xFC));
                setCurrentM0Mask(myPOSM0 & 0x03, myTIAPokeRegister[NUSIZ0] & 0x07, ((myTIAPokeRegister[NUSIZ0] & 0x30) >> 4) , CLOCKS_PER_LINE_VISIBLE - (myPOSM0 & 0xFC));
                                
                break;
            }
            
            case NUSIZ1:    // Number-size of player-missile 1
            {
               
                
                // TODO: Technically the "enable" part, [0], should depend on the current
                // enabled or disabled state.  This mean we probably need a data member
                // to maintain that state (01/21/99).
                
                setCurrentP1Mask(myPOSP1 & 0x03, 0, myTIAPokeRegister[NUSIZ1] & 0x07, CLOCKS_PER_LINE_VISIBLE - (myPOSP1 & 0xFC));                
                setCurrentM1Mask(myPOSM1 & 0x03, myTIAPokeRegister[NUSIZ1] & 0x07, ((myTIAPokeRegister[NUSIZ1] & 0x30) >> 4) , CLOCKS_PER_LINE_VISIBLE - (myPOSM1 & 0xFC));
                
                break;
            }
            
            case COLUP0:    // Color-Luminance Player 0
            case COLUP1:    // COLUM P1
            case COLUPF:    // COLUM PF
            case COLUBK:    // COLUM BK
            {
                int zColor = (int)(aByteValue & 0xfe);
                if(myColorLossEnabled && bool(myScanlineCountForLastFrame & BIT0))  zColor |= BIT0;
                myTIAPokeRegister[addr]=zColor;
                break;
            }
            
             
            
            case 0x0A:    // Control Playfield, Ball size, Collisions
            {
                //myCTRLPF = aByteValue;
                
                // The playfield priority and score bits from the control register
                // are accessed when the frame is being drawn.  We precompute the
                // necessary aByteValue here so we can save time while drawing.
                myPlayfieldPriorityAndScore = ((myTIAPokeRegister[CTRLPF] & 0x06) << 5);
                
                // Update the playfield mask based on reflection state if
                // we're still on the left hand side of the playfield
                if(((clock - myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL) < (68 + 79)) {
                  //  myCurrentPFMask = PLAYFIELD_TABLE[myTIAPokeRegister[CTRLPF] & 0x01];
                    //TODO : figure out what this did exactly
                }
                
                setCurrentBLMask(myPOSBL & 0x03,(myTIAPokeRegister[CTRLPF] & 0x30) >> 4,CLOCKS_PER_LINE_VISIBLE - (myPOSBL & 0xFC));
                
                
                break;
            }
            
            case REFP0:    // Reflect Player 0
            {
                // See if the reflection state of the player is being changed
                if ((zPreviousValue & BIT3) != (aByteValue & BIT3)) myCurrentGRP0 = PLAYER_REFLECT_TABLE[myCurrentGRP0];
                break;
            }
            
            case REFP1:    // Reflect Player 1
            {
                // See if the reflection state of the player is being changed
                if((zPreviousValue & BIT3) != (aByteValue & BIT3))  myCurrentGRP1 = PLAYER_REFLECT_TABLE[myCurrentGRP1];
                
                break;
            }
            
            case PF0 :    // Playfield register byte 0
            {
              
                
                updatePlayfieldStatus();
                
                break;
            }
            
            case PF1:    // Playfield register byte 1
            {
              //  myPF = (myPF & 0x000FF00F) | ((int)aByteValue << 4);
                
                updatePlayfieldStatus();
          
                
                break;
            }
            
            case PF2 :    // Playfield register byte 2
            {
               // myPF = (myPF & 0x00000FFF) | ((int)aByteValue << 12);
                
                updatePlayfieldStatus();
              
                
                break;
            }
            
            case RESP0:    // Reset Player 0
            {
                int hpos = (clock - myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL;
                int newx = hpos < CLOCKS_PER_LINE_BLANK ? 3 : (((hpos - CLOCKS_PER_LINE_BLANK) + 5) % CLOCKS_PER_LINE_VISIBLE);
                
                // Find out under what condition the player is being reset
                int when = PLAYER_POSITION_RESET_WHEN_TABLE[myTIAPokeRegister[NUSIZ0] & 7][myPOSP0][newx];
                
                // Player is being reset during the display of one of its copies
                if(when == 1) {
                    // So we go ahead and update the display before moving the player
                    // TODO: The 11 should depend on how much of the player has already
                    // been displayed.  Probably change table to return the amount to
                    // delay by instead of just 1 (01/21/99).
                    updateFrame(clock + 11);
                    
                    myPOSP0 = newx;
                    
                    // Setup the mask to skip the first copy of the player
                    
                    setCurrentP0Mask(myPOSP0 & 0x03,1,myTIAPokeRegister[NUSIZ0] & 0x07,CLOCKS_PER_LINE_VISIBLE - (myPOSP0 & 0xFC));
                }
                // Player is being reset in neither the delay nor display section
                else if(when == 0) {
                    myPOSP0 = newx;
                    
                    // So we setup the mask to skip the first copy of the player
                    setCurrentP0Mask(myPOSP0 & 0x03,1,myTIAPokeRegister[NUSIZ0] & 0x07,CLOCKS_PER_LINE_VISIBLE - (myPOSP0 & 0xFC));
                    
                }
                // Player is being reset during the delay section of one of its copies
                else if(when == -1) {
                    myPOSP0 = newx;
                    
                    // So we setup the mask to display all copies of the player
                    setCurrentP0Mask(myPOSP0 & 0x03,0,myTIAPokeRegister[NUSIZ0] & 0x07,CLOCKS_PER_LINE_VISIBLE - (myPOSP0 & 0xFC));
                    
                }
                break;
            }
            
            case RESP1:    // Reset Player 1
            {
                int hpos = (clock - myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL;
                int newx = hpos < CLOCKS_PER_LINE_BLANK ? 3 : (((hpos - CLOCKS_PER_LINE_BLANK) + 5) % CLOCKS_PER_LINE_VISIBLE);
                
                // Find out under what condition the player is being reset
                int when = PLAYER_POSITION_RESET_WHEN_TABLE[myTIAPokeRegister[NUSIZ1] & 7][myPOSP1][newx];
                
                // Player is being reset during the display of one of its copies
                if(when == 1) {
                    // So we go ahead and update the display before moving the player
                    // TODO: The 11 should depend on how much of the player has already
                    // been displayed.  Probably change table to return the amount to
                    // delay by instead of just 1 (01/21/99).
                    updateFrame(clock + 11);
                    
                    myPOSP1 = newx;
                    
                    // Setup the mask to skip the first copy of the player
                    
                    setCurrentP1Mask(myPOSP1 & 0x03, 1, myTIAPokeRegister[NUSIZ1] & 0x07, CLOCKS_PER_LINE_VISIBLE - (myPOSP1 & 0xFC));
                }
                // Player is being reset in neither the delay nor display section
                else if(when == 0) {
                    myPOSP1 = newx;
                    
                    // So we setup the mask to skip the first copy of the player
                    setCurrentP1Mask(myPOSP1 & 0x03, 1, myTIAPokeRegister[NUSIZ1] & 0x07, CLOCKS_PER_LINE_VISIBLE - (myPOSP1 & 0xFC));
                    
                }
                // Player is being reset during the delay section of one of its copies
                else if(when == -1) {
                    myPOSP1 = newx;
                    
                    // So we setup the mask to display all copies of the player
                    setCurrentP1Mask(myPOSP1 & 0x03, 0, myTIAPokeRegister[NUSIZ1] & 0x07, CLOCKS_PER_LINE_VISIBLE - (myPOSP1 & 0xFC));
                    
                }
                break;
            }
            
            case RESM0:    // Reset Missile 0
            {
                int hpos = (clock - myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL;
                myPOSM0 = hpos < CLOCKS_PER_LINE_BLANK ? 2 : (((hpos - CLOCKS_PER_LINE_BLANK) + 4) % CLOCKS_PER_LINE_VISIBLE);
                
                // TODO: Remove the following special hack for Dolphin by
                // figuring out what really happens when Reset Missile
                // occurs 20 cycles after an HMOVE (04/13/02).
                if(((clock - myLastHMOVEClock) == (20 * 3)) && (hpos == 69)) {
                    myPOSM0 = 8;
                }
                setCurrentM0Mask(myPOSM0 & 0x03, myTIAPokeRegister[NUSIZ0] & 0x07, ((myTIAPokeRegister[NUSIZ0] & 0x30) >> 4), CLOCKS_PER_LINE_VISIBLE - (myPOSM0 & 0xFC));
                break;
            }
            
            case RESM1:    // Reset Missile 1
            {
                int hpos = (clock - myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL;
                myPOSM1 = hpos < CLOCKS_PER_LINE_BLANK ? 2 : (((hpos - CLOCKS_PER_LINE_BLANK) + 4) % CLOCKS_PER_LINE_VISIBLE);
                
                // TODO: Remove the following special hack for Pitfall II by
                // figuring out what really happens when Reset Missile
                // occurs 3 cycles after an HMOVE (04/13/02).
                if(((clock - myLastHMOVEClock) == (3 * 3)) && (hpos == 18)) {
                    myPOSM1 = 3;
                }
                setCurrentM1Mask(myPOSM1 & 0x03, myTIAPokeRegister[NUSIZ1] & 0x07, ((myTIAPokeRegister[NUSIZ1] & 0x30) >> 4) , CLOCKS_PER_LINE_VISIBLE - (myPOSM1 & 0xFC));
                
                
                break;
            }
            
            case RESBL:    // Reset Ball
            {
                int hpos = (clock - myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL ;
                myPOSBL = hpos < CLOCKS_PER_LINE_BLANK ? 2 : (((hpos - CLOCKS_PER_LINE_BLANK) + 4) % CLOCKS_PER_LINE_VISIBLE);
                
                // TODO: Remove the following special hack for Escape from the
                // Mindmaster by figuring out what really happens when Reset Ball
                // occurs 18 cycles after an HMOVE (01/09/99).
                if(((clock - myLastHMOVEClock) == (18 * 3)) &&
                        ((hpos == 60) || (hpos == 69))) {
                    myPOSBL = 10;
                }
                // TODO: Remove the following special hack for Decathlon by
                // figuring out what really happens when Reset Ball
                // occurs 3 cycles after an HMOVE (04/13/02).
                else if(((clock - myLastHMOVEClock) == (3 * 3)) && (hpos == 18)) {
                    myPOSBL = 3;
                }
                // TODO: Remove the following special hack for Robot Tank by
                // figuring out what really happens when Reset Ball
                // occurs 7 cycles after an HMOVE (04/13/02).
                else if(((clock - myLastHMOVEClock) == (7 * 3)) && (hpos == 30)) {
                    myPOSBL = 6;
                }
                // TODO: Remove the following special hack for Hole Hunter by
                // figuring out what really happens when Reset Ball
                // occurs 6 cycles after an HMOVE (04/13/02).
                else if(((clock - myLastHMOVEClock) == (6 * 3)) && (hpos == 27)) {
                    myPOSBL = 5;
                }
                setCurrentBLMask(myPOSBL & 0x03,(myTIAPokeRegister[CTRLPF] & 0x30) >> 4, CLOCKS_PER_LINE_VISIBLE - (myPOSBL & 0xFC));
                
                break;
            }
            
            // AUDIO REGISTER POKE
            case AUDC0:    // Audio control 0
            case AUDC1:    // Audio control 1
            case AUDF0:    // Audio frequency 0
            case AUDF1:    // Audio frequency 1
            case AUDV0:    // Audio volume 0
            case AUDV1:    // Audio volume 1
                getAudio().pokeAudioRegister(addr, aByteValue, mySystem.getCycles()); //outsource to JSAudio
                break;
                
                
            case GRP0: //0x1B:    // Graphics Player 0
            {
                // Set player 0 graphics
               // myGRP0 = aByteValue; //(myBitEnabled[TIABitP0] ? aByteValue : 0);
                
                // Copy player 1 graphics into its delayed register
                myDGRP1 = myTIAPokeRegister[GRP1];
                
                // Get the "current" data for GRP0 base on delay register and reflect
                int grp0 = bool(myTIAPokeRegister[VDELP0] & BIT0) ? myDGRP0 : myTIAPokeRegister[GRP0];
                myCurrentGRP0 = bool(myTIAPokeRegister[REFP0] & BIT3) ? PLAYER_REFLECT_TABLE[grp0] : grp0;
                
                // Get the "current" data for GRP1 base on delay register and reflect
                int grp1 = bool(myTIAPokeRegister[VDELP1] & BIT0) ? myDGRP1 : myTIAPokeRegister[GRP1];
                myCurrentGRP1 = bool(myTIAPokeRegister[REFP1] & BIT3) ? PLAYER_REFLECT_TABLE[grp1] : grp1;
                
                // Set enabled object bits
                if(myCurrentGRP0 != 0)
                    myEnabledObjects |= BIT_P0;
                else
                    myEnabledObjects &= ~BIT_P0;
                
                if(myCurrentGRP1 != 0)
                    myEnabledObjects |= BIT_P1;
                else
                    myEnabledObjects &= ~BIT_P1;
                
                break;
            }
            
            case GRP1: //0x1C:    // Graphics Player 1
            {
                // Set player 1 graphics
               // myGRP1 = aByteValue; //(myBitEnabled[TIABitP1] ? aByteValue : 0);
                
                // Copy player 0 graphics into its delayed register
                myDGRP0 = myTIAPokeRegister[GRP0];
                
                // Copy ball graphics into its delayed register
                myDENABL = bool(myTIAPokeRegister[ENABL] & BIT1);
                
                // Get the "current" data for GRP0 base on delay register
                int grp0 = bool(myTIAPokeRegister[VDELP0] & BIT0) ? myDGRP0 : myTIAPokeRegister[GRP0];
                myCurrentGRP0 = bool(myTIAPokeRegister[REFP0] & BIT3) ? PLAYER_REFLECT_TABLE[grp0] : grp0;
                
                // Get the "current" data for GRP1 base on delay register
                int grp1 = bool(myTIAPokeRegister[VDELP1] & BIT0) ? myDGRP1 : myTIAPokeRegister[GRP1];
                myCurrentGRP1 = bool(myTIAPokeRegister[REFP1] & BIT3) ? PLAYER_REFLECT_TABLE[grp1] : grp1;
                
                // Set enabled object bits
                if(myCurrentGRP0 != 0)
                    myEnabledObjects |= BIT_P0;
                else
                    myEnabledObjects &= ~BIT_P0;
                
                if(myCurrentGRP1 != 0)
                    myEnabledObjects |= BIT_P1;
                else
                    myEnabledObjects &= ~BIT_P1;
                
                if(bool(myTIAPokeRegister[VDELBL] & BIT0) ? myDENABL : bool(myTIAPokeRegister[ENABL] & BIT1))
                    myEnabledObjects |= BIT_BL;
                else
                    myEnabledObjects &= ~BIT_BL;
                
                break;
            }
            
            case ENAM0:    // Enable Missile 0 graphics
            {
               // myENAM0 = bool(myBitEnabled[TIABitM0] ? aByteValue & 0x02 : 0);
             //   if (myBitEnabled[TIABitM0] == false) myTIAPokeRegister[ENAM0]=0;
                if(bool(myTIAPokeRegister[ENAM0] & BIT1) && !isRESMP0())
                    myEnabledObjects |= BIT_M0;
                else
                    myEnabledObjects &= ~BIT_M0;
                break;
            }
            
            case ENAM1:    // Enable Missile 1 graphics
            {
                //myENAM1 = bool(myBitEnabled[TIABitM1] ? aByteValue & 0x02 : 0);
               //  if (myBitEnabled[TIABitM1] == false) myTIAPokeRegister[ENAM1]=0;
                if(bool(myTIAPokeRegister[ENAM1] & BIT1) && !isRESMP1())
                    myEnabledObjects |= BIT_M1;
                else
                    myEnabledObjects &= ~BIT_M1;
                break;
            }
            
            case ENABL:    // Enable Ball graphics
            {
              //  myENABL = bool(myBitEnabled[TIABitBL] ? aByteValue & 0x02 : 0);
               //    if (myBitEnabled[TIABitBL] == false) myTIAPokeRegister[ENABL]=0;
                if(bool(myTIAPokeRegister[VDELBL] & BIT0) ? myDENABL : bool(myTIAPokeRegister[ENABL] & BIT1))
                    myEnabledObjects |= BIT_BL;
                else
                    myEnabledObjects &= ~BIT_BL;
                
                break;
            }
            
            case HMP0:    // Horizontal Motion Player 0
            {
                //  int zSignedVal=com.mauvila.mvunsigned.MvUnsignedUtil.toSignedByteValue(aByteValue)
               // myHMP0 = aByteValue >> 4;
                break;
            }
            
            case HMP1:    // Horizontal Motion Player 1
            {
               // myHMP1 = aByteValue >> 4;
                break;
            }
            
            case HMM0:    // Horizontal Motion Missile 0
            {
                int tmp = aByteValue >> 4;
                
                // Should we enabled TIA M0 "bug" used for stars in Cosmic Ark?
                if((clock == (myLastHMOVEClock + 21 * 3)) && ((zPreviousValue >> 4)== 7) && (tmp == 6)) {
                    myM0CosmicArkMotionEnabled = true;
                    myM0CosmicArkCounter = 0;
                }
                
               // myHMM0 = tmp;
                break;
            }
            
            case HMM1:    // Horizontal Motion Missile 1
            {
               // myHMM1 = aByteValue >> 4;
                break;
            }
            
            case HMBL:    // Horizontal Motion Ball
            {
              //  myHMBL = aByteValue >> 4;
                break;
            }
            
            case VDELP0:    // Vertial Delay Player 0
            {
                //myVDELP0 = bool(aByteValue & 0x01);
                
                int grp0 = bool(myTIAPokeRegister[VDELP0] & BIT0) ? myDGRP0 : myTIAPokeRegister[GRP0];
                myCurrentGRP0 = bool(myTIAPokeRegister[REFP0] & BIT3) ? PLAYER_REFLECT_TABLE[grp0] : grp0;
                
                if(myCurrentGRP0 != 0)
                    myEnabledObjects |= BIT_P0;
                else
                    myEnabledObjects &= ~BIT_P0;
                break;
            }
            
            case VDELP1:    // Vertial Delay Player 1
            {
               // myVDELP1 = bool(aByteValue & 0x01);
                int grp1 = bool(myTIAPokeRegister[VDELP1] & BIT0) ? myDGRP1 : myTIAPokeRegister[GRP1];
                myCurrentGRP1 = bool(myTIAPokeRegister[REFP1] & BIT3) ? PLAYER_REFLECT_TABLE[grp1] : grp1;
                if(myCurrentGRP1 != 0) myEnabledObjects |= BIT_P1;
                else myEnabledObjects &= ~BIT_P1;
                break;
            }
            
            case VDELBL:    // Vertial Delay Ball
            {
              //  myVDELBL = bool(aByteValue & 0x01);
                
                if(bool(myTIAPokeRegister[VDELBL] & BIT0) ? myDENABL : bool(myTIAPokeRegister[ENABL] & BIT1))
                    myEnabledObjects |= BIT_BL;
                else
                    myEnabledObjects &= ~BIT_BL;
                break;
            }
            
            case RESMP0 :    // Reset missile 0 to player 0
            {
                if(((zPreviousValue & BIT1)!=0) && !bool(aByteValue & 0x02)) {
                    char middle;
                    
                    if((myTIAPokeRegister[NUSIZ0] & 0x07) == 0x05)
                        middle = 8;
                    else if((myTIAPokeRegister[NUSIZ0] & 0x07) == 0x07)
                        middle = 16;
                    else
                        middle = 4;
                    
                    myPOSM0 = (myPOSP0 + middle) % CLOCKS_PER_LINE_VISIBLE;
                    setCurrentM0Mask(myPOSM0 & 0x03, myTIAPokeRegister[NUSIZ0] & 0x07, ((myTIAPokeRegister[NUSIZ0] & 0x30) >> 4), CLOCKS_PER_LINE_VISIBLE - (myPOSM0 & 0xFC));
                    
                    
                }
                
                //myRESMP0 = bool(aByteValue & 0x02);
                
                if(bool(myTIAPokeRegister[ENAM0] & BIT1) && !isRESMP0())
                    myEnabledObjects |= BIT_M0;
                else
                    myEnabledObjects &= ~BIT_M0;
                
                break;
            }
            
            case RESMP1:    // Reset missile 1 to player 1
            {
                if(((zPreviousValue & BIT1)!=0) && !bool(aByteValue & 0x02)) {
                    char middle;
                    
                    if((myTIAPokeRegister[NUSIZ1] & 0x07) == 0x05)
                        middle = 8;
                    else if((myTIAPokeRegister[NUSIZ1] & 0x07) == 0x07)
                        middle = 16;
                    else
                        middle = 4;
                    
                    myPOSM1 = (myPOSP1 + middle) % CLOCKS_PER_LINE_VISIBLE;
                    
                    setCurrentM1Mask(myPOSM1 & 0x03, myTIAPokeRegister[NUSIZ1] & 0x07, ((myTIAPokeRegister[NUSIZ1] & 0x30) >> 4) , CLOCKS_PER_LINE_VISIBLE - (myPOSM1 & 0xFC));
                    
                }
                
                //myRESMP1 = bool(aByteValue & 0x02);
                
                if(bool(myTIAPokeRegister[ENAM1] & BIT1) && !isRESMP1())
                    myEnabledObjects |= BIT_M1;
                else
                    myEnabledObjects &= ~BIT_M1;
                break;
            }
            
            case HMOVE: //0x2A:    // Apply horizontal motion HMOVE
            {
                // Figure out what cycle we're at
                int x = ((clock - myClockWhenFrameStarted) % CLOCKS_PER_LINE_TOTAL) / 3;
                
                // See if we need to enable the HMOVE blank bug
                if(myAllowHMOVEBlanks && HMOVE_BLANK_ENABLE_CYCLES[x]) {
                    // TODO: Allow this to be turned off using properties...
                    myHMOVEBlankEnabled = true;
                }
                
                myPOSP0 += COMPLETE_MOTION_TABLE[x][myTIAPokeRegister[HMP0]  >> 4];
                myPOSP1 += COMPLETE_MOTION_TABLE[x][myTIAPokeRegister[HMP1] >> 4];
                myPOSM0 += COMPLETE_MOTION_TABLE[x][myTIAPokeRegister[HMM0] >> 4];
                myPOSM1 += COMPLETE_MOTION_TABLE[x][myTIAPokeRegister[HMM1] >> 4];
                myPOSBL += COMPLETE_MOTION_TABLE[x][myTIAPokeRegister[HMBL] >> 4];
                
                if(myPOSP0 >= CLOCKS_PER_LINE_VISIBLE)
                    myPOSP0 -= CLOCKS_PER_LINE_VISIBLE;
                else if(myPOSP0 < 0)
                    myPOSP0 += CLOCKS_PER_LINE_VISIBLE;
                
                if(myPOSP1 >= CLOCKS_PER_LINE_VISIBLE)
                    myPOSP1 -= CLOCKS_PER_LINE_VISIBLE;
                else if(myPOSP1 < 0)
                    myPOSP1 += CLOCKS_PER_LINE_VISIBLE;
                
                if(myPOSM0 >= CLOCKS_PER_LINE_VISIBLE)
                    myPOSM0 -= CLOCKS_PER_LINE_VISIBLE;
                else if(myPOSM0 < 0)
                    myPOSM0 += CLOCKS_PER_LINE_VISIBLE;
                
                if(myPOSM1 >= CLOCKS_PER_LINE_VISIBLE)
                    myPOSM1 -= CLOCKS_PER_LINE_VISIBLE;
                else if(myPOSM1 < 0)
                    myPOSM1 += CLOCKS_PER_LINE_VISIBLE;
                
                if(myPOSBL >= CLOCKS_PER_LINE_VISIBLE)
                    myPOSBL -= CLOCKS_PER_LINE_VISIBLE;
                else if(myPOSBL < 0)
                    myPOSBL += CLOCKS_PER_LINE_VISIBLE;
                setCurrentBLMask(myPOSBL & 0x03,(myTIAPokeRegister[CTRLPF] & 0x30) >> 4,CLOCKS_PER_LINE_VISIBLE - (myPOSBL & 0xFC));
                
                setCurrentP0Mask(myPOSP0 & 0x03,0,myTIAPokeRegister[NUSIZ0] & 0x07,CLOCKS_PER_LINE_VISIBLE - (myPOSP0 & 0xFC));
                
                setCurrentP1Mask(myPOSP1 & 0x03, 0, myTIAPokeRegister[NUSIZ1] & 0x07, CLOCKS_PER_LINE_VISIBLE - (myPOSP1 & 0xFC));
                
                
                setCurrentM0Mask(myPOSM0 & 0x03, myTIAPokeRegister[NUSIZ0] & 0x07, ((myTIAPokeRegister[NUSIZ0] & 0x30) >> 4) /*| 0x01*/, CLOCKS_PER_LINE_VISIBLE - (myPOSM0 & 0xFC));
                setCurrentM1Mask(myPOSM1 & 0x03, myTIAPokeRegister[NUSIZ1] & 0x07, ((myTIAPokeRegister[NUSIZ1] & 0x30) >> 4) , CLOCKS_PER_LINE_VISIBLE - (myPOSM1 & 0xFC));
                
                
                
                // Remember what clock HMOVE occured at
                myLastHMOVEClock = clock;
                
                // Disable TIA M0 "bug" used for stars in Cosmic ark
                myM0CosmicArkMotionEnabled = false;
                break;
            }
            
            case HMCLR: //0x2b:    // Clear horizontal motion registers
            {
                myTIAPokeRegister[HMP0]  = 0;
                myTIAPokeRegister[HMP1] = 0;
                myTIAPokeRegister[HMM0] = 0;
                myTIAPokeRegister[HMM1] = 0;
                myTIAPokeRegister[HMBL] = 0;
                break;
            }
            
            case CXCLR: //0x2c:    // Clear collision latches
            {
                myCollision = 0;
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
    
    
    private boolean[] debugRenderTypes=new boolean[256];
    
    private boolean debugStraightPoke=false;
    private boolean debugLockP0Mask=false;
    private boolean debugLockP1Mask=false;
    
    
    
    private boolean[] debugRegLocked=new boolean[0x2C];
    
    
    public void debugResetRenderTypes() {debugRenderTypes=new boolean[256];}
    public boolean[] debugGetRenderTypes() {return debugRenderTypes;}
    public void setDebugLockRegister(int aItem, boolean aValue) {debugRegLocked[aItem]=aValue;}
    public boolean getDebugLockRegister(int aItem) {return debugRegLocked[aItem];}
    public void debugUnlockAllRegisters() {
        debugRegLocked=new boolean[0x2C];
        debugLockP0Mask=false;
        debugLockP1Mask=false;
    }
    public int debugGetNUSIZ0() {return myTIAPokeRegister[NUSIZ0];}
    public int debugGetNUSIZ1() {return myTIAPokeRegister[NUSIZ1];}
    
    public void debugSetP0Mask(int aA, int aB, int aC, int aD) {
        setCurrentP0Mask(aA, aB, aC, aD);
        debugLockP0Mask=true;
    }
    
    public void debugSetP1Mask(int aA, int aB, int aC, int aD) {
        setCurrentP1Mask(aA, aB, aC, aD);
        debugLockP1Mask=true;
    }
    
    public String debugDumpRegs() {
        StringBuffer zSB=new StringBuffer();
        zSB.append("ENAM0=" + bool(myTIAPokeRegister[ENAM0] & BIT1) + "; NUSIZ0=" + dbgHex(myTIAPokeRegister[NUSIZ0]) + "; ENAM1=" + bool(myTIAPokeRegister[ENAM1] & BIT1) + "; NUSIZ1=" + dbgHex(myTIAPokeRegister[NUSIZ1]) + "\n");
        // zSB.append("ENAM0=" + bool(myTIAPokeRegister[ENAM0] & BIT1) + "\n");
        zSB.append("CurrentM0Mask : alignment=" + myCurrentM0Mask[0] + ", num=" + myCurrentM0Mask[1] + ", size=" + myCurrentM0Mask[2] + ", x=" + myCurrentM0Mask[3] + "\n");
        zSB.append("CurrentM1Mask : alignment=" + myCurrentM1Mask[0] + ", num=" + myCurrentM1Mask[1] + ", size=" + myCurrentM1Mask[2] + ", x=" + myCurrentM1Mask[3] + "\n");
        zSB.append("CurrentP0Mask : alignment=" + myCurrentP0Mask[0] + ", num=" + myCurrentP0Mask[1] + ", size=" + myCurrentP0Mask[2] + ", x=" + myCurrentP0Mask[3] + "\n");
        zSB.append("CurrentP1Mask : alignment=" + myCurrentP1Mask[0] + ", num=" + myCurrentP1Mask[1] + ", size=" + myCurrentP1Mask[2] + ", x=" + myCurrentP1Mask[3] + "\n");
        
        zSB.append("M0Pos=" + myPOSM0 + "; M1Pos=" + myPOSM1 + "\n");
        zSB.append("P0Pos=" + myPOSP0 + "; P1Pos=" + myPOSP1 + "\n");
        zSB.append("COLUBK=0x" + Integer.toHexString(myTIAPokeRegister[COLUBK]) + "\n");
        return zSB.toString();
    }
    public void debugPoke(int aAddr, int aValue) {
        debugStraightPoke=true;
        poke((char)aAddr, aValue);
        debugStraightPoke=false;}
    
    public String dbgHex(int aNum) {
        return "0x"+ Integer.toHexString(aNum);
    }
    
    //^^^^^^^^^^ DEBUG STOP ^^^^^^^^^^^^^^^^^^^^
    
    
    
    
    
    
    
    
    
    
    
    
}
