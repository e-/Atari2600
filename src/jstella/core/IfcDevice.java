//============================================================================
//
// MM     MM  6666  555555  0000   2222
// MMMM MMMM 66  66 55     00  00 22  22
// MM MMM MM 66     55     00  00     22
// MM  M  MM 66666  55555  00  00  22222  --  "A 6502 Microprocessor Emulator"
// MM     MM 66  66     55 00  00 22
// MM     MM 66  66 55  55 00  00 22
// MM     MM  6666   5555   0000  222222
//
// Copyright (c) 1995-2007 by Bradford W. Mott and the Stella team
//
// See the file "license" for information on usage and redistribution of
// this file, and for a DISCLAIMER OF ALL WARRANTIES.
//
// $Id: IfcDevice.java,v 1.3 2007/08/12 04:51:29 mauvila Exp $
//============================================================================
package jstella.core;


import jstella.j6507.*;

/**
 *  Java interface for devices which can be attached to a 6502/6507
 *  based system.
 * @author Bradford W. Mott
 * @version $Id: IfcDevice.java,v 1.3 2007/08/12 04:51:29 mauvila Exp $
 */
 public interface IfcDevice {
    // protected JSSystem mySystem=null;
    /**
     * This returns the name of the device.  Used for debugging purposes.
     * @return name of device
     */
    public String name();
    /**
     * This method resets the device.
     */
    public void reset();
    /**
     * This method is called to "install" the device into the given system.
     * When a device installs itself, it gives the system data on where (i.e.
     * what addresses) it will be found, and how (i.e. by what addresses) the
     * system (and thus CPU) can access it.
     * @param system The system on which to install this device.
     */
    public void install(JSSystem system);
    
    
    /**
     * Called by the system (via page access) to retrieve a byte.  This can represent
     * something stored in memory at the given address, or the device can return the
     * result of some on-the-fly calculation.  Or the device may return a value
     * associated with hardware (e.g. a joystick) associated with that address.
     * It is up to the device to determine what occurs here.
     * <p>
     * This int returned by this method must be the value of an unsigned byte, that is,
     * must be greater-than/equal to zero and less-than 256.
     * @param address the address the system is querying
     * @return an int representing an unsigned byte of data
     */
    public int peek(int address);
    /**
     * Called by the system (via page access) to "give" a byte of data to the device.
     * The device can interpret this in the traditional way, that is, to
     * store the byte of data at the address specified.  The device can also use poke
     * commands as a signal to do something, often times heeding what address was poked,
     * but ignoring the byte provided.  Or the device may send the byte of data to
     * another piece of hardware.  Or the device could simply do nothing.
     * It is up to the device to determine what occurs here.
     * <p>
     * The byte value provided must be the value of an unsigned byte, that is,
     * must be greater-than/equal to zero and less-than 256.
     * @param address the 16-bit address to poke
     * @param aByteValue the byte of data to write
     */
   public void poke(int address, int aByteValue);
    /**
     * Called whenever the system resets its count of CPU cycles.  Because many devices
     * use this cycle count for their own operations, this method lets the devices know
     * that they need to adjust to the CPU cycle count returning to zero.
     */
    public void systemCyclesReset();
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
}

