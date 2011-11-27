function JSRiot(console) {
  this.serialVersionID;
  this.mvRandom = Math.random();

  this.myConsole = null;
  this.JSSystem = null;
  this.myRAM = [];
  for (var i=0;i<128;i++) {
    myRAM[i] = Math.floor(Math.random()*255);
  }
  this.myTimer = 0;
  this.myIntervalShift = 0;
  this.myCyclesWhenTimerSet = 0;
  this.myCyclesWhenInterruptReset = 0;
  this.myTimerReadAfterInterrupt = false;

  this.myDDRA = 0;
  this.myDDRB = 0;

  // Constructor
  this.myConsole = console;
  this.reset();

  this.name = function() {
    return "6532";
  }

  this.reset = function() {
    this.myTimer = 25 + Math.floor(Math.random()*75);
    this.myIntervalShift = 6;
    this.myCyclesWhenTimerSet = 0;
    this.myCyclesWhenInterruptReset = 0;
    this.myTimerReadAfterInterrupt = false;

    this.myDDRA = 0x00;
    this.myDDRB = 0x00;
  }

  
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Resets the RIOT chip
     */
    this.systemCyclesReset = function() {
        // JSSystem cycles are being reset to zero so we need to adjust
        // the cycle count we remembered when the timer was last set
        this.myCyclesWhenTimerSet -= mySystem.getCycles(); // TODO : MYSYSTEM
        this.myCyclesWhenInterruptReset -= mySystem.getCycles();
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.install = function(system) {
        // Remember which system I'm installed in
        this.mySystem = system;
        
        var shift = PAGE_SHIFT;
        var mask = PAGE_MASK;
        
        assert((0x1080 & mask) == 0);
        // We're installing in a 2600 system
        for(var address = 0; address < 8192; address += (1 << shift)) {
            if((address & 0x1080) == 0x0080) {
                if((address & 0x0200) == 0x0000) {
                    var access= new PageAccess(this); //TODO
                    access.setDirectPeekMemory(this.myRAM, address & 0x007f);
                    access.setDirectPokeMemory(this.myRAM, address & 0x007f);
                    
                    mySystem.setPageAccess((address >> shift), access);
                } else {
                    var access= new PageAccess(this);
                    access.setIndirectMode();//Bases(0,0);//directPeekBase = 0;
                    
                    this.mySystem.setPageAccess((address >> shift), access);
                }
            }
        }
    }
    
    
    /**
     * Returns the hex string equivalent.
     * Because I'm lazy
     */
    this.toHexStr = function(addr) {
        return "0x" + addr.toString(16);
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.peek = function(addr) {
        var zReturn=0;
      
        switch(addr & 0x07) {
            
            case 0x00:    // Port A I/O Register (Joystick)
            {
           
                var value = 0x00;
                
                if(this.myConsole.getController(Jack.LEFT).read(DigitalPin.One))      value |= 0x10;
                if(this.myConsole.getController(Jack.LEFT).read(DigitalPin.Two))      value |= 0x20;
                if(this.myConsole.getController(Jack.LEFT).read(DigitalPin.Three))    value |= 0x40;
                if(this.myConsole.getController(Jack.LEFT).read(DigitalPin.Four))     value |= 0x80;                
                if(this.myConsole.getController(Jack.RIGHT).read(DigitalPin.One))     value |= 0x01;
                if(this.myConsole.getController(Jack.RIGHT).read(DigitalPin.Two))     value |= 0x02;
                if(this.myConsole.getController(Jack.RIGHT).read(DigitalPin.Three))   value |= 0x04;
                if(this.myConsole.getController(Jack.RIGHT).read(DigitalPin.Four))    value |= 0x08;                
                zReturn=value; break;
            }
            
            case 0x01:    // Port A Data Direction Register
            {
                zReturn=this.myDDRA; break;
            }
            
            case 0x02:    // Port B I/O Register (JSConsole switches)
            {
               
                zReturn=this.myConsole.readSwitches(); break;
            }
            
            case 0x03:    // Port B Data Direction Register
            {
                zReturn=this.myDDRB; break;
            }
            
            case 0x04:    // Timer Output
            case 0x06:
            {
               
                var zCurrentCycle = this.mySystem.getCycles() - 1;
                var zCyclesElapsed = zCurrentCycle - this.myCyclesWhenTimerSet;
                var zCurrentIntervalCount = this.myTimer - (zCyclesElapsed >> this.myIntervalShift) - 1;
                
                // See if the zCurrentIntervalCount has expired yet?
                if(zCurrentIntervalCount >= 0) {
                    zReturn=zCurrentIntervalCount;  break;
                } else {
                    zCurrentIntervalCount = (this.myTimer << this.myIntervalShift) - zCyclesElapsed - 1;
                    
                    if((zCurrentIntervalCount <= -2) && !this.myTimerReadAfterInterrupt) {
                        // Indicate that zCurrentIntervalCount has been read after interrupt occured
                        this.myTimerReadAfterInterrupt = true;
                        this.myCyclesWhenInterruptReset = this.mySystem.getCycles();
                    }
                    
                    if(this.myTimerReadAfterInterrupt) {
                        var zOffset = this.myCyclesWhenInterruptReset -  (this.myCyclesWhenTimerSet + (this.myTimer << this.myIntervalShift));
                        
                        zCurrentIntervalCount = this.myTimer - (zCyclesElapsed >> this.myIntervalShift) - zOffset;
                    }
                    
                    zReturn=zCurrentIntervalCount & 0xff; break;
                }
            }
            
            case 0x05:    // Interrupt Flag
            case 0x07:
            {
               
                var cycles = this.mySystem.getCycles() - 1;
                var delta = cycles - this.myCyclesWhenTimerSet;
                var timer = this.myTimer - (delta >> this.myIntervalShift) - 1;
                
                if((timer >= 0) || this.myTimerReadAfterInterrupt)
                    zReturn=0x00;
                else zReturn=0x80;
                break;
            }
            
            default:
            {
             
                zReturn=0; break;
            }
        }
        assert((zReturn>=0)&&(zReturn<0x100));
        return zReturn;
    }
    
    this.bool = function(aValue) {
        return (aValue!=0);
    }
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    this.poke = function(addr, value) {
        
        if((addr & 0x07) == 0x00)         // Port A I/O Register (Joystick)
        {
            var a = value & myDDRA;
            
            this.myConsole.getController(Jack.LEFT).write(DigitalPin.One, bool(a & 0x10));
            this.myConsole.getController(Jack.LEFT).write(DigitalPin.Two, bool(a & 0x20));
            this.myConsole.getController(Jack.LEFT).write(DigitalPin.Three, bool(a & 0x40));
            this.myConsole.getController(Jack.LEFT).write(DigitalPin.Four, bool(a & 0x80));
            
            this.myConsole.getController(Jack.RIGHT).write(DigitalPin.One, bool(a & 0x01));
            this.myConsole.getController(Jack.RIGHT).write(DigitalPin.Two, bool(a & 0x02));
            this.myConsole.getController(Jack.RIGHT).write(DigitalPin.Three, bool(a & 0x04));
            this.myConsole.getController(Jack.RIGHT).write(DigitalPin.Four, bool(a & 0x08));
        } else if((addr & 0x07) == 0x01)    // Port A Data Direction Register
        {
            this.myDDRA = value;
            
            
        } else if((addr & 0x07) == 0x02)    // Port B I/O Register (JSConsole switches)
        {
            return;
        } else if((addr & 0x07) == 0x03)    // Port B Data Direction Register
        {
//        myDDRB = value;
            return;
        } else if((addr & 0x17) == 0x14)    // TIM1T - Write timer divide by 1
        {
            this.myTimer = value;
            this.myIntervalShift = 0;
            this.myCyclesWhenTimerSet = this.mySystem.getCycles();
            this.myTimerReadAfterInterrupt = false;
        } else if((addr & 0x17) == 0x15)    // TIM8T - Write timer divide by 8
        {
            this.myTimer = value;
            this.myIntervalShift = 3;
            this.myCyclesWhenTimerSet = this.mySystem.getCycles();
            this.myTimerReadAfterInterrupt = false;
        } else if((addr & 0x17) == 0x16)    // TIM64T - Write timer divide by 64
        {
            this.myTimer = value;
            this.myIntervalShift = 6;
            this.myCyclesWhenTimerSet = this.mySystem.getCycles();
            this.myTimerReadAfterInterrupt = false;
        } else if((addr & 0x17) == 0x17)    // TIM1024T - Write timer divide by 1024
        {
            this.myTimer = value;
            this.myIntervalShift = 10;
            this.myCyclesWhenTimerSet = this.mySystem.getCycles();
            this.myTimerReadAfterInterrupt = false;
        } else if((addr & 0x14) == 0x04)    // Write Edge Detect Control
        {
            
        } else {
            
        }
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    
}
