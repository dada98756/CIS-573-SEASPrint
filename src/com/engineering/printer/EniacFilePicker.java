package com.engineering.printer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class EniacFilePicker extends Activity {
	private TextView etvPath;
	private ListView fileList;
	private Button selectButton;
	private boolean sdCardStatue;
	//used to tell eniac from sdCard access
	private boolean eniac;
	private CommandConnection c = null;
	
	private String ecurrentPath;
	private String eroot;
	private String ecurrentFile;
	private String[] ecurrentFilList;
	private String[] dirs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eniacpick);
		String[] ecurrenStrings = null;
		dirs = null;
		String[] fs = null;
		try {
			c = new CommandConnection(EngineeringPrinter.connect);
			String returnString = c.execWithReturn("ls -l | grep ^d");
			//Log.d("testaa", returnString);
			ecurrenStrings = returnString.split("\\n");
			ecurrentPath = c.execWithReturn("pwd");
			eroot = ecurrentPath;
			dirs = new String[ecurrenStrings.length];
			//Log.d("testdd",""+ecurrenStrings.length);
			String[] temp = null;
			for(int i=0;i<ecurrenStrings.length;i++){
				temp = ecurrenStrings[i].split(" ");
				dirs[i] = temp[temp.length-1];
				Log.d("testaa", dirs[i]+"    "+ecurrenStrings[i]);
			}
			returnString = c.execWithReturn("ls -l");
			fs = returnString.split("\\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		fileList = (ListView) this.findViewById(R.id.efileList);
		etvPath = (TextView) this.findViewById(R.id.etvPath);
		selectButton = (Button) findViewById(R.id.eselectBtn);
		selectButton.setEnabled(false);
		
		einflateListView(fs);
		fileList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				if(!isDirectory(ecurrentFilList[position])){
					ecurrentFile = ecurrentPath+"/"+ecurrentFilList[position];
					selectButton.setEnabled(true);
					return;
				}
				selectButton.setEnabled(false);
				try {
					//ecurrentPath = c.execWithReturn("pwd");
					ecurrentPath = ecurrentPath+"/"+ecurrentFilList[position];
					//c.execWithReturn("cd "+ecurrentFilList[position]);
					Log.d("testFind","ls -l "+ecurrentPath+"| grep ^d");
					String returnString = c.execWithReturn("ls -l "+ecurrentPath+"| grep ^d");
					String[] ecurrenStrings = returnString.split("\\n");
					dirs = new String[ecurrenStrings.length];
					Log.d("testdd",""+ecurrenStrings.length);
					String[] temp = null;
					for(int i=0;i<ecurrenStrings.length;i++){
						temp = ecurrenStrings[i].split(" ");
						dirs[i] = temp[temp.length-1];
						Log.d("testaa", dirs[i]+"    "+ecurrenStrings[i]);
					}
				    returnString = c.execWithReturn("ls -l "+ecurrentPath);
					String[] fs = returnString.split("\\n");
					einflateListView(fs);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	private void einflateListView(String[] files) {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		String[] temp  =null;
		ecurrentFilList = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			temp = files[i].split(" ");
			String filename = temp[temp.length-1];
			listItem.put("filename", filename);
			ecurrentFilList[i] = filename;
			listItem.put("lastModi", "");
			listItems.add(listItem);
		}

		SimpleAdapter eadapter = new SimpleAdapter(EniacFilePicker.this, listItems,
				R.layout.eitemlist, new String[] { "filename", "lastModi" },
				new int[] { R.id.efile_name, R.id.efile_modify});
		//fileList.setAdapter(adapter);
		
		fileList.setAdapter(eadapter);
		try {
			etvPath.setText(ecurrentPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void one2cBtnClick(View v){
		selectButton.setEnabled(false);
		Intent myIntent = new Intent(v.getContext(), SDFilePicker.class);
        startActivity(myIntent);
	}
	public void onRootBtnClick(View v) {
		selectButton.setEnabled(false);
		String returnString = null;
//		currentPath = root;
//		currentFileList = root.listFiles();
//		inflateListView(currentFileList);
		try{
			//c.execWithReturn("cd ~/");
			returnString = c.execWithReturn("ls -l");
			ecurrentFilList = returnString.split("\\n");
			Log.d("testFind","ls -l "+eroot+"| grep ^d");
			String returnString1 = c.execWithReturn("ls -l "+eroot+"| grep ^d");
			String[] ecurrenStrings = returnString1.split("\\n");
			dirs = new String[ecurrenStrings.length];
			Log.d("testdd",""+ecurrenStrings.length);
			String[] temp = null;
			for(int i=0;i<ecurrenStrings.length;i++){
				temp = ecurrenStrings[i].split(" ");
				dirs[i] = temp[temp.length-1];
				Log.d("testaa", dirs[i]+"    "+ecurrenStrings[i]);
			}
			ecurrentPath = eroot;
			einflateListView(ecurrentFilList);
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.filepicker, menu);
		return true;
	}
	public void onc2eBtnClick(View v) {
		//String cPath = c.execWithReturn("pwd");
		finish();
	}
	

	public void onQuitBtnClick(View v) {
		finish();
	}
	
	public void onSelectBtnClick(View v) {
		if(ecurrentFile!=null){
			Intent myIntent = new Intent(v.getContext(), PrinterSelectScreen.class);
			myIntent.putExtra("filePath", ecurrentFile);
			myIntent.putExtra("eniac", true);
			startActivity(myIntent);
		}
	}
	
	private boolean isDirectory(String path){
		boolean toR = false;
		for(int i=0;i<dirs.length;i++){
			if(dirs[i].equals(path)) {
				toR =true;
				break;
			}
		}
		return toR;
	}
}
