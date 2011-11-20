/*
 * InputMaster.java
 *
 * Created on August 2, 2007, 9:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */



package jstella.runner;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.MemoryImageSource;
import javax.swing.event.MouseInputListener;
import javax.swing.event.MouseInputListener;
import jstella.core.*;


import jstella.core.JSController.PaddleID;
import java.awt.*;
import static jstella.core.JSConstants.*;
/**
 * This class is used to centralize the handling of input, e.g. keyboard and mouse 
 * events.  
 * 
 * Some of the input events have to do with console switches (e.g. reset), and 
 * they are funelled to JSConsole "console switch" methods.  
 * 
 * Others represent joystick and paddle actions, and they are routed to the proper
 * Controller object.
 * 
 * The concept of "binder" here is something that links the user event 
 * (e.g. the spacebar pressed) with a virtual event (e.g. the fire button being pressed
 * on the left joystick).  These aren't "hard-wired" (or hard-coded), so that the user
 * can configure what-key-does-what.
 * @author J.L. Allen
 */
public class InputMaster {
    public static Cursor INVISIBLE_CURSOR;
    
    static{
        createInvisibleCursor();
    }
    
  
    
    private final static int DEFAULT_PADDLE_SHIFT_PERCENTAGE=3;
    
    
    

    
    
    
    
    private PaddleMouse[] myPaddleMouse={new PaddleMouse(Jack.LEFT, PaddleID.PADDLE_ALPHA),
    new PaddleMouse(Jack.LEFT, PaddleID.PADDLE_BETA),
    new PaddleMouse(Jack.RIGHT, PaddleID.PADDLE_ALPHA),
    new PaddleMouse(Jack.RIGHT, PaddleID.PADDLE_BETA)};
    
    
    
    public final static InputControlBinder[] DEFAULT_CONTROL_BINDERS= {
        new InputControlBinder(KeyEvent.VK_UP, VirtualControlTask.JOYSTICK_A_UP),
        new InputControlBinder(KeyEvent.VK_DOWN, VirtualControlTask.JOYSTICK_A_DOWN),
        new InputControlBinder(KeyEvent.VK_LEFT, VirtualControlTask.JOYSTICK_A_LEFT),
        new InputControlBinder(KeyEvent.VK_RIGHT, VirtualControlTask.JOYSTICK_A_RIGHT),
        new InputControlBinder(KeyEvent.VK_SPACE, VirtualControlTask.JOYSTICK_A_BUTTON),
        new InputControlBinder(KeyEvent.VK_COMMA, VirtualControlTask.PADDLE_A_CCW),
        new InputControlBinder(KeyEvent.VK_PERIOD, VirtualControlTask.PADDLE_A_CW),
        new InputControlBinder(KeyEvent.VK_Z, VirtualControlTask.PADDLE_A_BUTTON),
        new InputControlBinder(KeyEvent.VK_G, VirtualControlTask.BOOSTERGRIP_A_BOOSTER),
        new InputControlBinder(KeyEvent.VK_F, VirtualControlTask.BOOSTERGRIP_A_TRIGGER)
        
        
        
        
    };
    
     public final static InputSwitchBinder[] DEFAULT_SWITCH_BINDERS=
    { 
        new InputSwitchBinder(KeyEvent.VK_F1, VirtualSwitchTask.SWITCH_RESET),
        new InputSwitchBinder(KeyEvent.VK_F2, VirtualSwitchTask.SWITCH_SELECT),
        new InputSwitchBinder(KeyEvent.VK_F3, true, VirtualSwitchTask.SWITCH_BW, true),
        new InputSwitchBinder(KeyEvent.VK_F4, true, VirtualSwitchTask.SWITCH_BW, false),
        new InputSwitchBinder(KeyEvent.VK_F5, true, VirtualSwitchTask.SWITCH_DIFFICULTY_P0, true),
        new InputSwitchBinder(KeyEvent.VK_F6, true, VirtualSwitchTask.SWITCH_DIFFICULTY_P0, false),
        new InputSwitchBinder(KeyEvent.VK_F7, true, VirtualSwitchTask.SWITCH_DIFFICULTY_P1, true),
        new InputSwitchBinder(KeyEvent.VK_F8, true, VirtualSwitchTask.SWITCH_DIFFICULTY_P1, false)
    };
    
    public enum PaddleMouseAxis { MOUSE_X, MOUSE_Y, MOUSE_XY};
    
  
    
