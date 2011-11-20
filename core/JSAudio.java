/*
 * JSAudio.java
 *
 * Created on July 18, 2007, 9:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jstella.core;
import javax.sound.sampled.*;
import static jstella.core.JSConstants.*;

/**
 * This class is responsible for getting pokes from the TIA and converting
 * them into sound.
 *
 * <p>
 * This class uses Java Sound, specifically the javax.sound.sampled package.
 * It synthesizes sound based on the values of the TIA sound registers.
 * <p>
 * The sound is created in a simple format: linear PCM.  The sample rate (i.e. how many
 * values per second) used is 44,100 (samples/sec), because that seems to be the most
 * commonly supported sample rate among java implementations (based on my very quick
 * not-very-good assessment.)  Each sample is a single signed byte (because that's
 * what the Java 'byte' class is.) Therefore, each sample must be between (inclusive)
 * -128 and 127.  (And so on...)
 * <p>
 * The source data line is used to play the sound.  Make sure that its internal buffer
 * doesn't completely empty, or otherwise, expect popping sounds.
 * <p>
 * Some definitions:
 * <dl>
 *  <dt>sample</dt>
 *  <dd>A single value of digital audio data, representing the amplitude of the wave at that point.  
 * Every sound can be represented as a wave diagram.  Each sample corresponds with a given x value, and the 
 * value of the sample corresponds to the y value at that x value.  </dd>
 * </dl>
 * <dl>
 *<dt>display frame</dt>
 *<dd> An animation frame. Animation is simply a bunch of pictures shown one after the other very fast.  Each one of these pictures is called 
 * a frame.
 * </dd>
 * </dl>
 * <dl>
 * <dt>audio frame</dt>
 * <dd>A group of samples corresponding to the same moment of time...one sample for each channel.
 * Thus, when there is only one channel (mono), samples=audio frames.  In stereo music, there are 
 * twice as many samples as audio frames.  (However, a lot of time, "sample rate" means "sample rate per channel".)
 *</dd>
 *</dl>
 *
 *
 * @author Bradford W. Mott and the Stella team (original)
 * J.L. Allen (Java translation)
 */
public class JSAudio  {
   private final static long serialVersionUID = -2127696001297260042L;
    
    //TODO : get rid of debug stuff after this class is fixed
   
    public final static double CPU_SPEED=1193191.66666667; //6502 speed is 1.19 MHz (million cycles /sec)
    
   // public static boolean DEBUG_AUDIO_PLAY=false;    
   // public static java.util.List<int[]> DEBUG_AUDIO_PLAY_LIST=null;
    
  //  public static boolean DEBUG_AUDIO_UNPOKE=false;
   // public static java.util.List<int[]> DEBUG_AUDIO_UNPOKE_LIST=null;
    
      
    private final static int JAVA_SOUND_SAMPLE_RATE=44100; //44,100 samples/sec
    private final static int TIA_SAMPLE_RATE=31400;
    
    public final static double CYCLES_PER_SAMPLE=CPU_SPEED / (double)JAVA_SOUND_SAMPLE_RATE; //27.056
   
    
    // *** AUDIO FORMAT CONSTANTS ***
    private final static int BYTES_PER_SAMPLE=1;
    private final static int BITS_PER_SAMPLE=BYTES_PER_SAMPLE * 8;
    private final static boolean BIG_ENDIAN=true;
    private final static boolean SIGNED_VALUE=true;
    private final static int CHANNELS=1;
    
    
    
    //  *** AUDIO BUFFER MANAGEMENT ***
    private final static int DEFAULT_BUFFER_CUSHION=4000; //samples left in buffer to prevent underflow
  
    
    
    
    //Java sound objects
    private AudioFormat myAudioFormat=null;
    private SourceDataLine mySDLine=null;
    
    
    //Misc
    private JSConsole myConsole=null;
    private byte[] myPreOutputBuffer=new byte[JAVA_SOUND_SAMPLE_RATE*4];
    private boolean myInitSuccess=false;
    
    
    // Poke/Queue related variables
   // private java.util.Queue<AudioRegisterPoke> myPokeQueue=new java.util.ArrayDeque<AudioRegisterPoke>(); //J6SE only
    private java.util.Queue<AudioRegisterPoke> myPokeQueue=new java.util.LinkedList<AudioRegisterPoke>(); //J5SE
    
    private int myExcessCycles=0;
    private double myCyclePool=0;
    private int myPreviousCycle=0;
    private AudioRegisterPoke myPreviousPoke=null;
    
    
    //Audio registers
    private int[] myAUDC=new int[2];
    private int[] myAUDF=new int[2];
    private int[] myAUDV={1,1};
    
    private int[] myFutureAUDC=new int[2];
    private int[] myFutureAUDF=new int[2];
    private int[] myFutureAUDV=new int[2];
    
    
    //Synthesis related variables
    private FrequencyDivider[] myFrequencyDivider={new FrequencyDivider(), new FrequencyDivider()};//[2];    // Frequency dividers
    private int[] myP4=new int[2];           // 4-bit register LFSR (lower 4 bits used)
    private int[] myP5=new int[2];           // 5-bit register LFSR (lower 5 bits used)
    private int  myOutputCounter=0;
    
