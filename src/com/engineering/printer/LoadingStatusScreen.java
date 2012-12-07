package com.engineering.printer;

import java.io.IOException;
import java.util.Calendar;

import com.engineering.printer.PrinterSelectScreen.MyOnItemSelectedListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class LoadingStatusScreen extends Activity {
	public String user=EngineeringPrinter.user;
	public String password = EngineeringPrinter.password;
	public String printer = MyOnItemSelectedListener.printer;
	public boolean duplex = PrinterSelectScreen.duplex;
	public Integer number = PrinterSelectScreen.number;
	public boolean timedPrinting = PrinterSelectScreen.timedPrinting;


	public FileUpload.UploadProgress upload = EngineeringPrinter.upload;

	private ProgressBar mProgress;
	private TextView mUpdate;
	private TextView mConstantLoading;
	private int mProgressStatus = 0;
	private Handler mHandler = new Handler();
	private boolean eniac = false;
	private String filename = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadingstatus);
		Intent mt = getIntent();
		eniac = mt.getBooleanExtra("eniac", false);
		filename = mt.getStringExtra("filePath");
		Log.d("LoadingStatus",eniac+" "+filename);

	}

	@Override
	public void onResume() {
		super.onResume();
		mProgress = (ProgressBar) findViewById(R.id.progress_bar);
		mProgress.setProgress(0);

		mUpdate = (TextView) findViewById(R.id.byte_progress);
		mUpdate.setText("Initializing upload.");

		mConstantLoading = (TextView) findViewById(R.id.loading_constant);
		mConstantLoading.setText("Initializing upload.");
		//add this file name to the history list.
		Document.addToHistory(Document.descriptor.startsWith
				("/")?Document.descriptor.substring(1):Document.descriptor+"   "+Calendar.getInstance().getTime());
		if(!eniac){
			try {
				FileUpload fu = new FileUpload(EngineeringPrinter.connect);
				upload  = fu.startUpload(Document.data,  EngineeringPrinter.eb);
			}
			catch (IOException ioe){
				//ALERT DIALOG
				Log.e("Connection", "Failed to connect or send");
			}
		}
		else{
			try{
				
					//not uploading file here, will do later.
				
			}catch(Exception ex){
				ex.printStackTrace();
				//finish();
			}
			//finish();
		}
		final Handler handle = new Handler();
		// Start lengthy operation in a background thread
		new Thread( new Runnable() {
			@Override
			public void run() {
				if(!eniac){
					while (mProgressStatus < 100) {
						//DO STUFF
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {

						}
						// Update the progress bar
						handle.post(new Runnable()  {
							@Override
							public void run() {
								int val = upload.getPercentComplete();
								mProgressStatus = val;
								mProgress.setProgress(val);
								Log.i("Connection", "Percentage " + Integer.toString(val));
								mUpdate.setText(PrepareStatus(upload.getBytesWritten(), upload.getTotalBytes()));
							}
						});
					}

					final String filename = upload.GetResult();
					handle.post(new Runnable() {
						@Override
						public void run() {
							UploadComplete(filename);
						}
					});
				}
				else{
					while (mProgressStatus < 100) {
						//DO STUFF
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {

						}
						// Update the progress bar
						handle.post(new Runnable()  {
							@Override
							public void run() {
								int val = 100;
								mProgressStatus = val;
								mProgress.setProgress(val);
								Log.i("Connection", "Percentage " + Integer.toString(val));
								mUpdate.setText("100/100");
							}
						});
					}
					handle.post(new Runnable() {
						@Override
						public void run() {
							UploadComplete(filename);
						}
					});
				}
			}

		}).start();
		//Log.i("Connection", "Temporary filename is " + filename);  
	}
	/*
	 * @param String filename the file that just has been uploaded to the server, and this name is the *local* name on server, not the original name
	 */
	private void UploadComplete(final String filename) {
		mProgress.setVisibility(View.GONE);
		mUpdate.setVisibility(View.GONE);
		mConstantLoading.setVisibility(View.GONE);

		//new Thread( new Runnable() {
			//@Override
			//public void run() {
				try {
					if (!PrinterSelectScreen.timedPrinting){ //normal non-timed printing
						String local_filename = filename;
						String local_printer = printer;
						boolean local_duplex = duplex;
						int local_number = number;
						CommandConnection cc = new CommandConnection(EngineeringPrinter.connect);
						new PrintCaller(cc).printFile(local_filename, local_printer, local_number, local_duplex);	
						if(!eniac)	
							cc.execWithReturn("rm " + local_filename);
					}else{ //timed printing
						System.out.println("About to print timely");
						TimedPrintingUtil tp = TimedPrintingUtil.getInstance(EngineeringPrinter.connect,EngineeringPrinter.eb);
						System.out.println(eniac+" and file: "+filename);
						tp.addToPrintList(filename);
						if(!eniac)	
							tp.getmConn().execWithReturn("rm " + filename);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//}
		//}
				//).start();
		Toast.makeText(LoadingStatusScreen.this, "Success! File uploaded and sent to printer!", Toast.LENGTH_LONG).show();
		System.out.println("Going to go back");
		this.finish();
	}

    /*
     * @param String filenmae the file just processed
     */
	private String ContentProcessing(String filename) {
		return filename;
	}



	private String PrepareStatus(
			int bytesWritten, int totalBytes) {
		int orders_of_magnitude = 0;
		if (totalBytes >= 0 && totalBytes < 1024) {
			orders_of_magnitude = 1;
		}
		else if (totalBytes >= 1024 && totalBytes <1024*1024) {
			orders_of_magnitude = 1024;
		}
		else {
			orders_of_magnitude = 1024*1024;
		}
		float writ = (float)bytesWritten/(float)orders_of_magnitude;
		float total = (float)totalBytes/(float)orders_of_magnitude;

		String suffix = null;
		if (orders_of_magnitude ==1) {
			suffix = "B";
		}
		else if (orders_of_magnitude==1024) {
			suffix = "KiB";
		}
		else {
			suffix = "MB";
		}
		return String.format("%.2f %s out of %.2f %s uploaded.", writ, suffix, total, suffix);
	}

}