    private java.util.Map<Integer, InputSwitchBinder> myInputSwitchMap=new java.util.HashMap<Integer, InputSwitchBinder>();
    private java.util.Map<Integer, InputControlBinder> myInputControlMap=new java.util.HashMap<Integer, InputControlBinder>();
    private boolean myMouseMovementPaddleMode=false;
    private boolean myPaddleModeLock=false;
    
    private IfcInputMasterClient myInputMasterClient=null;
    private KeyListener myKeyListener=null;
    
     private Component myPaddleMouseComponent=null;
 
    private PaddleMouseAxis myPaddleMouseAxis=PaddleMouseAxis.MOUSE_X;
    
    private int myPaddleShiftPercentage=DEFAULT_PADDLE_SHIFT_PERCENTAGE; //percentage of total range that a keypress will cause the paddle to turn
    
    //TODO : have mouse control paddle better
    
  
    
    
    
    
    
    
    
    
    
    /** Creates a new instance of InputMaster */
    private InputMaster() {
           
        myKeyListener=new JSKeyListener();
        setControls(DEFAULT_CONTROL_BINDERS);
        setSwitches(java.util.Arrays.asList(DEFAULT_SWITCH_BINDERS));
    }
    
    /**
     * Creates a new instance of InputMaster.
     * @param aClient The client (usually the Intercessor object)
     */
    public InputMaster(IfcInputMasterClient aClient) {
        this();
        setInputMasterClient(aClient);
    }
    
    /**
     * This associates a PaddleMouse object with the given component,
     * adding the appropriate mouse listeners.
     * 
     * The paddle index corresponds to what virtual paddle.  
     * (The 2600 allowed four paddles, two for each jack. 0 = first (main) paddle).
     * @param aPaddleIndex 0 for main paddle; 1,2, or 3 for the other 3 available paddles
     * @param aComp the component in which to install the mouse listeners, etc.
     */
      public void addPaddleToComponent(int aPaddleIndex, java.awt.Component aComp) {
        myPaddleMouseComponent=aComp;
        PaddleMouse zPM=getPaddleMouse(aPaddleIndex);
        changeCursor(aComp, !myMouseMovementPaddleMode);
        aComp.addMouseListener(zPM);
        aComp.addMouseMotionListener(zPM);
        aComp.addMouseWheelListener(zPM);
      
        
    }
    
    
    private static void createInvisibleCursor() {
        int[] pixels = new int[16 * 16];
        Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
        Cursor zCursor =Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisibleCursor");
        INVISIBLE_CURSOR= zCursor;
    }
    
    
    /**
     *   
     * If paddle mode is locked, then pressing the right mouse button (or whatever else)
     * will not automatically result in the toggling of paddle mode on/off.  Thus,
     * if paddle mode is locked when paddle mode is off, paddle mode will remain off.
     * If locked when paddle mode is on, paddle mode will remain on.  This is useful for
     * applets that don't want the user accidentally exiting paddle mode, and not being
     * able to figure out what happened.
     * @param aLocked true to lock in the current state of paddle mode
     */
    public void setPaddleModeLock(boolean aLocked)
    {
        myPaddleModeLock=aLocked;
    }
    
    /**
     * Paddle mode is when the mouse cursor disappears and the mouse actions
     * are interpreted as paddle actions (see PaddleMouse inner class).
     * @param aEnabled true to turn paddle mode on
     */
    public void setPaddleMode(boolean aEnabled)
    {
        setPaddleMode(myPaddleMouseComponent, aEnabled);
    }
    
    /**
     * Paddle mode is when the mouse cursor disappears and the mouse actions
     * are interpreted as paddle actions (see PaddleMouse inner class).
     * 
     * If the paddle mouse has already been added to a component, the other
     * setPaddleMode method may be called.
     * @param aComp the component in which the cursor will disappear/become visible again
     * @param aEnabled true to turn paddle mode on
     */
    public void setPaddleMode(Component aComp, boolean aEnabled)
    {
       if (myPaddleModeLock==false)
       {
        myMouseMovementPaddleMode=aEnabled;
       }//end : not locked
        if (aComp!=null) changeCursor(aComp, !myMouseMovementPaddleMode);
    }
    
    public boolean getPaddleMode()
    {
        return myMouseMovementPaddleMode;
    }
    
    private void toggleMouseMode(Component aComp) {
      
        setPaddleMode(aComp, !getPaddleMode());
        
       
    }
    
    
    public void setPaddleMouseAxis(PaddleMouseAxis aAxis)
    {
        myPaddleMouseAxis=aAxis;
    }
    
