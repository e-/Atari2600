
package jstella.core;

import jstella.j6507.IfcSystem;
import static jstella.core.JSConstants.*;


/**
 * The MOS 6532 RAM-I/O-Timer (RIOT) chip.
 * This chip, as the name suggests, is responsible for the RAM, input/output (joystick directions) for
 * the 2600.  It also provides a timer.
 *
 * <p>
 * This class was previously called M6532, but because 6532 is awfully close
 * to 6502, and RIOT is a cool but yet natural acronym, I changed the name.
 *
 * <p>
 * Consult the "Stella Programmer's Guide" (Steve Wright, 1979) for more information.
 * This guide was written back in the 1970s and was used originally by people who programmed
 * games for the Atari 2600.  It is widely available on the Internet.
 */
public class JSRiot implements IfcDevice, java.io.Serializable {
    private final static long serialVersionUID = 7428490123154878165L;
    private static java.util.Random mvRandom=new java.util.Random();
    
    
    
    
    
    private JSConsole myConsole=null;
    private JSSystem mySystem=null;
    
    private int[] myRAM=new int[128];
    
    // Current value of my Timer
    private int myTimer=0;
    
    // Log base 2 of the number of cycles in a timer interval
    private int myIntervalShift=0;
    
    // Indicates the number of cycles when the timer was last set
    private int myCyclesWhenTimerSet=0;
    
    // Indicates when the timer was read after timer interrupt occured
    private int myCyclesWhenInterruptReset=0;
    
    // Indicates if a read from timer has taken place after interrupt occured
    private boolean myTimerReadAfterInterrupt=false;
    
    // Data Direction Register for Port A
    private int myDDRA=0;
    
