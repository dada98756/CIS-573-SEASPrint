package com.engineering.printer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import android.net.Uri;
import android.util.Log;

/**
 * Document Stub.
 * 
 * @author SEASPrint
 *
 */
public class Document {
	/**
	 * Document data.
	 */
    public static byte [] data;
    /**
     * Document descriptor.
     */
    public static String descriptor;
    /**
     * Prints History List.
     */
    public static List<String> history = new LinkedList<String>();
    
    /**
     * Reads a input stream and transforms it into a document object.
     * @param datain
     */
    public static void load(InputStream datain) {
        try {
            int count = datain.available();
            data = new byte[count];
            datain.read(data,0,count);
        }
        catch (IOException ioe) {
            Log.e("Connection",ioe.toString());
        }
    }
    
    /**
     * Loads a file and transforms it into an document object.
     * @param f
     */
    public static void loadFile(File f){
    	try {
			data = FileUtils.readFileToByteArray(f);
		} catch (IOException e) {
			Log.e("File loader","Error when try to load file from local");
			e.printStackTrace();
		}
    }
    
    /**
     * Sets the file descriptor for the document as an uri.
     * @param uri
     */
    public static void setDescriptor(Uri uri) {
        String intentData=uri.toString();
        if(intentData.substring(0, 4).equals("file"))
            descriptor=intentData.substring(intentData.lastIndexOf("/"), intentData.length());
        else
            descriptor="content";
        Log.i("Connection", descriptor);
    }
    
    /**
     * Sets the file path as the descriptor for the document.
     * @param path
     */
    public static void setDeFile(String path){
    	if(path.substring(0, 4).equals("file"))
            descriptor=path.substring(path.lastIndexOf("/"), path.length());
        else
            descriptor="content";
        Log.i("Connection", descriptor);
    }
    
    /**
     * Adds a print history to the history list.
     * @param item
     */
    public static void addToHistory(String item)
    {
    	history.add(item);
    }
    
    /**
     * Gets the print history list.
     * @return
     */
    public static List<String> getHistory()
    {
    	return history;
    }
    
}