    public PaddleMouseAxis getPaddleMouseAxis()
    {
        return myPaddleMouseAxis;
    }
    
    
    /**
     * Performs the same thing as the other setControls methods, but extracts
     * the relevant information from the user config map to do so.
     * @param aConfigMap the user config map, which possibly contains control key mappings
     */
    public void setControls(java.util.Map<String, String> aConfigMap) {
        java.util.List<InputControlBinder> zCIList=convertConfigMapToControlBinderList(aConfigMap);
        setControls(zCIList);
        
        
    }
    
    /**
     * This is used to apply the given control binder objects, thus 
     * equating certain keyboard keys with certain controller events.
     * @param aControlItemList list of controller binders
     */
    public void setControls(java.util.List<InputControlBinder> aControlItemList) {
        myInputControlMap.clear();
        
        for (InputControlBinder zItem : aControlItemList) {
            myInputControlMap.put(new Integer(zItem.getVKCode()), zItem);
        }//end : for loop
    }
    
    /**
     * Performs the same thing as the other setControls methods.
     * @param aControlItemArray an array of control binders
     */
    public void setControls(InputControlBinder[] aControlItemArray) {
        setControls(java.util.Arrays.asList(aControlItemArray));
    }
    
    public void setSwitches(java.util.List<InputSwitchBinder> aSwitchMappingList)
    {
          myInputSwitchMap.clear();
        
        for (InputSwitchBinder zItem : aSwitchMappingList) {
            myInputSwitchMap.put(new Integer(zItem.getVKCode()), zItem);
        }//end : for loop
    }
    
    
    private void changeCursor(Component aComp, boolean aVisible) {
        if (aVisible==true) aComp.setCursor(Cursor.getDefaultCursor());
        else aComp.setCursor(INVISIBLE_CURSOR);
    }
    
  
    
    public void setInputMasterClient(IfcInputMasterClient aClient) {
        myInputMasterClient=aClient;
    }
    
    
    public PaddleMouse getPaddleMouse(int aIndex) {
        return myPaddleMouse[aIndex];
    }
    
    public java.awt.event.KeyListener getKeyListener() {
        return myKeyListener;
    }
    
    
    public static void addDefaultControlItemsToConfigMap(java.util.Map<String, String> aConfigMap) {
        
        addControlBinderListToConfigMap(java.util.Arrays.asList(DEFAULT_CONTROL_BINDERS), aConfigMap);
    }
    
