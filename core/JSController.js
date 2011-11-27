function JSController() {
  this.serialVersionUID;
  this.JOYSTICK_UP = 0;
  this.JOYSTICK_DOWN=1;
  this.JOYSTICK_LEFT=2;
  this.JOYSTICK_RIGHT=3;
  this.JOYSTICK_BUTTON=5;

  this.PADDLE_ALPHA_BUTTON = 3;
  this.PADDLE_BETA_BUTTON=2;
  this.PADDLE_ALPHA_RESISTANCE = 8;
  this.PADDLE_BETA_RESISTANCE = 4;

  this.BOOSTERGRIP_BOOSTER = 4;
  this.BOOSTERGRIP_TRIGGER = 8;

  this.PaddleID = {
    PADDLE_ALPHA: "PADDLE_ALPHA",
    PADDLE_BETA: "PADDLE_BETA"
  };

  this.myJack = Jack.LEFT; // TODO
	this.myPinValue = [0,0,0,0,0,0,0,0];
  this.read = function (pin) {
    // TODO
    if (pin <10 ) return (this.myPinValue[this.getPinIndex(pin)]!=0);
    if (pin >=10) return (this.myPinValue[this.getPinIndex(pin)]);
  }
  this.write = function (pin, value) {
  }
  this.setJoystickState = function(aJoystickDir, aPressed) {
    if (aPressed == true) this.myPinValue[aJoystickDir] = 0;
    else this.myPinValue[aJoystickDir] =1;
  }
  this.setPaddleTrigger = function(aID, aPressed) {
    var zValue = (aPressed)? 0: 1;
    if (aID == this.PaddleID.PADDLE_ALPHA) this.myPinValue[this.PADDLE_ALPHA_BUTTON] = zValue;
    else if (aID == this.PaddleID.PADDLE_BETA) this.myPinValue[this.PADDLE_BETA_BUTTON] = zValue;
  }
  this.changeControllerState = function (aControlEventType, aOn) {
    var zValue = (aOn)? 0: 1;
    this.myPinValue[aControlEventType] = zValue;
  }
  this.toPercentX = function (aResistance) {
    return Math.floor(10- (aResistance/10000.0));
  }
  this.toResistance = function (aPercentX) {
    return Math.floor(10000.0 * (100 - aPercentX));
  }
  this.setPaddlePosition = function (aID, aPercentage) {
    var zNewPercent = (aPercentage>100)? 100: aPercentage;
    zNewPercent = (zNewPercent>0)? zNewPercent: 0;
    var zRes = this.toResistance(zNewPercent);
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
    if (aPin < 10) {
      switch (aPin) {
        case 1 : return 0;
        case 2 : return 1;
        case 3 : return 2;
        case 4 : return 3;
        case 5 : return 5;
        default : return 0;
      }
    }
    if (aPin>10) {
      switch (aPin) {
        case 11 : return 4;
        case 12 : return 8;
        default : return 0;
      }
    }
  }

  this.resetController = function() {
    var i;
    for (i=0;i<this.myPinValue.length;i++) {
      this.myPinValue[i] = 1;
    }
    this.setPaddlePosition(this.PaddleID.PADDLE_ALPHA, 00);
    this.setPaddlePosition(this.PaddleID.PADDLE_BETA, 00);
  }

    this.resetController();

}
