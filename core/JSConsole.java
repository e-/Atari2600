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
// $Id: JSConsole.java,v 1.19 2007/09/24 00:31:14 mauvila Exp $
//============================================================================
package jstella.core;

/*
 *For users - new features:
 *
 *
 *
 *
 *
 ** Since version 0.7:
 *   -unofficial opcode support : rla
 *   -can manually specify cartridge type
 *   -can manually specify display height
 *   -can lock applet in paddle mode
 *   -fixed CPU bugs
 *   -now recognizes .a26 files
 *   -multiple applets (main + options applet) are available
 *   -JavaScript can act with applet
 *   -now uses user's machine to help effect graphics optimization
 * Note : games (states) saved in version 0.7 are not compatible with versions 0.75+.
 *
 ** Since version 0.75:
 *   -Support for FASC cartridges added
 *   -Saved games are now smaller (they were about 1000k, now around 15k - 100k)
 *   -Booster grip support
 *   -A little more graphics optimization
 */

/*
 *For developers - changes:
 ** Since version 0.7:
 *     -(see new features)
 *     -got rid of 'char' variables in 6507 class...now int
 *     -changed name of SuperController to JSController, got rid of IfcController
 *     -added a Toolkit.sync() command to the Canvas paint method, don't know if this helps/hurts
 *     -fixed J6507 bug where stack pointer was not being treated as 8 bits
 *     -public applet methods for JavaScript interface
 *     -now using createCompatibleImage() instead of creating a BufferedImage directly (for back buffer)
 *     
 *
 * Since version 0.75:
 *     -cleaned up JSTIA a good bit
 *     -for eligible BufferedImage types, writes straight to BackBuffer's data array (JSVideo)
 */

/*
 *CONVENTIONS for JStella :
 * (All of these are optional, but some are recommended, and some are listed just so that the reader
 * understand what is what (FYI).  And everything here of course is open to debate.  And remember,
 * it's better to contribute and not use the conventions than not to contribute at all.)
 *
 * Recommended :
 *  Variables for a class should be private and begin with "my".  The accessor methods
 *   should usually have a verb in the name (e.g. get, set) and should not include the word "my".
 *   e.g. private int myInteger=0;
 *        public int getInteger();
 *
 *   Constants (i.e. final static variables) should be named using capital letters and underscores.
 *
 * FYI (purely optional) :
 *  Argument variables begin with 'a' (argument).
 *  Temporary variables (those declared inside a method) begin with 'z' (zeitweilig).
 *
 */


import java.io.*;
import javax.swing.*;

import jstella.cart.Cartridge;
import jstella.j6507.*;

import static jstella.core.JSConstants.*;
//TODO : don't serialize JSVideo

/**
 * The console - the actual 2600 case.  This class encompasses the other emulation
 * classes.
 * 
 * <p>
 * Consult the "Stella Programmer's Guide" (Steve Wright, 1979) for more information.
 * This guide was written back in the 1970s and was used originally by people who programmed
 * games for the Atari 2600.  It is widely available on the Internet.
 * 
 * <ul>The console will contain:
 * <li>1 TIA chip (TIA) - graphics/sound creator, also manages analog (paddle) controllers
 * <li>1 RIOT chip (JSRiot) - provides the RAM, some timers, and manages digital (joystick) controllers
 * <li>1 CPU (contained within a JSSystem object)
 * <li>1 ROM (Cartridge) - the game to play
 * </ul>
 * 
 * <p>The console (and thus the emulator) is run by having an external class repeatedly
 * call doFrame().  Each call represents a frame, and typically there are about 50-60 frames/sec.</p>
 * 
 * <p>
 *     Here is the core emulation in a nutshell:
 *     Some kind of GUI class has a timer in it that is set to execute once every 17 milliseconds
 *     (approximately 60 times per minute).  (For the standard NTSC games, that is.)  So 60 times per second,
 *     the doFrame() method in JSConsole is called.  This method renders a visual frame of the animation,
 *     and performs any type of calculations/"game logic" that goes with it.
 * </p> 
 * <p> 
 * The doFrame() method will do three main things.  First, it will have the JSTIA class execute
 * the CPU, which reads the ROM instructions...the CPU will in turn, based upon the instructions it 
 * reads, send data to the TIA (mostly) which tell the TIA what to draw.  The TIA changes the 
 * pixel data in the JSVideo class.  (The CPU also sends audio data to the TIA, which forwards it
 * to the JSAudio object.)  When the CPU reaches the instruction that signals the end
 * of a visual frame, the TIA class quits running the CPU.  Second, the doFrame method tells the 
 * JSVideo to display its newly arranged visual data...i.e. paint it to the canvas.  Third, the 
 * doFrame() method tells JSAudio to play a sound based on the sound data that the CPU/ROM sent to the
 * TIA class. <br>
 * So that's the emulation in a nutshell.
 *     
 * </p>
 * @author Bradford W. Mott and the Stella team (original)
 * J.L. Allen (Java translation)
 */
