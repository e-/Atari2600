/*
 * IfcCanvas.java
 *
 * Created on August 23, 2007, 12:26 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jstella.core;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
/**
 * This interface allows the core emulator classes to paint things to the screen.
 * It should be implemented by a GUI class.
 * @author J.L. Allen
 */
public interface IfcCanvas {
      //public int getCanvasWidth();
     // public int getCanvasHeight();
      
    /**
     * This is called by the core emulator classes to tell the GUI to paint 
     * an image (the back buffer) to the screen.
     * @param aImage the back buffer to paint
     * @param aOriginalWidth the width of the original (unscaled) image to paint
     * @param aOriginalHeight the height of the original (unscaled) image to paint
     * @param aOriginalClip a clip that indicates what part of the unscaled image has changed, and thus needs to
     * be repainted.
     */
      public void paintCanvas(BufferedImage aImage, int aOriginalWidth, int aOriginalHeight, Rectangle aOriginalClip);
    /**
     * This is called by the core emulator classes to tell the GUI to paint 
     * an image (the back buffer) to the screen.
     * @param aImage 
    * @param aOriginalWidth the width of the original (unscaled) image to paint
     * @param aOriginalHeight the height of the original (unscaled) image to paint
     */
      public void paintCanvas(BufferedImage aImage, int aOriginalWidth, int aOriginalHeight);
    
}
