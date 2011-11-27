function InputMaster(aClient) {
  this.myInputMasterClient = null;
  this.VirtualControlTask = {
    JOYSTICK_A_UP : "JOYSTICK_A_UP",
    JOYSTICK_A_DOWN : "JOYSTICK_A_DOWN",
    JOYSTICK_A_LEFT : "JOYSTICK_A_LEFT",
    JOYSTICK_A_RIGHT : "JOYSTICK_A_RIGHT",
    JOYSTICK_A_BUTTON : "JOYSTICK_A_BUTTON",
  };
  this.performControlItemAction = function(aVItem, aPressed) {
    var zSControllerA = this.myInputMasterClient.getConsole().getController(Jack.LEFT);
    var zSControllerB = this.myInputMasterClient.getConsole().getController(Jack.RIGHT);
    switch (aVItem) {
      case this.VirtualControlTask.JOYSTICK_A_UP : zSControllerA.changeControllerState(0, aPressed); break;
      case this.VirtualControlTask.JOYSTICK_A_DOWN : zSControllerA.changeControllerState(1, aPressed); break;
      case this.VirtualControlTask.JOYSTICK_A_LEFT : zSControllerA.changeControllerState(2, aPressed); break;
      case this.VirtualControlTask.JOYSTICK_A_RIGHT : zSControllerA.changeControllerState(3, aPressed); break;
      case this.VirtualControlTask.JOYSTICK_A_BUTTON : zSControllerA.changeControllerState(5, aPressed); break;
            
//      case JOYSTICK_B_UP : zSControllerB.changeControllerState(JSController.JOYSTICK_UP, aPressed); break;
//      case JOYSTICK_B_DOWN : zSControllerB.changeControllerState(JSController.JOYSTICK_DOWN, aPressed); break;
//      case JOYSTICK_B_LEFT : zSControllerB.changeControllerState(JSController.JOYSTICK_LEFT, aPressed); break;
//      case JOYSTICK_B_RIGHT : zSControllerB.changeControllerState(JSController.JOYSTICK_RIGHT, aPressed); break;
//      case JOYSTICK_B_BUTTON : zSControllerB.changeControllerState(JSController.JOYSTICK_BUTTON, aPressed); break;
//            
//      case PADDLE_A_BUTTON : zSControllerA.changeControllerState(JSController.PADDLE_ALPHA_BUTTON, aPressed); break;
//      case PADDLE_B_BUTTON : zSControllerA.changeControllerState(JSController.PADDLE_BETA_BUTTON, aPressed); break;
//      case PADDLE_C_BUTTON : zSControllerB.changeControllerState(JSController.PADDLE_ALPHA_BUTTON, aPressed); break;
//      case PADDLE_D_BUTTON : zSControllerB.changeControllerState(JSController.PADDLE_BETA_BUTTON, aPressed); break;
//           
//      case PADDLE_A_CW : if (aPressed) zSControllerA.changePaddlePosition(JSController.PaddleID.PADDLE_ALPHA, myPaddleShiftPercentage); break;
//      case PADDLE_B_CW : if (aPressed) zSControllerA.changePaddlePosition(JSController.PaddleID.PADDLE_BETA, myPaddleShiftPercentage); break;
//      case PADDLE_C_CW : if (aPressed) zSControllerB.changePaddlePosition(JSController.PaddleID.PADDLE_ALPHA, myPaddleShiftPercentage); break;
//      case PADDLE_D_CW : if (aPressed) zSControllerB.changePaddlePosition(JSController.PaddleID.PADDLE_BETA, myPaddleShiftPercentage); break;
//            
//      case PADDLE_A_CCW : if (aPressed) zSControllerA.changePaddlePosition(JSController.PaddleID.PADDLE_ALPHA, -1 * myPaddleShiftPercentage); break;
//      case PADDLE_B_CCW : if (aPressed) zSControllerA.changePaddlePosition(JSController.PaddleID.PADDLE_BETA, -1 * myPaddleShiftPercentage); break;
//      case PADDLE_C_CCW : if (aPressed) zSControllerB.changePaddlePosition(JSController.PaddleID.PADDLE_ALPHA, -1 * myPaddleShiftPercentage); break;
//      case PADDLE_D_CCW : if (aPressed) zSControllerB.changePaddlePosition(JSController.PaddleID.PADDLE_BETA, -1 * myPaddleShiftPercentage); break;
//            
//      case BOOSTERGRIP_A_BOOSTER : zSControllerA.setBoosterGripBooster(aPressed); break;
//      case BOOSTERGRIP_B_BOOSTER : zSControllerB.setBoosterGripBooster(aPressed); break;
//            
//      case BOOSTERGRIP_A_TRIGGER : zSControllerA.setBoosterGripTrigger(aPressed); break;
//      case BOOSTERGRIP_B_TRIGGER : zSControllerB.setBoosterGripTrigger(aPressed); break;
  }
	}
  
 
  /* Constructor */
  if (aClient === undefined || aClient === null) {
    this.setControls(DEFAULT_CONTROL_BINDERS);
    this.setSwitches(DEFAULT_SWITCH_BINDERS);
  }
  this.myInputMasterClient = aClient;

	self=this;

  $(document).keydown(function(e){
    switch(e.keyCode) {
      case 32:
        // SPACE
        self.performControlItemAction(self.VirtualControlTask.JOYSTICK_A_BUTTON, true);
        break;
      case 37:
        // left arrow
        self.performControlItemAction(self.VirtualControlTask.JOYSTICK_A_LEFT, true);
        break;
      case 38:
        // up arrow
        self.performControlItemAction(self.VirtualControlTask.JOYSTICK_A_UP, true);
        break;

      case 39:
        // right arrow
        self.performControlItemAction(self.VirtualControlTask.JOYSTICK_A_RIGHT, true);
        break;

      case 40:
        // down arrow
        self.performControlItemAction(self.VirtualControlTask.JOYSTICK_A_DOWN, true);
        break;

    }
  });

	$(document).keyup(function(e){
    switch(e.keyCode) {
      case 32:
        // SPACE
        self.performControlItemAction(self.VirtualControlTask.JOYSTICK_A_BUTTON, false);
        break;
      case 37:
        // left arrow
        self.performControlItemAction(self.VirtualControlTask.JOYSTICK_A_LEFT, false);
        break;
      case 38:
        // up arrow
        self.performControlItemAction(self.VirtualControlTask.JOYSTICK_A_UP, false);
        break;

      case 39:
        // right arrow
        self.performControlItemAction(self.VirtualControlTask.JOYSTICK_A_RIGHT, false);
        break;

      case 40:
        // down arrow
        self.performControlItemAction(self.VirtualControlTask.JOYSTICK_A_DOWN, false);
        break;

    }
  });

}