public class JSConsole implements java.io.Serializable {
    private final static long serialVersionUID = -2643184369415869796L;
    
   // public final static boolean DEBUG_MODE_ON = true;
    

    public final static int DEFAULT_YSTART=34;
    public final static int DEFAULT_DISPLAY_HEIGHT=210;
    public final static int DEFAULT_DISPLAY_WIDTH=CLOCKS_PER_LINE_VISIBLE;
    
    private final static int TRASH_FRAMES=60; //used in detection of display height/display type
    
    private int myFrameRate=60;
    private DisplayFormat myDisplayFormat=DisplayFormat.NTSC;
    
    
    private int myDisplayHeight=DEFAULT_DISPLAY_HEIGHT;
    private int myDisplayWidth=DEFAULT_DISPLAY_WIDTH;
    private int myYStart=DEFAULT_YSTART;
    
    private transient IfcConsoleClient myConsoleClient=null;
    
    private JSController[] myControllers=new JSController[2];
    
    
    
    
    private int mySwitches=0xFF;
    
    
    private JSTIA myTIA=null;
    private jstella.core.JSSystem mySystem=null;
    private Cartridge myCart=null;
    private JSRiot myRiot=null;
    private JSVideo myVideo=null;
    private transient JSAudio myAudio=null; //transient - therefore, not stored in a "saved game"
    
    
   
    
    
    
    
 
    
    private int myTelevisionMode=TELEVISION_MODE_OFF;
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public JSConsole(IfcConsoleClient aConsoleClient) {
        setConsoleClient(aConsoleClient);
        //   myUserPaletteDefined=false;
        
        
        
        initializeAudio();
        initializeVideo();
        
        flipSwitch(ConsoleSwitch.SWITCH_RESET, false);
        flipSwitch(ConsoleSwitch.SWITCH_SELECT, false);
        flipSwitch(ConsoleSwitch.SWITCH_BW, false);
        flipSwitch(ConsoleSwitch.SWITCH_DIFFICULTY_P0, false); //amateur setting
        flipSwitch(ConsoleSwitch.SWITCH_DIFFICULTY_P1, false); //amateur setting
        
        myControllers[0] = new JSController(Jack.LEFT);
        myControllers[1] = new JSController(Jack.RIGHT);
        
        mySystem = new JSSystem(this);
        myRiot = new JSRiot(this);
        myTIA = new JSTIA(this);
        
        mySystem.attach(myRiot);
        mySystem.attach(myTIA);
        
    }
    
