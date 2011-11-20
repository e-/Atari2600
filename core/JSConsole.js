function JSConsole () {
  var serialVersionUID;
  var DEFAULT_YSTART = 34;
  var DEFAULT_DISPLAY_HEIGHT = 210;
  var DEFAULT_DISPLAY_WIDTH = CLOCKS_PER_LINE_VISBILE;
  var TRASH_FRAMES =60;

  var myFrameRate = 60;
  var myDisplayFormat; // TODO
  var myStart = DEFAUKT_YSTART;
  var myConsoleClient = null; // TODO
  var myController = null ; // TODO

  var mySwitches = 0xFF;
  var mySystem = null; // TODO
  var myCart = null; // TODO
  var myRiot = null; // TODO
  var myVideo = null; // TODO
  var myAudio = null; // TODO

  var myTelevisionMode = TELEVISION_MODE_OFF;

  this.JSConsole = function (aConsoleClient) {
    this.setConsoleClient(aConsoleClient);
    this.initializeAudio();
    this.initializeVideo();
    // ... 
  }
}