    public static boolean checkConfigMapForControls(java.util.Map<String, String> aConfigMap) {
        boolean zFoundControl=false;
        VirtualControlTask[] zVCI=VirtualControlTask.values();
        for (int i=0; i<zVCI.length; i++) {
            if (aConfigMap.get(zVCI[i].getConfigKey())!=null) {
                zFoundControl=true;
                break;
            }//end : found control
        }//end : for i loop
        return zFoundControl;
    }
    
    
    /**
     * This is the main method for taking care of keyboard actions.  It is usually
     * called by a KeyListener in the Canvas object.  When the user presses a key
     * down, or releases a key, this method is called.  It performs different actions,
     * depending on what the current key configuration is.
     * @param aVKCode the virtual key code (as in the KeyEvent class)
     * @param aPressed true if the key was pressed (down), false if the key was released (up)
     * @return true if the method performed an action based on this key
     */
    public boolean processInputKeyEvent(int aVKCode, boolean aPressed) {
        boolean zReturn=false;
        Integer zVK=new Integer(aVKCode);
        
        
        InputControlBinder zCI=myInputControlMap.get(zVK);
        if (zCI!=null) {
            performControlItemAction(zCI.getTarget(), aPressed);
            
        }//end : not null
        
        
        InputSwitchBinder zSM=myInputSwitchMap.get(zVK);
        if (zSM!=null) {
            if (zSM.myFullControl==true) {
                performSwitchTask(zSM.getVirtualSwitchTask(), aPressed, myInputMasterClient);
              
                zReturn=true;
            }//end : full control, i.e. both up and down
            else if (zSM.myKeyPressed==aPressed) {
                performSwitchTask(zSM.getVirtualSwitchTask(), zSM.myTurnOn, myInputMasterClient);
                zReturn=true;
            }//end : not full control, matches key state
            
            
            
        }
        return zReturn;
    }
    
    
    private void performControlItemAction(VirtualControlTask aVItem, boolean aPressed) {
        JSController zSControllerA=(JSController)myInputMasterClient.getConsole().getController(Jack.LEFT);
        JSController zSControllerB=(JSController)myInputMasterClient.getConsole().getController(Jack.RIGHT);
        switch (aVItem) {
            case JOYSTICK_A_UP : zSControllerA.changeControllerState(JSController.JOYSTICK_UP, aPressed); break;
            case JOYSTICK_A_DOWN : zSControllerA.changeControllerState(JSController.JOYSTICK_DOWN, aPressed); break;
            case JOYSTICK_A_LEFT : zSControllerA.changeControllerState(JSController.JOYSTICK_LEFT, aPressed); break;
            case JOYSTICK_A_RIGHT : zSControllerA.changeControllerState(JSController.JOYSTICK_RIGHT, aPressed); break;
            case JOYSTICK_A_BUTTON : zSControllerA.changeControllerState(JSController.JOYSTICK_BUTTON, aPressed); break;
            
            case JOYSTICK_B_UP : zSControllerB.changeControllerState(JSController.JOYSTICK_UP, aPressed); break;
            case JOYSTICK_B_DOWN : zSControllerB.changeControllerState(JSController.JOYSTICK_DOWN, aPressed); break;
            case JOYSTICK_B_LEFT : zSControllerB.changeControllerState(JSController.JOYSTICK_LEFT, aPressed); break;
            case JOYSTICK_B_RIGHT : zSControllerB.changeControllerState(JSController.JOYSTICK_RIGHT, aPressed); break;
            case JOYSTICK_B_BUTTON : zSControllerB.changeControllerState(JSController.JOYSTICK_BUTTON, aPressed); break;
            
            case PADDLE_A_BUTTON : zSControllerA.changeControllerState(JSController.PADDLE_ALPHA_BUTTON, aPressed); break;
            case PADDLE_B_BUTTON : zSControllerA.changeControllerState(JSController.PADDLE_BETA_BUTTON, aPressed); break;
            case PADDLE_C_BUTTON : zSControllerB.changeControllerState(JSController.PADDLE_ALPHA_BUTTON, aPressed); break;
            case PADDLE_D_BUTTON : zSControllerB.changeControllerState(JSController.PADDLE_BETA_BUTTON, aPressed); break;
            
            case PADDLE_A_CW : if (aPressed) zSControllerA.changePaddlePosition(JSController.PaddleID.PADDLE_ALPHA, myPaddleShiftPercentage); break;
            case PADDLE_B_CW : if (aPressed) zSControllerA.changePaddlePosition(JSController.PaddleID.PADDLE_BETA, myPaddleShiftPercentage); break;
            case PADDLE_C_CW : if (aPressed) zSControllerB.changePaddlePosition(JSController.PaddleID.PADDLE_ALPHA, myPaddleShiftPercentage); break;
            case PADDLE_D_CW : if (aPressed) zSControllerB.changePaddlePosition(JSController.PaddleID.PADDLE_BETA, myPaddleShiftPercentage); break;
            
            case PADDLE_A_CCW : if (aPressed) zSControllerA.changePaddlePosition(JSController.PaddleID.PADDLE_ALPHA, -1 * myPaddleShiftPercentage); break;
            case PADDLE_B_CCW : if (aPressed) zSControllerA.changePaddlePosition(JSController.PaddleID.PADDLE_BETA, -1 * myPaddleShiftPercentage); break;
            case PADDLE_C_CCW : if (aPressed) zSControllerB.changePaddlePosition(JSController.PaddleID.PADDLE_ALPHA, -1 * myPaddleShiftPercentage); break;
            case PADDLE_D_CCW : if (aPressed) zSControllerB.changePaddlePosition(JSController.PaddleID.PADDLE_BETA, -1 * myPaddleShiftPercentage); break;
            
            case BOOSTERGRIP_A_BOOSTER : zSControllerA.setBoosterGripBooster(aPressed); break;
            case BOOSTERGRIP_B_BOOSTER : zSControllerB.setBoosterGripBooster(aPressed); break;
            
            case BOOSTERGRIP_A_TRIGGER : zSControllerA.setBoosterGripTrigger(aPressed); break;
            case BOOSTERGRIP_B_TRIGGER : zSControllerB.setBoosterGripTrigger(aPressed); break;
             
            
        }//end : switch
    }
    
