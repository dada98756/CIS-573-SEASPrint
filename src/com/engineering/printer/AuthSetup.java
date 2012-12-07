package com.engineering.printer;

import java.io.IOException;

import android.util.Log;

import com.trilead.ssh2.Connection;

/**
 * Sets up authentication.
 * 
 * @author SEASPrint
 *
 */
public class AuthSetup {
	
	/**
	 * User Name
	 */
	String mUsername;
	/**
	 * password
	 */
	String mPassword;
	/**
	 * Host name for remote machine.
	 */
	String mHost;
	/**
	 * Port number for remote machine
	 */
	int mPort;
	
	/**
	 * Constructor
	 * @param username
	 * @param pw
	 * @param host
	 * @param port
	 */
	public AuthSetup(String username, String pw, String host, int port) {
		mUsername = username;
		mPassword = pw;
		mHost = host;
		mPort = port;
	}

	/**
	 * Generates a session key.
	 * @return
	 * @throws IOException io
	 */
	public String keyGen() throws IOException{
		//try {
			Connection conn =(new ConnectionFactory()).MakeConnection(mUsername, mPassword,mHost, mPort);
			CommandConnection cc = new CommandConnection(conn);
			Log.d("Boot", "test ssh-keygen");
			Log.d("Boot", cc.execWithReturn("rm ~/.ssh/seasprint_rsa"));
			Log.d("Boot", cc.execWithReturn("rm ~/.ssh/seasprint_rsa.pub"));
			Log.d("Boot", cc.execWithReturn("ssh-keygen -t rsa -N '' -f ~/.ssh/seasprint_rsa"));
			cc.execWithReturn("cat ~/.ssh/seasprint_rsa.pub >> ~/.ssh/authorized_keys");
			String str = cc.execWithReturn("cat ~/.ssh/seasprint_rsa");
			return str;
		//}
		//catch (IOException e) {
		//	throw new IOException(e.fillInStackTrace());
		//}
	}
	
}