    //CONFIG variables
    private int  myChannels=CHANNELS;
    private int  myVolumePercentage=100;
    private int  myVolumeClip=128;
    private int myNominalDisplayFrameRate=60;
    private double myRealDisplayFrameRate=60.0;
    private double myCycleSampleFactor=1.0;
    private double myAdjustedCyclesPerAudioFrame=CYCLES_PER_SAMPLE;
    //private int myWholeCyclesPerAudioFrame=(int)(CYCLES_PER_SAMPLE / CHANNELS);
    
    private int myBufferCushion=DEFAULT_BUFFER_CUSHION * myChannels;
    
    private boolean mySoundEnabled=true;
    
    
    
    
    /**
     * Creates a new instance of JSAudio
     * @param aConsole the console that owns this object
     */
    protected JSAudio(JSConsole aConsole) {
        myConsole=aConsole;
        reset();
        
    }
    
    
    
    public boolean isSoundEnabled() {  return mySoundEnabled;   }
    
    public void setVolume(int percent) {  myVolumePercentage=percent;  }
    
    public void setSoundEnabled(boolean aSoundEnabled) 
      { 
       /* if (aSoundEnabled!=mySoundEnabled)
         {
             reset();
         }//end : change in value
        */
      
        //TODO : better system of figuring out if sound is open/active, and informing user of this
        if ((mySDLine!=null)&&(mySDLine.isOpen()))
        {
        //if (aSoundEnabled==true) mySDLine.start();
        if (aSoundEnabled==false) 
        {
            mySDLine.stop();
            mySDLine.flush();
        }
        }//end : not null
         
         mySoundEnabled = aSoundEnabled;          
     }
    
    public void pauseAudio() 
    {
        //TODO : a more elegant way of stopping audio temporarily, to keep clicks, etc from occuring during a pause
       if (mySDLine!=null) mySDLine.stop();  
    }
    
    
    /**
     * Sets the nominal (theoretical) display frame rate.  This is usually 60 or 50.
     * See documentation for setRealDisplayFrameRate(...).
     * @param aFrameRate 
     */
    protected /*synchronized*/ void setNominalDisplayFrameRate(int aFrameRate) {
        myNominalDisplayFrameRate = aFrameRate;
        myPreviousCycle=0; //? (JLA)
        updateCycleSampleFactor();
    }
    
    /**
     * The sound object bases its frequency on the display's frame rate.  If the game
     * was designed for 60 frames per second display rate, then it expects to be
     * called that many times per second to accurately produce the correct pitch.
     * Because the timers that are used to run the emulator use "millisecond deley" rather
     * than "frequency", certain frame rates can only be approximated.  
     * For example, a delay of 16 ms is 62.5 frames per second, and a delay of 17 ms
     * is 58.82 frames per second.  
     * To remedy this approximation, the class responsible for timing will notify
     * this class (via JSConsole) to compensate for this inexact.
     * @param aRealFrameRate 
     */
    public /*synchronized*/ void setRealDisplayFrameRate(double aRealFrameRate)
    {
        myRealDisplayFrameRate= aRealFrameRate;
        updateCycleSampleFactor();
        
    }
    
    
    
    private void updateCycleSampleFactor()
    {
        myCycleSampleFactor= myRealDisplayFrameRate / (double)myNominalDisplayFrameRate;
     
        myAdjustedCyclesPerAudioFrame=CYCLES_PER_SAMPLE * myCycleSampleFactor;
    }
    
    protected void systemCyclesReset(int aCurrentCycle) {
        myPreviousCycle -= aCurrentCycle;
        
    }
    
    /**
     * Used when user is saving game (aka a state save).  The only data from this class
     * that needs to be saved are the contents of the registers.  (Sometimes a game
     * will set the register early on and not touch it again.)  This is called
     * during the serialization method of JSConsole.
     * @return The audio register data in array form.
     */
    protected int[] getAudioRegisterData()
    {
        int[] zReturn=new int[6];
        zReturn[0]=myAUDC[0];
        zReturn[1]=myAUDC[1];
        zReturn[2]=myAUDF[0];
        zReturn[3]=myAUDF[1];
        zReturn[4]=myAUDV[0];
        zReturn[5]=myAUDV[1];
        return zReturn;
    }
    
    /**
     * This is the opposite of the getAudioRegisterData() method.  It is called 
     * by JSConsole after a saved game has been read.
     * @param aData an array of the register values
     */
    protected void setAudioRegisterData(int[] aData)
    {
        setAudioRegister(AUDC0, aData[0]); 
        setAudioRegister(AUDC1, aData[1]); 
        setAudioRegister(AUDF0, aData[2]); 
        setAudioRegister(AUDF1, aData[3]);  
        setAudioRegister(AUDV0, aData[4]); 
        setAudioRegister(AUDV1, aData[5]); 
      
    }
    
    
    /**
     * Changes the audio mode to either mono (channels=1), or stereo (channels=2).
     * @param aChannels Number of channels (1 or 2)
     */
    protected void setChannelNumber(int aChannels)
    {
        assert((aChannels==1)||(aChannels==2));
        if (myChannels!=aChannels) //is a change
        {
            myChannels=aChannels;
            initialize();
            
        }//end : changing value
        
    }
    
