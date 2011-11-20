/*
 * IfcConsoleClient.java
 *
 * Created on August 23, 2007, 12:24 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jstella.core;

/**
 * This is an interface for the GUI class that interacts with the JSConsole object.
 * @author J.L. Allen
 */
public interface IfcConsoleClient {
    /**
     * This is called whenever the core classes want access to the GUI's canvas.
     * The GUI classes are in charge of creating and maintaining an object that
     * implements the IfcCanvas interface.
     * @return the GUI's canvas
     */
    public IfcCanvas getCanvas();
    
   
    
}
