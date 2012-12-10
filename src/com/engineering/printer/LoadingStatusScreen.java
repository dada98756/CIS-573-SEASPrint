package com.engineering.printer;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import com.engineering.printer.PrinterSelectScreen.MyOnItemSelectedListener;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class LoadingStatusScreen extends Activity {
	public static int MAX_PAGES = 5;
	public static int DELAY = 30;
	public static int MINS_IN_HOUR = 60;
	public static int HOURS_IN_DAY = 24;
	public static String NEXT_TIME_PREF = "NextPrintTime";

	public String user=EngineeringPrinter.user;
	public String password = EngineeringPrinter.password;
	public String printer;
	public boolean duplex;
	public Integer number;
	public boolean timedPrinting;
	public Integer start;
	public Integer end;
	public boolean isPdf;
	public int dup_multiplier;


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
	}

	@Override
	public void onResume() {
		super.onResume();
		Intent mt = getIntent();
		eniac = mt.getBooleanExtra("eniac", false);
		filename = mt.getStringExtra("filePath");
		isPdf = mt.getBooleanExtra("isPdf", true);
		Log.d("LoadingStatus",eniac+" "+filename);

		printer = MyOnItemSelectedListener.printer;
		duplex = PrinterSelectScreen.duplex;
		if (duplex){
			dup_multiplier = 2;
		}else{
			dup_multiplier = 1;
		}
		number = PrinterSelectScreen.number;
		timedPrinting = PrinterSelectScreen.timedPrinting;
		start = PrinterSelectScreen.start;
		end = PrinterSelectScreen.end;

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
			String local_filename = filename;
			String local_printer = printer;
			boolean local_duplex = duplex;
			int local_number = number;
			int local_start = start;
			int local_end = end;

			TimedPrintingUtil tp = TimedPrintingUtil.getInstance(EngineeringPrinter.connect,EngineeringPrinter.eb);
			String pdfFilename ;
			if (isPdf){
				pdfFilename = local_filename;
			}else{
				pdfFilename = tp.convertToPdf(local_filename);
			}
			int total_count = tp.getPdfPageCount(pdfFilename);
			local_end = total_count < local_end? total_count: local_end;
			local_end = 0 > local_end? 0: local_end;
			local_start = total_count < local_start? total_count: local_start;
			local_start = 0 > local_start? 0: local_start;
			if (local_start > local_end){
				int temp = local_start;
				local_start = local_end;
				local_end = temp;
			}
			if (local_start ==0 && local_end ==0){
				local_start = 1;
				local_end = total_count;
			}
			PrintCaller pc = new PrintCaller(tp.getmConn());
			if (!PrinterSelectScreen.timedPrinting){ //normal non-timed printing
				pc.printFileWithPageRange(pdfFilename, local_printer, local_number, local_duplex,local_start,local_end);	
			}else{ //timed printing
				int count = local_end - local_start + 1;
				if (count * local_number <= MAX_PAGES*dup_multiplier){
					pc.printFileWithPageRange(pdfFilename, local_printer, local_number, local_duplex,local_start,local_end);
				}else{// we need to schedule printing jobs
					TimeZone tz = TimeZone.getTimeZone("UTC");
					//Calendar c = Calendar.getInstance(tz);
					Calendar c = Calendar.getInstance();
					int curr_hour = c.get(Calendar.HOUR_OF_DAY);
					int curr_min = c.get(Calendar.MINUTE);
					int curr_day = c.get(Calendar.DAY_OF_YEAR);
					SharedPreferences settings = getSharedPreferences(NEXT_TIME_PREF, 0);
					int day = settings.getInt("day", -1);
					int next_hour;
					int next_min ;
					if (day != curr_day){
						next_hour = curr_hour;
						next_min = curr_min;
					}else{
						next_hour = settings.getInt("hour", -1);
						next_min = settings.getInt("min", -1);
					}
					String time;
					for (int i = 0; i < local_number; i++){
						if (count > MAX_PAGES*dup_multiplier){
							int curr_start = local_start;
							int curr_end;
							while (curr_start <= local_end){
								time = next_hour+":"+next_min;
								curr_end = curr_start + MAX_PAGES*dup_multiplier - 1;
								pc.printFileWithTime(pdfFilename, local_printer, local_number, local_duplex, curr_start, curr_end, time);
								next_min += DELAY;
								if (next_min >= MINS_IN_HOUR){
									next_min -= MINS_IN_HOUR;
									next_hour = (next_hour + 1) % HOURS_IN_DAY;
								}
								curr_start = curr_end + 1;
							}
						}else{
							time = next_hour+":"+next_min;
							pc.printFileWithTime(pdfFilename, local_printer, local_number, local_duplex, local_start, local_end, time);
							next_min += DELAY;
							if (next_min >= MINS_IN_HOUR){
								next_min -= MINS_IN_HOUR;
								next_hour = (next_hour + 1) % HOURS_IN_DAY;
							}
						}
					}
					//store next_print_time for next use
                    SharedPreferences.Editor ed = settings.edit();
                    ed.putInt("day", curr_day);
                    ed.putInt("hour", next_hour);
                    ed.putInt("min", next_min);
                    ed.commit();
				}

			}

			if(!eniac)	{
				tp.getmConn().execWithReturn("rm " + local_filename);
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
