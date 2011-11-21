function JSController() {
  var serialVersionUID;
  var JOYSTICK_UP = 0;
  var JOYSTICK_DOWN=1;
  var JOYSTICK_LEFT=2;
  var JOYSTICK_RIGHT=3;
  var JOYSTICK_BUTTON=5;

  var PADDLE_ALPHA_BUTTON = 3;
  var PADDLE_BETA_BUTTON=2;
  var PADDLE_ALPHA_RESISTANCE = 8;
  var PADDLE_BETA_RESISTANCE = 4;

  var BOOSTERGRIP_BOOSTER = 4;
  var BOOSTERGRIP_TRIGGER = 8;

  var PaddleID = {
    PADDLE_ALPHA: "PADDLE_ALPHA",
    PADDLE_BETA: "PADDLE_BETA"
  };

  var myJack = Jack.LEFT; // TODO
  var myPinValue = [0,0,0,0,0,0,0,0];

  this.JSController = function () {
    this.resetController();
  }
  this.resetController = function() {
    var i;
    for (i=0;iL<this.myPinValue.length;i++) {
      this.myPinValue[i] = 1;
    }
    this.setPaddlePosition(this.PaddleID.PADDLE_ALPHA, 00);
    this.setPaddlePosition(this.PaddleID.PADDLE_BETA, 00);
  }

  this.read = function (pin) {
    // TODO
    if (typeof pin == "Digital") return (this.myPinValue[this.getPinIndex(pin)]!=0);
    if (typeof pin == "Analog") return (this.myPinValue[this.getPinIndex(pin)]);
  }
  this.write = function (pin, value) {
  }
  this.setJoystickState = function(aJoystickDir, aPressed) {
    if (aPressed == true) myPinValue[aJoystickDir] = 0;
    else myPinValue[aJoystickDir] =1;
  }
  this.setPaddleTrigger = function(aID, aPressed) {
    var zValue = (aPressed)? 0: 1;
    if (aID == this.PaddleID.PADDLE_ALPHA) this.myPinValue[this.PADDLE_ALPHA_BUTTON] = zValue;
    else if (aID == this.PaddleID.PADDLE_BETA) this.myPinValue[this.PADDLE_BETA_BUTTON] = zValue;
  }
  this.changeControllerState = function (aControlEventType, aOn) {
    var zValue = (aOn)? 0: 1;
    this.myPinValue[aControlEventT$ype] = zValue;
  }
  this.toPercentX = function (aResistance) {
    return (10- (aResistance/10000.0));
  }
  this.toResistance = function (aPercentX) {
    return (10000.0 * (100 - aPercentX));
  }
  this.setPaddlePosition = function (aID, aPercentage) {
    var zNewPercent = (aPercentage>100)? 100: aPercentage;
    zNewPercent = (zNewPercent>0)? zNewPercent: 0;
    var zRes = toResistance(zNewPercent);
    if (aID == this.PaddleID.PADDLE_ALPHA) this.myPinValue[this.PADDLE_ALPHA_RESISTANCE] = zRes;
    else if (aID == this.PaddleID.PADDLE_BETA) this.myPinValue[this.PADDLE_BETA_RESISTANCE] = zRes;
  }
  this.getPaddlePosition = function(aID) {
    var zIndex = (aID == this.PaddleID.PADDLE_BETA)? PADDLE_BETA_RESISTANCE: PADDLE_ALPHA_RESISTANCE;
    return this.toPercentX(this.myPinValue[zIndex]);
  }
  this.changePaddlePositon = function (aID, aDeltaPercent) {
    var zCurrent = this.getPaddlePosition(aID);
    this.setPaddlePosition(aID, zCurrent+aDeltaPercent);
  }
  this.setBoosterGripBooster = function (aPressed) {
    this.setPaddlePosition(this.PaddleID.PADDLE_BETA, (aPressed? 100: 0));
  }
  this.setBoosterGripTrigger = function (aPressed) {
    this.setPaddlePosition(this.PaddleID.PADDLE_ALPHA, (aPressed? 100: 0));
  }
  this.getPinIndex = function (aPin) {
    // TODO
    if (typeof aPin == "DigitalPin") {
      switch (aPin) {
        case One : return 0;
        case Two : return 1;
        case Three : return 2;
        case Four : return 3;
        case Six : return 5;
        default : return 0;
      }
    }
    if (typeof aPin == "AnalogPin") {
      switch (aPin) {
        case Five : return 4;
        case Nine : return 8;
        default : return 0;
      }
    }
  }
}
