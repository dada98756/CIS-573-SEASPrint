package com.engineering.printer;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;

/**
 * Error callback handlers.
 * 
 * @author SEASPrint
 *
 */
public class ErrorCallback {

	/**
	 * The activity that raises the error.
	 */
    private Activity mAct;
    /**
     * Handler For 
     */
    private Handler mHand;

    public  ErrorCallback(Activity act) {
        mAct = act;
        mHand = new Handler();
    }
    
    /**
     * Presents the error information with a popup.
     */
    public void error() {
        Runnable r = new Runnable() {
            @Override
			public void run() {
                AlertDialog.Builder altb = new AlertDialog.Builder(mAct);
                altb.setMessage("Connection interrupted with server.  Try connecting again or verifying your connectivity to the network.");
                altb.create().show();
            }
        };
        mHand.post( r);
    }
    
}
