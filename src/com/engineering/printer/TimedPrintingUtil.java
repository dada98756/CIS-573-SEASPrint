package com.engineering.printer;

import java.io.IOException;

import com.trilead.ssh2.Connection;

public class TimedPrintingUtil {
	private static Connection connection;
	private static TimedPrintingUtil instance;
	private static CommandConnection mConn;
	private static ErrorCallback mCb;
	private static final String TO_PRINT = "to_print";
	private static final String SETUP_SH = "curl -L https://raw.github.com/emish/cets_autoprint/master/setup.sh | sh";

	public synchronized static TimedPrintingUtil getInstance(Connection conn,ErrorCallback cb) throws IOException{
		if (instance == null || conn != connection){
			instance = new TimedPrintingUtil(conn);
		}
		instance.mCb = cb;
		return instance;
	}

	private TimedPrintingUtil(Connection conn) throws IOException{
		mConn = new CommandConnection(conn);
		setup();
	}

	private void setup() throws IOException{
		String returnV = mConn.execWithReturnPty("echo | screen -ls");
		System.out.println(returnV);
		String firstL = returnV.split(" ")[0];
		if(firstL.equals("No")){
			mConn.execWithoutReturnPty("cd ~",false);
			
			mConn.execWithoutReturnPty("screen -i;"+SETUP_SH, false);
			System.out.println("Screen Done!");

			mConn.execWithoutReturnPty("screen -r", false);
			System.out.println("Screen Done again!");
			mConn.execWithoutReturnPty("python ~/autoprint/autoprint.py;sreen -d", false);

			System.out.println("Python done!");
		}
	}
	
	public synchronized boolean addToPrintList(String filename){
		try {
			String target = "~/" + TO_PRINT;
			System.out.println("Going to cp"+ filename + " " + target);
			mConn.execWithoutReturnPty("cp " + filename + " " + target,false);
		} catch (IOException e) {
			mCb.error();
		}
		return true;
	}

	public CommandConnection getmConn() {
		return mConn;
	}

}
