/*
 * JSException.java
 *
 * Created on August 2, 2007, 2:16 AM
 *
 *
 */

package jstella.core;

/**
 *
 *
 */
public class JSException extends java.lang.Exception {
    
    
    
    public enum ExceptionType { UNSPECIFIED, IO, AUDIO, CART_NOT_SUPPORTED, INSTRUCTION_NOT_RECOGNIZED};
    
    private String myMessage="";
    private ExceptionType myExceptionType=ExceptionType.UNSPECIFIED;
    
    /**
     * Creates a new instance of <code>JSException</code> without detail message.
     */
    public JSException(ExceptionType aType, String aMsg) {
        super(aMsg);
        myMessage=aMsg;
        myExceptionType=aType;
        
    }
    
    public ExceptionType getExceptionType() {
        return myExceptionType;
    }
    
    public String getJStellaMessage() {
        return myMessage;
    }
    
    public String toString()
    {
        return getJStellaMessage() + " (Exception Type: " + myExceptionType + ")";
    }
    
    /**
     * Constructs an instance of <code>JSException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public JSException(String msg) {
        super(msg);
    }
}