    /**
     *
     * @param in This is a method called by the JVM system whenever this object
     * is deserialized.  It serves as sort of an alternate constructor,
     * one that is used in place of the normal one.
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in)  throws IOException, ClassNotFoundException {
        double zVersion=in.readDouble(); //Read the manually written JStella version number from stream
       //TODO : make sure version being read is not GREATER than version being used
        in.defaultReadObject();
        
        Object zAudioArrayObj=in.readUnshared();
       
        
        initializeAudio(); //myAudio is transient, so this must create a new one when it loads
         if (zAudioArrayObj instanceof int[])
         {
            int[] zAudioRegisters=(int[])zAudioArrayObj;
            myAudio.setAudioRegisterData(zAudioRegisters);
           // System.out.println("debug : reading audio data- " + zAudioRegisters[1] + ", " + zAudioRegisters[3] + "," + zAudioRegisters[5] );
     
         }//end : is int[]
        adjustBackBuffer();
    }
    
     private void writeObject(ObjectOutputStream out) throws IOException
    {
      out.writeDouble(JSConstants.JSTELLA_VERSION_NUMBER);   //First, manually write JStella version number to stream
      out.defaultWriteObject();
      int[] zAudioRegisters=myAudio.getAudioRegisterData();
      out.writeUnshared(zAudioRegisters);
    //  System.out.println("debug : writing audio data- " + zAudioRegisters[1] + ", " + zAudioRegisters[3] + "," + zAudioRegisters[5] );
     } 
    
    
    
    
    /**
     * The console should (must) be destroyed right before the object is no longer used,
     * e.g. when loading a serialized console from a stream.  Destroying the console
     * will free up the audio resources that the audio object has reserved.
     */
    public void destroy() {
        if (myAudio!=null) {
            myAudio.close();
            myAudio=null;
        }//end : not null
    }
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private void detectDisplayHeight() throws JSException
    {
       mySystem.reset();
        for (int i=0; i<TRASH_FRAMES; i++)
        {           
            myTIA.processFrame();          
        }
        
       // System.out.println("debug : myDetectedYStop=" + myTIA.getDetectedYStop() + ", myDetectedYStart=" + myTIA.getDetectedYStart());
        myDisplayHeight=myTIA.getDetectedYStop() - myTIA.getDetectedYStart(); //getVBlankOn() - myTIA.getVBlankOff();
        if (myDisplayHeight<=0) myDisplayHeight += myTIA.getVSyncOn();
        myDisplayHeight=Math.min(myDisplayHeight, FRAME_Y_MAX);
        
        if (myDisplayHeight < FRAME_Y_MIN)
        {
            //TODO : make sure this doesn't happen
            myDisplayHeight=220;
            myYStart=34;
            
            System.out.println("Warning: JStella was unable to detect the proper frame height");
           // assert(false);
        }//end : less than the min
        
        myYStart=myTIA.getDetectedYStart();
       // System.out.println("Detected display dimensions: yStart=" + myYStart + ", display height=" + myDisplayHeight);
        
        
        if ((myDisplayFormat == DisplayFormat.PAL) && (myDisplayHeight ==210)) myDisplayHeight=250;
        
        adjustBackBuffer(); 
    }
    
    private void detectDisplayFormat() throws JSException {
        // Run the system for 60 frames, looking for PAL scanline patterns
        // We assume the first 30 frames are garbage, and only consider
        // the second 30 (useful to get past SuperCharger BIOS)
        // Unfortunately, this means we have to always enable 'fastscbios',
        // since otherwise the BIOS loading will take over 250 frames!
        mySystem.reset();
        
        int zPalCount = 0;
        
        
        for (int i=0; i<TRASH_FRAMES; i++)
        {
            myTIA.processFrame();
        }
        
        for (int i=0; i<30; i++)
        {           
            myTIA.processFrame();
           //  System.out.println("Debug : scan lines=" + myTIA.scanlines());
            if(myTIA.scanlines() > 285) 
            {
                ++zPalCount;
               
            }//end : >285 lines
        }
        
        if (zPalCount >= 15) setDisplayFormat(DisplayFormat.PAL);
        else setDisplayFormat(DisplayFormat.NTSC);
       
        
       // System.out.println("Display format = " + myDisplayFormat + ", display height=" + myDisplayHeight);
        
    }
    
