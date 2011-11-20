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
// $Id: NullDevice.java,v 1.3 2007/08/18 09:10:37 mauvila Exp $
//============================================================================
package jstella.core;

import jstella.j6507.IfcSystem;


/**
 * Class that represents a "null" device.  The basic idea is that a
 * null device is installed in a 6502 based system anywhere there are
 * holes in the address space (i.e. no real device attached).
 *
 * @author  Bradford W. Mott
 * @version $Id: NullDevice.java,v 1.3 2007/08/18 09:10:37 mauvila Exp $
 */
public class NullDevice implements IfcDevice, java.io.Serializable {
    private final static long serialVersionUID = 2224011782042263586L;
    public NullDevice(){}
    public String name(){  return "NULL";}
    public void reset(){}
    public void install(JSSystem system){}
    public int peek(int address){ 
        //System.out.println("DEBUG:ERROR - peek at null device address : " + address);   
        assert(false);  return 0; }
    public void poke(int address, int aByteValue){ assert(false); /* throw and error message*/}
    public boolean save(java.io.DataOutputStream out){ return true;}
    public boolean load(java.io.DataInputStream in){ return true;}
    public void systemCyclesReset() { }
    
    
}
