package com.engineering.printer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;

public class CommandConnection {
    
    private Connection mConn;
    private Session myss;
    public CommandConnection(Connection c) throws IOException{
       mConn = c;
    }
    
    public String execWithReturn(String cmd) throws IOException {
        Session sess = mConn.openSession();
        sess.execCommand(cmd);
        InputStream rs = sess.getStdout();
        BufferedReader rd = new BufferedReader(new InputStreamReader(rs));
        String str = null;
        StringBuilder sb = new StringBuilder();
        while ((str = rd.readLine()) != null) {
            sb.append(str);
            sb.append("\n");
        }
        
        InputStream es = sess.getStderr();
        BufferedReader er = new BufferedReader(new InputStreamReader(es));
        StringBuilder eb = new StringBuilder();
        String estr =null;
        while ((estr = rd.readLine()) != null) {
            eb.append(estr);
            eb.append("\n");
        }
        String error = eb.toString().trim();
        if (error.length() > 0) {
            Log.e("Connection", error);
        }
        sess.close();
        return sb.toString().trim();
    }
    
    public String execWithReturnPty(String cmd) throws IOException {
       // if(myss==null){
    			myss = mConn.openSession();
    			myss.requestPTY("xterm");
       // }
        
        myss.execCommand(cmd);
        InputStream rs = myss.getStdout();
        BufferedReader rd = new BufferedReader(new InputStreamReader(rs));
        String str = null;
        StringBuilder sb = new StringBuilder();
        while ((str = rd.readLine()) != null) {
            sb.append(str);
            sb.append("\n");
        }
        
        InputStream es = myss.getStderr();
        BufferedReader er = new BufferedReader(new InputStreamReader(es));
        StringBuilder eb = new StringBuilder();
        String estr =null;
        while ((estr = rd.readLine()) != null) {
            eb.append(estr);
            eb.append("\n");
        }
        String error = eb.toString().trim();
        if (error.length() > 0) {
            Log.e("Connection", error);
        }
        //sess.close();
        return sb.toString().trim();
    }
    public void closeSession(){
    		System.out.println("About to close session");
    		myss.close();
    		myss=null;
    }
    public void execWithoutReturnPty(String cmd) throws IOException {
    		//if(myss==null){
			myss = mConn.openSession();
			myss.requestPTY("xterm");
    		//}
       
        myss.execCommand(cmd);

    }

    
}
