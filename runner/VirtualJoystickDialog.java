/*
 * VirtualJoystickDialog.java
 *
 * Created on August 19, 2007, 3:32 AM
 */

package jstella.runner;
import java.awt.image.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.net.*;

/**
 *
 * @author  J.L. Allen
 */
public class VirtualJoystickDialog extends javax.swing.JDialog {
    
    private final static double LEASH_FRACTION=0.5; //fraction of radius to serve as maximum radius
    private final static double THRESHOLD_FRACTION=0.2;
    
    private final static String DIRECTORY_RESOURCES="/jstella/resources/";
    private final static String RESOURCE_JOYSTICK_HEAD=DIRECTORY_RESOURCES + "joystickhead.gif";
    private final static String RESOURCE_JOYSTICK_BASE=DIRECTORY_RESOURCES + "joystickbase.gif";
    
    private ImageIcon myBaseImage=null;
    private ImageIcon myHeadImage=null;
    
    private Point myCurrentHeadPoint=new Point();
    private Point myCurrentGrabPoint=new Point();
    
    private InputMaster myInputMaster=null;
    
    private boolean myHeadIsGrabbed=false;
    
    private BufferedImage myBackBuffer=null;
    
    private CanvasPanel myCanvasPanel=new CanvasPanel();
    
    private boolean[] myDirections=new boolean[4];
    
    private Point myDragStartPoint=new Point();
    private boolean myDialogIsGrabbed=false;
    
    private int myHeadHalfWidth=0;
    private int myHeadHalfHeight=0;
    
    
    /** Creates new form VirtualJoystickDialog */
    public VirtualJoystickDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setUndecorated(true);
         this.getContentPane().add(myCanvasPanel, BorderLayout.CENTER);
        loadImages();
       
