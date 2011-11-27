/*
 * JStellaCanvas.java
 *
 * Created on August 23, 2007, 12:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


/**
 *
 * @author J.L. Allen
 */
function JStellaCanvas(){
   /* this. Rectangle myClippingRectangle=new Rectangle();
    private AffineTransform myTransform=new AffineTransform();
    
    private BufferedImage myImage=null;
    private int myOriginalWidth=10;
    private int myOriginalHeight=10;
    
    private int myPreviousCanvasWidth=10;
    private int myPreviousCanvasHeight=10;
    
    private double myScaleX=1.0;
    private double myScaleY=1.0;
    
    private int myOffsetX=0;
    private int myOffsetY=0;
    
    private boolean myLetterBoxMode=false;
    private Toolkit myDefaultToolkit=null;
    
    private boolean myRefreshNeeded=false;
    */
    /** Creates a new instance of JStellaCanvas */
   
    
/*  public JStellaCanvas() {
            setOpaque(true);
            setBackground(java.awt.Color.BLACK);
            //setBorder(null);
            setFocusable(true);
            //setDoubleBuffered(false); //I don't know if this will improve performance, but I did it anyway... JLA
            this.addComponentListener(new CanvasComponentListener());
            
            
        }*/
        
        /**
         * This method paints the myBackBuffer BufferedImage to the screen.  The clipping
         * is actually handeled by whatever commands the canvas to repaint, usually
         * the drawMediaSource() method.
         * @param g Graphics (2D)
         */
  
        /*public void paint(Graphics g) {
          
            Graphics2D z2D=(Graphics2D)g;
             if (myRefreshNeeded==true)
            {
                z2D.setColor(Color.BLACK);
                //z2D.setColor(Color.GREEN); //debug
                z2D.fillRect(0,0, this.getWidth(), this.getHeight());
                myRefreshNeeded=false;
            }//end : redraw parent
         //   long zNanoA=System.nanoTime();
           if (myImage!=null) 
           {
                z2D.drawImage(myImage, myTransform, null);
                //System.out.println("debug - image height=" + myImage.getHeight());
           }
            syncPainting();
           // java.awt.Toolkit.getDefaultTo
         //  long zNanoB=System.nanoTime();
        }
        
        */
        /*private void syncPainting() //not sure if this is necessary/prudent...needs checking
        {
            if (myDefaultToolkit==null) myDefaultToolkit=java.awt.Toolkit.getDefaultToolkit();
            
            if (myDefaultToolkit!=null) myDefaultToolkit.sync();
        }
        */
        /*private void setOriginalDimensions(int aOriginalWidth, int aOriginalHeight)
        {
           if ((aOriginalWidth!=myOriginalWidth)||(aOriginalHeight!=myOriginalHeight) || (myPreviousCanvasWidth!=getCanvasWidth()) || (myPreviousCanvasHeight!=getCanvasHeight()))
              {
                 myOriginalWidth=aOriginalWidth;
                 myOriginalHeight=aOriginalHeight;
                 myPreviousCanvasWidth=getCanvasWidth();
                 myPreviousCanvasHeight=getCanvasHeight();
                 updateScale();
               
              }//end : change in dimensions
               
           
            
        }
        
       */
        /*
        private void updateScale()
        {
           myTransform.setToIdentity();
            myScaleX=(double)getCanvasWidth() / myOriginalWidth; // / getCanvasWidth();
            myScaleY=(double)getCanvasHeight() / myOriginalHeight; // / getCanvasHeight();
         if (myLetterBoxMode==true)
          {
              double zOriginalRatio=(double)myOriginalWidth * JSConstants.PIXEL_WIDTH_HEIGHT_RATIO /(double)myOriginalHeight;
              double zCanvasRatio=(double)getCanvasWidth()/(double)getCanvasHeight();
              if (zCanvasRatio>zOriginalRatio) 
              {
                  myScaleX=myScaleY * JSConstants.PIXEL_WIDTH_HEIGHT_RATIO;
                  double zBorderWidth= (getCanvasWidth() - (myOriginalWidth * myScaleX)) / 2.0;
                  myOffsetX=(int)zBorderWidth;
                  myOffsetY=0;
                  myTransform.translate(zBorderWidth, 0.0);
              }//end : limiter=height
              else 
              {
                  myScaleY=myScaleX / JSConstants.PIXEL_WIDTH_HEIGHT_RATIO;
                  
                  //myScaleX=myScaleY * JSConstants.PIXEL_WIDTH_HEIGHT_RATIO;
                   double zBorderWidth= (getCanvasHeight() - (myOriginalHeight * myScaleY)) / 2.0;
                   myOffsetY=(int)zBorderWidth;
                   myOffsetX=0;
                  myTransform.translate(0.0, zBorderWidth);
              }//end : limiter=width
              
              
              
          }//end : letter box mode
            myTransform.scale(myScaleX, myScaleY);
          
        }
        */
        /*
        public void setLetterBoxMode(boolean aEnable)
        {
            if (aEnable!=myLetterBoxMode)
            {
            myLetterBoxMode=aEnable;
            updateScale();
            refreshCanvas();
            }//end : changed value
        }
        */
        this.refreshCanvas = function()
        {
/*            updateScale();
            myRefreshNeeded=true;
            repaint();*/
        }
				/*
        public boolean getLetterBoxMode()
        {
            return myLetterBoxMode;
        }
        */
				/*
        this.getCanvasWidth = function() {
            return this.getWidth();
        }
        */
				/*
        public int getCanvasHeight() {
            return this.getHeight();
        }
        */

        /*private void setClippingRectangle(Rectangle aOriginalClip)
        {
            setClippingRectangle(aOriginalClip.x, aOriginalClip.y, aOriginalClip.width, aOriginalClip.height);
        }
        */
				/*
        private void setClippingRectangle(int aClipX, int aClipY, int aClipWidth, int aClipHeight)
        {
         
          int zX=(int)(aClipX *myScaleX);
          int zY=(int)(aClipY * myScaleY);
        
        int zW=(int)((aClipX + aClipWidth) * myScaleX) + 1 - zX;
        int zH=(int)((aClipY + aClipHeight) * myScaleY) + 1 - zY; 
        
        if (myLetterBoxMode==true)
        {
          zX += myOffsetX;
          zY += myOffsetY;
        }//end : letter box mode
        
        myClippingRectangle.setRect(zX, zY, zW, zH);
        }
        
        */

     this.paintCanvas = function(aImage, aOriginalWidth, aOriginalHeight, aOriginalClip) {
        this.myImage=aImage;

				t = getImageData();

				var x = aOriginalClip.x, y= aOriginalClip.y, w = aOriginalClip.width, h = aOriginalClip.height;

				var i, j, c;
				for(i=0;i<w;i++){
					for(j=0;j<h;j++){
						if(x+i>=0 && y+j>=0 && x+i<160)
						{
							c = aImage[(x+i)][(y+j)];
							setPixel(t, x+i, y+j, (c >> 16) & 0xFF, (c >> 8) & 0xFF, c & 0xFF);
						}
					}
				}
				
			putImageData(t);
/*        setOriginalDimensions(aOriginalWidth, aOriginalHeight);
     
        setClippingRectangle(aOriginalClip);
        repaint(myClippingRectangle);*/


        
    }
    
    
   /* public  void paintCanvas(BufferedImage aImage, int aOriginalWidth, int aOriginalHeight) {
        myImage=aImage;
        setOriginalDimensions(aOriginalWidth, aOriginalHeight);
       
       
        repaint();
        
    }
    */
   /* public String toString()
    {
        return "Canvas : " + this.getWidth() + " x " + this.getHeight() + "; scale=" + myScaleX + " x " + myScaleY;
    }*/
    
    
    //====================================================
    
    /*private class CanvasComponentListener implements ComponentListener
    {
        public void componentShown(ComponentEvent componentEvent) {
        }

        public void componentResized(ComponentEvent componentEvent) {
            
            updateScale();
            refreshCanvas();
            //JStellaCanvas.this.repaint();
        }

        public void componentMoved(ComponentEvent componentEvent) {
        }

        public void componentHidden(ComponentEvent componentEvent) {
        }
        
    }
    
    */
    
}
