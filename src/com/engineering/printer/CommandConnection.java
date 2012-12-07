package com.engineering.printer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;

/**
 * Builds a connection to the remote machine and execute command on it.
 * 
 * @author CIS573 Final Project
 *
 */
public class CommandConnection {
   
	/**
	 * The connections object.
	 */
    private Connection mConn;
    /**
     * The ssh session object.
     */
    private Session myss;
    /**
     * Initializes the command connection with a connection object.
     * @param c
     * @throws IOException
     */
    public CommandConnection(Connection c) throws IOException{
       mConn = c;
    }
    
    /**
     * Executes the command and returns the execution result.
     * @param cmd 
     * @return the execution result.
     * @throws IOException
     */
    public String execWithReturn(String cmd) throws IOException {
        Session sess = mConn.openSession();
        sess.execCommand(cmd);       
        String ostr = getReturnString(sess.getStdout());        
        String error = getReturnString(sess.getStderr());
        /**
         * If an error occurs, terminates the session.
         */
        if (error.length() > 0) {
            Log.e("Connection", error);
        }
        sess.close();
        return ostr;
    }
    
    /**
     * Reads the remote information.
     * @param rs
     * @return
     * @throws IOException
     */
    private String getReturnString(InputStream rs) throws IOException{
        BufferedReader rd = new BufferedReader(new InputStreamReader(rs));
        String str = null;
        StringBuilder sb = new StringBuilder();
        while ((str = rd.readLine()) != null) {
            sb.append(str);
            sb.append("\n");
        }
        return sb.toString().trim();
    }
    
    
    /**
     * Executes the command with pseudo terminal.
     * @param cmd
     * @return
     * @throws IOException
     */
    public String execWithReturnPty(String cmd) throws IOException {
		myss = mConn.openSession();
		myss.requestPTY("xterm");
        
        myss.execCommand(cmd);
        String ostr = getReturnString(myss.getStdout());      
        String error =getReturnString(myss.getStderr());     
        if (error.length() > 0) {
            Log.e("Connection", error);
        }
        //sess.close();
        return ostr;
    }
    
    /**
     * Close session.
     */
    public void closeSession(){
    		System.out.println("About to close session");
    		myss.close();
    		myss=null;
    }
    
    /**
     * Executes without return pseudo terminal.
     * @param cmd
     * @throws IOException
     */
    public void execWithoutReturnPty(String cmd) throws IOException {
		myss = mConn.openSession();
		myss.requestPTY("xterm");     
        myss.execCommand(cmd);

    }

    
}
