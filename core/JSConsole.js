function JSConsole (aConsoleClient) {
  this.serialVersionUID = 1;
  this.DEFAULT_YSTART = 34;
  this.DEFAULT_DISPLAY_HEIGHT = 210;
  this.DEFAULT_DISPLAY_WIDTH = this.CLOCKS_PER_LINE_VISBILE;
  this.TRASH_FRAMES =60;

  this.myFrameRate = 60;
  this.myDisplayFormat; // TODO
  this.myStart = this.DEFAUKT_YSTART;
  this.myConsoleClient = null; // TODO
  this.myController = null ; // TODO

  this.mySwitches = 0xFF;
  this.mySystem = null; // TODO
  this.myCart = null; // TODO
  this.myRiot = null; // TODO
  this.myVideo = null; // TODO
  this.myAudio = null; // TODO

  this.myTelevisionMode = this.TELEVISION_MODE_OFF;

 // this.JSConsole = function (aConsoleClient) {
//    this.setConsoleClient(aConsoleClient);
//    this.initializeAudio();
//    this.initializeVideo();
    // ... 
  //}


    /**
     *
     * @param in This is a method called by the JVM system whenever this object
     * is deserialized.  It serves as sort of an alternate constructor,
     * one that is used in place of the normal one.
    * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
//    private void readObject(java.io.ObjectInputStream in)  throws IOException, ClassNotFoundException {
//        double zVersion=in.readDouble(); //Read the manually written JStella version number from stream
//       //TODO : make sure version being read is not GREATER than version being used
//        in.defaultReadObject();
//        
//        Object zAudioArrayObj=in.readUnshared();
//       
//        
//        initializeAudio(); //myAudio is transient, so this must create a new one when it loads
//         if (zAudioArrayObj instanceof int[])
//         {
//            int[] zAudioRegisters=(int[])zAudioArrayObj;
//            myAudio.setAudioRegisterData(zAudioRegisters);
//           // System.out.println("debug : reading audio data- " + zAudioRegisters[1] + ", " + zAudioRegisters[3] + "," + zAudioRegisters[5] );
//     
//         }//end : is int[]
//        adjustBackBuffer();
//    }
//    
//     private void writeObject(ObjectOutputStream out) throws IOException
//    {
//      out.writeDouble(JSConstants.JSTELLA_VERSION_NUMBER);   //First, manually write JStella version number to stream
//      out.defaultWriteObject();
//      int[] zAudioRegisters=myAudio.getAudioRegisterData();
//      out.writeUnshared(zAudioRegisters);
//    //  System.out.println("debug : writing audio data- " + zAudioRegisters[1] + ", " + zAudioRegisters[3] + "," + zAudioRegisters[5] );
//     } 
//    
//    
//    
//    
//    /**
//     * The console should (must) be destroyed right before the object is no longer used,
//     * e.g. when loading a serialized console from a stream.  Destroying the console
//     * will free up the audio resources that the audio object has reserved.
//     */
//    public void destroy() {
//        if (myAudio!=null) {
//            myAudio.close();
//            myAudio=null;
//        }//end : not null
//    }
//    
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    private void detectDisplayHeight() throws JSException
//    {
//       mySystem.reset();
//        for (int i=0; i<TRASH_FRAMES; i++)
//        {           
//            myTIA.processFrame();          
//        }
//        
//       // System.out.println("debug : myDetectedYStop=" + myTIA.getDetectedYStop() + ", myDetectedYStart=" + myTIA.getDetectedYStart());
//        myDisplayHeight=myTIA.getDetectedYStop() - myTIA.getDetectedYStart(); //getVBlankOn() - myTIA.getVBlankOff();
//        if (myDisplayHeight<=0) myDisplayHeight += myTIA.getVSyncOn();
//        myDisplayHeight=Math.min(myDisplayHeight, FRAME_Y_MAX);
//        
//        if (myDisplayHeight < FRAME_Y_MIN)
//        {
//            //TODO : make sure this doesn't happen
//            myDisplayHeight=220;
//            myYStart=34;
//            
//            System.out.println("Warning: JStella was unable to detect the proper frame height");
//           // assert(false);
//        }//end : less than the min
//        
//        myYStart=myTIA.getDetectedYStart();
//       // System.out.println("Detected display dimensions: yStart=" + myYStart + ", display height=" + myDisplayHeight);
//        
//        
//        if ((myDisplayFormat == DisplayFormat.PAL) && (myDisplayHeight ==210)) myDisplayHeight=250;
//        
//        adjustBackBuffer(); 
//    }
//    
//    private void detectDisplayFormat() throws JSException {
//        // Run the system for 60 frames, looking for PAL scanline patterns
//        // We assume the first 30 frames are garbage, and only consider
//        // the second 30 (useful to get past SuperCharger BIOS)
//        // Unfortunately, this means we have to always enable 'fastscbios',
//        // since otherwise the BIOS loading will take over 250 frames!
//        mySystem.reset();
//        
//        int zPalCount = 0;
//        
//        
//        for (int i=0; i<TRASH_FRAMES; i++)
//        {
//            myTIA.processFrame();
//        }
//        
//        for (int i=0; i<30; i++)
//        {           
//            myTIA.processFrame();
//           //  System.out.println("Debug : scan lines=" + myTIA.scanlines());
//            if(myTIA.scanlines() > 285) 
//            {
//                ++zPalCount;
//               
//            }//end : >285 lines
//        }
//        
//        if (zPalCount >= 15) setDisplayFormat(DisplayFormat.PAL);
//        else setDisplayFormat(DisplayFormat.NTSC);
//       
//        
//       // System.out.println("Display format = " + myDisplayFormat + ", display height=" + myDisplayHeight);
//        
//    }
//    
//    public void changeYStart(int aNewYStart)
//    {
//       if (aNewYStart!=myYStart)
//       {
//           myYStart=aNewYStart;
//           myTIA.frameReset();
//       }//end : new value
//    }
//    
//    public void changeDisplayHeight(int aNewHeight)
//    {
//         if (aNewHeight!=myDisplayHeight)
//       {
//           myDisplayHeight=aNewHeight;
//           adjustBackBuffer();
//           myVideo.refresh();
//           myTIA.frameReset();
//       }//end : new value
//    }
//    
//    private void adjustBackBuffer()
//    {
//        getVideo().adjustBackBuffer(JSVideo.DEFAULT_WIDTH, myDisplayHeight);
//    }
//    
//   
//    
//    public int getDisplayWidth() { return myDisplayWidth; }
//    public int getDisplayHeight() { return myDisplayHeight; }
//    public int getYStart() {    return myYStart; }
//    
    	this.setConsoleClient = function(aConsoleClient) { myConsoleClient=aConsoleClient; }
//    public IfcConsoleClient getConsoleClient() { return myConsoleClient;}
//    
//    
//    public JSController getController(Jack jack) {return (jack == Jack.LEFT) ? myControllers[0] : myControllers[1];}
//    
//    public JSTIA getTIA() { return myTIA; }
//    public JSVideo getVideo() {    return myVideo;  }
//    public JSAudio getAudio()   {   return myAudio;  }
//    public jstella.core.JSSystem getSystem()  { return mySystem; }
//    public Cartridge getCartridge()  { return myCart; }
//    public JSRiot getRiot() { return myRiot; }
//    
//    public   int getNominalFrameRate() {
//        // Set the correct framerate based on the format of the ROM
//        // This can be overridden by changing the framerate in the
//        // VideoDialog box or on the commandline, but it can't be saved
//        // (ie, framerate is now solely determined based on ROM format).
//        // int framerate = myOSystem.settings().getInt("framerate");
//        // if(framerate == -1) {
//        return myFrameRate;
//                
//    }
//    
//    public void setNominalFrameRate(int aFrameRate)
//    {
//        myFrameRate=aFrameRate;
//        getAudio().setNominalDisplayFrameRate(aFrameRate);
//    }
//    
//    
//  public DisplayFormat getDisplayFormat()
//  {
//      return myDisplayFormat;
//  }
//    
//  private   void setDisplayFormat(DisplayFormat aDisplayFormat) {
//        myDisplayFormat=aDisplayFormat;
//        setNominalFrameRate(aDisplayFormat.getDisplayRate());
//        getVideo().setTIAPalette(aDisplayFormat.getDisplayPalette());
//      
//         mySystem.reset();
//  
//        
//       
//    }
//
//   private void reinstallCore()
//   {
//        mySystem.attach(myRiot);
//        mySystem.attach(myTIA);
//   }
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    
    	this.initializeVideo = function() {
//        if(myVideo==null) myVideo = new JSVideo(this);
//        
//        //setColorLossPalette(false);
//        getVideo().initialize();
//        
      }
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    private  void initializeAudio() {
//        if (myAudio==null) myAudio = new JSAudio(this);
//        else myAudio.close(); //Closes any open audio resources
//        
//        getAudio().setNominalDisplayFrameRate(getNominalFrameRate());
//        getAudio().initialize();
//    }
//    
//    
//    
//    
    this.setTelevisionMode = function(aTelevisionMode)
    {
			this.myTelevisionMode=aTelevisionMode;
    }
    
    this.getTelevisionMode = function()
    {
        return this.myTelevisionMode;
    }
//    
//    // ===================== Cartridge related methods ============================
//    
	    
    
    this.insertCartridge2 = function(aCart, aDisplayHeight){
        if ((this.myCart!=null)&&(this.myCart!=aCart)) {
//TODO            mySystem.unattach(myCart);
//TODO            reinstallCore();
        }//end : previous cartridge is being replaced
        
 //TODO       myVideo.clearBackBuffer();
 //       myVideo.clearBuffers();
 //       myCart=aCart;
 //       myCart.setConsole(this);
        
 //       mySystem.attach(myCart);
 //       mySystem.reset();
        //myDisplayFormat = DEFAULT_DISPLAY_FORMAT;//myProperties.get(Properties.PropertyType.Display_Format);
  //      detectDisplayFormat();
        
  //      if (aDisplayHeight<=0) detectDisplayHeight();
  //      else myDisplayHeight=aDisplayHeight;
  //      adjustBackBuffer();
        
        
        // Make sure height is set properly for PAL ROM
       
  //      setTelevisionMode(TELEVISION_MODE_GAME);
        // Reset, the system to its power-on state
  //      mySystem.reset();
       	console.log("JStella display: YStart=" + this.myYStart + ", DisplayHeight=" + this.myDisplayHeight);
    }
    
    
		this.insertCartridge = function (aCart)
  	  {
	        this.insertCartridge2(aCart, -1);
	    }

    this.createCartridge = function(aInputStream, aCartridgeType) {
        var zCart=null;
				//try
				{
            if (aInputStream!=null) {
                //zROMData = readByteArrayFromStream(aInputStream);
								zROMData = aInputStream;

                if (zROMData!=null) {
                    
										cart = new Cartridge();

                    if (aCartridgeType==null) zCart=cart.create(zROMData);
                    else zCart=cart.create(zROMData, aCartridgeType);
                    
                }//end : ROMData not null
                else {
                    console.log("Could not read stream");
                    
                }//end : ROMData is null
                
            }//end : stream not null
            else {
                console.log("JSTELLA ERROR : attempting to read from a null stream");
            }//end : stream is null
        }//end : try
//        catch (e) {
//            console.log("Could not load ROM");
//        }
        
			  return zCart;
    }
    
    /*public static Cartridge createCartridge(java.io.InputStream aInputStream) throws JSException {
       return createCartridge(aInputStream, null);
        
    }*/
//    
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    
//    
//    
//    
//    
//    private static byte[] readByteArrayFromStream(java.io.InputStream aStream) throws IOException {
//        byte[] zReturn=null;
//        java.io.ByteArrayOutputStream zBAOS=new java.io.ByteArrayOutputStream();
//        int zInt=0;
//        while ((zInt = aStream.read()) != -1) {
//            zBAOS.write(zInt);
//        }//end : while loop
//        zBAOS.close();
//        zReturn=zBAOS.toByteArray();
//        return zReturn;
//    }//::
//    
//    
//    // ====================== MAIN METHOD ========================
//    
//    
//    /**
//     * This is the main method of the class.  This method should be called
//     * by the "outside" GUI object for every intended frame...that is,
//     * the runner object should call this 50-60 times/sec, depending on
//     * what the designated frame rate is.
//     * @throws jstella.core.JSException
//     */
    this.doFrame = function(){
        //profiling note - Sep 3 2007 - it seems that a lot of times, whenever doFrame() lasts long (e.g. 59 milliseconds), that 
        //  the processFrame is taking up most of the time
        
        if (this.myVideo!=null)
        {   
         
        if (this.getTelevisionMode()==TELEVISION_MODE_GAME)
        {
            if (this.myCart!=null) 
            {
            myTIA.processFrame();    
//            myVideo.doFrameVideo();
//            myAudio.doFrameAudio(mySystem.getCycles(), getNominalFrameRate());
            }//end : cartridge loaded
						else
							console.log("no cartridege in doFrame JSConsole")
           
        }
        else if (this.getTelevisionMode()==TELEVISION_MODE_SNOW)
        {
            myVideo.doSnow();
        }//end : not snow
        else if (this.getTelevisionMode()==TELEVISION_MODE_TEST_PATTERN)
        {
           myVideo.doTestPattern();  
        }//end : test pattern
        
        }//end : video not null
        else  {
            console.log("JStella Error : cannot animate");
        }//end : myVideo is null
        
    }
//    
//    public synchronized void updateVideoFrame()
//    {
//        if (myVideo!=null)
//        {
//            myVideo.updateVideoFrame();  
//            
//        }
//        
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
//    //================= JSConsole Switches =====================
//    
//    
//    public  int readSwitches() {
//        return mySwitches;
//    }
//    
//    
//    
//    
//    /**
//     * Flips a console switch.
//     * Switch down is equivalent to setting the bit to zero.
//     * See the SWITCH constants in the JSConsole class.
//     * <p>
//     * RESET - down to reset
//     * SELECT - down to select
//     * BW - down to change into black and white mode
//     * DIFFICULTY P0 - down to set player 0 to easy
//     * DIFFICULTY P1 - down to set player 1 to easy
//     *
//     * </p>
//     * @param aSwitchType what switch to flip (see SWITCH constants in JSConstants)
//     * @param aSwitchDown true if the switch should be down (see method description for details)
//     */
    this.flipSwitch = function(aSwitchType, aSwitchDown) {
        if (aSwitchDown) this.mySwitches &= ~aSwitchType.getBitMask();
        else this.mySwitches |= aSwitchType.getBitMask();
    }
    
    this.isSwitchOn = function(aSwitch)
    {
        return ((this.mySwitches & aSwitch.getBitMask())==0); //in this case, a bit value of zero means 'on'
    }
//    
//    
//    
//    //================ GUI Options ==================================================
//    
//    public void setPhosphorEnabled(boolean aEnable)
//    {
//        getVideo().setPhosphorEnabled(aEnable);
//    }
//    
//    public boolean isPhosphorEnabled()
//    {
//        return getVideo().getPhosphorEnabled();
//    }
//    
//    public void setStereoSound(boolean aEnable)
//    {
//        if (aEnable==true) getAudio().setChannelNumber(2);
//        else getAudio().setChannelNumber(1);
//        
//    }
//    
//    public boolean isStereoSound()
//    {
//        return (getAudio().getChannelNumber()==2);
//    }
//    
//    public void setSoundEnabled(boolean aEnabled)
//    {
//        getAudio().setSoundEnabled(aEnabled);
//    }
//    
//    public boolean isSoundEnabled()
//    {
//        return getAudio().isSoundEnabled();
//    }
//    
//    public void grayCurrentFrame()
//    {
//        getVideo().grayCurrentFrame();
//    }
//    
//    public void pauseAudio() { getAudio().pauseAudio(); }
//    
//    
// 
//    
//    //Misc tasks
//    
//    //TODO : Figure out if this setup (using java.util.Timer) is thread safe
//    //TODO : Fix letter box mode...junk keeps appearing on margins (Sep 7 2007)
//    
//   //TODO : find out what causes the processFrame() to occasionally last about 60 ms (seen during slowdown)
//                     //Update : possibly the processFrame() is blocking when accessing the back buffer because of draw
//    
//    //TODO : Make Berenstein bears work, assuming the flaw is with the emulator
//    //TODO : Fix flaws in emulator that cause AIR-RAID to act funny
//    
//    
//    //Misc bugs
//  
//    
//    
//    
//    
//    
//    public void debugDoFrame()
//    {
//        try{
//        doFrame();
//        }//end : try
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }        
//    }
//    
//    public void debugProcessFrame()
//    {
//        try{
//        myTIA.processFrame();
//        }//end : try
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }        
//    }
//    
//     public void debugDoFrameVideo()
//    {
//        try{
//        myVideo.doFrameVideo();//.processFrame();
//        }//end : try
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }        
//    }
//     
//     
//     //========================================================================
//    
//     
//    
//   
        this.setConsoleClient(aConsoleClient);
        //   myUserPaletteDefined=false;
        
        
        
       // this.initializeAudio();
        this.initializeVideo();
        
        this.flipSwitch(ConsoleSwitch.SWITCH_RESET, false);
        this.flipSwitch(ConsoleSwitch.SWITCH_SELECT, false);
        this.flipSwitch(ConsoleSwitch.SWITCH_BW, false);
        this.flipSwitch(ConsoleSwitch.SWITCH_DIFFICULTY_P0, false); //amateur setting
        this.flipSwitch(ConsoleSwitch.SWITCH_DIFFICULTY_P1, false); //amateur setting
        
        /*myControllers[0] = new JSController(Jack.LEFT);
        myControllers[1] = new JSController(Jack.RIGHT); */
        
        this.mySystem = new JSSystem(this);
        this.myRiot = new JSRiot(this);
        this.myTIA = new JSTIA(this);
        
        mySystem.attach(this.myRiot);
        mySystem.attach(this.myTIA);
}
