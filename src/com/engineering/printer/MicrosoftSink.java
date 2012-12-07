package com.engineering.printer;

/**
 * 
 * A support class for Microsoft document.
 * 
 * @author SEASPrint
 *
 */
public class MicrosoftSink  {
	
	/**
	 * Tests if a type is a Microsoft document.
	 * 
	 * @param type
	 * @return
	 */
    public static boolean Filter(String type) {
        if (type.equals("application/vnd.ms-powerpoint") || 
                type.equals("application/vnd.ms-excel")||
                type.equals("application/msword")) {
            return true;
        }
        return false;
    }
    
}
