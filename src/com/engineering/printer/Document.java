package com.engineering.printer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import android.net.Uri;
import android.util.Log;

public class Document {
    public static byte [] data;
    public static String descriptor;
    
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
    
    public static void loadFile(File f){
    	try {
			data = FileUtils.readFileToByteArray(f);
		} catch (IOException e) {
			Log.e("File loader","Error when try to load file from local");
			e.printStackTrace();
		}
    }
    
    public static void setDescriptor(Uri uri) {
        String intentData=uri.toString();
        String fileName;
        if(intentData.substring(0, 4).equals("file"))
            descriptor=intentData.substring(intentData.lastIndexOf("/"), intentData.length());
        else
            descriptor="content";
        Log.i("Connection", descriptor);
    }
}