    private void performSwitchTask(VirtualSwitchTask aTask, boolean aTurnOn, IfcInputMasterClient aMonitor) {
      
           
                    ConsoleSwitch zConsoleSwitch=aTask.getConsoleSwitch(); 
                    myInputMasterClient.getConsole().flipSwitch(zConsoleSwitch, aTurnOn);
                    if (aMonitor!=null) aMonitor.switchFlipped();
                 
        
    }
    
 
    
    /**
     * This is a quick way of setting whether controller A's button is pressed.
     * Mostly used by VirtualJoystick
     * @param aButtonPressed true if the button is down
     */
    public void setControllerAButton(boolean aButtonPressed) {
        JSController zSControllerA=(JSController)myInputMasterClient.getConsole().getController(Jack.LEFT);
        zSControllerA.changeControllerState(JSController.JOYSTICK_BUTTON, aButtonPressed);
    }
    
    /**
     * This is a quick way of setting the current direction(s) of controller A (the left
     * controller).  This is intended mostly for the VirtualJoystick class.
     * @param aUpDownLeftRight an boolean[4] array specifying whether the following directions are pressed : up, down, left, right
     */
    public void setControllerADirections(boolean[] aUpDownLeftRight) {
        JSController zSControllerA=(JSController)myInputMasterClient.getConsole().getController(Jack.LEFT);
        
        
        zSControllerA.changeControllerState(JSController.JOYSTICK_UP, aUpDownLeftRight[0]);
        zSControllerA.changeControllerState(JSController.JOYSTICK_DOWN, aUpDownLeftRight[1]);
        zSControllerA.changeControllerState(JSController.JOYSTICK_LEFT, aUpDownLeftRight[2]);
        zSControllerA.changeControllerState(JSController.JOYSTICK_RIGHT, aUpDownLeftRight[3]);
        
    }
    
    /**
     * This is a convenience method that performs the reverse of convertIntegerArrayToString.
     * It is used when reading user config items.
     * @param aIntString A string that was previously formed by the convertIntegerArrayToString method
     * @return an array of ints, corresponding to those in the specified string
     * @throws java.lang.NumberFormatException this is thrown if the numbers in the string are incorrectly formatted
     */
    public static int[] convertIntegerStringToArray(String aIntString) throws NumberFormatException {
        int[] zReturn=new int[0];
        
        String zCurrentString=aIntString;
        java.util.List<Integer> zIntList=new java.util.ArrayList<Integer>();
        boolean zBreak=false;
        do{
            int zLeftIndex=zCurrentString.indexOf("(");
            int zRightIndex=zCurrentString.indexOf(")");
            if ((zLeftIndex!=-1) && (zRightIndex!=-1) && (zLeftIndex < zRightIndex)) {
                String zSubStr=zCurrentString.substring(zLeftIndex + 1, zRightIndex);
                int zParsedInt=Integer.parseInt(zSubStr);
                zIntList.add(new Integer(zParsedInt));
                zCurrentString=zCurrentString.substring(zRightIndex+1);
            } else zBreak=true;
            
        }while (zBreak==false);
        
        zReturn=new int[zIntList.size()];
        for (int i=0; i<zReturn.length; i++) {
            zReturn[i]=zIntList.get(i).intValue();
        }//end : for i loop
        
        return zReturn;
    }
    
    /**
     * This is a convenience method that takes the integers in a given array,
     * and makes a string out of them, with each inside a set of parentheses.
     * e.g. (12) (18) (24).
     * 
     * Because all user config items are stored as strings, it is necessary to perform
     * this conversion.
     * @param aArray an array of ints
     * @return a string containing the specified ints
     */
    public static String convertIntegerArrayToString(int[] aArray) {
        StringBuffer zSB=new StringBuffer();
        for (int i=0; i<aArray.length; i++) {
            zSB.append("(" + aArray[i] + ")");
        }//end : for i loop
        return zSB.toString();
    }
    
    
    public static void addControlBinderListToConfigMap(java.util.List<InputControlBinder> aControlBinderList, java.util.Map<String, String> aConfigMap) {
        int zCount=aControlBinderList.size();
        for (InputControlBinder zCI : aControlBinderList) {
            String zStrToAdd="(" + zCI.getVKCode() + ")";
            if (aConfigMap.containsKey(zCI.getTarget().getConfigKey())==true) {
                zStrToAdd="" + aConfigMap.get(zCI.getTarget().getConfigKey()) + zStrToAdd;
            }//end : already has key
            aConfigMap.put(zCI.getTarget().getConfigKey(), zStrToAdd);
        }//for loop
    }
    