    // Data Direction Register for Port B
    private int myDDRB=0;
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * The constructor - creates a new JSRiot chip.
     * @param console The console to which this chip belongs
     */
    public JSRiot(JSConsole console) {
        myConsole=console;
        
        
        
        // Randomize the 128 bytes of memory - just like it would be in real life
        
        
        
        for (int i=0; i<myRAM.length; i++) {
            //This takes a random integer (which can be any value an integer can hold), divides it by 256, and using
            //  the REMAINDER for this division, which is guaranteed to be between 0 and 255 (inclusive).
            myRAM[i]=Math.abs(mvRandom.nextInt() % 256);
        }
        
        
        
        // Initialize other data members
        reset();
    }
    
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public String name() {
        return "6532";
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Resets the RIOT chip.
     */
    public void reset() {
        
        
        myTimer = 25 + (mvRandom.nextInt() % 75);
        myIntervalShift = 6;
        myCyclesWhenTimerSet = 0;
        myCyclesWhenInterruptReset = 0;
        myTimerReadAfterInterrupt = false;
        
        // Zero the I/O registers
        myDDRA = 0x00;
        myDDRB = 0x00;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Resets the RIOT chip
     */
    public void systemCyclesReset() {
        // JSSystem cycles are being reset to zero so we need to adjust
        // the cycle count we remembered when the timer was last set
        myCyclesWhenTimerSet -= mySystem.getCycles();
        myCyclesWhenInterruptReset -= mySystem.getCycles();
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void install(jstella.core.JSSystem system) {
        
        // Remember which system I'm installed in
        mySystem = system;
        
        int shift = PAGE_SHIFT;
        int mask = PAGE_MASK;
        
        // Make sure the system we're being installed in has a page size that'll work
        assert((0x1080 & mask) == 0);
        
        // All accesses are to this device
        
        
        
        
        // We're installing in a 2600 system
        for(int address = 0; address < 8192; address += (1 << shift)) {
            if((address & 0x1080) == 0x0080) {
                if((address & 0x0200) == 0x0000) {
                    jstella.core.PageAccess access=new jstella.core.PageAccess(this);
                    access.setDirectPeekMemory(myRAM, address & 0x007f);
                    access.setDirectPokeMemory(myRAM, address & 0x007f);
                    
                    mySystem.setPageAccess((address >> shift), access);
                } else {
                    jstella.core.PageAccess access=new jstella.core.PageAccess(this);
                    access.setIndirectMode();//Bases(0,0);//directPeekBase = 0;
                    
                    mySystem.setPageAccess((char)(address >> shift), access);
                }
            }
        }
    }
    
    
    /**
     * Returns the hex string equivalent.
     * Because I'm lazy
     */
    public static String toHexStr(char addr) {
        return "0x" + Integer.toHexString((int)addr);
    }
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public int peek(int addr) {
        int zReturn=0;
      
      
        switch(addr & 0x07) {
            
            case 0x00:    // Port A I/O Register (Joystick)
            {
           
                int value = 0x00;
                
                if(myConsole.getController(Jack.LEFT).read(DigitalPin.One))      value |= 0x10;
                if(myConsole.getController(Jack.LEFT).read(DigitalPin.Two))      value |= 0x20;
                if(myConsole.getController(Jack.LEFT).read(DigitalPin.Three))    value |= 0x40;
                if(myConsole.getController(Jack.LEFT).read(DigitalPin.Four))     value |= 0x80;                
                if(myConsole.getController(Jack.RIGHT).read(DigitalPin.One))     value |= 0x01;
                if(myConsole.getController(Jack.RIGHT).read(DigitalPin.Two))     value |= 0x02;
                if(myConsole.getController(Jack.RIGHT).read(DigitalPin.Three))   value |= 0x04;
                if(myConsole.getController(Jack.RIGHT).read(DigitalPin.Four))    value |= 0x08;                
                zReturn=value; break;
            }
            
            case 0x01:    // Port A Data Direction Register
            {
                zReturn=myDDRA; break;
            }
            
            case 0x02:    // Port B I/O Register (JSConsole switches)
            {
               
                zReturn=myConsole.readSwitches(); break;
            }
            
            case 0x03:    // Port B Data Direction Register
            {
                zReturn=myDDRB; break;
            }
            
            case 0x04:    // Timer Output
            case 0x06:
            {
               
                int zCurrentCycle = mySystem.getCycles() - 1;
                int zCyclesElapsed = zCurrentCycle - myCyclesWhenTimerSet;
                int zCurrentIntervalCount = (int)myTimer - (int)(zCyclesElapsed >> myIntervalShift) - 1;
                
                // See if the zCurrentIntervalCount has expired yet?
                if(zCurrentIntervalCount >= 0) {
                    zReturn=zCurrentIntervalCount;  break;
                } else {
                    zCurrentIntervalCount = (int)(myTimer << myIntervalShift) - (int)zCyclesElapsed - 1;
                    
                    if((zCurrentIntervalCount <= -2) && !myTimerReadAfterInterrupt) {
                        // Indicate that zCurrentIntervalCount has been read after interrupt occured
                        myTimerReadAfterInterrupt = true;
                        myCyclesWhenInterruptReset = mySystem.getCycles();
                    }
                    
                    if(myTimerReadAfterInterrupt) {
                        int zOffset = myCyclesWhenInterruptReset -  (myCyclesWhenTimerSet + (myTimer << myIntervalShift));
                        
                        zCurrentIntervalCount = myTimer - (zCyclesElapsed >> myIntervalShift) - zOffset;
                    }
                    
                    zReturn=zCurrentIntervalCount & 0xff; break;
                }
            }
            
            case 0x05:    // Interrupt Flag
            case 0x07:
            {
               
                int cycles = mySystem.getCycles() - 1;
                int delta = cycles - myCyclesWhenTimerSet;
                int timer = (int)myTimer - (int)(delta >> myIntervalShift) - 1;
                
                if((timer >= 0) || myTimerReadAfterInterrupt)
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
    
    private boolean bool(int aValue) {
        return (aValue!=0);
    }
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void poke(int addr, int value) {
        
        if((addr & 0x07) == 0x00)         // Port A I/O Register (Joystick)
        {
            int a = value & myDDRA;
            
            myConsole.getController(Jack.LEFT).write(DigitalPin.One, bool(a & 0x10));
            myConsole.getController(Jack.LEFT).write(DigitalPin.Two, bool(a & 0x20));
            myConsole.getController(Jack.LEFT).write(DigitalPin.Three, bool(a & 0x40));
            myConsole.getController(Jack.LEFT).write(DigitalPin.Four, bool(a & 0x80));
            
            myConsole.getController(Jack.RIGHT).write(DigitalPin.One, bool(a & 0x01));
            myConsole.getController(Jack.RIGHT).write(DigitalPin.Two, bool(a & 0x02));
            myConsole.getController(Jack.RIGHT).write(DigitalPin.Three, bool(a & 0x04));
            myConsole.getController(Jack.RIGHT).write(DigitalPin.Four, bool(a & 0x08));
        } else if((addr & 0x07) == 0x01)    // Port A Data Direction Register
        {
            myDDRA = value;
            
            
        } else if((addr & 0x07) == 0x02)    // Port B I/O Register (JSConsole switches)
        {
            return;
        } else if((addr & 0x07) == 0x03)    // Port B Data Direction Register
        {
//        myDDRB = value;
            return;
        } else if((addr & 0x17) == 0x14)    // TIM1T - Write timer divide by 1
        {
            myTimer = value;
            myIntervalShift = 0;
            myCyclesWhenTimerSet = mySystem.getCycles();
            myTimerReadAfterInterrupt = false;
        } else if((addr & 0x17) == 0x15)    // TIM8T - Write timer divide by 8
        {
            myTimer = value;
            myIntervalShift = 3;
            myCyclesWhenTimerSet = mySystem.getCycles();
            myTimerReadAfterInterrupt = false;
        } else if((addr & 0x17) == 0x16)    // TIM64T - Write timer divide by 64
        {
            myTimer = value;
            myIntervalShift = 6;
            myCyclesWhenTimerSet = mySystem.getCycles();
            myTimerReadAfterInterrupt = false;
        } else if((addr & 0x17) == 0x17)    // TIM1024T - Write timer divide by 1024
        {
            myTimer = value;
            myIntervalShift = 10;
            myCyclesWhenTimerSet = mySystem.getCycles();
            myTimerReadAfterInterrupt = false;
        } else if((addr & 0x14) == 0x04)    // Write Edge Detect Control
        {
            
        } else {
            
        }
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 


    
    
    
    
    
}
