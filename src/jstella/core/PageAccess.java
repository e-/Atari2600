
package jstella.core;

import jstella.j6507.*;
import java.io.*;
import static jstella.core.JSConstants.PAGE_MASK;


/**
 * A class used by JSSystem that acts as an agent for each device, representing
 * its device at a given memory segment.  The JSSystem determines, based on the address
 * of a peek/poke, what PageAccess to use.  It then forwards this peek/poke to the 
 * PageAccess.  If the PageAccess uses the standard "indirect" mode, it simply
 * forwards the peek/poke to its client (the device).  If the client (device) had
 * previously set up this page access with an array, the page access can just interact
 * with this surrogate array instead of bothering the device.  This is called the direct
 * mode, and it can be used for peek, poke, or both.
 */
public class PageAccess implements java.io.Serializable {
    private final static long serialVersionUID = -6487146100140974640L;
    
       
        private int myDirectPeekBaseIndex=0;
        private int myDirectPokeBaseIndex=0;
        private int[] myDirectPeekMemory=null;
        private int[] myDirectPokeMemory=null;
    
        private IfcDevice myDevice=null;
        
        
     
        
    
        
        public PageAccess(IfcDevice aDevice) {
            setDevice(aDevice);
        }
        
         private void writeObject(ObjectOutputStream out) throws IOException
          {
            out.defaultWriteObject();
           /*  out.writeInt(myDirectPeekBaseIndex);
             out.writeInt(myDirectPokeBaseIndex);
             out.writeObject(myDirectPeekMemory);
             out.writeObject(myDirectPokeMemory);
             out.writeObject(myDevice);
            */
          } 
    
        private void readObject(ObjectInputStream in)  throws IOException, ClassNotFoundException {
             in.defaultReadObject();
          /*   myDirectPeekBaseIndex=in.readInt();
             myDirectPokeBaseIndex=in.readInt(); //out.write(directPokeBaseIndex);
             myDirectPeekMemory=(int[])in.readObject();
             myDirectPokeMemory=(int[])in.readObject();
             myDevice=(IfcDevice) in.readObject(); 
           */
          }
        
        
        
        public static PageAccess createDirectPeekAccess(IfcDevice aDevice, int[] aDirectPeekMemory, int aDirectPeekBaseIndex)
        {
            PageAccess zReturn=new PageAccess(aDevice);
            zReturn.setDirectPeekMemory(aDirectPeekMemory, aDirectPeekBaseIndex);
            return zReturn;
        }
        
        public static PageAccess createDirectPokeAccess(IfcDevice aDevice, int[] aDirectPokeMemory, int aDirectPokeBaseIndex)
        {
            PageAccess zReturn=new PageAccess(aDevice);
            zReturn.setDirectPokeMemory(aDirectPokeMemory, aDirectPokeBaseIndex);
            return zReturn;
        }
        
        public static PageAccess createIndirectAccess(IfcDevice aDevice)
        {
            PageAccess zReturn=new PageAccess(aDevice);
            zReturn.setIndirectMode();
            return zReturn;
        }
        
        
        public void copyDataFrom(PageAccess aPA) {
            myDevice=aPA.myDevice;
           
            myDirectPeekMemory=aPA.myDirectPeekMemory;
            myDirectPokeMemory=aPA.myDirectPokeMemory;
            myDirectPeekBaseIndex=aPA.myDirectPeekBaseIndex;
            myDirectPokeBaseIndex=aPA.myDirectPokeBaseIndex;
            
        }
     
        public boolean usesDirectPeek() {
           return (myDirectPeekMemory!=null);
        }
        
        public boolean usesDirectPoke() {
         return (myDirectPokeMemory!=null);
        }
        
        public void directPoke(char aPageOffset,int aByteValue) {
            assert((aByteValue>=0)&&(aByteValue<0x100));
            assert(myDirectPokeMemory!=null);
            myDirectPokeMemory[myDirectPokeBaseIndex + aPageOffset]=aByteValue;
        }
        
        public int directPeek(char aPageOffset) {
           int zReturn=0;
            
            assert(myDirectPeekMemory!=null);
            
            zReturn=myDirectPeekMemory[myDirectPeekBaseIndex + aPageOffset];
            if (zReturn<0)
            {
                assert(false);
            }
            assert((zReturn>=0)&&(zReturn<0x100));
        
            return zReturn;
        }
        
        public void setDevice(IfcDevice aDevice) {
            myDevice=aDevice;
        }
        
        public IfcDevice getDevice() {
            return myDevice;
        }
      
       
        
       

     /*   public boolean isPokable() {
            return true;
        }

        public boolean isPeekable() {
            return true;
        }
      */

        public void pagePoke(char aAddress, int aValue) {
            directPoke(aAddress, aValue);
        }

        public int pagePeek(char aAddress) {
            return directPeek(aAddress);
        }

     
        public int peek(int aAddress)
        {
            if (usesDirectPeek()==true) return directPeek((char)(aAddress & PAGE_MASK));
            else return getDevice().peek((char)aAddress);
        }
        
        public void poke(int aAddress, int aByteValue)
        {
            if (usesDirectPoke()==true) directPoke((char)(aAddress& PAGE_MASK), aByteValue);
            else getDevice().poke((char)aAddress, aByteValue);
        }

    /**
     * Turns both directPeek mode and directPoke mode off.
     */
    public void setIndirectMode()
    {
        setDirectPeekMemory(null, 0);
        setDirectPokeMemory(null, 0);
    }
        
    public int[] getDirectPeekMemory() {
        return myDirectPeekMemory;
    }

    /**
     * Sets the array associated with direct-peek mode, thus turning the mode on.
     * @param aDirectPeekMemory the array to route peek requests to
     * @param aDirectPeekBaseIndex the base index that the given address (converted to page offset) will be added to
     */
    public void setDirectPeekMemory(int[] aDirectPeekMemory, int aDirectPeekBaseIndex ) {
        this.myDirectPeekMemory = aDirectPeekMemory;
        myDirectPeekBaseIndex=aDirectPeekBaseIndex;
    }

    public int[] getDirectPokeMemory() {
        return myDirectPokeMemory;
    }

    public void setDirectPokeMemory(int[] aDirectPokeMemory, int aDirectPokeBaseIndex) {
        this.myDirectPokeMemory = aDirectPokeMemory;
        myDirectPokeBaseIndex=aDirectPokeBaseIndex;
    }

 
        
    }//END CLASS : PageAccess