    public static java.util.List<InputControlBinder> convertConfigMapToControlBinderList(java.util.Map<String, String> aConfigMap) {
        java.util.List<InputControlBinder> zReturn=new java.util.ArrayList<InputControlBinder>();
        
        
        VirtualControlTask[] zVCIArray=VirtualControlTask.values();
        for (int iItem=0; iItem<zVCIArray.length; iItem++) {
            if (aConfigMap.containsKey(zVCIArray[iItem].getConfigKey())==true) {
                String zValue=aConfigMap.get(zVCIArray[iItem].getConfigKey());
                int[] zValueInts=InputMaster.convertIntegerStringToArray(zValue);
                for (int iValue=0; iValue<zValueInts.length; iValue++) {
                    zReturn.add(new InputControlBinder(zValueInts[iValue], zVCIArray[iItem]));
                }//end : for iValue loop
                
            }//end : has key
            
        }//end : for iItem loop
        
        return zReturn;
    }
    
  
    
   
    
    //=================================================
    /**
     * The PaddleMouse object is in charge of getting certain user mouse events and 
     * interpreting them as paddle events for a specified paddle.
     */
    private  class PaddleMouse implements MouseInputListener, MouseWheelListener {
        private Jack myJack=Jack.LEFT;
        private JSController.PaddleID myPaddleID=JSController.PaddleID.PADDLE_ALPHA;
        private int myLastX=0;
        private double myScrollFactor=3.0;
       
        
        
        
        private PaddleMouse(Jack aJack, JSController.PaddleID aPaddleID) {
            myJack=aJack;
            myPaddleID=aPaddleID;
        }
        
        
        
        
        private void paddleMouseMoved(MouseEvent e)
        {
            if (myMouseMovementPaddleMode==true) {
                Component zComp=e.getComponent();
                
                if (zComp!=null) {
                    
                    myLastX=e.getX();
                    int zPercentage=0;
                    
                    
                    if (getPaddleMouseAxis()==PaddleMouseAxis.MOUSE_X) zPercentage=(int)(((double)myLastX / (double)zComp.getWidth()) * 100);
                    else /*if (getPaddleMouseAxis()==PaddleMouseAxis.MOUSE_Y)*/ zPercentage=(int)(((double)e.getY() / (double)zComp.getHeight()) * 100);
                    
                    JSController zCtrl=myInputMasterClient.getConsole().getController(myJack);
                    if (zCtrl != null)  {
                        
                        
                        zCtrl.setPaddlePosition(myPaddleID, zPercentage);
                    }//end : is super controller
                }//end : zComp not null
            }//end : mouse movement paddle mode == true
        }
        
        public void mouseMoved(MouseEvent e) {
            paddleMouseMoved(e);
        }
        
        
        
        
        public void mouseDragged(MouseEvent e) {
           paddleMouseMoved(e);
        }
        
  /*      private JSController getSuperController() {
            JSController zReturn=null;
            IfcController zCtrl=myInputMasterClient.getConsole().getController(myJack);
            if (zCtrl instanceof JSController) {
                
                zReturn=(JSController)zCtrl;
            }
            assert(zReturn!=null);
            return zReturn;
        }
   */
        
        public void mouseReleased(MouseEvent e) {
            boolean zLMB=javax.swing.SwingUtilities.isLeftMouseButton(e);
            if (zLMB==true) {
                JSController zCtrl=myInputMasterClient.getConsole().getController(myJack);
                if (zCtrl != null) {
                    
                  
                    zCtrl.setPaddleTrigger(myPaddleID, false);
                }//end : is super controller
                
            }//end : is LMB
        }
        
        public void mousePressed(MouseEvent e) {
            boolean zLMB=javax.swing.SwingUtilities.isLeftMouseButton(e);
            if (zLMB==true) {
                JSController zCtrl=myInputMasterClient.getConsole().getController(myJack);
                if (zCtrl != null) {
                    
                    
                    zCtrl.setPaddleTrigger(myPaddleID, true);
                }//end : is super controller
                
            }//end : is LMB
            else if (javax.swing.SwingUtilities.isRightMouseButton(e)==true) toggleMouseMode(e.getComponent());
            
        }
        
        public void mouseExited(MouseEvent e) {
        }
        
        public void mouseEntered(MouseEvent e) {
        }
        
        public void mouseClicked(MouseEvent e) {
        }
        
