/**
 * A go-between for the GUI classes and JSConsole.  It is mostly a way of
 * centralizing commonly used methods for the various GUI classes (JStellaMain,
 * JStellaApplet, etc.)
 * It is also a way of letting those who work strictly with the GUI be free from
 * a lot of the behind-the-scenes stuff.
 * <p>
 * It handles the timers, etc.
 * </p>
 * 
 * This class is optional--GUI classes are free to interact directly with the JSConsole.
 * @author Sysop
 */
function Intercessor(aClient){

    //NTSC and PAL are television formats...NTSC is used the U.S.
    // -NTSC televisions update the screen 60 times/sec (i.e. 60 Hertz).  To emulate this,
    // a util timer is used, with a delay of 17 milliseconds.  Thus, there are (1000 ms /17 ms) or about 58.8 cycles
    // per second using this delay.
    
    this.TIMER_DELAY_NTSC= 17;
    this.TIMER_DELAY_PAL=20;
    this.TIMER_DELAY_SNOW=100;
    
		this.myUtilTimer=null; 
    this.myInputMaster=new InputMaster(this);
    this.myCanvas=null;
    
    
    this.myIntercessorClient=null;
    this.myConsole=null;
    
    this.myVirtualJoystickDialog=null;
    
    this.myCurrentTimerDelay=this.TIMER_DELAY_NTSC;
    this.myAutoPauseMode=false;
    this.myPausedByPlayer=false;
    this.myPausedByFocusLoss=false;
   
//    this.myCanvasFocusListener=new IntercessorKeyboardFocusListener();
    
    /**
     * Creates a new instance of Intercessor.  GUI classes should implement the interface
     * IfcIntercessorClient, which contains methods telling the GUI to update this-and-that.
     * @param aClient the client (i.e. GUI class) of the intercessor.
     */
    
		this.createCanvas = function()
    {
        if (this.myCanvas==null) 
        {
           this.myCanvas=new JStellaCanvas();
       //    myCanvas.addKeyListener(myInputMaster.getKeyListener());
       //    myCanvas.addFocusListener(myCanvasFocusListener);
           
       //    myInputMaster.addPaddleToComponent(0, myCanvas);
         //  if (myIntercessorClient!=null) myIntercessorClient.displayCanvas(myCanvas);
        }
    };

    this.updateTimerDelay = function() {
        if (this.myConsole.getTelevisionMode()==TELEVISION_MODE_SNOW) this.myCurrentTimerDelay=this.TIMER_DELAY_SNOW;
        else if (this.myConsole.getDisplayFormat()==DisplayFormat.PAL) this.myCurrentTimerDelay=this.TIMER_DELAY_PAL;
        else this.myCurrentTimerDelay=this.TIMER_DELAY_NTSC;
//       	this.myConsole.getAudio().setRealDisplayFrameRate(1000.0 / myCurrentTimerDelay);
    };
    
    this.initConsole = function(aConsole) {
        if ((this.myConsole!=null)&&(this.myConsole!=aConsole)) {
            myConsole.destroy();
        }//end : destroy old console
        this.myConsole=aConsole;
        this.myConsole.setConsoleClient(this);
    		
       //updateTelevisionMode(); TODO
       
			 //myCanvas.requestFocusInWindow(); TODO
       this.updateTimerDelay();
    };
    
		this.myIntercessorClient=aClient; /* Constructor */
		this.createCanvas();
		tempConsole = new JSConsole(this);
		console.log(tempConsole);
		this.initConsole(tempConsole);
    

    /**
     * Returns the cartridge object.  Will return NULL if no cartridge has been loaded.
     * @return the cartridge object
     */
    this.getCartridge = function() {
        return myConsole.getCartridge();
    };
    
    
    
    this.getInputMaster = function()
    {
        return myInputMaster;
    };
    
    /**
     * Changes the television mode (game, tv test pattern, snow/static) based
     * on whether a cartridge is loaded, and if not, what the configuration specifies.
     */
    this.updateTelevisionMode = function()
    {
        if (myConsole.getCartridge()!=null) myConsole.setTelevisionMode(TELEVISION_MODE_GAME);
        else
        {
           zDefaultScreen=myIntercessorClient.getConfiguration().get(JStellaMain.CONFIG_KEY_DEFAULT_SCREEN);
           if (JStellaMain.CONFIG_VALUE_DEFAULT_SCREEN_SNOW.equals(zDefaultScreen)) myConsole.setTelevisionMode(TELEVISION_MODE_SNOW);
           else myConsole.setTelevisionMode(TELEVISION_MODE_TEST_PATTERN);
        }//end : no cartridge loaded
       myConsole.updateVideoFrame();
        
        updateTimerDelay();
    };
    
    this.isVirtualJoystickEnabled = function() {
        if ((myVirtualJoystickDialog==null)||(myVirtualJoystickDialog.isVisible()==false)) return false;
        else return true;
    };
    
    /**
     * The virtual joystick is a separate window with a graphical representation of a 
     * 2600 joystick that allows the user to use the mouse to emulate joystick movement.
     * @param aParent the parent window of which the virtual joystick window will be a child (sort-of)
     */
    this.enableVirtualJoystick = function(aParent) {
        /* TODO DODO no Virtual Joystick support */

/*				if (myVirtualJoystickDialog==null) {
            myVirtualJoystickDialog=new VirtualJoystickDialog(aParent, myInputMaster);
        }//end : is null
        myVirtualJoystickDialog.setVisible(true);*/
        // this.setAutoPauseMode(false);
        
    };
    
    this.disableVirtualJoystick = function() {
        if (myVirtualJoystickDialog!=null) myVirtualJoystickDialog.setVisible(false);
       
    };
    
    this.toggleVirtualJoystick = function(aParent) {
    };
    
    
    this.startTimer = function() {
        if (this.myUtilTimer!=null) {
          killTimer(this.myUtilTimer);
        }//end : resetting timer

					this.myUtilTimer = setInterval(
					(function(self){
						return function(){
							self.MainTimerTask(self);
						};
					}
					)(this)
						 ,this.myCurrentTimerDelay);
       };
       this.stopTimer = function() {
            if (this.myUtilTimer!=null) {
                clearInterval(this.myUtilTimer);
               	this.myUtilTimer=null;
                
            }
    };
    
    this.destroy = function() {
        myConsole.destroy();
    };
    
    
    this.loadStateFromStream = function(aInputStream) {
    };
    
    this.saveStateToStream = function(aOutputStream){
       
    };
    
    this.showDefaultExceptionResponse = function(e) {
				console.log(e);
    }
    
   
    this.updatePause = function() { 
    }
    this.playROM3 = function(aROMStream, aCartridgeType, aDisplayHeight) {   
        this.stopTimer();
        zCart = this.myConsole.createCartridge(aROMStream, aCartridgeType);
          
        if (zCart!=null) {
            this.myConsole.insertCartridge(zCart, aDisplayHeight);
            this.updateTimerDelay();
                
            this.myCanvas.refreshCanvas();
            this.startTimer(); 
        }//end : not null
    };

    this.playROM = function(aROMStream)  {   this.playROM3(aROMStream, null, -1);   }    
    this.playROM2 = function(aROMStream, aCartridgeType) { this.playROM3(aROMStream, aCartridgeType, -1); }
    
    this.getConsole = function() {
        return this.myConsole;
    };

    this.getCanvas = function() {
        return this.myCanvas;
    };
    
    
    this.getJStellaCanvas = function()
    {
        return this.myCanvas;
    };
    
    
    this.runMainLoop = function() {
        if (this.myConsole!=null) {
        	this.myConsole.doFrame();
        }//end : my console == false
    };
    
    
    //==============================================
    
    this.MainTimerTask = function(self) {
      self.runMainLoop();
    } 
}
