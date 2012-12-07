

package com.engineering.printer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.net.ssl.ManagerFactoryParameters;

import com.trilead.ssh2.Connection;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Engineering Printer Log in activity.
 * 
 * @author SEASPrint
 *
 */
public class EngineeringPrinter extends Activity {
	
	public static final String PREFS_NAME = "PrintToEngineeringPrefs";
	public static final String PASSWORD_FAIL = "";
	public static final String USER_FAIL = "";
	public static final String PASSWORD_KEY = "pw";
	public static final String USER_KEY = "user";
	public static final String HOST_FAIL = "minus.seas.upenn.edu";
	public static final String HOST_KEY = "host";
	public static final String PORT_FAIL = "22";
	public static final String PORT_KEY = "port";
	public static final String KEY_KEY = "privatekey";
	public static final String SAVED = "saved";
	public static String user;
	public static String password;
	public static String host;
	public static String privatekey;
	public static int port;
	public static Connection connect = null;
	public static FileUpload.UploadProgress upload;
	public static ErrorCallback eb;
	public static boolean Microsoft;
	public static String type;
	
	private static final char KEY = 1337;
	
	/**
	 * Encrypts the password string.
	 * 
	 * @param pw
	 * @return
	 */
	public String encryptPassword(String pw) {
		char letters[] = pw.toCharArray();
		for(int i = 0; i < letters.length; i++) letters[i] = (char)(KEY ^ pw.charAt(letters.length - i - 1));
		return new String(letters);
	}
	
	/**
	 * Decrypts the password string.
	 * @param pw
	 * @return
	 */
	public String decryptPassword(String pw) {
		char letters[] = pw.toCharArray();
		for(int i = 0; i < letters.length; i++) letters[i] = (char)(KEY ^ pw.charAt(letters.length - i - 1));
		return new String(letters);
	}
	
    /** 
     * Creates the activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Initializes the data.
        try {
        	initializeDocument();
        }
        catch  (FileNotFoundException fnf){
            Log.e("Connection","File Not Found");
        }
        eb = new ErrorCallback(this);
        
    }
    
    /**
     * Starts running the activity.
     */
    @Override
    public void onStart() {
        super.onStart();
        setContentView(R.layout.main);
        connect=null;        
        final SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (pref.contains(PASSWORD_KEY)) 
        {
        	pref.edit().remove(PASSWORD_KEY).commit();
        }
        user = pref.getString(USER_KEY, USER_FAIL);
        host = "minus.seas.upenn.edu";
        String portStr = pref.getString(PORT_KEY, PORT_FAIL);
        
        //USERNAME TEXT BOX
        final EditText usertext = (EditText) findViewById(R.id.usertext);        
        //PASSWORD TEXT BOX
        final EditText passtext = (EditText) findViewById(R.id.passtext);        
        //HOST NAME TEXT BOX
        final EditText hostname = (EditText) findViewById(R.id.hostname);        
        //Port TEXT BOX
        final EditText portname = (EditText) findViewById(R.id.portname);
        
        usertext.setText(user);
        passtext.setText("");
        hostname.setText(host);
        portname.setText(portStr);
        
        
        final CheckBox checkBox = (CheckBox) findViewById(R.id.save);
        checkBox.setChecked(pref.getBoolean(SAVED, false));
        

        
    	final Button printbutton = (Button) findViewById(R.id.button);
    	printbutton.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	if(connect == null) {
            		// Perform action on key press
            		try {
	                	user=usertext.getText().toString();
	                	password =passtext.getText().toString();
	                	host = hostname.getText().toString();
	                	if(portname.getText().toString().length()!=0)
	                	{
	                		String temp=portname.getText().toString();
	                		port = Integer.parseInt(temp);
	                	}
	                	else
	                		port = 22;
	                			             
	                	ConnectionFactory cf = new ConnectionFactory();
	                	Log.e("user",user);
	                	Log.e("host",host);
	                	Log.e("port",((Integer)(port)).toString());
	                	String key = (new AuthSetup(user, password, host, port)).keyGen();
	                	connect = cf.MakeConnectionKey(user, key, host, port);
                        final SharedPreferences pref1 = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
	                	final Editor edit = pref1.edit();
	                	if(checkBox.isChecked())
	                	{
		                	edit.putString(USER_KEY, user);
		                	edit.putString(KEY_KEY, key);
		                	edit.putString(HOST_KEY, host);
		                	edit.putString(PORT_KEY, port+"");
	                	}
	                	edit.putBoolean(SAVED,checkBox.isChecked());
	                	edit.commit();
	                	
            		} catch(IOException e) {
            			new AlertDialog.Builder(v.getContext()).setMessage("Could not connect to server! Verify login information and network status.").create().show();
            			return;
            		}
            		//Intent myIntent = new Intent(v.getContext(), PrinterSelectScreen.class);
            		Intent myIntent = new Intent(v.getContext(), ControlPanelScreen.class);
                    startActivityForResult(myIntent, 0);
            	};
            }
    	});
    }
    
    private void initializeDocument() throws FileNotFoundException
    {
    	//Initializes the data.
    	InputStream is = null;
    	System.out.println("Try to get data initial");
    	if (null != getIntent().getData()) {
            System.out.println("Getting data initial");
    		is = getContentResolver().openInputStream(getIntent().getData());
	        Document.load(is);
	        Document.setDescriptor(getIntent().getData());
	        EngineeringPrinter.Microsoft = MicrosoftSink.Filter(getIntent().getType());
	        EngineeringPrinter.type = getIntent().getType();
    	}   
    }
}