        public void mouseWheelMoved(MouseWheelEvent e) {
            
            //JSController zSC=getSuperController();
            JSController zCtrl=myInputMasterClient.getConsole().getController(myJack);
            zCtrl.changePaddlePosition(myPaddleID, (int)((double)e.getWheelRotation() * myScrollFactor));
            
        }
        
    }
    
    //========================================================================
    private class JSKeyListener extends java.awt.event.KeyAdapter {
        public void keyPressed(KeyEvent e) {
            
            super.keyPressed(e);
            boolean zMatch=processInputKeyEvent(e.getKeyCode(), true);
            if (zMatch==true) e.consume();
        }
        
        public void keyReleased(KeyEvent e) {
            super.keyReleased(e);
            boolean zMatch=processInputKeyEvent(e.getKeyCode(), false);
            if (zMatch==true) e.consume();
        }
    }
    
    //======================================================================
    /**
     * This should be implemented by the class that creates InputMaster objects.
     * It is used so that the InputMaster object can communicate with the other
     * objects.
     */
    public interface IfcInputMasterClient {
        /**
         * This is called when the InputMaster handles a console switch keyboard event, so 
         * that the client/GUI class may update the display, etc.
         */
        public void switchFlipped();
        /**
         * The implementing class should return the JSConsole object
         * @return the console object
         */
        public JSConsole getConsole();        
    }
  
    
    //====================================================================
    /**
     * These are the "virtual switch tasks", representing events that are applied
     * to the 2600 console's switches.
     */
    public enum VirtualSwitchTask {
        SWITCH_RESET(ConsoleSwitch.SWITCH_RESET, "Switch : Reset"), 
        SWITCH_SELECT(ConsoleSwitch.SWITCH_SELECT, "Switch : Select"), 
        SWITCH_BW(ConsoleSwitch.SWITCH_BW, "Switch : B/W mode"), 
        SWITCH_DIFFICULTY_P0(ConsoleSwitch.SWITCH_DIFFICULTY_P0, "Switch : Left player difficulty"), 
        SWITCH_DIFFICULTY_P1(ConsoleSwitch.SWITCH_DIFFICULTY_P1, "Switch : Right player difficulty");
        
        private String myDescription="";
        private ConsoleSwitch myConsoleSwitch=null;
        private VirtualSwitchTask(ConsoleSwitch aSwitch, String aDescription){
            myConsoleSwitch=aSwitch; 
            myDescription=aDescription; } 
        
        public ConsoleSwitch getConsoleSwitch() { return myConsoleSwitch; }
        
    }
    
    
     //===========================================================
    /**
     * An instance of this class links a certain user keyboard event (a keyboard key)
     * with a virtual switch task (e.g. the reset button being pressed).
     */
    public static class InputSwitchBinder {
      
        private boolean myTurnOn=true;
        private boolean myFullControl=true; //true= (on key down=turn on, on key down=turn off)
        private VirtualSwitchTask myVirtualSwitchTask=VirtualSwitchTask.SWITCH_RESET;
        private int myVKCode=0;
        private boolean myKeyPressed=true; //true=on key down, false=on key up
        
        
        
        /**
         * Creates a new binder which links a SPECIFIC user event (either key down or key up)
         * with a SPECIFIC virtual console switch event (either switch down or switch up).
         * @param aVKCode The virtual key code (as in KeyEvent class)
         * @param aKeyPressed true if this is to bind the pressing of the key, false if this is to bind the release
         * @param aSwitch the console switch to act on
         * @param aTurnOn if true, this object will press the switch; if false, this object will release the switch
         */
        public InputSwitchBinder(int aVKCode, boolean aKeyPressed, VirtualSwitchTask aSwitch, boolean aTurnOn ) {
            myFullControl=false;
            myVKCode=aVKCode;
            myKeyPressed=aKeyPressed;
            myVirtualSwitchTask=aSwitch;
            myTurnOn=aTurnOn;
        }
        
       
        
        /**
         * Creates a new binder with "full control", linking the press/release of a keyboard key
         * to the respective press/release of a console switch.
         * @param aVKCode the virtual key code, as listed in KeyEvent class
         * @param aSwitch the switch to flip
         */
        public InputSwitchBinder(int aVKCode, VirtualSwitchTask aSwitch) {
            myFullControl=true;
            myVKCode=aVKCode;
            myVirtualSwitchTask=aSwitch;
            
        }  
        
        public int getVKCode() { return myVKCode; }
        public VirtualSwitchTask getVirtualSwitchTask() { return myVirtualSwitchTask; }
        
        
        
    } 
    