    public void changeYStart(int aNewYStart)
    {
       if (aNewYStart!=myYStart)
       {
           myYStart=aNewYStart;
           myTIA.frameReset();
       }//end : new value
    }
    
    public void changeDisplayHeight(int aNewHeight)
    {
         if (aNewHeight!=myDisplayHeight)
       {
           myDisplayHeight=aNewHeight;
           adjustBackBuffer();
           myVideo.refresh();
           myTIA.frameReset();
       }//end : new value
    }
    
    private void adjustBackBuffer()
    {
        getVideo().adjustBackBuffer(JSVideo.DEFAULT_WIDTH, myDisplayHeight);
    }
    
   
    
    public int getDisplayWidth() { return myDisplayWidth; }
    public int getDisplayHeight() { return myDisplayHeight; }
    public int getYStart() {    return myYStart; }
    
    public void setConsoleClient(IfcConsoleClient aConsoleClient) { myConsoleClient=aConsoleClient; }
    public IfcConsoleClient getConsoleClient() { return myConsoleClient;}
    
    
    public JSController getController(Jack jack) {return (jack == Jack.LEFT) ? myControllers[0] : myControllers[1];}
    
    public JSTIA getTIA() { return myTIA; }
    public JSVideo getVideo() {    return myVideo;  }
    public JSAudio getAudio()   {   return myAudio;  }
    public jstella.core.JSSystem getSystem()  { return mySystem; }
    public Cartridge getCartridge()  { return myCart; }
    public JSRiot getRiot() { return myRiot; }
    
    public   int getNominalFrameRate() {
        // Set the correct framerate based on the format of the ROM
        // This can be overridden by changing the framerate in the
        // VideoDialog box or on the commandline, but it can't be saved
        // (ie, framerate is now solely determined based on ROM format).
        // int framerate = myOSystem.settings().getInt("framerate");
        // if(framerate == -1) {
        return myFrameRate;
                
    }
    
    public void setNominalFrameRate(int aFrameRate)
    {
        myFrameRate=aFrameRate;
        getAudio().setNominalDisplayFrameRate(aFrameRate);
    }
    
    
  public DisplayFormat getDisplayFormat()
  {
      return myDisplayFormat;
  }
    
  private   void setDisplayFormat(DisplayFormat aDisplayFormat) {
        myDisplayFormat=aDisplayFormat;
        setNominalFrameRate(aDisplayFormat.getDisplayRate());
        getVideo().setTIAPalette(aDisplayFormat.getDisplayPalette());
      
         mySystem.reset();
  
        
       
    }