    /**
     * Returns the number of channels the audio system is using.
     * @return returns a 1 for mono, 2 for stereo
     */
    protected int getChannelNumber()
    {
        return myChannels;
    }
    
    /**
     * Closes the sound, freeing up system sound resources.
     */
    protected void close() {
        try{
            if (mySDLine!=null) mySDLine.close();
            
        }//end : try
        catch (Exception e) {
            //TODO : better exception handling
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * Initializes the sound.  After calling this, one should make sure that
     * close() is called when this class is finished.
     */
    protected synchronized void initialize() {
        try{
            boolean zPreviouslyEnabled=mySoundEnabled;
            mySoundEnabled=false; //just in case...
            
            
            //Step 0 - get rid of old sound system, if one exists
            if (mySDLine!=null)
            {
               mySDLine.stop();
               mySDLine.close(); //get rid of old one
               mySDLine=null;
              
            }//end : old one exists
            
             clearPokeQueue();
            
            //Step 1 - establish what format we're going to use
            myAudioFormat = new AudioFormat((float)JAVA_SOUND_SAMPLE_RATE, BITS_PER_SAMPLE, myChannels, SIGNED_VALUE, BIG_ENDIAN);
            //System.out.println("AudioFormat - sampleRate=" + myAudioFormat.getSampleRate() + ", framerate=" + myAudioFormat.getFrameRate());
            
            //Step 2 - acquire a Source Data Line
            DataLine.Info zDLI=new DataLine.Info(SourceDataLine.class, myAudioFormat);
            //System.out.println("Acquiring source data line info - " + zDLI);
            mySDLine=(SourceDataLine)AudioSystem.getLine(zDLI);
            
            //Step 3 - open and start that Source Data Line object
            mySDLine.open(myAudioFormat);
            //mySDLine.start();
            myInitSuccess=true;
            
            mySoundEnabled=zPreviouslyEnabled;
            myBufferCushion=DEFAULT_BUFFER_CUSHION * myChannels;
            updateCycleSampleFactor();
            
        }//end : try
        catch (LineUnavailableException e) {
            
            //TODO : some type of notification that audio wasn't available
            myInitSuccess=false;
            
        }//end : catch - Line Unavailable
        
    }
    
    /**
     * Checks to see if the required Java Sound objects were created successfully.
     * @return true if sound objects created successfully
     */
    protected boolean isSuccessfullyInitialized() {
        return myInitSuccess;
    }
    
    protected void reset() {
        
        myPreviousCycle=0;
        
        clearPokeQueue();
        myAUDC[0] = myAUDC[1] = myAUDF[0] = myAUDF[1] = myAUDV[0] = myAUDV[1] = 0;
        myFutureAUDC[0] = myFutureAUDC[1] = myFutureAUDF[0] = myFutureAUDF[1] = myFutureAUDV[0] = myFutureAUDV[1] = 0;
        myP4[0] = myP5[0] = myP4[1] = myP5[1] = 1;
        myFrequencyDivider[0].set(0);
        myFrequencyDivider[1].set(0);
        myOutputCounter = 0;
    }
    
    
    // ==================== AUDIO REGISTER METHODS ======================
    
    
    /**
     * This is the method that actually changes the sound register
     * variables.  It is called by processPokeQueue(...) shortly before
     * process(...) is called.
     * @param address Address of a sound register
     * @param value The value to assign to the given register
     */
    private void setAudioRegister(int address, int value) {
        switch(address) {
            case 0x15:   myAUDC[0] = value & 0x0f;    break;
            case 0x16:   myAUDC[1] = value & 0x0f;    break;
            
            case 0x17:
                myAUDF[0] = value & 0x1f;
                myFrequencyDivider[0].set(myAUDF[0]);
                break;
                
            case 0x18:
                myAUDF[1] = value & 0x1f;
                myFrequencyDivider[1].set(myAUDF[1]);
                break;
                
            case 0x19:    myAUDV[0] = value & 0x0f;   break;
            case 0x1a:    myAUDV[1] = value & 0x0f;   break;
            default:   break;
        }//end : switch
    }
    
    
    private int getAudioRegister(char address) {
        switch(address) {
            case 0x15:  return myAUDC[0];
            case 0x16:  return myAUDC[1];
            case 0x17:  return myAUDF[0];
            case 0x18:  return myAUDF[1];
            case 0x19:  return myAUDV[0];
            case 0x1a:  return myAUDV[1];
            default: return 0;
        }//end : switch
    }
    
    
    
    private void setFutureAudioRegister(char address, int value) {
        switch(address) {
            case 0x15:    myFutureAUDC[0] = value & 0x0f;    break;
            case 0x16:    myFutureAUDC[1] = value & 0x0f;    break;
            case 0x17:    myFutureAUDF[0] = value & 0x1f;    break;
            case 0x18:    myFutureAUDF[1] = value & 0x1f;    break;
            case 0x19:    myFutureAUDV[0] = value & 0x0f;    break;
            case 0x1a:    myFutureAUDV[1] = value & 0x0f;    break;
            default:    break;
        }//end : switch
    }
    
    
    private int getFutureAudioRegister(char address) {
        switch(address) {
            case 0x15:   return myFutureAUDC[0];
            case 0x16:   return myFutureAUDC[1];
            case 0x17:   return myFutureAUDF[0];
            case 0x18:   return myFutureAUDF[1];
            case 0x19:   return myFutureAUDV[0];
            case 0x1a:   return myFutureAUDV[1];
            default:   return 0;
        } //end : switch
    }
    
    
    
    
    // ================= MAIN SYNTHESIS METHOD =====================
    private boolean bool(int aValue) {
        return (aValue!=0);
    }
    
    
    
    /**
     * This method creates sound samples based on the current settings of the
     * TIA sound registers.
     * @param buffer the array into which the newly calculated byte values are to be placed
     * @param aStartIndex the array index at which the method should start placing the samples
     * @param samples the number of samples to create
     */
    private void synthesizeAudioData(byte[] aPreOutputBuffer, int aStartIndex, int aAudioFrames) {
       // int zSamplesToProcess=aSamplesPerChannel;
        int zSamples=aAudioFrames * myChannels;
        int zVolChannelZero = ((myAUDV[0] << 2) * myVolumePercentage) / 100;
        int zVolChannelOne = ((myAUDV[1] << 2) * myVolumePercentage) / 100;
        int zIndex=aStartIndex;
        // Loop until the sample buffer is full
        while(zSamples > 0) {
            // Process both TIA sound channels
            for(int c = 0; c < 2; ++c) {
                // Update P4 & P5 registers for channel if freq divider outputs a pulse
                if((myFrequencyDivider[c].clock())) {
                    switch(myAUDC[c]) {
                        case 0x00:    // Set to 1
                        {  // Shift a 1 into the 4-bit register each clock
                            myP4[c] = (myP4[c] << 1) | 0x01;
                            break;
                        }
                        
                        case 0x01:    // 4 bit poly
                        {
                            // Clock P4 as a standard 4-bit LSFR taps at bits 3 & 2
                            myP4[c] = bool(myP4[c] & 0x0f) ?
                                ((myP4[c] << 1) | ((bool(myP4[c] & 0x08) ? 1 : 0) ^
                                    (bool(myP4[c] & 0x04) ? 1 : 0))) : 1;
                            break;
                        }
                        
                        case 0x02:    // div 31 . 4 bit poly
                        {
                            // Clock P5 as a standard 5-bit LSFR taps at bits 4 & 2
                            myP5[c] = bool(myP5[c] & 0x1f) ?
                                ((myP5[c] << 1) | ((bool(myP5[c] & 0x10) ? 1 : 0) ^
                                    (bool(myP5[c] & 0x04) ? 1 : 0))) : 1;
                            
                            // This does the divide-by 31 with length 13:18
                            if((myP5[c] & 0x0f) == 0x08) {
                                // Clock P4 as a standard 4-bit LSFR taps at bits 3 & 2
                                myP4[c] = bool(myP4[c] & 0x0f) ?
                                    ((myP4[c] << 1) | ((bool(myP4[c] & 0x08) ? 1 : 0) ^
                                        (bool(myP4[c] & 0x04) ? 1 : 0))) : 1;
                            }
                            break;
                        }
                        
                        case 0x03:    // 5 bit poly . 4 bit poly
                        {
                            // Clock P5 as a standard 5-bit LSFR taps at bits 4 & 2
                            myP5[c] = bool(myP5[c] & 0x1f) ?
                                ((myP5[c] << 1) | ((bool(myP5[c] & 0x10) ? 1 : 0) ^
                                    (bool(myP5[c] & 0x04) ? 1 : 0))) : 1;
                            
                            // P5 clocks the 4 bit poly
                            if (bool(myP5[c] & 0x10)) {
                                // Clock P4 as a standard 4-bit LSFR taps at bits 3 & 2
                                myP4[c] = bool(myP4[c] & 0x0f) ?
                                    ((myP4[c] << 1) | ((bool(myP4[c] & 0x08) ? 1 : 0) ^
                                        (bool(myP4[c] & 0x04) ? 1 : 0))) : 1;
                            }
                            break;
                        }
                        
                        case 0x04:    // div 2
                        {
                            // Clock P4 toggling the lower bit (divide by 2)
                            myP4[c] =(myP4[c] << 1) | (bool(myP4[c] & 0x01) ? 0 : 1);
                            break;
                        }
                        
                        case 0x05:    // div 2
                        {
                            // Clock P4 toggling the lower bit (divide by 2)
                            myP4[c] = (myP4[c] << 1) | (bool(myP4[c] & 0x01) ? 0 : 1);
                            break;
                        }
                        
                        case 0x06:    // div 31 . div 2
                        {
                            // Clock P5 as a standard 5-bit LSFR taps at bits 4 & 2
                            myP5[c] = bool(myP5[c] & 0x1f) ?
                                ((myP5[c] << 1) | ((bool(myP5[c] & 0x10) ? 1 : 0) ^
                                    (bool(myP5[c] & 0x04) ? 1 : 0))) : 1;
                            
                            // This does the divide-by 31 with length 13:18
                            if((myP5[c] & 0x0f) == 0x08) {
                                // Clock P4 toggling the lower bit (divide by 2)
                                myP4[c] = (myP4[c] << 1) | (bool(myP4[c] & 0x01) ? 0 : 1);
                            }
                            break;
                        }
                        
                        case 0x07:    // 5 bit poly . div 2
                        {
                            // Clock P5 as a standard 5-bit LSFR taps at bits 4 & 2
                            myP5[c] = bool(myP5[c] & 0x1f) ?
                                ((myP5[c] << 1) | ((bool(myP5[c] & 0x10) ? 1 : 0) ^
                                    (bool(myP5[c] & 0x04) ? 1 : 0))) : 1;
                            
                            // P5 clocks the 4 bit register
                            if(bool(myP5[c] & 0x10)) {
                                // Clock P4 toggling the lower bit (divide by 2)
                                myP4[c] = (myP4[c] << 1) | (bool(myP4[c] & 0x01) ? 0 : 1);
                            }
                            break;
                        }
                        
                        case 0x08:    // 9 bit poly
                        {
                            // Clock P5 & P4 as a standard 9-bit LSFR taps at 8 & 4
                            myP5[c] = (bool(myP5[c] & 0x1f) || bool(myP4[c] & 0x0f)) ?
                                ((myP5[c] << 1) | ((bool(myP4[c] & 0x08) ? 1 : 0) ^
                                    (bool(myP5[c] & 0x10) ? 1 : 0))) : 1;
                            myP4[c] =(myP4[c] << 1) | (bool(myP5[c] & 0x20) ? 1 : 0);
                            break;
                        }
                        
                        case 0x09:    // 5 bit poly
                        {
                            // Clock P5 as a standard 5-bit LSFR taps at bits 4 & 2
                            myP5[c] = bool(myP5[c] & 0x1f) ?
                                ((myP5[c] << 1) | ((bool(myP5[c] & 0x10) ? 1 : 0) ^
                                    (bool(myP5[c] & 0x04) ? 1 : 0))) : 1;
                            
                            // Clock value out of P5 into P4 with no modification
                            myP4[c] = (myP4[c] << 1) | (bool(myP5[c] & 0x20) ? 1 : 0);
                            break;
                        }
                        
                        case 0x0a:    // div 31
                        {
                            // Clock P5 as a standard 5-bit LSFR taps at bits 4 & 2
                            myP5[c] = bool(myP5[c] & 0x1f) ?
                                ((myP5[c] << 1) | ((bool(myP5[c] & 0x10) ? 1 : 0) ^
                                    (bool(myP5[c] & 0x04) ? 1 : 0))) : 1;
                            
                            // This does the divide-by 31 with length 13:18
                            if((myP5[c] & 0x0f) == 0x08) {
                                // Feed bit 4 of P5 into P4 (this will toggle back and forth)
                                myP4[c] = (myP4[c] << 1) | (bool(myP5[c] & 0x10) ? 1 : 0);
                            }
                            break;
                        }
                        
                        case 0x0b:    // Set last 4 bits to 1
                        {
                            // A 1 is shifted into the 4-bit register each clock
                            myP4[c] = (myP4[c] << 1) | 0x01;
                            break;
                        }
                        
                        case 0x0c:    // div 6
                        {
                            // Use 4-bit register to generate sequence 000111000111
                            myP4[c] = (~myP4[c] << 1) |
                                    ((!(!bool(myP4[c] & 4) && (bool(myP4[c] & 7)))) ? 0 : 1);
                            break;
                        }
                        
                        case 0x0d:    // div 6
                        {
                            // Use 4-bit register to generate sequence 000111000111
                            myP4[c] = (~myP4[c] << 1) |
                                    ((!(!bool(myP4[c] & 4) && (bool(myP4[c] & 7)))) ? 0 : 1);
                            break;
                        }
                        
                        case 0x0e:    // div 31 . div 6
                        {
                            // Clock P5 as a standard 5-bit LSFR taps at bits 4 & 2
                            myP5[c] = bool(myP5[c] & 0x1f) ?
                                ((myP5[c] << 1) | ((bool(myP5[c] & 0x10) ? 1 : 0) ^
                                    (bool(myP5[c] & 0x04) ? 1 : 0))) : 1;
                            
                            // This does the divide-by 31 with length 13:18
                            if((myP5[c] & 0x0f) == 0x08) {
                                // Use 4-bit register to generate sequence 000111000111
                                myP4[c] = (~myP4[c] << 1) |
                                        ((!(!bool(myP4[c] & 4) && (bool(myP4[c] & 7)))) ? 0 : 1);
                            }
                            break;
                        }
                        
                        case 0x0f:    // poly 5 . div 6
                        {
                            // Clock P5 as a standard 5-bit LSFR taps at bits 4 & 2
                            myP5[c] = bool(myP5[c] & 0x1f) ?
                                ((myP5[c] << 1) | ((bool(myP5[c] & 0x10) ? 1 : 0) ^
                                    (bool(myP5[c] & 0x04) ? 1 : 0))) : 1;
                            
                            // Use poly 5 to clock 4-bit div register
                            if(bool(myP5[c] & 0x10)) {
                                // Use 4-bit register to generate sequence 000111000111
                                myP4[c] = (~myP4[c] << 1) |
                                        ((!(!bool(myP4[c] & 4) && (bool(myP4[c] & 7)))) ? 0 : 1);
                            }
                            break;
                        }//end : case 0x0f
                    }//end : switch
                }//end : frequency divider clock() method returned true
            }//end : for c(channel) loop
            
            myOutputCounter += JAVA_SOUND_SAMPLE_RATE;
            
            if(myChannels == 1) {
                // Handle mono sample generation
                while((zSamples > 0) && (myOutputCounter >= TIA_SAMPLE_RATE)) {
                    int zChannelZero=(bool(myP4[0] & 8) ? zVolChannelZero : 0); //if bit3 is on, amplitude is the volume, otherwise amp is 0
                    int zChannelOne= (bool(myP4[1] & 8) ? zVolChannelOne : 0);
                    int zBothChannels=zChannelZero + zChannelOne + myVolumeClip;
                    aPreOutputBuffer[zIndex]=(byte)(zBothChannels-128); // we are using a signed byte, which has a min. of -128
                    
                    myOutputCounter -= TIA_SAMPLE_RATE;
                    zIndex++;
                    zSamples--;
                    //assert(zIndex<=aStartIndex + zSamplesToProcess);
                    
                }//end : while loop
            } //end : is mono
            else {
                  // Handle stereo sample generation
                while((zSamples > 0) && (myOutputCounter >= TIA_SAMPLE_RATE)) {
                    int zChannelZero=(bool(myP4[0] & 8) ? zVolChannelZero : 0) + myVolumeClip; //if bit3 is on, amplitude is the volume, otherwise amp is 0
                    int zChannelOne= (bool(myP4[1] & 8) ? zVolChannelOne : 0) + myVolumeClip;
                    //int zBothChannels=zChannelZero + zChannelOne + myVolumeClip;
                    
                    aPreOutputBuffer[zIndex]=(byte)(zChannelZero-128); // we are using a signed byte, which has a min. of -128
                    zIndex++;
                    zSamples--;
                    
                    aPreOutputBuffer[zIndex]=(byte)(zChannelOne-128);
                    zIndex++;
                    zSamples--;
                    
                    myOutputCounter -= TIA_SAMPLE_RATE;
                  
                  
                   // assert(zIndex<=aStartIndex + zSamplesToProcess);
                    
                }//end : while loop
              
            }//end : is stereo
            
        }//end : while samples loop
        
        
        
    }//::
    
    
    //======================================================================
    
    /**
     * This takes register values off the register queue and submits them to the process of
     * sample creation by calling the process(...) method.
     *
     * This is called by the doFrameAudio() method, which is called once per frame.
     * <b>
     * When the ROM tells the CPU to poke the sound registers in the TIA, the TIA forwards
     * the data to this class (to the set() method).  Instead of setting the register
     * variables immediately, it saves the poke data as a AudioRegisterPoke object and puts it
     * in a queue object.  The processPokeQueue method takes these one-by-one off the front
     * of the queue and, one-by-one, sets the register variables, and calls the process(...)
     * method, creating a sound with a sample length corresponding to the number of
     * processor cycles that elapsed between it and the sound register change after
     * it.
     * @return total number of samples created
     */
    private /*synchronized*/ int processPokeQueue() {
        assert(myPokeQueue.size()>0);
        int zCurrentBufferIndex=0;
        boolean zEndOfFrame=false;
        
        while (zEndOfFrame==false) {
            AudioRegisterPoke zRW=myPokeQueue.poll();
            if ((zRW==null)) {
                zEndOfFrame=true;
            }//end : reached frame end
            
            
            if (zRW!=null) {
                assert(zRW.myDeltaCycles>=0);
                
              
                
                
                if (zRW.myAddr!=0) setAudioRegister((char)zRW.myAddr, zRW.myByteValue);
                myCyclePool+=zRW.myDeltaCycles;
               // if (myCyclePool>=CYCLE_COUNT_CUTOFF) {
                    double zAudioFramesInPool=(double)myCyclePool / myAdjustedCyclesPerAudioFrame;
                    int zWholeAudioFramesInPool=(int)(zAudioFramesInPool);
                    myCyclePool=myCyclePool  - ((double)zWholeAudioFramesInPool*myAdjustedCyclesPerAudioFrame);
                    //myCyclePool= myCyclePool % myWholeCyclesPerAudioFrame; //the new cycle count is the remainder from the division
                    
                    //   dbgout("Processing --frame=" + zRW.myDebugFrameCounter +", #=" + zRW.myDebugCounter + ", " + zSamplesInBunch + " samples/pool, zDeltaCycles==" + zRW.myDeltaCycles);
                    synthesizeAudioData(myPreOutputBuffer, zCurrentBufferIndex, zWholeAudioFramesInPool);
                    zCurrentBufferIndex += (zWholeAudioFramesInPool*myChannels);  //each samples is represented by a single byte in myAudioBuffer
               // }//end : met the threshold
                    
               /*   if (DEBUG_AUDIO_UNPOKE==true)
            {
                if (DEBUG_AUDIO_UNPOKE_LIST==null) DEBUG_AUDIO_UNPOKE_LIST=new java.util.Vector<int[]>();
                int zDbgIsEOF=(zRW.myFrameEnd ? 1 : 0);
                int[] zData={zDbgIsEOF, zRW.myAddr, zRW.myByteValue, zRW.myDeltaCycles, (int)(myCyclePool * 1000), (int)(myAdjustedCyclesPerAudioFrame * 1000), zWholeAudioFramesInPool};
                if (DEBUG_AUDIO_UNPOKE_LIST.size() > 10000) DEBUG_AUDIO_UNPOKE_LIST.remove(0);
                DEBUG_AUDIO_UNPOKE_LIST.add(zData);
            }     
                */
                    
                
            }//end : zRW not null
              AudioRegisterPoke zNextARP=myPokeQueue.peek();
              if ((zNextARP==null)||(zNextARP.myFrameEnd==true)) zEndOfFrame=true;
        } //end : while end of frame is false
        
        return zCurrentBufferIndex;
    }
    
    /**
     * A very imperfect method that plays the sound information that has accumulated over
     * the past video frame.
     * @param aCycles the current CPU system cycle count
     * @param aFPS the console's framerate, in frames per second
     */
    protected synchronized void doFrameAudio(int aCycles, int aFPS) {
        if (isSoundEnabled()==true) {
            //STEP 1 : add an 'end of frame' signal to the poke queue
            addPokeToQueue(true, aCycles, 0, 0); //indicates the end of a frame
            
            //STEP 2 : turn a frame's worth of poke objects into audio data (in the myPreOutputBuffer)
            int zSamples=processPokeQueue();
            
            
            int zBufferSize=mySDLine.getBufferSize();
            int zAvailable=mySDLine.available();
            int zInBuffer=zBufferSize - zAvailable;
            
            
            //STEP 3 : send audio data to the audio system (i.e. play the sound)
            
            
            
            //CURRENT SYSTEM OF BUFFER MANAGEMENT :
            //This assumes the actual emulator is running fastor than the nominal CPU speed, thus gradually filling up
            //the SDL sound buffer (not to be confused with myPreOutputBuffer).  It just limits the number of samples that
            //actually make it into the play buffer.  It doesn't affect the number of samples processed, as this might
            //affect the frequency (pitch) of music.
            
            double zPercentFull= 100.0 * ((double)zInBuffer / (double)zBufferSize); //used in debugging
            
            int zToPlay=Math.min(myBufferCushion - zInBuffer, zSamples);
            zToPlay=Math.max(0,zToPlay); //Make sure it's zero or greater
            if (myChannels==2) zToPlay=roundToEven(zToPlay); //make sure it's even if it's stereo
            
            if (mySDLine.isRunning()==false) mySDLine.start();
         
            mySDLine.write(myPreOutputBuffer, 0,  zToPlay); //Math.min(zSamples,zAvail));  //This sends the samples to the play buffer - out of the programmer's hands after this point
            
         /*   if (DEBUG_AUDIO_PLAY==true)
            {
                if (DEBUG_AUDIO_PLAY_LIST==null) DEBUG_AUDIO_PLAY_LIST=new java.util.Vector<int[]>();
                int[] zData={zSamples, zInBuffer, (zSamples - zToPlay), (int)(myRealDisplayFrameRate * 1000), (int)(myCycleSampleFactor * 1000)};
                if (DEBUG_AUDIO_PLAY_LIST.size() > 10000) DEBUG_AUDIO_PLAY_LIST.remove(0);
                DEBUG_AUDIO_PLAY_LIST.add(zData);
            }
          */
            
            // dbgout(" - writing to SDLine - samples=" + zSamples + ", in buffer=" + zInBuffer + ", samples cut=" + (zToPlay - zSamples) + ",percent full=" + (int)zPercentFull);
            
            
            
           
        }//end : sound is enabled
    }
    
    private static int roundToEven(int aNumber)
    {
        //return (aNumber  / 2) * 2; 
        return (aNumber % 2 != 0) ? (aNumber - 1) : aNumber; 
    }
    
    private void clearPokeQueue() {
        myPokeQueue.clear();
        addPokeToQueue(true, 0, 0, 0);   //Add a 1-frame lag
    }
    
    
    
    
    
    private void addPokeToQueue(boolean aFrameEnd, int aCycleNumber, int aAddress, int aByteValue) {
        //STEP 1 : Determine how many cycles have elapsed since last poke and assign that to previous poke (in queue)
        int zDeltaCycles=aCycleNumber - myPreviousCycle;
        if (myPreviousPoke!=null) myPreviousPoke.myDeltaCycles=zDeltaCycles; //setting delta cycles on previous one
        
        //STEP 2 : Determine if this poke is actually changing any values
        int zValueToOverwrite=getFutureAudioRegister((char)aAddress);
        
        //STEP 3 : If poke is a new value or this is the end of a frame, add a poke object to queue
           //I'm not sure how necessary this whole only-add-if-different-value thing is...I just thought it might be good        if ((zValueToOverwrite!=aByteValue)||(aFrameEnd==true)) 
        {     
        AudioRegisterPoke zRW=new AudioRegisterPoke(aFrameEnd, aAddress, aByteValue);
            myPokeQueue.offer(zRW);
            setFutureAudioRegister((char)aAddress, aByteValue);
            myPreviousPoke=zRW;
            myPreviousCycle=aCycleNumber;
            
            //  dbgout("addPokeToQueue- (different value) end of frame==" + aFrameEnd + ", delta cycles for prev=" + zDeltaCycles);
        }//end : new value for future reg
        
    }
    
    
    /**
     * This method is called by TIA when it receives a poke command destined for a
     * sound register.  This is the method that converts the data received into a
     * AudioRegisterPoke object and stores that object on a queue for later processing.
     * Check out the processPokeQueue(...) description for details.
     * @param addr address to poke
     * @param aByteValue byte value of the poke
     * @param cycle the CPU system cycle number
     * @see processPokeQueue(int, int, int)
     */
    protected /*synchronized*/ void pokeAudioRegister(int addr, int aByteValue, int cycle) {
        
        if (isSoundEnabled()==true) {
            addPokeToQueue(false, cycle, addr, aByteValue);
        }//end : sound enabled
    }
    
    
    
    
    
    
    
    
    
    
    
//=============================================================================
//========================== INNER CLASSES ====================================
//=============================================================================
    
    
    
    private class FrequencyDivider {
        //private final static long serialVersionUID = 8982487492473260236L;
        private int myDivideByValue=0;
        private int myCounter=0;
        
        public FrequencyDivider()  {  myDivideByValue = myCounter = 0; }
        public void set(int divideBy)  {  myDivideByValue = divideBy;   }
        public boolean clock() {
            myCounter++;
            if(myCounter > myDivideByValue) {
                myCounter = 0;
                return true;
            }
            return false;
        }
        
    }//END INNER CLASS
    
    
    
    
    
    
    
    //=======================================================
    
    private class AudioRegisterPoke {
       // private final static long serialVersionUID = -4187197137229151004L;
        private int myAddr;
        private int myByteValue;
        private int myDeltaCycles=0;
        
        private boolean myFrameEnd=false;
        
        
        
        public AudioRegisterPoke(boolean aFrameEnd, int aAddr, int aByteValue) {
            
            myAddr=aAddr;
            myByteValue=aByteValue;
            
            myFrameEnd=aFrameEnd;
        }
    }//END : CLASS
    
    //=========================================================================================
    
    
    
    
    
    
    
    //NOTE : when actual frame rate is below the theoretical audio frame rate, you get a buffer underrun -> popping noises
    // when actual frame rate is significantly above the theoretical fps, you get too much cut out by the compensation,
    //which causes a sort of singing-on-a-bumpy-road effect...
    
    
    // =======================DEBUG SECTION=====================
    
    public void debugSetReg(char address, int value) {
        setAudioRegister(address, value);
    }
    
    
    private void dbgout(String aOut) {
        System.out.println("DEBUG: " + aOut);
    }
    
    /**
     * Occasionally good for debugging, but ultimately, this method must go.
     * @param aTimerDelay
     * @deprecated
     */
    public void debugPlayChunk(int aTimerDelay) {
        
        int zSamples=(int)(((float)JAVA_SOUND_SAMPLE_RATE/1000f) * aTimerDelay);
        
        if (zSamples>myPreOutputBuffer.length) zSamples=myPreOutputBuffer.length;
        synthesizeAudioData(myPreOutputBuffer, 0, zSamples);
        int zAvail=mySDLine.available();
        
        mySDLine.write(myPreOutputBuffer, 0, Math.min(zSamples,zAvail));
        
    }
    
    
    public SourceDataLine debugGetSourceDataLine() {return mySDLine;}
    
    
    
    
    
    
    
}
