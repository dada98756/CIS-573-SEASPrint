package com.engineering.printer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import io.filepicker.FilePicker;
import io.filepicker.FilePickerAPI;
/**
 *
 * @author Siyong Liang
 */
public class ControlPanelScreen extends Activity{
	private Button sdButton;
	private Button cloudButton;
	private Button eniacButton;
	private Button printHistoryButton;
	private String filePickerAPIKey = "ArYkjf8hmSpCG8cycKjmsz" ; 
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controlpanel);  
        
        sdButton = (Button) findViewById(R.id.sd_browser_button);
        sdButton.setEnabled(true);
        
        cloudButton = (Button)findViewById(R.id.cloud_browser_button);
        cloudButton.setEnabled(true);
        
        eniacButton = (Button)findViewById(R.id.eniac_browser_button);
        eniacButton.setEnabled(true);
        
        printHistoryButton = (Button)findViewById(R.id.cloud_browser_button);
        eniacButton.setEnabled(true);
    }
    
    public void onSDBrowserBtnClick(View v) {
    	Intent myIntent = new Intent(v.getContext(), SDFilePicker.class);
        startActivityForResult(myIntent, 0);
	}
    
    public void onCloudBtnClick(View v)
    {
        FilePickerAPI.setKey(filePickerAPIKey);
        Intent myIntent = new Intent(v.getContext(), FilePicker.class);
        startActivityForResult(myIntent, FilePickerAPI.REQUEST_CODE_GETFILE);
    }
    public void onHistoryBtnClick(View v)
    {
        FilePickerAPI.setKey(filePickerAPIKey);
        Intent myIntent = new Intent(v.getContext(), PrintHistoryScreen.class);
        startActivityForResult(myIntent, 0);
    }
    public void onEniacBtnClick(View v)
    {
        Intent myIntent = new Intent(v.getContext(), EniacFilePicker.class);
        startActivityForResult(myIntent, 0);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "Quit");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 0:
			finish();
			break;
		}
		return true;
	}
    
    @Override
	public void onResume() {
        super.onResume();
    }
     
    @Override
    protected void onActivityResult(int requestCode,
                                int resultCode, Intent data) {
        if (requestCode == FilePickerAPI.REQUEST_CODE_GETFILE) {
            if (resultCode != RESULT_OK)
            //Result was cancelled by the user or there was an error
                    return;
            Uri uri = data.getData();
            InputStream is = null;
            try{
            	is = getContentResolver().openInputStream(uri);
        	}catch (FileNotFoundException ex){
//        		Logger.getLogger(ControlPanelScreen.class.getName()).log(Level.SEVERE,null,ex);
        	}
            Document.load(is);
            Document.setDescriptor(uri);
            data.setClass(this, PrinterSelectScreen.class);
            data.putExtra("filePath", uri.toString());
            startActivityForResult(data,0);
    	};
            
    }
}
