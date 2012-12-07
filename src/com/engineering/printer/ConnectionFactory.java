package com.engineering.printer;

import java.io.IOException;
import android.util.Log;

import com.trilead.ssh2.Connection;

/**
 * Connection Factory.
 * 
 * @author SEASPrint
 *
 */
public class ConnectionFactory {
	
    public ConnectionFactory() {
        
    }
    
    /**
     * Creates a connection given the user name and password.
     * @param username
     * @param password
     * @return
     * @throws IOException
     */
    public Connection MakeConnection(String username, String password) throws IOException {
        Connection conn = new Connection("eniac.seas.upenn.edu");
        conn.connect();
        conn.authenticateWithPassword(username, password);
        CommandConnection cc = new CommandConnection(conn);
        return conn; 
    }
    
    /**
     * Creates a connection given user name, password, host and port.
     * @param username
     * @param password
     * @param host
     * @param port
     * @return
     * @throws IOException
     */
    public Connection MakeConnection(String username, String password, String host, int port) throws IOException {
        Connection conn = new Connection(host,port);
        conn.connect();
        if (!conn.authenticateWithPassword(username, password)) {
            throw new IOException();
        }
        return conn;
    }
    
    /**
     * Creates a connection given the key instead of password.
     * @param username
     * @param key
     * @param host
     * @param port
     * @return
     * @throws IOException
     */
    public Connection MakeConnectionKey(String username, String key, String host, int port) throws IOException {
    	Log.d("Boot", "With Key!");
        Connection conn = new Connection(host,port);
        conn.connect();
        Log.d("Boot", ""+conn.authenticateWithPublicKey(username, key.toCharArray(),null));//conn.authenticateWithPassword(username, password);
        Log.d("Boot", "Yay!");
        return conn;
    }
    
}
