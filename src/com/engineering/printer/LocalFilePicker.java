package com.engineering.printer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class LocalFilePicker extends Activity {
	private TextView tvPath;
	private ListView fileList;
	private Button selectButton;
	private boolean sdCardStatue;
	private CommandConnection c = null;
	File currentPath;
	File root;
	File currentFile;
	File[] currentFileList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filepicker);
		String[] fs = null;
		try {
			c = new CommandConnection(EngineeringPrinter.connect);
			String returnString = c.execWithReturn("ls");
			Log.d("testaa", returnString);
			fs = returnString.split("\\n");
			Log.d("testdd",""+fs.length);
			for(int i=0;i<fs.length;i++){
				Log.d("testaa", fs[i]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		sdCardStatue = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);

		fileList = (ListView) this.findViewById(R.id.fileList);
		tvPath = (TextView) this.findViewById(R.id.tvPath);
		selectButton = (Button) findViewById(R.id.selectBtn);
		selectButton.setEnabled(false);

		root = new File("/mnt/sdcard/");
		if (sdCardStatue) {
			currentPath = root;
			currentFileList = root.listFiles();
			inflateListView(currentFileList);

		}
		fileList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				if (currentFileList[position].isFile()) {
					currentFile = currentFileList[position];
					selectButton.setEnabled(true);
					return;
				}
				File[] tem = currentFileList[position].listFiles();
				if (tem == null || tem.length == 0) {
					Toast.makeText(LocalFilePicker.this, "Not Available",
							Toast.LENGTH_SHORT).show();
				} else {
					currentPath = currentFileList[position];
					currentFileList = tem;
					inflateListView(currentFileList);
				}
			}
		});
	}

	private void inflateListView(File[] files) {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < files.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			String filename = files[i].getName();
			listItem.put("filename", filename);

			//if (".txt".equalsIgnoreCase(filename.substring(filename
				//	.lastIndexOf(".")))
				//	|| ".doc".equalsIgnoreCase(filename.substring(filename
				//			.lastIndexOf(".")))) {
				File myFile = files[i];

				long modTime = myFile.lastModified();
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				System.out.println(dateFormat.format(new Date(modTime)));

				listItem.put(
						"modify",
						"Date Modified: "
								+ dateFormat.format(new Date(modTime)));
				listItems.add(listItem);
			//}
		}

		SimpleAdapter adapter = new SimpleAdapter(LocalFilePicker.this, listItems,
				R.layout.itemlist, new String[] { "filename", "modify" },
				new int[] { R.id.file_name, R.id.file_modify });

		fileList.setAdapter(adapter);

		try {
			tvPath.setText(currentPath.getCanonicalPath());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	

	public void onRootBtnClick(View v) {
		currentPath = root;
		currentFileList = root.listFiles();
		inflateListView(currentFileList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.filepicker, menu);
		return true;
	}
	public void onc2eBtnClick(View v){
		Intent myIntent = new Intent(v.getContext(), EniacFilePicker.class);
        startActivity(myIntent);
	}
	public void onSelectBtnClick(View v) {
		if(currentFile.isDirectory()){
			new AlertDialog.Builder(v.getContext()).setMessage("This is a directory!").create().show();
		}
		else if (currentFile != null) {
			InputStream is = null;
	        
	        System.out.println("Try to get data");

			    System.out.println("Getting data");
			    Document.loadFile(currentFile);
			    Document.setDeFile(currentFile.getPath());
			    //Document.setDescriptor(getIntent().getData());
			     // EngineeringPrinter.Microsoft = MicrosoftSink.Filter(getIntent().getType());
			     // EngineeringPrinter.type = getIntent().getType();
			Intent myIntent = new Intent(v.getContext(), PrinterSelectScreen.class);
            startActivityForResult(myIntent, 0);
		}
	}

	public void onQuitBtnClick(View v) {
		finish();
	}
}