    // ====================================================================
    
    /**
     * These are the "virtual control tasks", representing controller events.
     */
    public enum VirtualControlTask {
        JOYSTICK_A_UP("Joystick A : up", "joystick.a.up"),
        JOYSTICK_A_DOWN("Joystick A : down", "joystick.a.down"),
        JOYSTICK_A_LEFT("Joystick A : left", "joystick.a.left"),
        JOYSTICK_A_RIGHT("Joystick A : right", "joystick.a.right"),
        JOYSTICK_A_BUTTON("Joystick A : button", "joystick.a.button"),
        
        JOYSTICK_B_UP("Joystick B : up", "joystick.b.up"),
        JOYSTICK_B_DOWN("Joystick B : down", "joystick.b.down"),
        JOYSTICK_B_LEFT("Joystick B : left", "joystick.b.left"),
        JOYSTICK_B_RIGHT("Joystick B : right", "joystick.b.right"),
        JOYSTICK_B_BUTTON("Joystick B : button", "joystick.b.button"),
        
        PADDLE_A_CW("Paddle A : clockwise", "paddle.a.cw"),
        PADDLE_A_CCW("Paddle A : counter-clockwise", "paddle.a.ccw"),
        PADDLE_A_BUTTON("Paddle A : button", "paddle.a.button"),
        
        
        PADDLE_B_CW("Paddle B : clockwise", "paddle.b.cw"),
        PADDLE_B_CCW("Paddle B : counter-clockwise", "paddle.b.ccw"),
        PADDLE_B_BUTTON("Paddle B : button", "paddle.b.button"),
        
        PADDLE_C_CW("Paddle C : clockwise", "paddle.c.cw"),
        PADDLE_C_CCW("Paddle C : counter-clockwise", "paddle.c.ccw"),
        PADDLE_C_BUTTON("Paddle C : button", "paddle.c.button"),
        
        PADDLE_D_CW("Paddle D : clockwise", "paddle.d.cw"),
        PADDLE_D_CCW("Paddle D : counter-clockwise", "paddle.d.ccw"),
        PADDLE_D_BUTTON("Paddle D : button", "paddle.d.button"),
        
        BOOSTERGRIP_A_BOOSTER("Booster Grip A : booster", "boostergrip.a.booster"),
        BOOSTERGRIP_A_TRIGGER("Booster Grip A : trigger", "boostergrip.a.trigger"),
        
        BOOSTERGRIP_B_BOOSTER("Booster Grip B : booster", "boostergrip.b.booster"),
        BOOSTERGRIP_B_TRIGGER("Booster Grip B : trigger", "boostergrip.b.trigger");
        
        
        private String myDescription="";
        private String myConfigKey="";
        
        
        private VirtualControlTask(String aDescription, String aConfigKey) {
            myDescription=aDescription;
            myConfigKey="jstella.control." + aConfigKey;
        }
        
        public String toString() { return myDescription; }
        public String getConfigKey() { return myConfigKey; }
        
        
    }//END : ENUM VirtualControlItem
    
    
    //==========================================================
    /**
     * Objects of this class link a given key with a given controller action.
     */
    public static class InputControlBinder {
        private int myVKCode=0;
        private VirtualControlTask myTarget=null;
        
        /**
         * Creates a binder object, which is used to link a user event with a virtual controller
         * event.
         * @param aVKCode The virtual key code (as in KeyEvent class)
         * @param aTarget the controller action to perform
         */
        public InputControlBinder(int aVKCode, VirtualControlTask aTarget) {
            myVKCode=aVKCode;
            myTarget=aTarget;
        }
        
        public String getActualKeyText() {
            return KeyEvent.getKeyText(myVKCode);
        }
        
        public VirtualControlTask getTarget() {
            return myTarget;
        }
        
        public int getVKCode() {
            return myVKCode;
        }
        
        public int hashCode() {
            if (myTarget!=null) return (myVKCode << 8) ^ myTarget.hashCode();
            else return (myVKCode << 8);
        }
        
        public boolean equals(Object object) {
            boolean zReturn=false;
            if (object instanceof InputControlBinder) {
                InputControlBinder zOther=(InputControlBinder)object;
                if ((zOther.myVKCode==myVKCode)&&(zOther.myTarget==myTarget)) zReturn=true;
            }//end : is same type
            return zReturn;
        }
        
        
        
        
        
    }
    
    
    
    
}
