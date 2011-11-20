/*
 * JSController.java
 *
 * Created on August 2, 2007, 8:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jstella.core;

import java.awt.event.ActionEvent;

import static jstella.core.JSConstants.*;

/**
 * A class representing the controllers for the console (e.g. joystick, paddles)
 * @author J.L. Allen, etc.
 */
public class JSController implements java.io.Serializable{
   // private final static long serialVersionUID = 3260266847988351073L; //OLD SuperController value
    private final static long serialVersionUID = -2036930480809642251L;
    public final static int JOYSTICK_UP=0; //pin one
    public final static int JOYSTICK_DOWN=1;
    public final static int JOYSTICK_LEFT=2;
    public final static int JOYSTICK_RIGHT=3;  //pin four
    public final static int JOYSTICK_BUTTON=5; //pin six
    
    public final static int PADDLE_ALPHA_BUTTON=3; //pin four
    public final static int PADDLE_BETA_BUTTON=2; //pin three
    public final static int PADDLE_ALPHA_RESISTANCE=8; //pin nine
    public final static int PADDLE_BETA_RESISTANCE=4; //pin five
    
    public final static int BOOSTERGRIP_BOOSTER=4; //pin five
    public final static int BOOSTERGRIP_TRIGGER=8; //pin nine
    /**
     * The Atari 2600 has the potential for 4 paddles at a time, with two for each jack.
     * This type specifies which paddle in the jack is being referred to, alpha or beta.
     */
    public enum PaddleID { PADDLE_ALPHA, PADDLE_BETA};
    
    private Jack myJack=Jack.LEFT;
    
    private int[] myPinValue=new int[9];
    
    
    /**
     * Creates a new instance of JSController
     * @param aJack What jack the controller corresponds to
     */
    public JSController(Jack aJack) {
        //setSystem(aSystem);
        resetController();
    }
    
    public void resetController() {
        for (int i = 0; i < myPinValue.length; i++) {
            myPinValue[i]=1;
        }
        setPaddlePosition(PaddleID.PADDLE_ALPHA, 00);
        setPaddlePosition(PaddleID.PADDLE_BETA, 00);
    }
    
    /**
     * Is called to read the value (true or false) of a given pin
     * @param pin what pin to read
     * @return whether the pin is "on"
     */
    public boolean read(DigitalPin pin) {
        return (myPinValue[getPinIndex(pin)]!=0);
    }
    
    public void write(DigitalPin pin, boolean value) {
    }
    
    /**
     * This reads the resistance value associated with the given pin.
     * 
     * The paddles for the Atari 2600 consisted (in part) of a potentiometer, which is 
     * an electrical resistor that varies its resistance based upon where a dial setting
     * is...[this is the same type of thing as the volume control on an electric guitar].
     * This part of the paddle (i.e. the non-button part) uses these analog pins to
     * communicate resistance, and thus position of the dial.
     * @param pin the pin to read
     * @return the resistance
     */
    public int read(AnalogPin pin) {
        return myPinValue[getPinIndex(pin)];
    }
    
    
    public void setJoystickState(int aJoystickDir, boolean aPressed) {
        if (aPressed==true) myPinValue[aJoystickDir]=0;
        else myPinValue[aJoystickDir]=1;
    }
    
    public void setPaddleTrigger(PaddleID aID, boolean aPressed) {
        int zValue=(aPressed) ? 0 : 1;
        if (aID==PaddleID.PADDLE_ALPHA) myPinValue[PADDLE_ALPHA_BUTTON]=zValue;
        else if (aID==PaddleID.PADDLE_BETA) myPinValue[PADDLE_BETA_BUTTON]=zValue;
    }
    
 
    
    public void changeControllerState(int aControlEventType, boolean aOn) {
        
        int zValue=(aOn) ? 0 : 1;
        myPinValue[aControlEventType]=zValue;
    }
    
    
    private static int toPercentX(int aResistance) {
        return (int)(100 - ((double)aResistance / 10000.0));
    }
    
    private static int toResistance(int aPercentX) {
        return (int)(10000.0 * (100 - aPercentX));
    }
    
    /**
     * This sets the position of the paddle in terms of percentage, where
     * 0 is one extreme and 100 is the other.
     * @param aID which of the two paddles in the jack is it
     * @param aPercentage the new position of the paddle dial
     */
    public void setPaddlePosition(PaddleID aID, int aPercentage) {
        //  System.out.println("DEBUG - setting paddle x pos : " + aPercentage);
        // int zRes= (int)(10000.0 * (100 - aPercentage));
        int zNewPercent=Math.min(aPercentage, 100);
        zNewPercent=Math.max(0, zNewPercent);
        int zRes=toResistance(zNewPercent);
        
        if (aID==PaddleID.PADDLE_ALPHA) myPinValue[PADDLE_ALPHA_RESISTANCE]=zRes;
        else if (aID==PaddleID.PADDLE_BETA) myPinValue[PADDLE_BETA_RESISTANCE]=zRes;
    }
    
    public int getPaddlePosition(PaddleID aID) {
        int zIndex= (aID==PaddleID.PADDLE_BETA) ? PADDLE_BETA_RESISTANCE : PADDLE_ALPHA_RESISTANCE;
        return toPercentX(myPinValue[zIndex]);
    }
    
    public void changePaddlePosition(PaddleID aID, int aDeltaPercent) {
        int zCurrent=getPaddlePosition(aID);
        setPaddlePosition(aID, zCurrent + aDeltaPercent);
       // System.out.println("DEBUG - changing paddle pos to " + (zCurrent + aDeltaPercent));
                
    }
    
    
    public void setBoosterGripBooster(boolean aPressed)
    {
        //myPinValue[BOOSTERGRIP_BOOSTER]=aPressed ? RESISTANCE_MAX : RESISTANCE_MIN;
        setPaddlePosition(PaddleID.PADDLE_BETA, (aPressed ? 100 : 0) );
        //System.out.println("debug: booster down=" + aPressed);
    }
    
     public void setBoosterGripTrigger(boolean aPressed)
    {
        //myPinValue[BOOSTERGRIP_TRIGGER]=aPressed ? RESISTANCE_MAX : RESISTANCE_MIN;
         setPaddlePosition(PaddleID.PADDLE_ALPHA, (aPressed ? 100 : 0) );
       //  System.out.println("debug : trigger down=" + aPressed);
    }
    
    
    
    
    private int getPinIndex(DigitalPin aPin) {
        switch (aPin) {
            case One : return 0;
            case Two : return 1;
            case Three : return 2;
            case Four : return 3;
            case Six : return 5;
            default : assert(false);
            return 0;
            
        }//end : switch
    }
    
    private int getPinIndex(AnalogPin aPin) {
        switch (aPin) {
            case Five : return 4;
            case Nine : return 8;
            
            default : assert(false);
            return 0;
            
        }//end : switch
    }
    
    
    
    
    
    
    
}
