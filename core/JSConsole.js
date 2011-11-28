function JSConsole (aConsoleClient) {
  this.serialVersionUID = 1;
  this.DEFAULT_YSTART = 34;
  this.DEFAULT_DISPLAY_HEIGHT = 210;
  this.DEFAULT_DISPLAY_WIDTH = CLOCKS_PER_LINE_VISIBLE;
  this.TRASH_FRAMES =60;

  this.myFrameRate = 60;
 
 	this.myDisplayFormat=DisplayFormat.NTSC;
    
    
  this.myDisplayHeight=this.DEFAULT_DISPLAY_HEIGHT;
  this.myDisplayWidth=this.DEFAULT_DISPLAY_WIDTH;
  this.myYStart=this.DEFAULT_YSTART;
	(function(t){console.log(t);})(this);
    
  this.myConsoleClient=null;
    
  this.myControllers= [];
    
    
 
  this.mySwitches = 0xFF;
  this.mySystem = null; // TODO
  this.myCart = null; // TODO
  this.myRiot = null; // TODO
  this.myVideo = null; // TODO
  this.myAudio = null; // TODO

  this.myTelevisionMode = TELEVISION_MODE_OFF;

//    
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   	this.detectDisplayHeight = function()
    {
  	    this.mySystem.reset();
        for (var i=0; i<this.TRASH_FRAMES; i++)
        {           
            this.myTIA.processFrame();          
        }
        
        console.log("debug : myDetectedYStop=" + this.myTIA.getDetectedYStop() + ", myDetectedYStart=" + this.myTIA.getDetectedYStart());
        this.myDisplayHeight=this.myTIA.getDetectedYStop() - this.myTIA.getDetectedYStart(); //getVBlankOn() - myTIA.getVBlankOff();
        if (this.myDisplayHeight<=0) this.myDisplayHeight += this.myTIA.getVSyncOn();
        this.myDisplayHeight=Math.min(this.myDisplayHeight, FRAME_Y_MAX);
        
        if (this.myDisplayHeight < FRAME_Y_MIN)
        {
            //TODO : make sure this doesn't happen
            this.myDisplayHeight=220;
            this.myYStart=34;
            
            console.log("Warning: JStella was unable to detect the proper frame height");
           // assert(false);
        }//end : less than the min
        
        this.myYStart=this.myTIA.getDetectedYStart();
        
        
        if ((this.myDisplayFormat == DisplayFormat.PAL) && (this.myDisplayHeight ==210)) this.myDisplayHeight=250;
        
        this.adjustBackBuffer(); 
    }
//    
    this.detectDisplayFormat = function() {
        // Run the system for 60 frames, looking for PAL scanline patterns
        // We assume the first 30 frames are garbage, and only consider
        // the second 30 (useful to get past SuperCharger BIOS)
        // Unfortunately, this means we have to always enable 'fastscbios',
        // since otherwise the BIOS loading will take over 250 frames!
        this.mySystem.reset();
        
        var zPalCount = 0;
        
        
        for (var i=0; i<this.TRASH_FRAMES; i++)
        {
            this.myTIA.processFrame();
        }
        
        for (var i=0; i<30; i++)
        {           
            this.myTIA.processFrame();
            if(this.myTIA.scanlines() > 285) 
            {
                ++zPalCount;
               
            }//end : >285 lines
        }
        
        if (zPalCount >= 15) this.setDisplayFormat(DisplayFormat.PAL);
        else this.setDisplayFormat(DisplayFormat.NTSC);
       
        
       console.log("Display format = " + this.myDisplayFormat + ", display height=" + this.myDisplayHeight);
        
    }
			this.adjustBackBuffer = function()
    {
				var t = this.getVideo();
        t.adjustBackBuffer(t.DEFAULT_WIDTH, this.myDisplayHeight);
    }
//    
//   
//    
	    this.getDisplayWidth = function() { return this.myDisplayWidth; }	
	   	this.getDisplayHeight = function() { return this.myDisplayHeight; }
	    this.getYStart = function() {    return this.myYStart; }
//    
    	this.setConsoleClient = function(aConsoleClient) { this.myConsoleClient=aConsoleClient; }
			this.getConsoleClient = function() { return this.myConsoleClient;}
    	this.getController = function(jack) {return (jack == Jack.LEFT) ? this.myControllers[0] : this.myControllers[1];}
//    
			this.getTIA = function() { return this.myTIA; }
			this.getVideo = function() {    return this.myVideo;  }
    this.setNominalFrameRate = function( aFrameRate)
    {
        this.myFrameRate=aFrameRate;
    }
//    
//    
  	this.getDisplayFormat = function()
  {
      return this.myDisplayFormat;
  }
//    
  	this.setDisplayFormat = function(aDisplayFormat) {
        this.myDisplayFormat=aDisplayFormat;
        this.setNominalFrameRate(aDisplayFormat.getDisplayRate());
        this.getVideo().setTIAPalette(aDisplayFormat.getDisplayPalette());
      
        this.mySystem.reset();
    }
//
   this.reinstallCore = function()
   {
        this.mySystem.attach(this.myRiot);
        this.mySystem.attach(this.myTIA);
   }
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    
    	this.initializeVideo = function() {
        if(this.myVideo==null) this.myVideo = new JSVideo(this);
	        this.getVideo().initialize();
      }
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
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
	            this.mySystem.unattach(this.myCart);
	            this.reinstallCore();
        }//end : previous cartridge is being replaced
        
        this.myVideo.clearBackBuffer();
       	this.myVideo.clearBuffers();
 	      this.myCart=aCart;
 	      this.myCart.setConsole(this);
        
 	      this.mySystem.attach(this.myCart);
 				this.mySystem.reset();
        this.detectDisplayFormat();
        
  	    if (aDisplayHeight<=0) this.detectDisplayHeight();
        else this.myDisplayHeight=aDisplayHeight;
      	this.adjustBackBuffer();
        
        
        // Make sure height is set properly for PAL ROM
       
        this.setTelevisionMode(TELEVISION_MODE_GAME);
        // Reset, the system to its power-on state
        this.mySystem.reset();
       	console.log("JStella display: YStart=" + this.myYStart + ", DisplayHeight=" + this.myDisplayHeight);
    }
    
    
		this.insertCartridge = function (aCart)
  	  {
	        this.insertCartridge2(aCart, -1);
	    }

    this.createCartridge = function(aInputStream, aCartridgeType) {
        var zCart=null;
				{
            if (aInputStream!=null) {
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
        
			  return zCart;
    }
    
//    
//    
//// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    
//    
//    
//    
//    
//    
//    // ====================== MAIN METHOD ========================
//    
//    
    this.doFrame = function(){
        //profiling note - Sep 3 2007 - it seems that a lot of times, whenever doFrame() lasts long (e.g. 59 milliseconds), that 
        //  the processFrame is taking up most of the time
        
        if (this.myVideo!=null)
        {   
         
        if (this.getTelevisionMode()==TELEVISION_MODE_GAME)
        {
            if (this.myCart!=null) 
            {
            this.myTIA.processFrame();    
            this.myVideo.doFrameVideo();
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
    	this.readSwitches = function() {
	        return this.mySwitches;	
					}
//    
//    
//    
//    
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
//     //========================================================================
//    
//     
//    
//   
        this.setConsoleClient(aConsoleClient);
        
        
        

        this.initializeVideo();
        this.flipSwitch(ConsoleSwitch.SWITCH_RESET, false);
        this.flipSwitch(ConsoleSwitch.SWITCH_SELECT, false);
        this.flipSwitch(ConsoleSwitch.SWITCH_BW, false);
        this.flipSwitch(ConsoleSwitch.SWITCH_DIFFICULTY_P0, false); //amateur setting
        this.flipSwitch(ConsoleSwitch.SWITCH_DIFFICULTY_P1, false); //amateur setting
        
				this.myControllers = [];
        this.myControllers[0] = new JSController(Jack.LEFT);
        this.myControllers[1] = new JSController(Jack.RIGHT); 
        
        this.mySystem = new JSSystem(this);
        this.myRiot = new JSRiot(this);
        this.myTIA = new JSTIA(this);
        
        this.mySystem.attach(this.myRiot);
        this.mySystem.attach(this.myTIA);
				console.log(this);
}
