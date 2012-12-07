package com.engineering.printer;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;

/**
 * 
 * File Uploader.
 * 
 * @author SEASPrint
 *
 */
public class FileUpload implements Runnable {
    
    private SCPClient mSCP;
    private UploadProgress mF;
    private CommandConnection mConn;
    private byte [] mData;
    
    public FileUpload(Connection conn) throws IOException {
        mSCP = new SCPClient(conn);
        mConn = new CommandConnection(conn);
    }
    
    /**
     * 
     * Upload progress information wrapper.
     * 
     * @author SEASPrint
     *
     */
    public class UploadProgress {
        
        public UploadProgress(int totalbytes) {
            mFinish = new Semaphore(0);
            mTotalBytes = totalbytes;
            mBytesWritten = new AtomicInteger();
            mPassed = false;
        }
        
        public boolean NearComplete() {
            return mTotalBytes == mBytesWritten.get();
        }
        
        private int mTotalBytes;
        private AtomicInteger mBytesWritten;
        private Semaphore mFinish;
        private String mTarget;
        private boolean mPassed;
        
        /**
         * Advances the upload progress.
         * 
         * @param additional_bytes_written
         */
        public void IncrementProgress(int additional_bytes_written) {
            int val = mBytesWritten.addAndGet(additional_bytes_written);
            Log.i("Connection", val + " bytes written!");
        }
        
        /**
         * Gets the bytes that have been written.
         * 
         * @return
         */
        public int getBytesWritten() {
            return mBytesWritten.get();
        }
        
        /**
         * Gets the total number of bytes.
         * 
         * @return
         */
        public int getTotalBytes() {
            return mTotalBytes;
        }
        
        /**
         * Gets the progress percentage.
         * 
         * @return
         */
        public int getPercentComplete() {
            return( (100)*mBytesWritten.get()) / mTotalBytes;
        }
        
        public void SignalCompletion(String filename) {
            mTarget = filename;
            mFinish.release();
        }
        
        public String GetResult()  {
            if (!mPassed) {
                try {
                    mFinish.acquire();
                }
                catch (InterruptedException ie) {
                   GetResult();
                }
                mPassed = true;
            }
            return mTarget;            
        }
        
    }
    
    /**
     * Uploads the file.
     */
    @Override
	public void run() {
        try {
            String home = mConn.execWithReturn("echo ~");
            //creates a temporary file at the remote machine.
            String tmpfile = mConn.execWithReturn("echo `mktemp " + home  + "/tmp.XXXXXXXX`");
            String [] toks = tmpfile.split("/");
            String rebuild = "";
            for (int i = 0 ; i < toks.length - 1;i++) {
                rebuild = rebuild + "/" + toks[i];
            }
            String remoteTargetDirectory = rebuild;
            String remoteFileName = toks[toks.length - 1];   
            Log.i("Connection", tmpfile);
            mSCP.put(mF,mData, remoteFileName, remoteTargetDirectory);
            mF.SignalCompletion(tmpfile);
        }
        catch (IOException ioe) {
            mCb.error();
        }
        
    }
    
    private ErrorCallback mCb;
    
    /**
     * Creates a thread to upload the file.
     * @param data
     * @param cb
     * @return
     * @throws IOException
     */
    public UploadProgress startUpload(byte [] data, ErrorCallback cb) throws IOException {
        
        mCb = cb;
        mData = data;
        mF = new UploadProgress(data.length);
        Thread thr = new Thread(this);
        thr.start();
        return mF;
        

    }

}