        validate();
        centerHead();
       // paintBackBuffer();
    }
    
    public VirtualJoystickDialog(java.awt.Frame aParent, InputMaster aInputMaster)
    {
        this(aParent, false);
        myInputMaster=aInputMaster;
    }
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("JStella Joystick");
        setFocusable(false);
        setFocusableWindowState(false);
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VirtualJoystickDialog(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    private boolean hitHead(int aX, int aY)
    {
        boolean zReturn=false;
        if ((aX>=myCurrentHeadPoint.x)&&(aY>=myCurrentHeadPoint.y) && (aX < myCurrentHeadPoint.x + myHeadImage.getIconWidth())
          && ( aY < myCurrentHeadPoint.y + myHeadImage.getIconHeight())) zReturn=true;
        
        
        return zReturn;
    }
    
    
   
   private void registerDirections()
   {
       Point zCenterHead=getHeadCenterPoint();
       Point zCenterBase=getBaseCenterPoint();
       double zThreshold=(myBaseImage.getIconWidth() / 2.0) * THRESHOLD_FRACTION;
       int zRelativeX=zCenterHead.x - zCenterBase.x;
       if (zRelativeX >= zThreshold)
       {
           myDirections[2]=false;
           myDirections[3]=true;
          
       }//end : is right
       else if (zRelativeX <= (zThreshold * -1))
       {
           myDirections[2]=true;
           myDirections[3]=false;
       }//end : is left
       else 
       {
           myDirections[2]=false;
           myDirections[3]=false;
       }//end : is horizontally center
       
       
       int zRelativeY=zCenterHead.y - zCenterBase.y;
       if (zRelativeY >= zThreshold)
       {
           myDirections[0]=false;
           myDirections[1]=true;
          
       }//end : is down
       else if (zRelativeY <= (zThreshold * -1))
       {
           myDirections[0]=true;
           myDirections[1]=false;
       }//end : is up
       else 
       {
           myDirections[0]=false;
           myDirections[1]=false;
       }//end : is vertically center
       
       if (myInputMaster!=null) myInputMaster.setControllerADirections(myDirections);
       
   }
   
   private void startDialogDrag(Point aEventPoint)
   {
       myDragStartPoint.setLocation(aEventPoint);
       myDialogIsGrabbed=true; 
   }
   
   private void stopDialogDrag()
   {
       myDialogIsGrabbed=false;
   }
   
   private void dragDialog(Point aEventPoint)
   {
               Point zCurrentLoc = this.getLocation();
               int zDeltaX=aEventPoint.x - myDragStartPoint.x;
               int zDeltaY=aEventPoint.y - myDragStartPoint.y;
               this.setLocation(zCurrentLoc.x + zDeltaX, zCurrentLoc.y + zDeltaY);
   }
   
    
    
    private double distanceFromCenter(double aProposedHeadX, double aProposedHeadY)
    {
       
        double zBaseCenterX=(double)myBaseImage.getIconWidth() / 2.0;
        double zBaseCenterY=(double)myBaseImage.getIconHeight() / 2.0;
        double zHeadCenterX= aProposedHeadX + ((double)myHeadImage.getIconWidth() / 2.0);
        double zHeadCenterY= aProposedHeadY + ((double)myHeadImage.getIconHeight() / 2.0);
        return Math.abs(Point2D.distance(zBaseCenterX, zBaseCenterY, zHeadCenterX, zHeadCenterY));
             
       
    }
    
    private void loadImages()
    {
        
        URL zBaseURL=this.getClass().getResource(RESOURCE_JOYSTICK_BASE);
        myBaseImage=new ImageIcon(zBaseURL);
        
        URL zHeadURL=this.getClass().getResource(RESOURCE_JOYSTICK_HEAD);
        myHeadImage=new ImageIcon(zHeadURL);
        
        myBackBuffer=new BufferedImage(myBaseImage.getIconWidth(), myBaseImage.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        myCanvasPanel.setPreferredSize(new Dimension(myBackBuffer.getWidth(), myBackBuffer.getHeight()));
       
        myHeadHalfWidth=myHeadImage.getIconWidth() / 2;
        myHeadHalfHeight=myHeadImage.getIconHeight() / 2;
        
        
        this.pack();
        
        

        
        
        
    }
    
    private void paintBackBuffer()
    {
        if (myBackBuffer!=null)
        {
            Graphics2D z2D=myBackBuffer.createGraphics();
             myBaseImage.paintIcon(this, z2D, 0, 0);
            myHeadImage.paintIcon(this, z2D, myCurrentHeadPoint.x, myCurrentHeadPoint.y);
            
            
            
            z2D.dispose();
             myCanvasPanel.repaint();
        }//end : not null
    }
    
    private Point getHeadCenterPoint(int aHeadX, int aHeadY)
    {
        int zX=aHeadX + (myHeadImage.getIconWidth() / 2);
        int zY=aHeadY + (myHeadImage.getIconHeight() / 2);
        return new Point(zX, zY);
        
    }
    
    private Point getHeadCenterPoint()
    {
        return getHeadCenterPoint(myCurrentHeadPoint.x, myCurrentHeadPoint.y);
    }
    
    private Point getBaseCenterPoint()
    {
         int zX=(myBaseImage.getIconWidth() / 2);
        int zY=(myBaseImage.getIconHeight() / 2);
        return new Point(zX, zY);
    }
    
     private void centerHead()
    {
        myCurrentHeadPoint.x = (int) ((myBaseImage.getIconWidth() / 2.0) -  (myHeadImage.getIconWidth() / 2.0  ));
        myCurrentHeadPoint.y = (int) ((myBaseImage.getIconHeight() / 2.0) -  (myHeadImage.getIconHeight() / 2.0  ));
        registerDirections();
        paintBackBuffer();
    }
    
    private void moveHeadCenter(int aNewHeadCenterX, int aNewHeadCenterY)
    {
        moveHead(aNewHeadCenterX - myHeadHalfWidth, aNewHeadCenterY - myHeadHalfHeight);
    }
     
    private void moveHead(int aMouseX, int aMouseY)
    {
       double zProposedHeadX = aMouseX - myCurrentGrabPoint.x;
       double zProposedHeadY = aMouseY - myCurrentGrabPoint.y;
       double zDistanceFromCenter=distanceFromCenter(zProposedHeadX, zProposedHeadY);
       double zLeashLength=((double)myBaseImage.getIconWidth() / 2.0)  * LEASH_FRACTION;
       if (zDistanceFromCenter < zLeashLength)
       {
           myCurrentHeadPoint.x = (int)zProposedHeadX;
           myCurrentHeadPoint.y = (int)zProposedHeadY;
       }//end : leash will reach
       else
       {
           //Beyond the length of leash, so move as close as possible
           Point zBaseCenter=getBaseCenterPoint();
           Point zProposedHeadCenter=getHeadCenterPoint((int)zProposedHeadX, (int)zProposedHeadY);
           int zDeltaX=zProposedHeadCenter.x - zBaseCenter.x;
           int zDeltaY=zProposedHeadCenter.y - zBaseCenter.y;
           
           double zAcceptableFraction=zLeashLength / zDistanceFromCenter;
           myCurrentHeadPoint.x = zBaseCenter.x + ((int)(zDeltaX * zAcceptableFraction)) - myHeadHalfWidth;
           myCurrentHeadPoint.y = zBaseCenter.y + ((int)(zDeltaY * zAcceptableFraction)) - myHeadHalfHeight;
           
           
         
           
       }//end : get as close as possible
       
      // System.out.println("(" + aMouseX + "," + aMouseY + ")");
       registerDirections();
       paintBackBuffer();
       
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    public class CanvasPanel extends JPanel implements MouseListener, MouseMotionListener
    {
        public CanvasPanel()
        {
            addMouseListener(this);
            addMouseMotionListener(this);
        }
        
        public void paint(Graphics g) {
            super.paint(g);
            if (myBackBuffer!=null) g.drawImage(myBackBuffer, 0, 0, null);
        }

        public void mouseReleased(MouseEvent e) {
            
             if (SwingUtilities.isLeftMouseButton(e)==true) 
            {
               centerHead();
               myHeadIsGrabbed=false; 
            }//end : LMB
            else if (SwingUtilities.isRightMouseButton(e)==true)
            {
                if (myInputMaster!=null) myInputMaster.setControllerAButton(false);
                stopDialogDrag();
                
            }//end : RMB  
            else if (SwingUtilities.isMiddleMouseButton(e)==true)
            {
                stopDialogDrag();
            }
        }

        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)==true) 
            {
                if (hitHead(e.getX(), e.getY())==true)
                {
                    myCurrentGrabPoint.x=e.getX() - myCurrentHeadPoint.x;
                    myCurrentGrabPoint.y=e.getY() - myCurrentHeadPoint.y;
                    myHeadIsGrabbed=true;
                }//end : is a hit
                else
                {
                    myCurrentGrabPoint.x=myHeadHalfWidth;
                    myCurrentGrabPoint.y=myHeadHalfHeight;
                    
                    myHeadIsGrabbed=true;
                    moveHead(e.getX(), e.getY());
                }//end : a miss
                     
               
            }//end : LMB
            else if (SwingUtilities.isRightMouseButton(e)==true)
            {
                if (myInputMaster!=null) myInputMaster.setControllerAButton(true);
                startDialogDrag(e.getPoint());
                
            }//end : RMB
            else if (SwingUtilities.isMiddleMouseButton(e)==true)
            {
                startDialogDrag(e.getPoint());
               
            }
        }

        public void mouseMoved(MouseEvent e) {
            if (myHeadIsGrabbed==true)
                moveHead(e.getX(), e.getY());
                
            
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
            if (myDialogIsGrabbed==true)
            {
               dragDialog(e.getPoint());
      
            }
            else if (myHeadIsGrabbed==true) moveHead(e.getX(), e.getY());
        }

        public void mouseClicked(MouseEvent e) {
          
        }
        
        
        
    }
    
    
    
    
    
}