   private void reinstallCore()
   {
        mySystem.attach(myRiot);
        mySystem.attach(myTIA);
   }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    private void initializeVideo() {
        if(myVideo==null) myVideo = new JSVideo(this);
        
        //setColorLossPalette(false);
        getVideo().initialize();
        
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private  void initializeAudio() {
        if (myAudio==null) myAudio = new JSAudio(this);
        else myAudio.close(); //Closes any open audio resources
        
        getAudio().setNominalDisplayFrameRate(getNominalFrameRate());
        getAudio().initialize();
    }
    
    
    
    
    public void setTelevisionMode(int aTelevisionMode)
    {
        myTelevisionMode=aTelevisionMode;
    }
    
    public int getTelevisionMode()
    {
        return myTelevisionMode;
    }
    
    // ===================== Cartridge related methods ============================
    
    public void insertCartridge(Cartridge aCart) throws JSException
    {
        insertCartridge(aCart, -1);
    }
    
    public void insertCartridge(Cartridge aCart, int aDisplayHeight) throws JSException {
        if ((myCart!=null)&&(myCart!=aCart)) {
            mySystem.unattach(myCart);
            reinstallCore();
        }//end : previous cartridge is being replaced
        
        myVideo.clearBackBuffer();
        myVideo.clearBuffers();
        myCart=aCart;
        myCart.setConsole(this);
        
        mySystem.attach(myCart);
        mySystem.reset();
        //myDisplayFormat = DEFAULT_DISPLAY_FORMAT;//myProperties.get(Properties.PropertyType.Display_Format);
        detectDisplayFormat();
        
        if (aDisplayHeight<=0) detectDisplayHeight();
        else myDisplayHeight=aDisplayHeight;
        adjustBackBuffer();
        
        
        // Make sure height is set properly for PAL ROM
       
        setTelevisionMode(TELEVISION_MODE_GAME);
        // Reset, the system to its power-on state
        mySystem.reset();
        System.out.println("JStella display: YStart=" + myYStart + ", DisplayHeight=" + myDisplayHeight);
    }
    
    
    
    
    public static Cartridge createCartridge(java.io.InputStream aInputStream, String aCartridgeType) throws JSException {
         Cartridge zCart=null;
        try{
            if (aInputStream!=null) {
                
                byte[] zROMData=readByteArrayFromStream(aInputStream);
                if (zROMData!=null) {
                    
                    if (aCartridgeType==null) zCart=Cartridge.create(zROMData);
                    else zCart=Cartridge.create(zROMData, aCartridgeType);
                    
                }//end : ROMData not null
                else {
                    throw new JSException(JSException.ExceptionType.IO, "Could not read stream");
                    
                }//end : ROMData is null
                
            }//end : stream not null
            else {
                System.out.println("JSTELLA ERROR : attempting to read from a null stream");
            }//end : stream is null
        }//end : try
        catch (java.io.IOException e) {
            throw new JSException(JSException.ExceptionType.IO, "Could not load ROM");
        }
        
   return zCart;
    }
    
    public static Cartridge createCartridge(java.io.InputStream aInputStream) throws JSException {
       return createCartridge(aInputStream, null);
        
    }
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
    
    
    
    private static byte[] readByteArrayFromStream(java.io.InputStream aStream) throws IOException {
        byte[] zReturn=null;
        java.io.ByteArrayOutputStream zBAOS=new java.io.ByteArrayOutputStream();
        int zInt=0;
        while ((zInt = aStream.read()) != -1) {
            zBAOS.write(zInt);
        }//end : while loop
        zBAOS.close();
        zReturn=zBAOS.toByteArray();
        return zReturn;
    }//::
    
    
    // ====================== MAIN METHOD ========================
    
    
    /**
     * This is the main method of the class.  This method should be called
     * by the "outside" GUI object for every intended frame...that is,
     * the runner object should call this 50-60 times/sec, depending on
     * what the designated frame rate is.
     * @throws jstella.core.JSException
     */
    public synchronized void doFrame() throws JSException {
        //profiling note - Sep 3 2007 - it seems that a lot of times, whenever doFrame() lasts long (e.g. 59 milliseconds), that 
        //  the processFrame is taking up most of the time
        
        if (myVideo!=null)
        {   
         
        if (getTelevisionMode()==TELEVISION_MODE_GAME)
        {
            if (myCart!=null) 
            {
          //   long zTimeA=System.nanoTime();
            myTIA.processFrame();    
         //   long zTimeC=System.nanoTime();
            myVideo.doFrameVideo();
            myAudio.doFrameAudio(mySystem.getCycles(), getNominalFrameRate());
          //    long zTimeB=System.nanoTime();
   /*            //if (JSConsole.DEBUG_MODE_ON==true)
         {
            int zDeltaBA=(int)(zTimeB - zTimeA) / 1000;
              int zDeltaCA=(int)(zTimeC - zTimeA) / 1000;
            System.out.println("debug JSConsole : doFrame=" + zDeltaBA + " microseconds, processFrame=" + zDeltaCA + " microsec");
        }//end : debug mode on
    */
       
       
        
       
            }//end : cartridge loaded
           
        }
        else if (getTelevisionMode()==TELEVISION_MODE_SNOW)
        {
            myVideo.doSnow();
        }//end : not snow
        else if (getTelevisionMode()==TELEVISION_MODE_TEST_PATTERN)
        {
           myVideo.doTestPattern();  
        }//end : test pattern
        
        }//end : video not null
        else  {
            System.out.println("JStella Error : cannot animate");
            System.exit(1);
        }//end : myVideo is null
        
    }
    
    public synchronized void updateVideoFrame()
    {
        if (myVideo!=null)
        {
            myVideo.updateVideoFrame();  
            
        }
        
    }
   
    
    
    
    
    
    
    
    
    //================= JSConsole Switches =====================
    
    
    public  int readSwitches() {
        return mySwitches;
    }
    
    
    
    
    /**
     * Flips a console switch.
     * Switch down is equivalent to setting the bit to zero.
     * See the SWITCH constants in the JSConsole class.
     * <p>
     * RESET - down to reset
     * SELECT - down to select
     * BW - down to change into black and white mode
     * DIFFICULTY P0 - down to set player 0 to easy
     * DIFFICULTY P1 - down to set player 1 to easy
     *
     * </p>
     * @param aSwitchType what switch to flip (see SWITCH constants in JSConstants)
     * @param aSwitchDown true if the switch should be down (see method description for details)
     */
    public  void flipSwitch(ConsoleSwitch aSwitchType, boolean aSwitchDown) {
        
        if (aSwitchDown) mySwitches &= ~aSwitchType.getBitMask();
        else mySwitches |= aSwitchType.getBitMask();
        
     
    }
    
    public boolean isSwitchOn(ConsoleSwitch aSwitch)
    {
        return ((mySwitches & aSwitch.getBitMask())==0); //in this case, a bit value of zero means 'on'
    }
    
    
    
    //================ GUI Options ==================================================
    
    public void setPhosphorEnabled(boolean aEnable)
    {
        getVideo().setPhosphorEnabled(aEnable);
    }
    
    public boolean isPhosphorEnabled()
    {
        return getVideo().getPhosphorEnabled();
    }
    
    public void setStereoSound(boolean aEnable)
    {
        if (aEnable==true) getAudio().setChannelNumber(2);
        else getAudio().setChannelNumber(1);
        
    }
    
    public boolean isStereoSound()
    {
        return (getAudio().getChannelNumber()==2);
    }
    
    public void setSoundEnabled(boolean aEnabled)
    {
        getAudio().setSoundEnabled(aEnabled);
    }
    
    public boolean isSoundEnabled()
    {
        return getAudio().isSoundEnabled();
    }
    
    public void grayCurrentFrame()
    {
        getVideo().grayCurrentFrame();
    }
    
    public void pauseAudio() { getAudio().pauseAudio(); }
    
    
 
    
    //Misc tasks
    
    //TODO : Figure out if this setup (using java.util.Timer) is thread safe
    //TODO : Fix letter box mode...junk keeps appearing on margins (Sep 7 2007)
    
   //TODO : find out what causes the processFrame() to occasionally last about 60 ms (seen during slowdown)
                     //Update : possibly the processFrame() is blocking when accessing the back buffer because of draw
    
    //TODO : Make Berenstein bears work, assuming the flaw is with the emulator
    //TODO : Fix flaws in emulator that cause AIR-RAID to act funny
    
    
    //Misc bugs
  
    
    
    
    
    
    public void debugDoFrame()
    {
        try{
        doFrame();
        }//end : try
        catch (Exception e)
        {
            e.printStackTrace();
        }        
    }
    
    public void debugProcessFrame()
    {
        try{
        myTIA.processFrame();
        }//end : try
        catch (Exception e)
        {
            e.printStackTrace();
        }        
    }
    
     public void debugDoFrameVideo()
    {
        try{
        myVideo.doFrameVideo();//.processFrame();
        }//end : try
        catch (Exception e)
        {
            e.printStackTrace();
        }        
    }
     
     
     //========================================================================
    
     
    
   
    
    
}
